package com.wuxianyingke.property.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Gallery;

/*
 * 循环展示图片
 * */
public class MyGallery extends Gallery {

	public MyGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		return super.onFling(e1, e2, 0, velocityY);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if(getAdapter() != null)
			super.onLayout(changed, l, t, r, b);
	}
}
