package com.wuxianyingke.property.activities;

import java.sql.Array;
import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
//import android.hardware.display.DisplayManager;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.mantoto.property.R;
import com.wuxianyingke.progerty.databases.LivingDB;
import com.wuxianyingke.property.activities.MessageInfoActivity.MessageListItem;
import com.wuxianyingke.property.adapter.ImageAdapter;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.remote.RemoteApi.FleaContent;
import com.wuxianyingke.property.remote.RemoteApi.LivingItem;
import com.wuxianyingke.property.remote.RemoteApi.NetInfo;
import com.wuxianyingke.property.remote.RemoteApi.Promotion;
import com.wuxianyingke.property.remote.RemoteApi.PromotionList;
import com.wuxianyingke.property.threads.GetCanyinDetailThread;
import com.wuxianyingke.property.threads.GetCanyinOwnListThread;
import com.wuxianyingke.property.threads.GetProductDetailThread;
import com.wuxianyingke.property.views.IndicationDotList;
import com.wuxianyingke.property.views.MyGallery;


public class CanyinDetailOwnActivity extends Activity   implements OnClickListener{

	private int mLivingItemID = 0 ;
	private int popGalleryIndex = 0; // pop弹出的是那张图片
	private LivingItem mProductDetail ;
	
	private MyGallery mMyGallery = null;
	private IndicationDotList mDotList = null;
	
	private ImageAdapter mAdapter = null ;
	private PopupWindow mImgPop = null;
	private View mImgView = null;
	private MyGallery mPopGallery = null;
	private IndicationDotList mPopIndicationDotList = null;
	private Button mPopDownloadBtn = null;
	private ImageAdapter mPopAdapter = null;
	private ProgressDialog mWaitLoading = null ;

	private GetCanyinDetailThread mThread = null ;
	private GetCanyinOwnListThread mOwnListThread = null ;
	
	
	private int propertyid;
	private String source = "";
	private ScrollView mAllViewSv = null ;
	private TextView mIsVisitorTextView;
	private SharedPreferences saving=null ;
	ImageView cartImageview;
	private Button topbar_left,topbar_button_right;
	private TextView topbar_txt;
	private String telephone;
	private ArrayList<ImageView> productImgList = new ArrayList<ImageView>();
	private ArrayList<ImageView> activityImgList = new ArrayList<ImageView>();
	private float latitude,longitude;
	
	private TextView yingyeshijianTv,dianhuaTv,dizhiTv,miaoshuTv;
	private LinearLayout dianmianLayout,shangpinglist,huodonglist,img_ll;
	private ImageView peisongImg,dianhuaImg,dizhiImg;
	private Button huodongButton,shangpinButton;
	private int shoucang_flag;
	private int shoucangStatus;

