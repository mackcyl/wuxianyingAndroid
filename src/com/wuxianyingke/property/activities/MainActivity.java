package com.wuxianyingke.property.activities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.mantoto.property.R;
import com.wuxianyingke.gaode.RouteActivity;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.common.LogUtil;
import com.wuxianyingke.property.remote.RemoteApiImpl;
import com.wuxianyingke.property.remote.RemoteApi.NetInfo;
import com.wuxianyingke.property.remote.RemoteApi.WeatherInfo;
import com.wuxianyingke.property.service.SendLocation;
import com.wuxianyingke.property.threads.MessageTypeThread;

public class MainActivity extends Activity {
	static final String TAG = "MainActivity";
	private GridView mGridView; // MyGridView
	private ImageView mTuichuLinearLayout, mShouyeLinearLayout,
			mBohaoLinearLayout;
	private LocationClient mLocClient;
	private static SendLocation info = new SendLocation();
	
	private TextView DateTextView,Date_day_TextView,Date_weekday_TextView,DiZhiTextView,WenduTextView,TianQiTextView;
	private ImageView TianqiImageView,LuKuangImage;
	
	public MyLocationListenner myListener = new MyLocationListenner();
	// 定义图标数组
	private int[] imageRes = {
			R.drawable.style_tongzhi,
			R.drawable.style_progress,
			R.drawable.style_sign_qrcode,
			R.drawable.style_property,
			R.drawable.style_repair,
			R.drawable.style_my_task,


			R.drawable.style_xinxi,
			R.drawable.style_daishoukuaidi,
			R.drawable.style_tiaozhaoshichang,
			R.drawable.style_canyin,
			R.drawable.style_gouwu,
			R.drawable.style_jiaotong,
			R.drawable.style_yiliao,
			R.drawable.style_shenghuofuwu,
			R.drawable.style_changyongxinxi,
			R.drawable.style_youchangfuwu,
			R.drawable.style_xiugaimima,
			R.drawable.style_shenghuobianqian,
			R.drawable.style_tuijianyingyong,
			R.drawable.style_wifi
	};

	// 定义标题数组
	private int[] itemName = {
			R.string.gridview_tongzhi,
			R.string.gridview_progress,
			R.string.gridview_sign_qrcode,
			R.string.gridview_property,
			R.string.gridview_repair,
			R.string.gridview_my_task,


			R.string.gridview_xinxi,
			R.string.gridview_daishoukuaidi,
			R.string.gridview_tiaozhaoshichang,
			R.string.gridview_canyin,
			R.string.gridview_gouwu,
			R.string.gridview_jiaotong,
			R.string.gridview_yiliao,
			R.string.gridview_shenghuofuwu,
			R.string.gridview_changyongxinxi,
			R.string.gridview_youchangfuwu,
			R.string.gridview_xiugaimima,
			R.string.gridview_shenghuobianqian,
			R.string.gridview_tuijianyingyong,
			R.string.gridview_wifi
	};
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			LogUtil.d(TAG, "PushHandler handleMessage : " + msg.what);

