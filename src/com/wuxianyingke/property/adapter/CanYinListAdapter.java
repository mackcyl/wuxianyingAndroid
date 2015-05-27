package com.wuxianyingke.property.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mantoto.property.R;
import com.wuxianyingke.property.activities.CanyinDetailActivity;
import com.wuxianyingke.property.activities.CanyinDetailOwnActivity;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.remote.RemoteApi.LivingItem;

public class CanYinListAdapter extends BaseAdapter {

	private List<LivingItem> mList;
	private Context mContext;
	private Handler mHandler;
	private boolean mStoped;
	private boolean mIsOnEdit;
	private int mCount;
	private int shoucang_flag;
	private int favorite_flat;

	public CanYinListAdapter(Context ctx, List<LivingItem> list, Handler handler,int shoucang_flag , int favorite_flat) {
		this.mContext = ctx;
		this.mList = list;
		this.mHandler = handler;
		this.mStoped = false;
		this.shoucang_flag=shoucang_flag;
		this.mCount = mList.size();
		this.favorite_flat = favorite_flat;
	}

	public void freeDrawable() {
		mStoped = true;
		Log.d("MyTag", "App bitmaps free !!! ");
		for (int i = 0; i < mList.size(); i++) {
			BitmapDrawable a = (BitmapDrawable) mList.get(i).FrontCover.imgDw;
			if (a != null && !a.getBitmap().isRecycled())
				a.getBitmap().recycle();
			mList.get(i).FrontCover.imgDw = null;
		}
		System.gc();
	}

	public void setIsOnEdit(boolean isOnEdit) {
		mIsOnEdit = isOnEdit;
	}

	public final boolean getIsOnEdit() {
		return mIsOnEdit;
	}

	public void appandAdapter(List<LivingItem> list) {
		for (int i = 0; i < list.size(); i++) {
			mList.add(list.get(i));
			mCount++;
		}

	}

	@Override
	public int getCount() {
		return
		/* mCount */
		mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		if (mStoped)
			return convertView;
		ProductItem productItem;

		Log.d("MyTag","CanYinListAdapter--position="+position);

		final LivingItem info = mList.get(position);
		if (convertView == null) {

			View v = LayoutInflater.from(mContext).inflate(
					R.layout.canyin_list_item, null);
			productItem = new ProductItem();
			productItem.mIcon = (ImageView) v.findViewById(R.id.item_icon);
			productItem.mName = (TextView) v.findViewById(R.id.item_name);
			productItem.mLeixing = (TextView) v.findViewById(R.id.item_leixing);
			productItem.mJuli = (TextView) v.findViewById(R.id.item_juli);
			productItem.mItemBackground = (LinearLayout) v
					.findViewById(R.id.product_list_item_bg);
			productItem.mTuan = (ImageView) v.findViewById(R.id.TuanImageView);
			productItem.mJuan = (ImageView) v.findViewById(R.id.JuanImageView);
			productItem.mHui = (ImageView) v.findViewById(R.id.HuiImageView);
			
			productItem.mRenjun = (TextView) v.findViewById(R.id.item_renjun);
			productItem.mDizhi = (TextView) v.findViewById(R.id.item_dizhi);

			v.setTag(productItem);
			convertView = v;
		} else {
			productItem = (ProductItem) convertView.getTag();
		}
//
//		if (position == 0)
//			productItem.mItemBackground
//					.setBackgroundResource(R.drawable.style_item_top);
//		else if (position == mList.size() - 1)
//			productItem.mItemBackground
//					.setBackgroundResource(R.drawable.style_item_bottom);
//		else
//			productItem.mItemBackground
//					.setBackgroundResource(R.drawable.style_item_center);

		if (info.FrontCover.imgDw == null)
			productItem.mIcon.setImageResource(R.drawable.icon_coo8_default);
		else
			productItem.mIcon.setImageDrawable(info.FrontCover.imgDw);

		if (info.has_deal > 0)
		{
			productItem.mTuan.setImageResource(R.drawable.tuan);
			productItem.mTuan.setVisibility(View.VISIBLE);
		}
		else
			productItem.mTuan.setVisibility(View.GONE);

		if (info.has_coupon > 0)
		{
			productItem.mJuan.setImageResource(R.drawable.juan);
			productItem.mJuan.setVisibility(View.VISIBLE);
		}
		else
			productItem.mJuan.setVisibility(View.GONE);
		if (info.has_activity > 0)
		{
			productItem.mHui.setImageResource(R.drawable.hui);
			productItem.mHui.setVisibility(View.VISIBLE);
		}
		else
			productItem.mHui.setVisibility(View.GONE);

		
		productItem.mName.setText(info.LivingItemName);
		productItem.mLeixing.setText(info.categories);
		productItem.mDizhi.setText(info.address);
		
		//productItem.mRenjun.setText("￥:"+info.avg_price+"/人");
		productItem.mRenjun.setText("");
		Log.d("MyTag","CanYinListAdapter--info.distance="+info.distance);
		Log.d("MyTag","productItem.mJuli="+productItem.mJuli);
		if(info.distance<=999)
		{

			productItem.mJuli.setText(""+String.valueOf(info.distance)+"m");
		}
		else
		{
			double price = (double)(info.distance)/1000;
			productItem.mJuli.setText(""+String.valueOf(price)+"km");
		}

		if(info.source.equals("own"))
		{
			productItem.mItemBackground.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mContext,
							CanyinDetailOwnActivity.class);
					intent.putExtra(Constants.CANYIN_ID_ACTION, info.LivingItemID);
					intent.putExtra(Constants.CANYIN_SOURCE_ACTION, info.source);
					intent.putExtra(Constants.SHOUCANG_FLAT, shoucang_flag);
					intent.putExtra(Constants.FAVORITE_FLAT, favorite_flat);

					mContext.startActivity(intent);
				}
			});
		}
		else
		{
			productItem.mItemBackground.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mContext,
							CanyinDetailActivity.class);
					intent.putExtra(Constants.CANYIN_ID_ACTION, info.LivingItemID);
					intent.putExtra(Constants.CANYIN_SOURCE_ACTION, info.source);
					intent.putExtra(Constants.SHOUCANG_FLAT, shoucang_flag);
					intent.putExtra(Constants.FAVORITE_FLAT, favorite_flat);
					mContext.startActivity(intent);
				}
			});
		}

		return convertView;
	}

	class ProductItem {
		LinearLayout mItemBackground;
		ImageView mIcon;
		TextView mName;
		TextView mLeixing;
		TextView mJuli;
		TextView mRenjun;
		TextView mDizhi;
		ImageView mTuan;
		ImageView mJuan;
		ImageView mHui;
	}
}
