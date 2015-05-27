package com.wuxianyingke.property.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mantoto.property.R;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.common.MD5;
import com.wuxianyingke.property.common.Util;
import com.wuxianyingke.property.remote.RemoteApi.NetInfo;
import com.wuxianyingke.property.remote.RemoteApiImpl;
import com.wuxianyingke.property.remote.RemoteApi.User;

/**
 * 联系发布人
 * 
 * @author wentinggao
 * 
 */
public class ContactPublisherActivity extends BaseActivityWithRadioGroup {
	private long mFleaid;
	private String mConatctName;
	private int propertyid;
	private EditText mContactPublisherContentEditText;
	private ProgressDialog mProgressBar = null;
	private String mErrorInfo = "";
	private String desc = "";
	private SharedPreferences saving;
	private Button topbar_left;
	private TextView topbar_txt,topbar_right;
	public Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (mProgressBar != null) {
				mProgressBar.dismiss();
				mProgressBar = null;
			}
			switch (msg.what) {
			// 发送失败
			case 0:
				Toast.makeText(ContactPublisherActivity.this, mErrorInfo,
						Toast.LENGTH_SHORT).show();
				break;

			// 发送成功
			case 1:
				Toast.makeText(ContactPublisherActivity.this, "发送成功",
						Toast.LENGTH_SHORT).show();
				finish();
				break;

			// 通讯错误
			case 4:
				Toast.makeText(ContactPublisherActivity.this,
						"通讯错误，请检查网络或稍后再试。", Toast.LENGTH_SHORT).show();
				break;
			case 8:
				Toast.makeText(ContactPublisherActivity.this, desc,
						Toast.LENGTH_SHORT).show();
				break;
			case 9:
				Toast.makeText(ContactPublisherActivity.this, "网络超时，请重新获取",
						Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_publisher);
		//initRadioGroup(R.id.radioGroup, R.id.toggle_radio1, 0);
		saving = this.getSharedPreferences(LocalStore.USER_INFO, 0);
		propertyid = (int) saving.getLong(LocalStore.PROPERTY_ID, 0);
		mFleaid = getIntent().getLongExtra("fleaid", -1);
		mConatctName = getIntent().getStringExtra("conatctname");
		Log.d("conatcctName", "mConatctName=" + mConatctName);
		initWidgets();
	}

	private void initWidgets() {
		propertyid = (int) saving.getLong(LocalStore.PROPERTY_ID, 0);
		topbar_left = (Button) findViewById(R.id.topbar_left);
		topbar_txt = (TextView) findViewById(R.id.topbar_txt);
		topbar_txt.setText("联系发布人");
		topbar_left.setVisibility(View.VISIBLE);
		topbar_left.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		topbar_right = (TextView)findViewById(R.id.topbar_right);
		topbar_right.setText("提交");
		topbar_right.setVisibility(View.VISIBLE);
		topbar_right.setClickable(true);
		topbar_right.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				sendContent();
			}
			
		});
		mContactPublisherContentEditText = (EditText) findViewById(R.id.ContactPublisherContentEditText);

	}

	public void sendContent() {
		if (Util.isEmpty(mContactPublisherContentEditText)) {
			Toast.makeText(ContactPublisherActivity.this, "内容不能为空",
					Toast.LENGTH_SHORT).show();
			return;
		} else {
			mProgressBar = ProgressDialog.show(ContactPublisherActivity.this,
					null, "登录中，请稍候...", true);
			Thread loginThread = new Thread() {
				public void run() {
					RemoteApiImpl remote = new RemoteApiImpl();
					NetInfo retNetInfo = remote.sendMessageFromFlea(propertyid,
							saving.getLong(LocalStore.USER_ID, 0), mFleaid,
							mContactPublisherContentEditText.getText()
									.toString().trim());

					Message msg = new Message();
					if (retNetInfo == null) {
						msg.what = 4;
					} else if (200 == retNetInfo.code) {
						msg.what = 1;
					} else {
						msg.what = 0;
						mErrorInfo = retNetInfo.desc;
					}

					mHandler.sendMessage(msg);
				}
			};
			loginThread.start();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	void freeResource() {
		// TODO Auto-generated method stub

	}

}