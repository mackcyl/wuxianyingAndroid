package com.wuxianyingke.property.threads;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LogUtil;
import com.wuxianyingke.property.remote.RemoteApi.NetInfo;
import com.wuxianyingke.property.remote.RemoteApi.PersonalInformation;
import com.wuxianyingke.property.remote.RemoteApiImpl;

public class PersonalInformationUpdateThread extends Thread {
	private final static String TAG = "MyTag";
	private Handler handler;
	private Context ctx;
	private PersonalInformation mPersonalInformation;
	private NetInfo mNetInfo;
	private boolean running = true;
	private long muserid;

	public PersonalInformationUpdateThread(Context ctx, Handler handler,
			long userid, PersonalInformation mPersonalInformation) {
		this.handler = handler;
		this.ctx = ctx;
		this.muserid = userid;
		this.mPersonalInformation = mPersonalInformation;
	}

	public synchronized void stopRun() {
		running = false;
		this.interrupt();
	}

	public NetInfo getNetInfo() {
		return mNetInfo;
	}

	public void run() {
		try {
			running = true;
			RemoteApiImpl remoteApi = new RemoteApiImpl();

			mNetInfo = remoteApi.updatePersonalInformation(ctx, muserid,
					mPersonalInformation);
			if (mNetInfo == null) {
				Message msg = new Message();
				msg.what = Constants.MSG_NETWORK_ERROR;
				handler.sendMessage(msg);
				return;
			}

			if (!running)
				return;

			Message msg = new Message();
			msg.what = Constants.MSG_UPADTE_PERSONALINFOMATION_FINISH;
			handler.sendMessage(msg);

		} catch (Exception ex) {
			ex.printStackTrace();
			LogUtil.d(TAG, "ActivityThread::Run() error = " + ex.getMessage());
		}
	}
}
