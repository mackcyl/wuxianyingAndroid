package com.wuxianyingke.property.threads;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LogUtil;
import com.wuxianyingke.property.remote.RemoteApi.ErrorInfo;
import com.wuxianyingke.property.remote.RemoteApiImpl;

public class SetDefaultAddressThread extends Thread
{
	private final static String TAG = "MyTag";
	private Handler handler;
	private Context ctx;
	private ErrorInfo mErrorInfo;
	private boolean running = true;
	private long uid = 0;
	private String addressid="";

	public SetDefaultAddressThread(Context ctx, Handler handler, long uid,String addressid)
	{
		this.handler = handler;
		this.ctx = ctx;
		this.uid = uid;
		this.addressid=addressid;
	}

	public synchronized void stopRun()
	{
		running = false;
		this.interrupt();
	}
	
	public ErrorInfo getErrorInfo()
	{
		return mErrorInfo;
	}

	public void run() 
	{
		try 
		{	
		    running = true;
			RemoteApiImpl remoteApi = new RemoteApiImpl();
			mErrorInfo = remoteApi.setDefaultAddress(ctx, uid, addressid);
			/*remoteApi.loginByMobile(ctx, "13556565658");
			remoteApi.registerByMobile(ctx, "13556565658");
			
			remoteApi.getCheckCode(ctx, "13556565658",0);*/
			if (mErrorInfo == null) 
			{
				Message msg = new Message();
				msg.what = Constants.MSG_SET_DEFALUT_ADDRESS_NETWORK_ERROR;
				handler.sendMessage(msg);
				return;
			}
			
			if (!running) 
				return;
			
			handler.sendMessage(handler.obtainMessage(Constants.MSG_SET_DEFALUT_ADDRESS_FINISH, mErrorInfo));
			
		} catch(Exception ex) {
			Message msg = new Message();
			msg.what = Constants.MSG_SET_DEFALUT_ADDRESS_NETWORK_ERROR;
			handler.sendMessage(msg);
			ex.printStackTrace();
			LogUtil.d(TAG, "ActivityThread::Run() error = "+ex.getMessage());
		}
	}
}
