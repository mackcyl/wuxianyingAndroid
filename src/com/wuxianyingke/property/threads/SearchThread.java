package com.wuxianyingke.property.threads;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;

import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LogUtil;
import com.wuxianyingke.property.common.Util;
import com.wuxianyingke.property.remote.RemoteApi.ProductTopInfo;
import com.wuxianyingke.property.remote.RemoteApi.SearchResult;
import com.wuxianyingke.property.remote.RemoteApiImpl;

public class SearchThread extends Thread
{
	private Handler handler;
	private Context ctx;
	private SearchResult mSearchResult;
	private boolean running = true;
	private String key = "";
	private int type;
	private int pageIndex;
	private int pageSize;

	public SearchThread(Context ctx, Handler handler, String searchKey, int type, int pageIndex, int pageSize)
	{
		this.handler = handler;
		this.ctx = ctx;
		this.type = type;
		this.pageIndex = pageIndex;
		this.pageSize = pageSize;
		if(searchKey != null)
			this.key = searchKey;
	}

	public synchronized void stopRun()
	{
		running = false;
		this.interrupt();
	}
	
	public SearchResult getSearchResult()
	{
		return mSearchResult;
	}

	public void run() 
	{
		try 
		{
			running = true;
			RemoteApiImpl remoteApi = new RemoteApiImpl();
			mSearchResult = remoteApi.getSearchResult(ctx, key, type, pageIndex, pageSize);
			
			if (!running) 
				return;
			
			Message msg = new Message();
			msg.what = Constants.MSG_GET_SEARCH_RESULT_FINISH;
			handler.sendMessage(msg);
			
			if(mSearchResult == null)
				return;
			
			// 开始下载图片
			if(mSearchResult.productResultList != null)
				for(int i = 0; i < mSearchResult.productResultList.size(); i++) 
				{
					if (!running) 
					{
						LogUtil.d("MyTag", "SearchThread is stoped......");
						return;
					}
					ProductTopInfo info =  mSearchResult.productResultList.get(i);
					Drawable dw = Util.getShareDrawableFromCache(ctx, info.imageUrl);
					if (dw != null)
					{
						info.imageDrawable = dw;
						Message iconmsg = new Message();
						iconmsg.what = Constants.MSG_GET_SEARCH_ICON_FINISH;
						handler.sendMessage(iconmsg);
					}
				}
			if(mSearchResult.productGuessList != null)
				for(int i = 0; i < mSearchResult.productGuessList.size(); i++) 
				{
					if (!running) 
					{
						LogUtil.d("MyTag", "SearchThread is stoped......");
						return;
					}
					ProductTopInfo info =  mSearchResult.productGuessList.get(i);
					Drawable dw = Util.getShareDrawableFromCache(ctx, info.imageUrl);
					if (dw != null)
					{
						info.imageDrawable = dw;
						Message iconmsg = new Message();
						iconmsg.what = Constants.MSG_GET_SEARCH_ICON_FINISH;
						handler.sendMessage(iconmsg);
					}
				}
			if(mSearchResult.productSuggestList != null)
				for(int i = 0; i < mSearchResult.productSuggestList.size(); i++) 
				{
					if (!running) 
					{
						LogUtil.d("MyTag", "SearchThread is stoped......");
						return;
					}
					ProductTopInfo info =  mSearchResult.productSuggestList.get(i);
					Drawable dw = Util.getShareDrawableFromCache(ctx, info.imageUrl);
					if (dw != null)
					{
						info.imageDrawable = dw;
						Message iconmsg = new Message();
						iconmsg.what = Constants.MSG_GET_SEARCH_ICON_FINISH;
						handler.sendMessage(iconmsg);
					}
				}

		} catch(Exception ex) {
			LogUtil.d("MyTag", "SearchThread::Run() error = "+ex.getMessage());
			Message msg = new Message();
			msg.what = Constants.MSG_NETWORK_ERROR;
			handler.sendMessage(msg);
		}
	}
}
