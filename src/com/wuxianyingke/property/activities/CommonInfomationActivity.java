package com.wuxianyingke.property.activities;

import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.mantoto.property.R;
import com.wuxianyingke.property.adapter.InfomationsAdapter;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.remote.RemoteApi.InformationsInfo;
import com.wuxianyingke.property.threads.InfomationThread;

public class CommonInfomationActivity extends Activity {

	private ProgressDialog mProgressDialog = null;
	private InfomationThread mInfomationThread = null;
	private ListView mLogsListView = null;
	private InfomationsAdapter mLogAdapter = null;
	private int propertyid;
	public TextView topbar_txt;
	public Button topbar_left;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constants.MSG_NETWORK_ERROR:
				if (mProgressDialog != null) {
					mProgressDialog.dismiss();
					mProgressDialog = null;
				}
					View view = findViewById(R.id.view_network_error);
					view.setVisibility(View.VISIBLE);
				break;

			case Constants.MSG_GET_INFOMATION_FINISH:
				showLogsListView(mInfomationThread.getActivitys());
				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_infomation);
		SharedPreferences saving = this.getSharedPreferences(
				LocalStore.USER_INFO, 0);
		propertyid = (int) saving.getLong(LocalStore.PROPERTY_ID, 0);
		topbar_txt = (TextView) findViewById(R.id.topbar_txt);
		topbar_left = (Button) findViewById(R.id.topbar_left);
		topbar_txt.setText("常用信息");
		topbar_left.setVisibility(View.VISIBLE);
		topbar_left.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			finish();	
			}
		});
		mLogsListView = (ListView) findViewById(R.id.product_message_list);
		// 设置不显示滚动条
		mLogsListView.setVerticalScrollBarEnabled(false);
		mLogsListView.setDivider(getResources().getDrawable(
				R.drawable.list_line));
		initResource();
	}

	@Override
	protected void onRestart() {
		if (mLogsListView != null)
			mLogsListView.invalidateViews();
		super.onRestart();
	}

	protected void initResource() {
		// 获取资源
		startProgressDialog();
		endChildrenThreads();
		mInfomationThread = new InfomationThread(CommonInfomationActivity.this,
				mHandler, propertyid);
		mInfomationThread.start();
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
		mProgressDialog = new ProgressDialog(CommonInfomationActivity.this);
		mProgressDialog.setMessage("加载中，请稍候...");
		mProgressDialog.setCancelable(true);
		mProgressDialog.show();
	}

	private void endChildrenThreads() {
		if (mInfomationThread != null) {
			mInfomationThread.stopRun();
			mInfomationThread = null;
		}

		mLogsListView.setAdapter(null);

	}

	public void showLogsListView(List<InformationsInfo> list) {
		if (list == null) {
			if (mProgressDialog != null) {
				mProgressDialog.dismiss();
				mProgressDialog = null;
			}
			return;
		}
		mLogsListView.setVisibility(View.VISIBLE);
			mLogAdapter = new InfomationsAdapter(this, list);
			if (mProgressDialog != null) {
				mProgressDialog.dismiss();
				mProgressDialog = null;
			}
			mLogsListView.setAdapter(mLogAdapter);
	}



	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		finish();
		return super.onKeyDown(keyCode, event);
	}

}
