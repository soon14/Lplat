/**
 * 
 */
package com.zengshi.butterfly.core.invoker;

import java.io.Serializable;

/**
 *
 */
public interface Response extends Serializable {

	public String getCode();
	
	public String getMsg();
}
