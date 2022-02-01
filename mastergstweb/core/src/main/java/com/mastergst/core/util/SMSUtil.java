/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.core.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SMSUtil {

	private static final String TEMPLATE_SMS = "Your OTP number is";

	/**
	 * Sends the SMS to destination numbers
	 * 
	 * @param destinationNos
	 *            List of destination numbers
	 * @param message
	 *            SMS Message as a String
	 * @return String as a result
	 * @throws BaseException
	 */
	public static String sendSMS(final List<String> destinationNos, String message) throws Exception {
		final String method = "::sendSMS(final List<String> destinationNos,final String message)::";

		String smsStatus = "";
		try {

			for (String destinationNo : destinationNos) {
				smsStatus = sendSMS(
						prepareRequestString(destinationNo, TEMPLATE_SMS +" "+ message +" - MasterGST", "mastergst", "04244805"),
						"http://smscountry.com/SMSCwebservice_Bulk.aspx");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return smsStatus;
	}

	/**
	 * Sends the SMS to destination numbers
	 * 
	 * @param destinationNos
	 *            List of destination numbers
	 * @param message
	 *            SMS Message as a String
	 * @param smsgatewayUserName
	 *            SMS Gateway UserName as a String
	 * @param smsgatewayPassword
	 *            SMS Gateway Password as a String
	 * @param smsgatewayURL
	 *            SMS Gateway URL as a String
	 * @param String
	 *            as a result
	 * @throws BaseException
	 */
	public static String sendSMS(final List<String> destinationNos, final String message,
			final String smsgatewayUserName, final String smsgatewayPassword, final String smsgatewayURL)
			throws Exception {
		final String method = "::sendSMS(final List<String> destinationNos,final String message, final String smsgatewayUserName,final String smsgatewayPassword, final String smsgatewayURL)::";

		String smsStatus = "";
		try {
			for (String destinationNo : destinationNos) {
				smsStatus = sendSMS(prepareRequestString(destinationNo, TEMPLATE_SMS +" "+ message +" - MasterGST", "mastergst", "04244805"),
						"http://smscountry.com/SMSCwebservice_Bulk.aspx");
			}
		} catch (Exception e) {

			e.printStackTrace();
		}

		return smsStatus;
	}

	/**
	 * Build up Query String for sending SMS
	 * 
	 * @param destinationNo
	 *            destination number
	 * @param message
	 *            SMS Message as a String
	 * @param smsgatewayUserName
	 *            SMS Gateway UserName as a String
	 * @param smsgatewayPassword
	 *            SMS Gateway Password as a String
	 * @param smsgatewayURL
	 *            SMS Gateway URL as a String
	 * @return String as a Request XML
	 */
	private static String prepareRequestString(final String destinationNo, final String message,
			final String smsgatewayUserName, final String smsgatewayPassword) throws UnsupportedEncodingException {
		return "User=" + URLEncoder.encode(smsgatewayUserName, "UTF-8") + "&passwd=" + smsgatewayPassword
				+ "&mobilenumber=" + destinationNo + "&message=" + URLEncoder.encode(message, "UTF-8")
				+ "&sid=mstgst&mtype=N&DR=Y";
	}

	/**
	 * Send your XML in an HTTP POST to the SMS provider XML gateway
	 * 
	 * @param requestString
	 *            XML as a requestString
	 * @param String
	 *            as a result
	 * @throws BaseException
	 */
	private static String sendSMS(final String requestString, final String smsgatewayURL) throws Exception {
		final String method = "::send2SMS(final String requestString,final String smsgatewayURL )::";

		String response = "";
		try {
			// Send your XML in an HTTP POST to the 2sms XML gateway
			URL url = new URL(smsgatewayURL);
			HttpURLConnection urlconnection = (HttpURLConnection) url.openConnection();
			urlconnection.setRequestMethod("POST");
			urlconnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			urlconnection.setDoOutput(true);
			OutputStreamWriter out = new OutputStreamWriter(urlconnection.getOutputStream());
			out.write(requestString);
			out.close();
			BufferedReader in = new BufferedReader(new InputStreamReader(urlconnection.getInputStream()));
			String decodedString;
			while ((decodedString = in.readLine()) != null) {
				response += decodedString;
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	public static void main(String[] args) throws Exception {
		System.out.println("ASVSYSCY");
		List<String> destinationNos = new ArrayList<String>();
		destinationNos.add("9959239453");
		String sms = sendSMS(destinationNos, OTP(4));
		System.out.println("sms\t" + sms);
	}

	public static String OTP(int len) {
		String numbers = "0123456789";
		Random rndm_method = new Random();
		char[] otp = new char[len];
		for (int i = 0; i < len; i++) {
			otp[i] = numbers.charAt(rndm_method.nextInt(numbers.length()));
		}
		return new String(otp);
	}
}
