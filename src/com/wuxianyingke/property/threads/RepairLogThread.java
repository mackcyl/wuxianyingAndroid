package com.wuxianyingke.property.threads;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LogUtil;
import com.wuxianyingke.property.remote.RemoteApi.RepairLog;
import com.wuxianyingke.property.remote.RemoteApi.Repair;
import com.wuxianyingke.property.remote.RemoteApiImpl;

import java.util.List;


public class RepairLogThread extends Thread
{
	private final static String TAG = "RepairLogThread";
	private Handler handler;
	private Context ctx;
	private int propertyid;
	private long repairId,userId;
	private List<RepairLog> mActivityList;

	private boolean running = true;

	public RepairLogThread(Context ctx, Handler handler, int propertyid, long userId, long repairId)
	{
		this.handler = handler;
		this.ctx = ctx;
		this.propertyid = propertyid;
		this.userId = userId;
		this.repairId = repairId;
	}

	public synchronized void stopRun(){
		running = false;
		this.interrupt();

	}
	
	public List<RepairLog> getActivitys()
	{
		return mActivityList;
	}

	public void run() 
	{
		try 
		{
			running = true;
			RemoteApiImpl remoteApi = new RemoteApiImpl();
			mActivityList = remoteApi.getRepairLog(ctx, userId, repairId, propertyid);
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
			msg.what = Constants.MSG_GET_REPAIR_DETAIL_FINSH;
			handler.sendMessage(msg);

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
