package com.wuxianyingke.property.adapter;

import java.util.ArrayList;
import java.util.List;

import com.mantoto.property.R;
import com.wuxianyingke.property.activities.ProductDetailActivity;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.remote.RemoteApi.AppPopularize;
import com.wuxianyingke.property.remote.RemoteApi.Flea;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class AppPopularizeAdapter extends BaseAdapter
{

	private ArrayList<AppPopularize> mList;
	private Context mContext;
	private Handler mHandler;
	private boolean mStoped;
	private boolean mIsOnEdit;
	private int mCount;

	public AppPopularizeAdapter(Context ctx, ArrayList<AppPopularize> list, Handler handler)
	{
		this.mContext = ctx;
		this.mList = list;
		this.mHandler = handler;
		this.mStoped = false;
		this.mCount = mList.size();
	}

	public void freeDrawable()
	{
		mStoped = true;
		Log.d("MyTag", "App bitmaps free !!! ");
		for (int i = 0; i < mList.size(); i++)
		{
			BitmapDrawable a = (BitmapDrawable) mList.get(i).imgDw;
			if (a != null && !a.getBitmap().isRecycled())
				a.getBitmap().recycle();
			mList.get(i).imgDw = null;
		}
		System.gc();
	}

	public void setIsOnEdit(boolean isOnEdit)
	{
		mIsOnEdit = isOnEdit;
	}

	public final boolean getIsOnEdit()
	{
		return mIsOnEdit;
	}

	public void appandAdapter(ArrayList<AppPopularize> list) 
	{
		for(int i=0; i<list.size(); i++)
		{
			mList.add(list.get(i));
			mCount++;
		}
	}
	
	@Override
	public int getCount()
	{
		return /*mCount*/mList.size();
	}

	@Override
	public Object getItem(int position)
	{
		return mList.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent)
	{

		if (mStoped)
			return convertView;
		ProductItem productItem;
		final AppPopularize info = mList.get(position);
		if (convertView == null)
		{

			View v = LayoutInflater.from(mContext).inflate(R.layout.listview_search_item, null);
			productItem = new ProductItem();
			productItem.mProductIcon = (ImageView) v.findViewById(R.id.item_icon);
			productItem.mProductTime = (TextView) v.findViewById(R.id.item_time);
			productItem.mProductTitle = (TextView) v.findViewById(R.id.item_title);
			productItem.mProductContent = (TextView) v.findViewById(R.id.item_content);
			productItem.mItemBackground = (LinearLayout) v.findViewById(R.id.product_list_item_bg);
			productItem.mArrow = (ImageView) v.findViewById(R.id.item_image_arrow);
			v.setTag(productItem);
			convertView = v;
		} else
		{
			productItem = (ProductItem) convertView.getTag();
		}
//
//		if (position == 0)
//			productItem.mItemBackground.setBackgroundResource(R.drawable.style_item_top);
//		else if (position == mList.size() - 1)
//			productItem.mItemBackground.setBackgroundResource(R.drawable.style_item_bottom);
//		else
//			productItem.mItemBackground.setBackgroundResource(R.drawable.style_item_center);

		if (info.imgDw == null)
			productItem.mProductIcon.setImageResource(R.drawable.icon_coo8_default);
		else
			productItem.mProductIcon.setImageDrawable(info.imgDw);
		
		productItem.mProductTime.setText(info.cTime);
		productItem.mProductTitle.setText(info.appName);
		productItem.mProductContent.setText(info.appDescription);



		productItem.mItemBackground.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent();
				intent.setData(Uri.parse(info.url));
				intent.setAction(Intent.ACTION_VIEW);
				mContext.startActivity(intent); //启动浏览器
			}
		});

		return convertView;
	}

	class ProductItem
	{
		LinearLayout mItemBackground;
		ImageView mProductIcon;
		TextView mProductTime;
		TextView mProductTitle;
		TextView mProductContent;
		ImageView mArrow;
	}

}
