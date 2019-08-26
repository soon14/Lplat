package com.zengshi.butterfly.core.route;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang.ArrayUtils;

/**
 * 按主机权重路由
 *
 */
@Deprecated
public class Bundle<T extends Host> {

	private String name;
	private int[] routeMap;
	private List<T> hosts;
	
	private int priorityCount=0;
	
	public  Bundle(String name,List<T> hosts) {
		this.name=name;
		this.hosts=hosts;
		
		init();
	}
	
	public String getName() {
		return this.name;
	}
	
	private void init() {
		int  count=0;
		for(int i=0;i<this.hosts.size();i++) {
			Host host=this.hosts.get(i);
			this.priorityCount +=host.getPriority();
		}
		//System.out.println(" count =="+this.priorityCount);
		routeMap=new int[count];
		for(int i=0;i<this.hosts.size();i++) {
			Host host=this.hosts.get(i);
			int p=host.getPriority();
			int[] map=new int[p];
			for(int j=0;j<map.length;j++) {
				map[j]=i;
			}
			routeMap=ArrayUtils.addAll(routeMap, map);
		}
	}
	
	public void add(T host) {
		synchronized (this.hosts) {
			this.hosts.add(host);
			this.init();
		}
	}
	
	public void remove(T host) {
		synchronized (this.hosts) {
			this.hosts.remove(host);
			this.init();
		}
	}
	
	public void add(List<T> hosts) {
		synchronized (this.hosts) {
			this.hosts.addAll(hosts);
			this.init();
		}
	}
	
	public void remove(List<T> hosts) {
		synchronized (this.hosts) {
			this.hosts.removeAll(hosts);
			this.init();
		}
	}
	
	public void removeAll() {
		synchronized (this.hosts) {
			this.hosts.clear();
			this.init();
		}
	}
	
	public static void main1(String[] arg) {
		List<Host> hosts=new ArrayList<Host>();
		hosts.add(new Host("192.168.0.1",80,30));
		hosts.add(new Host("192.168.0.2",80,32));
		hosts.add(new Host("192.168.0.3",80,50));
		Bundle<Host> b=new Bundle<Host>("query",hosts);
		
		HashMap<Host, Integer> count=new HashMap<Host, Integer>();
		
		for(int i=0;i<10000;i++) {
			Host server=b.getHost();
			if(count.containsKey(server)) {
				count.put(server, count.get(server)+1);
			}else {
				count.put(server, 1);
			}
		}
		
		for(Entry<Host, Integer> entry:count.entrySet()) {
			System.out.println(entry.getKey()+"==="+entry.getValue());
		}
	}
	
	public static void main(String[] arg) {
		List<Host> hosts=new ArrayList<Host>();
		hosts.add(new WebAppHost("192.168.0.1",80,30,"app1"));
		hosts.add(new WebAppHost("192.168.0.2",80,32,"app1"));
		hosts.add(new WebAppHost("192.168.0.3",80,50,"app1"));
		Bundle<Host> b=new Bundle<Host>("query",hosts);
		
		HashMap<Host, Integer> count=new HashMap<Host, Integer>();
		
		for(int i=0;i<10000;i++) {
			Host server=b.getHost();
			if(count.containsKey(server)) {
				count.put(server, count.get(server)+1);
			}else {
				count.put(server, 1);
			}
		}
		
		for(Entry<Host, Integer> entry:count.entrySet()) {
			System.out.println(entry.getKey()+"==="+entry.getValue());
		}
	}
	
	public Host getHost() {
		int index=getRandom(this.priorityCount);
		try {
			return this.hosts.get(this.routeMap[index]);
		} catch (Exception e) {
			System.out.println(index);
			e.printStackTrace();
		}
		return null;
	}
	
	public static int getRandom(int seed) {
		return (int) Math.floor(Math.random() * seed);
	}
	
}
