/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.core.util;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class CustomDoubleDeserializer extends JsonDeserializer<Double> {

	@Override
	public Double deserialize(JsonParser jsonParser, DeserializationContext arg1)
			throws IOException, JsonProcessingException {
		String valueAsString = jsonParser.getValueAsString();
		if (NullUtil.isEmpty(valueAsString)) {
			return null;
		}
		return Double.parseDouble(valueAsString);
	}

}
