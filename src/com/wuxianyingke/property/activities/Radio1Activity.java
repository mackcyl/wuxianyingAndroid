package com.wuxianyingke.property.activities;

import java.util.*;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Gallery.LayoutParams;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.mantoto.property.R;
import com.wuxianyingke.gaode.RouteActivity;
import com.wuxianyingke.property.adapter.IndexImageAdapter;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.common.LogUtil;
import com.wuxianyingke.property.common.Update;
import com.wuxianyingke.property.remote.RemoteApi;
import com.wuxianyingke.property.remote.RemoteApiImpl;
import com.wuxianyingke.property.remote.RemoteApi.HomeMessage;
import com.wuxianyingke.property.remote.RemoteApi.WeatherInfo;
import com.wuxianyingke.property.remote.RemoteApi.Repair;
import com.wuxianyingke.property.remote.RemoteApi.RepairLog;
import com.wuxianyingke.property.service.SendLocation;
import com.wuxianyingke.property.threads.GetIndexInfoThread;
import com.wuxianyingke.property.threads.GetRepairLogLastThread;
import com.wuxianyingke.property.views.IndicationDotList;
import com.wuxianyingke.property.views.MyGallery;

public class Radio1Activity extends Activity
{
	static final String TAG = "Radio1Activity";

	private final static int SCANNIN_GREQUEST_CODE = 1024;

	private GridView mGridView;

	private ProgressDialog mWaitLoading = null ;
	private IndicationDotList mDotList = null;
	private GetIndexInfoThread mThread=null ;
	private GetRepairLogLastThread rThread = null;
	private MyGallery mGallery=null ;
	private IndexImageAdapter mGalleryAdapter = null ;

	private long tempUserId = -1 ;
	private String tempUserName;

	private HomeMessage mHomeMessage ;
	private RepairLog repairLogLast;
	private TextView topbar_txt;
	private Button logoutBtn;

	private ImageView mLuKuangImage;
	private LocationClient mLocClient;
	private static SendLocation info = new SendLocation();
	private TextView DateTextView,Date_day_TextView,Date_weekday_TextView,DiZhiTextView,WenduTextView,TianQiTextView ,IndexRepairLog;
	private ImageView TianqiImageView,LuKuangImage;
	public MyLocationListenner myListener = new MyLocationListenner();

	private int[] imageRes = {
			R.drawable.style_tongzhi,
			R.drawable.style_progress,
			R.drawable.style_sign_qrcode,
			R.drawable.style_property,
			R.drawable.style_repair
//			R.drawable.style_my_task
	};

	// 定义标题数组
	private int[] itemName = {
			R.string.gridview_tongzhi,
			R.string.gridview_progress,
			R.string.gridview_sign_qrcode,
			R.string.gridview_property,
			R.string.gridview_repair,
//			R.string.gridview_my_task
	};

	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case Constants.MSG_GET_INDEX_INFO_FINISH:
				if(mWaitLoading != null && mWaitLoading.isShowing())	
					mWaitLoading.dismiss() ;
				if(mThread != null && mThread.getAllIndexInfo() != null){
					mHomeMessage = mThread.getAllIndexInfo() ;
					for (int i = 0; i < mHomeMessage.notes.size(); ++i) {
						mGalleryAdapter.addImg(mHomeMessage.notes.get(i));
						
					}
					mDotList.setCount(mHomeMessage.notes.size());
					mGalleryAdapter.notifyDataSetChanged();
				}
				LocalStore.setIsVisitor(Radio1Activity.this,mHomeMessage.isVisitor);
				break ;
			case Constants.MSG_GET_INDEX_INFO_NET_ERROR:
				if(mWaitLoading != null && mWaitLoading.isShowing())	
					mWaitLoading.dismiss() ;
				
				View view = (View) findViewById(R.id.view_network_error);
				view.setVisibility(View.VISIBLE);
				break ;
			case Constants.MSG_GET_REPAIR_LOG_LAST_FINSH:
				if(rThread != null && rThread.getRepairLogLast() != null){
					repairLogLast = rThread.getRepairLogLast() ;

					IndexRepairLog = (TextView)findViewById(R.id.index_repair_log);
					IndexRepairLog.setText(repairLogLast.displayContent);
					LogUtil.d(TAG,"repairLogLast.displayContent = "+repairLogLast.displayContent);
				}




