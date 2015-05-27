package com.wuxianyingke.property.threads;

import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LogUtil;
import com.wuxianyingke.property.common.Util;
import com.wuxianyingke.property.remote.RemoteApi.Flea;
import com.wuxianyingke.property.remote.RemoteApi.FleaInfo;
import com.wuxianyingke.property.remote.RemoteApi.FleaPicture;
import com.wuxianyingke.property.remote.RemoteApiImpl;
import com.wuxianyingke.property.remote.RemoteApi.ProductBase;
import com.wuxianyingke.property.remote.RemoteApi.ProductListRetInfo;
import com.wuxianyingke.property.remote.RemoteApi.ProductPic;

public class GetProductListThread extends Thread {
	private final static String TAG = "MyTag";
	private Context mContext;
	private Handler mHandler;
	private int mPageIndex;
	private int propertyid;
	private boolean running = true;
	private boolean isRunning = true;

	private ArrayList<Flea> mProductList;

	public GetProductListThread(Context context, Handler handler,
			int propertyid, int pageIndex) {
		this.mContext = context;
		this.mHandler = handler;
		this.mPageIndex = pageIndex;
		this.propertyid = propertyid;
	}

	public synchronized void stopRun() {
		running = false;
		this.interrupt();

	}

	public ArrayList<Flea> getProductList() {
		return mProductList;
	}

	public void run() {
		try {
			running = true;
			RemoteApiImpl rai = new RemoteApiImpl();
			FleaInfo retInfo = rai.getFleaByProperty(propertyid, mPageIndex);

			if (retInfo != null) {
				if (retInfo.netInfo.code != 200
						&& retInfo.netInfo.desc.equals("空数据")) {
					mHandler.sendEmptyMessage(Constants.MSG_GET_PRODUCT_LIST_EMPTY);
					return;
				}
				if (!running)
					return;
				mProductList = retInfo.fleas;
				mHandler.sendEmptyMessage(Constants.MSG_GET_PRODUCT_LIST_FINISH);
			} else {
				if (!running)
					return;
				mHandler.sendEmptyMessage(Constants.MSG_NETWORK_ERROR);
				return;
			}

			int count = mProductList.size();
			for (int i = 0; i < count; ++i) {
				if (!isRunning)
					return;
				FleaPicture pic = mProductList.get(i).frontCover;
				if (pic.path != null) {
					Drawable dw = null;
					try {
						dw = Util.getDrawableFromCache(mContext, pic.path);
						Log.d("MyTag", "Constants.URL pic.path/"+pic.path);
					} catch (IOException e) {
						e.printStackTrace();
					}
					if (dw != null) {
						pic.imgDw = dw;
						mHandler.sendEmptyMessage(Constants.MSG_GET_PRODUCT_DETAIL_IMG_FINISH);
					}
				}
			}
		} catch (Exception ex) {
			LogUtil.d(TAG, "ActivityThread::Run() error = " + ex.getMessage());
			ex.getMessage();
			if (!running)
				return;
			Message msg = new Message();
			msg.what = Constants.MSG_NETWORK_ERROR;
			mHandler.sendMessage(msg);
		}
	}

}
