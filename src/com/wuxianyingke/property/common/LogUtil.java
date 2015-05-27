package com.wuxianyingke.property.common;


import android.util.Log;

public class LogUtil {
	
	public static int d(String tag, String msg) {
		if (Constants.DEBUG == true) {
			return Log.d(tag, msg);
		} else {
			return 0;
		}
	}
}
