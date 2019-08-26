/**
 * 
 */
package com.zengshi.butterfly.core.route;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.core.OrderComparator;

/**
 *
 */
@Deprecated
public class RouteBalance {

	private List<Route> routes=new ArrayList<Route>();
	
	
	public  RouteBalance(String balance) {
		String[] route=balance.split(";");
		for(String r:route) {
			routes.add(createRoute(r));
		}
		OrderComparator.sort(routes);
	}
	
	private Route createRoute(String route) {
		int idx_port=route.indexOf(":");
		int idx_prority=route.indexOf(",");
		if(idx_prority == -1) {
			idx_prority = route.length();
		}
		
		String ip=route.substring(0, idx_port);

		String port=route.substring(idx_port+1,idx_prority);
		String prority=idx_prority==route.length()?"100":route.substring(idx_prority+1);
		Route host=new Route(ip, port, Integer.parseInt(prority));
		
		return host;
	}
	
	public Route getHost() {
		return routes.get(0);
	}
	
	public static void main(String [] arg) {
		String balance="192.168.0.1:8080,20;192.168.0.2:8081,80";
		
		RouteBalance balance1=new RouteBalance(balance);
		for(Route r:balance1.routes) {
			System.out.println(r);
		}
		
		  java.util.Random r=new java.util.Random(10); 
		  for(int i=0;i<20;i++){ 
		      System.out.println(r.nextInt(100)); 
		  } 
	}
}
