package com.wuxianyingke.property.threads;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;

import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.remote.RemoteApi.HomeMessage;
import com.wuxianyingke.property.remote.RemoteApiImpl;

public class GetIndexInfoThread extends Thread{

	private Context mContext ;
	private Handler mHandler ;
	private HomeMessage homeMessage ;
	private boolean isRuning = true ;
	
	public GetIndexInfoThread(Context context , Handler handler){
		mContext = context ;
		mHandler = handler ;
	}
	
	public HomeMessage getAllIndexInfo(){
		return homeMessage ;
	}
	
	public void stopRun(){
		isRuning = false ;
	}
	
	public void run(){
		RemoteApiImpl rai = new RemoteApiImpl() ;
		//TODO 获取首页信息，取得userid和logoid logoid userid propertyid
		SharedPreferences saving = mContext.getSharedPreferences(LocalStore.USER_INFO, 0);
		homeMessage = rai.getHomeMsg(0, LocalStore.getUserInfo().userId,(int) saving.getLong(LocalStore.PROPERTY_ID, 0)) ;
		if(homeMessage != null){
			mHandler.sendEmptyMessage(Constants.MSG_GET_INDEX_INFO_FINISH) ;
		} else {
			mHandler.sendEmptyMessage(Constants.MSG_GET_INDEX_INFO_NET_ERROR) ;
			return  ;
		}
		
	}
	
}
