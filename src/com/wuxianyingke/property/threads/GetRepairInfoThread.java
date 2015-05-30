package com.wuxianyingke.property.threads;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.Util;
import com.wuxianyingke.property.remote.RemoteApi.RepairPicture;
import com.wuxianyingke.property.remote.RemoteApi.RepairLog;
import com.wuxianyingke.property.remote.RemoteApiImpl;

import java.io.IOException;
import java.util.ArrayList;

public class GetRepairInfoThread extends Thread {

	private Context mContext;
	private Handler mHandler;
	private ArrayList<RepairPicture> mRepairImgItem;
	private RepairLog mRepairLogItem;
	private boolean isRuning = true;
	private int propertyid;
	private long repairid ,userid;
	public ArrayList<Drawable> imgDwList = new ArrayList<Drawable>();

	public GetRepairInfoThread(Context context, Handler handler,
							   int mPropertyid, long mRepairid, long mUserid){
		this.mContext = context;
		this.mHandler = handler;
		this.userid = mUserid;
		this.repairid = mRepairid;
		this.propertyid = mPropertyid;
	}

	public ArrayList<RepairPicture> getRepairImgItem() {
		return mRepairImgItem;
	}
	public Drawable getDrawable(int id) {
		return imgDwList.get(id);
	}
	public void stopRun() {
		isRuning = false;
	}

	public void run() {
		RemoteApiImpl rai = new RemoteApiImpl();
		mRepairImgItem = rai.getRepairPicture(mContext, userid, repairid, propertyid);

		if (mRepairImgItem != null ) {
			mHandler.sendEmptyMessage(Constants.MSG_GET_REPAIR_PIC_LIST_FINSH);
		} 
		int dwid = 0;

		if(mRepairImgItem!= null )
		{
			for (int i = mRepairImgItem.size() - 1; i >= 0; --i)
				
			{
				if (!isRuning)
					return;
				if (mRepairImgItem.get(i).path != null) {
					Drawable dw = null;
					try {
						dw = Util.getDrawableFromCache(mContext,
								mRepairImgItem.get(i).path);
					} catch (IOException e) {
						e.printStackTrace();
					}
					if (dw != null) {
						imgDwList.add(dw);
						Message msg = new Message();
						msg.what = Constants.MSG_GET_REPAIR_PIC_FINSH;
						msg.arg1 = i;
						msg.arg2 = dwid;
						dwid++;
						mHandler.sendMessage(msg);
					}
				}
			}
		}		
	}
}
