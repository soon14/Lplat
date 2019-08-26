/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zengshi.butterfly.core.util.sequence;

/**
 * 
 */
public class Sequence {

	private String name;
	private Long max ;
	private Long min ;
	private int cacheCount =1;

	private Long current=min;
	private Long start;
	private Long end;
	private int step = 1;
	
	public Sequence(String name) {
		this(name,1);
	}
	
	public Sequence(String name, int cacheCount) {
		this(name,cacheCount,1);
	}
	public Sequence(String name, int cacheCount,int step) {
		this(name,cacheCount,step,Long.MAX_VALUE);
	}
	public Sequence(String name, int cacheCount,int step,Long max) {
		this(name,cacheCount,step,max,0l);
	}
	public Sequence(String name, int cacheCount,int step,Long max,Long min) {
		super();
		this.name = name;
		this.cacheCount = cacheCount;
		this.step=step;
		this.max=max;
		this.min=min;
		this.current=min;
		this.start=min;
		this.end=this.min+this.cacheCount;
	}
	
	public int getStep() {
		return step;
	}
	public void setStep(int step) {
		this.step = step;
	}
	public boolean isMaxValue() {
		return (this.current+this.step)  > this.max;
	}
	public boolean isOvered() {
		return (this.current+this.step)  > this.end;
	}

	public Long getStart() {
		return start;
	}

	public void setStart(Long start) {
		this.start = start;
	}

	public Long getEnd() {
		return end;
	}

	public void setEnd(Long end) {
		this.end = end;
	}

	public Long getCurrent() {
		return current;
	}

	public void setCurrent(Long current) {
		this.current = current;
	}

	public Long getNext() {
		return (this.current=(this.current+this.step));
	}


	public int getCacheCount() {
		return cacheCount;
	}

	public void setCacheCount(int cacheCount) {
		this.cacheCount = cacheCount;
	}

	public Long getMax() {
		return max;
	}

	public void setMax(Long max) {
		this.max = max;
	}

	public Long getMin() {
		return min;
	}

	public void setMin(Long min) {
		this.min = min;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
