package com.wuxianyingke.property.push;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.mantoto.property.R;
import com.wuxianyingke.property.activities.NormalDialog;
import com.wuxianyingke.property.activities.SplashActivity;
import com.wuxianyingke.property.activities.pushDialog;
import com.wuxianyingke.property.activities.pushDialog.pushDialogListener;
import com.wuxianyingke.property.adapter.PushListItemAdapter;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.common.LogUtil;
import com.wuxianyingke.property.common.Util;
import com.wuxianyingke.property.remote.RemoteApi.PushMessage;
import com.wuxianyingke.property.remote.RemoteApi.PushMessageRetInfo;

public class PushActivity extends Activity{

	private PushMessageRetInfo pushMsg ;
	private ImageView pushImg ;
	private TextView pushTv ;
	
	private ListView pushLv ;
	
	private PushListItemAdapter adapter ;
	
	private boolean isAlive = true ;
	
	private PushMessage tempMsg ;
	
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case 999:
				if(pushImg != null){
					pushImg.setBackgroundDrawable(tempMsg.imgDw) ;
				}
				break ;
			case 1000:
//				pushImg.setBackgroundDrawable(pushMsg.imgDw) ;
				tempMsg = pushMsg.pushList.get(msg.arg1) ;
				createDialog(pushMsg.pushList.get(msg.arg1)) ;
				break ;
			}
		}
		
	} ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.push) ;
		pushMsg = (PushMessageRetInfo)getIntent().getSerializableExtra("push_msg") ;
		
		
		pushLv = (ListView) findViewById(R.id.push_list) ;
		
		/*mainLl = (LinearLayout) findViewById(R.id.main_ll) ;
		mainLl.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(PushActivity.this , SplashActivity.class) ;
				startActivity(intent) ;
			}
		}) ;
*/		if(pushMsg == null || pushMsg.pushList == null)	return ;
			adapter = new PushListItemAdapter(handler, this, pushMsg.pushList) ;
		pushLv.setAdapter(adapter) ;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		isAlive = false ;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			LogUtil.d("TAG", "onKeyDown") ;
			pushMsg = null ;
			finish() ;
			System.gc() ;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void createDialog(PushMessage pushMsg){
		getPushMsgImgThread = new GetPushMsgImgThread() ;
		getPushMsgImgThread.start() ;
		
		pushDialog tmpdialog = new pushDialog(PushActivity.this, 
				new pushDialogListener() {
					
					@Override
					public void onClick(View view) {
						// TODO Auto-generated method stub   
						switch(view.getId()){     
						case R.id.dialog_button_1:
							Intent intent = new Intent(PushActivity.this , SplashActivity.class) ;
							startActivity(intent) ;
							break;     
						case R.id.dialog_button_2:  
	                        break; 
                   
						}
					}
				});
		

		tmpdialog.show();
		pushImg = (ImageView)tmpdialog.getImage();
		pushTv = (TextView) tmpdialog.getText();
		pushTv.setText(pushMsg.msg) ;
		

	}
	
	private GetPushMsgImgThread getPushMsgImgThread ;
	private class GetPushMsgImgThread extends Thread{
		public void run(){
			Drawable dw = null;
			try {
				dw = Util.getDrawableFromCache(PushActivity.this, tempMsg.imgUrl);
				if(dw != null){
					tempMsg.imgDw = dw ;
					if(isAlive){
						handler.sendEmptyMessage(999) ;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	} ;

}
