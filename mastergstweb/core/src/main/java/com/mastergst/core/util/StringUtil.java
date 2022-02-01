/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.core.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Sets;

/**
 * Util class for Strings.
 * 
 * @author Ashok Samrat
 * @version 1.0 25 January 2017
 * @since 1.0
 */
public class StringUtil {
	/**
	 * This method returns common values among collection of values.
	 * 
	 * @param collection
	 * @return collection
	 */
	public static Set<String> getCommonValues(List<List<String>> values) {
		Set<String> commonValues = new HashSet<String>();
		String leftValue = "";
		for (int i = 0; i < values.size() - 1; i++) {
			if (i == 0) {
				leftValue = getCommaSeperatedString(values.get(i));
			}
			String rightValue = getCommaSeperatedString(values.get(i + 1));

			Set<String> leftSet = Sets.newHashSet(Splitter.on(',').omitEmptyStrings().split(leftValue));
			Set<String> rightSet = Sets.newHashSet(Splitter.on(',').omitEmptyStrings().split(rightValue));

			commonValues = Sets.intersection(leftSet, rightSet);
			leftValue = Joiner.on(",").join(commonValues);
		}
		return commonValues;
	}

	/**
	 * This method converts list to comma separated string.
	 * 
	 * @param collection
	 * @return String
	 */
	public static String getCommaSeperatedString(List<String> values) {
		return Joiner.on(",").join(values);
	}
}
