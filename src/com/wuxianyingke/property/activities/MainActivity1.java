package com.wuxianyingke.property.activities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.common.Update;
import com.wuxianyingke.property.remote.RemoteApi.Loading;
import com.wuxianyingke.property.remote.RemoteApiImpl;

public class MainActivity1 extends BaseMainActivity {
	int propertyid =0;
	private Handler mMainHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {

			}
			super.handleMessage(msg);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPreferences saving = this.getSharedPreferences(
				LocalStore.USER_INFO, 0);
	     propertyid = (int) saving.getLong(LocalStore.PROPERTY_ID, 0);
		new Thread() {
			public void run() {
				RemoteApiImpl rai = new RemoteApiImpl();
				Loading loading = rai
						.getLoadingInfo(LocalStore
								.getLoadingId(MainActivity1.this
										.getApplicationContext()),propertyid);
				File file = new File(Constants.LOADING_PIC_PATH
						+ Constants.LOADING_PIC_FILENAME);

				if (loading == null) {
					return;
				}
				if (loading.logoId == LocalStore
						.getLoadingId(MainActivity1.this) && file.exists())
					return;

				if (file.exists()) {
					file.delete();
				}

				if (loading != null) {
					if (loading.netInfo.code == 200) {
						LocalStore.setLoadingId(MainActivity1.this,
								loading.logoId);
						File path = new File(Constants.LOADING_PIC_PATH);
						if (!path.isDirectory()) {
							path.mkdir();
						}

						File tempFile = new File(Constants.LOADING_PIC_PATH
								+ Constants.LOADING_PIC_FILENAME + ".tmp");
						if (tempFile.exists()) {
							tempFile.delete();
						}

						try {
							FileOutputStream outStream = new FileOutputStream(
									tempFile);
							HttpURLConnection conn = (HttpURLConnection) new URL(
									loading.logoImgUrl)
									.openConnection();
							conn.setConnectTimeout(10 * 1000);
							conn.setRequestMethod("GET");
							int status = conn.getResponseCode();
							if (status == 200) {
								InputStream inStream = conn.getInputStream();
								byte[] buffer = new byte[1024];
								int len = 0;
								while ((len = inStream.read(buffer)) != -1) {
									outStream.write(buffer, 0, len);
								}
								outStream.close();
								inStream.close();
								tempFile.renameTo(file);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}.start();

		Thread updateThread = new Thread() {
			public void run() {
				try {
					Update.checkVersion(MainActivity1.this, MainActivity1.this,
							mMainHandler, Constants.PACKAGENAME,
							Constants.VERSION_CODE, "1", 0);

				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		};
		updateThread.start();
	}

	@Override
	void menuIndex1Event() {
		Toast.makeText(MainActivity1.this, "Menu index 1 clicked",
				Toast.LENGTH_SHORT).show();

	}

	@Override
	void menuIndex2Event() {
		Toast.makeText(MainActivity1.this, "Menu index 2 clicked",
				Toast.LENGTH_SHORT).show();

	}

	@Override
	void menuIndex3Event() {
		Toast.makeText(MainActivity1.this, "Menu index 3 clicked",
				Toast.LENGTH_SHORT).show();

	}

	@Override
	void menuIndex4Event() {
		super.exitApp(MainActivity1.this);
	}
}