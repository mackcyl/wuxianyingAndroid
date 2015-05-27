package com.wuxianyingke.property.push;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

import com.mantoto.property.R;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.common.LogUtil;
import com.wuxianyingke.property.remote.RemoteApi.PushMessageRetInfo;
import com.wuxianyingke.property.remote.RemoteApiImpl;

public class AlarmService extends Service {

	private NotificationManager myNotiManager; // 状态栏通知

	private Handler handler = new Handler();
    private WakeLock  wakeLock=null;
	private Runnable runnable = new Runnable() {
		public void run() {
			handler.postDelayed(this, 1000 * 60 * 5);
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(System.currentTimeMillis());
			LogUtil.d("TAG",
					"TimerTask.run()---------------------"
							+ calendar.get(Calendar.HOUR_OF_DAY));
			writeLog("TimerTask.run() hour " + + calendar.get(Calendar.HOUR_OF_DAY) + "  miniute " + calendar.get(Calendar.MINUTE) +"\n") ;
			
			/*ContentResolver cv = AlarmService.this.getContentResolver();
			String strTimeFormat = android.provider.Settings.System.getString(cv,
					android.provider.Settings.System.TIME_12_24);*/
			
			
				if (LocalStore.getPushMessge(AlarmService.this)) {
					RemoteApiImpl rai = new RemoteApiImpl();
					PushMessageRetInfo retInfo = rai.getPushMessage(
							AlarmService.this, LocalStore.getUserInfo().userId,
							LocalStore.getPushMsgId(AlarmService.this));
					//writeLog("server ret code = " + retInfo.netInfo.code + "ret des = " + retInfo.netInfo.desc + "\n") ;
					if (retInfo != null) {
						if (retInfo.netInfo.code == 201) {
							// 无数据
							return;
						}
						LogUtil.d("TAG", "retInfo.size()" + retInfo.pushList.size());
						createNotify(retInfo);
						
						if (retInfo.pushList != null)
							LocalStore.setPushMsgId(AlarmService.this,
									retInfo.pushList.get(0).pushMessageId);
					}
				}
			}

			// postDelayed(this,1000)方法安排一个Runnable对象到主线程队列中
	};

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	private void writeLog(String log){
		File file = new File("/sdcard/llog.txt") ;
		if(!file.exists()){
			try {
				file.createNewFile() ;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		FileOutputStream fos = null ;
		try {
			fos = new FileOutputStream(file , true) ;
			fos.write(log.getBytes()) ;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fos.close() ;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();

		LogUtil.d("TAG", "AlarmService.onCreate---------------------");
		 PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE); 
         wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "AlarmService"); 
         wakeLock.acquire(); 

		myNotiManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		// sendBroadcast(new Intent(Constants.ALARM_INTENT_FILTER17)) ;

		// startTimer() ;
		handler.postDelayed(runnable, 1000 * 60 * 1); // 开始Timer
//		handler.removeCallbacks(runnable); // 停止Timer
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		LogUtil.d("TAG", "AlarmService.onStart---------------------");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		startService(new Intent(getApplicationContext(), AlarmService.class));
		LogUtil.d("TAG", "AlarmService.onDestroy---------------------");
	}

	private void createNotify(PushMessageRetInfo retInfo) {
		/*
		 * 创建新的Intent，作为单击Notification留言条时， 会运行的Activity
		 */
		LogUtil.d("TAG", "createNotify---------------------");
		Intent notifyIntent = new Intent(AlarmService.this, PushActivity.class);
		notifyIntent.putExtra("push_msg", retInfo);
		notifyIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		/* 创建PendingIntent作为设置递延运行的Activity */
		PendingIntent appIntent = PendingIntent.getActivity(AlarmService.this,
				0, notifyIntent, 0);

		/* 创建Notication，并设置相关参数 */
		Notification myNoti = new Notification();
		/* 设置statusbar显示的icon */
		myNoti.icon = R.drawable.icon;
		/* 设置statusbar显示的文字信息 */
		// myNoti.tickerText= new_msg ;
		myNoti.flags = Notification.FLAG_AUTO_CANCEL;
		/* 设置notification发生时同时发出默认声音 */
		myNoti.defaults = Notification.DEFAULT_SOUND;
		myNoti.setLatestEventInfo(AlarmService.this, "漫途社区", "漫途社区最新通知",
				appIntent);
		/* 送出Notification */
		Random random = new Random(new Date().getTime());
		myNotiManager.notify(random.nextInt(100000), myNoti);
	}

}
