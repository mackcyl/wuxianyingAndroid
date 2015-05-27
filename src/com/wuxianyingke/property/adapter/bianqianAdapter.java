package com.wuxianyingke.property.adapter;

import java.util.ArrayList;
import java.util.List;

import com.mantoto.property.R;
import com.wuxianyingke.property.activities.AddBianqianActivity;
import com.wuxianyingke.property.activities.NormalDialog;
import com.wuxianyingke.property.activities.ProductDetailActivity;
import com.wuxianyingke.property.activities.Radio3Activity;
import com.wuxianyingke.property.activities.StickerActivity;
import com.wuxianyingke.property.activities.NormalDialog.NormalDialogListener;
import com.wuxianyingke.property.adapter.MessageOutBoxAdapter.ActivityItem;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.common.LocalStore.bianqian;
import com.wuxianyingke.property.remote.RemoteApi.AppPopularize;
import com.wuxianyingke.property.remote.RemoteApi.Flea;
import com.wuxianyingke.property.remote.RemoteApi.MessageInfo;

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
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class bianqianAdapter extends BaseAdapter
{

	private ArrayList<bianqian> mList;
	private Context mContext;
	private Handler mHandler;
	private boolean mStoped;
	private boolean mIsOnEdit;
	private int mCount;

	public bianqianAdapter(Context ctx, ArrayList<bianqian> list, Handler handler)
	{
		this.mContext = ctx;
		this.mList = list;
		this.mHandler = handler;
		this.mStoped = false;
		this.mCount = mList.size();
	}

	public void setIsOnEdit(boolean isOnEdit)
	{
		mIsOnEdit = isOnEdit;
	}

	public final boolean getIsOnEdit()
	{
		return mIsOnEdit;
	}

	public void appandAdapter(ArrayList<bianqian> list) 
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
		ActivityItem activityItem;
		final bianqian activity = mList.get(position);
		if (convertView == null) 
		{
			View v = LayoutInflater.from(mContext).inflate(R.layout.bianqian_list_item, null);
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
		activityItem.mProductMessageTitleTextView.setText(activity.content);
		activityItem.mProductMessageTimeTextView.setText(activity.cTime);
		activityItem.mMainRadio4ListItemLinearLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
						| Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.setClass(mContext, AddBianqianActivity.class);
				intent.putExtra("id", activity.id);
				intent.putExtra("content", activity.content);
				((StickerActivity)mContext).startActivityForResult(intent, 0);
			}
		});

		activityItem.mMainRadio4ListItemLinearLayout.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				NormalDialog tmpdialog = new NormalDialog(mContext, 
						new NormalDialogListener() {
							
							@Override
							public void onClick(View view) {
								// TODO Auto-generated method stub   
								switch(view.getId()){     
								case R.id.dialog_button_1:  
									break;     
								case R.id.dialog_button_2:  
									new LocalStore().deleteBianqianByid(mContext, activity.id);
									Message msg = new Message();
									msg.what = 0;
									mHandler.sendMessage(msg);
			                        break; 
								case R.id.dialog_button_3:  
			                        break; 
		                   
								}
							}
						},

						"删除",
						"是否删除这条便签",
						mContext.getString(R.string.txt_cancel),
						"删除");
				tmpdialog.show();
				return true;
			}
		});
		
		
		return convertView;
	}

	class ActivityItem 
	{
		TextView mProductMessageTitleTextView;
		TextView mProductMessageTimeTextView;
		LinearLayout mMainRadio4ListItemLinearLayout;
	}

}
