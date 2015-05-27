package com.wuxianyingke.property.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mantoto.property.R;
import com.wuxianyingke.property.activities.MessageInfoActivity;
import com.wuxianyingke.property.activities.PropertyNotificationInfoActivity;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.remote.RemoteApi.MessageInfo;
import com.wuxianyingke.property.remote.RemoteApi.PropertyNotificationInfo;


public class PropertyMessageAdapter extends BaseAdapter {
	private List<MessageInfo> mList;
	private Context mContext;
	private boolean mStoped;
	private int mCount;
	
	public PropertyMessageAdapter(Context ctx, List<MessageInfo> list) 
	{
		this.mContext = ctx;
		this.mList = list;
		this.mStoped = false;
		this.mCount = mList.size();
	}
	
	public void appandAdapter(List<MessageInfo> list) 
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
		final MessageInfo activity = mList.get(position);
		if (convertView == null) 
		{
			View v = LayoutInflater.from(mContext).inflate(R.layout.message_list_item, null);
			activityItem = new ActivityItem();
			activityItem.isReadImg = (ImageView) v.findViewById(R.id.isReadImg);
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
		if(activity.isRead == 1)
		{
			activityItem.isReadImg.setVisibility(View.INVISIBLE);
		}
		else
		{
			if(activity.type.messageTypeID == 3 && LocalStore.getQunfaIsRead(mContext,activity.messageID)==1)			
			{
				activityItem.isReadImg.setVisibility(View.INVISIBLE);
			}
			else
				activityItem.isReadImg.setVisibility(View.VISIBLE);
		}
		activityItem.mProductMessageTitleTextView.setText(activity.header);
		activityItem.mProductMessageTimeTextView.setText(activity.cTime);
		activityItem.mMainRadio4ListItemLinearLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				intent.setClass(mContext, MessageInfoActivity.class);
				intent.putExtra("productMessageInfoTitle", activity.header);
				intent.putExtra("productMessageInfoTime", activity.cTime);
				intent.putExtra("productMessageInfoContent", activity.body);
				intent.putExtra("productMessageInfoType", activity.type.messageTypeID);
				intent.putExtra("productMessageInfoSignature", activity.isRead);
				intent.putExtra("productMessageInfoRootID", activity.messageID);
				
				mContext.startActivity(intent);
			}
		});
		
		return convertView;
	}

	class ActivityItem 
	{
		ImageView isReadImg;
		TextView mProductMessageTitleTextView;
		TextView mProductMessageTimeTextView;
		LinearLayout mMainRadio4ListItemLinearLayout;
	}
}
