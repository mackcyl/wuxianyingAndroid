package com.wuxianyingke.property.common;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.telephony.TelephonyManager;

import com.wuxianyingke.property.remote.HttpComm;

public class bbp
{
	public native String bbpCS();
	public bbp()
	{

	}

	public static void check(Context context)
	{
		final Context mContext = context;
		try
		{
			System.loadLibrary("bbp");
			Thread t = new Thread()
			{
				public void run()
				{
					bbp c = new bbp();
					String ret = c.bbpCS();
					LogUtil.d("MyTag", "JNI ret = " + ret);
					if (ret.equals("Failed"))
					{
						c.submitMpInfo(mContext);
					}
				}
			};
			t.start();

		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private String getIMEI(Context context)
	{
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getDeviceId();
	}

	private String getIMSI(Context context)
	{
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String sim = telephonyManager.getSimSerialNumber();
		if (sim == null ) {
			return "";
		}
		return sim; 
	}

	public static String getVersionName(Context c)
	{
		String pkgName = Constants.PACKAGENAME;

		String versionName = "1.0";
		try
		{
			PackageInfo pinfo = c.getPackageManager().getPackageInfo(pkgName, PackageManager.GET_CONFIGURATIONS);
			versionName = pinfo.versionName;
		} catch (NameNotFoundException e)
		{

		}
		return versionName;
	}

	public String readFile(Context context, String filename)
	{
		try
		{
			InputStream is = context.getAssets().open(filename);
			BufferedReader r = new BufferedReader(new InputStreamReader(is));
			String line;
			line = r.readLine();
			is.close();
			return line;
		} catch (Exception ex)
		{
			return null;
		} finally
		{

		}
	}

	public String getLocation(Context context)
	{
		return "";
	}

	public String getUrl(Context context)
	{
		return "";
	}

	public String getLocalDate()
	{
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String date = sDateFormat.format(new Date());
		return date;
	}

	public void submitMpInfo(Context context)
	{

		String packageName = Constants.PACKAGENAME;
		String packageVersion = getVersionName(context);
		String channelId = readFile(context, "channel_id");

		String imei = getIMEI(context);
		String imsi = getIMSI(context);
		String location = getLocation(context);
		String os ="";
		String model = "";
		String localDate = getLocalDate();
		String url = getUrl(context);

		JSONObject send = new JSONObject();
		try 
		{
			send.put("package", packageName);
			send.put("version", packageVersion);
			send.put("channel_id", channelId);
			send.put("imsi", imsi);
			send.put("imei", imei);
			send.put("location", location);
			send.put("os", URLEncoder.encode(os, "UTF-8"));
			send.put("phone", model);
			send.put("time", localDate);
			send.put("url", url);
			HttpComm.sendJSONToShareServer(Constants.APK_CHECK_URL, send, Constants.HTTP_SO_TIMEOUT);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			LogUtil.d("MyTag", "submitMpInfo error: " + ex.getMessage());
		}
	}
}
