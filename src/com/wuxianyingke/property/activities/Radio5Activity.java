package com.wuxianyingke.property.activities;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mantoto.property.R;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.common.LogUtil;

public class Radio5Activity extends BaseMainRadioActivity {

	private LinearLayout mLinearCommonInformation, mLinearPaidServices,
			mLinearLivingCircle, mLinearPropertyPhone, mLinearChangePassword,
			mLinearQuitLogin,mFleaListLinearLayout,mAppPopularizeLinearLayout;
	private final static String FIRST_CONFIG_MESSAGE = "first_config_message";
	private final static String CHANNEL_ID = "channel_id";
	private ProgressDialog mProgressBar = null;
	private Thread mUpdateThread;
	private TextView topbar_txt;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 11:
				if (mProgressBar != null) {
					mProgressBar.dismiss();
					mProgressBar = null;
				}
				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_radio5);
		setSelfIndex(4);
		initWidgets();
	}

	private void initWidgets() {
		topbar_txt = (TextView) findViewById(R.id.topbar_txt);
		topbar_txt.setText(getResources().getText(R.string.radio_main5));
		mLinearCommonInformation = (LinearLayout) findViewById(R.id.CommonInformationLinearLayout);
		mLinearPaidServices = (LinearLayout) findViewById(R.id.PaidServicesLinearLayout);
		mLinearLivingCircle = (LinearLayout) findViewById(R.id.LivingCircleLinearLayout);
		mLinearPropertyPhone = (LinearLayout) findViewById(R.id.PropertyPhoneLinearLayout);
		mLinearChangePassword = (LinearLayout) findViewById(R.id.ChangePasswordLinearLayout);
		mLinearQuitLogin = (LinearLayout) findViewById(R.id.QuitLoginLinearLayout);
		mFleaListLinearLayout = (LinearLayout) findViewById(R.id.FleaListLinearLayout);
		/*mAppPopularizeLinearLayout = (LinearLayout) findViewById(R.id.AppPopularizeLinearLayout);
		mAppPopularizeLinearLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 推广应用
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setClass(Radio5Activity.this,
                        AppPopularizeActivity.class);
                startActivity(intent);

            }
        });*/
		mLinearCommonInformation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 常用信息
				Intent intent = new Intent();
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
						| Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.setClass(Radio5Activity.this,
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
				intent.setClass(Radio5Activity.this, PaidServicesActivity.class);
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
				intent.setClass(Radio5Activity.this, LivingCircleActivity.class);
				startActivity(intent);

			}
		});

		mLinearPropertyPhone.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 物业电话
				Uri uri = Uri.parse("tel:"
						+ LocalStore.getInvitationCode(Radio5Activity.this).phoneNumber);
				Intent it = new Intent(Intent.ACTION_CALL, uri);
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
				intent.setClass(Radio5Activity.this,
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
				intent.setClass(Radio5Activity.this,
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

	private void checkUpdate() {
		LogUtil.d("MyTag", "checkUpdate");

		mUpdateThread = new Thread() {
			public void run() {
				try {
					// MpApi mp = MpApi.getAPI();
					// SharedPreferences saving =
					// getSharedPreferences(FIRST_CONFIG_MESSAGE, 0);
					/*
					 * Update.checkVersion(Radio5Activity.this,
					 * Radio5Activity.this, mHandler, Constants.PACKAGENAME,
					 * Constants.VERSION_CODE, "1", 1,
					 * mp.getPhoneMd5Imei(Radio5Activity.this),
					 * saving.getString( CHANNEL_ID, ""));
					 */

				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		};
		mUpdateThread.start();
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

				LocalStore.logout(Radio5Activity.this);

				Intent intent = new Intent();
				intent.setClass(Radio5Activity.this, LoginActivity.class);
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

	@Override
	void freeResource() {
		// TODO Auto-generated method stub

	}

	@Override
	void initResource() {
		// TODO Auto-generated method stub

	}
}