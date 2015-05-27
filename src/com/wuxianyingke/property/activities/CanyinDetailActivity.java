package com.wuxianyingke.property.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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
import com.wuxianyingke.property.adapter.ImageAdapter;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.remote.RemoteApi.FleaContent;
import com.wuxianyingke.property.remote.RemoteApi.LivingItem;
import com.wuxianyingke.property.remote.RemoteApi.NetInfo;
import com.wuxianyingke.property.threads.GetCanyinDetailThread;
import com.wuxianyingke.property.threads.GetProductDetailThread;
import com.wuxianyingke.property.views.IndicationDotList;
import com.wuxianyingke.property.views.MyGallery;


public class CanyinDetailActivity extends Activity   implements OnClickListener{

	private int mLivingItemID = 0 ;
	private int popGalleryIndex = 0; // pop弹出的是那张图片
	private String source = "";
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
	private TextView mNameTextView,mTuanNumberTextView,mRenjunTextView,mDianhuaTextView,mJuanTextView,mDizhiTextView;
	private LinearLayout mTuanLinearLayout,mJuanLinearLayout;
	private ImageView TuanImageView, JuanImageView;
	private ProgressDialog mWaitLoading = null ;
	
	private GetCanyinDetailThread mThread = null ;
	
	private Button LianxiButton ;
	
	private int propertyid;
	private ScrollView mAllViewSv = null ;
	private TextView mIsVisitorTextView;
	private SharedPreferences saving=null ;
	ImageView cartImageview;
	private Button topbar_left,topbar_button_right;
	private TextView topbar_txt;
	private String telephone;
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
					mAdapter = new ImageAdapter(CanyinDetailActivity.this , 
							params , ImageView.ScaleType.CENTER_CROP) ;
					mMyGallery.setAdapter(mAdapter) ;
					mNameTextView.setText(mProductDetail.LivingItemName) ;
					topbar_txt.setText(mProductDetail.LivingItemName) ;
					if(mProductDetail.has_deal>0)
						mTuanNumberTextView.setText(String.valueOf(mProductDetail.deals.size())+"项") ;
					else
						TuanImageView.setVisibility(View.INVISIBLE);
					//mRenjunTextView.setText(String.valueOf(mProductDetail.avg_price)) ;
					
					mDianhuaTextView.setText("预约电话："+mProductDetail.telephone) ;
					telephone=mProductDetail.telephone;
					mDizhiTextView.setText("地址："+mProductDetail.address);
					if(mProductDetail.has_coupon>0)
						mJuanTextView.setText(mProductDetail.coupon.coupon_description) ;
					else
						JuanImageView.setVisibility(View.INVISIBLE);
					
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
					Toast.makeText(CanyinDetailActivity.this, mErrorInfo.desc, Toast.LENGTH_SHORT).show();
				}else
				{
					Toast.makeText(CanyinDetailActivity.this, "读取失败，可能网络问题或服务器无反应", Toast.LENGTH_SHORT).show();
				}
				break;
			}
		}
	} ;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState) ;
		setContentView(R.layout.canyin_detail) ;


		mLivingItemID = getIntent().getIntExtra(Constants.CANYIN_ID_ACTION , 0) ;

		SharedPreferences saving = this.getSharedPreferences(
				LocalStore.USER_INFO, 0);
		propertyid = (int) saving.getLong(LocalStore.PROPERTY_ID, 0);

		topbar_txt= (TextView) findViewById(R.id.topbar_txt) ;
		topbar_left=(Button)findViewById(R.id.topbar_left);
		topbar_left.setVisibility(View.VISIBLE);

		favorite_flag = getIntent().getIntExtra(Constants.FAVORITE_FLAT , 0) ;
		Log.d("MyTag", "GanyinDetailOwn favorite_flag="+favorite_flag);

		topbar_left.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// TODO Auto-generated method stub
				if (0 != favorite_flag) {
					Intent intent = new Intent();
					intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
							| Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.setClass(CanyinDetailActivity.this, CollectionListActivity.class);
					intent.putExtra(Constants.SHOUCANG_FLAT, shoucang_flag);
					startActivity(intent);
				} else {
					finish();
				}

			}
		});

//		topbar_left.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				finish();
//			}
//		});
//
		topbar_button_right = (Button) findViewById(R.id.topbar_button_right) ;
		topbar_button_right.setVisibility(View.VISIBLE);
		LivingDB db = new LivingDB(this);
		shoucangStatus=db.getOneItem(mLivingItemID);
