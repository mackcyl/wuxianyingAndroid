package com.wuxianyingke.property.activities;

import java.io.File;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.LinearLayout;

import com.mantoto.property.R;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.common.LogUtil;
import com.wuxianyingke.property.common.Util;
import com.wuxianyingke.property.push.AlarmService;
import com.wuxianyingke.property.remote.HttpComm;
import com.wuxianyingke.property.remote.RemoteApiImpl;

public class SplashActivity extends Activity {
	private final static String FIRST_CONFIG_MESSAGE = "first_config_message";
	private final static String CHANNEL_ID = "channel_id";
	public final static int MSG_GOTO_MAIN = 1;
	private boolean is_exit = false;

	private LinearLayout mainLl;

	final Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_GOTO_MAIN:
				if (!is_exit) {
					SharedPreferences saving = SplashActivity.this
							.getSharedPreferences(LocalStore.USER_INFO, 0);
					// 验证码是否为空
					if (saving.getLong(LocalStore.PROPERTY_ID, 0) != 0) {
						// 用户id是否为空
						if (saving.getLong(LocalStore.USER_ID, 0) != 0) {
							// 用户是否选中自动登录
							if (LocalStore.getUserStatus(SplashActivity.this)) {
								Intent intent = new Intent();
								intent.setClass(SplashActivity.this,
										Radio1Activity.class);
								startActivity(intent);
								finish();
							} else {
								Intent intent = new Intent();
								intent.setClass(SplashActivity.this,
										LoginActivity.class);
								startActivity(intent);
								finish();
							}
						} else {
							Intent intent = new Intent();
							intent.setClass(SplashActivity.this,
									LoginActivity.class);
							startActivity(intent);
							finish();
						}

					} else {
						Intent intent = new Intent();
						intent.setClass(SplashActivity.this,
								InvitationCodeActivity.class);
						startActivity(intent);
						finish();
					}
				}
				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		//remote();
		mainLl = (LinearLayout) findViewById(R.id.main_ll);
		File file = new File(Constants.LOADING_PIC_PATH
				+ Constants.LOADING_PIC_FILENAME);
		if (file.exists()) {
			Bitmap bitmap = BitmapFactory.decodeFile(Constants.LOADING_PIC_PATH
					+ Constants.LOADING_PIC_FILENAME);
			BitmapDrawable bd = new BitmapDrawable(bitmap);
			mainLl.setBackgroundDrawable(bd);
		}

		LogUtil.d("TAG", "SplashActivity.onCreate---------------------");

		if (!LocalStore.getUserStatus(this)) {
			LocalStore.logout(this);
		}
		if (Util.getNetworkState(this) != 0) {
			Thread pioneerThread = new Thread() {
				public void run() {
					try {
						RemoteApiImpl rai = new RemoteApiImpl();

					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			};
			pioneerThread.start();
		}

		Thread t = new Thread() {
			@SuppressWarnings("finally")
			public void run() {
				try {
					Thread.sleep(2000);

					Message msg = new Message();
					msg.what = MSG_GOTO_MAIN;
					handler.sendMessage(msg);
				} catch (Exception ex) {
					LogUtil.d("MyTag", "Splash error:" + ex.getMessage());
				}
			}
		};
		t.start();

		Intent intent11 = new Intent(this, AlarmService.class);
		intent11.putExtra("action", "manually");
		startService(intent11);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			is_exit = true;
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	
	public void  remote ()
	{
	    Thread remoteThread = new Thread() {
            public void run() {
                try {
                    JSONObject lastest = new JSONObject();
                    String url="http://221.0.78.196:8080/propertymanagement/tmp/apklist.json";
                    JSONObject response = HttpComm.sendJSONToServer(url,
                            lastest, Constants.HTTP_SO_TIMEOUT);
                   Log.d("MyTag","remote"+response.toString());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        remoteThread.start();
	  
	}
}