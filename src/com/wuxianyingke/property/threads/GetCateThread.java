package com.wuxianyingke.property.threads;

import java.util.ArrayList;

import android.content.Context;
import android.os.Handler;

import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.remote.RemoteApiImpl;
import com.wuxianyingke.property.remote.RemoteApi.ProductCaategoryRetInfo;
import com.wuxianyingke.property.remote.RemoteApi.ProductCategory;

public class GetCateThread extends Thread {
	private Context mContext;
	private Handler mHandler;
	private Long cId ;
	private boolean isRunning ;

	private ArrayList<ProductCategory> mCateList ;
	
	public GetCateThread(Context context, Handler handler, Long cId) {
		mContext = context ;
		mHandler = handler ;
		this.cId = cId ;
	}
	
	public ArrayList<ProductCategory> getCateList(){
		return mCateList ;
	}
	
	public void run(){
		RemoteApiImpl rai = new RemoteApiImpl() ;
		ProductCaategoryRetInfo retInfo = null ;
		if(-1 == cId){
			retInfo = rai.getRootCategory() ;
		} else {
			retInfo = rai.getChildCategory(cId) ;
		}
		if(retInfo != null){
			mCateList = retInfo.list ;
			mHandler.sendEmptyMessage(Constants.MSG_GET_CATE_FINISH) ;
		} else {
			mHandler.sendEmptyMessage(Constants.MSG_GET_CATE_NET_ERROR) ;
			return ;
		}
		/*
		int count = mCateList.size() ;
		
		for(int i = 0 ; i < count ; ++i){
			if(!isRunning)	return ;
			ProductCategory cate = mCateList.get(i) ;
			if(cate.imgUrl != null && !cate.imgUrl.equals("")){
				Drawable dw = null ;
				try {
					dw = Util.getDrawableFromCache(mContext , cate.imgUrl) ;
				} catch (IOException e) {
					e.printStackTrace();
				}
				if(dw != null){
					cate.imgDrawable = dw ;
					mHandler.sendEmptyMessage(Constants.MSG_GET_CATE_IMG_FINISH) ;
				}
			}
		}*/
	}
}
