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
import com.wuxianyingke.property.remote.RemoteApiImpl;
import com.wuxianyingke.property.remote.RemoteApi.ProductDetailNew;

public class GetProductDetailThread extends Thread {

	private Context mContext;
	private Handler mHandler;
	private FleaContent mPd;
	private boolean isRuning = true;
	private Long fleaid;
	private int propertyid;
	public GetProductDetailThread(Context context, Handler handler,
			int mPropertyid,long mFleaid) {
		this.mContext = context;
		this.mHandler = handler;
		this.fleaid = mFleaid;
		this.propertyid = mPropertyid;
		Log.d("MyTag","GetProductDetailThread=fleaid="+fleaid+"/propertyid="+propertyid);
	}

	public FleaContent getProductDetail() {
		return mPd;
	}

	public void stopRun() {
		isRuning = false;
	}

	public void run() {
		RemoteApiImpl rai = new RemoteApiImpl();
		mPd = rai.getFleaContent(propertyid,fleaid) ;
		if (!isRuning)
			return;
		if (mPd != null && mPd.netInfo.code == 200) {
			mHandler.sendEmptyMessage(Constants.MSG_GET_PRODUCT_DETAIL_FINISH);
		} else {
			mHandler.sendEmptyMessage(Constants.MSG_GET_PRODUCT_DETAIL_NET_ERROR);
			return;
		}
		
		int imgCount = mPd.fleaPictureArray.size() ;

		for (int i = imgCount - 1; i >= 0; --i) 
			
		{
			if (!isRuning)
				return;
			if (mPd.fleaPictureArray.get(i).path != null) {
				Drawable dw = null;
				try {
					dw = Util.getDrawableFromCache(mContext,
							mPd.fleaPictureArray.get(i).path);
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (dw != null) {
					mPd.fleaPictureArray.get(i).imgDw = dw;
					Message msg = new Message();
					msg.what = Constants.MSG_GET_PRODUCT_DETAIL_IMG_FINISH;
					msg.arg1 = i;
					mHandler.sendMessage(msg);
				}
			}
		}
	}
}
