package com.wuxianyingke.property.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;

import com.mantoto.property.R;
import com.wuxianyingke.property.common.Util;

public class HelpActivity extends Activity {

	private boolean mFromMore;
	private WebView mWebView;
	private String url = "file:///android_asset/html/help.html";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);
		initWidget();
	}

	public void initWidget() {
		ImageView cartImageview = (ImageView) findViewById(R.id.cart_imageview);
		Util.modifyCarNumber(this, cartImageview);
		mFromMore = getIntent().getBooleanExtra("fromMore", true);
		Button backbutton = (Button) findViewById(R.id.topbar_left);
		if (!mFromMore) {
			backbutton.setText(R.string.txt_back);
		}
		backbutton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mWebView = (WebView) findViewById(R.id.help_webview);
		mWebView.setBackgroundColor(0);
		mWebView.loadUrl(url);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

}
