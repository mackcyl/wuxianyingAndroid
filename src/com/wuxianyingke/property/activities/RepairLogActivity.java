package com.wuxianyingke.property.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import com.mantoto.property.R;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.common.LogUtil;
import com.wuxianyingke.property.remote.RemoteApi.RepairLog;
import com.wuxianyingke.property.remote.RemoteApiImpl;
import com.wuxianyingke.property.threads.RepairLogThread;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class RepairLogActivity extends Activity {

	private ProgressDialog mWaitLoading = null;
	private ProgressDialog mProgressDialog = null;
    private TextView topbar_txt;
    private Button topbar_left;
    private TextView repair_status_desc;
    private String mRepairLogTitle,mRepairLogStatusDesc ,mRepairLogStatusName;
    private LinearLayout ScrollViewLinearLayout;
    private EditText input_message;
    private Button input_message_send;
	private long userid = 0;
	private int propertyid;
	private long rootid;
	private String errorInfo="";
	private String msgbody="";
	private int msgType = 0,repairLogStatusId=0;
	
	private RepairLogThread mThread = null;
	
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
					
//						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//						if(input_message!=null)
//							input_message.setText("");
//						addMessageMine(msgbody, sdf.format(new Date()));
//						if (mWaitLoading != null)
//						{
//							mWaitLoading.dismiss();
//							mWaitLoading = null;
//
//						}
				}
					break;
				case 4:
					{
//						Toast.makeText(getApplicationContext(), "发送错误："+errorInfo,
//							     Toast.LENGTH_SHORT).show();
//							if (mWaitLoading != null)
//							{
//								mWaitLoading.dismiss();
//								mWaitLoading = null;
//
//							}
					}
						break;
				case Constants.MSG_NETWORK_ERROR:
				{
					Toast.makeText(getApplicationContext(), "网络连接失败",
						     Toast.LENGTH_SHORT).show();
						if (mProgressDialog != null)
						{
							mProgressDialog.dismiss();
							mProgressDialog = null;

						}
				}
					break;

				case Constants.MSG_GET_REPAIR_DETAIL_FINSH:
				{
					if(mThread!=null)
					{
						showLogsListView(mThread.getActivitys());
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
		SharedPreferences saving = this.getSharedPreferences(LocalStore.USER_INFO, 0);
		userid = LocalStore.getUserInfo().userId;
		propertyid=(int) saving.getLong(LocalStore.PROPERTY_ID, 0);
        setContentView(R.layout.repair_log);
        Bundle bundle = getIntent().getExtras();

		mRepairLogTitle = bundle.getString("repairLogTitle");
		mRepairLogStatusDesc = bundle.getString("repairLogStatusDesc");
		mRepairLogStatusName = bundle.getString("repairLogStatusName");
//		repairLogStatusId = bundle.getInt("repairLogStatusId");
        rootid =(long) bundle.getLong("repairId");
        initWidgets();


        mThread = new RepairLogThread(RepairLogActivity.this, mHandler, propertyid, userid, rootid);
        mThread.start();
        startProgressDialog();
    }
    
	private void startProgressDialog()
	{
		if (mProgressDialog != null)
			mProgressDialog.dismiss();
		mProgressDialog = new ProgressDialog(RepairLogActivity.this);
		mProgressDialog.setMessage("加载中，请稍候...");
		mProgressDialog.setCancelable(true);
		mProgressDialog.show();
	}
	
    private void initWidgets() {

		repair_status_desc = (TextView) findViewById(R.id.repair_status_desc);
    	ScrollViewLinearLayout = (LinearLayout)findViewById(R.id.ScrollViewLinearLayout);
        topbar_txt = (TextView) findViewById(R.id.topbar_txt);

        topbar_left = (Button) findViewById(R.id.topbar_left);
        topbar_txt.setText(mRepairLogTitle+" - 进度详情");
        topbar_left.setVisibility(View.VISIBLE);
        topbar_left.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		repair_status_desc.setText(mRepairLogStatusDesc);

		/*
        input_message = (EditText)findViewById(R.id.input_message);
        input_message_send = (Button)findViewById(R.id.input_message_send);
        input_message_send.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(userid == 0||LocalStore.getIsVisitor(getApplicationContext()))
				{
					Toast.makeText(getApplicationContext(), "游客或者未认证用户无法完成此操作",
							Toast.LENGTH_SHORT).show();
					return;
				}
				if(msgType == 3)
				{
					Toast.makeText(getApplicationContext(), "物业群发信息，不可回复。",
						     Toast.LENGTH_SHORT).show();
				}

				msgbody=input_message.getText().toString();
				if(msgbody.equals(""))
				{
					Toast.makeText(getApplicationContext(), "请输入内容。",
						     Toast.LENGTH_SHORT).show();
				}
				else {
					sendInBoxMessage(RepairLogActivity.this,rootid,msgbody, msgbody);
				}
			}
		});

        if(msgType == 3)
        {
        	input_message.setEnabled(false);
        	input_message.setHint("物业群发消息，不可回复");
        	input_message_send.setEnabled(false);
        	LocalStore.setQunfaIsRead(RepairLogActivity.this,rootid);
        }
        */
    }

	private void sendInBoxMessage(final Context activity,
			final long messageId,final String mEmailMessageTitle, final String mEmailMessageBody) {

		/*
		mWaitLoading = ProgressDialog.show(activity, null, "发送中，请稍候......",
				true);
		Thread registerThread = new Thread() {
			public void run() {
				RemoteApiImpl remote = new RemoteApiImpl();
				NetInfo netInfo = remote.sendMessageReply(RepairLogActivity.this, LocalStore.getUserInfo().userId, propertyid,
						(int)messageId, mEmailMessageTitle, mEmailMessageBody);

				Message msg = new Message();
				if (netInfo == null) {
					msg.what = 4;
				} else if (200 == netInfo.code) {
					msg.what = 3;
				} else {
					msg.what = 2;
					errorInfo = netInfo.desc;
				}
				mHandler.sendMessage(msg);
			}
		};
		registerThread.start();
		*/
	}
	
    @Override
    protected void onStart() {
        super.onStart();
    }


	class RepairLogItem
	{
		TextView repair_log_content;
		TextView repair_log_time;
		TextView repair_theWorker;
	}
    private void addRepairLogMine(String content, String time) {
    	View v = LayoutInflater.from(this).inflate(R.layout.repair_log_content1, null);
    	RepairLogItem item = new RepairLogItem();
    	item.repair_log_content = (TextView) v.findViewById(R.id.repair_log_content);
    	item.repair_log_time = (TextView) v.findViewById(R.id.repair_log_time);
    	item.repair_log_content.setText(content);
    	item.repair_log_time.setText(time);
    	ScrollViewLinearLayout.addView(v);
    }
    
    private void addRepairLogTheirs(String content, String time,String theWorker) {
    	View v = LayoutInflater.from(this).inflate(R.layout.repair_log_content2, null);
		RepairLogItem item = new RepairLogItem();
    	item.repair_log_content = (TextView) v.findViewById(R.id.repair_log_content);
    	item.repair_log_time = (TextView) v.findViewById(R.id.repair_log_time);
    	item.repair_theWorker = (TextView) v.findViewById(R.id.repair_log_theWorker);
    	item.repair_log_content.setText(content);
    	item.repair_log_time.setText(time);
    	item.repair_theWorker.setText(theWorker);
    	ScrollViewLinearLayout.addView(v);
    }
    
    public void showLogsListView(List<RepairLog> list)
	{
		if (mProgressDialog != null)
		{
				mProgressDialog.dismiss();
				mProgressDialog = null;
				
		}
		
		for(int i = 0; i < list.size(); i++)
		{
			RepairLog info = list.get(i);

			LogUtil.d("displayContent",info.displayContent);
//			if(info.toUserId==userid||info.toUserId==-1)
//			{
			addRepairLogMine(info.displayContent, info.cTime);
//			}
//			else
//			{
//				addMessageMine(info.body, info.cTime);
//			}
		}
	}
}