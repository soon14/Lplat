package com.zengshi.paas.exception;

public class PropertyMissingException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7186053906495295473L;

	public PropertyMissingException(String propertyKey) {
		super("property config " + propertyKey
				+ " is missing,please check your config!");
	}

	public PropertyMissingException() {
		super();
	}

	public PropertyMissingException(String propertyKey, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super("property config " + propertyKey
				+ " is missing,please check your config!", cause,
				enableSuppression, writableStackTrace);
	}

	public PropertyMissingException(String propertyKey, Throwable cause) {
		super("property config " + propertyKey
				+ " is missing,please check your config!", cause);
	}

	public PropertyMissingException(Throwable cause) {
		super(cause);
	}

}
