package com.wuxianyingke.property.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import com.mantoto.property.R;


public class ImageAdapter extends BaseAdapter {
	int mGalleryItemBackground;
	private Context mContext;
	private Gallery.LayoutParams mParams ;
	private ImageView.ScaleType mScaleType ;
	private List<Drawable> imgList ;

	public ImageAdapter(Context context , Gallery.LayoutParams params, 
			ImageView.ScaleType scaleType) {
		mContext = context;
		mParams = params ;
		mScaleType = scaleType ;
		this.imgList = new ArrayList<Drawable>() ;
		// 获得Gallery组件的属性
		TypedArray typedArray = mContext.obtainStyledAttributes(R.styleable.Gallery);
		mGalleryItemBackground = typedArray.getResourceId(
				R.styleable.Gallery_android_galleryItemBackground, 0);
	}

	public void addImg(Drawable d){
		imgList.add(d) ;
		notifyDataSetChanged() ;
	}
	
	public void freeDrawable()
	{
		Log.d("MyTag", "App bitmaps free !!! ");
		for(int i=0; i<imgList.size(); i++)
		{			
			BitmapDrawable a = (BitmapDrawable) imgList.get(i);
			if(a!=null && !a.getBitmap().isRecycled())
				a.getBitmap().recycle();
//			imgList.get(i) = null ;
		}
		imgList.clear() ;
		System.gc();
	}
	
	// 返回图像总数
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
		ImageView imageView = new ImageView(mContext);
		imageView.setLayoutParams(new Gallery.LayoutParams(
			     LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		if(imgList.get(position) == null){
//			imageView.setImageResource(R.drawable.jk) ;
			imageView.setBackgroundColor(0) ;
		} else {
			imageView.setImageDrawable(imgList.get(position)) ;
		}
		imageView.setScaleType(mScaleType);

		return imageView;
	}
}