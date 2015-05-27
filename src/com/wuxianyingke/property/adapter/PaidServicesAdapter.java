package com.wuxianyingke.property.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mantoto.property.R;
import com.wuxianyingke.property.remote.RemoteApi.PaidServicesInfo;


public class PaidServicesAdapter extends BaseAdapter {
	private List<PaidServicesInfo> mList;
	private Context mContext;
	private boolean mStoped;
	private int mCount;
	
	public PaidServicesAdapter(Context ctx, List<PaidServicesInfo> list) 
	{
		this.mContext = ctx;
		this.mList = list;
		this.mStoped = false;
		this.mCount = mList.size();
	}
	
	public void appandAdapter(List<PaidServicesInfo> list) 
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
		final PaidServicesInfo activity = mList.get(position);
		if (convertView == null) 
		{
			View v = LayoutInflater.from(mContext).inflate(R.layout.paid_services_list_item, null);
			activityItem = new ActivityItem();
			activityItem.mProductMessageTitleTextView = (TextView) v.findViewById(R.id.ProductMessageTitleTextView);
			activityItem.mProductMessageTimeTextView = (TextView) v.findViewById(R.id.ProductMessageTimeTextView);
			activityItem.mMainRadio4ListItemLinearLayout = (LinearLayout) v.findViewById(R.id.MainRadio4ListItemLinearLayout);
			v.setTag(activityItem);
			convertView = v;
		} 
		else 
		{
			activityItem = (ActivityItem)convertView.getTag();
		}
		activityItem.mProductMessageTitleTextView.setText(activity.serviceName);
		activityItem.mProductMessageTimeTextView.setText(activity.price);
		
		
		return convertView;
	}

	class ActivityItem 
	{
		TextView mProductMessageTitleTextView;
		TextView mProductMessageTimeTextView;
		LinearLayout mMainRadio4ListItemLinearLayout;
	}
}
