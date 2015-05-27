package com.wuxianyingke.property.remote;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

import com.wuxianyingke.property.common.LogUtil;

public class HttpConnect {

	private int connectTimeout;
	private int readTimeout;
	private int connections;
	private String encoding;

	public HttpConnect() {
		connectTimeout = 8000;
		readTimeout = 10000;
		connections = 1;
		encoding = "UTF-8";
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public int getConnections() {
		return connections;
	}

	public void setConnections(int connections) {
		if (connections < 1)
			connections = 1;
		this.connections = connections;
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public int getReadTimeout() {
		return readTimeout;
	}

	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}

	public HttpURLConnection connectByGet(String url, Map params)
			throws IOException {
		if (params != null) {
			if (url.contains("?"))
				url = (new StringBuilder(String.valueOf(url))).append("&")
						.toString();
			else
				url = (new StringBuilder(String.valueOf(url))).append("?")
						.toString();
			for (Iterator iterator = params.entrySet().iterator(); iterator
					.hasNext();) {
				java.util.Map.Entry param = (java.util.Map.Entry) iterator
						.next();
				url = (new StringBuilder(String.valueOf(url))).append(
						(String) param.getKey()).append("=").append(
						(String) param.getValue()).append("&").toString();
			}

		}
		LogUtil.d("MyTag", (new StringBuilder("url is ")).append(url).toString());
		URL myURL = new URL(url);
		HttpURLConnection conn = null;
		conn = (HttpURLConnection) myURL.openConnection();
		if (connectTimeout != -1)
			conn.setConnectTimeout(connectTimeout);
		if (readTimeout != -1)
			conn.setReadTimeout(readTimeout);
		for (int i = 0; i < connections;)
			try {
				conn.connect();
				break;
			} catch (IOException e) {
				if (i == connections - 1) {
					LogUtil.d("MyTag", (new StringBuilder("Connect error: "))
							.append(e.getMessage()).toString());
					e.printStackTrace();
				}
				i++;
			}

		return conn;
	}

	public String connectByGetString(String url, Map params)  {
		HttpURLConnection conn =null;
		String s;
		try {
			conn = connectByGet(url, params);
			s = responseString(conn);
		} catch (Exception e) {
			e.printStackTrace();
			conn.disconnect();
			return null;
		} finally {
			conn.disconnect();
		}
		conn.disconnect();
		return s;
	}

	private String responseString(HttpURLConnection conn)  {
		
		StringBuilder sb2 = new StringBuilder();
		String result="";
		try {
			int res = conn.getResponseCode();
			if (res == 200) {
				InputStream inputStream = conn
						.getInputStream();// 获取返回的数据流

				InputStreamReader isr = new InputStreamReader(
						inputStream, "UTF-8");// 一定要在这个地方才不会乱码(utf-8,gb2312)

				BufferedReader br = new BufferedReader(isr);// 利用BufferedReader将流转为String

				String temp;

				while ((temp = br.readLine()) != null) {
					result = result + temp;
				}
				
				/*
				InputStream in = conn.getInputStream();
				int ch;
				while ((ch = in.read()) != -1)
					sb2.append((char) ch);*/
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

}
