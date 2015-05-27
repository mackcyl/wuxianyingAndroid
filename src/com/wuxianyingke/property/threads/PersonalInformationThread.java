package com.wuxianyingke.property.threads;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LogUtil;
import com.wuxianyingke.property.remote.RemoteApi.PersonalInformation;
import com.wuxianyingke.property.remote.RemoteApiImpl;

public class PersonalInformationThread extends Thread {
	private final static String TAG = "MyTag";
	private Handler handler;
	private Context ctx;
	private PersonalInformation mPersonalInformation = null;
	private boolean running = true;
	private long muserid;

	public PersonalInformationThread(Context ctx, Handler handler, long userid) {
		this.handler = handler;
		this.ctx = ctx;
		this.muserid = userid;
	}

	public synchronized void stopRun() {
		running = false;
		this.interrupt();
	}

	public PersonalInformation getPersonalInformation() {
		return mPersonalInformation;
	}

	public void run() {
		try {
			running = true;
			RemoteApiImpl remoteApi = new RemoteApiImpl();
			
			mPersonalInformation = remoteApi.getPersonalInformation(ctx, muserid);
			if (mPersonalInformation == null) {
				Message msg = new Message();
				msg.what = Constants.MSG_NETWORK_ERROR;
				handler.sendMessage(msg);
				return;
			}

			if (!running)
				return;

			Message msg = new Message();
			msg.what = Constants.MSG_GET_PERSONALINFOMATION_FINISH;
			handler.sendMessage(msg);

		} catch (Exception ex) {
			Message msg = new Message();
			msg.what = Constants.MSG_NETWORK_ERROR;
			handler.sendMessage(msg);
			ex.printStackTrace();
			LogUtil.d(TAG, "ActivityThread::Run() error = " + ex.getMessage());
		}
	}
}
