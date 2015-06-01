package com.wuxianyingke.property.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.AbsListView.OnScrollListener;
import com.mantoto.property.R;
import com.wuxianyingke.property.adapter.PropertyMessageAdapter;
import com.wuxianyingke.property.adapter.RepairListAdapter;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.common.LogUtil;
import com.wuxianyingke.property.remote.RemoteApi;
import com.wuxianyingke.property.remote.RemoteApi.Repair;
import com.wuxianyingke.property.remote.RemoteApi.MessageTypeInfo;
import com.wuxianyingke.property.threads.MessageOutBoxThread;
import com.wuxianyingke.property.threads.MessageTypeThread;
import com.wuxianyingke.property.threads.RepairListThread;

import java.util.List;

/**
 * 报修进度
 * @author mackcyl
 *
 */
public class RepairListActivity extends Activity {

	private ProgressDialog mProgressDialog = null;
	private RepairListThread mLogsThread = null;

	private ListView mLogsListView = null;
	private static RepairListAdapter mLogAdapter = null;
	private int propertyid;
	private TextView topbar_txt,topbar_right,txt_btn3;
	private Button topbar_left;
	private long userid = 0;
	private LinearLayout add_message_linearlayout,message_btn_linearlayout;
	private RepairListThread mRepairListThread =null;
	private boolean typeinited = false;
	
	private Handler mHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
				case Constants.MSG_NETWORK_ERROR:
					if (mProgressDialog != null)
					{
						mProgressDialog.dismiss();
						mProgressDialog = null;
					}
					if(mPageNum == 1)
					{
						View view = findViewById(R.id.view_network_error);
						view.setVisibility(View.VISIBLE);
					}
					else
					{
						LogUtil.d("MyTag","MSG_NETWORK_ERROR");
						mPageNum--;
						mAllowGetLogAgain = false;
						searchLayout.setVisibility(View.GONE);
					}
					break;

				case Constants.MSG_GET_REPAIR_LIST_FINSH:
					if(mLogsThread!=null)
					showLogsListView(mLogsThread.getActivitys());
//					showMessageTypeView(mLogsThread.getActivitys());
					break;
					
//				case Constants.MSG_GET_REPAIR_LIST_FINSH:
//					if(mMessageTypeThread!=null)
//					showMessageTypeView(mMessageTypeThread.getMessageTypeList());
//					break;
					
			}
			super.handleMessage(msg);
		}
	};
	
	private void showMessageTypeView(List<MessageTypeInfo> list)
	{
		for(int i = 0 ; i<list.size();i++)
		{
			final MessageTypeInfo info = list.get(i);
	    	View v = LayoutInflater.from(this).inflate(R.layout.message_btn, null);
	    	TextView txt_btn = (TextView) v.findViewById(R.id.txt_btn);
	    	txt_btn.setText(info.messageTypeName);
	    	txt_btn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					newMessage(info.messageTypeID,info.messageTypeName);
				}
			});
	    	message_btn_linearlayout.addView(v);
			
		}
		typeinited = true;
	}
	
	private void newMessage(int type,String name) {

		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
				| Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.setClass(RepairListActivity.this, AddMessageActivity.class);
		intent.putExtra("type", type);
		intent.putExtra("name", name);
		startActivityForResult(intent, 0);
	}
  	protected  void onActivityResult(int requestCode, int resultCode, Intent data)  {
  		
		startProgressDialog();
		endChildrenThreads();
		mAllowGetLogAgain = false;
		searchLayout.setVisibility(View.VISIBLE);
		mLogsThread = new RepairListThread(RepairListActivity.this, mHandler,propertyid, userid, mPageNum);
		mLogsThread.start();
		
        super.onActivityResult(requestCode, resultCode,  data);
  	}
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		userid = LocalStore.getUserInfo().userId;
		setContentView(R.layout.repair_list);
		topbar_txt = (TextView) findViewById(R.id.topbar_txt);

		/*
		topbar_right = (TextView) findViewById(R.id.topbar_right);
		topbar_right.setText(R.string.message_left);
		topbar_right.setVisibility(View.VISIBLE);
		topbar_right.setClickable(true);
		topbar_right.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(userid == 0||LocalStore.getIsVisitor(getApplicationContext()))
				{
					Toast.makeText(getApplicationContext(), "游客或者未认证用户无法完成此操作",
							Toast.LENGTH_SHORT).show();
					return;
				}
				if(typeinited)
					add_message_linearlayout.setVisibility(View.VISIBLE);
				else
				{
					Toast.makeText(getApplicationContext(), "未获得消息类型列表，请稍后重试。",
					Toast.LENGTH_SHORT).show();
				}
			}
		});
		*/
		topbar_left = (Button) findViewById(R.id.topbar_left);
		topbar_txt.setText(R.string.gridview_progress);
	    topbar_left.setVisibility(View.VISIBLE);
	    topbar_left.setOnClickListener(new OnClickListener() {
	            @Override
	            public void onClick(View v) {
	                // TODO Auto-generated method stub
	            	finish();
	            }
	        });
		SharedPreferences saving = this.getSharedPreferences(LocalStore.USER_INFO, 0);
		propertyid=(int) saving.getLong(LocalStore.PROPERTY_ID, 0);
		mLogsListView = (ListView) findViewById(R.id.RepairListView);

		// 设置不显示滚动条
		mLogsListView.setVerticalScrollBarEnabled(false);
		mLogsListView.setDivider(getResources().getDrawable(R.drawable.list_line));
		mLogsListView.addFooterView(showLayout());
		mLogsListView.setOnScrollListener(mScrollListner);

		startProgressDialog();

		mLogsThread = new RepairListThread(RepairListActivity.this, mHandler,propertyid,userid, mPageNum);
		mLogsThread.start();

		txt_btn3 = (TextView) findViewById(R.id.txt_btn3);
		add_message_linearlayout = (LinearLayout)findViewById(R.id.add_message_linearlayout);
		message_btn_linearlayout = (LinearLayout)findViewById(R.id.message_btn_linearlayout);
		add_message_linearlayout.setVisibility(View.GONE);
		
		
		txt_btn3.setClickable(true);
		txt_btn3.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				add_message_linearlayout.setVisibility(View.GONE);
			}
		});

