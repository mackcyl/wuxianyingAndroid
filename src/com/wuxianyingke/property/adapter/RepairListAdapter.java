package com.wuxianyingke.property.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.mantoto.property.R;
import com.wuxianyingke.property.activities.RepairLogActivity;
import com.wuxianyingke.property.remote.RemoteApi.Repair;

import java.util.List;


public class RepairListAdapter extends BaseAdapter {
	private List<Repair> mList;
	private Context mContext;
	private boolean mStoped;
	private int mCount;

	public RepairListAdapter(Context ctx, List<Repair> list)
	{
		this.mContext = ctx;
		this.mList = list;
		this.mStoped = false;
		this.mCount = mList.size();
	}
	
	public void appandAdapter(List<Repair> list)
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
		final Repair activity = mList.get(position);
		if (convertView == null) 
		{
			View v = LayoutInflater.from(mContext).inflate(R.layout.repair_list_item, null);
			activityItem = new ActivityItem();

			activityItem.isReadImg = (ImageView) v.findViewById(R.id.isRepairReadImg);
			activityItem.mRepairTypeTextView = (TextView) v.findViewById(R.id.RepairTypeTextView);
			activityItem.mRepairContentTextView = (TextView) v.findViewById(R.id.RepairContentTextView);
			activityItem.mRepairStatusTextView = (TextView) v.findViewById(R.id.RepairStatusTextView);
			activityItem.mRepairCtimeTextView = (TextView) v.findViewById(R.id.RepairCtimeTextView);

			activityItem.mMainRadio4ListItemLinearLayout = (LinearLayout) v.findViewById(R.id.MainRadio4ListItemLinearLayout);
			v.setTag(activityItem);
			convertView = v;
		} 
		else 
		{
			activityItem = (ActivityItem)convertView.getTag();
		}


		int statusId = (int)activity.status.repairStatusId;

		switch (statusId){
			case 1:
				activityItem.isReadImg.setVisibility(View.VISIBLE);
				activityItem.mRepairStatusTextView.setText("查看进度");
				break;
			case 2:
				break;
			case 3:
				activityItem.isReadImg.setVisibility(View.INVISIBLE);
				activityItem.mRepairStatusTextView.setTextColor(0xff00B1FB);
				activityItem.mRepairStatusTextView.setText(activity.status.repairStatusName);
				break;
			case 4:
				break;
		}

		activityItem.mRepairTypeTextView.setText(activity.type.repairTypeName+" - ");
		activityItem.mRepairContentTextView.setText(activity.body);

		activityItem.mRepairCtimeTextView.setText(activity.cTime);

		activityItem.mMainRadio4ListItemLinearLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				intent.setClass(mContext, RepairLogActivity.class);
				intent.putExtra("repairLogTitle", activity.type.repairTypeName);
				intent.putExtra("repairLogStatusName", activity.status.repairStatusName);
				intent.putExtra("repairLogStatusId", activity.status.repairStatusId);
				intent.putExtra("repairLogStatusDesc", activity.status.repairStatusDescription);
				intent.putExtra("repairDesc",activity.body);
				intent.putExtra("repairCTime",activity.cTime);

				intent.putExtra("repairId", activity.repairid);

				mContext.startActivity(intent);
			}
		});
		
		return convertView;
	}

	class ActivityItem 
	{
		ImageView isReadImg;
		TextView mRepairContentTextView;
		TextView mRepairStatusTextView;
		TextView mRepairTypeTextView;
		TextView mRepairCtimeTextView;
		LinearLayout mMainRadio4ListItemLinearLayout;
	}
}
