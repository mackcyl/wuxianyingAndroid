package com.wuxianyingke.property.activities;

import java.util.ArrayList;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.mantoto.property.R;
import com.wuxianyingke.property.adapter.bianqianAdapter;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.common.LocalStore.bianqian;
import com.wuxianyingke.property.remote.RemoteApi.WifiInfo;
import com.wuxianyingke.property.remote.RemoteApiImpl;
import com.wuxianyingke.property.remote.RemoteApi.NetInfo;

public class WifiPasswordActivity extends Activity {
	 private TextView topbar_txt,topbar_right,wifipass,wifidescription;
	 private Button topbar_left;
	 private Button mSaveButton;
	 private String errorInfo="";

	 private String description="";
	 private JSONObject response = null;
		private ProgressDialog mWaitLoading = null;
	 
		private Handler mHandler = new Handler()
		{
			public void handleMessage(Message msg)
			{
				switch (msg.what)
				{
					default:
						if(wifipass!=null)
							wifipass.setText(errorInfo);

						if(wifidescription!=null)
							wifidescription.setText(description);
						if (mWaitLoading != null)
						{
							mWaitLoading.dismiss();
							mWaitLoading = null;
							
						}
				}
			}
		};
		
		@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wifipassword);
		initWidgets();
		getWifiPassword(this);
	}
	
  private void initWidgets()
  {
	  wifipass = (TextView) findViewById(R.id.wifipass);
	  wifidescription = (TextView) findViewById(R.id.wifidescription);
	  topbar_txt = (TextView) findViewById(R.id.topbar_txt);
      topbar_left = (Button) findViewById(R.id.topbar_left);
      topbar_txt.setText("免费WiFi");
      topbar_left.setVisibility(View.VISIBLE);
      String title=LocalStore.getBianqianTitle(this);
	  String content=LocalStore.getBianqianContent(this);
      topbar_left.setOnClickListener(new OnClickListener() {

          @Override
          public void onClick(View v) {
              // TODO Auto-generated method stub
              finish();
          }
      });

      topbar_right = (TextView) findViewById(R.id.topbar_right);
      topbar_right.setVisibility(View.GONE);
     
		
  }
  
	private void getWifiPassword(final Context activity) {
		mWaitLoading = ProgressDialog.show(activity, null, "获取中，请稍候......",
				true);
		Thread registerThread = new Thread() {
			public void run() {
				RemoteApiImpl remote = new RemoteApiImpl();
				WifiInfo netInfo = remote.sendGetWifi(WifiPasswordActivity.this);

				Message msg = new Message();
				if (netInfo == null) {
					msg.what = 4;
					errorInfo = "网络连接失败";
					description="";
				} else if (200 == netInfo.code) {
					msg.what = 3;
					errorInfo=netInfo.desc;
					description=netInfo.desc1;
				} else {
					msg.what = 2;
					errorInfo = netInfo.desc;
					description="";
				}
				mHandler.sendMessage(msg);
			}
		};
		registerThread.start();
	}
}