			switch (msg.what) {
			case Constants.MSG_LOCATION_READY: {
				SendLocation obj = (SendLocation) msg.obj;
				LogUtil.d(TAG, "MSG_LOCATION_READY");

				LogUtil.d(TAG, "obj.city=" + obj.city);
				LogUtil.d(TAG, "obj.longitude=" + obj.longitude);
				LogUtil.d(TAG, "obj.latitude=" + obj.latitude);
				mLocClient.stop();
				// 加载地址 location和region, 需要本地的 region是城市
				// http://api.map.baidu.com/place/search?query=公交站&location=%@&coord_type=wgs84&radius=1000&region=%@&output=html&src=wuxianying|propertyManager
				String city = obj.city;
				String location = obj.latitude + "," + obj.longitude;
				String mapUrl = "http://api.map.baidu.com/place/search?query=医院&location="
						+ location
						+ "&coord_type=wgs84&radius=1000&output=html&src=wuxianying|propertyManager";
				LogUtil.d(TAG, "obj.mapUrl=" + mapUrl);
				Intent intent = new Intent();
				intent.setData(Uri.parse(mapUrl));
				intent.setAction(Intent.ACTION_VIEW);
				MainActivity.this.startActivity(intent); //启动浏览器
			}
				break;
			case 3:
				initTopBar();
				break;

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
		
		if(LocalStore.weatherInfo.img_title_single.equals("特大暴雨"))
				{
					rid = R.drawable.baoyu;
				}
		else if(LocalStore.weatherInfo.img_title_single.equals("大暴雨"))
				{
			rid = R.drawable.baoyu;
				}
		else if(LocalStore.weatherInfo.img_title_single.equals("暴雨"))
				{
			rid = R.drawable.baoyu;
				}
		else if(LocalStore.weatherInfo.img_title_single.equals("大雨"))
				{
			rid = R.drawable.baoyu;
				}
		else if(LocalStore.weatherInfo.img_title_single.equals("中雨"))
				{
			rid = R.drawable.baoyu;
				}
		else if(LocalStore.weatherInfo.img_title_single.equals("小雨"))
				{
			rid = R.drawable.xiaoyu;
				}
		else if(LocalStore.weatherInfo.img_title_single.equals("雷阵雨"))
				{
			rid = R.drawable.leizhenyu;
				}
		else if(LocalStore.weatherInfo.img_title_single.equals("阵雨"))
				{
			rid = R.drawable.zhenyu;
				}
		else if(LocalStore.weatherInfo.img_title_single.equals("冻雨"))
				{
			rid = R.drawable.yujiaxue;
				}
		else if(LocalStore.weatherInfo.img_title_single.equals("雨夹雪"))
				{
			rid = R.drawable.yujiaxue;
				}
		else if(LocalStore.weatherInfo.img_title_single.equals("暴雪"))
				{
			rid = R.drawable.baoxue;
				}
		else if(LocalStore.weatherInfo.img_title_single.equals("阵雪"))
				{
			rid = R.drawable.zhenxue;
				}
		else if(LocalStore.weatherInfo.img_title_single.equals("大雪"))
				{
			rid = R.drawable.zhenxue;
				}
		else if(LocalStore.weatherInfo.img_title_single.equals("中雪"))
		{
	rid = R.drawable.zhongxue;
		}
		else if(LocalStore.weatherInfo.img_title_single.equals("小雪"))
		{
	rid = R.drawable.xiaoxue;
		}
		else if(LocalStore.weatherInfo.img_title_single.equals("霾"))
		{
	rid = R.drawable.wumai;
		}
		else if(LocalStore.weatherInfo.img_title_single.equals("强沙尘暴"))
		{
	rid = R.drawable.qiangshacenbao;
		}
		else if(LocalStore.weatherInfo.img_title_single.equals("沙尘暴"))
		{
	rid = R.drawable.shachenbao;
		}
		else if(LocalStore.weatherInfo.img_title_single.equals("扬沙"))
		{
	rid = R.drawable.yangsha;
		}
		else if(LocalStore.weatherInfo.img_title_single.equals("浮尘"))
		{
	rid = R.drawable.yangsha;
		}
		else if(LocalStore.weatherInfo.img_title_single.equals("雾"))
		{
	rid = R.drawable.zhongwu;
		}
		else if(LocalStore.weatherInfo.img_title_single.equals("阴"))
		{
	rid = R.drawable.yin;
		}
		else if(LocalStore.weatherInfo.img_title_single.equals("多云"))
		{
	rid = R.drawable.yin;
		}
		else if(LocalStore.weatherInfo.img_title_single.equals("晴"))
		{
	rid = R.drawable.qing;
		}

		TianqiImageView.setImageResource(rid);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 this.requestWindowFeature(this.getWindow().FEATURE_NO_TITLE); //
		// 设置Activity标题不显示
//		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//				WindowManager.LayoutParams.FLAG_FULLSCREEN); // 设置全屏显示
		Log.d("MyTag", "getString(R.string.gridview_tongzhi)"
				+ getResources().getString(R.string.about));
		setContentView(R.layout.gridview_main);
		initWidget();

		// get location
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
	}

	private void initWidget() {
		mTuichuLinearLayout = (ImageView) findViewById(R.id.TuichuLinearLayout);
		mShouyeLinearLayout = (ImageView) findViewById(R.id.ShouyeLinearLayout);
		mBohaoLinearLayout = (ImageView) findViewById(R.id.BohaoLinearLayout);
		mTuichuLinearLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				confirmLogouDialog();
			}
		});
		mShouyeLinearLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this,
						Radio1Activity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
						| Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});
		mBohaoLinearLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Uri uri = Uri.parse("tel:"
						+ LocalStore.getInvitationCode(MainActivity.this).phoneNumber);
				Intent it = new Intent(Intent.ACTION_CALL, uri);
				startActivity(it);
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
		SimpleAdapter simpleAdapter = new SimpleAdapter(MainActivity.this,
				data, R.layout.gridview_item, new String[] { "ItemImageView",
						"ItemTextView" }, new int[] { R.id.ItemImageView,
						R.id.ItemTextView });
		mGridView.setAdapter(simpleAdapter);
		// 为mGridView添加点击事件监听器
		mGridView.setOnItemClickListener(new GridViewItemOnClick());
		

