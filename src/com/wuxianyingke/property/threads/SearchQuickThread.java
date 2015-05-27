package com.wuxianyingke.property.threads;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LogUtil;
import com.wuxianyingke.property.remote.RemoteApi.QuickSearchResult;
import com.wuxianyingke.property.remote.RemoteApiImpl;

public class SearchQuickThread extends Thread
{
	private Handler handler;
	private Context ctx;
	private QuickSearchResult mQuickResult;
	private boolean running = true;
	private String key = "";

	public SearchQuickThread(Context ctx, Handler handler, String searchKey)
	{
		this.handler = handler;
		this.ctx = ctx;
		if(searchKey != null)
			this.key = searchKey;
	}

	public synchronized void stopRun()
	{
		running = false;
		this.interrupt();
	}
	
	public QuickSearchResult getSearchResult()
	{
		return mQuickResult;
	}

	public void run() 
	{
		try 
		{
			running = true;
			RemoteApiImpl remoteApi = new RemoteApiImpl();
			mQuickResult = remoteApi.getQuickSearchResult(ctx, key);
			
			if (!running) 
				return;
			
			Message msg = new Message();
			msg.what = Constants.MSG_GET_QUICK_SEARCH_FINISH;
			handler.sendMessage(msg);

		} catch(Exception ex) {
			LogUtil.d("MyTag", "SearchQuickThread::Run() error = "+ex.getMessage());
		}
	}
}
