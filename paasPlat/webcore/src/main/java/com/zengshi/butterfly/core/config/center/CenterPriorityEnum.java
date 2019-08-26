/**
 * 
 */
package com.zengshi.butterfly.core.config.center;

/**
 * 2011-7-18
 */
public enum CenterPriorityEnum {

	DB {
		@Override
		public int getPriority() {
			
			return 0;
		}
		
		@Override 
		public  Class getCenterClass(){
			return DBConfigCenter.class;
		}
	},
	CUSTORMER {
		@Override
		public int getPriority() {
			return 1;
		}
		@Override 
		public Class getCenterClass(){
			return CustomerConfigCenter.class;
		}
	},
	DEV{

		@Override
		public int getPriority() {
			
			return 2;
		}
		@Override 
		public Class getCenterClass(){
			return DevConfigCenter.class;
		}
	};
	
	public abstract int getPriority();
	
	public abstract Class getCenterClass();
	
	public String toString(){
		return this.getPriority()+"";
	}
	
	public static void main(String[] arg) {
		System.out.println(CenterPriorityEnum.DB.getPriority());
	}
}

