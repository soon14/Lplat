/**
 * 
 */
package com.zengshi.butterfly.core.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class DefaultTree<T extends ITreeable> extends AbstractTree<T> implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7557427221316758484L;
	private transient static final Logger logger=LoggerFactory.getLogger(DefaultTree.class);
	//private List<T> source;
	
	private Map<String, DefaultTree<T>> rndAccessTree;
	
	public DefaultTree() {
		super();
		// TODO Auto-generated constructor stub
	}
	public DefaultTree(String nodeId) {
		super();
		this.setNodeId(nodeId);
	}

	public DefaultTree(T node) {
		this.initInstance(node);
	}

	
	public void initInstance(T node) {
		this.setNodeId(node.getNodeId());
		this.setObject(node);
		this.parentNodeId=node.getParentNodeId();

	}
	
	public DefaultTree(String rootId,List<T> nodeList) {
		this.setNodeId(rootId);
		constructRndData(rootId,nodeList);
	}
	
	private void constructRndData(String rootId,List<T> nodeList) {
		//结构化rndAccessTree
		if(rndAccessTree == null) rndAccessTree =new HashMap<String, DefaultTree<T>>();
		for(T obj:nodeList) {
			if(rootId.equals(obj.getNodeId())) {
				this.setNodeId(obj.getNodeId());
				this.setObject(obj);
				continue;
			}
			rndAccessTree.put(obj.getNodeId(), new DefaultTree<T>(obj));
		}
		rndAccessTree.put(rootId, this);
		
		for(ITreeable obj:nodeList) {
			DefaultTree tree=rndAccessTree.get(obj.getNodeId());
			
			//设置root
			tree.setRoot(rndAccessTree.get(rootId) == null?  new DefaultTree<T>(rootId): rndAccessTree.get(rootId));
			if(rootId.equals(obj.getParentNodeId())) {
				tree.parent=tree.root;
			}else {
				//设置parent
				tree.parent=rndAccessTree.get(obj.getParentNodeId());
			}
			if(tree.parent == null) {
				logger.warn("waring :there is no parent for node "+ tree);
				//System.out.println("waring :there is no parent for node "+ tree);
			}else {
				tree.parent.addChild(tree);
			}
			
		}
	}
	
	/**
	 * 
	 * @param s
	 * @param nodeId
	 * @return
	 */
	public List<DefaultTree<T>> getNodes(List<DefaultTree<T>> s,
			String nodeId) {
		DefaultTree<T> tree = rndAccessTree.get(nodeId);
		this.getSubNodes(s, nodeId);
		s.add(tree);
		return s;
	}
	
	public List<DefaultTree<T>> getSubNodes(List<DefaultTree<T>> s,
			String nodeId) {
		DefaultTree<T> tree = rndAccessTree.get(nodeId);
		List<ITree<T>> x = tree.getChildren();
		if (x != null && x.size() > 0) {
			for(ITree<T> t:x) {
				s.add((DefaultTree)t);
			}
			//s.addAll(x);
			for (ITree<T> xx : x) {
				getSubNodes(s, xx.getNodeId());
			}
		} else
			return s;
		return s;
	}
	
	public DefaultTree<T> getNode(String nodeId) {
		if(this.root != null) {
			return ((DefaultTree<T>)this.root).getNode(nodeId);
		}else {
			return this.rndAccessTree.get(nodeId);
		}
		
	}
	public List<DefaultTree<T>> getSubNodes(String nodeId) {
		List<DefaultTree<T>> res = new ArrayList<DefaultTree<T>>();
		List<ITree<T>> cs = this.getChildren();
		if (!cs.isEmpty()) {	
			for (ITree<T> c:cs) {
				res.addAll(getSubNodes(c.getNodeId()));
			}
		}
		return res;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "id:"+this.getNodeId()+" pid:"+this.parentNodeId;
	}
	
	
	
}
