package com.wuxianyingke.property.activities;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mantoto.property.R;
import com.wuxianyingke.property.adapter.FleaOwnerAdapter;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.common.Util;
import com.wuxianyingke.property.remote.RemoteApi.Flea;
import com.wuxianyingke.property.threads.FleaOwnerThread;

public class FleaOwnerActivity extends BaseActivityWithRadioGroup {

	private enum ProductListTab {
		LIST, GRID
	}

	private enum DataSource {
		CATE_SOURCE,
	}

	private ListView mProductLv;
	private ArrayList<Flea> mDataList = new ArrayList<Flea>();
	private FleaOwnerAdapter mListAdapter; // TODO 写重复了一个adapter
	private Button mLeftTopButton, mRightTopButton;
	private FleaOwnerThread mThread = null;

	private boolean isLoading = false;
	private boolean isComplete = false;

	private int page = 1;
	private Long cateId;
	private int sortType = 1;
	private SharedPreferences saving;
	private ProgressDialog mWaitLoading;
	private int propertyid;
    private TextView topbar_txt;
	private ProductListTab mTab = ProductListTab.LIST;

	private DataSource mDataSource = DataSource.CATE_SOURCE;

	// 品牌商品列表

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constants.MSG_GET_PRODUCT_LIST_FINISH:
				if (mWaitLoading != null && mWaitLoading.isShowing()) {
					mWaitLoading.dismiss();
				}
				isLoading = false;
				if (mDataSource == DataSource.CATE_SOURCE) {
					if (mThread != null && mThread.getProductList() != null) {
						int count = mThread.getProductList().size();
						for (int i = 0; i < count; ++i) {
							mDataList.add(mThread.getProductList().get(i));
						}
						if (mListAdapter == null) {
							if (count == 0) {
								((TextView) findViewById(R.id.empty_tv))
										.setVisibility(View.VISIBLE);
							}
							mListAdapter = new FleaOwnerAdapter(
									FleaOwnerActivity.this, mDataList,
									propertyid, saving.getLong(
											LocalStore.USER_ID, 0), mHandler);
							mProductLv.setAdapter(mListAdapter);

						} else {
							mListAdapter.notifyDataSetChanged();
						}
					}
				}
				break;
			case Constants.MSG_GET_PRODUCT_LIST_EMPTY:
				if (mWaitLoading != null && mWaitLoading.isShowing()) {
					mWaitLoading.dismiss();
				}
				((TextView) findViewById(R.id.empty_tv))
						.setVisibility(View.VISIBLE);
				break;
			case Constants.MSG_GET_PRODUCT_LIST_NET_ERROR:
				if (mWaitLoading != null && mWaitLoading.isShowing()) {
					mWaitLoading.dismiss();
				}
				isLoading = false;
				View view = (View) findViewById(R.id.view_network_error);
				view.setVisibility(View.VISIBLE);
				break;
			case Constants.MSG_GET_PRODUCT_DETAIL_FAILD:
				if (mWaitLoading != null && mWaitLoading.isShowing()) {
					mWaitLoading.dismiss();
				}
				isLoading = false;
				break;
			case Constants.MSG_GET_SEARCH_ICON_FINISH:
			case Constants.MSG_GET_PRODUCT_DETAIL_IMG_FINISH:
				if (mListAdapter != null)
					mListAdapter.notifyDataSetChanged();
				break;
			case 3:
				showDialog();
				break;
			case 200:

				if (mWaitLoading != null && mWaitLoading.isShowing()) {
					mWaitLoading.dismiss();
				}
				mDataList.remove(msg.arg1);
				if (mListAdapter != null)
					mListAdapter.notifyDataSetChanged();
				Toast.makeText(FleaOwnerActivity.this, "删除成功！",
						Toast.LENGTH_SHORT).show();
				break;
			case 500:
				if (mWaitLoading != null && mWaitLoading.isShowing()) {
					mWaitLoading.dismiss();
				}
				Toast.makeText(FleaOwnerActivity.this, "删除失败！",
						Toast.LENGTH_SHORT).show();
				break;
			case 404:
				if (mWaitLoading != null && mWaitLoading.isShowing()) {
					mWaitLoading.dismiss();
				}
				Toast.makeText(FleaOwnerActivity.this, "通讯错误，请检查网络或稍后再试！",
						Toast.LENGTH_SHORT).show();
				break;
			}

		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.flea_owner);
		saving = this.getSharedPreferences(LocalStore.USER_INFO, 0);
		propertyid = (int) saving.getLong(LocalStore.PROPERTY_ID, 0);
		initRadioGroup(R.id.radioGroup, R.id.toggle_radio1, 2);
		ImageView cartImageview = (ImageView) findViewById(R.id.cart_imageview);
		Util.modifyCarNumber(this, cartImageview);
		topbar_txt= (TextView) findViewById(R.id.topbar_txt);
		topbar_txt.setText("我的商品");
		mLeftTopButton = (Button) findViewById(R.id.topbar_left);
		mLeftTopButton.setText("返回");
		mLeftTopButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		cateId = getIntent().getLongExtra(Constants.CATE_ID_ACTION, -1L);

		mProductLv = (ListView) findViewById(R.id.product_list_view);
		startThread();
	}

	private void showDialog() {
		mWaitLoading = new ProgressDialog(FleaOwnerActivity.this);
		String msg = getResources().getString(R.string.txt_loading);
		mWaitLoading.setMessage(msg);
		mWaitLoading.setCancelable(false);
		mWaitLoading.show();
	}

	private void startThread() {
		mDataList.clear();
		if (mListAdapter != null) {
			freeResource();
			mListAdapter = null;
			mProductLv.setAdapter(null);
		}
		if (mDataSource == DataSource.CATE_SOURCE) {
			mThread = new FleaOwnerThread(FleaOwnerActivity.this, mHandler,
					propertyid, saving.getLong(LocalStore.USER_ID, 0));
			mThread.start();
		}
		isLoading = true;
		showDialog();
	}

	@Override
	public void freeResource() {
		int count = mDataList.size();
		for (int i = 0; i < count; ++i) {
			BitmapDrawable bd = (BitmapDrawable) mDataList.get(i).frontCover.imgDw;
			if (bd != null && !bd.getBitmap().isRecycled()) {
				bd.getBitmap().recycle();
			}
		}
	}

}
