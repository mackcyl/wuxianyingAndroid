package com.wuxianyingke.property.activities;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.drm.DrmStore.Action;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mantoto.property.R;
import com.wuxianyingke.property.adapter.PropertyMessageAdapter;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.common.LogUtil;
import com.wuxianyingke.property.remote.RemoteApiImpl;
import com.wuxianyingke.property.remote.RemoteApi.MessageInfo;
import com.wuxianyingke.property.remote.RemoteApi.NetInfo;
import com.wuxianyingke.property.threads.MessageInBoxContentThread;
import com.wuxianyingke.property.threads.MessageOutBoxThread;


public class MessageInfoActivity extends Activity {

	private ProgressDialog mWaitLoading = null;
	private ProgressDialog mProgressDialog = null;
    private TextView topbar_txt;
    private Button topbar_left;
    private TextView message_info_title;
    private String mProductMessageInfoTitle, 
            mProductMessageInfoContent;
    private LinearLayout ScrollViewLinearLayout;
    private EditText input_message;
    private Button input_message_send;
	private long userid = 0;
	private int propertyid;
	private int rootid;
	private String errorInfo="";
	private String msgbody="";
	private int msgType = 0;
	

	private MessageInBoxContentThread mThread = null;
	
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
					
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						if(input_message!=null)
							input_message.setText("");
						addMessageMine(msgbody, sdf.format(new Date()));
						if (mWaitLoading != null)
						{
							mWaitLoading.dismiss();
							mWaitLoading = null;
							
						}
				}
					break;
				case 4:
					{
						Toast.makeText(getApplicationContext(), "发送错误："+errorInfo,
							     Toast.LENGTH_SHORT).show();
							if (mWaitLoading != null)
							{
								mWaitLoading.dismiss();
								mWaitLoading = null;
								
							}
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

				case Constants.MSG_MESSAGE_IN_BOX_CONTENT_FINISH:
				{
					if(mThread!=null)
					{
						//mThread
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
        
		SharedPreferences saving = this.getSharedPreferences(
				LocalStore.USER_INFO, 0);

		userid = LocalStore.getUserInfo().userId;
		propertyid=(int) saving.getLong(LocalStore.PROPERTY_ID, 0);
        setContentView(R.layout.message_info);
        Bundle bundle = getIntent().getExtras();
        mProductMessageInfoTitle = bundle.getString("productMessageInfoTitle");
        mProductMessageInfoContent = bundle.getString("productMessageInfoContent");
        msgType = bundle.getInt("productMessageInfoType");
        

        rootid =(int) bundle.getLong("productMessageInfoRootID");

        initWidgets();

        mThread = new MessageInBoxContentThread(MessageInfoActivity.this, mHandler, propertyid, userid, rootid);
        mThread.start();
        startProgressDialog();
    }
    
	private void startProgressDialog()
	{
		if (mProgressDialog != null)
			mProgressDialog.dismiss();
		mProgressDialog = new ProgressDialog(MessageInfoActivity.this);
		mProgressDialog.setMessage("加载中，请稍候...");
		mProgressDialog.setCancelable(true);
		mProgressDialog.show();
	}
	
    private void initWidgets() {
    	message_info_title = (TextView) findViewById(R.id.message_info_title);
    	ScrollViewLinearLayout = (LinearLayout)findViewById(R.id.ScrollViewLinearLayout);
        topbar_txt = (TextView) findViewById(R.id.topbar_txt);

        topbar_left = (Button) findViewById(R.id.topbar_left);
        topbar_txt.setText("详细信息");
        topbar_left.setVisibility(View.VISIBLE);
        topbar_left.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
            }
        });
        message_info_title.setText(mProductMessageInfoTitle);

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
					sendInBoxMessage(MessageInfoActivity.this,rootid,msgbody, msgbody);
				}
			}
		});
        
        if(msgType == 3)
        {
        	input_message.setEnabled(false);
        	input_message.setHint("物业群发消息，不可回复");
        	input_message_send.setEnabled(false);
        	LocalStore.setQunfaIsRead(MessageInfoActivity.this,rootid);
        }
    }

	private void sendInBoxMessage(final Context activity,
			final long messageId,final String mEmailMessageTitle, final String mEmailMessageBody) {
		mWaitLoading = ProgressDialog.show(activity, null, "发送中，请稍候......",
				true);
		Thread registerThread = new Thread() {
			public void run() {
				RemoteApiImpl remote = new RemoteApiImpl();
				NetInfo netInfo = remote.sendMessageReply(MessageInfoActivity.this, LocalStore.getUserInfo().userId, propertyid,
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
	}
	
    @Override
    protected void onStart() {
        super.onStart();
    }


	class MessageListItem
	{
		TextView message_item_content;
		TextView message_item_time;
	}
    private void addMessageMine(String content, String time) {
    	View v = LayoutInflater.from(this).inflate(R.layout.message_info_content1, null);
    	MessageListItem item = new MessageListItem();
    	item.message_item_content = (TextView) v.findViewById(R.id.message_item_content);
    	item.message_item_time = (TextView) v.findViewById(R.id.message_item_time);
    	item.message_item_content.setText(content);
    	item.message_item_time.setText("发送时间："+time);
    	ScrollViewLinearLayout.addView(v);
    }
    
    private void addMessagtheirs(String content, String time) {
    	View v = LayoutInflater.from(this).inflate(R.layout.message_info_content2, null);
    	MessageListItem item = new MessageListItem();
    	item.message_item_content = (TextView) v.findViewById(R.id.message_item_content);
    	item.message_item_time = (TextView) v.findViewById(R.id.message_item_time);
    	item.message_item_content.setText(content);
    	item.message_item_time.setText("接受时间："+time);
    	ScrollViewLinearLayout.addView(v);
    }
    
    public void showLogsListView(List<MessageInfo> list) 
	{
		if (mProgressDialog != null)
		{
				mProgressDialog.dismiss();
				mProgressDialog = null;
				
		}
		
		for(int i = 0; i < list.size(); i++)
		{
			MessageInfo info = list.get(i);
			if(info.toUserId==userid||info.toUserId==-1)
			{
				addMessagtheirs(info.body, info.cTime);
			}
			else
			{
				addMessageMine(info.body, info.cTime);
			}
		}
	}
}