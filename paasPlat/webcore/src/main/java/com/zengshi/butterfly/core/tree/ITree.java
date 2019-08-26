package com.zengshi.butterfly.core.tree;

import java.util.List;

/**
 * 树型结构接口
 *
 * @param <T>
 */
public interface ITree<T> {

	/**
	 * 设置当前节点ID
	 * @param nodeId
	 */
	public void setNodeId(String nodeId);
	
	/**
	 * 获取当前节点ID
	 * @return
	 */
	public String getNodeId();
	
	/**
	 * 取父节点ID值
	 * @return
	 */
	public String getParentNodeId();
	
	/**
	 * 取父节点
	 * @return
	 */
	public ITree<T> getParent();
	
	/**
	 * 取所有子节点
	 * @return
	 */
	public <K extends ITree<T>>  List<K> getChildren();
	
	/**
	 * 添加子节点
	 * @param node
	 */
	public  void  addChild(ITree<T> node);
	
	/**
	 * 在指定索引处添加子节点
	 * @param index
	 * @param node
	 */
	public  void addChild(int index,ITree<T> node) ;
	
	/**
	 * 取根节点
	 * @return
	 */
	public ITree<T> getRoot();
	
	/**
	 * 移除子节点
	 * @param index
	 */
	public void removeChild(int index);
	
	/**
	 * 移除子节点
	 * @param node
	 */
	public <K extends ITree<T>>  void removeChild(K node);
	
	/**
	 * 移除当前节点
	 */
	public void remove();
	
	/**
	 * 是否有字节点
	 * @return
	 */
	public boolean hasChildren();
	
	/**
	 * 是否是根节点
	 * @return
	 */
	public boolean isRoot();
	
	/**
	 * 设置节点值
	 * @param object
	 */
	public void setObject(T object);
	
	/**
	 * 取节点值
	 * @return
	 */
	public T getObject();
}
