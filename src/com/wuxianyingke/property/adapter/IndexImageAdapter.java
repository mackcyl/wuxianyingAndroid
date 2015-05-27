package com.wuxianyingke.property.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.mantoto.property.R;
import com.wuxianyingke.property.remote.RemoteApi.Note;

public class IndexImageAdapter extends BaseAdapter {
	int mGalleryItemBackground;
	private Context mContext;
	private Gallery.LayoutParams mParams ;
	private List<Note> imgList ;
	public IndexImageAdapter(Context context , Gallery.LayoutParams params
			) {
		mContext = context;
		mParams = params ;
		this.imgList = new ArrayList<Note>() ;
		// 获得Gallery组件的属性
		TypedArray typedArray = mContext.obtainStyledAttributes(R.styleable.Gallery);
		mGalleryItemBackground = typedArray.getResourceId(
				R.styleable.Gallery_android_galleryItemBackground, 0);
	}

	public void addImg(Note d){
		imgList.add(d) ;
		notifyDataSetChanged() ;
	}
	
	public void freeDrawable()
	{
		imgList.clear() ;
		System.gc();
	}
	
	// 返回总数
	public int getCount() {
		return imgList.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	// 返回具体位置的ImageView对象
	public View getView(int position, View convertView, ViewGroup parent) {
		
		LinearLayout ll=new LinearLayout(mContext);
		ll.setOrientation(LinearLayout.VERTICAL);
		TextView bodyView = new TextView(mContext);
		TextView titleView = new TextView(mContext);
		TextView timeView = new TextView(mContext);
		titleView.setGravity(Gravity.LEFT);
		titleView.setSingleLine(true);
		timeView.setGravity(Gravity.LEFT);
		ll.setPadding(10, 10, 10, 10);
		ll.addView(titleView);
		ll.addView(timeView);
		ll.addView(bodyView);
		ll.setLayoutParams(mParams);
		if(imgList==null)
		{
			return ll;	
		}
		if(imgList.get(position) == null){
			//imageView.setBackgroundColor(1) ;
		} else {
			//imageView.setBackgroundColor();
			
			titleView.setTextColor(Color.parseColor("#4c4d51"));
			titleView.setText(imgList.get(position).header) ;
			titleView.setTextSize(30);
			timeView.setPadding(0, 20, 0, 15);
			timeView.setTextSize(15);
			timeView.setText(imgList.get(position).time);
			timeView.setTextColor(Color.parseColor("#a7a7a7"));
			bodyView.setTextColor(Color.parseColor("#666666"));
			bodyView.setTextSize(20);
			if(imgList.get(position).body.length()>200)
			{
				bodyView.setText("       "+imgList.get(position).body.substring(0, 200)+"......（点击查看完整内容）") ;
			}else
			{
				bodyView.setText("       "+imgList.get(position).body) ;
			}
		
			bodyView.setPadding(10, 0, 10, 0);
			
			
		}

		return ll;
	}

}