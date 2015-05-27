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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.mantoto.property.R;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.common.MD5;
import com.wuxianyingke.property.common.Util;
import com.wuxianyingke.property.remote.RemoteApi.User;
import com.wuxianyingke.property.remote.RemoteApi;
import com.wuxianyingke.property.remote.RemoteApiImpl;

public class Register1Activity extends Activity {
	private Button mRegisterButton;
	private TextView mTopbarTxt;
	private ProgressDialog mWaitLoading = null;
	private String mToActivity = null;
	private String mErrorInfo = "";
	private EditText Register2_edit1, Register2_edit2,Register2_edit3;
    private ImageButton Register2_edit1_clear_btn,Register2_edit2_clear_btn,Register2_edit3_clear_btn;
	private TextView Register2_txt1, Register2_txt2,Register2_txt3;
	private String desc = "";
	private String getCode = "";
	private String menpaihao = "";
	private String username = "";
	private String U_ID = "";
	private String U_PASS = "";

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
				Toast.makeText(Register1Activity.this, mErrorInfo,
						Toast.LENGTH_SHORT).show();
				break;

			// 注册成功
			case 3:

				RemoteApi.LoginInfo logininfo = new RemoteApi.LoginInfo();
				logininfo.U_ID=U_ID;
				logininfo.U_PASS=U_PASS;
				logininfo.autoLogin = false;
				LocalStore.setLoginInfo(Register1Activity.this,logininfo )	;
				
				LocalStore.setUserStatus(Register1Activity.this, true);
				Toast.makeText(Register1Activity.this, "注册成功啦~",
						Toast.LENGTH_SHORT).show();
				Intent intent = new Intent();
				intent.setClass(Register1Activity.this, Radio1Activity.class);
				
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
						| Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				finish();
				break;

			// 通讯错误
			case 4:
				Toast.makeText(Register1Activity.this, "通信错误，请检查网络或稍后再试。",
						Toast.LENGTH_SHORT).show();
				break;

			case 8:
				Toast.makeText(Register1Activity.this, desc, Toast.LENGTH_SHORT)
						.show();
				break;
			case 9:
				Toast.makeText(Register1Activity.this, "网络超时，请重新获取",
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
			Intent intent = new Intent(Register1Activity.this,
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
		setContentView(R.layout.register2);
		Intent intent = this.getIntent();
		menpaihao = intent.getStringExtra("menpaihao");
		username = intent.getStringExtra("username");
		initRegisterNormal();
	}

	// 普通注册初始化
	private void initRegisterNormal() {
		Register2_edit1 = (EditText) findViewById(R.id.Register2_edit1);
		Register2_edit1.addTextChangedListener(new TextWatcher() {
			
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
					Register2_edit1_clear_btn.setVisibility(View.VISIBLE);
				}
				else
				{
					Register2_edit1_clear_btn.setVisibility(View.GONE);					
				}
			}
		});
		
