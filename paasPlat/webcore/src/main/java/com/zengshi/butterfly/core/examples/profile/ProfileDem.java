/**
 * 
 */
package com.zengshi.butterfly.core.examples.profile;

import com.zengshi.butterfly.core.util.profiling.UtilTimerStack;

/**
 *
 */
public class ProfileDem {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		UtilTimerStack.setActive(true);
		String timekey="main";
		try {
			UtilTimerStack.push(timekey);
			invoke0();
		} catch (Exception e) {

		}finally {
			UtilTimerStack.pop(timekey);
		}

	}

	public static void invoke0() {
		String timeKey="invoke0";
		try {
			UtilTimerStack.push(timeKey);
			Thread.sleep(10);
			invoke1();
			invoke2();
		} catch (Exception e) {

		}finally {
			UtilTimerStack.pop(timeKey);
		}
	}
	public static void invoke1() {
		String timeKey="invoke1";
		try {
			UtilTimerStack.push(timeKey);
			Thread.sleep(10);
		} catch (Exception e) {

		}finally {
			UtilTimerStack.pop(timeKey);
		}
	}
	public static void invoke2() {
		String timeKey="invoke2";
		try {
			UtilTimerStack.push(timeKey);
			Thread.sleep(10);
			invoke3();
		} catch (Exception e) {
			// TODO: handle exception
		}finally {
			UtilTimerStack.pop(timeKey);
		}
	}
	public static void invoke3() {
		String timeKey="invoke3";
		try {
			UtilTimerStack.push(timeKey);
			Thread.sleep(10);
		} catch (Exception e) {
			
		}finally {
			UtilTimerStack.pop(timeKey);
		}
	}
}
