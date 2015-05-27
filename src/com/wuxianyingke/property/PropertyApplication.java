package com.wuxianyingke.property;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Application;
import com.mantoto.property.R;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.common.LogUtil;

public class PropertyApplication extends Application
{
	private List<Activity> activityList = new LinkedList<Activity>();
	private static PropertyApplication instance;
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		instance = this;
		LocalStore.initUserInfo(this);
		LocalStore.initCityInfo(this);
		LocalStore.initWeatherInfo(this);
		//bbp.check(this);
	}

	public static PropertyApplication getInstance()
	{
		return instance;
	}

	public void addActivity(Activity activity)
	{
		activityList.add(activity);
		LogUtil.d("HeapLog", "Count="+activityList.size()+" add = "+activity.getClass().getName());
	}
	
	public void finishActivity(String activityName)
	{
		for(int i=0; i<activityList.size(); i++)
		{
			try
			{
				Activity activity = activityList.get(i);
				if(activity.getClass().getName().equals(activityName))
				{
					activity.finish();
					activityList.remove(i);
					return;
				}
				
			} catch (Exception ex)
			{
				ex.printStackTrace();				
			}
		}
	}

	public void removeActivity(String activityName)
	{
		for(int i=0; i<activityList.size(); i++)
		{
			try
			{
				Activity activity = activityList.get(i);
				if(activity.getClass().getName().equals(activityName))
				{
					activityList.remove(i);
					LogUtil.d("HeapLog", "Count="+activityList.size()+" remove = "+activityName);
					return;
				}
				
			} catch (Exception ex)
			{
				ex.printStackTrace();				
			}
		}
	}
	
	public void clearStack()
	{
		LogUtil.d("HeapLog", "clearStack Count="+activityList.size());
		for (Activity activity : activityList)
		{
			try
			{
				activity.finish();
				
			} catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
		activityList.clear();
	}
	
	public void exit()
	{
		LogUtil.d("HeapLog", "exit Count="+activityList.size());
		for (Activity activity : activityList)
		{
			try
			{
				activity.finish();
				
			} catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
		System.exit(0);
	}
}