		Register2_edit1_clear_btn =  (ImageButton) findViewById(R.id.Register2_edit1_clear_btn);
		if(Register2_edit1.getText().toString().equals(""))
		{
			Register2_edit1_clear_btn.setVisibility(View.GONE);
		}
		else
		{
			Register2_edit1_clear_btn.setVisibility(View.VISIBLE);					
		}
		Register2_edit1_clear_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Register2_edit1.setText("");
			}
		});
		
		
		
		Register2_edit2 = (EditText) findViewById(R.id.Register2_edit2);
		Register2_edit2.addTextChangedListener(new TextWatcher() {
			
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
					Register2_edit2_clear_btn.setVisibility(View.VISIBLE);
				}
				else
				{
					Register2_edit2_clear_btn.setVisibility(View.GONE);					
				}
			}
		});
		
		Register2_edit2_clear_btn =  (ImageButton) findViewById(R.id.Register2_edit2_clear_btn);
		if(Register2_edit2.getText().toString().equals(""))
		{
			Register2_edit2_clear_btn.setVisibility(View.GONE);
		}
		else
		{
			Register2_edit2_clear_btn.setVisibility(View.VISIBLE);					
		}
		Register2_edit2_clear_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Register2_edit2.setText("");
			}
		});
		
		
		Register2_edit3 = (EditText) findViewById(R.id.Register2_edit3);
		Register2_edit3.addTextChangedListener(new TextWatcher() {
			
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
					Register2_edit3_clear_btn.setVisibility(View.VISIBLE);
				}
				else
				{
					Register2_edit3_clear_btn.setVisibility(View.GONE);					
				}
			}
		});
		
		Register2_edit3_clear_btn =  (ImageButton) findViewById(R.id.Register2_edit3_clear_btn);
		if(Register2_edit3.getText().toString().equals(""))
		{
			Register2_edit3_clear_btn.setVisibility(View.GONE);
		}
		else
		{
			Register2_edit3_clear_btn.setVisibility(View.VISIBLE);					
		}
		Register2_edit3_clear_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Register2_edit3.setText("");
			}
		});
		
		Register2_txt1 = (TextView) findViewById(R.id.Register2_txt1);
		Register2_txt2 = (TextView) findViewById(R.id.Register2_txt2);
		Register2_txt3 = (TextView) findViewById(R.id.Register2_txt3);
		regTip(false,true,false);
		//mUserNameEditText = (EditText) findViewById(R.id.UserNameEditText);
		//mPasswordEditText = (EditText) findViewById(R.id.PasswordEditText);
		//mRegPasswordEditText = (EditText) findViewById(R.id.RegPasswordEditText);
		Button mRegisterButton = (Button) findViewById(R.id.Register2_btn);
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
		mWaitLoading = ProgressDialog.show(Register1Activity.this, null,
				"注册中，请稍候......", true);
		U_ID = Register2_edit1.getText().toString();
		U_PASS = Register2_edit2.getText().toString();
		Thread registerThread = new Thread() {
			public void run() {
				SharedPreferences saving = Register1Activity.this
						.getSharedPreferences(LocalStore.USER_INFO, 0);
				RemoteApiImpl remote = new RemoteApiImpl();
				User retUserInfo = remote.userRegister(Register1Activity.this,
						menpaihao,
						username,
						Register2_edit1.getText().toString(),
						MD5.toMD5(Register2_edit2.getText().toString()),
						saving.getLong(LocalStore.PROPERTY_ID, 0));

				Message msg = new Message();
				if (retUserInfo == null) {
					msg.what = 4;
				} else if (200 == retUserInfo.netInfo.code) {
					msg.what = 3;
					LocalStore.setUserInfo(Register1Activity.this, retUserInfo);
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
		if (Util.isEmpty(Register2_edit1)) {
			Toast.makeText(Register1Activity.this,
					R.string.error_username_cannot_be_empty, Toast.LENGTH_SHORT)
					.show();
			return false;
		} else if (!Util.isRegisterUserName(Register2_edit1.getText()
				.toString().trim())) {
			regTip(false,true,false);
			Toast.makeText(Register1Activity.this, "用户名输入不合法",
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (Util.isEmpty(Register2_edit2)) {
			regTip(true,false,false);
			Toast.makeText(Register1Activity.this,
					R.string.error_password_cannot_be_empty, Toast.LENGTH_SHORT)
					.show();
			return false;
		} else if (!Register2_edit2.getText().toString()
				.equals(Register2_edit3.getText().toString())) {
			regTip(false,false,true);
			Toast.makeText(Register1Activity.this,
					R.string.error_two_password_not_same, Toast.LENGTH_SHORT)
					.show();
			return false;
		}
		else {
			String password1 = Register2_edit2.getText().toString();
			Pattern pat = Pattern.compile("^[a-z|0-9|A-Z]+$");
			Matcher isOk = pat.matcher(password1);
			if (!isOk.matches() || password1.length() < 6
					|| password1.length() > 16) {
				Toast.makeText(Register1Activity.this, R.string.warn_bad_format,
						Toast.LENGTH_SHORT).show();
				return false;
			} else {
				return true;
			}
		}

	}


	private void resetTip() {
		Register2_txt1.setText("");
		Register2_txt2.setText("");
		Register2_txt3.setText("");
	}
	private void regTip(boolean tip1,boolean tip2,boolean tip3) {
		resetTip();
		if(tip1)
			Register2_txt1.setText(R.string.Register2_tip1);
		if(tip2)
			Register2_txt2.setText(R.string.Register2_tip2);
		if(tip3)
			Register2_txt3.setText(R.string.Register2_tip3);		
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
