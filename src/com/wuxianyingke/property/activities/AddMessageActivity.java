package com.wuxianyingke.property.activities;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mantoto.property.R;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.common.Util;
import com.wuxianyingke.property.remote.RemoteApiImpl;
import com.wuxianyingke.property.remote.RemoteApi.NetInfo;

public class AddMessageActivity extends Activity {
	
    private TextView topbar_txt,topbar_right;
    private Button topbar_left;
    private LocalStore localstore;
	private ProgressDialog mWaitLoading = null;
	private int propertyid;
	private int type;
	private String title;
	private String errorinfo;
    
	private EditText content;	
	
	private Handler mHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
				case 2:
				{

					Toast.makeText(getApplicationContext(), "网络连接失败",
						     Toast.LENGTH_SHORT).show();
						if (mWaitLoading != null)
						{
							mWaitLoading.dismiss();
							mWaitLoading = null;
							
						}
				}
					break;
				case 3:
				{
					Toast.makeText(getApplicationContext(), "提交成功",
						     Toast.LENGTH_SHORT).show();
						if (mWaitLoading != null)
						{
							mWaitLoading.dismiss();
							mWaitLoading = null;
							
						}
						if (mWaitLoading != null)
						{
							mWaitLoading.dismiss();
							mWaitLoading = null;
							
						}
						finish();
				}
					break;
				case 4:
					{
						Toast.makeText(getApplicationContext(), "发送错误："+errorinfo,
							     Toast.LENGTH_SHORT).show();
							if (mWaitLoading != null)
							{
								mWaitLoading.dismiss();
								mWaitLoading = null;
								
							}
					}
						break;

			}
			super.handleMessage(msg);
		}
	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.addbianqian);
		localstore = new LocalStore();
		initWidgets();
		setResult(0,getIntent());

        Bundle bundle = getIntent().getExtras();

        type = bundle.getInt("type");
        title = bundle.getString("name");
        topbar_txt.setText(title);
		SharedPreferences saving = this.getSharedPreferences(
				LocalStore.USER_INFO, 0);
		propertyid = (int)saving.getLong(LocalStore.PROPERTY_ID, 0);
	}
	
	private void initWidgets() {
		
		content = (EditText)findViewById(R.id.content);
		content.setHint("请输入信息内容");
		topbar_txt = (TextView) findViewById(R.id.topbar_txt);
		 topbar_left = (Button) findViewById(R.id.topbar_left);
		 topbar_left.setVisibility(View.VISIBLE);
		 topbar_left.setEnabled(true);
		 topbar_left.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		 topbar_right = (TextView) findViewById(R.id.topbar_right);
		 topbar_right.setText("提交");
		 topbar_right.setVisibility(View.VISIBLE);
		 topbar_right.setOnClickListener(new OnClickListener() {

	            @Override
	            public void onClick(View v) {
	                // TODO Auto-generated method stub
	            	//save()
	            	if(content.getText().toString().equals(""))
	            	{
	            		Toast.makeText(getApplicationContext(), "请输入内容。。。",
	            			     Toast.LENGTH_SHORT).show();
	            		return;
	            	}
	            	else
	            	{
	            		sendMessage(AddMessageActivity.this);
	            	}
	            }
	        });

		
	}
	private void sendMessage(final Context activity) {

		if (Util.isEmpty(content)) {
			Toast.makeText(activity, "内容不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		mWaitLoading = ProgressDialog.show(activity, null, "发送中，请稍候......",
				true);
		Thread registerThread = new Thread() {
			public void run() {
				RemoteApiImpl remote = new RemoteApiImpl();
				NetInfo netInfo = remote.sendMessage(activity,
						LocalStore.getUserInfo().userId, propertyid,
						type, title, content.getText().toString());

				Message msg = new Message();
				if (netInfo == null) {
					msg.what = 4;
				} else if (200 == netInfo.code) {
					msg.what = 3;
				} else {
					msg.what = 2;
					errorinfo = netInfo.desc;
				}
				mHandler.sendMessage(msg);
			}
		};
		registerThread.start();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

}