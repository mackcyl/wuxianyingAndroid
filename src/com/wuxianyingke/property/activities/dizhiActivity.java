package com.wuxianyingke.property.activities;

import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMap.CancelableCallback;
import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.maps2d.overlay.PoiOverlay;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.mantoto.property.R;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LogUtil;


public class dizhiActivity extends Activity 
	implements LocationSource,AMapLocationListener{
    static final String TAG = "YiLiaoActivity";
    private TextView topbar_txt;
    private Button topbar_left;
	private MapView mapView; 
	private AMap aMap;
	private OnLocationChangedListener mListener;
	private LocationManagerProxy mAMapLocationManager;
	private AMapLocation myLoc=null;
	private PoiSearch poiSearch;
	private PoiResult poiResult; // poi返回的结果
	private PoiSearch.Query query;// Poi查询条件类
	private PoiOverlay poiOverlay;// poi图层
	private List<PoiItem> poiItems;// poi数据
	private Marker detailMarker;// 显示Marker的详情
	
	float latitude,longitude;
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            LogUtil.d(TAG, "PushHandler handleMessage : " + msg.what);

            switch (msg.what) {
            case Constants.MSG_LOCATION_READY: {
            }
                break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.yiliao);
        // BaiduLocation mBaiduLocation = new BaiduLocation(this, mHandler);
        // mBaiduLocation.getLocation(Constants.MSG_LOCATION_READY);
        initWidgets();

        latitude = getIntent().getFloatExtra("latitude",0.0f);
        longitude = getIntent().getFloatExtra("longitude",0.0f);
		
        // 为WebView设置WebViewClient处理某些操作
        mapView = (MapView) findViewById(R.id.map); 
        mapView.onCreate(savedInstanceState);// 必须要写
        if (aMap == null) { 
        	aMap = mapView.getMap();
        	}
        setUpMap();
    }
    
	private void setUpMap() {
    	// 自定义系统定位小蓝点
		MyLocationStyle myLocationStyle = new MyLocationStyle();
		myLocationStyle.myLocationIcon(BitmapDescriptorFactory
				.fromResource(R.drawable.location_marker));// 设置小蓝点的图标
		myLocationStyle.strokeColor(Color.BLACK);// 设置圆形的边框颜色
		myLocationStyle.radiusFillColor(Color.argb(100, 0, 0, 180));// 设置圆形的填充颜色
		// myLocationStyle.anchor(int,int)//设置小蓝点的锚点
		myLocationStyle.strokeWidth(1.0f);// 设置圆形的边框粗细
		aMap.setMyLocationStyle(myLocationStyle);
		aMap.setLocationSource(this);// 设置定位监听
		aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
		aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
		aMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).icon(
				BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
		
		changeCamera(
				CameraUpdateFactory.newCameraPosition(new CameraPosition(
						new LatLng(latitude, longitude), 18, 0, 30)), null);
	   // aMap.setMyLocationType()
	}

    private void initWidgets() {
        //mYiLiaoWebView = (WebView) findViewById(R.id.YiLiaoWebView);
        topbar_txt = (TextView) findViewById(R.id.topbar_txt);

        topbar_left = (Button) findViewById(R.id.topbar_left);
        topbar_txt.setText("地 址");
        topbar_left.setVisibility(View.VISIBLE);
        topbar_left.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
    }
	
	
	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
		changeCamera(CameraUpdateFactory.zoomTo(17), null);
	}
	
	private void changeCamera(CameraUpdate update, CancelableCallback callback) {
		if(aMap!=null)
			aMap.moveCamera(update);
	}
	
	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
		deactivate();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}
	/**
	 * 此方法已经废弃
	 */

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLocationChanged(AMapLocation arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void activate(OnLocationChangedListener arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deactivate() {
		// TODO Auto-generated method stub
		
	}
}