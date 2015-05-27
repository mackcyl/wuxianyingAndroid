package com.wuxianyingke.property.remote;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.MultipartPostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import android.util.Log;

import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LogUtil;

public class HttpComm
{
	private static final String TAG = "TAG";
	private static long timeInSec;

	private static String getToken()
	{
		Calendar curr = Calendar.getInstance();
		timeInSec = curr.getTimeInMillis() / 1000;
		String sigSource = timeInSec + "www.wuxianyingke.com";
//		String sig = Util.md5(sigSource.getBytes());
		return "";
	}

	public static JSONObject sendJSONToServer(String paramString, JSONObject jsonObject, int timeout)
	{
		try
		{
			String url = Constants.URL + paramString;
			LogUtil.d(TAG, "Url is " + url);
			/*jsonObject.put("token", getToken());
			jsonObject.put("timestamp", timeInSec);
			jsonObject.put("cpackname", Constants.PACKAGENAME);
			jsonObject.put("cversion", Constants.VERSION_CODE);
			jsonObject.put("roleid", Constants.ROLEID);
			jsonObject.put("platform", Constants.PLATFORM);*/
			
			URI uri = new URL(url).toURI();
			HttpPost httpPost = new HttpPost(uri);
			byte[] httpPostBytes = jsonObject.toString().getBytes();
			ByteArrayEntity byteArrayEntity = new ByteArrayEntity(httpPostBytes);

			LogUtil.d(TAG, "bytearray:" + new String(httpPostBytes));

			byteArrayEntity.setContentType("application/json");
			httpPost.setEntity(byteArrayEntity);
			DefaultHttpClient httpClient = new DefaultHttpClient();

			HttpParams httpParams = httpClient.getParams();
			//HttpConnectionParams.setSocketBufferSize(httpParams, 8192);
			HttpConnectionParams.setConnectionTimeout(httpParams, timeout);
			HttpConnectionParams.setSoTimeout(httpParams, timeout);

			LogUtil.d(TAG, "execute Http locahost");
			HttpResponse localObject = httpClient.execute(httpPost);
			LogUtil.d(TAG, "execute Http locahost finished " + localObject.getStatusLine().getStatusCode());
			if (((HttpResponse) localObject).getStatusLine().getStatusCode() != 200)
			{
				int statusCode = ((HttpResponse) localObject).getStatusLine().getStatusCode();
				LogUtil.d(TAG, "error statusCode = " + statusCode);
				return null;
			}
			StringBuilder builder = new StringBuilder();
			InputStreamReader is = new InputStreamReader(localObject.getEntity().getContent());
			BufferedReader bufferedReader2 = new BufferedReader(is);

			for (String s = bufferedReader2.readLine(); s != null; s = bufferedReader2.readLine())
			{
				builder.append(s);
			}
			LogUtil.d(TAG, ">>>>>>" + builder.toString());
			JSONObject retJsonObject = new JSONObject(builder.toString());
			return retJsonObject;

		} catch (Exception ex)
		{
			LogUtil.d(TAG, ">>>>>> error: " + ex.getMessage());
			ex.printStackTrace();
			return null;
		}
	}

