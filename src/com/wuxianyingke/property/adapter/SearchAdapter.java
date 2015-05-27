package com.wuxianyingke.property.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.text.Html;
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
import com.wuxianyingke.property.remote.RemoteApi.ProductTopInfo;

public class SearchAdapter extends BaseAdapter
{
	private List<ProductTopInfo> mList;
	private Context mContext;
	private boolean mStoped;

	public SearchAdapter(Context ctx, List<ProductTopInfo> list)
	{
		this.mContext = ctx;
		this.mList = list;
		this.mStoped = false;
	}

	public void freeDrawable()
	{
		mStoped = true;
		Log.d("MyTag", "App bitmaps free !!! ");
		for(int i=0; i<mList.size(); i++)
		{
			BitmapDrawable a = (BitmapDrawable) mList.get(i).imageDrawable;
			if(a!=null && !a.getBitmap().isRecycled())
				a.getBitmap().recycle();
			mList.get(i).imageDrawable = null;
		}
		System.gc();
	}

	@Override
	public int getCount()
	{
		return mList.size();
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
	public View getView(int position, View convertView, ViewGroup parent)
	{
		if (mStoped)
			return convertView;
		ProductItem productItem;
		final ProductTopInfo info = mList.get(position);
		if (convertView == null)
		{
			View v = LayoutInflater.from(mContext).inflate(R.layout.gridview_search_item, null);

			productItem = new ProductItem();
			productItem.mItemBackground = (LinearLayout) v.findViewById(R.id.search_grid_bg);
			productItem.mProductIcon = (ImageView) v.findViewById(R.id.appIcon);
			productItem.mProductDesc = (TextView) v.findViewById(R.id.appDesc);
			v.setTag(productItem);
			convertView = v;
		} 
		else
		{
			productItem = (ProductItem) convertView.getTag();
		}
		
		if (info.imageDrawable == null)
			productItem.mProductIcon.setImageResource(R.drawable.icon_coo8_default);
		else
			productItem.mProductIcon.setImageDrawable(info.imageDrawable);
		
		productItem.mProductDesc.setText("");
		if(info.productDesc.length() < 40)
			productItem.mProductDesc.append(info.productDesc);
		else
			productItem.mProductDesc.append(info.productDesc.subSequence(0, 39)+"...");
		productItem.mProductDesc.append("\n促销价： ");
		productItem.mProductDesc.append(Html.fromHtml("<font color=\"#ff0000\">" + "¥"+info.productPrice + "</font>"));
		
		productItem.mItemBackground.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				/*Intent intent = new Intent(mContext, ProductDetailActivity.class);
				intent.putExtra(Constants.PRODUCT_ID_ACTION, info.productId);
				mContext.startActivity(intent);*/
			}
		});

		return convertView;
	}
	
	class ProductItem
	{
		LinearLayout mItemBackground;
		ImageView mProductIcon;
		TextView mProductDesc;
	}
}
