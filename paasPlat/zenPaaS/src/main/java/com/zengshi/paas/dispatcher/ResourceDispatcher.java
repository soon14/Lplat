package com.zengshi.paas.dispatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.zengshi.paas.ConfigurationCenter;
import com.zengshi.paas.ConfigurationWatcher;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 分布式资源调度器
 *
 */
public class ResourceDispatcher implements ConfigurationWatcher {
	
	public static final Logger log = Logger.getLogger(ResourceDispatcher.class);

	public static final String ROOT_PATH = "/dispatcher";
	public static final String EXECUTOR_PATH = "/executor";
	public static final String EXECUTOR_PATH1 = "/executor/";
	public static final String LEADER_PATH = "/leader";
	public static final String EXECUTOR_I_PATH = "/executor/executor_";
	public static final String OWNER_PATH = "/owners/";
	public static final String OWNER_PATH1 = "/owners";
	public static final String ZK_SERVER_KEY = "zkServer";
	public static final String RESOURCES_KEY = "resources";

	private int resourceAmount = 0;
	private JSONArray resourceArray = null;
	private String zkServer = null;

	private ZooKeeper zk = null;
	private Thread selectLeaderThread = null;
	private String nodeName = null;
	private String node = null;
	private final Object lock = new Object();
	private final Object lock1 = new Object();
	private final Object lock2 = new Object();
	private boolean isLeader = false;
	private JSONArray occupiedResource = null;

	private ConfigurationCenter cc = null;
	private String confPath = "/com/zpaas/dispatcher/resourceDispatcher";
	private ResourceExecutor executor = null;
	private String resourceId = null;
	private String domainPath = null;

	private Watcher wh = new Watcher() {
		public void process(WatchedEvent event) {
			if (log.isDebugEnabled()) {
				log.debug("receive watch event:" + event.toString());
			}
			if ((domainPath + LEADER_PATH).equals(event.getPath())) {
				if (EventType.NodeDeleted.equals(event.getType())) {
					if (log.isDebugEnabled()) {
						log.debug("Leader is down, notify to select leader.");
					}
					synchronized (lock) {
						lock.notifyAll();
					}
				}
			} else if (EventType.NodeChildrenChanged.equals(event.getType())
					&& (domainPath + EXECUTOR_PATH).equals(event.getPath())) {
				dispatchResource();
				
			} else if (EventType.NodeDataChanged.equals(event.getType()) && nodeName.equals(event.getPath())) {
				occupyResource();
				
			}
			
		}
	};

	public ResourceDispatcher() {

	}

