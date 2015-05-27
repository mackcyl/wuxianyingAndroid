package com.wuxianyingke.property.threads;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LogUtil;
import com.wuxianyingke.property.remote.RemoteApi.ExpressService;
import com.wuxianyingke.property.remote.RemoteApiImpl;


public class PropertyCollectionThread extends Thread
{
	private final static String TAG = "MyTag";
	private Handler handler;
	private Context ctx;
	private int count;
	private int propertyid;
	private long userId;
	private List<ExpressService> mActivityList;

	private boolean running = true;

	public PropertyCollectionThread(Context ctx, Handler handler, int propertyid,long userId,int count)
	{
		this.handler = handler;
		this.ctx = ctx;
		this.propertyid = propertyid;
		this.userId=userId;
		this.count = count;
	}

	public synchronized void stopRun(){
		running = false;
		this.interrupt();

	}
	
	public List<ExpressService> getActivitys()
	{
		return mActivityList;
	}

	public void run() 
	{
		try 
		{
			running = true;
			RemoteApiImpl remoteApi = new RemoteApiImpl();
			mActivityList = remoteApi.getPropertyCollection(ctx, propertyid,userId,count);
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
			msg.what = Constants.MSG_PROPERTY_COLLECTION_FINISH;
			handler.sendMessage(msg);
			
			/*if (!running) 
				return;
			Radio4Activity.setAllowGetPageAgain();*/
			
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
