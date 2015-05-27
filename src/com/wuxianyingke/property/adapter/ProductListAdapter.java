package com.wuxianyingke.property.adapter;

import java.util.List;

import com.mantoto.property.R;
import com.wuxianyingke.property.activities.ProductDetailActivity;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.remote.RemoteApi.Flea;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
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


public class ProductListAdapter extends BaseAdapter
{

	private List<Flea> mList;
	private Context mContext;
	private Handler mHandler;
	private boolean mStoped;
	private boolean mIsOnEdit;
	private int mCount;

	public ProductListAdapter(Context ctx, List<Flea> list, Handler handler)
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
			BitmapDrawable a = (BitmapDrawable) mList.get(i).frontCover.imgDw;
			if (a != null && !a.getBitmap().isRecycled())
				a.getBitmap().recycle();
			mList.get(i).frontCover.imgDw = null;
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

	public void appandAdapter(List<Flea> list) 
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
		final Flea info = mList.get(position);
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

		if (info.frontCover.imgDw == null)
			productItem.mProductIcon.setImageResource(R.drawable.icon_coo8_default);
		else
			productItem.mProductIcon.setImageDrawable(info.frontCover.imgDw);
		
		productItem.mProductTime.setText(info.cTime);
		productItem.mProductTitle.setText(info.header);
		productItem.mProductContent.setText(info.description);



		productItem.mItemBackground.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(mContext, ProductDetailActivity.class);
				intent.putExtra(Constants.PRODUCT_ID_ACTION, info.fleaID);
				mContext.startActivity(intent);
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
