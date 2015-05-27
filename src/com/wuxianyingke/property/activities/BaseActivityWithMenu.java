package com.wuxianyingke.property.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.mantoto.property.R;
import com.wuxianyingke.property.PropertyApplication;

public abstract class BaseActivityWithMenu extends Activity
{
	public static final int ID_MENU1 = Menu.FIRST;
	public static final int ID_MENU2 = Menu.FIRST + 1;
	public static final int ID_MENU3 = Menu.FIRST + 2;
	public static final int ID_MENU4 = Menu.FIRST + 3;
	
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
	
	public void exitApp(final Context context)
	{
		DialogInterface.OnClickListener okButton = new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				
				PropertyApplication.getInstance().exit();				
			}
		};

		DialogInterface.OnClickListener cancelButton = new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
			}
		};
		
		final AlertDialog dialog = new AlertDialog.Builder(context)
									.setTitle(R.string.dialog_title)
									.setMessage(R.string.dialog_info_exit)
									.setPositiveButton(R.string.txt_ok, okButton)
									.setNegativeButton(R.string.txt_cancel, cancelButton)
									.create();
		dialog.show();
	}
	
/*	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// 菜单
		menu.add(0, ID_MENU1, 1, R.string.menu_index1_txt).setIcon(R.drawable.menu_index1_pic);
		menu.add(0, ID_MENU2, 2, R.string.menu_index2_txt).setIcon(R.drawable.menu_index2_pic);
		menu.add(0, ID_MENU3, 3, R.string.menu_index3_txt).setIcon(R.drawable.menu_index3_pic);
		menu.add(0, ID_MENU4, 4, R.string.menu_index4_txt).setIcon(R.drawable.menu_index4_pic);
		return super.onCreateOptionsMenu(menu);
	}
*/
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case ID_MENU1:

				return true;
				
			case ID_MENU2:

				return true;

			case ID_MENU3:

				return true;
				
			case ID_MENU4:
				exitApp(BaseActivityWithMenu.this);
				return true;

			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}
}
