package com.wuxianyingke.property.activities;

import android.app.Activity;
import android.app.ActivityGroup;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.wuxianyingke.property.PropertyApplication;
import com.mantoto.property.R;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.common.Util;

public abstract class BaseMainActivity extends ActivityGroup
{
	// 声明PopupWindow对象的引用
		private PopupWindow popupWindow;
//	public static final int ID_MENU1 = Menu.FIRST;
	public static final int ID_MENU2 = Menu.FIRST + 1;
	public static final int ID_MENU3 = Menu.FIRST + 2;
	public static final int ID_MENU4 = Menu.FIRST + 3;
	public static final int ID_MENU5 = Menu.FIRST + 4;
	public static final int ID_MENU6 = Menu.FIRST + 5;
	private LinearLayout mContainerLinear;
	private RadioGroup mRadioGroup;
	private RadioButton mRadio1Button, mRadio2Button, mRadio3Button, mRadio4Button,mRadio5Button;
	abstract void menuIndex1Event();
	abstract void menuIndex2Event();
	abstract void menuIndex3Event();
	abstract void menuIndex4Event();
	static ImageView cartImageview;
	public enum ActivityIndex
	{
		Radio1Activity, Radio2Activity, Radio3Activity, Radio4Activity, Radio5Activity,
	}
	private ActivityIndex mCurrentActivity;
	private LinearLayout mMainLinearLayout,mLinearCommonInformation, mLinearPaidServices,
	mLinearLivingCircle, mLinearPropertyPhone, mLinearChangePassword,
	mLinearQuitLogin,mFleaListLinearLayout;
	private Handler mMainHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{

			}
			super.handleMessage(msg);
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		cartImageview=(ImageView)findViewById(R.id.cart_imageview);
		Util.modifyCarNumber(this,cartImageview);
		confirmUrlOk();

		Thread updateThread = new Thread()
		{
			public void run()
			{
				try
				{
					

				} catch (Exception ex)
				{
					ex.printStackTrace();
				}
			}
		};
		updateThread.start();

		mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);
		mRadioGroup.setOnCheckedChangeListener(mRadioGroupListener);
		mRadio1Button = (RadioButton) findViewById(R.id.toggle_radio1);
		mRadio2Button = (RadioButton) findViewById(R.id.toggle_radio2);
		mRadio3Button = (RadioButton) findViewById(R.id.toggle_radio3);
		mRadio4Button = (RadioButton) findViewById(R.id.toggle_radio4);
		mRadio5Button = (RadioButton) findViewById(R.id.toggle_radio5);
		mContainerLinear = (LinearLayout) findViewById(R.id.mainLinear);
		int actitvityIndex = getIntent().getIntExtra(Constants.MAINACTIVITYINDEXACTION, -1);
		startChildActivty(actitvityIndex);
	}
	
	public static void onChangeCarNumber(Activity activity)
	{
		Util.modifyCarNumber(activity,cartImageview);
	}
	
	private void confirmUrlOk()
	{
		// 再次确认url设置完成
		SharedPreferences saving = getSharedPreferences(Constants.URL_SETTING, 0);
		/*if (Constants.BANGCLE_URL.equals(""))
		{
			String currentUrl = saving.getString(Constants.BANGCLE_URL_SETTING, "");
			if (currentUrl.equals(""))
				Constants.BANGCLE_URL = Constants.ORIGINAL_BANGCLE_URL;
			else
				Constants.BANGCLE_URL = currentUrl;
		}
		if (Constants.PRODUCT_URL.equals(""))
		{
			String currentProductUrl = saving.getString(Constants.PRODUCT_URL_SETTING, "");
			if (currentProductUrl.equals(""))
				Constants.PRODUCT_URL = Constants.ORIGINAL_PRODUCT_URL;
			else
				Constants.PRODUCT_URL = currentProductUrl;
		}*/
	}

	@Override
	protected void onNewIntent(Intent intent)
	{
		super.onNewIntent(intent);
		int actitvityIndex = intent.getIntExtra(Constants.MAINACTIVITYINDEXACTION, -1);
		startChildActivty(actitvityIndex);
	}

	private void startChildActivty(int actitvityIndex)
	{
		if (actitvityIndex != -1)
		{
			switch (actitvityIndex)
			{
				case 0:
					mRadio1Button.performClick();
					break;
				case 1:
					mRadio2Button.performClick();
					break;
				case 2:
					mRadio3Button.performClick();
					break;
				case 3:
					mRadio4Button.performClick();
					break;
				case 4:
					mRadio5Button.performClick();
					break;
			}
		} else
			mRadio1Button.performClick();
	}

	private RadioGroup.OnCheckedChangeListener mRadioGroupListener = new RadioGroup.OnCheckedChangeListener()
	{
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId)
		{
			
			setRecyleFlag();
			Util.logHeap(BaseMainActivity.this.getClass());
			if (checkedId == mRadio1Button.getId())
			{
				Constants.RADIO_CHECKED=1;
				mCurrentActivity = ActivityIndex.Radio1Activity;
				mContainerLinear.removeAllViews();
				mContainerLinear.addView(BaseMainActivity.this.getLocalActivityManager().startActivity("Radio1",
						new Intent(BaseMainActivity.this, Radio1Activity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP).putExtra("FromGroup", true))
						.getDecorView(), LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

			} 
			else if (checkedId == mRadio2Button.getId())
			{
				Constants.RADIO_CHECKED=2;
				mCurrentActivity = ActivityIndex.Radio2Activity;
				mContainerLinear.removeAllViews();
				mContainerLinear.addView(BaseMainActivity.this.getLocalActivityManager().startActivity("Radio2",
						new Intent(BaseMainActivity.this, Radio2Activity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP).putExtra("FromGroup", true))
						.getDecorView(), LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
			} 
			else if (checkedId == mRadio3Button.getId())
			{
				Constants.RADIO_CHECKED=3;
				mCurrentActivity = ActivityIndex.Radio3Activity;
				mContainerLinear.removeAllViews();
				mContainerLinear.addView(BaseMainActivity.this.getLocalActivityManager().startActivity("Radio3",
						new Intent(BaseMainActivity.this, Radio3Activity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP).putExtra("FromGroup", true))
						.getDecorView(), LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
			} 
			else if (checkedId == mRadio4Button.getId())
			{
				Constants.RADIO_CHECKED=4;
				mCurrentActivity = ActivityIndex.Radio4Activity;
				mContainerLinear.removeAllViews();
				mContainerLinear.addView(BaseMainActivity.this.getLocalActivityManager().startActivity("Radio4",
						new Intent(BaseMainActivity.this, Radio4Activity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP).putExtra("FromGroup", true))
						.getDecorView(), LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
			} 
			else if (checkedId == mRadio5Button.getId())
			{
				
				//getPopupWindow();
				//selectTypeDialog(mRadio5Button);
				
				Constants.RADIO_CHECKED=5;
				mCurrentActivity = ActivityIndex.Radio5Activity;
				mContainerLinear.removeAllViews();
				mContainerLinear.addView(BaseMainActivity.this.getLocalActivityManager().startActivity("Radio5",
						new Intent(BaseMainActivity.this, Radio5Activity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP).putExtra("FromGroup", true))
						.getDecorView(), LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
			} 
		}
	};

	private void setRecyleFlag()
	{
		//else if (mCurrentActivity == ActivityIndex.Radio5Activity)
			//Radio5Activity.mRecyleFlag[4] = true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// 菜单
//		menu.add(0, ID_MENU1, 1, R.string.menu_index1_txt).setIcon(R.drawable.menu_index1_pic);
		menu.add(0, ID_MENU2, 2, R.string.menu_index2_txt).setIcon(R.drawable.menu_index2_pic);
		//menu.add(0, ID_MENU3, 3, R.string.menu_index3_txt).setIcon(R.drawable.menu_index3_pic);
		//menu.add(0, ID_MENU4, 4, R.string.menu_index4_txt).setIcon(R.drawable.menu_index4_pic);
		menu.add(0, ID_MENU5, 6, R.string.menu_index5_txt).setIcon(R.drawable.menu_index5_pic);
		//menu.add(0, ID_MENU6, 5, R.string.menu_index6_txt).setIcon(R.drawable.menu_index6_pic);
		
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		Intent intent = new Intent();
		switch (item.getItemId())
		{
			/*case ID_MENU1:
				// 关于我们
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.setClass(BaseMainActivity.this, AboutActivity.class);
				intent.putExtra("radio_checked", Constants.RADIO_CHECKED);
				startActivity(intent);
				return true;*/

			case ID_MENU2:
				//设置
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.setClass(BaseMainActivity.this, SettingActivity.class);
				startActivity(intent);
				return true;

			case ID_MENU3:
				return true;

			case ID_MENU4:
				// 帮助
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.setClass(BaseMainActivity.this, HelpActivity.class);
				intent.putExtra("radio_checked", Constants.RADIO_CHECKED);
				startActivity(intent);
				return true;

			case ID_MENU5:
				exitApp(BaseMainActivity.this);
				return true;
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			exitApp(BaseMainActivity.this);
			return true;
		} else
			return super.onKeyDown(keyCode, event);
	}

	private void selectTypeDialog(final RadioButton textView) {
		View view = LayoutInflater.from(this).inflate(R.layout.more, null);
		initWidgets(view);
		popupWindow = new PopupWindow(view, 300,
				LayoutParams.WRAP_CONTENT, true);
		popupWindow.setAnimationStyle(R.style.AnimationPreview);
		// 加上下面两行可以用back键关闭popupwindow，否则必须调用dismiss();
		ColorDrawable dw = new ColorDrawable(-00000);
		//（以某个View为参考）,表示弹出窗口以parent组件为参考，位于左侧，偏移-90。
		popupWindow.setBackgroundDrawable(dw);
		popupWindow.showAtLocation(textView, Gravity.BOTTOM|Gravity.RIGHT, 0, 50);
		popupWindow.update();
		/*Button open = (Button) view.findViewById(R.id.open);
		open.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				popupWindow.dismiss();
			}
		});*/
		/*view.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				popupWindow.dismiss();
				return false;
			}
		});*/
		
		//popupWindow.showAsDropDown(textView, 0, -650);
	}
	OnClickListener CloseClickListener = new OnClickListener() {
		public void onClick(View v) {
			if (popupWindow != null && popupWindow.isShowing())
			{
				Radio5Activity.mRecyleFlag[4] = false;
				popupWindow.dismiss();
			}

		}
	};
	private void initWidgets(View v) {
	mMainLinearLayout = (LinearLayout) findViewById(R.id.MainLinearLayout);
		mLinearCommonInformation = (LinearLayout) v.findViewById(R.id.CommonInformationLinearLayout);
		mLinearPaidServices = (LinearLayout) v.findViewById(R.id.PaidServicesLinearLayout);
		mLinearLivingCircle = (LinearLayout) v.findViewById(R.id.LivingCircleLinearLayout);
		mLinearPropertyPhone = (LinearLayout) v.findViewById(R.id.PropertyPhoneLinearLayout);
		mLinearChangePassword = (LinearLayout) v.findViewById(R.id.ChangePasswordLinearLayout);
		mLinearQuitLogin = (LinearLayout) v.findViewById(R.id.QuitLoginLinearLayout);
		mFleaListLinearLayout = (LinearLayout) v.findViewById(R.id.FleaListLinearLayout);
		mMainLinearLayout.setOnClickListener(CloseClickListener);
		mLinearCommonInformation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 常用信息
				Intent intent = new Intent();
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
						| Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.setClass(BaseMainActivity.this,
						CommonInfomationActivity.class);
				startActivity(intent);

			}
		});

		mLinearPaidServices.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 有偿服务
				Intent intent = new Intent();
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
						| Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
						| Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.setClass(BaseMainActivity.this, PaidServicesActivity.class);
				startActivity(intent);
			}
		});

		mLinearLivingCircle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 生活圈
				Intent intent = new Intent();
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
						| Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
						| Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.setClass(BaseMainActivity.this, LivingCircleActivity.class);
				startActivity(intent);

			}
		});

		mLinearPropertyPhone.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 物业电话
				Uri uri = Uri.parse("tel:"
						+ LocalStore.getInvitationCode(BaseMainActivity.this).phoneNumber);
				Intent it = new Intent(Intent.ACTION_DIAL, uri);
				startActivity(it);

			}
		});
		mFleaListLinearLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 跳蚤市场
				Intent intent = new Intent();
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
						| Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.setClass(BaseMainActivity.this,
						ProductListActivity.class);
				startActivity(intent);
			}
		});
		mLinearChangePassword.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 修改密码
				Intent intent = new Intent();
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
						| Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.setClass(BaseMainActivity.this,
						ModifyPasswordActivity.class);
				startActivity(intent);
			}
		});

		mLinearQuitLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 退出
				confirmLogouDialog();
			}
		});

	}
	protected void confirmLogouDialog() {
		AlertDialog.Builder builder = new Builder(this);

		String strTitle = getResources().getString(R.string.txt_tips);
		String strOk = getResources().getString(R.string.txt_ok);
		String strCancel = getResources().getString(R.string.txt_cancel);

		builder.setTitle(strTitle);
		builder.setMessage("确认退出登录吗？");

		builder.setPositiveButton(strOk, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();

				LocalStore.logout(BaseMainActivity.this);

				Intent intent = new Intent();
				intent.setClass(BaseMainActivity.this, LoginActivity.class);
				startActivity(intent);

				finish();
			}
		});

		builder.setNegativeButton(strCancel,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		builder.create().show();
	}

	

	/*** * 获取PopupWindow实例 */
	private void getPopupWindow() {
		if (null != popupWindow) {
			popupWindow.dismiss();
			return;
		} else {
			//initPopuptWindow();
		}
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

		final AlertDialog dialog = new AlertDialog.Builder(context).setTitle(R.string.dialog_title).setMessage(R.string.dialog_info_exit).setPositiveButton(R.string.txt_ok, okButton)
				.setNegativeButton(R.string.txt_cancel, cancelButton).create();
		dialog.show();
	}
}