//		LuKuangImage = (ImageView) findViewById(R.id.LuKuangImage);
//		LuKuangImage.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
//				Intent intent = new Intent();
//				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
//						| Intent.FLAG_ACTIVITY_CLEAR_TOP);
//				intent.setClass(MainActivity.this, lukuangActivity.class);
//				startActivity(intent);
//			}
//		});
	}

	// 定义点击事件监听器
	public class GridViewItemOnClick implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int position,
				long arg3) {
			Intent intent = new Intent();
			switch (position) {
			case 0:

				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
						| Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.setClass(MainActivity.this, Radio2Activity.class);
				startActivity(intent);
				break;
			case 1:
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
						| Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.setClass(MainActivity.this, MessageActivity.class);
				startActivity(intent);
				break;
			case 2:
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
						| Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.setClass(MainActivity.this, Radio4Activity.class);
				startActivity(intent);
				break;
			case 3:
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
						| Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.setClass(MainActivity.this, ProductListActivity.class);
				startActivity(intent);
				break;
			case 4:
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
						| Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.setClass(MainActivity.this, CanYinListActivity.class);
				intent.putExtra(Constants.SHOUCANG_FLAT, Constants.FLAG_CANYIN);
				startActivity(intent);
				break;
			case 5:
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
						| Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.setClass(MainActivity.this, GouWuListActivity.class);
				intent.putExtra(Constants.SHOUCANG_FLAT, Constants.FLAG_GOUWU);
				startActivity(intent);
				break;
			case 6:
				 // 加载地址 location和region, 需要本地的 region是城市
                // http://api.map.baidu.com/place/search?query=公交站&location=%@&coord_type=wgs84&radius=1000&region=%@&output=html&src=wuxianying|propertyManager
                
				/*
				String city = info.city;
                String jiaotongLocation = info.latitude + "," + info.longitude;
                String jiaotongMapUrl = "http://api.map.baidu.com/place/search?query=公交站&location="
                        + jiaotongLocation
                        + "&coord_type=wgs84&radius=1000&region="
                        + city + "&output=html&src=wuxianying|propertyManager";
                LogUtil.d(TAG, "obj.mapUrl=" + jiaotongMapUrl);
				intent = new Intent();
				intent.setData(Uri.parse(jiaotongMapUrl));
				intent.setAction(Intent.ACTION_VIEW);
				MainActivity.this.startActivity(intent); //启动浏览器
				*/
				
				
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
						| Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.setClass(MainActivity.this, RouteActivity.class);
				startActivity(intent);
				break;
			case 7:
				// 加载地址 location和region, 需要本地的 region是城市
				// http://api.map.baidu.com/place/search?query=公交站&location=%@&coord_type=wgs84&radius=1000&region=%@&output=html&src=wuxianying|propertyManager
				/*
				String location = info.latitude + "," + info.longitude;
				String mapUrl = "http://api.map.baidu.com/place/search?query=医院&location="
						+ location
						+ "&coord_type=wgs84&radius=1000&output=html&src=wuxianying|propertyManager";
				LogUtil.d(TAG, "obj.mapUrl=" + mapUrl);
				intent = new Intent();
				intent.setData(Uri.parse(mapUrl));
				intent.setAction(Intent.ACTION_VIEW);
				MainActivity.this.startActivity(intent); //启动浏览器
				*/
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
						| Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.setClass(MainActivity.this, YiLiaoActivity.class);
				startActivity(intent);
				break;
			case 8:
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
						| Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.setClass(MainActivity.this,
						ShengHuoFuWuListActivity.class);
				intent.putExtra(Constants.SHOUCANG_FLAT, Constants.FLAG_SHENGHUOFUWU);
				startActivity(intent);
				break;
			case 9:
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
						| Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.setClass(MainActivity.this,
						CommonInfomationActivity.class);
				startActivity(intent);
				break;
			case 10:
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
						| Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.setClass(MainActivity.this, PaidServicesActivity.class);
				startActivity(intent);
				break;
			case 11:
				if(LocalStore.getIsVisitor(getApplicationContext()))
				{
					Toast.makeText(getApplicationContext(), "游客或者未认证用户无法完成此操作",
							Toast.LENGTH_SHORT).show();
					return;
				}
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
						| Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.setClass(MainActivity.this, ModifyPasswordActivity.class);
				startActivity(intent);
				break;
			case 12:
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
						| Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.setClass(MainActivity.this, StickerActivity.class);
				startActivity(intent);
				break;
			case 13:
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
						| Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.setClass(MainActivity.this, AppPopularizeActivity.class);
				startActivity(intent);
				break;
			case 14:
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
						| Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.setClass(MainActivity.this, WifiPasswordActivity.class);
				startActivity(intent);
				break;
			}
//			Toast.makeText(getApplicationContext(), position + "",
//					Toast.LENGTH_SHORT).show();
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
			LocalStore.cityInfo.city_name =  info.city;
			LocalStore.setCityInfo(MainActivity.this, LocalStore.cityInfo);
			

			Thread registerThread = new Thread() {
				public void run() {
					RemoteApiImpl remote = new RemoteApiImpl();
					WeatherInfo netInfo = remote.getWeather(MainActivity.this,info.city);

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
						LocalStore.saveWeatherInfo(MainActivity.this);
						mLocClient.stop();
						msg.what = 3;
					} 
					mHandler.sendMessage(msg);
				}
			};
			registerThread.start();
			/*Message msg = new Message();
			msg.what = Constants.MSG_LOCATION_READY;
			msg.obj = info;
			mHandler.sendMessage(msg);*/
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

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

				LocalStore.logout(MainActivity.this);

				Intent intent = new Intent();
				intent.setClass(MainActivity.this, LoginActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
						| Intent.FLAG_ACTIVITY_CLEAR_TOP);
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