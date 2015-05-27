package com.wuxianyingke.property.threads;

import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;

import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.Util;
import com.wuxianyingke.property.remote.RemoteApiImpl;
import com.wuxianyingke.property.remote.RemoteApi.TopicsProduct;

public class GetTopicsListThread extends Thread {

	private Context mContext ;
	private Handler mHandler ;
	private String mTopicsId ;
	
	private boolean isRunning = true ;
	
	private ArrayList<TopicsProduct> mProductList ;
	
	public GetTopicsListThread(Context context, Handler handler, String topicsId) {
		this.mContext = context ;
		this.mHandler = handler ;
		this.mTopicsId = topicsId ;
	}
	
	public ArrayList<TopicsProduct> getProductList(){
		return mProductList ;
	}
	
	public void run(){
		RemoteApiImpl rai = new RemoteApiImpl() ;
		mProductList = rai.getTopicsList(mTopicsId) ;
		if(mProductList != null){
			mHandler.sendEmptyMessage(Constants.MSG_GET_TOPICS_LIST_FINISH) ;
		} else {
			mHandler.sendEmptyMessage(Constants.MSG_GET_TOPICS_LIST_NET_ERROR) ;
			return ;
		}
		
		int count = mProductList.size() ;
		for(int i = 0 ; i < count ; ++i){
			if(!isRunning)	return ;
			TopicsProduct pi = mProductList.get(i) ;
			if(pi.imageUrl != null){
				Drawable dw = null ;
				try {
					dw = Util.getDrawableFromCache(mContext , pi.imageUrl) ;
				} catch (IOException e) {
					e.printStackTrace();
				}
				if(dw != null){
					pi.imageDrawable = dw ;
					mHandler.sendEmptyMessage(Constants.MSG_GET_TOPICS_LIST_IMG_FINISH) ;
				}
			}
		}
	}

}