	private int favorite_flag;

	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case Constants.MSG_GET_PRODUCT_DETAIL_FINISH:
				if(mWaitLoading != null && mWaitLoading.isShowing()){
					mWaitLoading.dismiss() ;
				}
				if(mThread != null && mThread.getProductDetail() != null){
					mAllViewSv.setVisibility(View.VISIBLE) ;
					
					mProductDetail = mThread.getProductDetail() ;
                     
					Gallery.LayoutParams params = new Gallery.LayoutParams(220, 220) ;
					mAdapter = new ImageAdapter(CanyinDetailOwnActivity.this , 
							params , ImageView.ScaleType.CENTER_CROP) ;
					mMyGallery.setAdapter(mAdapter) ;
					topbar_txt.setText(mProductDetail.LivingItemName) ;
					//mRenjunTextView.setText(String.valueOf(mProductDetail.avg_price)) ;
					yingyeshijianTv.setText("营业时间："+mProductDetail.Hours);
					dianhuaTv.setText(mProductDetail.telephone) ;
					telephone=mProductDetail.telephone;
					dizhiTv.setText("地址："+mProductDetail.address);
					miaoshuTv.setText(mProductDetail.Description);
					Boolean ForExpress = mProductDetail.ForExpress;
					if(ForExpress)
						peisongImg.setVisibility(View.VISIBLE);
					else {
						peisongImg.setVisibility(View.GONE);
					}
					latitude = mProductDetail.latitude;
					longitude = mProductDetail.longitude;
					dizhiImg.setVisibility(View.VISIBLE);
					
					mOwnListThread.start() ;
				}
				break ;
			case Constants.MSG_GET_PRODUCT_DETAIL_FAILD:
			case Constants.MSG_GET_PRODUCT_DETAIL_NET_ERROR:
				if(mWaitLoading != null && mWaitLoading.isShowing()){
					mWaitLoading.dismiss() ;
				}
				mAllViewSv.setVisibility(View.GONE) ;
				View view = (View) findViewById(R.id.view_network_error);
				view.setVisibility(View.VISIBLE);
				break ;
			case Constants.MSG_GET_PRODUCT_DETAIL_IMG_FINISH:
				Log.d("MyTag","mProductDetail.fleaPictureArray.get(i)"+mProductDetail.livingItemPicture.get(msg.arg1).path);
				Log.d("MyTag","msg.arg1"+msg.arg1);
				mAdapter.addImg(mProductDetail.livingItemPicture.get(msg.arg1).imgDw) ;
				mDotList.setCount(mDotList.getCount()+1) ;
				break ;
			case Constants.MSG_ADD_FAVORITE_FINISH:
				if(mWaitLoading != null && mWaitLoading.isShowing()){
					mWaitLoading.dismiss() ;
				}
				NetInfo mErrorInfo=(NetInfo) msg.obj;
				if(!"".equals(mErrorInfo.desc)&&mErrorInfo.desc!=null)
				{
					Toast.makeText(CanyinDetailOwnActivity.this, mErrorInfo.desc, Toast.LENGTH_SHORT).show();
				}else
				{
					Toast.makeText(CanyinDetailOwnActivity.this, "读取失败，可能网络问题或服务器无反应", Toast.LENGTH_SHORT).show();
				}
				break;
			case Constants.MSG_GET_PRODUCT_FINISH:
				