	public void init() {
		if (log.isDebugEnabled()) {
			log.debug("init");
		}
		domainPath = ROOT_PATH + "/" + resourceId;
		try {
			process(cc.getConfAndWatch(confPath, this));
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
	}

	public void process(String conf) {
		if (log.isInfoEnabled()) {
			log.info("new dispatcher configuration is received: " + conf);
		}
		JSONObject json = JSONObject.fromObject(conf);
		boolean changed = false;
		if (json.getString(ZK_SERVER_KEY) != null && !json.getString(ZK_SERVER_KEY).equals(zkServer)) {
			changed = true;
			zkServer = json.getString(ZK_SERVER_KEY);
		}
		boolean resChanged = false;
		if (resourceArray == null) {
			if (json.getJSONArray(RESOURCES_KEY) != null) {
				resChanged = true;
				resourceArray = json.getJSONArray(RESOURCES_KEY);
				resourceAmount = resourceArray.size();
				executor.modifyAllResources(resourceArray);
			}
		} else {
			if (json.getJSONArray(RESOURCES_KEY) != null
					&& !(json.getJSONArray(RESOURCES_KEY).containsAll(resourceArray) && resourceArray.containsAll(json
							.getJSONArray(RESOURCES_KEY)))) {
				resChanged = true;
				resourceArray = json.getJSONArray(RESOURCES_KEY);
				resourceAmount = resourceArray.size();
				executor.modifyAllResources(resourceArray);
			}
		}

		try {
			if (changed) {
				if (zkServer != null) {
					zk = new ZooKeeper(zkServer, 6000, null);
					createPath();
					startService();
					if (selectLeaderThread != null) {
						selectLeaderThread.interrupt();
					}
					selectLeaderThread = new Thread() {
						public void run() {
							while (true && !this.isInterrupted()) {
								try {
									selectLeader();
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					};
					selectLeaderThread.start();
				}
			} else {
				if (resChanged) {
					dispatchResource();
					
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
	}

	public void createPath() throws Exception {
		if (log.isDebugEnabled()) {
			log.debug("begin to createPath");
		}
		Stat stat = null;
		try {
			stat = zk.exists(ROOT_PATH, true);
		} catch (Exception e) {
		}
		if (stat == null) {
			try {
				zk.create(ROOT_PATH, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			} catch (Exception e) {
			}
		}

		try {
			stat = zk.exists(domainPath, true);
		} catch (Exception e) {
		}
		if (stat == null) {
			try {
				zk.create(domainPath, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			} catch (Exception e) {
			}
		}

		try {
			stat = zk.exists(domainPath + EXECUTOR_PATH, true);
		} catch (Exception e) {
		}
		if (stat == null) {
			try {
				zk.create(domainPath + EXECUTOR_PATH, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			} catch (Exception e) {
			}
		}

		try {
			stat = zk.exists(domainPath + OWNER_PATH1, true);
		} catch (Exception e) {
		}
		if (stat == null) {
			try {
				zk.create(domainPath + OWNER_PATH1, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			} catch (Exception e) {
			}
		}

	}

	public void selectLeader() {
		if (log.isDebugEnabled()) {
			log.debug("begin to select leader...");
		}
		try {
			Stat stat = zk.exists(domainPath + LEADER_PATH, wh);
			String newLeader = null;
			if (stat == null) {
				newLeader = zk.create(domainPath + LEADER_PATH, nodeName.getBytes(), Ids.OPEN_ACL_UNSAFE,
						CreateMode.EPHEMERAL);
			}
			if (newLeader != null) {
				if (log.isDebugEnabled()) {
					log.debug("I'm the leader:" + nodeName);
				}
				isLeader = true;
				dispatchResource();
				synchronized (lock) {
					lock.wait();
				}
			} else {
				if (log.isDebugEnabled()) {
					log.debug("follow leader.");
				}
				isLeader = false;
				synchronized (lock) {
					lock.wait();
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
	}

	public void dispatchResource() {
		synchronized (lock2) {

			if (!isLeader) {
				return;
			}
			if (log.isDebugEnabled()) {
				log.debug("begin to dispatch resource");
			}
			List<String> children = null;
			try {
				children = zk.getChildren(domainPath + EXECUTOR_PATH, wh);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			if (log.isDebugEnabled()) {
				log.debug("new servers:" + children);
			}
			HashMap<String, String> owners = new HashMap<String, String>();
			for (int i = 0; i < resourceAmount; i++) {
				String resource = resourceArray.getString(i);
				byte[] ownerByte = null;
				try {
					ownerByte = zk.getData(domainPath + OWNER_PATH + resource, true, null);
				} catch (Exception e) {
				}
				if (ownerByte != null) {
					String owner = new String(ownerByte);
					owners.put(resource, owner);
				}
			}
			if (log.isDebugEnabled()) {
				log.debug("current resource owner: " + owners);
			}
			dispatcher(children, owners);
			
		}
		if (log.isDebugEnabled()) {
			log.debug("dispatch round finished:" + nodeName);
		}
	}

	public void dispatcher(List<String> servers, HashMap<String, String> owners) {
		if (servers == null || servers.size() == 0) {
			return;
		}
		int serverAmount = servers.size();
		int count = resourceAmount / serverAmount;
		int mod = resourceAmount % serverAmount;
		HashMap<String, ArrayList<String>> dispatchResult = new HashMap<String, ArrayList<String>>();
		if (owners == null || owners.size() == 0) {
			int j = 0;
			for (int i = 0; i < resourceAmount; i++) {
				String resource = resourceArray.getString(i);
				String server = servers.get(j);
				ArrayList<String> resources = dispatchResult.get(server);
				if (resources == null) {
					resources = new ArrayList<String>();
					dispatchResult.put(server, resources);
				}
				resources.add(resource);
				j++;
				if (j >= serverAmount) {
					j = 0;
				}
			}
		} else {
			HashMap<String, ArrayList<String>> old = new HashMap<String, ArrayList<String>>();
			ArrayList<String> allocated = new ArrayList<String>();
			for (String resource : owners.keySet()) {
				String server = owners.get(resource).replace(domainPath + EXECUTOR_PATH1, "");
				if (!servers.contains(server)) {
					continue;
				}
				ArrayList<String> resources = old.get(server);
				if (resources == null) {
					resources = new ArrayList<String>();
					old.put(server, resources);
				}
				allocated.add(resource);
				resources.add(resource);
			}

			if (log.isDebugEnabled()) {
				log.debug("allocated resource: " + allocated);
				log.debug("allocate info: " + old);
			}

			ArrayList<String> free = new ArrayList<String>();
			for (int i = 0; i < resourceAmount; i++) {
				String resource = resourceArray.getString(i);
				if (!allocated.contains(resource)) {
					free.add(resource);
				}
			}

			if (log.isDebugEnabled()) {
				log.debug("free resource: " + free);
			}

			int k = mod;
			for (String server : old.keySet()) {
				ArrayList<String> resources = old.get(server);
				int size = resources.size();
				if (size == count) {
					if (k > 0 && free.size() > 0) {
						k--;
						resources.add(free.remove(free.size() - 1));
					}
				} else if (size == count + 1) {
					if (k > 0) {
						k--;
					} else {
						free.add(resources.remove(size - 1));
					}
				} else if (size > count + 1) {
					if (k > 0) {
						k--;
						for (int i = size - 1; i > count; i--) {
							free.add(resources.remove(i));
						}
					} else {
						for (int i = size - 1; i >= count; i--) {
							free.add(resources.remove(i));
						}
					}
				} else {
					int freeSize = free.size();
					if (k > 0 && free.size() > 0) {
						k--;
						for (int i = freeSize - 1; i > freeSize - 1 - (count + 1 - size); i--) {
							resources.add(free.remove(i));
						}
					} else {
						for (int i = freeSize - 1; i > freeSize - 1 - (count - size); i--) {
							resources.add(free.remove(i));
						}
					}
				}
			}

			if (log.isDebugEnabled()) {
				log.debug("new allocated resource: " + old);
				log.debug("new free resource: " + free);
			}

			servers.removeAll(old.keySet());
			if (log.isDebugEnabled()) {
				log.debug("left servers need to assign resource: " + servers);
			}
			int size = servers.size();
			int freeSize = free.size();
			if (size > 0 && freeSize > 0) {
				int j = 0;
				for (int i = 0; i < freeSize; i++) {
					String server = servers.get(j);
					ArrayList<String> resources = dispatchResult.get(server);
					if (resources == null) {
						resources = new ArrayList<String>();
						dispatchResult.put(server, resources);
					}
					resources.add(free.get(i));
					j++;
					if (j >= size) {
						j = 0;
					}
				}
				if (size > freeSize) {
					for (int i = freeSize; i < size; i++) {
						dispatchResult.put(servers.get(i), new ArrayList<String>());
					}
				}
			} else {
				if (size > 0) {
					for (int i = 0; i < size; i++) {
						dispatchResult.put(servers.get(i), new ArrayList<String>());
					}
				}
			}
			dispatchResult.putAll(old);
		}
		if (log.isDebugEnabled()) {
			log.debug("final allocate info: " + dispatchResult);
		}
		
		for (String server : dispatchResult.keySet()) {
			try {
				zk.setData(domainPath + EXECUTOR_PATH + "/" + server, JSONArray.fromObject(dispatchResult.get(server))
						.toString().getBytes(), -1);
			} catch (Exception e) {
				
			}

		}

	}
	
	public void startService() throws Exception {
		nodeName = zk.create(domainPath + EXECUTOR_I_PATH, null, Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
		node = nodeName.substring(nodeName.lastIndexOf("/") + 1);
		if (log.isDebugEnabled()) {
			log.debug(nodeName + ":" + node + " start to provide service.");
		}

		zk.getData(nodeName, wh, null);
	}

	@SuppressWarnings("unchecked")
	public void occupyResource() {
		synchronized (lock1) {

			try {
				String tmp = new String(zk.getData(nodeName, wh, null));
				JSONArray occupyResource = JSONArray.fromObject(tmp);
				if (log.isDebugEnabled()) {
					log.debug("begin to occupy resource:" + occupyResource);
				}
				executor.prepareResourceAllocate(occupyResource);

				ArrayList<Object> left = new ArrayList<Object>();
				if (occupiedResource != null) {
					if (log.isDebugEnabled()) {
						log.debug("occupiedResource:" + occupiedResource);
					}
					if (occupiedResource.containsAll(occupyResource) && occupyResource.containsAll(occupiedResource)) {
						if (log.isDebugEnabled()) {
							log.debug("occupyResource has no change, break.");
						}

						return;
					}
					ArrayList<Object> occupied = new ArrayList<Object>();
					occupied.addAll(JSONArray.toCollection(occupiedResource));

					ArrayList<Object> occupying = new ArrayList<Object>();
					occupying.addAll(JSONArray.toCollection(occupyResource));

					occupied.removeAll(occupying);
					int size = occupied.size();
					if (size > 0) {
						for (int i = 0; i < size; i++) {
							try {
								String resource = (String) occupied.get(i);
								int count = 1;
								while (!executor.canReleaseResource(resource)) {
									try {
										if (log.isDebugEnabled()) {
											log.debug("waite executor to release resource:" + resource);
										}
										Thread.sleep(count * 1000);
									} catch (Exception e) {

									}
									count++;
								}
								String node = domainPath + OWNER_PATH + resource;
								if (log.isDebugEnabled()) {
									log.debug("release resource:" + node);
								}
								zk.delete(node, -1);
							} catch (Exception e) {

							}
						}
					}

					occupied = new ArrayList<Object>();
					occupied.addAll(JSONArray.toCollection(occupiedResource));
					occupying.removeAll(occupied);
					left.addAll(occupying);
				} else {
					left.addAll(JSONArray.toCollection(occupyResource));
				}
				int count = 0;
				do {
					if (left.size() == 0) {
						break;
					}
					ArrayList<Object> resources = new ArrayList<Object>();
					resources.addAll(left);
					left = new ArrayList<Object>();
					int size = resources.size();
					for (int i = 0; i < size; i++) {
						String resource = (String) resources.get(i);
						String node = null;
						String occupyNode = domainPath + OWNER_PATH + resource;
						try {
							node = zk
									.create(occupyNode, nodeName.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
							if (log.isDebugEnabled()) {
								log.debug("occupy resource:" + occupyNode);
							}
						} catch (Exception e) {
							
							String oldOwner = null;
							try {
								oldOwner = new String(zk.getData(occupyNode, wh, null));
							} catch (Exception e1) {
							}
							if (nodeName.equals(oldOwner)) {
								continue;
							}
							
							String newRes = null;
							String oldRes = null;
							try {
								newRes = new String(zk.getData(nodeName, wh, null));
								oldRes = new String(zk.getData(oldOwner, wh, null));
							} catch (Exception e1) {
							}
							if(oldRes != null) {
								JSONArray oldArray = JSONArray.fromObject(oldRes);
								if(oldArray.contains(resource)) {
									if (newRes == null) {
										log.warn("the resource had been assigned to " + oldOwner + ". " + 
												nodeName + "abandon occuping.");
										occupyResource.remove(resource);
										continue;
									}else {
										JSONArray newArray = JSONArray.fromObject(newRes);
										if(!newArray.contains(resource)) {
											log.warn("the resource had been assigned to " + oldOwner + ". " + 
													nodeName + "abandon occuping.");
											occupyResource.remove(resource);
											continue;
										}
									}
								}
							}

							if (log.isDebugEnabled()) {
								log.debug("failed to occupy resource:" + occupyNode);
								log.debug("new occupyier is:" + nodeName + " old is:" + oldOwner);
							}
						}
						if (node == null) {
							left.add(resource);
						}
					}
					count++;
					if (left.size() > 0) {
						if (log.isDebugEnabled()) {
							log.debug("waiting for occupy resources: " + count);
						}
						try {
							Thread.sleep(1000 * (count));
						} catch (Exception e) {

						}
					}
				} while (left.size() > 0);
				occupiedResource = occupyResource;
				JSONArray commitArray = new JSONArray();
				commitArray.addAll(occupiedResource);
				executor.commitResourceAllocate(commitArray);
			} catch (Exception e) {
				log.error(e.getMessage(),e);
			} 
		}
		if (log.isDebugEnabled()) {
			log.debug("occupy resource finished:" + nodeName);
		}
	}

	public ConfigurationCenter getCc() {
		return cc;
	}

	public void setCc(ConfigurationCenter cc) {
		this.cc = cc;
	}

	public String getConfPath() {
		return confPath;
	}

	public void setConfPath(String confPath) {
		this.confPath = confPath;
	}

	public ResourceExecutor getExecutor() {
		return executor;
	}

	public void setExecutor(ResourceExecutor executor) {
		this.executor = executor;
	}

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	public int getResourceAmount() {
		return resourceAmount;
	}

	public JSONArray getResourceArray() {
		return resourceArray;
	}

	public static void main(String[] args) throws Exception {
		ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[] { "dispatcherContext.xml" });
		ctx.getBean("resourceDispatcher");
		while (true) {
			Thread.sleep(1000);
		}
	}
}
