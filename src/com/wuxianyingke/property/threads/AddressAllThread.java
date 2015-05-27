package com.wuxianyingke.property.threads;

import java.util.List;

import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LogUtil;
import com.wuxianyingke.property.remote.RemoteApiImpl;
import com.wuxianyingke.property.remote.RemoteApi.AddressItem;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

public class AddressAllThread extends Thread {
	
	private final static String TAG = "MyTag";
	private Handler handler;
	private Context ctx;
	private List<AddressItem> mAddressList;
	private boolean running = true;
	private long uid =0;

	public AddressAllThread(Context ctx, Handler handler, long uid) {
		this.handler = handler;
		this.ctx = ctx;
		this.uid = uid;
	}

	public synchronized void stopRun() {
		running = false;
		this.interrupt();
	}

	public List<AddressItem> getAddressList() {
		return mAddressList;
	}

	public void run() {
		try {
			running = true;
			RemoteApiImpl remoteApi = new RemoteApiImpl();
			mAddressList = remoteApi.getAllAddress(ctx, uid);
			if (mAddressList == null) {
				Message msg = new Message();
				msg.what = Constants.MSG_NETWORK_ERROR;
				handler.sendMessage(msg);
				return;
			}

			if (!running)
				return;

			Message msg = new Message();
			msg.what = Constants.MSG_GET_ADDRESS_LIST_FINISH;
			handler.sendMessage(msg);

		} catch (Exception ex) {
			ex.printStackTrace();
			LogUtil.d(TAG, "AddressAllThread::Run() error = " + ex.getMessage());

			if (!running)
				return;
			Message msg = new Message();
			msg.what = Constants.MSG_NETWORK_ERROR;
			handler.sendMessage(msg);
		}
	}
}
