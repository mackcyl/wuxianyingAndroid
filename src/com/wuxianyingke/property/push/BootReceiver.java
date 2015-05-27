package com.wuxianyingke.property.push;

import com.wuxianyingke.property.common.LogUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {
	private final String TAG = "TAG";
	private static final String ACTION = "android.intent.action.BOOT_COMPLETED";

	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(ACTION)) {
			LogUtil.d(TAG, "BootReceiver complete");

			Intent mTimekeepingService = new Intent(context, AlarmService.class); // 启动报时服务
			// mTimekeepingService.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mTimekeepingService.putExtra("action", "manually");
			context.startService(mTimekeepingService);
		}
	}
}
