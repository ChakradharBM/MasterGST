/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.core.exception;

/**
 * This provides all Custom Exception of MastreGST
 * 
 * @author Ashok Samrat
 * @version 1.0
 * @since 1.0
 */

public class MasterGSTException extends Exception {

	private Throwable t;
	private String message;

	/**
	 * Default constructor for the class. It takes in no inputs
	 **/
	public MasterGSTException() {
		super();
	}

	/**
	 * Constructor with throwable class and errorcode as parameter
	 * 
	 * @Param t Throwable
	 * @Param message String
	 */
	public MasterGSTException(Throwable t, String message) {
		this.t = t;
		this.message = message;
	}

	/**
	 * Constructor with errorcode as parameter
	 * 
	 * @param message
	 *            String
	 */
	public MasterGSTException(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return message;
	}

	@Override
	public String getMessage() {
		return message;
	}

	/**
	 * Method return Throwable class.
	 * 
	 **/
	public Throwable getRootCause() {
		return t;
	}
}
