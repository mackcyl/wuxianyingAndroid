package com.wuxianyingke.property.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mantoto.property.R;
import com.wuxianyingke.property.remote.RemoteApi.ExpressService;


public class PropertyCollectionAdapter extends BaseAdapter {
	private List<ExpressService> mList;
	private Context mContext;
	private boolean mStoped;
	private int mCount;
	
	public PropertyCollectionAdapter(Context ctx, List<ExpressService> list) 
	{
		this.mContext = ctx;
		this.mList = list;
		this.mStoped = false;
		this.mCount = mList.size();
	}
	
	public void appandAdapter(List<ExpressService> list) 
	{
		for(int i=0; i<list.size(); i++)
		{
			mList.add(list.get(i));
			mCount++;
		}
	}
	
	@Override
	public int getCount() {
		return mCount;
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
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		if(mStoped)
			return convertView;
		ActivityItem activityItem;
		final ExpressService activity = mList.get(position);
		if (convertView == null) 
		{
			View v = LayoutInflater.from(mContext).inflate(R.layout.main_radio4_list_item, null);
			activityItem = new ActivityItem();
			activityItem.mProductMessageTitleTextView = (TextView) v.findViewById(R.id.PropertyCollectionTitleTextView);
			activityItem.mProductMessageTimeTextView = (TextView) v.findViewById(R.id.PropertyCollectionTimeTextView);
			activityItem.mMainRadio4ListItemLinearLayout = (LinearLayout) v.findViewById(R.id.MainRadio4ListItemLinearLayout);
			activityItem.mPropertyCollectionTextView = (TextView) v.findViewById(R.id.PropertyCollectionButton);
			v.setTag(activityItem);
			convertView = v;
		} 
		else 
		{
			activityItem = (ActivityItem)convertView.getTag();
		}
		int bianhao=0;
		bianhao=position+1;
		if(bianhao<10)
		{
			activityItem.mPropertyCollectionTextView.setText("0"+bianhao+"");	
		}else
		{
			activityItem.mPropertyCollectionTextView.setText(bianhao+"");	
		}
		activityItem.mProductMessageTimeTextView.setText(activity.CTime);
		activityItem.mProductMessageTitleTextView.setText("您在 "+activity.RTime+" 代收已经确认，代收编号为："+activity.expressServiceID);
		return convertView;
	}

	class ActivityItem 
	{
		TextView mProductMessageTitleTextView;
		TextView mProductMessageTimeTextView;
		TextView  mPropertyCollectionTextView;
		LinearLayout mMainRadio4ListItemLinearLayout;
	}
}
