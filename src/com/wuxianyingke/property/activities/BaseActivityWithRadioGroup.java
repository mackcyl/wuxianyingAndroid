package com.wuxianyingke.property.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.wuxianyingke.property.PropertyApplication;
import com.wuxianyingke.property.common.Constants;

public abstract class BaseActivityWithRadioGroup extends Activity
{
	private RadioGroup mRadioGroup;
	private RadioButton[] mRadioButton = new RadioButton[5];
	private int checkedRadioId;
	
	abstract void freeResource();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		PropertyApplication.getInstance().addActivity(this);
	}
	
	@Override
	protected void onDestroy()
	{
		freeResource();
		PropertyApplication.getInstance().removeActivity(getClass().getName());
		super.onDestroy();
	}
	
	public void initRadioGroup(int groupResid, int firstResId, int checkedRadioIndex)
	{
		mRadioGroup = (RadioGroup) findViewById(groupResid);
		mRadioGroup.setOnCheckedChangeListener(mBotRadioGroupListener);
		if(checkedRadioIndex == 0)
		{
			for(int index=0; index<5; index++)
				mRadioButton[index] = (RadioButton) findViewById(firstResId + index);
			checkedRadioId = -1;
			return;
		}
		for(int index=0; index<5; index++)
		{
			mRadioButton[index] = (RadioButton) findViewById(firstResId + index);
			if((index+1) == checkedRadioIndex)
				checkedRadioId = firstResId + index;
		}
		mRadioButton[checkedRadioIndex - 1].setChecked(true);
	}
	
	private RadioGroup.OnCheckedChangeListener mBotRadioGroupListener = new RadioGroup.OnCheckedChangeListener()
	{
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId)
		{
			Intent intent = new Intent(BaseActivityWithRadioGroup.this, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			for(int index=0; index<5; index++)
			{
				if(checkedId == checkedRadioId)
					return;
				if(checkedId == mRadioButton[index].getId())
				{
					PropertyApplication.getInstance().clearStack();
					intent.putExtra(Constants.MAINACTIVITYINDEXACTION, index);
					break;
				}
			}
			startActivity(intent);
			finish();
		}
	};
}