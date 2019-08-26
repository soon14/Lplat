/**
 * 
 */
package com.zengshi.butterfly.core.examples.tree;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import com.zengshi.butterfly.core.tree.DefaultTree;

/**
 *
 */
public class DefalutTreeExample {

	private static final String file_path="/home/yangqx/work/share/workspace/zj/butterfly/butterfly-core/src/main/java/com/zengshi/butterfly/core/examples/tree/mockdata.txt";
	public static void main(String[] arg) {
		
		//FileUtil.file2String("/com/zengshi/core/tree/example/mockdata.txt", "utf-8");
		List<Menu> menus=new ArrayList<Menu>();
		try {
			BufferedReader br=new BufferedReader(new FileReader(file_path));
			while(br.ready()) {
				String line=br.readLine();
				String[] datas=line.split("\t");
				Menu m=new Menu(datas[0],datas[1],datas[2],datas[3],datas[4]);
				menus.add(m);
			}
			System.out.println(menus.size());
			DefaultTree<Menu> tree=new DefaultTree<Menu>("0",menus);
			System.out.println(tree.getRoot());
			System.out.println(tree.getNode("20207749"));
			System.out.println(tree.getNode("296"));
			System.out.println(tree.getNode("295"));
			System.out.println(tree.getNode("294"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
}
