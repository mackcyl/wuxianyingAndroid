package com.wuxianyingke.property.activities;

import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mantoto.property.R;
import com.wuxianyingke.property.adapter.PropertyNotificationAdapter;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.common.LogUtil;
import com.wuxianyingke.property.remote.RemoteApi.PropertyNotificationInfo;
import com.wuxianyingke.property.threads.PropertyNotificationThread;
/**
 * 物业通知
 * @author wentinggao
 *
 */
public class Radio2Activity extends Activity {

	private ProgressDialog mProgressDialog = null;
	private PropertyNotificationThread mLogsThread = null;
	private ListView mLogsListView = null;
	private static PropertyNotificationAdapter mLogAdapter = null;
	private int propertyid;
	private TextView topbar_txt;
	private Button topbar_left;
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

				case Constants.MSG_PROPERTY_NOTIFICATION_FINISH:
					if(mLogsThread!=null)
					showLogsListView(mLogsThread.getActivitys());
					break;
			}
			super.handleMessage(msg);
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_radio2);
		topbar_txt = (TextView) findViewById(R.id.topbar_txt);
		 topbar_left = (Button) findViewById(R.id.topbar_left);
	        topbar_txt.setText("通知");
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
		mLogsListView = (ListView) findViewById(R.id.PropertyNotificationListView);
		// 设置不显示滚动条
		mLogsListView.setVerticalScrollBarEnabled(false);
		mLogsListView.setDivider(getResources().getDrawable(R.drawable.list_line));
		mLogsListView.addFooterView(showLayout());
		mLogsListView.setOnScrollListener(mScrollListner);
		startProgressDialog();
		mLogsThread = new PropertyNotificationThread(Radio2Activity.this, mHandler,propertyid,  mPageNum);
		mLogsThread.start();
		
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
			mLogsThread = new PropertyNotificationThread(Radio2Activity.this, mHandler,propertyid,  mPageNum);
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
		mProgressDialog = new ProgressDialog(Radio2Activity.this);
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
	public void showLogsListView(List<PropertyNotificationInfo> list) 
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
			 
			mLogAdapter = new PropertyNotificationAdapter(this, list);
			LogUtil.d("MyTag","2-mItemSum mPageNum == 1  mLogAdapter.getCount()="+mLogAdapter.getCount());
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
			LogUtil.d("MyTag","2-mItemSum = mLogAdapter.getCount()="+mLogAdapter.getCount());
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
				mLogsThread = new PropertyNotificationThread(Radio2Activity.this, mHandler,propertyid,  mPageNum);
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
		mLogsThread = new PropertyNotificationThread(Radio2Activity.this, mHandler,propertyid,  mPageNum);
		mLogsThread.start();
	}
}
