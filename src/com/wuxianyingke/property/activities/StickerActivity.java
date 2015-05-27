package com.wuxianyingke.property.activities;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mantoto.property.R;
import com.wuxianyingke.property.adapter.AppPopularizeAdapter;
import com.wuxianyingke.property.adapter.bianqianAdapter;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.common.LocalStore.bianqian;
import com.wuxianyingke.property.common.LogUtil;
import com.wuxianyingke.property.common.Util;
import com.wuxianyingke.property.remote.RemoteApi.AppPopularize;
import com.wuxianyingke.property.threads.GetAppPopularizeThread;

public class StickerActivity extends Activity {
	 private TextView topbar_txt,topbar_right;
	 private Button topbar_left;
	 private Button mSaveButton;
	 private JSONObject response = null;
	 private ListView mLogsListView;
	 private bianqianAdapter mListAdapter;
	 private LocalStore localstore;
	 
		private Handler mHandler = new Handler()
		{
			public void handleMessage(Message msg)
			{
				switch (msg.what)
				{
					case 0:
					default:
						mListAdapter.notifyDataSetChanged();
				}
			}
		};
		
  @Override
protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	setContentView(R.layout.sticker);
	localstore = new LocalStore();
	localstore.initBianqian(this);
	initWidgets();
	
}
	@Override
	protected void onNewIntent(Intent intent)
	{
		if(mListAdapter!=null)
		{
			mListAdapter.notifyDataSetChanged();
		}
		super.onNewIntent(intent);
	}
  private void initWidgets()
  {
	  topbar_txt = (TextView) findViewById(R.id.topbar_txt);
      topbar_left = (Button) findViewById(R.id.topbar_left);
      topbar_txt.setText("生活便签");
      topbar_left.setVisibility(View.VISIBLE);
      String title=LocalStore.getBianqianTitle(this);
	  String content=LocalStore.getBianqianContent(this);
      topbar_left.setOnClickListener(new OnClickListener() {

          @Override
          public void onClick(View v) {
              // TODO Auto-generated method stub
              finish();
          }
      });

      topbar_right = (TextView) findViewById(R.id.topbar_right);
      topbar_right.setText("新建");
      topbar_right.setVisibility(View.VISIBLE);
      topbar_right.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
					| Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.setClass(StickerActivity.this, AddBianqianActivity.class);
			startActivityForResult(intent, 0);
		}
	});
	 mLogsListView = (ListView) findViewById(R.id.bianqian_list_view);
	 mLogsListView.setVerticalScrollBarEnabled(false);
	 mLogsListView.setDivider(getResources().getDrawable(R.drawable.list_line));
	 showLogsListView(localstore.bianqian_List); 
		
  }
  	public void showLogsListView(ArrayList<bianqian> arrayList) 
	{
  		if(mListAdapter == null)
  			mListAdapter = new bianqianAdapter(this, arrayList,mHandler);
  		
		mLogsListView.setVisibility(View.VISIBLE);
		mLogsListView.setAdapter(mListAdapter);

	}
  	protected  void onActivityResult(int requestCode, int resultCode, Intent data)  {
  		if(mListAdapter!=null)
		{
			mListAdapter.notifyDataSetChanged();
		}
        super.onActivityResult(requestCode, resultCode,  data);
  	}
}
