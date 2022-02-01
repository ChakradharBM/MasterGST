/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.core.util;

import java.util.Collection;
import java.util.Map;

/**
 * This Checks the Null.
 * 
 * @author Ashok Samrat
 * @version 1.0 25 January 2017
 * @since 1.0
 */

public final class NullUtil {
	/**
	 * This method returns true if the collection is null or is empty.
	 * 
	 * @param collection
	 * @return true | false
	 */
	public static boolean isEmpty(Collection<?> collection) {
		if (collection == null || collection.isEmpty()) {
			return true;
		}
		return false;
	}

	/**
	 * This method returns true of the map is null or is empty.
	 * 
	 * @param map
	 * @return true | false
	 */
	public static boolean isEmpty(Map<?, ?> map) {
		if (map == null || map.isEmpty()) {
			return true;
		}
		return false;
	}

	/**
	 * This method returns true if the objet is null.
	 * 
	 * @param object
	 * @return true | false
	 */
	public static boolean isEmpty(Object object) {
		if (object == null) {
			return true;
		}
		return false;
	}

	/**
	 * This method returns true if the input array is null or its length is
	 * zero.
	 * 
	 * @param array
	 * @return true | false
	 */
	public static boolean isEmpty(Object[] array) {
		if (array == null || array.length == 0) {
			return true;
		}
		return false;
	}

	/**
	 * This method returns true if the input string is null or its length is
	 * zero.
	 * 
	 * @param string
	 * @return true | false
	 */
	public static boolean isEmpty(String string) {
		if (string == null || string.trim().length() == 0) {
			return true;
		}
		return false;
	}

	/**
	 * This method returns true if the collection is null or is empty.
	 * 
	 * @param collection
	 * @return true | false
	 */
	public static boolean isNotEmpty(Collection<?> collection) {
		if (collection != null && !collection.isEmpty()) {
			return true;
		}
		return false;
	}

	/**
	 * This method returns true of the map is null or is empty.
	 * 
	 * @param map
	 * @return true | false
	 */
	public static boolean isNotEmpty(Map<?, ?> map) {
		if (map != null && !map.isEmpty()) {
			return true;
		}
		return false;
	}

	/**
	 * This method returns true if the objet is null.
	 * 
	 * @param object
	 * @return true | false
	 */
	public static boolean isNotEmpty(Object object) {
		if (object != null) {
			return true;
		}
		return false;
	}

	/**
	 * This method returns true if the input array is null or its length is
	 * zero.
	 * 
	 * @param array
	 * @return true | false
	 */
	public static boolean isNotEmpty(Object[] array) {
		if (array != null && array.length != 0) {
			return true;
		}
		return false;
	}

	/**
	 * This method returns true if the input string is null or its length is
	 * zero.
	 * 
	 * @param string
	 * @return true | false
	 */
	public static boolean isNotEmpty(String string) {
		if (string != null && string.trim().length() != 0) {
			return true;
		}
		return false;
	}

}
