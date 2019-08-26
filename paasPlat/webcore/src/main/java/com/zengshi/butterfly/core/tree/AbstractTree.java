package com.zengshi.butterfly.core.tree;

import java.util.ArrayList;
import java.util.List;

public class  AbstractTree<T> implements ITree<T>{

	protected String nodeId;
	
	protected String parentNodeId;
	
	protected ITree<T> parent;
	
	protected  List<ITree<T> > chilrend;
	
	protected ITree<T> root;
	
	protected T value;
	
	@Override
	public void setNodeId(String nodeId) {
		this.nodeId=nodeId;
		
	}

	@Override
	public String getNodeId() {
		// TODO Auto-generated method stub
		return this.nodeId;
	}

	@Override
	public String getParentNodeId() {
		// TODO Auto-generated method stub
		return this.parentNodeId;
	}

	@Override
	public ITree<T> getParent() {
		// TODO Auto-generated method stub
		return this.parent;
	}

	@Override
	public  List<ITree<T>> getChildren() {
		// TODO Auto-generated method stub
		return this.chilrend;
	}

	@Override
	public  void addChild(ITree<T> node) {
		if(this.chilrend == null) this.chilrend = new ArrayList<ITree<T>>();
		this.chilrend.add(node);
		
	}

	@Override
	public  void addChild(int index,ITree<T> node) {
		if(this.chilrend == null) this.chilrend = new ArrayList<ITree<T>>();
		this.chilrend.add(index, node);
		
	}

	@Override
	public ITree<T> getRoot() {
		// TODO Auto-generated method stub
		return this.root;
	}
	
	protected void setRoot(ITree<T> root) {
		this.root=root;
	}

	@Override
	public void removeChild(int index) {
		this.chilrend.remove(index);
		
	}

	@Override
	public <K extends ITree<T>> void removeChild(K node) {
		this.chilrend.remove(node);
		
	}

	@Override
	public void remove() {
		this.parent.removeChild(this);
		
	}

	@Override
	public boolean hasChildren() {
		// TODO Auto-generated method stub
		return chilrend !=null && chilrend.size() >0;
	}

	@Override
	public boolean isRoot() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setObject(T object) {
		this.value=object;
		
	}

	@Override
	public T getObject() {
		// TODO Auto-generated method stub
		return this.value;
	}


}
