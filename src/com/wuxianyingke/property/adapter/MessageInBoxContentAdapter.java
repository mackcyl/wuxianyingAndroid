package com.wuxianyingke.property.adapter;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mantoto.property.R;
import com.wuxianyingke.property.activities.Radio3Activity;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.remote.RemoteApi.MessageInfo;


public class MessageInBoxContentAdapter extends BaseAdapter {
	private List<MessageInfo> mList;
	private Context mContext;
	private boolean mStoped;
	private int mCount;
	 private LayoutInflater mInflater;
	 private long userid;

	public MessageInBoxContentAdapter(Context ctx, List<MessageInfo> list) 
	{
		this.mContext = ctx;
		this.mList = list;
		this.mStoped = false;
		this.mCount = mList.size();
		  mInflater = LayoutInflater.from(ctx);
		  userid=LocalStore.getUserInfo().userId;
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
		
		
		ActivityItem activityItem;
		final MessageInfo activity = mList.get(position);
		Log.d("MyTag", activity.userid+"/"+LocalStore.getUserInfo().userId);
		
			 if (userid==activity.toUserId)
			  {
				  convertView = mInflater.inflate(R.layout.chatting_item_msg_text_left, null);
			  }else{
				  convertView = mInflater.inflate(R.layout.chatting_item_msg_text_right, null);
			  }
			 activityItem = new ActivityItem();
			activityItem.tvSendTime = (TextView) convertView.findViewById(R.id.tv_sendtime);
			activityItem.tvUserName = (TextView) convertView.findViewById(R.id.tv_username);
			activityItem.tvContent = (TextView) convertView.findViewById(R.id.tv_chatcontent);
			convertView.setTag(activityItem);
		

		activityItem.tvSendTime.setText(activity.cTime);
		activityItem.tvUserName.setText(activity.header);
		activityItem.tvContent.setText(activity.body);
		
		return convertView;
	}

	  static class ActivityItem 
	{
		public TextView tvSendTime;
        public TextView tvUserName;
        public TextView tvContent;
	}
}
