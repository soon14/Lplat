package com.zengshi.butterfly.app.filter;

import java.util.List;

public class FilterChain<T> {
	
	private List<IFilter<T>> filters;

	public void setFilters(List<IFilter<T>> filters) {
		this.filters = filters;
	}
	
	public T doFilterChain(T context) throws Exception
	{
		if(filters == null || filters.isEmpty())
			return context;
		
		for(IFilter<T> filter : filters)
			context = filter.doFilter(context);
		
		return context;
				
	}
	
	/**
	 * 释放资源
	 * @throws Exception
	 */
	public void destroyFilterChain() throws Exception
	{
		if(filters == null || filters.isEmpty())
			return;
		
		for(IFilter<T> filter : filters)
			filter.destroy();
	}
	
}
