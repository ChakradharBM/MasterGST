/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.core.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Formatter;
import java.util.logging.Logger;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;

public class TCClient {
	private static final Logger m_Logger = Logger.getLogger(TCClient.class.getName());
	private static TrustManager[] trustAllCerts = null;

	private final static String PROTOCOL = "https://";
	private final static String UPLOAD_HASH_URL = "/services/corpservice/v2/uploadHash";

	private final static int SUCCESS = 0;
	private final static int FAILED = 1;

	static {
		// Create a trust manager that does not validate certificate chains
		trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(X509Certificate[] certs, String authType) {
			}

			public void checkServerTrusted(X509Certificate[] certs, String authType) {
			}
		} };
	}

	public TCClient() {
		try {
			trustCerts();
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void trustCerts() throws NoSuchAlgorithmException, KeyManagementException {
		// Install the all-trusting trust manager
		final SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, trustAllCerts, new java.security.SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		// Create all-trusting host name verifier
		HostnameVerifier allHostsValid = new HostnameVerifier() {
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		};
		HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
	}

	/**
	 * Accept a Sha256 hex encoded string
	 * 
	 * @param domain
	 * @param uuid
	 * @param cs
	 * @param input256Hash
	 *            SHA256 Hex encoded string (Length: 64)
	 * @param emailid
	 * @return Success / Fail
	 */
	public int uploadHash(String domain, String uuid, String cs, String input256Hash, String emailid) {
		int iStatus = -1;
		if (emailid == null) {
			emailid = "";
		}

		String WSURL = PROTOCOL + domain + UPLOAD_HASH_URL;
		URLConnection connection = null;
		try {
			// Code to make a webservice HTTP request
			URL url = new URL(WSURL);
			connection = url.openConnection();
			MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE,
					"---------------------------" + System.currentTimeMillis(), Charset.forName("UTF-8"));
			multipartEntity.addPart("uuid", new StringBody(uuid));
			multipartEntity.addPart("cs", new StringBody(cs));
			multipartEntity.addPart("emailid", new StringBody(emailid));
			multipartEntity.addPart("uploadhash", new StringBody(input256Hash));

			HttpsURLConnection httpConn = (HttpsURLConnection) connection;
			httpConn.setRequestMethod("POST");
			httpConn.setRequestProperty("Cache-Control", "no-cache");
			httpConn.setDoOutput(true);
			httpConn.setDoInput(true);
			httpConn.setRequestProperty("Content-Length", String.valueOf(multipartEntity.getContentLength()));
			httpConn.setRequestProperty("Content-Type", multipartEntity.getContentType().getValue());
			httpConn.setRequestProperty("Accept", "application/json");

			OutputStream out = connection.getOutputStream();
			multipartEntity.writeTo(out);
			out.close();

			iStatus = httpConn.getResponseCode();
			// Read the response.
			if (iStatus == 200) {
				m_Logger.info("Success");
				StringBuffer inbuff = new StringBuffer();
				String responseString = "";
				InputStreamReader isr = new InputStreamReader(httpConn.getInputStream());
				BufferedReader in = new BufferedReader(isr);

				while ((responseString = in.readLine()) != null) {
					inbuff.append(responseString);
				}
				iStatus = SUCCESS;
				m_Logger.info("Success: " + inbuff.toString());
				System.out.println("Successful hash upload");
			} else {
				iStatus = FAILED;
				m_Logger.info("Failed:  " + iStatus);
			}
		} catch (IOException ioe) {
			m_Logger.warning("1 " + ioe.getMessage());
		} catch (NullPointerException npe) {
			m_Logger.warning("2 " + npe.getMessage());
		} catch (Exception e) {
			m_Logger.severe("3 " + e.getMessage());
		}
		return iStatus;
	}

	/**
	 * Accept a Sha256 hex encoded string
	 * 
	 * @param domain
	 * @param uuid
	 * @param cs
	 * @param input256Hash
	 *            SHA256 Hex encoded string (Length: 64)
	 * @param emailid
	 * @return Success / Fail
	 */
	public int markforSignCorpfile(String domain, String uuid, String cs, String doccategory, String emailid,
			String sendemail) {
		int iStatus = -1;
		if (emailid == null) {
			emailid = "";
		}

		String WSURL = PROTOCOL + domain + "/services/corpservice/v2/markforsigncorpfiledsc";
		URLConnection connection = null;
		try {
			// Code to make a webservice HTTP request
			URL url = new URL(WSURL);
			connection = url.openConnection();
			MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE,
					"---------------------------" + System.currentTimeMillis(), Charset.forName("UTF-8"));
			multipartEntity.addPart("uuid", new StringBody(uuid));
			multipartEntity.addPart("cs", new StringBody(cs));
			multipartEntity.addPart("doc_category", new StringBody(doccategory));
			multipartEntity.addPart("emailid", new StringBody(emailid));
			multipartEntity.addPart("sendemail", new StringBody(sendemail));

			HttpsURLConnection httpConn = (HttpsURLConnection) connection;
			httpConn.setRequestMethod("POST");
			httpConn.setRequestProperty("Cache-Control", "no-cache");
			httpConn.setDoOutput(true);
			httpConn.setDoInput(true);
			httpConn.setRequestProperty("Content-Length", String.valueOf(multipartEntity.getContentLength()));
			httpConn.setRequestProperty("Content-Type", multipartEntity.getContentType().getValue());
			httpConn.setRequestProperty("Accept", "application/json");

			OutputStream out = connection.getOutputStream();
			multipartEntity.writeTo(out);
			out.close();

			iStatus = httpConn.getResponseCode();
			// Read the response.
			if (iStatus == 200) {
				m_Logger.info("Success");
				StringBuffer inbuff = new StringBuffer();
				String responseString = "";
				InputStreamReader isr = new InputStreamReader(httpConn.getInputStream());
				BufferedReader in = new BufferedReader(isr);

				while ((responseString = in.readLine()) != null) {
					inbuff.append(responseString);
				}
				iStatus = SUCCESS;
				m_Logger.info("Success: " + inbuff.toString());
				System.out.println("Successful hash upload");
			} else {
				iStatus = FAILED;
				m_Logger.info("Failed:  " + iStatus);
			}
		} catch (IOException ioe) {
			m_Logger.warning("1 " + ioe.getMessage());
		} catch (NullPointerException npe) {
			m_Logger.warning("2 " + npe.getMessage());
		} catch (Exception e) {
			m_Logger.severe("3 " + e.getMessage());
		}
		return iStatus;
	}

	public int downlaodSign(String domain, String uuid, String cs) {
		int iStatus = -1;

		String WSURL = PROTOCOL + domain + "/services/corpservice/v2/fetchsignbuffer";
		URLConnection connection = null;
		try {
			// Code to make a webservice HTTP request
			URL url = new URL(WSURL + "/" + uuid + "/" + cs);

			HttpURLConnection con = (HttpURLConnection) url.openConnection();

			// optional default is GET
			con.setRequestMethod("GET");
			con.setDoInput(true);
			con.setDoOutput(true);

			// add request header
			con.setRequestProperty("User-Agent", "Mozilla/5.0");

			int responseCode = con.getResponseCode();
			System.out.println("\nSending 'GET' request to URL : " + url);
			System.out.println("Response Code : " + responseCode);

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// print result
			System.out.println(response.toString());
		} catch (IOException ioe) {
			m_Logger.warning("1 " + ioe.getMessage());
		} catch (NullPointerException npe) {
			m_Logger.warning("2 " + npe.getMessage());
		} catch (Exception e) {
			m_Logger.severe("3 " + e.getMessage());
		}
		return iStatus;
	}

	// //////////////////////////////////////////////////////////////////////////////
	// Utility functions
	// //////////////////////////////////////////////////////////////////////////////

	public static String getCS(String APIKEY, String uuid) {
		return md5_sum16(APIKEY + uuid);
	}

	public static String getDownloadCS(String APIKEY, String uuid) {
		return md5_sum16(APIKEY + (md5_sum16(APIKEY + uuid)));
	}

	public static String md5_sum16(String sData) {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
			return byteArray2Hex(md.digest(sData.getBytes())).substring(0, 16);
		} catch (NoSuchAlgorithmException e) {
			return "NA";
		}
	}

	private static String byteArray2Hex(final byte[] hash) {
		Formatter formatter = new Formatter();
		for (byte b : hash) {
			formatter.format("%02x", b);
		}
		return formatter.toString().toUpperCase();
	}

	public static String getSha256(String value) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(value.getBytes());
			return bytesToHex(md.digest());
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	private static String bytesToHex(byte[] bytes) {
		StringBuffer result = new StringBuffer();
		for (byte b : bytes)
			result.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
		return result.toString();
	}
}