				if(mOwnListThread != null && mOwnListThread.getProductDetail() != null){
					PromotionList list = mOwnListThread.getProductDetail();


					for(int i = 0; i<list.promotionList.size();i++)
					{
						Promotion promotion = list.promotionList.get(i);
				    	View v = getLayoutInflater().inflate(R.layout.canyin_detail_own_content, null);

//						Log.i("MyTag", " promotion = "+list.promotionList.get(i) );


						int serverImageWidth = promotion.Width;
						int serverImageHeight = promotion.Height;

//						double value1 = (serverImageWidth/serverImageHeight);

//						Log.i("MyTag", "服务器返回图片尺寸: 宽度 = " + serverImageWidth + "高度 = :" + serverImageHeight);

				    	ImageView canyinImg = (ImageView) v.findViewById(R.id.canyinImg);
						canyinImg.setScaleType(ImageView.ScaleType.FIT_XY);
						canyinImg.setAdjustViewBounds(true);

						Display display = getWindowManager().getDefaultDisplay();

//						Log.i("MyTag", "屏幕尺寸1: 宽度 = "+display.getWidth()+"高度 = :"+display.getHeight()
//						);
						LayoutParams params = canyinImg.getLayoutParams();
						params.width = display.getWidth()-20;
						params.height = (display.getWidth()-20)*serverImageHeight/serverImageWidth;

//						int testv = (display.getWidth()-20)*3/4;
//						Log.i("MyTag", "图片尺寸: 宽度 = " + params.width + "高度 = :" + params.height +" 4:3"+testv);

//						Log.i("MyTag", "比例: 宽度 = " + value1 + "高度 = :" + 4/3);
						canyinImg.setLayoutParams(params);



				    	canyinImg.setVisibility(View.GONE);
				    	productImgList.add(canyinImg);
				    	TextView canyin_title = (TextView) v.findViewById(R.id.canyin_title);
				    	canyin_title.setText(promotion.Header);

				    	TextView canyin_price = (TextView) v.findViewById(R.id.canyin_price);
				    	if(0!=promotion.Price)
				    	{
				    		canyin_price.setText("￥ "+promotion.Price+".00");
				    	}
				    	TextView canyin_desc = (TextView) v.findViewById(R.id.canyin_desc);
				    	canyin_desc.setText(promotion.Body);
						shangpinglist.addView(v);
					}
							
				}
				break;
			case Constants.MSG_GET_ACTIVITY_FINISH:
				if(mOwnListThread != null && mOwnListThread.getActivityDetail() != null){
					PromotionList list = mOwnListThread.getActivityDetail();
					for(int i = 0; i<list.promotionList.size();i++)
					{
						Promotion promotion = list.promotionList.get(i);
				    	View v = getLayoutInflater().inflate(R.layout.canyin_detail_own_content, null);

						int serverImageWidth = promotion.Width;
						int serverImageHeight = promotion.Height;
//						Log.i("MyTag", "服务器返回图片尺寸: 宽度 = " + serverImageWidth + "高度 = :" + serverImageHeight);


						TextView canyin_title = (TextView) v.findViewById(R.id.canyin_title);
				    	canyin_title.setText(promotion.Header);

				    	ImageView canyinImg = (ImageView) v.findViewById(R.id.canyinImg);

//						Log.i("ACTIVITY_IMG_FINISH", "图片尺寸: 宽度 = " + canyinImg.getWidth() + "高度 = :" + canyinImg.getHeight());
						canyinImg.setScaleType(ImageView.ScaleType.FIT_XY);
						Display display = getWindowManager().getDefaultDisplay();

						LayoutParams params = canyinImg.getLayoutParams();
						params.width = display.getWidth()-20;
						params.height = (display.getWidth()-20)*serverImageHeight/serverImageWidth;

//						int testv = (display.getWidth()-20)*3/4;
						Log.i("MyTag", "图片尺寸: 宽度 = " + params.width + "高度 = :" + params.height);

//						Log.i("MyTag", "比例: 宽度 = " + value1 + "高度 = :" + 4/3);
//						params.height=380;
						canyinImg.setLayoutParams(params);
//						Log.i("ACTIVITY_IMG_FINISH111", "图片尺寸: 宽度 = " + canyinImg.getWidth() + "高度 = :" + canyinImg.getHeight());
				    	canyinImg.setVisibility(View.GONE);
				    	activityImgList.add(canyinImg);
				    	
				    	TextView canyin_price = (TextView) v.findViewById(R.id.canyin_price);
				    	if(0!=promotion.Price)
				    	{
				    		canyin_price.setText("￥ "+promotion.Price+".00");
				    	}
				    	TextView canyin_desc = (TextView) v.findViewById(R.id.canyin_desc);
				    	canyin_desc.setText(promotion.Body);
						huodonglist.addView(v);
					}
							
				}
				break;
			case Constants.MSG_GET_PRODUCT_IMG_FINISH:
				{
				ImageView canyinImg= productImgList.get(msg.arg1);
		    	LayoutParams pr=canyinImg.getLayoutParams();
				canyinImg.setImageDrawable(mOwnListThread.getDrawable(msg.arg2));
		    	canyinImg.setLayoutParams(pr);
				canyinImg.setVisibility(View.VISIBLE);
				}
				break;
			case Constants.MSG_GET_ACTIVITY_IMG_FINISH:
				{
					ImageView canyinImg= activityImgList.get(msg.arg1);
//					Log.i("ACTIVITY_IMG_FINISH", "图片尺寸: 宽度 = " + canyinImg.getWidth() + "高度 = :" + canyinImg.getHeight());
		    		LayoutParams pr=canyinImg.getLayoutParams();


					canyinImg.setImageDrawable(mOwnListThread.getDrawable(msg.arg2));
		    		canyinImg.setLayoutParams(pr);
					canyinImg.setVisibility(View.VISIBLE);
				}
				break;
			}
		}
	} ;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState) ;
		setContentView(R.layout.canyin_detail_own) ;
		SharedPreferences saving = this.getSharedPreferences(
				LocalStore.USER_INFO, 0);
		propertyid = (int) saving.getLong(LocalStore.PROPERTY_ID, 0);

		topbar_txt= (TextView) findViewById(R.id.topbar_txt) ;
		topbar_left=(Button)findViewById(R.id.topbar_left);
		topbar_left.setVisibility(View.VISIBLE);
		mLivingItemID = getIntent().getIntExtra(Constants.CANYIN_ID_ACTION , 0) ;
		shoucang_flag = getIntent().getIntExtra(Constants.SHOUCANG_FLAT , 0) ;
		source = getIntent().getStringExtra(Constants.CANYIN_SOURCE_ACTION);

		favorite_flag = getIntent().getIntExtra(Constants.FAVORITE_FLAT , 0) ;
		Log.d("MyTag", "GanyinDetailOwn favorite_flag="+favorite_flag);

		topbar_left.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// TODO Auto-generated method stub
				if(0!=favorite_flag){
					Intent intent = new Intent();
					intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
							| Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.setClass(CanyinDetailOwnActivity.this, CollectionListActivity.class);
					intent.putExtra(Constants.SHOUCANG_FLAT,shoucang_flag);
					startActivity(intent);
				}else{
					finish();
				}

			}
		});

		Log.d("MyTag", "mProductId=" + mLivingItemID);
		Log.d("MyTag", "GanyinDetailOwn propertyid="+propertyid);


		mAllViewSv = (ScrollView) findViewById(R.id.all_view_sv) ;
		mMyGallery = (MyGallery) findViewById(R.id.detail_gallery) ;
		mMyGallery.setSpacing(18) ;
		mMyGallery.setOnItemSelectedListener(gallerySelectListener) ;
		mMyGallery.setOnItemClickListener(galleryClickListener) ;
		mDotList = (IndicationDotList) findViewById(R.id.detail_indication) ;
		
		yingyeshijianTv= (TextView) findViewById(R.id.yingyeshijianTv);
		dianhuaTv= (TextView) findViewById(R.id.dianhuaTv);
		dizhiTv= (TextView) findViewById(R.id.dizhiTv) ;
		miaoshuTv= (TextView) findViewById(R.id.miaoshuTv) ;
		dianmianLayout= (LinearLayout) findViewById(R.id.dianmianLayout) ;
		img_ll= (LinearLayout) findViewById(R.id.huodonglist) ;
		shangpinglist= (LinearLayout) findViewById(R.id.shangpinglist) ;
		huodonglist= (LinearLayout) findViewById(R.id.huodonglist) ;
		peisongImg = (ImageView) findViewById(R.id.peisongImg) ;
		dianhuaImg = (ImageView) findViewById(R.id.dianhuaImg) ;
		dizhiImg = (ImageView) findViewById(R.id.dizhiImg) ;
		huodongButton = (Button) findViewById(R.id.huodongButton) ;
		shangpinButton = (Button) findViewById(R.id.shangpinButton) ;
		topbar_button_right = (Button) findViewById(R.id.topbar_button_right) ;
		topbar_button_right.setVisibility(View.VISIBLE);
		LivingDB db = new LivingDB(this);
		shoucangStatus=db.getOneItem(mLivingItemID);
		db.close();

