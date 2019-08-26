/**
 * 
 */
package com.zengshi.butterfly.core.examples.tree;

import com.zengshi.butterfly.core.tree.ITreeable;

/**
 *
 */
public class Menu implements ITreeable{

	private String menuId;
	private String menuName;
	private String menuDesc;
	private String busiType;
	private String menuPid;
	
	
	public Menu(String menuId, String menuName, String menuDesc,
			String busiType, String menuPid) {
		super();
		this.menuId = menuId;
		this.menuName = menuName;
		this.menuDesc = menuDesc;
		this.busiType = busiType;
		this.menuPid = menuPid;
	}
	public String getMenuId() {
		return menuId;
	}
	public void setMenuId(String menuId) {
		this.menuId = menuId;
	}
	public String getMenuName() {
		return menuName;
	}
	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}
	public String getMenuDesc() {
		return menuDesc;
	}
	public void setMenuDesc(String menuDesc) {
		this.menuDesc = menuDesc;
	}
	public String getBusiType() {
		return busiType;
	}
	public void setBusiType(String busiType) {
		this.busiType = busiType;
	}
	public String getMenuPid() {
		return menuPid;
	}
	public void setMenuPid(String menuPid) {
		this.menuPid = menuPid;
	}
	@Override
	public String getNodeId() {
		// TODO Auto-generated method stub
		return this.menuId;
	}
	@Override
	public String getParentNodeId() {
		// TODO Auto-generated method stub
		return this.menuPid;
	}
	
	
}
