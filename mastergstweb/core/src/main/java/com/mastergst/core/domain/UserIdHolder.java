/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.core.domain;

public class UserIdHolder {
	
	private static ThreadLocal<String> currentUserId = new ThreadLocal<>();
	
	public static String getCurrentUserId(){
		return currentUserId.get();
	}
	
	public static void setCurrentUserId(String userId){
		currentUserId.set(userId);
	}
	
	public static void remove(){
		currentUserId.remove();
	}

}