//		Log.d("MyTag", "CanyinDetailOwn 336 ----- ----------distance 336=" + mProductDetail.LivingItemName);
//		Log.d("MyTag", "CanyinDetailOwn 336 ----- ----------distance 336=" + mProductDetail.LivingItemID);
//		Log.d("MyTag", "CanyinDetailOwn 336 ----- ----------distance 336=" + mProductDetail.distance);
		if(-1!=shoucangStatus)
		{
			topbar_button_right.setBackgroundResource(R.drawable.shoucang);
		}else
		{
			topbar_button_right.setBackgroundResource(R.drawable.no_shoucang);
		}
		
		topbar_button_right.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				LivingDB db = new LivingDB(CanyinDetailOwnActivity.this);
				mProductDetail.flag=shoucang_flag;

//				Log.d("MyTag", "deleteOneApp ----- ----------shoucang_flag=" + shoucang_flag);
//				Log.d("MyTag", "deleteOneApp ----- ----------shoucang_flag=" + mProductDetail);
//				Log.d("MyTag", "deleteOneApp ----- ----------shoucangStatus=" + shoucangStatus);

				if(-1!=shoucangStatus)
				{
					shoucangStatus = -1;
					db.deleteOneApp(mProductDetail.LivingItemID);
					topbar_button_right.setBackgroundResource(R.drawable.no_shoucang);
					Toast.makeText(CanyinDetailOwnActivity.this, "取消收藏成功", Toast.LENGTH_SHORT).show();

				}else
				{

					db.insertOneItem(mProductDetail);

					shoucangStatus=db.getOneItem(mLivingItemID);
					topbar_button_right.setBackgroundResource(R.drawable.shoucang);
					Toast.makeText(CanyinDetailOwnActivity.this, "收藏成功", Toast.LENGTH_SHORT).show();
				}
				
				db.close();		
			}
		});
		
		dianhuaImg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				 Uri uri = Uri.parse("tel:"
	                        + telephone);
	                Intent it = new Intent(Intent.ACTION_CALL, uri);
	                startActivity(it);

				
				/*long uid=LocalStore.getUserInfo().userId;*/
			}
		});
		
		huodongButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				changeTo(1);				
			}
		}) ;		
		shangpinButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				changeTo(0);
			}
		}) ;
		
		dizhiImg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
						| Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra("latitude", latitude);
				intent.putExtra("longitude", longitude);
				intent.setClass(CanyinDetailOwnActivity.this, dizhiActivity.class);
				startActivity(intent);
			}
		}) ;
		/*if (LocalStore.getIsVisitor(this)) {
			mContactPublisherButton.setClickable(false);
			mIsVisitorTextView.setVisibility(View.VISIBLE);
		}*/
		
		showDialog() ;
		changeTo(0);
		mThread = new GetCanyinDetailThread(this, mHandler, propertyid,mLivingItemID,source) ;
		mOwnListThread = new GetCanyinOwnListThread(this, mHandler, propertyid,mLivingItemID,source) ;
		mThread.start() ;
		
		
	}
	@Override
	protected void onStart()
	{
		super.onStart();
	}
	
	private void changeTo(int i)
	{
		if(i==0)
		{
			shangpinButton.setTextColor(Color.parseColor("#ffffff"));
			huodongButton.setTextColor(Color.parseColor("#00b1ff"));
			shangpinButton.setBackgroundResource(R.drawable.switch_button_left_on);
			huodongButton.setBackgroundResource(R.drawable.switch_button_right_default);
			shangpinglist.setVisibility(View.VISIBLE);
			huodonglist.setVisibility(View.GONE);
			dianmianLayout.setVisibility(View.VISIBLE);
			img_ll.setVisibility(View.VISIBLE);
		}
		else
		{
			shangpinButton.setTextColor(Color.parseColor("#00b1ff"));
			huodongButton.setTextColor(Color.parseColor("#ffffff"));
			shangpinButton.setBackgroundResource(R.drawable.switch_button_left_default);
			huodongButton.setBackgroundResource(R.drawable.switch_button_right_on);
			shangpinglist.setVisibility(View.GONE);
			huodonglist.setVisibility(View.VISIBLE);
			dianmianLayout.setVisibility(View.GONE);
			img_ll.setVisibility(View.GONE);
		}
	}
	private void showDialog(){
		mWaitLoading = new ProgressDialog(CanyinDetailOwnActivity.this);
		String msg = getResources().getString(R.string.txt_loading) ;
		mWaitLoading.setMessage(msg);
		mWaitLoading.setCancelable(false);
		mWaitLoading.show();
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
	private final OnItemClickListener galleryClickListener = new OnItemClickListener()
	{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{
			popGalleryIndex = position;
			initImagePopWindow();
		}
	};
	/*
	 * 当用户点击了gallery上的图片回调
	 * 
	 * @selectItemIndex
	 */
	private final OnItemClickListener popGalleryClickListener = new OnItemClickListener()
	{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{

		}
	};
	/*
	 * 当用户滑动了图片回调
	 */
	private final OnItemSelectedListener popGallerySelectListener = new OnItemSelectedListener()
	{

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
		{
			mPopIndicationDotList.setIndex(position);
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent)
		{

		}
	};
	private void initImagePopWindow()
	{
		mImgView = getLayoutInflater().inflate(R.layout.appshare_app_pop, null);
		mImgPop = new PopupWindow(mImgView, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, true);

		mPopDownloadBtn = (Button) mImgView.findViewById(R.id.pop_download_btn);
		mPopDownloadBtn.setOnClickListener(CloseClickListener);

		RelativeLayout popRl = (RelativeLayout) mImgView.findViewById(R.id.app_pop_rl);
		popRl.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				if (mImgPop != null && mImgPop.isShowing())
				{
					mImgPop.dismiss();
					mMyGallery.setAdapter(mAdapter);
					mMyGallery.setSelection(popGalleryIndex);
				}
			}
		});

		mImgPop.setAnimationStyle(R.style.AnimationPreview);

		Gallery.LayoutParams params = new Gallery.LayoutParams(/*410, 520*/
		
		ViewGroup.LayoutParams.FILL_PARENT,
		ViewGroup.LayoutParams.FILL_PARENT
		 );


		mPopGallery = (MyGallery) mImgView.findViewById(R.id.pop_img_gallery);
		mPopGallery.setSpacing(10);

		mPopGallery.setOnItemClickListener(popGalleryClickListener);
		mPopGallery.setOnItemSelectedListener(popGallerySelectListener);
		
		mPopAdapter = new ImageAdapter(this, params, ImageView.ScaleType.CENTER_CROP);
			for (int i = mProductDetail.livingItemPicture.size() - 1; i >= 0; --i) 
		{
			Log.d("MyTag","mProductDetail.fleaPictureArray.get(i)"+mProductDetail.livingItemPicture.get(i).path);
			mPopAdapter.addImg(mProductDetail.livingItemPicture.get(i).imgDw);
		}

		mPopGallery.setAdapter(mPopAdapter);
		mPopGallery.setSelection(popGalleryIndex);

		LinearLayout vv = (LinearLayout) findViewById(R.id.app_info_all_rl);
		vv.setOnClickListener(this);
		// 加上下面两行可以用back键关闭popupwindow，否则必须调用dismiss();
		ColorDrawable dw = new ColorDrawable(-00000);
		mImgPop.setBackgroundDrawable(dw);
		mImgPop.showAtLocation(vv, Gravity.CENTER, 0, 0);
		mImgPop.update();

		mPopIndicationDotList = (IndicationDotList) mImgView.findViewById(R.id.pop_dot_list);
		mPopIndicationDotList.setCount( mProductDetail.livingItemPicture.size());
	}
	OnClickListener CloseClickListener = new OnClickListener() {
		public void onClick(View v) {
			if (mImgPop != null && mImgPop.isShowing())
			{
				mImgPop.dismiss();
			}

		}
	};
	void freeResource()
	{
		mThread.stopRun() ;
		Log.d("TAG", "ProductDetailActivity freeResource ******") ;
		if(mProductDetail == null)	return ;
		mAdapter.freeDrawable() ;
		mMyGallery.setAdapter(null) ;
	}
	
	void freeResource1(){
		mThread.stopRun() ;
		Log.d("TAG", "ProductDetailActivity freeResource ******") ;
		if(mProductDetail == null)	return ;
		mAdapter.freeDrawable() ;
		mMyGallery.setAdapter(null) ;
	}
	@Override
	public void onClick(View v)
	{
		if (mImgPop != null && mImgPop.isShowing())
		{
			mImgPop.dismiss();
		}
	}
	
}
