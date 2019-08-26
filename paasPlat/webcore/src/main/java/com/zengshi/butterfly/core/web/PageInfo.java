/**
 * 
 */
package com.zengshi.butterfly.core.web;

/**
 *
 */
public class PageInfo {

	private Integer pageStart;
	
	private Integer pageCount;
	
	private Long totalCount;
	
	

	public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getPageStart() {
		return pageStart;
	}

	public void setPageStart(Integer pageStart) {
		this.pageStart = pageStart;
	}

	public Integer getPageCount() {
		return pageCount;
	}

	public void setPageCount(Integer pageCount) {
		this.pageCount = pageCount;
	}
	
	
}
