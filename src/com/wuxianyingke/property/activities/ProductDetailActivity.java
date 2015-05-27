package com.wuxianyingke.property.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.mantoto.property.R;
import com.wuxianyingke.property.adapter.ImageAdapter;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.remote.RemoteApi.FleaContent;
import com.wuxianyingke.property.remote.RemoteApi.NetInfo;
import com.wuxianyingke.property.threads.GetProductDetailThread;
import com.wuxianyingke.property.views.IndicationDotList;
import com.wuxianyingke.property.views.MyGallery;


public class ProductDetailActivity extends BaseActivityWithRadioGroup  implements OnClickListener{

	private Long mProductId = null ;
	private int popGalleryIndex = 0; // pop弹出的是那张图片
	private FleaContent mProductDetail ;
	
	private MyGallery mMyGallery = null;
	private IndicationDotList mDotList = null;
	
	private ImageAdapter mAdapter = null ;
	private PopupWindow mImgPop = null;
	private View mImgView = null;
	private MyGallery mPopGallery = null;
	private IndicationDotList mPopIndicationDotList = null;
	private Button mPopDownloadBtn = null;
	private ImageAdapter mPopAdapter = null;
	private TextView mFleaNameTextView,mContactNameTextView,mContactTimeTextView,mFleaContentTextView;
	private ProgressDialog mWaitLoading = null ;
	
	private GetProductDetailThread mThread = null ;
	
	private int propertyid;
	private ScrollView mAllViewSv = null ;
	private TextView mIsVisitorTextView;
	private SharedPreferences saving=null ;
	ImageView cartImageview;
	private TextView topbar_right,topbar_txt;
	private Button topbar_left;
	
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
                     if(saving.getLong(LocalStore.USER_ID, 0)== mProductDetail.flea.userID)
                     {
                    	 topbar_right.setVisibility(View.GONE);
                     }
					Gallery.LayoutParams params = new Gallery.LayoutParams(220, 220) ;
					mAdapter = new ImageAdapter(ProductDetailActivity.this , 
							params , ImageView.ScaleType.CENTER_CROP) ;
					mMyGallery.setAdapter(mAdapter) ;
					mFleaNameTextView.setText(mProductDetail.flea.header) ;
					mContactNameTextView.setText(mProductDetail.username) ;
					mContactTimeTextView.setText(mProductDetail.flea.cTime) ;
					mFleaContentTextView.setText(mProductDetail.flea.description) ;
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
				Log.d("MyTag","mProductDetail.fleaPictureArray.get(i)"+mProductDetail.fleaPictureArray.get(msg.arg1).path);
				Log.d("MyTag","msg.arg1"+msg.arg1);
				mAdapter.addImg(mProductDetail.fleaPictureArray.get(msg.arg1).imgDw) ;
				mDotList.setCount(mDotList.getCount()+1) ;
				break ;
			case Constants.MSG_ADD_FAVORITE_FINISH:
				if(mWaitLoading != null && mWaitLoading.isShowing()){
					mWaitLoading.dismiss() ;
				}


				Log.d("收藏","msg.arg1"+msg.arg1);


