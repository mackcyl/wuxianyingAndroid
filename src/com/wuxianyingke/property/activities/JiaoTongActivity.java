package com.wuxianyingke.property.activities;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMap.InfoWindowAdapter;
import com.amap.api.maps2d.AMap.OnMarkerClickListener;
import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.AMap.CancelableCallback;
import com.amap.api.maps2d.LocationSource.OnLocationChangedListener;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.maps2d.overlay.PoiOverlay;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.poisearch.Cinema;
import com.amap.api.services.poisearch.Dining;
import com.amap.api.services.poisearch.Hotel;
import com.amap.api.services.poisearch.PoiItemDetail;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.Scenic;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.amap.api.services.poisearch.PoiSearch.SearchBound;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.mantoto.property.R;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LogUtil;
import com.wuxianyingke.property.service.SendLocation;

/**
 * 交通
 * 
 * @author wentinggao
 * 
 */
public class JiaoTongActivity extends Activity 
	implements LocationSource,AMapLocationListener, OnPoiSearchListener, OnMarkerClickListener, InfoWindowAdapter {
    static final String TAG = "JiaoTongActivity";
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
        setContentView(R.layout.jiaotong);
        // BaiduLocation mBaiduLocation = new BaiduLocation(this, mHandler);
        // mBaiduLocation.getLocation(Constants.MSG_LOCATION_READY);
        initWidgets();
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

		aMap.setOnMarkerClickListener(this);// 添加点击marker监听事件
		aMap.setInfoWindowAdapter(this);// 添加显示infowindow监听事件
	   // aMap.setMyLocationType()
	}

    private void initWidgets() {
        //mJiaotongWebView = (WebView) findViewById(R.id.JiaotongWebView);
        topbar_txt = (TextView) findViewById(R.id.topbar_txt);

        topbar_left = (Button) findViewById(R.id.topbar_left);
        topbar_txt.setText("交通");
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
		changeCamera(CameraUpdateFactory.zoomTo(16), null);
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
	public void onLocationChanged(Location location) {
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	/**
	 * 定位成功后回调函数
	 */
	@Override
	public void onLocationChanged(AMapLocation aLocation) {
		if (mListener != null && aLocation != null) {
				mListener.onLocationChanged(aLocation);// 显示系统小蓝点
				if(myLoc==null)
				{
				myLoc=aLocation;
				aMap.setOnMapClickListener(null);// 进行poi搜索时清除掉地图点击事件
				query = new PoiSearch.Query("", "公交", myLoc.getCity());// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
				query.setPageSize(30);// 设置每页最多返回多少条poiitem
				query.setPageNum(0);// 设置查第一页

				query.setLimitDiscount(false);
				query.setLimitGroupbuy(false);
	
					poiSearch = new PoiSearch(this, query);
					poiSearch.setOnPoiSearchListener(this);
					poiSearch.setBound(new SearchBound(new LatLonPoint( myLoc.getLatitude(), myLoc.getLongitude()), 1000, true));//
					// 设置搜索区域为以lp点为圆心，其周围2000米范围
					/*
					 * List<LatLonPoint> list = new ArrayList<LatLonPoint>();
					 * list.add(lp);
					 * list.add(AMapUtil.convertToLatLonPoint(Constants.BEIJING));
					 * poiSearch.setBound(new SearchBound(list));// 设置多边形poi搜索范围
					 */
					poiSearch.searchPOIAsyn();// 异步搜索
			}
		}
	}

	/**
	 * 激活定位
	 */
	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;
		if (mAMapLocationManager == null) {
			mAMapLocationManager = LocationManagerProxy.getInstance(this);
			/*
			 * mAMapLocManager.setGpsEnable(false);
			 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true Location
			 * API定位采用GPS和网络混合定位方式
			 * ，第一个参数是定位provider，第二个参数时间最短是2000毫秒，第三个参数距离间隔单位是米，第四个参数是定位监听者
			 */
			mAMapLocationManager.requestLocationUpdates(
					LocationProviderProxy.AMapNetwork, 1000, 10, this);
		}
	}

	/**
	 * 停止定位
	 */
	@Override
	public void deactivate() {
		mListener = null;
		if (mAMapLocationManager != null) {
			mAMapLocationManager.removeUpdates(this);
			mAMapLocationManager.destory();
		}
		mAMapLocationManager = null;
	}

	@Override
	public void onPoiItemDetailSearched(PoiItemDetail result, int rCode) {
		// TODO Auto-generated method stub
		if (rCode == 0) {
			if (result != null) {// 搜索poi的结果
				if (detailMarker != null) {
					StringBuffer sb = new StringBuffer(result.getSnippet());
					if ((result.getGroupbuys() != null && result.getGroupbuys()
							.size() > 0)
							|| (result.getDiscounts() != null && result
									.getDiscounts().size() > 0)) {

						if (result.getGroupbuys() != null
								&& result.getGroupbuys().size() > 0) {// 取第一条团购信息
							sb.append("\n团购："
									+ result.getGroupbuys().get(0).getDetail());
						}
						if (result.getDiscounts() != null
								&& result.getDiscounts().size() > 0) {// 取第一条优惠信息
							sb.append("\n优惠："
									+ result.getDiscounts().get(0).getDetail());
						}
					} else {
						sb = new StringBuffer("地址：" + result.getSnippet()
								+ "\n电话：" + result.getTel() + "\n类型："
								+ result.getTypeDes());
					}
					// 判断poi搜索是否有深度信息
					if (result.getDeepType() != null) {
						sb = getDeepInfo(result, sb);
						detailMarker.setSnippet(sb.toString());
					} else {
					}
				}

			} else {
				Toast.makeText(JiaoTongActivity.this, "没有信息", Toast.LENGTH_LONG).show();
			}
		} else if (rCode == 27) {

			Toast.makeText(JiaoTongActivity.this, "网络错误", Toast.LENGTH_LONG).show();
		} else if (rCode == 32) {
			Toast.makeText(JiaoTongActivity.this, "key错误", Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(JiaoTongActivity.this, "其他错误："+rCode, Toast.LENGTH_LONG).show();
		}
	}
	
	private StringBuffer getDeepInfo(PoiItemDetail result,
			StringBuffer sbuBuffer) {
		switch (result.getDeepType()) {
		// 餐饮深度信息
		case DINING:
			if (result.getDining() != null) {
				Dining dining = result.getDining();
				sbuBuffer
						.append("\n菜系：" + dining.getTag() + "\n特色："
								+ dining.getRecommend() + "\n来源："
								+ dining.getDeepsrc());
			}
			break;
		// 酒店深度信息
		case HOTEL:
			if (result.getHotel() != null) {
				Hotel hotel = result.getHotel();
				sbuBuffer.append("\n价位：" + hotel.getLowestPrice() + "\n卫生："
						+ hotel.getHealthRating() + "\n来源："
						+ hotel.getDeepsrc());
			}
			break;
		// 景区深度信息
		case SCENIC:
			if (result.getScenic() != null) {
				Scenic scenic = result.getScenic();
				sbuBuffer
						.append("\n价钱：" + scenic.getPrice() + "\n推荐："
								+ scenic.getRecommend() + "\n来源："
								+ scenic.getDeepsrc());
			}
			break;
		// 影院深度信息
		case CINEMA:
			if (result.getCinema() != null) {
				Cinema cinema = result.getCinema();
				sbuBuffer.append("\n停车：" + cinema.getParking() + "\n简介："
						+ cinema.getIntro() + "\n来源：" + cinema.getDeepsrc());
			}
			break;
		default:
			break;
		}
		return sbuBuffer;
	}

	@Override
	public void onPoiSearched(PoiResult result, int rCode) {
		// TODO Auto-generated method stub
		if (rCode == 0) {
			if (result != null && result.getQuery() != null) {// 搜索poi的结果
				if (result.getQuery().equals(query)) {// 是否是同一条
					poiResult = result;
					poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
					List<SuggestionCity> suggestionCities = poiResult
							.getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
					if (poiItems != null && poiItems.size() > 0) {
						aMap.clear();// 清理之前的图标
						poiOverlay = new PoiOverlay(aMap, poiItems);
						poiOverlay.removeFromMap();
						poiOverlay.addToMap();
						poiOverlay.zoomToSpan();

						Toast.makeText(JiaoTongActivity.this, "搜索完成", Toast.LENGTH_LONG).show();
					}  else {
						Toast.makeText(JiaoTongActivity.this, "没有任何结果", Toast.LENGTH_LONG).show();
					}
				}
			} else {
				Toast.makeText(JiaoTongActivity.this, "1000m范围类没有任何结果", Toast.LENGTH_LONG).show();
			}
		} else if (rCode == 27) {

			Toast.makeText(JiaoTongActivity.this, "网络错误", Toast.LENGTH_LONG).show();
		} else if (rCode == 32) {
			Toast.makeText(JiaoTongActivity.this, "key错误", Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(JiaoTongActivity.this, "其他错误："+rCode, Toast.LENGTH_LONG).show();
		}
	}

	public void doSearchPoiDetail(String poiId) {
		if (poiSearch != null && poiId != null) {
			poiSearch.searchPOIDetailAsyn(poiId);
		}
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		if (poiOverlay != null && poiItems != null && poiItems.size() > 0) {
			detailMarker = marker;
			doSearchPoiDetail(poiItems.get(poiOverlay.getPoiIndex(marker))
					.getPoiId());
		}
		return false;
	}

	@Override
	public View getInfoContents(Marker marker) {
		return null;
	}

	@Override
	public View getInfoWindow(Marker marker) {
		return null;
	}
    
    /**
     * 定位SDK监听函数
     */
    /*
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null)
                return;

            SendLocation info = new SendLocation();
            info.latitude = location.getLatitude();
            info.longitude = location.getLongitude();
            info.city = location.getCity();
            Message msg = new Message();
            msg.what = Constants.MSG_LOCATION_READY;
            msg.obj = info;
            mHandler.sendMessage(msg);
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }


    class webViewClient extends WebViewClient {

        // 重写shouldOverrideUrlLoading方法，使点击链接后不使用其他的浏览器打开。

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            view.loadUrl(url);

            // 如果不需要其他对点击链接事件的处理返回true，否则返回false

            return true;

        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history

        if ((keyCode == KeyEvent.KEYCODE_BACK) && mJiaotongWebView.canGoBack()) {
            mJiaotongWebView.goBack();
            return true;
        }

        // return true;
        // If it wasn't the Back key or there's no web page history, bubble up
        // to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }

    public class WebAppInterface {
        Context mContext;
        WebAppInterface(Context c) {
            mContext = c;
        }
        public void showToast(String toast) {
            Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
        }
    }
    */
}