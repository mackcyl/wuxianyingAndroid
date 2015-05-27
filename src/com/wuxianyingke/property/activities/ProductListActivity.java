package com.wuxianyingke.property.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mantoto.property.R;
import com.mantoto.property.R.id;
import com.mantoto.property.R.layout;
import com.mantoto.property.R.string;
import com.wuxianyingke.property.adapter.FleaOwnerAdapter;
import com.wuxianyingke.property.adapter.ProductListAdapter;
import com.wuxianyingke.property.adapter.PropertyNotificationAdapter;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.common.LogUtil;
import com.wuxianyingke.property.common.Util;
import com.wuxianyingke.property.remote.RemoteApi.Flea;
import com.wuxianyingke.property.remote.RemoteApi.ProductBase;
import com.wuxianyingke.property.remote.RemoteApi.PropertyNotificationInfo;
import com.wuxianyingke.property.threads.FleaOwnerThread;
import com.wuxianyingke.property.threads.GetProductListThread;
import com.wuxianyingke.property.threads.PropertyNotificationThread;

public class ProductListActivity extends Activity{

	private enum ProductListTab {
		LIST, GRID
	}

	private enum DataSource {
		CATE_SOURCE,
	}

	private ListView mLogsListView_all;
	private ListView mLogsListView_my;
	private ArrayList<Flea> mallDataList = new ArrayList<Flea>();
	private ArrayList<Flea> mmyDataList = new ArrayList<Flea>();
	private static ProductListAdapter mAllListAdapter; // TODO 写重复了一个adapter
	private GetProductListThread mAllThread = null;

	private FleaOwnerAdapter mMyListAdapter; // TODO 写重复了一个adapter
	private FleaOwnerThread mMyThread = null;
	
	private Button topbar_left;
	private TextView topbar_txt,topbar_right;
	private TextView my_product,all_product;
	private View my_product_line,all_product_line;
	private int page = 0;

	private ProgressDialog mProgressDialog;
	private int propertyid;
	private SharedPreferences saving;
	private ProductListTab mTab = ProductListTab.LIST;

	private DataSource mDataSource = DataSource.CATE_SOURCE;

	// 品牌商品列表

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constants.MSG_GET_PRODUCT_LIST_FINISH:
				if (mProgressDialog != null && mProgressDialog.isShowing()) {
					mProgressDialog.dismiss();
				}
				searchLayout1.setVisibility(View.GONE);
				searchLayout2.setVisibility(View.GONE);
				if(mAllThread!=null)
					showLogsListView(mAllThread.getProductList(),0);
				if(mMyThread!=null)
					showLogsListView(mMyThread.getProductList(),1);
				break;
			case Constants.MSG_GET_PRODUCT_LIST_EMPTY:
				if (mProgressDialog != null && mProgressDialog.isShowing()) {
					mProgressDialog.dismiss();
				}
				searchLayout1.setVisibility(View.GONE);
				searchLayout2.setVisibility(View.GONE);
				((TextView) findViewById(R.id.empty_tv))
						.setVisibility(View.VISIBLE);
				break;
			case Constants.MSG_NETWORK_ERROR:
				if (mProgressDialog != null && mProgressDialog.isShowing()) {
					mProgressDialog.dismiss();
				}
				if(mPageNum == 1)
				{
					View view = findViewById(R.id.view_network_error);
					view.setVisibility(View.VISIBLE);
				}
				else
				{
					LogUtil.d("MyTag","MSG_NETWORK_ERROR");
					mPageNum--;
					mAllowGetLogAgain = false;
					searchLayout1.setVisibility(View.GONE);
					searchLayout2.setVisibility(View.GONE);
				}
				break;
			case Constants.MSG_GET_SEARCH_ICON_FINISH:
			case Constants.MSG_GET_PRODUCT_DETAIL_IMG_FINISH:
				if (mMyListAdapter != null)
					mMyListAdapter.notifyDataSetChanged();
				if (mAllListAdapter != null)
					mAllListAdapter.notifyDataSetChanged();
				break;
			case 200:

