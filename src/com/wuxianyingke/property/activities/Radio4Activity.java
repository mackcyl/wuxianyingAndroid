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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mantoto.property.R;
import com.wuxianyingke.property.adapter.PropertyCollectionAdapter;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.common.LogUtil;
import com.wuxianyingke.property.common.Util;
import com.wuxianyingke.property.remote.RemoteApi.ExpressService;
import com.wuxianyingke.property.remote.RemoteApi.NetInfo;
import com.wuxianyingke.property.remote.RemoteApiImpl;
import com.wuxianyingke.property.threads.PropertyCollectionThread;

public class Radio4Activity extends Activity {

	private ProgressDialog mProgressDialog = null;
	private PropertyCollectionThread mLogsThread = null;
	private ListView mLogsListView = null;
	private EditText mPropertyCollectionEditText;
	private TextView mIsVisitorTextView;
	private PropertyCollectionAdapter mLogAdapter = null;
	private int propertyid;
	private ProgressDialog mProgressBar = null;
	private String mToActivity = null;
	private String mErrorInfo = "";
	private String desc = "";
	private String getCode = "";
    private TextView topbar_txt,topbar_right;
    private Button topbar_left;
	public Handler mHandlersend = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (mProgressBar != null) {
				mProgressBar.dismiss();
				mProgressBar = null;
			}
			switch (msg.what) {
			// 登录失败
			case 0:
				Toast.makeText(Radio4Activity.this, mErrorInfo,
						Toast.LENGTH_SHORT).show();
				break;

			// 登陆成功
			case 1:
				Toast.makeText(Radio4Activity.this, "发送成功", Toast.LENGTH_SHORT)
						.show();
				mPropertyCollectionEditText.setText("");
				mPageNum = 1;
				initResource();
				break;

			// 通讯错误
			case 4:
				Toast.makeText(Radio4Activity.this, "通讯错误，请检查网络或稍后再试。",
						Toast.LENGTH_SHORT).show();
				break;
			case 8:
				Toast.makeText(Radio4Activity.this, desc, Toast.LENGTH_SHORT)
						.show();
				break;
			case 9:
				Toast.makeText(Radio4Activity.this, "网络超时，请重新获取",
						Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constants.MSG_NETWORK_ERROR:
				if (mProgressDialog != null) {
					mProgressDialog.dismiss();
					mProgressDialog = null;
				}
				if (mPageNum == 1) {
					View view = findViewById(R.id.view_network_error);
					view.setVisibility(View.VISIBLE);
				} else {
					mPageNum--;
					mAllowGetLogAgain = true;
				}
				break;

			case Constants.MSG_PROPERTY_COLLECTION_FINISH:
				showLogsListView(mLogsThread.getActivitys());
				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_radio4);
		SharedPreferences saving = this.getSharedPreferences(
				LocalStore.USER_INFO, 0);
		propertyid = (int) saving.getLong(LocalStore.PROPERTY_ID, 0);
		topbar_txt = (TextView) findViewById(R.id.topbar_txt);
		topbar_txt.setText(getResources().getText(R.string.radio_main4));
		 topbar_left = (Button) findViewById(R.id.topbar_left);
		 topbar_right = (TextView) findViewById(R.id.topbar_right);
		 topbar_right.setText("提交");
		 topbar_left.setVisibility(View.VISIBLE);
		 topbar_right.setVisibility(View.VISIBLE);
	        topbar_left.setOnClickListener(new OnClickListener() {

	            @Override
	            public void onClick(View v) {
	                // TODO Auto-generated method stub
	                finish();
	            }
	        });
		mPropertyCollectionEditText = (EditText) findViewById(R.id.PropertyCollectionEditText);
		mIsVisitorTextView = (TextView) findViewById(R.id.IsVisitorTextView);
	
		mLogsListView = (ListView) findViewById(R.id.PropertyCollectionListView);
		// 设置不显示滚动条
		mLogsListView.setVerticalScrollBarEnabled(false);
		mLogsListView.addFooterView(showLayout());
		mLogsListView.setOnScrollListener(mScrollListner);
		initResource();
		topbar_right.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(LocalStore.getIsVisitor(getApplicationContext()))
				{
					Toast.makeText(getApplicationContext(), "游客或者未认证用户无法完成此操作",
							Toast.LENGTH_SHORT).show();
					return;
				}
				sendPropertyCollection();
			}
		});
		if(LocalStore.getIsVisitor(this))
		{
			topbar_right.setClickable(false);
			mIsVisitorTextView.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void onRestart() {
		if (mLogsListView != null)
			mLogsListView.invalidateViews();
		super.onRestart();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		Boolean needInit = intent.getBooleanExtra("FromGroup", false);
		if (needInit) {
			// 重新获取资源
			startProgressDialog();
			endChildrenThreads();
			mAllowGetLogAgain = false;
			searchLayout.setVisibility(View.VISIBLE);
			mLogsThread = new PropertyCollectionThread(Radio4Activity.this,
					mHandler, propertyid, LocalStore.getUserInfo().userId,mPageNum);
			mLogsThread.start();
		}
		super.onNewIntent(intent);
	}

	@Override
	protected void onDestroy() {
		endChildrenThreads();
		super.onDestroy();
	}

	private void startProgressDialog() {
		View view = findViewById(R.id.view_network_error);
		view.setVisibility(View.GONE);
		mLogsListView.setVisibility(View.GONE);
		if (mProgressDialog != null)
			mProgressDialog.dismiss();
		mProgressDialog = new ProgressDialog(Radio4Activity.this);
		mProgressDialog.setMessage("加载中，请稍候...");
		mProgressDialog.setCancelable(true);
		mProgressDialog.show();
	}

	private void endChildrenThreads() {
		if (mLogsThread != null) {
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
	public void showLogsListView(List<ExpressService> list) {
		if (list == null) {
			if (mProgressDialog != null) {
				mProgressDialog.dismiss();
				mProgressDialog = null;
				searchLayout.setVisibility(View.GONE);
			}
			return;
		}
		if (0==list.size()) {
			if (mProgressDialog != null) {
				mProgressDialog.dismiss();
				mProgressDialog = null;
				searchLayout.setVisibility(View.GONE);
			}
			return;
		}
		mLogsListView.setVisibility(View.VISIBLE);
		if (mPageNum == 1) {
			searchLayout.setVisibility(View.GONE);
			mLogAdapter = new PropertyCollectionAdapter(this, list);
			if (mProgressDialog != null) {
				mProgressDialog.dismiss();
				mProgressDialog = null;
			}
			mLogsListView.setAdapter(mLogAdapter);
		} else {
			mLogAdapter.appandAdapter(list);
			mLogAdapter.notifyDataSetChanged();
			mItemSum = mLogAdapter.getCount();
			int test12 = mItemSum % 10;
			if (test12 == 0)
				mLogsListView.setSelection(mItemSum - 10);
			else
				mLogsListView.setSelection(mItemSum - test12);
		}
		setAllowGetPageAgain();
	}

	// 以下为分页显示代码
	private static LinearLayout searchLayout = null;

	public LinearLayout showLayout() {
		searchLayout = new LinearLayout(this);
		searchLayout.setOrientation(LinearLayout.HORIZONTAL);

		ProgressBar progressBar = new ProgressBar(this);
		progressBar.setPadding(0, 0, 15, 0);
		progressBar.setIndeterminateDrawable(getResources().getDrawable(
				R.drawable.progress_medium));
		searchLayout.addView(progressBar, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));

		TextView textView = new TextView(this);
		textView.setText("加载中...");
		textView.setGravity(Gravity.CENTER_VERTICAL);
		searchLayout.addView(textView, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT));
		searchLayout.setGravity(Gravity.CENTER);

		LinearLayout loadingLayout = new LinearLayout(this);
		loadingLayout.addView(searchLayout, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		loadingLayout.setGravity(Gravity.CENTER);
		return loadingLayout;
	}

	private static boolean mAllowGetLogAgain = false;
	private static int mItemSum = 0;
	private int mPageNum = 1;
	private final int mPagecount = 10;

	public static void setAllowGetPageAgain() {
		int test12 = mItemSum % 10;
		if ((test12 != 0) || (mItemSum >= 96)) {
			LogUtil.d("MyTag","Radio4Activity test12="+test12+"/mItemSum/"+mItemSum);
			mAllowGetLogAgain = false;
			searchLayout.setVisibility(View.GONE);
		} else
		{
	   		mAllowGetLogAgain = true;
	   		LogUtil.d("MyTag","Radio4Activity mAllowGetLogAgain = truetest12="+test12+"/mItemSum/"+mItemSum);
		}
	}

	private OnScrollListener mScrollListner = new OnScrollListener() {
		private int lastItem = 0;

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			lastItem = firstVisibleItem + visibleItemCount - 1;

		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			if (mLogAdapter != null && lastItem >= (mLogAdapter.getCount() - 2)) {
				if (!mAllowGetLogAgain)
					return;
				mAllowGetLogAgain = false;
				mPageNum++;
				mLogsThread = new PropertyCollectionThread(
						Radio4Activity.this, mHandler, propertyid,LocalStore.getUserInfo().userId, mPageNum);
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
		freeResource() ;
		if (mLogsThread == null) 
		startProgressDialog();
		mLogsThread = new PropertyCollectionThread(Radio4Activity.this,
				mHandler, propertyid,LocalStore.getUserInfo().userId, mPageNum);
		mLogsThread.start();
	}

	// 物业代收提交内容
	private void sendPropertyCollection() {
		if (Util.isEmpty(mPropertyCollectionEditText)) {
			Toast.makeText(Radio4Activity.this,
					R.string.error_please_input_collection, Toast.LENGTH_SHORT)
					.show();
			return;
		}
		mProgressBar = ProgressDialog.show(Radio4Activity.this, null,
				"发送中，请稍候...", true);
		Thread sendPropertyCollectionThread = new Thread() {
			public void run() {
				RemoteApiImpl remote = new RemoteApiImpl();
				SharedPreferences saving = Radio4Activity.this
						.getSharedPreferences(LocalStore.USER_INFO, 0);
				NetInfo netInfo = remote.sendPropertyCollection(
						Radio4Activity.this, LocalStore.getUserInfo().userId,
						saving.getLong(LocalStore.PROPERTY_ID, 0),
						mPropertyCollectionEditText.getText().toString());
				Message msg = new Message();
				if (netInfo == null) {
					msg.what = 4;
				} else if (200 == netInfo.code) {
					msg.what = 1;
				} else {
					msg.what = 0;
					mErrorInfo = netInfo.desc;
				}

				mHandlersend.sendMessage(msg);
			}
		};
		sendPropertyCollectionThread.start();
	}

}
