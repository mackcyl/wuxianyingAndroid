package com.wuxianyingke.property.activities;

import java.io.File;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mantoto.property.R;
import com.wuxianyingke.property.activities.NormalDialog.NormalDialogListener;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.common.MD5;
import com.wuxianyingke.property.common.Util;
import com.wuxianyingke.property.remote.RemoteApi;
import com.wuxianyingke.property.remote.RemoteApi.User;
import com.wuxianyingke.property.remote.RemoteApiImpl;

public class LoginActivity extends Activity {
	private CheckBox mLoginCheckBox;
	private ProgressDialog mProgressBar = null;
	private String mErrorInfo = "";
	private EditText mUserName, mUserPassword;
	private ImageButton mUserName_clear, mUserPassword_clear;
	private ImageView mUserNameImage, mUserPasswordImage;
	private TextView login_register, login_nopass;
	private LinearLayout mLoginLogoLinearLayout;
	private String desc = "";

	private String U_ID = "";
	private String U_PASS = "";

	static LoginActivity mActivity;
	public Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (mProgressBar != null) {
				mProgressBar.dismiss();
				mProgressBar = null;
			}
			switch (msg.what) {
			// 登录失败
			case 0:

				Toast.makeText(LoginActivity.this, mErrorInfo,
						Toast.LENGTH_SHORT).show();
			   
				NormalDialog tmpdialog = new NormalDialog(mActivity, 
						new NormalDialogListener() {
							
							@Override
							public void onClick(View view) {
								// TODO Auto-generated method stub   
								switch(view.getId()){     
								case R.id.dialog_button_1:  
									break;     
								case R.id.dialog_button_2:  
									findPassword(); 
			                        break; 
								case R.id.dialog_button_3:  
			                        break; 
		                   
								}
							}
						},

						mActivity.getString(R.string.txt_tips),
						mActivity.getString(R.string.txt_tips_login_error),
						mActivity.getString(R.string.txt_cancel),
						mActivity.getString(R.string.find_password));
				tmpdialog.show();
				break;

			// 登陆成功
			case 1:
			{
				RemoteApi.LoginInfo logininfo = new RemoteApi.LoginInfo();
				logininfo.U_ID=U_ID;
				logininfo.U_PASS=U_PASS;
				logininfo.autoLogin = false;
				LocalStore.setLoginInfo(LoginActivity.this,logininfo )	;
				
				Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT)
						.show();
				Intent intent = new Intent();
				intent.setClass(LoginActivity.this, Radio1Activity.class);
				
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
						| Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				finish();
			}
				break;
				
			// 自动登陆失败
			case 3:
			{
				Toast.makeText(LoginActivity.this, mErrorInfo,
						Toast.LENGTH_SHORT).show();
				RemoteApi.LoginInfo logininfo = LocalStore.initLoginInfo(LoginActivity.this);
				logininfo.autoLogin = false;
				LocalStore.setLoginInfo(LoginActivity.this,logininfo )	;
			}
				break;