				mmyDataList.remove(msg.arg1);
				if (mMyListAdapter != null)
					mMyListAdapter.notifyDataSetChanged();
				Toast.makeText(ProductListActivity.this, "删除成功！",
						Toast.LENGTH_SHORT).show();
				break;
			case 500:
				Toast.makeText(ProductListActivity.this, "删除失败！",
						Toast.LENGTH_SHORT).show();
				break;
			case 404:
				Toast.makeText(ProductListActivity.this, "通讯错误，请检查网络或稍后再试！",
						Toast.LENGTH_SHORT).show();
				searchLayout1.setVisibility(View.GONE);
				searchLayout2.setVisibility(View.GONE);
				break;
			}
			super.handleMessage(msg);
		}

	};

	private void setListPage(int p) {
		page=p;
		showDialog();
		endChildrenThreads();
		mAllowGetLogAgain = false;
		searchLayout1.setVisibility(View.VISIBLE);
		searchLayout2.setVisibility(View.VISIBLE);

		if(page == 0)
		{
			all_product.setTextColor(Color.parseColor("#00a4f4"));
			my_product.setTextColor(Color.parseColor("#666666"));
			all_product_line.setBackgroundColor(Color.parseColor("#00a4f4"));
			my_product_line.setBackgroundColor(Color.parseColor("#666666"));		
			mLogsListView_my.setVisibility(View.GONE);
			mLogsListView_all.setVisibility(View.VISIBLE);	
			
			mAllThread = new GetProductListThread(ProductListActivity.this,
					mHandler, propertyid, mPageNum);
			mAllThread.start();		
		}
		else if(page == 1)
		{

			my_product.setTextColor(Color.parseColor("#00a4f4"));
			all_product.setTextColor(Color.parseColor("#666666"));
			my_product_line.setBackgroundColor(Color.parseColor("#00a4f4"));
			all_product_line.setBackgroundColor(Color.parseColor("#666666"));	
			mLogsListView_all.setVisibility(View.GONE);
			mLogsListView_my.setVisibility(View.VISIBLE);

			mMyThread = new FleaOwnerThread(ProductListActivity.this, mHandler,
					propertyid, saving.getLong(LocalStore.USER_ID, 0));
			
			mMyThread.start();	
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.product_list);
		saving = this.getSharedPreferences(
				LocalStore.USER_INFO, 0);
		propertyid = (int) saving.getLong(LocalStore.PROPERTY_ID, 0);
		ImageView cartImageview = (ImageView) findViewById(R.id.cart_imageview);
		Util.modifyCarNumber(this, cartImageview);
		topbar_txt= (TextView) findViewById(R.id.topbar_txt);
		topbar_txt.setText("跳蚤市场");
		topbar_left = (Button) findViewById(R.id.topbar_left);
		topbar_left.setVisibility(View.VISIBLE);
		topbar_left.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		topbar_right = (TextView) findViewById(R.id.topbar_right);
		topbar_right.setText("添加");
		topbar_right.setVisibility(View.VISIBLE);
//		topbar_left.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				Intent intent = new Intent();
//				intent.setClass(ProductListActivity.this,
//						FleaOwnerActivity.class);
//				intent.putExtra("toActivity", "ProductDetailActivity");
//				startActivity(intent);
//			}
//		});
		topbar_right.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(LocalStore.getIsVisitor(getApplicationContext()))
				{
					Toast.makeText(getApplicationContext(), "游客或者未认证用户无法完成此操作",
							Toast.LENGTH_SHORT).show();
					return;
				}
				Intent intent = new Intent();
				intent.setClass(ProductListActivity.this,
						ReleaseGoodsActivity.class);
				intent.putExtra("toActivity", "ProductDetailActivity");
				startActivity(intent);
			}
		});

		
		mLogsListView_all = (ListView) findViewById(R.id.product_list_view_all);
		mLogsListView_all.setVerticalScrollBarEnabled(false);
		mLogsListView_all.setDivider(getResources().getDrawable(R.drawable.list_line));
		mLogsListView_all.addFooterView(showLayout(1));
		mLogsListView_all.setOnScrollListener(mScrollListner);
		mAllThread = new GetProductListThread(ProductListActivity.this,
				mHandler, propertyid, mPageNum);
		
		mLogsListView_my = (ListView) findViewById(R.id.product_list_view_my);
		mLogsListView_my.setVerticalScrollBarEnabled(false);
		mLogsListView_my.setDivider(getResources().getDrawable(R.drawable.list_line));
		mLogsListView_my.addFooterView(showLayout(0));
		mLogsListView_my.setOnScrollListener(mScrollListner);
		mMyThread = new FleaOwnerThread(ProductListActivity.this,
				mHandler, propertyid, mPageNum);
		


		my_product = (TextView) findViewById(R.id.my_product);
		all_product = (TextView) findViewById(R.id.all_product);
		my_product_line = (View) findViewById(R.id.my_product_line);
		all_product_line = (View) findViewById(R.id.all_product_line);

		setListPage(0);

		my_product.setClickable(true);
		my_product.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(LocalStore.getIsVisitor(getApplicationContext()))
				{
					Toast.makeText(getApplicationContext(), "游客或者未认证用户无法完成此操作",
							Toast.LENGTH_SHORT).show();
					return;
				}
				if(page != 1)
					setListPage(1);
			}
		});
		all_product.setClickable(true);
		all_product.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(page != 0)
					setListPage(0);
			}
		});
	}
	protected void onRestart()
	{
		if(mLogsListView_all!=null)
			mLogsListView_all.invalidateViews();
		if(mLogsListView_my!=null)
			mLogsListView_my.invalidateViews();
		super.onRestart();
	}
	@Override
	protected void onNewIntent(Intent intent)
	{
		Boolean needInit = intent.getBooleanExtra("FromGroup", false);
		if(needInit)
		{
			//重新获取资源
			setListPage(0);
//			showDialog();
//			endChildrenThreads();
//			mAllowGetLogAgain = false;
//			searchLayout.setVisibility(View.VISIBLE);
//			
//			mThread = new GetProductListThread(ProductListActivity.this,
//					mHandler, propertyid, mPageNum);
//			mThread.start();
//			LogUtil.d("MyTag", "Radio2Activity.this onNewIntent");
		}
		super.onNewIntent(intent);
	}
	
	@Override
	protected void onDestroy()
	{
		endChildrenThreads();
		super.onDestroy();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		//startThread();
		super.onResume();
	}

	private void showDialog() {
		View view = findViewById(R.id.view_network_error);
		view.setVisibility(View.GONE);
		mLogsListView_all.setVisibility(View.GONE);
		mLogsListView_my.setVisibility(View.GONE);
		if (mProgressDialog != null)
			mProgressDialog.dismiss();
		mProgressDialog = new ProgressDialog(ProductListActivity.this);
		mProgressDialog.setMessage("加载中，请稍候...");
		mProgressDialog.setCancelable(true);
		mProgressDialog.show();
	}


	public void freeResource() {
		int count = mallDataList.size();
		for (int i = 0; i < count; ++i) {
			BitmapDrawable bd = (BitmapDrawable) mallDataList.get(i).frontCover.imgDw;
			if (bd != null && !bd.getBitmap().isRecycled()) {
				bd.getBitmap().recycle();
			}
		}
		
		count = mmyDataList.size();
		for (int i = 0; i < count; ++i) {
			BitmapDrawable bd = (BitmapDrawable) mmyDataList.get(i).frontCover.imgDw;
			if (bd != null && !bd.getBitmap().isRecycled()) {
				bd.getBitmap().recycle();
			}
		}
		mMyThread = null ;
		mAllThread = null ;
		showDialog();
		endChildrenThreads();
	}

	public void showLogsListView(List<Flea> list,int p) 
	{
		ListView mLogsListView = null;
		if(p != page)
		{
			searchLayout1.setVisibility(View.GONE);
			searchLayout2.setVisibility(View.GONE);
			return;
		}
		else
		{
			searchLayout1.setVisibility(View.GONE);
			searchLayout2.setVisibility(View.GONE);
			if(page == 0)
				mLogsListView=mLogsListView_all;
			else if( page == 1)
				mLogsListView=mLogsListView_my;				
		}
		
		
		if (list == null)
		{
			if (mProgressDialog != null)
			{
				mProgressDialog.dismiss();
				mProgressDialog = null;
				
			}
			return;
		}
		mLogsListView.setVisibility(View.VISIBLE);
		if(mPageNum == 1)
		{
			

			if(page == 0)
			{
				
				
				mAllListAdapter = new ProductListAdapter(this, list,mHandler);
				LogUtil.d("MyTag","ProductListActivity-mItemSum mPageNum == 1  mLogAdapter.getCount()="+mAllListAdapter.getCount());
				if (mProgressDialog != null)
				{
					mProgressDialog.dismiss();
					mProgressDialog = null;
				}
				mLogsListView_all.setAdapter(mAllListAdapter);				
			}	
			else if( page == 1)
			{
				
				if (mMyThread != null && mMyThread.getProductList() != null) {
					int count = mMyThread.getProductList().size();
					for (int i = 0; i < count; ++i) {
						mmyDataList.add(mMyThread.getProductList().get(i));
					}
						if (count == 0) {
							((TextView) findViewById(R.id.empty_tv))
									.setVisibility(View.VISIBLE);
						}
						mMyListAdapter = new FleaOwnerAdapter(
								this, mmyDataList,
								propertyid, saving.getLong(
										LocalStore.USER_ID, 0), mHandler);
						mLogsListView_my.setAdapter(mMyListAdapter);

				}
				
//				
//				mMyListAdapter = new FleaOwnerAdapter(
//					this, list,
//					propertyid, this.getSharedPreferences(LocalStore.USER_INFO, 0).getLong(
//							LocalStore.USER_ID, 0), mHandler);
//				LogUtil.d("MyTag","mItemSum mPageNum == 1  mLogAdapter.getCount()="+mMyListAdapter.getCount());
//				if (mProgressDialog != null)
//				{
//					mProgressDialog.dismiss();
//					mProgressDialog = null;
//				}
//				mLogsListView_my.setAdapter(mMyListAdapter);	
			}
			
		}
		else
		{
			if(page == 0)
			{
				mAllListAdapter.appandAdapter(list);
				mAllListAdapter.notifyDataSetChanged();
				LogUtil.d("MyTag","ProductListActivity470-mItemSum = mLogAdapter.getCount()="+mAllListAdapter.getCount());
				int test12 = mItemSum % 10;
				if(test12 == 0)
					mLogsListView_all.setSelection(mItemSum - 10);
				else
					mLogsListView_all.setSelection(mItemSum - test12);
			}
		}
		if(page == 0)
			setAllowGetPageAgain();
	}
	
	// 以下为分页显示代码
	private static LinearLayout searchLayout1 = null;
	private static LinearLayout searchLayout2 = null;
	public LinearLayout showLayout(int i)
	{
		if(i==1)
		{
			searchLayout1 = new LinearLayout(this);
			searchLayout1.setOrientation(LinearLayout.HORIZONTAL);
	
			ProgressBar progressBar = new ProgressBar(this);
			progressBar.setPadding(0, 0, 15, 0);
			progressBar.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress_medium));
			searchLayout1.addView(progressBar, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
	
			TextView textView = new TextView(this);
			textView.setText("加载中...");
			textView.setGravity(Gravity.CENTER_VERTICAL);
			searchLayout1.addView(textView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
			searchLayout1.setGravity(Gravity.CENTER);
	
			LinearLayout loadingLayout = new LinearLayout(this);
			loadingLayout.addView(searchLayout1, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
			loadingLayout.setGravity(Gravity.CENTER);
			return loadingLayout;
		}
		else
		{
			searchLayout2 = new LinearLayout(this);
			searchLayout2.setOrientation(LinearLayout.HORIZONTAL);
	
			ProgressBar progressBar = new ProgressBar(this);
			progressBar.setPadding(0, 0, 15, 0);
			progressBar.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress_medium));
			searchLayout2.addView(progressBar, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
	
			TextView textView = new TextView(this);
			textView.setText("加载中...");
			textView.setGravity(Gravity.CENTER_VERTICAL);
			searchLayout2.addView(textView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
			searchLayout2.setGravity(Gravity.CENTER);
	
			LinearLayout loadingLayout = new LinearLayout(this);
			loadingLayout.addView(searchLayout2, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
			loadingLayout.setGravity(Gravity.CENTER);
			return loadingLayout;			
		}
	}
	
	private static boolean mAllowGetLogAgain = false;
	private static int mItemSum = 0;
	private int mPageNum = 1;
	private final int mPagecount = 10;
	public static void setAllowGetPageAgain()
	{
		LogUtil.d("MyTag", "mItemSummItemSum="+mItemSum);
		if(mAllListAdapter!=null)
			mItemSum = mAllListAdapter.getCount();
		int test12 = mItemSum % 10;
		if((test12 != 0) || (mItemSum >= 96))
		{
			LogUtil.d("MyTag", "mItemSummItemSum"+test12);
			mAllowGetLogAgain = false;
			searchLayout1.setVisibility(View.GONE);
			searchLayout2.setVisibility(View.GONE);
		}
		else
		{
			mAllowGetLogAgain = true;
			LogUtil.d("MyTag", "mItemSummItemSummAllowGetLogAgain = true"+test12);
		}
	}
	
	private OnScrollListener mScrollListner = new OnScrollListener()
	{
		private int lastItem = 0;
		
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
		{
			lastItem = firstVisibleItem + visibleItemCount - 1;
			
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState)
		{
			if (mAllListAdapter != null && lastItem >= (mAllListAdapter.getCount()-2))
			{
				LogUtil.d("MyTag", "Radio2Activity.this mAllowGetLogAgain="+mAllowGetLogAgain);
				if (!mAllowGetLogAgain)
					return;
				mAllowGetLogAgain = false;
				mPageNum++;
				LogUtil.d("MyTag", "Radio2Activity.this onScrollStateChanged");
				mAllThread = new GetProductListThread(ProductListActivity.this,
						mHandler, propertyid, mPageNum);
				mAllThread.start();
			}
		}
	};

	
	private void endChildrenThreads()
	{

		if (mAllThread != null)
		{
			mAllThread.stopRun();
			mAllThread = null;
		}

		if (mMyThread != null)
		{
			mMyThread.stopRun();
			mMyThread = null;
		}
		
		mmyDataList.clear();
		mLogsListView_all.setAdapter(null);
		mLogsListView_my.setAdapter(null);
		
		mAllowGetLogAgain = true;
		mPageNum = 1;
		mItemSum = 0;
	}
//	void initResource() {
//		// TODO Auto-generated method stub
//		LogUtil.d("MyTag", "Radio2Activity.this initResource");
//		freeResource() ;
//		if (mThread == null) 
//		showDialog();
//		mThread = new GetProductListThread(ProductListActivity.this,
//				mHandler, propertyid, mPageNum);
//		mThread.start();
//	}
}
