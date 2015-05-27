package com.wuxianyingke.property.threads;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LogUtil;
import com.wuxianyingke.property.remote.RemoteApi.MessageInfo;
import com.wuxianyingke.property.remote.RemoteApiImpl;


public class MessageOutBoxThread extends Thread
{
	private final static String TAG = "MyTag";
	private Handler handler;
	private Context ctx;
	private int pageIndex;
	private int propertyid;
	private long userId;
	private List<MessageInfo> mActivityList;

	private boolean running = true;

	public MessageOutBoxThread(Context ctx, Handler handler, int propertyid,long userId,int count)
	{
		this.handler = handler;
		this.ctx = ctx;
		this.propertyid = propertyid;
		this.userId = userId;
		this.pageIndex = count;
	}

	public synchronized void stopRun(){
		running = false;
		this.interrupt();

	}
	
	public List<MessageInfo> getActivitys()
	{
		return mActivityList;
	}

	public void run() 
	{
		try 
		{
			running = true;
			RemoteApiImpl remoteApi = new RemoteApiImpl();
			mActivityList = remoteApi.getMessageOutbox(ctx, propertyid, userId, pageIndex);
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
			msg.what = Constants.MSG_MESSAGE_OUT_BOX_FINISH;
			handler.sendMessage(msg);
			
			/*if (!running) 
				return;
			Radio3Activity.setAllowGetBoxAgain();*/
			
		} catch(Exception ex) {
			LogUtil.d(TAG, "ActivityThread::Run() error = "+ex.getMessage());
			ex.getMessage();
			if (!running) 
				return;
			Message msg = new Message();
			msg.what = Constants.MSG_NETWORK_ERROR;
			handler.sendMessage(msg);
		}
	}
}