			// 通讯错误
			case 4:
				Toast.makeText(LoginActivity.this, "通讯错误，请检查网络或稍后再试。",
						Toast.LENGTH_SHORT).show();
				break;
			case 8:
				Toast.makeText(LoginActivity.this, desc, Toast.LENGTH_SHORT)
						.show();
				break;
			case 9:
				Toast.makeText(LoginActivity.this, "网络超时，请重新获取",
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
			finish();
			return true;
		} else
			return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.login);
		
		mActivity = this;
		mLoginLogoLinearLayout = (LinearLayout) findViewById(R.id.LoginLogoLinearLayout);
		/*
		File file = new File(Constants.LOADING_PIC_PATH
				+ Constants.LOGIN_PIC_FILENAME);
		if (file.exists()) {
			Bitmap bitmap = BitmapFactory.decodeFile(Constants.LOADING_PIC_PATH
					+ Constants.LOGIN_PIC_FILENAME);
			BitmapDrawable bd = new BitmapDrawable(bitmap);
			mLoginLogoLinearLayout.setBackgroundDrawable(bd);
		}
		*/
		initLoginNormal();
		
		RemoteApi.LoginInfo logininfo = LocalStore.initLoginInfo(LoginActivity.this);
		
			mUserName.setText(logininfo.U_ID);
			mUserPassword.setText(logininfo.U_PASS);

		if(logininfo.autoLogin)
		{
			startLogin();
		}
	}

	// 普通登录初始化
	private void initLoginNormal() {
		mUserName = (EditText) findViewById(R.id.userName);
		mUserName_clear = (ImageButton) findViewById(R.id.userName_clear_btn);
		mUserNameImage = (ImageView) findViewById(R.id.userNameImage);
		
		mUserName.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				if(arg0.length()>0)
				{
					mUserName_clear.setVisibility(View.VISIBLE);
					mActivity.
					mUserNameImage.setImageResource(R.drawable.login_user1);
				}
				else
				{
					mUserName_clear.setVisibility(View.GONE);		
					mUserNameImage.setImageResource(R.drawable.login_user2);			
				}
			}
		});
		
		if(mUserName.getText().toString().equals(""))
		{
			mUserName_clear.setVisibility(View.VISIBLE);
			mUserNameImage.setImageResource(R.drawable.login_user1);
		}
		else
		{
			mUserName_clear.setVisibility(View.GONE);		
			mUserNameImage.setImageResource(R.drawable.login_user2);			
		}
		mUserName_clear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mUserName.setText("");
			}
		});
		
		mUserPassword = (EditText) findViewById(R.id.UserPassword);
		mUserPassword_clear = (ImageButton) findViewById(R.id.UserPassword_clear_btn);
		mUserPasswordImage = (ImageView) findViewById(R.id.UserPasswordImage);
		mUserPassword.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				if(arg0.length()>0)
				{
					mUserPassword_clear.setVisibility(View.VISIBLE);
					mUserPasswordImage.setImageResource(R.drawable.login_pass1);
				}
				else
				{
					mUserPassword_clear.setVisibility(View.GONE);		
					mUserPasswordImage.setImageResource(R.drawable.login_pass2);			
				}
			}
		});
		
		if(mUserPassword.getText().toString().equals(""))
		{
			mUserPassword_clear.setVisibility(View.VISIBLE);
			mUserPasswordImage.setImageResource(R.drawable.login_pass1);
		}
		else
		{
			mUserPassword_clear.setVisibility(View.GONE);		
			mUserPasswordImage.setImageResource(R.drawable.login_pass2);			
		}
		mUserPassword_clear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mUserPassword.setText("");
			}
		});
		
		
		mLoginCheckBox = (CheckBox) findViewById(R.id.LoginCheckBox);

		Button mLoginButton = (Button) findViewById(R.id.loginButton);
		mLoginButton.setOnClickListener(mLoginNormalListener);

		
		Button mtouristButton = (Button) findViewById(R.id.touristButton);
		mtouristButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this,
						Radio1Activity.class);
				User tourist = new User();
				tourist.userId=0;
				LocalStore.setUserInfo(LoginActivity.this, tourist);
				LocalStore.setIsVisitor(getApplicationContext(), true);
				startActivity(intent);
			}
		});

		login_register = (TextView) findViewById(R.id.login_register);
		login_register.setClickable(true);
		login_register.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(LoginActivity.this,
						RegisterActivity.class);
				intent.putExtra(Constants.REGISTER_TYPE_TAG, 1);
				startActivity(intent);
			}
		});
		
		login_nopass = (TextView) findViewById(R.id.login_nopass);
		login_nopass.setClickable(true);
		login_nopass.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				findPassword();
			}
		});
	}

	// 普通登录按钮监听
	private final OnClickListener mLoginNormalListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			startLoginNormal();

		}
	};

	private void startLogin(){
		Boolean lastlogin = false;
		mProgressBar = ProgressDialog.show(LoginActivity.this, null,
				"登录中，请稍候...", true);
		Thread loginThread = new Thread() {
			public void run() {
				RemoteApiImpl remote = new RemoteApiImpl();
				User retUserInfo = remote.userLogin(LoginActivity.this,
						mUserName.getText().toString(),
						mUserPassword.getText().toString());

				Message msg = new Message();
				if (retUserInfo == null) {
					msg.what = 4;
				} else if (200 == retUserInfo.netInfo.code) {
					msg.what = 1;
					LocalStore.setUserInfo(LoginActivity.this, retUserInfo);
				} else {
					msg.what = 0;
					mErrorInfo = retUserInfo.netInfo.desc;
				}

				mHandler.sendMessage(msg);
			}
		};
		loginThread.start();
	}
	
	// 普通登录，未修改
	private void startLoginNormal() {
		if (Util.isEmpty(mUserName)) {
			Toast.makeText(LoginActivity.this,
					R.string.error_please_input_username, Toast.LENGTH_SHORT)
					.show();
			return;
		} else if (Util.isEmpty(mUserPassword)) {
			Toast.makeText(LoginActivity.this,
					R.string.error_please_input_password, Toast.LENGTH_SHORT)
					.show();
			return;
		}

		U_ID = mUserName.getText().toString();
		U_PASS = MD5.toMD5(mUserPassword.getText().toString());
		
		if (mLoginCheckBox.isChecked()) {
			LocalStore.setUserStatus(LoginActivity.this, true);
			Log.d("MyTag", "user_status=" + mLoginCheckBox.isChecked());
		} else {
			LocalStore.setUserStatus(LoginActivity.this, false);
			Log.d("MyTag", "user_status11111=" + false);
		}

		mProgressBar = ProgressDialog.show(LoginActivity.this, null,
				"登录中，请稍候...", true);
		Thread loginThread = new Thread() {
			public void run() {
				RemoteApiImpl remote = new RemoteApiImpl();
				User retUserInfo = remote.userLogin(LoginActivity.this,
						mUserName.getText().toString(),
						MD5.toMD5(mUserPassword.getText().toString()));

				Message msg = new Message();
				if (retUserInfo == null) {
					msg.what = 0;
				} else if (200 == retUserInfo.netInfo.code) {
					msg.what = 1;
					LocalStore.setUserInfo(LoginActivity.this, retUserInfo);
				} else {
					msg.what = 0;
					mErrorInfo = retUserInfo.netInfo.desc;
				}

				mHandler.sendMessage(msg);
			}
		};
		loginThread.start();
	}
	
	public void findPassword()
	{
		// TODO Auto-generated method stub
		Toast.makeText(LoginActivity.this, "请与物业客服中心联系.",
				Toast.LENGTH_LONG).show();
		/*
		Intent intent = new Intent(LoginActivity.this,
				RegisterActivity.class);
		intent.putExtra(Constants.REGISTER_TYPE_TAG, 1);
		startActivity(intent);
		*/
	}

}