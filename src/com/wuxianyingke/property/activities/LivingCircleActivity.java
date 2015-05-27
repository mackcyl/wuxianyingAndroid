package com.wuxianyingke.property.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mantoto.property.R;

/**
 * 生活圈
 * 
 * @author wentinggao
 * 
 */
public class LivingCircleActivity extends Activity {

	private ImageView mYingshiImageView, mGouwuImageView, mZhufangImageView,
			mYiyuanImageView, mDituImageView;
	public static AlertDialog dialog = null;
	public TextView topbar_txt;
	public Button topbar_left;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.living_circle);

		initWidgets();
	}

	private void initWidgets() {
		topbar_txt = (TextView) findViewById(R.id.topbar_txt);
		topbar_left = (Button) findViewById(R.id.topbar_left);
		topbar_txt.setText("生活圈");
		topbar_left.setVisibility(View.VISIBLE);
		topbar_left.setText("返回");
		topbar_left.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			finish();	
			}
		});
		mYingshiImageView = (ImageView) findViewById(R.id.YingshiImageView);
		mGouwuImageView = (ImageView) findViewById(R.id.GouwuImageView);
		mZhufangImageView = (ImageView) findViewById(R.id.ZhufangImageView);
		mYiyuanImageView = (ImageView) findViewById(R.id.YiyuanImageView);
		mDituImageView = (ImageView) findViewById(R.id.DituImageView);

		mYingshiImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialogInfo();
			}
		});
		mGouwuImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialogInfo();
			}
		});
		mZhufangImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialogInfo();
			}
		});
		mYiyuanImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialogInfo();
			}
		});
		mDituImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialogInfo();
			}
		});
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	private void dialogInfo() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setMessage("正在建设中......");
		builder.setTitle("友情提示");
		builder.setNegativeButton("确认", null);
		builder.create().show();

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		finish();
		return super.onKeyDown(keyCode, event);
	}
}