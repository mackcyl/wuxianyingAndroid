package com.wuxianyingke.property.threads;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LogUtil;
import com.wuxianyingke.property.remote.RemoteApi.NetInfo;
import com.wuxianyingke.property.remote.RemoteApiImpl;

public class AddtoFavoriteThread extends Thread
{
	private final static String TAG = "MyTag";
	private Handler handler;
	private Context ctx;
	private NetInfo mErrorInfo;
	private boolean running = true;
	private long uid = 0;
	private long productId = 0;

	public AddtoFavoriteThread(Context ctx, Handler handler, long uid,long productId)
	{
		this.handler = handler;
		this.ctx = ctx;
		this.uid = uid;
		this.productId=productId;
	}

	public synchronized void stopRun()
	{
		running = false;
		this.interrupt();
	}
	
	public NetInfo getErrorInfo()
	{
		return mErrorInfo;
	}

	public void run() 
	{
		try 
		{	
		    running = true;
			RemoteApiImpl remoteApi = new RemoteApiImpl();
			mErrorInfo = remoteApi.addtoFavorite(ctx, uid, productId);
			if (mErrorInfo == null) 
			{
				Message msg = new Message();
				msg.what = Constants.MSG_FAVORITE_NETWORK_ERROR;
				handler.sendMessage(msg);
				return;
			}
			
			if (!running) 
				return;
			
			handler.sendMessage(handler.obtainMessage(Constants.MSG_ADD_FAVORITE_FINISH, mErrorInfo));
			
		} catch(Exception ex) {
			ex.printStackTrace();
			LogUtil.d(TAG, "ActivityThread::Run() error = "+ex.getMessage());
		}
	}
}
