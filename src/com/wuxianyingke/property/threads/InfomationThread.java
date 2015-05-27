package com.wuxianyingke.property.threads;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LogUtil;
import com.wuxianyingke.property.remote.RemoteApi.InformationsInfo;
import com.wuxianyingke.property.remote.RemoteApiImpl;


public class InfomationThread extends Thread
{
	private final static String TAG = "MyTag";
	private Handler handler;
	private Context ctx;
	private int propertyid;
	private List<InformationsInfo> mActivityList;

	private boolean running = true;

	public InfomationThread(Context ctx, Handler handler, int propertyid)
	{
		this.handler = handler;
		this.ctx = ctx;
		this.propertyid = propertyid;
	}

	public synchronized void stopRun(){
		running = false;
		this.interrupt();

	}
	
	public List<InformationsInfo> getActivitys()
	{
		return mActivityList;
	}

	public void run() 
	{
		try 
		{
			running = true;
			RemoteApiImpl remoteApi = new RemoteApiImpl();
			mActivityList = remoteApi.getInformations(ctx,propertyid);
			if (mActivityList == null) 
			{
				if (!running) 
					return;
				Message msg = new Message();
				msg.what = Constants.MSG_NETWORK_ERROR;
				handler.sendMessage(msg);
				return;
			}
			
			if (!running) 
				return;
			Message msg = new Message();
			msg.what = Constants.MSG_GET_INFOMATION_FINISH;
			handler.sendMessage(msg);
			
			if (!running) 
				return;
			
		} catch(Exception ex) {
			LogUtil.d(TAG, "ActivityThread::Run() error = "+ex.getMessage());
			if (!running) 
				return;
			Message msg = new Message();
			msg.what = Constants.MSG_NETWORK_ERROR;
			handler.sendMessage(msg);
		}
	}
}
