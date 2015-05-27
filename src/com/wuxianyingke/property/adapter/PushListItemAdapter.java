package com.wuxianyingke.property.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mantoto.property.R;
import com.wuxianyingke.property.remote.RemoteApi.PushMessage;

public class PushListItemAdapter extends BaseAdapter{

	private Handler handler ;
	private Context ctx ;
	private ArrayList<PushMessage> info ;
	
	public PushListItemAdapter(Handler handler , Context ctx , ArrayList<PushMessage> info){
		this.handler = handler ;
		this.ctx = ctx ;
		this.info = info ;
	}
	
	@Override
	public int getCount() {
		return info.size();
	}

	@Override
	public Object getItem(int position) {
		return info.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position ;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final int p = position ;
		ViewHolder pItem = null ;
		if (convertView == null) {
			View v = LayoutInflater.from(ctx).inflate(
					R.layout.push_list_item, null);
			pItem = new ViewHolder();
			pItem.headerTv = (TextView) v.findViewById(R.id.header_tv);
			pItem.bodyTv = (TextView) v.findViewById(R.id.body_tv);
			pItem.mainRl = (RelativeLayout) v.findViewById(R.id.main_rl) ;
			v.setTag(pItem);
			convertView = v;
		} else {
			pItem = (ViewHolder) convertView.getTag();
		}
		
		if(!info.get(position).readed){
			pItem.headerTv.setText(info.get(position).header);
			pItem.bodyTv.setText(info.get(position).msg);
		} else {
			pItem.headerTv.setText(Html.fromHtml("<b><font color='#0000ff'>" + info.get(position).header + "</font></b>")) ;
			pItem.bodyTv.setText(Html.fromHtml("<b><font color='#0000ff'>" + info.get(position).msg + "</font></b>"));
		}
		
		if (position == 0) {
			pItem.mainRl.setBackgroundResource(R.drawable.style_item_top);
		} else if (position == info.size() - 1) {
			pItem.mainRl
					.setBackgroundResource(R.drawable.style_item_bottom);
		} else {
			pItem.mainRl
					.setBackgroundResource(R.drawable.style_item_center);
		}

		pItem.mainRl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Message msg = new Message() ;
				msg.what = 1000 ;
				msg.arg1 = p ;
				info.get(p).readed = true ;
				handler.sendMessage(msg) ;
				notifyDataSetChanged() ;
			}
		});
		return convertView;
	}

	private class ViewHolder {
		public TextView bodyTv ;
		public TextView headerTv ;
		public RelativeLayout mainRl ;
	}
	
}
