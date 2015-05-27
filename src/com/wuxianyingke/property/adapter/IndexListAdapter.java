package com.wuxianyingke.property.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mantoto.property.R;
import com.wuxianyingke.property.activities.PropertyNotificationInfoActivity;
import com.wuxianyingke.property.remote.RemoteApi.ProductMessage;

public class IndexListAdapter extends BaseAdapter {

	private ArrayList<ProductMessage> mDataList = null;
	private Context mContext = null;
	private Handler mHandler = null;

	public IndexListAdapter(List<ProductMessage> dataList, Context context,
			Handler handler) {
		mDataList = (ArrayList<ProductMessage>) dataList;
		mContext = context;
		mHandler = handler;
	}

	@Override
	public int getCount() {
		return mDataList.size();
	}

	@Override
	public Object getItem(int position) {
		return mDataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextListItem pItem = null;
		final ProductMessage activity = mDataList.get(position);
		if (convertView == null) {
			View v = LayoutInflater.from(mContext).inflate(
					R.layout.index_list_item, null);
			pItem = new TextListItem();
			pItem.mTitleTv = (TextView) v.findViewById(R.id.title_tv);
			pItem.mDesTv = (TextView) v.findViewById(R.id.des_tv);
			pItem.mBackGround = (LinearLayout) v.findViewById(R.id.index_text_item_bg) ;
			v.setTag(pItem);
			convertView = v;
		} else {
			pItem = (TextListItem) convertView.getTag();
		}

		pItem.mTitleTv.setText(activity.header);
		pItem.mDesTv.setText(activity.body);

		if (position == 0) {
			pItem.mBackGround.setBackgroundResource(R.drawable.style_item_top);
		} else if (position == mDataList.size() - 1) {
			pItem.mBackGround
					.setBackgroundResource(R.drawable.style_item_bottom);
		} else {
			pItem.mBackGround
					.setBackgroundResource(R.drawable.style_item_center);
		}

		pItem.mBackGround.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent=new Intent();
				intent.setClass(mContext, PropertyNotificationInfoActivity.class);
				intent.putExtra("productMessageInfoTitle", activity.header);
				intent.putExtra("productMessageInfoTime", activity.time);
				intent.putExtra("productMessageInfoContent", activity.body);
				mContext.startActivity(intent);
			}
		});
		return convertView;
	}

	private class TextListItem {
		public TextView mTitleTv;
		public TextView mDesTv;
		public LinearLayout mBackGround ;
	}

}
