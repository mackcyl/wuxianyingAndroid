package com.wuxianyingke.property.threads;

import java.io.IOException;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.Util;
import com.wuxianyingke.property.remote.RemoteApi.FleaContent;
import com.wuxianyingke.property.remote.RemoteApi.LivingItem;
import com.wuxianyingke.property.remote.RemoteApi.LivingItemPictureInfo;
import com.wuxianyingke.property.remote.RemoteApiImpl;
import com.wuxianyingke.property.remote.RemoteApi.ProductDetailNew;

public class GetCanyinDetailThread extends Thread {

	private Context mContext;
	private Handler mHandler;
	private LivingItem mLivingItem;
	private LivingItemPictureInfo mLivingItemPictureInfo;
	private boolean isRuning = true;
	private int fleaid;
	private int propertyid;
	private String source;

	public GetCanyinDetailThread(Context context, Handler handler,
			int mPropertyid,int mFleaid,String mSource) {
		this.mContext = context;
		this.mHandler = handler;
		this.fleaid = mFleaid;
		this.source = mSource;
		this.propertyid = mPropertyid;
		Log.d("MyTag","GetProductDetailThread=fleaid="+fleaid+"/propertyid="+propertyid);
	}

	public LivingItem getProductDetail() {
		return mLivingItem;
	}

	public void stopRun() {
		isRuning = false;
	}

	public void run() {
		RemoteApiImpl rai = new RemoteApiImpl();
		mLivingItem = rai.getLivingItemsByLivingItemId(mContext,fleaid,source,propertyid) ;
		mLivingItemPictureInfo=rai.getLivingItemsPictureByLivingItemId(mContext,fleaid,source,propertyid) ;
		if (!isRuning)
			return;
		if (mLivingItem != null ) {
			mHandler.sendEmptyMessage(Constants.MSG_GET_PRODUCT_DETAIL_FINISH);
		} else {
			mHandler.sendEmptyMessage(Constants.MSG_GET_PRODUCT_DETAIL_NET_ERROR);
			return;
		}
		
		int imgCount = mLivingItemPictureInfo.livingItemPicture.size() ;
		mLivingItem.livingItemPicture=mLivingItemPictureInfo.livingItemPicture;
		for (int i = imgCount - 1; i >= 0; --i) 
			
		{
			if (!isRuning)
				return;
			if (mLivingItemPictureInfo.livingItemPicture.get(i).path != null) {
				Drawable dw = null;
				try {
					dw = Util.getDrawableFromCache(mContext,
							mLivingItemPictureInfo.livingItemPicture.get(i).path);
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (dw != null) {
					mLivingItem.livingItemPicture.get(i).imgDw = dw;
					Message msg = new Message();
					msg.what = Constants.MSG_GET_PRODUCT_DETAIL_IMG_FINISH;
					msg.arg1 = i;
					mHandler.sendMessage(msg);
				}
			}
		}
	}
}
