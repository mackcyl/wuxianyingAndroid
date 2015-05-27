package com.wuxianyingke.property.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mantoto.property.R;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.Util;
/**
 * 关于
 * @author wentinggao
 *
 */
public class AboutActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		initWidgets();
	}

	private void initWidgets() {
		ImageView cartImageview=(ImageView)findViewById(R.id.cart_imageview);
		Util.modifyCarNumber(this,cartImageview);
		String version = Util.getPackageVersion(getApplicationContext(),
				Constants.PACKAGENAME);

		if (version != null) {
			TextView textVersion = (TextView) findViewById(R.id.text_version);
			textVersion.setText(version);
		}

		Button backbutton = (Button) findViewById(R.id.topbar_left);
		backbutton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

	}

	@Override
	protected void onStart() {
		super.onStart();
	}


}