//		mMessageTypeThread = new MessageTypeThread(this, mHandler, propertyid);
//		mMessageTypeThread.start();
//
//		mRepairListThread = new RepairListThread(this, mHandler, propertyid);
//		mMessageTypeThread.start();


	}

	

	@Override
	protected void onRestart()
	{
		if(mLogsListView!=null)
			mLogsListView.invalidateViews();
		super.onRestart();
	}

	@Override
	protected void onNewIntent(Intent intent)
	{
		Boolean needInit = intent.getBooleanExtra("FromGroup", false);
		if(needInit)
		{
			//重新获取资源
			startProgressDialog();
			endChildrenThreads();
			mAllowGetLogAgain = false;
			searchLayout.setVisibility(View.VISIBLE);
			mLogsThread = new RepairListThread(RepairListActivity.this, mHandler,propertyid, userid, mPageNum);
			mLogsThread.start();
			LogUtil.d("MyTag", "Radio2Activity.this onNewIntent");
		}
		super.onNewIntent(intent);
	}
	
	@Override
	protected void onDestroy()
	{
		endChildrenThreads();
		super.onDestroy();
	}
	

	private void startProgressDialog()
	{
		View view = findViewById(R.id.view_network_error);
		view.setVisibility(View.GONE);
		mLogsListView.setVisibility(View.GONE);
		if (mProgressDialog != null)
			mProgressDialog.dismiss();
		mProgressDialog = new ProgressDialog(RepairListActivity.this);
		mProgressDialog.setMessage("加载中，请稍候...");
		mProgressDialog.setCancelable(true);
		mProgressDialog.show();
	}

	
	private void endChildrenThreads()
	{

		if (mLogsThread != null)
		{
			mLogsThread.stopRun();
			mLogsThread = null;
		}

		mLogsListView.setAdapter(null);
		
		mAllowGetLogAgain = true;
		mPageNum = 1;
		mItemSum = 0;
	}
	
	private void stopProgressDialog() {

		if (mProgressDialog != null) {
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
	}
	public void showLogsListView(List<Repair> list)
	{
		if (list == null)
		{
			if (mProgressDialog != null)
			{
				mProgressDialog.dismiss();
				mProgressDialog = null;
				
			}
			searchLayout.setVisibility(View.GONE);
			return;
		}
		mLogsListView.setVisibility(View.VISIBLE);
		if(mPageNum == 1)
		{
			 
			mLogAdapter = new RepairListAdapter(this, list);
			if (mProgressDialog != null)
			{
				mProgressDialog.dismiss();
				mProgressDialog = null;
			}
			mLogsListView.setAdapter(mLogAdapter);
		}
		else
		{
			mLogAdapter.appandAdapter(list);
			mLogAdapter.notifyDataSetChanged();
			LogUtil.d("MyTag","1-mItemSum = mLogAdapter.getCount()="+mLogAdapter.getCount());
			int test12 = mItemSum % 10;
			if(test12 == 0)
				mLogsListView.setSelection(mItemSum - 10);
			else
				mLogsListView.setSelection(mItemSum - test12);
		}
		setAllowGetPageAgain();
	}
	
	// 以下为分页显示代码
	private static LinearLayout searchLayout = null;
	public LinearLayout showLayout()
	{
		searchLayout = new LinearLayout(this);
		searchLayout.setOrientation(LinearLayout.HORIZONTAL);

		ProgressBar progressBar = new ProgressBar(this);
		progressBar.setPadding(0, 0, 15, 0);
		progressBar.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress_medium));
		searchLayout.addView(progressBar, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

		TextView textView = new TextView(this);
		textView.setText("加载中...");
		textView.setGravity(Gravity.CENTER_VERTICAL);
		searchLayout.addView(textView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
		searchLayout.setGravity(Gravity.CENTER);

		LinearLayout loadingLayout = new LinearLayout(this);
		loadingLayout.addView(searchLayout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		loadingLayout.setGravity(Gravity.CENTER);
		return loadingLayout;
	}
	
	private static boolean mAllowGetLogAgain = false;
	private static int mItemSum = 0;
	private int mPageNum = 1;
	private final int mPagecount = 10;
	public static void setAllowGetPageAgain()
	{
		LogUtil.d("MyTag", "mItemSummItemSum="+mItemSum);
		if(mLogAdapter!=null)
			mItemSum = mLogAdapter.getCount();
		int test12 = mItemSum % 10;
		if((test12 != 0) || (mItemSum >= 96))
		{
			LogUtil.d("MyTag", "mItemSummItemSum"+test12);
			mAllowGetLogAgain = false;
			searchLayout.setVisibility(View.GONE);
		}
		else
		{
			mAllowGetLogAgain = true;
			LogUtil.d("MyTag", "mItemSummItemSummAllowGetLogAgain = true"+test12);
		}
	}
	
	private OnScrollListener mScrollListner = new OnScrollListener()
	{
		private int lastItem = 0;
		
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
		{
			lastItem = firstVisibleItem + visibleItemCount - 1;
			
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState)
		{
			if (mLogAdapter != null && lastItem >= (mLogAdapter.getCount()-2))
			{
				LogUtil.d("MyTag", "Radio2Activity.this mAllowGetLogAgain="+mAllowGetLogAgain);
				if (!mAllowGetLogAgain)
					return;
				mAllowGetLogAgain = false;
				mPageNum++;
				LogUtil.d("MyTag", "Radio2Activity.this onScrollStateChanged");
				mLogsThread = new RepairListThread(RepairListActivity.this, mHandler,propertyid, userid, mPageNum);
				mLogsThread.start();
			}
		}
	};

	void freeResource() {
		// TODO Auto-generated method stub
		mLogsThread = null ;
		stopProgressDialog();
		endChildrenThreads();
	}

	void initResource() {
		// TODO Auto-generated method stub
		LogUtil.d("MyTag", "Radio2Activity.this initResource");
		freeResource() ;
		if (mLogsThread == null) 
		startProgressDialog();
		mLogsThread = new RepairListThread(RepairListActivity.this, mHandler,propertyid, userid, mPageNum);
		mLogsThread.start();
	}
}
