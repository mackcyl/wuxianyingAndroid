package com.wuxianyingke.property.threads;

import java.io.IOException;
import java.util.ArrayList;

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
import com.wuxianyingke.property.remote.RemoteApi.PromotionList;
import com.wuxianyingke.property.remote.RemoteApiImpl;
import com.wuxianyingke.property.remote.RemoteApi.ProductDetailNew;

public class GetCanyinOwnListThread extends Thread {

	private Context mContext;
	private Handler mHandler;
	private PromotionList mActivityItem;
	private PromotionList mProductItem;
	private LivingItemPictureInfo mLivingItemPictureInfo;
	private boolean isRuning = true;
	private int fleaid;
	private int propertyid;
	private String source;
	public ArrayList<Drawable> imgDwList = new ArrayList<Drawable>();

	public GetCanyinOwnListThread(Context context, Handler handler,
			int mPropertyid,int mFleaid,String mSource) {
		this.mContext = context;
		this.mHandler = handler;
		this.fleaid = mFleaid;
		this.source = mSource;
		this.propertyid = mPropertyid;
		Log.d("MyTag","GetCanyinOwnListThread=fleaid="+fleaid+"/propertyid="+propertyid);
	}

	public PromotionList getProductDetail() {
		return mProductItem;
	}
	public PromotionList getActivityDetail() {
		return mActivityItem;
	}
	public Drawable getDrawable(int id) {
		return imgDwList.get(id);
	}
	public void stopRun() {
		isRuning = false;
	}

	public void run() {
		RemoteApiImpl rai = new RemoteApiImpl();
		mProductItem = rai.getProductByLivingItemId(mContext, fleaid);
		mActivityItem = rai.getActicityByLivingItemId(mContext, fleaid);
		if (!isRuning)
			return;
		if (mProductItem != null ) {
			mHandler.sendEmptyMessage(Constants.MSG_GET_PRODUCT_FINISH);
		} 
		
		if (mActivityItem != null ) {
			mHandler.sendEmptyMessage(Constants.MSG_GET_ACTIVITY_FINISH);
		} 

		int dwid = 0;
		
		if(mProductItem!= null )
		{
			for (int i = mProductItem.promotionList.size() - 1; i >= 0; --i) 
				
			{
				if (!isRuning)
					return;
				if (mProductItem.promotionList.get(i).Path != null) {
					Drawable dw = null;
					try {
						dw = Util.getDrawableFromCache(mContext,
								mProductItem.promotionList.get(i).Path);
					} catch (IOException e) {
						e.printStackTrace();
					}
					if (dw != null) {
						imgDwList.add(dw);
						Message msg = new Message();
						msg.what = Constants.MSG_GET_PRODUCT_IMG_FINISH;
						msg.arg1 = i;
						msg.arg2 = dwid;
						dwid++;
						mHandler.sendMessage(msg);
					}
				}
			}
		}
		
		if(mActivityItem!= null )
		{
			for (int i = mActivityItem.promotionList.size() - 1; i >= 0; --i) 
				
			{
				if (!isRuning)
					return;
				if (mActivityItem.promotionList.get(i).Path != null) {
					Drawable dw = null;
					try {
						dw = Util.getDrawableFromCache(mContext,
								mActivityItem.promotionList.get(i).Path);
					} catch (IOException e) {
						e.printStackTrace();
					}
					if (dw != null) {
						imgDwList.add(dw);
						Message msg = new Message();
						msg.what = Constants.MSG_GET_ACTIVITY_IMG_FINISH;
						msg.arg1 = i;
						msg.arg2 = dwid;
						dwid++;
						mHandler.sendMessage(msg);
					}
				}
			}
		}		
	}
}
