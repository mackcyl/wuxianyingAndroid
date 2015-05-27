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
import com.wuxianyingke.property.remote.RemoteApi.FleaPicture;
import com.wuxianyingke.property.remote.RemoteApi.LivingItem;
import com.wuxianyingke.property.remote.RemoteApi.LivingItemInfo;
import com.wuxianyingke.property.remote.RemoteApi.LivingItemPicture;
import com.wuxianyingke.property.remote.RemoteApiImpl;

public class GetCanYinListThread extends Thread {
	private final static String TAG = "MyTag";
	private Context mContext;
	private Handler mHandler;
	private int mPageIndex;
	private int propertyid;
	private boolean running = true;
	private boolean isRunning = true;
    private String flag="0";
	private ArrayList<LivingItem> mProductList;

	public GetCanYinListThread(Context context, Handler handler,
			int propertyid,String flag, int pageIndex) {
		this.mContext = context;
		this.mHandler = handler;
		this.mPageIndex = pageIndex;
		this.propertyid = propertyid;
		this.flag=flag;
	}

	public synchronized void stopRun() {
		running = false;
		this.interrupt();

	}

	public ArrayList<LivingItem> getProductList() {
		return mProductList;
	}

	public void run() {
		try {
			running = true;
			RemoteApiImpl rai = new RemoteApiImpl();
			LivingItemInfo livingItemInfo = rai.getLivingItems(mContext,propertyid,flag , mPageIndex);

			if (livingItemInfo != null) {
				if (livingItemInfo.netInfo.code != 200
						&& livingItemInfo.netInfo.desc.equals("空数据")) {
					mHandler.sendEmptyMessage(Constants.MSG_GET_CANYIN_LIST_EMPTY);
					return;
				}
				if (!running)
					return;
				mProductList = livingItemInfo.livingItem;
				mHandler.sendEmptyMessage(Constants.MSG_GET_CANYIN_LIST_FINISH);
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
				LivingItemPicture pic = mProductList.get(i).FrontCover;
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
						mHandler.sendEmptyMessage(Constants.MSG_GET_CANYIN_DETAIL_IMG_FINISH);
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
