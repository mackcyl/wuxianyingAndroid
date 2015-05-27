package com.wuxianyingke.property.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;

import com.mantoto.property.R;
import com.wuxianyingke.property.PropertyApplication;

public abstract class BaseMainRadioActivity extends Activity {
	public static boolean[] mRecyleFlag = { false, false, false, false, false };
	public int mSelfIndex;
	private boolean mFreeFlag = false;

	abstract void initResource();

	abstract void freeResource();

	public void setSelfIndex(int index) {
		mSelfIndex = index;
	}

	@Override
	protected void onPause() {
		if (mRecyleFlag[mSelfIndex]) {
			// 回收资源
			mRecyleFlag[mSelfIndex] = false;
			freeResource();
			mFreeFlag = true;
		}
		super.onPause();
	}

	/*
	 * @Override protected void onNewIntent(Intent intent) { Boolean needInit =
	 * intent.getBooleanExtra("FromGroup", false); if (needInit) { // 重新获取资源
	 * if(!mFreeFlag) return; initResource(); mFreeFlag = false ; }
	 * super.onNewIntent(intent); }
	 */

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exitApp(BaseMainRadioActivity.this);
			return true;
		} else
			return super.onKeyDown(keyCode, event);
	}

	public void exitApp(final Context context) {
		DialogInterface.OnClickListener okButton = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				PropertyApplication.getInstance().exit();
			}
		};

		DialogInterface.OnClickListener cancelButton = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		};

		final AlertDialog dialog = new AlertDialog.Builder(context)
				.setTitle(R.string.dialog_title)
				.setMessage(R.string.dialog_info_exit)
				.setPositiveButton(R.string.txt_ok, okButton)
				.setNegativeButton(R.string.txt_cancel, cancelButton).create();
		dialog.show();
	}
}