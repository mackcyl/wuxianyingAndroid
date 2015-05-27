package com.wuxianyingke.property.activities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mantoto.property.R;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.common.MD5;
import com.wuxianyingke.property.common.Util;
import com.wuxianyingke.property.remote.RemoteApi.User;
import com.wuxianyingke.property.remote.RemoteApiImpl;

public class fRegister1Activity extends Activity {
	private Button mRegisterButton;
	private TextView mTopbarTxt;
	private Button topbar_left,topbar_right;
	private ProgressDialog mWaitLoading = null;
	private String mToActivity = null;
	private String mErrorInfo = "";
	private EditText mHouseNumberEditText, mProprietorNameEditText,
			mUserNameEditText, mPasswordEditText, mRegPasswordEditText;
	private String desc = "";
	private String getCode = "";

	public Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (mWaitLoading != null) {
				mWaitLoading.dismiss();
				mWaitLoading = null;
			}
			switch (msg.what) {
			// 注册失败
			case 2:
				Toast.makeText(fRegister1Activity.this, mErrorInfo,
						Toast.LENGTH_SHORT).show();
				break;

			// 注册成功
			case 3:
				LocalStore.setUserStatus(fRegister1Activity.this, true);
				Toast.makeText(fRegister1Activity.this, "注册成功啦~",
						Toast.LENGTH_SHORT).show();
				Intent intent = new Intent();
				intent.setClass(fRegister1Activity.this, Radio1Activity.class);				
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
						| Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				finish();
				break;

			// 通讯错误
			case 4:
				Toast.makeText(fRegister1Activity.this, "通信错误，请检查网络或稍后再试。",
						Toast.LENGTH_SHORT).show();
				break;

			case 8:
				Toast.makeText(fRegister1Activity.this, desc, Toast.LENGTH_SHORT)
						.show();
				break;
			case 9:
				Toast.makeText(fRegister1Activity.this, "网络超时，请重新获取",
						Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(fRegister1Activity.this,
					LoginActivity.class);
			startActivity(intent);
			finish();
			return true;
		} else
			return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		mToActivity = getIntent().getStringExtra("toActivity");
		mTopbarTxt= (TextView) findViewById(R.id.topbar_txt);
		mTopbarTxt.setText("注       册");
		topbar_left= (Button) findViewById(R.id.topbar_left);
		topbar_left.setVisibility(View.VISIBLE);
		topbar_left.setText("返回");
		topbar_left.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		topbar_right= (Button) findViewById(R.id.topbar_right);
		topbar_right.setVisibility(View.GONE);
		initRegisterNormal();
	}

	// 普通注册初始化
	private void initRegisterNormal() {

		mHouseNumberEditText = (EditText) findViewById(R.id.HouseNumbereEditText);
		mProprietorNameEditText = (EditText) findViewById(R.id.ProprietorNameEditText);
		mUserNameEditText = (EditText) findViewById(R.id.UserNameEditText);
		mPasswordEditText = (EditText) findViewById(R.id.PasswordEditText);
		mRegPasswordEditText = (EditText) findViewById(R.id.RegPasswordEditText);
		Button mRegisterButton = (Button) findViewById(R.id.RegisterButton);
		mRegisterButton.setOnClickListener(mRegNormalListener);
	}

	// 普通注册按钮监听
	private final OnClickListener mRegNormalListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			startRegistrNormal();
		}
	};

	// 普通注册，原方法，未修改
	private void startRegistrNormal() {
		if (!inputIsLegalNormal()) {
			return;
		}
		mWaitLoading = ProgressDialog.show(fRegister1Activity.this, null,
				"注册中，请稍候......", true);
		Thread registerThread = new Thread() {
			public void run() {
				SharedPreferences saving = fRegister1Activity.this
						.getSharedPreferences(LocalStore.USER_INFO, 0);
				RemoteApiImpl remote = new RemoteApiImpl();
				User retUserInfo = remote.userRegister(fRegister1Activity.this,
						mHouseNumberEditText.getText().toString(),
						mProprietorNameEditText.getText().toString(),
						mUserNameEditText.getText().toString(),
						MD5.toMD5(mPasswordEditText.getText().toString()),
						saving.getLong(LocalStore.PROPERTY_ID, 0));

				Message msg = new Message();
				if (retUserInfo == null) {
					msg.what = 4;
				} else if (200 == retUserInfo.netInfo.code) {
					msg.what = 3;
					LocalStore.setUserInfo(fRegister1Activity.this, retUserInfo);
				} else {
					msg.what = 2;
					mErrorInfo = retUserInfo.netInfo.desc;
				}
				mHandler.sendMessage(msg);
			}
		};
		registerThread.start();
	}

	// 普通注册输入判断，未修改
	private boolean inputIsLegalNormal() {
		if (Util.isEmpty(mHouseNumberEditText)) {
			Toast.makeText(fRegister1Activity.this, "门牌号输入不合法",
					Toast.LENGTH_SHORT).show();
			return false;
		}
		if (Util.isEmpty(mProprietorNameEditText)) {
			Toast.makeText(fRegister1Activity.this, "业主姓名不能为空",
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (!Util.isRegisterUserName(mProprietorNameEditText.getText()
				.toString().trim())) {
			Toast.makeText(fRegister1Activity.this,
					R.string.error_username_length, Toast.LENGTH_SHORT).show();
			return false;
		} else if (Util.isEmpty(mUserNameEditText)) {
			Toast.makeText(fRegister1Activity.this,
					R.string.error_username_cannot_be_empty, Toast.LENGTH_SHORT)
					.show();
			return false;
		} else if (!Util.isRegisterUserName(mUserNameEditText.getText()
				.toString().trim())) {
			Toast.makeText(fRegister1Activity.this, "用户名输入不合法",
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (Util.isEmpty(mPasswordEditText)) {
			Toast.makeText(fRegister1Activity.this,
					R.string.error_password_cannot_be_empty, Toast.LENGTH_SHORT)
					.show();
			return false;
		} else if (!mPasswordEditText.getText().toString()
				.equals(mRegPasswordEditText.getText().toString())) {
			Toast.makeText(fRegister1Activity.this,
					R.string.error_two_password_not_same, Toast.LENGTH_SHORT)
					.show();
			return false;
		}

		else {
			String password1 = mPasswordEditText.getText().toString();
			Pattern pat = Pattern.compile("^[a-z|0-9|A-Z]+$");
			Matcher isOk = pat.matcher(password1);
			if (!isOk.matches() || password1.length() < 6
					|| password1.length() > 16) {
				Toast.makeText(fRegister1Activity.this, R.string.warn_bad_format,
						Toast.LENGTH_SHORT).show();
				return false;
			} else {
				return true;
			}
		}

	}

	private void goToActivity() {
		if (mToActivity == null) {
			return;
		}

		if (mToActivity.equals("OrderCarActivity")) {
			// Intent intent = new Intent(LoginActivity.this,
			// OrderCarActivity.class);
			// startActivity(intent);
		}
	}
}
