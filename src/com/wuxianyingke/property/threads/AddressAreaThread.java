package com.wuxianyingke.property.threads;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LogUtil;
import com.wuxianyingke.property.remote.RemoteApi.Area;
import com.wuxianyingke.property.remote.RemoteApiImpl;

public class AddressAreaThread extends Thread {
	private final static String TAG = "MyTag";
	private Handler handler;
	private Context ctx;
	private List<Area> mAddressAreaList = new ArrayList<Area>();
	private boolean running = true;
	private String mAreaId = "";
	private int mType;

	public AddressAreaThread(Context ctx, Handler handler, String aredid,
			int type) {
		this.handler = handler;
		this.ctx = ctx;
		this.mAreaId = aredid;
		this.mType = type;
	}

	public synchronized void stopRun() {
		running = false;
		this.interrupt();
	}

	public List<Area> getAddressAreaList() {
		return mAddressAreaList;
	}

	public void run() {
		try {
			running = true;
			mAddressAreaList.clear();
			RemoteApiImpl remoteApi = new RemoteApiImpl();
			mAddressAreaList = remoteApi.getAreaList(ctx, mAreaId, mType);
			if (mAddressAreaList == null) {
				Message msg = new Message();
				msg.what = Constants.MSG_NETWORK_ERROR;
				handler.sendMessage(msg);
				return;
			}

			if (!running)
				return;

			Message msg = new Message();
			msg.what = Constants.MSG_GET_AREA_LIST_FINISH;
			handler.sendMessage(msg);

		} catch (Exception ex) {
			ex.printStackTrace();
			LogUtil.d(TAG, "ActivityThread::Run() error = " + ex.getMessage());
		}
	}
}
