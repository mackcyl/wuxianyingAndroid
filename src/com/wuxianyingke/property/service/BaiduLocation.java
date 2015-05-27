package com.wuxianyingke.property.service;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.wuxianyingke.property.common.LogUtil;

public class BaiduLocation
{
    static final String TAG = "Location";
    public String cmd;

    private Context mContext;
    private Handler mHandler;
    private int mMsg;

    private AlarmManager mAlarmMgr;
    private Intent mIntent;
    private boolean mAlarmWorking = false;

    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();

    public BaiduLocation(Context context, Handler handler)
    {
        mContext = context;
        mHandler = handler;

        mAlarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public boolean isAlarmWorking()
    {
        LogUtil.d(TAG, "mAlarmWorking=" + mAlarmWorking);
        return mAlarmWorking;
    }

    public void getLocation(int msg)
    {
        LogUtil.d(TAG, "getLocation msg=" + msg);
        mMsg = msg;

        if (mLocationClient != null)
        {
            return;
        }
        else
        {
            LogUtil.d(TAG, "mLocationClient is null");
            mLocationClient = new LocationClient(mContext); // 声明LocationClient类
            mLocationClient.registerLocationListener(myListener); // 注册监听函数
        }

        setLocationOption();
        mLocationClient.start();
        mLocationClient.requestLocation();

        // if (mLocationClient.isStarted())
        // mLocationClient.requestLocation();
        // else
        // LogS.d(TAG, "mLocationClient is not started");
    }

    // 设置相关参数
    private void setLocationOption()
    {
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        // 定位模式分为两类：一次定位和定时定位。
        // 一次定位：用户程序调用requestLocation()后，定位SDK会立刻整合定位依据，发起网络请求，获取定位结果。定位结果通过BDLocationListener返回。
        // 定时定位：用户程序调用requestLocation()后，每隔设定的时间，整合定位依据，发起网络请求，获取定位结果。定位结果通过BDLocationListener返回。
        // 在setLocationOption方法中，设置的定时定位间隔超过1000ms，即为定时定位模式；如果不设定时间隔，或者时间间隔小于1000ms的话，认为是一次定位
        option.setScanSpan(1);
        mLocationClient.setLocOption(option);
    }

    public void setAlarm(int second)
    {
        LogUtil.d(TAG, "setAlarm second =" + second);

        mAlarmWorking = true;

        int requestCode = 0;
        PendingIntent pendIntent = PendingIntent.getService(mContext.getApplicationContext(), requestCode, mIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mAlarmMgr.cancel(pendIntent);

        int interval = second * 1000;
        int time = (int) (SystemClock.elapsedRealtime() + interval);
        mAlarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, time, interval, pendIntent);
    }

    public void cancelAlarm()
    {
        LogUtil.d(TAG, "cancelAlarm");

        mAlarmWorking = false;

        PendingIntent pendIntent = PendingIntent.getService(mContext.getApplicationContext(), 0, mIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        // 与上面的intent匹配（filterEquals(intent)）的闹钟会被取消
        mAlarmMgr.cancel(pendIntent);
    }

    public class MyLocationListener implements BDLocationListener
    {
        @Override
        public void onReceiveLocation(BDLocation location)
        {
            LogUtil.d(TAG, "onReceiveLocation");

            if (location != null)
            {
                SendLocation info = new SendLocation();
                info.latitude = location.getLatitude();
                info.longitude = location.getLongitude();

                Message msg = new Message();
                msg.what = mMsg;
                msg.obj = info;
                mHandler.sendMessage(msg);
            }

            mLocationClient.unRegisterLocationListener(myListener);
            mLocationClient.stop();
            mLocationClient = null;
        }

        public void onReceivePoi(BDLocation poiLocation)
        {
            if (poiLocation == null)
            {
                return;
            }
        }
    }
}