package com.wuxianyingke.property.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.mantoto.property.R;
import com.wuxianyingke.property.common.Constants;

public class FindPasswordActivity extends Activity
{
	private Button mTopbarLeft;
	private TextView mTopbarTxt;
	
	private int forTAG=0;
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
		if (keyCode == KeyEvent.KEYCODE_BACK) 
		{
			Intent intent = new Intent(FindPasswordActivity.this, LoginActivity.class);
			startActivity(intent);
			finish();
			return true;
		} 
		else 
			return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.find_password);
		Bundle extras=getIntent().getExtras();
		forTAG=extras.getInt(Constants.FORGET_TYPE_TAG);
		
		TextView promptText = (TextView) findViewById(R.id.prompt_text);
		promptText.setText(Html.fromHtml("目前仅支持网页找回密码："+"<font color='red'>www.coo8.com</font>"));
		
		mTopbarLeft = (Button) findViewById(R.id.topbar_left);
		mTopbarLeft.setText(R.string.txt_back);
		mTopbarLeft.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if(forTAG==0){
					Intent intent = new Intent(FindPasswordActivity.this, LoginActivity.class);
					intent.putExtra(Constants.LOGIN_TYPE_TAG, 0);
					startActivity(intent);
				}else{
					Intent intent = new Intent(FindPasswordActivity.this, LoginActivity.class);
					intent.putExtra(Constants.LOGIN_TYPE_TAG, 1);
					startActivity(intent);
				}
				finish();
			}
		});
		
		mTopbarTxt = (TextView) findViewById(R.id.topbar_txt);
		mTopbarTxt.setText(R.string.find_password);
		mTopbarTxt.setVisibility(View.GONE);
	}
	
	
}