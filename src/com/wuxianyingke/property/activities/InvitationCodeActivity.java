package com.wuxianyingke.property.activities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.mantoto.property.R;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.common.Util;
import com.wuxianyingke.property.common.UtilZip;
import com.wuxianyingke.property.remote.HttpComm;
import com.wuxianyingke.property.remote.RemoteApi.InvitationCode;
import com.wuxianyingke.property.remote.RemoteApiImpl;

/***
 * 小区物业邀请码
 * 
 * @author wentinggao
 * 
 */
public class InvitationCodeActivity extends Activity {
	
    private TextView topbar_txt,topbar_right;
    private Button topbar_left;
    private ImageButton clear;
    
	private EditText mInvitationCodeEditText;
	private Button mInvitationCodeButton;
	private ProgressDialog mProgressBar = null;
	private String mErrorInfo = "";
	private String desc="";
	java.io.File file =null;
	public Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			if (mProgressBar != null) 
			{
				mProgressBar.dismiss();
				mProgressBar = null; 
			}
			switch (msg.what)
			{
			// 登录失败
			case 0:
				Toast.makeText(InvitationCodeActivity.this, mErrorInfo, Toast.LENGTH_SHORT).show();
				break;

				// 登陆成功
			case 1:
				Toast.makeText(InvitationCodeActivity.this, "小区验证码验证成功", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent();
				intent.setClass(InvitationCodeActivity.this, LoginActivity.class);
				startActivity(intent);
				finish();
				break;

				// 通讯错误
			case 4:
				Toast.makeText(InvitationCodeActivity.this, "通讯错误，请检查网络或稍后再试。", Toast.LENGTH_SHORT).show();
				break;
			case 8:
				Toast.makeText(InvitationCodeActivity.this, desc, Toast.LENGTH_SHORT).show();
				break;
			case 9:
				Toast.makeText(InvitationCodeActivity.this, "网络超时，请重新获取", Toast.LENGTH_SHORT).show();
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
		setContentView(R.layout.invitation_code);
		initWidgets();
		
		
		

		/* file = new java.io.File("/sdcard/wxyk/pic.zip"); 
		try {
			UtilZip.zipFiles(UtilZip.listFiles("/sdcard/wxyk/pic"), file);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Thread avatarstupianThread = new Thread()
		{
			public void run()
			{
				
			  //JSONObject str_response = HttpComm.uploadFile(Constants.URL + "/Json/test.aspx", file, "");
				Log.d("MyTag", "str_response=11111");
				JSONObject str_response = HttpComm.uploadFile
				("/Json/FleaNew.aspx",  1,1,10143,"中午标题","内容","",file,Constants.HTTP_SO_TIMEOUT);
				try {
					JSONObject str_response = HttpComm.uploadFile
							("/Json/FleaEdit.aspx",27,1,10143,"编辑标题","内容","16,17,18,19,20",file,Constants.HTTP_SO_TIMEOUT);
					Log.d("MyTag", "str_response222="+str_response);
					if (str_response != null)
					{
						Log.d("MyTag", "str_response333="+str_response);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		
		avatarstupianThread.start();*/
	}
	
	 //提交验证码
		private void invitationCodeSend()
		{
			if (Util.isEmpty(mInvitationCodeEditText))
			{
				Toast.makeText(this, R.string.error_please_input_invitation_code, Toast.LENGTH_SHORT).show();
				return;
			}
			mProgressBar = ProgressDialog.show(InvitationCodeActivity.this, null, "验证中，请稍候..." ,true);
			Thread invitationCodeThread = new Thread()
			{
				public void run()
				{
					RemoteApiImpl remote = new RemoteApiImpl();
					InvitationCode retUserInfo = remote.sendInvitationCode(InvitationCodeActivity.this, mInvitationCodeEditText.getText().toString());

					Message msg = new Message();
					if(retUserInfo == null)
					{
						msg.what = 4;
					}
					else if(200==retUserInfo.netInfo.code)
					{
						File file = new File(Constants.LOADING_PIC_PATH + Constants.LOGIN_PIC_FILENAME) ;
						
						if(retUserInfo.logoUrl.equals(file.exists()))
							return ;
						
						if(file.exists()){
							file.delete() ;
						}
						
								//LocalStore.setLoadingId(InvitationCodeActivity.this, loading.logoId) ;
								File path = new File(Constants.LOADING_PIC_PATH) ;
								if(!path.isDirectory()){
									path.mkdir() ;
								} 
								
								File tempFile = new File(Constants.LOADING_PIC_PATH + Constants.LOGIN_PIC_FILENAME + ".tmp") ;
								if(tempFile.exists()){
									tempFile.delete() ;
								}
								try{
									FileOutputStream outStream = new FileOutputStream(tempFile);
									HttpURLConnection conn = (HttpURLConnection) new URL(Constants.URL +retUserInfo.logoUrl)
											.openConnection();
									conn.setConnectTimeout(10 * 1000);
									conn.setRequestMethod("GET");
									int status = conn.getResponseCode();
									if (status == 200) {
										InputStream inStream = conn.getInputStream();
										byte[] buffer = new byte[1024];
										int len = 0;
										while ((len = inStream.read(buffer)) != -1) {
											outStream.write(buffer, 0, len);
										}
										outStream.close();
										inStream.close();
										tempFile.renameTo(file) ;
									}
								} catch (Exception e) {
									e.printStackTrace() ;
								}
						msg.what = 1;
						LocalStore.setInvitationCode(InvitationCodeActivity.this, retUserInfo);
					}
					else
					{
						msg.what = 0;
						mErrorInfo = retUserInfo.netInfo.desc;
					}

					mHandler.sendMessage(msg);
				}
			};
			invitationCodeThread.start();
		}
		
	private void initWidgets() {
		
		topbar_txt = (TextView) findViewById(R.id.topbar_txt);
		topbar_txt.setText(getResources().getText(R.string.invitation_code_title));
		 topbar_left = (Button) findViewById(R.id.topbar_left);
		 topbar_left.setVisibility(View.GONE);
		 topbar_left.setEnabled(false);
		 topbar_right = (TextView) findViewById(R.id.topbar_right);
		 topbar_right.setText(R.string.txt_cancel);
		 topbar_right.setVisibility(View.VISIBLE);
		 topbar_right.setOnClickListener(new OnClickListener() {

	            @Override
	            public void onClick(View v) {
	                // TODO Auto-generated method stub
	                finish();
	            }
	        });

		mInvitationCodeEditText = (EditText) findViewById(R.id.InvitationCodeEditText);
		mInvitationCodeEditText.addTextChangedListener(new TextWatcher() {
			
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
					clear.setVisibility(View.VISIBLE);
				}
				else
				{
					clear.setVisibility(View.GONE);					
				}
			}
		});
		
		clear =  (ImageButton) findViewById(R.id.Invitation_clear_btn);
		if(mInvitationCodeEditText.getText().toString().equals(""))
		{
			clear.setVisibility(View.GONE);
		}
		else
		{
			clear.setVisibility(View.VISIBLE);					
		}
		clear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mInvitationCodeEditText.setText("");
			}
		});
		
		mInvitationCodeButton = (Button) findViewById(R.id.InvitationCodeButton);
		mInvitationCodeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				invitationCodeSend();
				//Intent in=new Intent();
				//in.setClass(InvitationCodeActivity.this, ContactPublisherActivity.class);
				//in.setClass(InvitationCodeActivity.this, ReleaseGoodsActivity.class);
				//startActivity(in);
			}
		});
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

}