//		Log.d("MyTag","--------shoucangStatus="+shoucangStatus);
		db.close();	
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
				LivingDB db = new LivingDB(CanyinDetailActivity.this);
				mProductDetail.flag=shoucang_flag;

//				Log.d("MyTag", "deleteOneApp2 ----- ----------shoucang_flag=" + shoucang_flag);
//				Log.d("MyTag", "deleteOneApp2 ----- ----------shoucang_flag=" + mProductDetail);
//				Log.d("MyTag", "deleteOneApp2 ----- ----------shoucangStatus=" + shoucangStatus);

				if(-1!=shoucangStatus)
				{

					shoucangStatus = -1;
					db.deleteOneApp(mProductDetail.LivingItemID);
					topbar_button_right.setBackgroundResource(R.drawable.no_shoucang);
					Toast.makeText(CanyinDetailActivity.this, "取消收藏成功", Toast.LENGTH_SHORT).show();
				}else
				{


					db.insertOneItem(mProductDetail);
					shoucangStatus=db.getOneItem(mLivingItemID);
					topbar_button_right.setBackgroundResource(R.drawable.shoucang);
					Toast.makeText(CanyinDetailActivity.this, "收藏成功", Toast.LENGTH_SHORT).show();
				}
				db.close();		
			}
		});
		mLivingItemID = getIntent().getIntExtra(Constants.CANYIN_ID_ACTION , 0) ;
		shoucang_flag= getIntent().getIntExtra(Constants.SHOUCANG_FLAT , 0) ;
		source = getIntent().getStringExtra(Constants.CANYIN_SOURCE_ACTION);
		Log.d("MyTag", "mProductId="+mLivingItemID);
		mAllViewSv = (ScrollView) findViewById(R.id.all_view_sv) ;
		mMyGallery = (MyGallery) findViewById(R.id.detail_gallery) ;
		mMyGallery.setSpacing(18) ;
		mMyGallery.setOnItemSelectedListener(gallerySelectListener) ;
		mMyGallery.setOnItemClickListener(galleryClickListener) ;
		mDotList = (IndicationDotList) findViewById(R.id.detail_indication) ;
		mNameTextView= (TextView) findViewById(R.id.NameTextView) ;
		mTuanNumberTextView= (TextView) findViewById(R.id.TuanNumberTextView) ;
		mRenjunTextView= (TextView) findViewById(R.id.RenjunTextView) ;
		mDianhuaTextView= (TextView) findViewById(R.id.DianhuaTextView) ;
		mDizhiTextView= (TextView) findViewById(R.id.DizhiTextView) ;
		
		mJuanTextView= (TextView) findViewById(R.id.JuanTextView) ;
		LianxiButton = (Button) findViewById(R.id.LianxiButton) ;
		TuanImageView = (ImageView) findViewById(R.id.TuanImageView) ;
		JuanImageView = (ImageView) findViewById(R.id.JuanImageView) ;
		mTuanLinearLayout = (LinearLayout) findViewById(R.id.TuanLinearLayout) ;
		LianxiButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				 Uri uri = Uri.parse("tel:"
	                        + telephone);
	                Intent it = new Intent(Intent.ACTION_CALL, uri);
	                startActivity(it);

				
				/*long uid=LocalStore.getUserInfo().userId;*/
			}
		}) ;
		mTuanLinearLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(CanyinDetailActivity.this,
						CanyinDetailTuanListActivity.class);
				 intent.putExtra(Constants.CANYIN_ID_ACTION, mLivingItemID);
				 intent.putExtra(Constants.CANYIN_SOURCE_ACTION, source);
				 CanyinDetailActivity.this.startActivity(intent);
				
			}
		}) ;
		/*if (LocalStore.getIsVisitor(this)) {
			mContactPublisherButton.setClickable(false);
			mIsVisitorTextView.setVisibility(View.VISIBLE);
		}*/
		showDialog() ;
		mThread = new GetCanyinDetailThread(this, mHandler, propertyid,mLivingItemID,source) ;
		mThread.start() ;
		
		
	}
	@Override
	protected void onStart()
	{
		super.onStart();
	}
	
	
	private void showDialog(){
		mWaitLoading = new ProgressDialog(CanyinDetailActivity.this);
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
