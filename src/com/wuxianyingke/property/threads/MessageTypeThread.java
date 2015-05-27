package com.wuxianyingke.property.threads;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LogUtil;
import com.wuxianyingke.property.remote.RemoteApi.MessageTypeInfo;
import com.wuxianyingke.property.remote.RemoteApiImpl;

public class MessageTypeThread extends Thread {
	private final static String TAG = "MyTag";
	private Handler handler;
	private Context ctx;
	private List<MessageTypeInfo> mMessageTypeList = new ArrayList<MessageTypeInfo>();
	private boolean running = true;
	private String mAreaId = "";
	private int propertyId;

	public MessageTypeThread(Context ctx, Handler handler, 
			int propertyId) {
		this.handler = handler;
		this.ctx = ctx;
		this.propertyId = propertyId;
	}

	public synchronized void stopRun() {
		running = false;
		this.interrupt();
	}

	public List<MessageTypeInfo> getMessageTypeList() {
		return mMessageTypeList;
	}

	public void run() {
		try {
			running = true;
			mMessageTypeList.clear();
			RemoteApiImpl remoteApi = new RemoteApiImpl();
			mMessageTypeList = remoteApi.getMessageType(ctx, propertyId);
			if (mMessageTypeList == null) {
				Message msg = new Message();
				msg.what = Constants.MSG_NETWORK_ERROR;
				handler.sendMessage(msg);
				return;
			}

			if (!running)
				return;

			Message msg = new Message();
			msg.what = Constants.MSG_GET_MESSAGE_TYPE_FINISH;
			handler.sendMessage(msg);

		} catch (Exception ex) {
			LogUtil.d(TAG, "ActivityThread::Run() error = " + ex.getMessage());
		}
	}
}
