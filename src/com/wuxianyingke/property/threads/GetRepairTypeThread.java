package com.wuxianyingke.property.threads;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.remote.RemoteApi;
import com.wuxianyingke.property.remote.RemoteApi.RepairType;
import com.wuxianyingke.property.remote.RemoteApiImpl;

import java.util.ArrayList;

/**
 * Created by mackcyl on 15/5/23.
 */
public class GetRepairTypeThread extends Thread{
    private final static String TAG = "GetRepairLogLastThread";
    private Context mContext;
    private Handler mHandler;
    private int propertyid;
    private ArrayList<RepairType> repairTypeList;

    public GetRepairTypeThread(Context context, Handler handler) {
        this.mContext = context;
        this.mHandler = handler;
    }

    public ArrayList<RepairType> getRepairTypeList(){
        return repairTypeList;
    }

    public void run(){
        RemoteApi rai = new RemoteApiImpl();
        SharedPreferences saving = mContext.getSharedPreferences(LocalStore.USER_INFO, 0);
        repairTypeList = rai.getRepairTypeList(mContext, LocalStore.getUserInfo().userId, (int) saving.getLong(LocalStore.PROPERTY_ID, 0));

        if(repairTypeList != null){
            mHandler.sendEmptyMessage(Constants.MSG_GET_REPAIR_TYPE_LIST_FINSH);
        }else{
            mHandler.sendEmptyMessage(Constants.MSG_GET_REPAIR_TYPE_LIST_ERROR);
            return ;
        }
    }
}