	public static JSONObject sendJSONToShareServer(String paramString, JSONObject jsonObject, int timeout)
	{
		try
		{
			String url = Constants.URL + paramString;
			LogUtil.d(TAG, "Switch url is " + url);
			URI uri = new URL(url).toURI();

			HttpPost httpPost = new HttpPost(uri);
			byte[] httpPostBytes = jsonObject.toString().getBytes();
			ByteArrayEntity byteArrayEntity = new ByteArrayEntity(httpPostBytes);
			LogUtil.d(TAG, "bytearray:" + new String(httpPostBytes));
			byteArrayEntity.setContentType("application/json");
			httpPost.setEntity(byteArrayEntity);
			DefaultHttpClient httpClient = new DefaultHttpClient();

			HttpParams httpParams = httpClient.getParams();
			HttpConnectionParams.setSocketBufferSize(httpParams, 8192);
			HttpConnectionParams.setConnectionTimeout(httpParams, timeout);
			HttpConnectionParams.setSoTimeout(httpParams, timeout);

			LogUtil.d(TAG, "execute Http locahost");
			HttpResponse localObject = httpClient.execute(httpPost);
			LogUtil.d(TAG, "execute Http locahost finished " + localObject.getStatusLine().getStatusCode());
			if (((HttpResponse) localObject).getStatusLine().getStatusCode() != 200)
			{
				int statusCode = ((HttpResponse) localObject).getStatusLine().getStatusCode();
				LogUtil.d(TAG, "error statusCode = " + statusCode);
				return null;
			}
			StringBuilder builder = new StringBuilder();
			InputStreamReader is = new InputStreamReader(localObject.getEntity().getContent());
			BufferedReader bufferedReader2 = new BufferedReader(is);

			for (String s = bufferedReader2.readLine(); s != null; s = bufferedReader2.readLine())
			{
				builder.append(s);
			}
			LogUtil.d(TAG, ">>>>>>" + builder.toString());
			JSONObject retJsonObject = new JSONObject(builder.toString());
			return retJsonObject;

		} catch (Exception ex)
		{
			LogUtil.d(TAG, ">>>>>> error: " + ex.getMessage());
			ex.printStackTrace();
			return null;
		}
	}

	public static JSONObject sendJSONToProductServer(String paramString, JSONObject jsonObject, int timeout)
	{
		try
		{
			String url = Constants.URL + paramString;

			/*SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
			Date curDate = new Date(System.currentTimeMillis());
			String date = formatter.format(curDate);
			String sigSource = "platformName=android&date=" + date + "&appkey=2341C87D8798A9808B9808FDE31066A1";
			String sign = "";
			url = url + "&content-type=application/json&clientVersion=1.0&channelId=101"
					+ "&platformName=android&date=" + date + "&sign=" + sign.toUpperCase();
			LogUtil.d(TAG, "Url is " + url);*/

			URI uri = new URL(url).toURI();
			HttpPost httpPost = new HttpPost(uri);
			byte[] httpPostBytes = jsonObject.toString().getBytes();
			ByteArrayEntity byteArrayEntity = new ByteArrayEntity(httpPostBytes);
			LogUtil.d(TAG, "bytearray:" + new String(httpPostBytes));
			byteArrayEntity.setContentType("application/json");
			httpPost.setEntity(byteArrayEntity);
			DefaultHttpClient httpClient = new DefaultHttpClient();

			HttpParams httpParams = httpClient.getParams();
			HttpConnectionParams.setSocketBufferSize(httpParams, 8192);
			HttpConnectionParams.setConnectionTimeout(httpParams, timeout);
			HttpConnectionParams.setSoTimeout(httpParams, timeout);

			LogUtil.d(TAG, "execute Http locahost");
			HttpResponse localObject = httpClient.execute(httpPost);
			LogUtil.d(TAG, "execute Http locahost finished " + localObject.getStatusLine().getStatusCode());
			if (((HttpResponse) localObject).getStatusLine().getStatusCode() != 200)
			{
				int statusCode = ((HttpResponse) localObject).getStatusLine().getStatusCode();
				LogUtil.d(TAG, "error statusCode = " + statusCode);
				return null;
			}
			StringBuilder builder = new StringBuilder();
			InputStreamReader is = new InputStreamReader(localObject.getEntity().getContent());
			BufferedReader bufferedReader2 = new BufferedReader(is);

			for (String s = bufferedReader2.readLine(); s != null; s = bufferedReader2.readLine())
			{
				builder.append(s);
			}
			LogUtil.d(TAG, ">>>>>>" + builder.toString());
			JSONObject retJsonObject = new JSONObject(builder.toString());
			return retJsonObject;

		} catch (Exception ex)
		{
			LogUtil.d(TAG, ">>>>>> error: " + ex.getMessage());
			ex.printStackTrace();
			return null;
		}
	}

