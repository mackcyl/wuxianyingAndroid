package com.wuxianyingke.property.threads;

import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;

import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.Util;
import com.wuxianyingke.property.remote.RemoteApi.Flea;
import com.wuxianyingke.property.remote.RemoteApi.FleaInfo;
import com.wuxianyingke.property.remote.RemoteApi.FleaPicture;
import com.wuxianyingke.property.remote.RemoteApiImpl;
import com.wuxianyingke.property.remote.RemoteApi.ProductBase;
import com.wuxianyingke.property.remote.RemoteApi.ProductListRetInfo;
import com.wuxianyingke.property.remote.RemoteApi.ProductPic;

public class FleaOwnerThread extends Thread {

	private Context mContext ;
	private Handler mHandler ;
	private long mUserid ;
	private int propertyid ;
	
	private boolean isRunning = true ;
	
	private ArrayList<Flea> mProductList ;
	
	public FleaOwnerThread(Context context, Handler handler, int propertyid, long userid) {
		this.mContext = context ;
		this.mHandler = handler ;
		this.mUserid = userid ;
		this.propertyid = propertyid ;
	}

	public synchronized void stopRun() {
		this.interrupt();

	}
	
	public ArrayList<Flea> getProductList(){
		return mProductList ;
	}
	
	public void run(){
		RemoteApiImpl rai = new RemoteApiImpl() ;
		FleaInfo retInfo = rai.getFleaByOwner(propertyid, mUserid) ;

		if(retInfo != null){
			if(retInfo.netInfo.code != 200 && retInfo.netInfo.desc.equals("空数据")){
				mHandler.sendEmptyMessage(Constants.MSG_GET_PRODUCT_LIST_EMPTY) ;
				return ;
			}
			mProductList = retInfo.fleas ;
			mHandler.sendEmptyMessage(Constants.MSG_GET_PRODUCT_LIST_FINISH) ;
		} else {
			mHandler.sendEmptyMessage(Constants.MSG_GET_PRODUCT_LIST_NET_ERROR) ;
			return ;
		}

		int count = mProductList.size() ;
		for(int i = 0 ; i < count ; ++i){
			if(!isRunning)	return ;
			FleaPicture pic = mProductList.get(i).frontCover ;
			if(pic.path != null){
				Drawable dw = null ;
				try {
					dw = Util.getDrawableFromCache(mContext ,  pic.path) ;
				} catch (IOException e) {
					e.printStackTrace();
				}
				if(dw != null){
					pic.imgDw = dw ;
					mHandler.sendEmptyMessage(Constants.MSG_GET_CATE_IMG_FINISH) ;
				}
			}
		}
	}

}
