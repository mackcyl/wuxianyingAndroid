package com.wuxianyingke.property.threads;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.wuxianyingke.property.activities.Radio2Activity;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LogUtil;
import com.wuxianyingke.property.remote.RemoteApi.ProductMessage;
import com.wuxianyingke.property.remote.RemoteApiImpl;


public class ProductMessageThread extends Thread
{
	private final static String TAG = "MyTag";
	private Handler handler;
	private Context ctx;
	private int count;
	private List<ProductMessage> mActivityList;

	private boolean running = true;

	public ProductMessageThread(Context ctx, Handler handler, int count)
	{
		this.handler = handler;
		this.ctx = ctx;
		this.count = count;
	}

	public synchronized void stopRun(){
		running = false;
		this.interrupt();

	}
	
	public List<ProductMessage> getActivitys()
	{
		return mActivityList;
	}

	public void run() 
	{
		try 
		{
			running = true;
			RemoteApiImpl remoteApi = new RemoteApiImpl();
			mActivityList = remoteApi.getproductMessage(ctx, count);
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
			msg.what = Constants.MSG_GET_PRODUCTMESSAGE_FINISH;
			handler.sendMessage(msg);
			
			if (!running) 
				return;
			Radio2Activity.setAllowGetPageAgain();
			
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