				break;
			case Constants.MSG_GET_REPAIR_LOG_LAST_ERROR:
				break;
			case 3:
				initTopBar();
				break ;
			}
		}
	};
	public void initTopBar()
	{	
		DateTextView = (TextView) findViewById(R.id.DateTextView);
		Date_day_TextView = (TextView) findViewById(R.id.Date_day_TextView);
		Date_weekday_TextView = (TextView) findViewById(R.id.Date_weekday_TextView);
		DiZhiTextView = (TextView) findViewById(R.id.DiZhiTextView);
		WenduTextView = (TextView) findViewById(R.id.WenduTextView);
		TianQiTextView = (TextView) findViewById(R.id.TianQiTextView);
		TianqiImageView = (ImageView) findViewById(R.id.TianqiImageView);

		

		final Calendar c = Calendar.getInstance();
		c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
		String mYear = String.valueOf(c.get(Calendar.YEAR)); // 获取当前年份
		String mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
		String mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码
		String mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
		if("1".equals(mWay)){
			mWay ="日";
		}else if("2".equals(mWay)){
			mWay ="一";
		}else if("3".equals(mWay)){
			mWay ="二";
		}else if("4".equals(mWay)){
			mWay ="三";
		}else if("5".equals(mWay)){
			mWay ="四";
		}else if("6".equals(mWay)){
			mWay ="五";
		}else if("7".equals(mWay)){
			mWay ="六";
		}
		
		DateTextView.setText(mYear+"."+mMonth);
		Date_day_TextView.setText(mDay);
		Date_weekday_TextView.setText("星期"+mWay);


		DiZhiTextView.setText(LocalStore.cityInfo.city_name);
		WenduTextView.setText(LocalStore.weatherInfo.temp1);
		TianQiTextView.setText(LocalStore.weatherInfo.fl1);
		
		int rid = R.drawable.qing;

		if (LocalStore.weatherInfo.img_title_single.equals("特大暴雨")) {
			rid = R.drawable.baoyu;
		} else if (LocalStore.weatherInfo.img_title_single.equals("大暴雨")) {
			rid = R.drawable.baoyu;
		} else if (LocalStore.weatherInfo.img_title_single.equals("暴雨")) {
			rid = R.drawable.baoyu;
		} else if (LocalStore.weatherInfo.img_title_single.equals("大雨")) {
			rid = R.drawable.baoyu;
		} else if (LocalStore.weatherInfo.img_title_single.equals("中雨")) {
			rid = R.drawable.baoyu;
		} else if (LocalStore.weatherInfo.img_title_single.equals("小雨")) {
			rid = R.drawable.xiaoyu;
		} else if (LocalStore.weatherInfo.img_title_single.equals("雷阵雨")) {
			rid = R.drawable.leizhenyu;
		} else if (LocalStore.weatherInfo.img_title_single.equals("阵雨")) {
			rid = R.drawable.zhenyu;
		} else if (LocalStore.weatherInfo.img_title_single.equals("冻雨")) {
			rid = R.drawable.yujiaxue;
		} else if (LocalStore.weatherInfo.img_title_single.equals("雨夹雪")) {
			rid = R.drawable.yujiaxue;
		} else if (LocalStore.weatherInfo.img_title_single.equals("暴雪")) {
			rid = R.drawable.baoxue;
		} else if (LocalStore.weatherInfo.img_title_single.equals("阵雪")) {
			rid = R.drawable.zhenxue;
		} else if (LocalStore.weatherInfo.img_title_single.equals("大雪")) {
			rid = R.drawable.zhenxue;
		} else if (LocalStore.weatherInfo.img_title_single.equals("中雪")) {
			rid = R.drawable.zhongxue;
		} else if (LocalStore.weatherInfo.img_title_single.equals("小雪")) {
			rid = R.drawable.xiaoxue;
		} else if (LocalStore.weatherInfo.img_title_single.equals("霾")) {
			rid = R.drawable.wumai;
		} else if (LocalStore.weatherInfo.img_title_single.equals("强沙尘暴")) {
			rid = R.drawable.qiangshacenbao;
		} else if (LocalStore.weatherInfo.img_title_single.equals("沙尘暴")) {
			rid = R.drawable.shachenbao;
		} else if (LocalStore.weatherInfo.img_title_single.equals("扬沙")) {
			rid = R.drawable.yangsha;
		} else if (LocalStore.weatherInfo.img_title_single.equals("浮尘")) {
			rid = R.drawable.yangsha;
		} else if (LocalStore.weatherInfo.img_title_single.equals("雾")) {
			rid = R.drawable.zhongwu;
		} else if (LocalStore.weatherInfo.img_title_single.equals("阴")) {
			rid = R.drawable.yin;
		} else if (LocalStore.weatherInfo.img_title_single.equals("多云")) {
			rid = R.drawable.yin;
		} else if (LocalStore.weatherInfo.img_title_single.equals("晴")) {
			rid = R.drawable.qing;
		}

		TianqiImageView.setImageResource(rid);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_radio1);
		initWidget();

		//设置当前登陆用户名
		topbar_txt = (TextView) findViewById(R.id.index_topbar_lift);
		tempUserName = LocalStore.getUserInfo().userName;
		topbar_txt.setText(tempUserName);

		tempUserId = LocalStore.getUserInfo().userId ;

		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);

		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		option.setAddrType("all");
		mLocClient.setLocOption(option);
		mLocClient.start();
		initTopBar();


		Thread updateThread = new Thread() {
			public void run() {
				try {
					Update.checkVersion(Radio1Activity.this, Radio1Activity.this,
							mHandler, Constants.PACKAGENAME,
							Constants.VERSION_CODE, "1", 0);

				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		};
		updateThread.start();

		rThread = new GetRepairLogLastThread(this, mHandler);
		rThread.start();

	}
	
	private void initWidget()
	{
		logoutBtn= (Button) findViewById(R.id.logout_button);
		logoutBtn.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                confirmLogouDialog();
            }
        });

		mGridView = (GridView) findViewById(R.id.MyGridView);
		mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		mGridView.setNumColumns(4);
		List<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
		int length = itemName.length;
		for (int i = 0; i < length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemImageView", imageRes[i]);
			map.put("ItemTextView", getResources().getText(itemName[i]));
			data.add(map);
		}
		// 为itme.xml添加适配器
		SimpleAdapter simpleAdapter = new SimpleAdapter(Radio1Activity.this,
				data, R.layout.gridview_item, new String[] { "ItemImageView",
				"ItemTextView" }, new int[] { R.id.ItemImageView,
				R.id.ItemTextView });
		mGridView.setAdapter(simpleAdapter);
		// 为mGridView添加点击事件监听器
		mGridView.setOnItemClickListener(new GridViewItemOnClick());
	}

	// 定义点击事件监听器
	public class GridViewItemOnClick implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int position,
								long arg3) {
			Intent intent = new Intent();
			switch (position) {
				case 0:
					LogUtil.d(TAG,"hello 0");

					intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
							| Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.setClass(Radio1Activity.this, Radio2Activity.class);
					startActivity(intent);
					break;
				case 1:
					LogUtil.d(TAG, "hello 1");
					intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
							| Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.setClass(Radio1Activity.this, RepairListActivity.class);
					startActivity(intent);
					break;
				case 2:
					LogUtil.d(TAG,"hello 2");
					intent.setClass(Radio1Activity.this, MipcaActivityCapture.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
					break;
				case 3:

					LogUtil.d(TAG,"hello 3");

					// TODO Auto-generated method stub
					Uri uri = Uri.parse("tel:"
							+ LocalStore.getInvitationCode(Radio1Activity.this).phoneNumber);
					Intent it = new Intent(Intent.ACTION_CALL, uri);
					startActivity(it);
					break;
				case 4:

					LogUtil.d(TAG,"hello 4");
					intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
							| Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.setClass(Radio1Activity.this, RepairActivity.class);
					intent.putExtra(Constants.SHOUCANG_FLAT, Constants.FLAG_CANYIN);
					startActivity(intent);
					break;
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case SCANNIN_GREQUEST_CODE:
				if(resultCode == RESULT_OK){
//					Bundle bundle = data.getExtras();
//					//显示扫描到的内容
//					mTextView.setText(bundle.getString("result"));
//					//显示
//					mImageView.setImageBitmap((Bitmap) data.getParcelableExtra("bitmap"));
				}
				break;
		}
	}
	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置

			if (location == null)
				return;


			info.latitude = location.getLatitude();
			info.longitude = location.getLongitude();
			info.city = location.getCity();

			LogUtil.d(TAG,"info.city"+info.city);

			LocalStore.cityInfo.city_name =  info.city;
			LocalStore.setCityInfo(Radio1Activity.this, LocalStore.cityInfo);
			

			Thread registerThread = new Thread() {
				public void run() {
					RemoteApiImpl remote = new RemoteApiImpl();
					WeatherInfo netInfo = remote.getWeather(Radio1Activity.this,info.city);

					Message msg = new Message();
					if (netInfo == null) {
						msg.what = 4;
					}
					else
					{				
						LocalStore.weatherInfo.fl1 = netInfo.fl1;
						LocalStore.weatherInfo.temp1 = netInfo.temp1;
						LocalStore.weatherInfo.img_title_single = netInfo.img_title_single;
						LocalStore.weatherInfo.wind1 = netInfo.wind1;
						LocalStore.saveWeatherInfo(Radio1Activity.this);
						mLocClient.stop();
						msg.what = 3;
					} 
					mHandler.sendMessage(msg);
				}
			};
			registerThread.start();
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}
	@Override
	protected void onNewIntent(Intent intent)
	{
		Boolean needInit = intent.getBooleanExtra("FromGroup", false);
		if(needInit)
		{
			//重新获取资源
			showDialog();
			endChildrenThreads();
			mThread = new GetIndexInfoThread(this, mHandler) ;
			mThread.start() ;
			mDotList.setCount(0);
			LogUtil.d("MyTag", "Radio1Activity.this onNewIntent");
		}
		super.onNewIntent(intent);
	}
	private void endChildrenThreads()
	{

		if (mThread != null)
		{
			mThread.stopRun();
			mThread = null;
		}
		
	}
	private void showDialog(){
		View view = (View) findViewById(R.id.view_network_error);
		view.setVisibility(View.GONE);
		mWaitLoading = new ProgressDialog(Radio1Activity.this);
		String msg = getResources().getString(R.string.txt_loading) ;
		mWaitLoading.setMessage(msg);
		mWaitLoading.setCancelable(true);
		mWaitLoading.show();
	}	
	
	
	void freeResource()
	{
		mThread.stopRun() ;
		mGalleryAdapter.freeDrawable() ;
		mHomeMessage = null ;
	}

	
	void initResource()
	{
		showDialog() ;
		mThread = new GetIndexInfoThread(this, mHandler) ;
		mThread.start() ;
		mDotList.setCount(0);
		
	}


	/*
	 * 当用户滑动了图片回调
	 */
	private final OnItemSelectedListener gallerySelectListener = 
		new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				mDotList.setIndex(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
	} ;
	
	/*
	 * 当用户点击了gallery上的图片回调
	 * 
	 * @selectItemIndex
	 */
	private final OnItemClickListener galleryClickListener =
		new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view,
				int position, long id) {
			Intent intent = new Intent(Radio1Activity.this , 
					Radio1InfoActivity.class) ;
			intent.putExtra("radio1info_title", mHomeMessage.notes.get(position).header) ;
			intent.putExtra("radio1info_body", mHomeMessage.notes.get(position).body) ;
			intent.putExtra("radio1info_time", mHomeMessage.notes.get(position).time) ;
			intent.putExtra("radio1info_signature", mHomeMessage.notes.get(position).signature) ;
			startActivity(intent) ;
		}
	} ;
	
	
	protected void confirmLogouDialog() {
        AlertDialog.Builder builder = new Builder(this);

        String strTitle = getResources().getString(R.string.txt_tips);
        String strOk = getResources().getString(R.string.txt_ok);
        String strCancel = getResources().getString(R.string.txt_cancel);

        builder.setTitle(strTitle);
        builder.setMessage("确认退出登录吗？");

        builder.setPositiveButton(strOk, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                LocalStore.logout(Radio1Activity.this);

                Intent intent = new Intent();
                intent.setClass(Radio1Activity.this, LoginActivity.class);
                startActivity(intent);

                finish();
            }
        });

        builder.setNegativeButton(strCancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.create().show();
    }

}