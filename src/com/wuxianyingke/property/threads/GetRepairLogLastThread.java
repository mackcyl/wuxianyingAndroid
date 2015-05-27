package com.wuxianyingke.property.threads;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.remote.RemoteApi;
import com.wuxianyingke.property.remote.RemoteApi.Repair;
import com.wuxianyingke.property.remote.RemoteApi.RepairLog;
import com.wuxianyingke.property.remote.RemoteApiImpl;

/**
 * Created by mackcyl on 15/5/20.
 */
public class GetRepairLogLastThread extends Thread {
    private final static String TAG = "GetRepairLogLastThread";
    private Context mContext;
    private Handler mHandler;
    private int propertyid;
    private RepairLog repairLogLast;

    public GetRepairLogLastThread(Context context, Handler handler) {
        this.mContext = context;
        this.mHandler = handler;
    }

    public RepairLog getRepairLogLast(){
        return repairLogLast;
    }

    public void run(){
        RemoteApi rai = new RemoteApiImpl();
        SharedPreferences saving = mContext.getSharedPreferences(LocalStore.USER_INFO, 0);
        repairLogLast = rai.getRepairLogLastest(mContext, LocalStore.getUserInfo().userId,(int)saving.getLong(LocalStore.PROPERTY_ID,0));

        if(repairLogLast != null){
            mHandler.sendEmptyMessage(Constants.MSG_GET_REPAIR_LOG_LAST_FINSH);
        }else{
            mHandler.sendEmptyMessage(Constants.MSG_GET_REPAIR_LOG_LAST_ERROR);
            return ;
        }
    }


}
