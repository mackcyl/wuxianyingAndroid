package com.wuxianyingke.property.activities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mantoto.property.R;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.common.MD5;
import com.wuxianyingke.property.common.Util;
import com.wuxianyingke.property.remote.RemoteApi.NetInfo;
import com.wuxianyingke.property.remote.RemoteApi.User;
import com.wuxianyingke.property.remote.RemoteApiImpl;

public class ModifyPasswordActivity extends Activity {
    private EditText mCurrentPassword, mNewPassword, mNewPassword2;
    private ProgressDialog mProgressBar = null;
    private String mToActivity = null;
    public TextView topbar_txt;
    public Button topbar_left;
    private Button mConfirmButton;

    private String mErrorText;

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (mProgressBar != null) {
                mProgressBar.dismiss();
                mProgressBar = null;
            }
            switch (msg.what) {
            // 登录失败
            case 500:
                Toast.makeText(ModifyPasswordActivity.this, mErrorText,
                        Toast.LENGTH_SHORT).show();
                break;
            // 登陆成功
            case 200:
                Toast.makeText(ModifyPasswordActivity.this, "密码修改成功",
                        Toast.LENGTH_SHORT).show();
                mCurrentPassword.setText("");
                mNewPassword.setText("");
                mNewPassword2.setText("");
                break;
            // 通讯错误
            default:
                Toast.makeText(ModifyPasswordActivity.this, "通讯错误，请检查网络或稍后再试。",
                        Toast.LENGTH_SHORT).show();
                break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        } else
            return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modify_password);

        mToActivity = getIntent().getStringExtra("toActivity");
        mCurrentPassword = (EditText) findViewById(R.id.currentPassword);
        mNewPassword = (EditText) findViewById(R.id.newPassword);
        mNewPassword2 = (EditText) findViewById(R.id.newPassword2);

        mConfirmButton = (Button) findViewById(R.id.confirmButton);
        mConfirmButton.setOnClickListener(mConfirmListener);
        topbar_txt = (TextView) findViewById(R.id.topbar_txt);
        topbar_left = (Button) findViewById(R.id.topbar_left);
        topbar_txt.setText("修改密码");
        topbar_left.setVisibility(View.VISIBLE);
        topbar_left.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
            }
        });
    }

    private void goToActivity() {
        if (mToActivity == null) {
            return;
        }

        if (mToActivity.equals("OrderCarActivity")) {
            // Intent intent = new Intent(LoginActivity.this,
            // OrderCarActivity.class);
            // startActivity(intent);
        }
    }

    private final OnClickListener mConfirmListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            modifyPassword();
        }
    };

    private void modifyPassword() {
        if (Util.isEmpty(mCurrentPassword) || Util.isEmpty(mNewPassword)
                || Util.isEmpty(mNewPassword2)) {
            Toast.makeText(ModifyPasswordActivity.this,
                    R.string.error_password_cannot_be_empty, Toast.LENGTH_SHORT)
                    .show();
            return;
        } else if (!mNewPassword.getText().toString()
                .equals(mNewPassword2.getText().toString())) {
            Toast.makeText(ModifyPasswordActivity.this,
                    R.string.error_two_password_not_same, Toast.LENGTH_SHORT)
                    .show();
            return;
        } else {
            String password1 = mNewPassword.getText().toString();
            Pattern pat = Pattern.compile("^[a-z|0-9|A-Z]+$");
            Matcher isOk = pat.matcher(password1);
            if (!isOk.matches() || password1.length() < 6
                    || password1.length() > 16) {
                Toast.makeText(ModifyPasswordActivity.this,
                        R.string.warn_bad_format, Toast.LENGTH_SHORT).show();
                return;
            }
        }

        mProgressBar = ProgressDialog.show(ModifyPasswordActivity.this, null,
                "处理中，请稍后......", true);
        Thread modifyThread = new Thread() {
            public void run() {
                User info = LocalStore.getUserInfo();
                RemoteApiImpl remote = new RemoteApiImpl();
                NetInfo retInfo = remote.modifyPassword(
                        ModifyPasswordActivity.this, info.userId,
                        MD5.toMD5(mCurrentPassword.getText().toString()),
                        MD5.toMD5(mNewPassword.getText().toString()));

                if (retInfo != null)
                    mErrorText = retInfo.desc;

                Message msg = new Message();
                msg.what = retInfo.code;
                mHandler.sendMessage(msg);
            }
        };
        modifyThread.start();
    }

}