	public static JSONObject getProductServerJson(String paramString, int timeout)
	{
		try
		{
			String url = Constants.URL + paramString;
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
			Date curDate = new Date(System.currentTimeMillis());
			String date = formatter.format(curDate);
			String sigSource = "platformName=android&date=" + date + "&appkey=2341C87D8798A9808B9808FDE31066A1";
			String sign = ""/*MD5.getMD5(sigSource.getBytes())*/;
			url = url + "&content-type=application/json&clientVersion=1.0&channelId=101"
					+ "&platformName=android&date=" + date + "&sign=" + sign.toUpperCase();
			LogUtil.d(TAG, "Url is " + url);

			URI uri = new URL(url).toURI();
			HttpGet httpGet = new HttpGet(uri);
			DefaultHttpClient httpClient = new DefaultHttpClient();

			HttpParams httpParams = httpClient.getParams();
			HttpConnectionParams.setSocketBufferSize(httpParams, 8192);
			HttpConnectionParams.setConnectionTimeout(httpParams, timeout);
			HttpConnectionParams.setSoTimeout(httpParams, timeout);

			LogUtil.d(TAG, "execute Http locahost");
			HttpResponse localObject = httpClient.execute(httpGet);
			LogUtil.d(TAG, "execute Http locahost finished " + localObject.getStatusLine().getStatusCode());
			if (((HttpResponse) localObject).getStatusLine().getStatusCode() != 200)
			{
				int statusCode = ((HttpResponse) localObject).getStatusLine().getStatusCode();
				LogUtil.d(TAG, "error statusCode = " + statusCode);
				return null;
			}
			StringBuilder builder = new StringBuilder();
			InputStreamReader is = new InputStreamReader(localObject.getEntity().getContent());
			BufferedReader bufferedReader2 = new BufferedReader(is);

			for (String s = bufferedReader2.readLine(); s != null; s = bufferedReader2.readLine())
			{
				builder.append(s);
			}
			LogUtil.d(TAG, ">>>>>>" + builder.toString());
			JSONObject retJsonObject = new JSONObject(builder.toString());
			return retJsonObject;

		} catch (Exception ex)
		{
			LogUtil.d(TAG, ">>>>>> error: " + ex.getMessage());
			ex.printStackTrace();
			return null;
		}
	}
	
	
	@SuppressWarnings("deprecation")
	public static JSONObject uploadFile(String httpaction , long fleaid,int propertyid,
			long userid,String header,String description,
			String deletelist, File f,int timeout){
		 String url = Constants.URL + httpaction;
		 JSONObject retJsonObject=null;
		HttpClient client = new HttpClient();
		MultipartPostMethod method = new MultipartPostMethod(url);
		try {
			Log.v("MyTag",f.getName());
			//method.addParameter("avatarFile", f.getName(),f);
			method.addParameter("fleaid",fleaid+"");
			method.addParameter("propertyid",propertyid+"");
			method.addParameter("userid",userid+"");
			method.addParameter("header",URLEncoder.encode(header, "UTF-8"));
			method.addParameter("description",URLEncoder.encode(description, "UTF-8"));
			method.addParameter("deletelist",deletelist);
			FilePart part1 = new FilePart("pictures",f);
			method.addPart(part1);
			client.setConnectionTimeout(timeout);
			client.setTimeout(timeout);
			client.setHttpConnectionFactoryTimeout(timeout);
			client.executeMethod(method);
            if (method.getStatusCode() == HttpStatus.SC_OK) {
            	 StringBuilder builder = new StringBuilder();
     			InputStreamReader is = new InputStreamReader(method.getResponseBodyAsStream());
     			BufferedReader bufferedReader2 = new BufferedReader(is);

     			for (String s = bufferedReader2.readLine(); s != null; s = bufferedReader2.readLine())
     			{
     				builder.append(s);
     			}
     			 LogUtil.d("MyTag", "builder.toString() " + builder.toString());
     			 retJsonObject = new JSONObject(builder.toString());
            }
		} catch (Exception e) {
			LogUtil.d("MyTag", ">>>>>> error: " + e.getMessage());
			e.printStackTrace();
			return null;
		}finally{
			method.releaseConnection();
		}
		return retJsonObject;
	}
}
