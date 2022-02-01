/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.core.util;

import java.net.InetAddress;
import java.util.Map;

import com.google.common.collect.Maps;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;

public class UnirestTest {

	//header keys---ip_address,client_id,client_secret,state_cd,gst_username
	public static void main(String[] args) {
		
		Map<String, String> gstnHeader = Maps.newHashMap();
		try {
			String requestUrl = String.format("%s/%s", "http://127.0.0.1:8080", "/mastergst-ihub/authentication/otprequest");
			
			gstnHeader.put("client_id", "0096d450-5817-4aff-8228-588c0daac8bc");
			gstnHeader.put("client_secret", "83961f99-dc4d-4c53-a552-83186dfb7ee1");
			gstnHeader.put("ip_address", InetAddress.getLocalHost().getHostAddress());
			gstnHeader.put("state_cd", "33");
			gstnHeader.put("gst_username", "Tera.TN.1");
			
			String email="admin@abcdef.com";
			
			HttpResponse<JsonNode> jsonResponse = Unirest.get(requestUrl)
					.queryString("email", email)
					.headers(gstnHeader).asJson();
			System.out.println(jsonResponse.getBody().toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