				NetInfo mErrorInfo=(NetInfo) msg.obj;
				if(!"".equals(mErrorInfo.desc)&&mErrorInfo.desc!=null)
				{
					Toast.makeText(ProductDetailActivity.this, mErrorInfo.desc, Toast.LENGTH_SHORT).show();
				}else
				{
					Toast.makeText(ProductDetailActivity.this, "读取失败，可能网络问题或服务器无反应", Toast.LENGTH_SHORT).show();
				}
				break;
			}
		}
	} ;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState) ;
		setContentView(R.layout.product_detail) ;
		 saving = this.getSharedPreferences(LocalStore.USER_INFO, 0);
		propertyid=(int) saving.getLong(LocalStore.PROPERTY_ID, 0);
		topbar_left=(Button)findViewById(R.id.topbar_left);
		topbar_left.setVisibility(View.VISIBLE);
		topbar_left.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		topbar_txt = (TextView)findViewById(R.id.topbar_txt);
		topbar_txt.setText("宝贝详情");
		topbar_right = (TextView)findViewById(R.id.topbar_right);
		topbar_right.setText("联系");
		topbar_right.setVisibility(View.VISIBLE);
		topbar_right.setClickable(true);
		topbar_right.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				//提交
				Intent intent = new Intent();
				intent.setClass(ProductDetailActivity.this, ContactPublisherActivity.class);
				intent.putExtra("toActivity", "ProductDetailActivity");
				intent.putExtra("fleaid",mProductDetail.flea.fleaID);
				intent.putExtra("conatctname",mProductDetail.username);
				Log.d("conatcctName", "mConatctName="+mProductDetail.username);
				startActivity(intent);
			}
		});
		
		
		mProductId = getIntent().getLongExtra(Constants.PRODUCT_ID_ACTION , -1) ;
		Log.d("MyTag", "mProductId="+mProductId);
		mAllViewSv = (ScrollView) findViewById(R.id.all_view_sv) ;
		mMyGallery = (MyGallery) findViewById(R.id.detail_gallery) ;
		mMyGallery.setSpacing(18) ;
		mMyGallery.setOnItemSelectedListener(gallerySelectListener) ;
		mMyGallery.setOnItemClickListener(galleryClickListener) ;
		mDotList = (IndicationDotList) findViewById(R.id.detail_indication) ;
		
		mFleaNameTextView= (TextView) findViewById(R.id.FleaNameTextView) ;
		mContactNameTextView= (TextView) findViewById(R.id.ContactNameTextView) ;
		mContactTimeTextView= (TextView) findViewById(R.id.ContactTimeTextView) ;
		mFleaContentTextView= (TextView) findViewById(R.id.FleaContentTextView) ;
		mIsVisitorTextView = (TextView) findViewById(R.id.IsVisitorTextView);
		
		if (LocalStore.getIsVisitor(this)) {
		//	mContactPublisherButton.setClickable(false);
			mIsVisitorTextView.setVisibility(View.VISIBLE);
		}
		showDialog() ;
		mThread = new GetProductDetailThread(this, mHandler, propertyid,mProductId) ;
		mThread.start() ;
		
		
	}
	@Override
	protected void onStart()
	{
		super.onStart();
	}
	
	
	private void showDialog(){
		mWaitLoading = new ProgressDialog(ProductDetailActivity.this);
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
			for (int i = mProductDetail.fleaPictureArray.size() - 1; i >= 0; --i) 
		{
			Log.d("MyTag","mProductDetail.fleaPictureArray.get(i)"+mProductDetail.fleaPictureArray.get(i).path);
			mPopAdapter.addImg(mProductDetail.fleaPictureArray.get(i).imgDw);
		}

		mPopGallery.setAdapter(mPopAdapter);
		mPopGallery.setSelection(popGalleryIndex);

		RelativeLayout vv = (RelativeLayout) findViewById(R.id.app_info_all_rl);
		vv.setOnClickListener(this);
		// 加上下面两行可以用back键关闭popupwindow，否则必须调用dismiss();
		ColorDrawable dw = new ColorDrawable(-00000);
		mImgPop.setBackgroundDrawable(dw);
		mImgPop.showAtLocation(vv, Gravity.CENTER, 0, 0);
		mImgPop.update();

		mPopIndicationDotList = (IndicationDotList) mImgView.findViewById(R.id.pop_dot_list);
		mPopIndicationDotList.setCount( mProductDetail.fleaPictureArray.size());
	}
	OnClickListener CloseClickListener = new OnClickListener() {
		public void onClick(View v) {
			if (mImgPop != null && mImgPop.isShowing())
			{
				mImgPop.dismiss();
			}

		}
	};
	@Override
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
