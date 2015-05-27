package com.wuxianyingke.property.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mantoto.property.R;
import com.wuxianyingke.property.adapter.MessageInBoxAdapter;
import com.wuxianyingke.property.adapter.MessageInBoxContentAdapter;
import com.wuxianyingke.property.adapter.MessageOutBoxAdapter;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.common.LogUtil;
import com.wuxianyingke.property.common.Util;
import com.wuxianyingke.property.remote.RemoteApi.MessageInfo;
import com.wuxianyingke.property.remote.RemoteApi.MessageTypeInfo;
import com.wuxianyingke.property.remote.RemoteApi.NetInfo;
import com.wuxianyingke.property.remote.RemoteApiImpl;
import com.wuxianyingke.property.threads.MessageInBoxContentThread;
import com.wuxianyingke.property.threads.MessageInBoxThread;
import com.wuxianyingke.property.threads.MessageOutBoxThread;
import com.wuxianyingke.property.threads.MessageTypeThread;

public class Radio3Activity extends Activity {

	private ProgressDialog mProgressDialog = null;
	private MessageInBoxAdapter mMessageInBoxAdapter = null;
	private MessageInBoxContentAdapter mMessageInBoxContentAdapter = null;
	private MessageOutBoxAdapter mMessageOutBoxAdapter = null;
	private ArrayAdapter<String> mEmailMessageSpinnerAdapter;
	private List<MessageTypeInfo> mMessageTypeInfoList = new ArrayList<MessageTypeInfo>();
	private MessageTypeThread mMessageTypeThread = null;
	private static MessageInBoxThread mMessageInBoxThread = null;
	private static MessageInBoxContentThread mMessageInBoxContentThread = null;
	private MessageOutBoxThread mMessageOutBoxThread = null;
	private static int messageTypeId = -1;
	private static int propertyid;
	private static ProgressDialog mWaitLoading = null;
	private String mToActivity = null;
	private static String mErrorInfo = "";
	private static String desc = "";
	private String getCode = "";
	private static LinearLayout mEmailMessageAddLinearLayout,
			mEmailMessageShowLinearLayout;
	private static Spinner mEmailMessageSpinner;
	private static EditText mEmailMessageTitleEditText;

	private static EditText mEmailMessageBodyEditText;
	private static ListView mEmailMessageOutboxListView;

	private static ListView mEmailMessageInboxListView,mEmailMessageInboxContentListView;
	private static TextView mMessageShowTitleTextView,
			mMessageShowTimeTextView, mIsVisitorTextView;
	private static TextView mMessageShowBodyTextView;
	private static Button mSendMessageShowButton;
	private static Button mSendMessageButton,mSendInBoxMessageButton;
	private static LinearLayout mEmailMessageInboxContentLinearLayout;
	private int messageStatus = 0;
	private long userid = 0;
	private static Context mContext;

	private static int returnFlag = 0;// 标识软返回按键状态，1是收件箱，2是发件箱
	public static int RETURN_FLAG_MESSAGE = 100;
	public TextView topbar_txt;
	public static Button topbar_left;
	public static Handler mHandler1 = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (mWaitLoading != null) {
				mWaitLoading.dismiss();
				mWaitLoading = null;
			}
			switch (msg.what) {
			// 注册失败
			case 2:
				Toast.makeText(mContext, mErrorInfo, Toast.LENGTH_SHORT).show();
				break;

			// 注册成功
			case 3:
				mEmailMessageSpinner.setId(0);
				mEmailMessageTitleEditText.setText("");
				mEmailMessageBodyEditText.setText("");
				Toast.makeText(mContext, "发送成功啦~", Toast.LENGTH_SHORT).show();
				break;

			// 通讯错误
			case 4:
				Toast.makeText(mContext, "通信错误，请检查网络或稍后再试。", Toast.LENGTH_SHORT)
						.show();
				break;

			case 8:
				Toast.makeText(mContext, desc, Toast.LENGTH_SHORT).show();
				break;
			case 9:
				Toast.makeText(mContext, "网络超时，请重新获取", Toast.LENGTH_SHORT)
						.show();
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};
	public Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constants.MSG_NETWORK_ERROR:
				if (mProgressDialog != null) {
					mProgressDialog.dismiss();
					mProgressDialog = null;
				}
				if (mPageNum == 1) {
					View view = findViewById(R.id.view_network_error);
					view.setVisibility(View.VISIBLE);
				} else {
					mPageNum--;
					mAllowGetBoxAgain = true;
				}
				break;
			case Constants.MSG_MESSAGE_OUT_BOX_FINISH:
				showMessageOutBoxListView(mMessageOutBoxThread.getActivitys());
				break;
			case Constants.MSG_MESSAGE_IN_BOX_FINISH:
				showMessageInBoxListView(mMessageInBoxThread.getActivitys());
				break;
			case Constants.MSG_MESSAGE_IN_BOX_CONTENT_FINISH:
				showMessageInBoxContentListView(mMessageInBoxContentThread.getActivitys());
				break;
			case Constants.MSG_GET_MESSAGE_TYPE_FINISH:
				if (mProgressDialog != null) {
					mProgressDialog.dismiss();
					mProgressDialog = null;
				}
				try {
					mMessageTypeInfoList = mMessageTypeThread
							.getMessageTypeList();
					mEmailMessageSpinnerAdapter.add("<-选择分类->");
					for (int i = 0; i < mMessageTypeInfoList.size(); i++) {
						String areaName = mMessageTypeInfoList.get(i).messageTypeName;
						mEmailMessageSpinnerAdapter.add(areaName);
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}

				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_radio3);
		mContext = this;
		SharedPreferences saving = this.getSharedPreferences(
				LocalStore.USER_INFO, 0);
		propertyid = (int) saving.getLong(LocalStore.PROPERTY_ID, 0);
		userid = LocalStore.getUserInfo().userId;
		topbar_txt = (TextView) findViewById(R.id.topbar_txt);
		topbar_txt.setText(getResources().getText(R.string.radio_main3));
		topbar_left = (Button) findViewById(R.id.topbar_left);
		topbar_left.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (1 == returnFlag) {
					refreshMessageInBox();
				}
				if (2 == returnFlag) {
					refreshMessageOutBox();
				}
			}
		});
		mEmailMessageAddLinearLayout = (LinearLayout) findViewById(R.id.EmailMessageAddLinearLayout);
		mEmailMessageShowLinearLayout = (LinearLayout) findViewById(R.id.EmailMessageShowLinearLayout);
		mEmailMessageSpinner = (Spinner) findViewById(R.id.EmailMessageSpinner);
		mEmailMessageTitleEditText = (EditText) findViewById(R.id.EmailMessageTitleEditText);
		mEmailMessageBodyEditText = (EditText) findViewById(R.id.EmailMessageBodyEditText);
		mSendMessageButton = (Button) findViewById(R.id.SendMessageButton);
		mMessageShowTitleTextView = (TextView) findViewById(R.id.MessageShowTitleTextView);
		mMessageShowTimeTextView = (TextView) findViewById(R.id.MessageShowTimeTextView);
		mMessageShowBodyTextView = (TextView) findViewById(R.id.MessageShowBodyTextView);
		mIsVisitorTextView = (TextView) findViewById(R.id.IsVisitorTextView);
		mSendMessageShowButton = (Button) findViewById(R.id.SendMessageShowButton);
		mSendInBoxMessageButton= (Button) findViewById(R.id.SendInBoxMessageButton);
		
		if (LocalStore.getIsVisitor(this)) {
			mIsVisitorTextView.setVisibility(View.VISIBLE);
		}
		mEmailMessageSpinnerAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item);
		mEmailMessageSpinner
				.setOnItemSelectedListener(new OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {

						Spinner mSpinner = (Spinner) parent;
						if (position > 0) {
							messageTypeId = mMessageTypeInfoList
									.get(position - 1).messageTypeID;

							LogUtil.d("MyTag", "MessageTypeId=" + messageTypeId);

						}

					}

					public void onNothingSelected(AdapterView<?> parent) {
					}

				});
		mSendMessageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				sendMessage(Radio3Activity.this, mEmailMessageTitleEditText
						.getText().toString().trim(), mEmailMessageBodyEditText
						.getText().toString().trim());
			
			}
		});
		if (LocalStore.getIsVisitor(this)) {
			mSendMessageShowButton.setClickable(false);
			mSendMessageButton.setClickable(false);
		} else {
			mSendMessageShowButton.setClickable(true);
			mSendMessageButton.setClickable(true);
		}
		mEmailMessageOutboxListView = (ListView) findViewById(R.id.EmailMessageOutboxListView);
		// 设置不显示滚动条
		mEmailMessageOutboxListView.setVerticalScrollBarEnabled(false);
		mEmailMessageOutboxListView.setDivider(getResources().getDrawable(
				R.drawable.list_line));

		mEmailMessageInboxListView = (ListView) findViewById(R.id.EmailMessageInboxListView);
		// 设置不显示滚动条
		mEmailMessageInboxListView.setVerticalScrollBarEnabled(false);
		mEmailMessageInboxListView.setDivider(getResources().getDrawable(
				R.drawable.list_line));
		mEmailMessageInboxContentListView = (ListView) findViewById(R.id.EmailMessageInboxContentListView);
		mEmailMessageInboxContentListView.setVerticalScrollBarEnabled(false);
		mEmailMessageInboxContentListView.setDivider(null);
		mEmailMessageInboxContentLinearLayout = (LinearLayout) findViewById(R.id.EmailMessageInboxContentLinearLayout);
		Bundle bundler = getIntent().getExtras();
		if (bundler != null) {
			if (bundler.containsKey("messageStatus")) {
				messageStatus = bundler.getInt("messageStatus");
			}
		}

	}

	// 查看发件箱内容详情
	public static void outBoxMessageContants(final Context mContext,
			final MessageInfo mMessageInfo) {
		returnFlag = 2;
		topbar_left.setText("返回");
		topbar_left.setVisibility(View.VISIBLE);
		mSendMessageShowButton.setVisibility(View.VISIBLE);
		mEmailMessageAddLinearLayout.setVisibility(View.GONE);
		mEmailMessageShowLinearLayout.setVisibility(View.VISIBLE);
		mEmailMessageOutboxListView.setVisibility(View.GONE);
		mEmailMessageInboxListView.setVisibility(View.GONE);
		mEmailMessageInboxContentLinearLayout.setVisibility(View.GONE);
		mMessageShowTitleTextView.setText(mMessageInfo.header);
		mMessageShowTimeTextView.setText("发送于：" + mMessageInfo.cTime);
		mMessageShowBodyTextView.setText(mMessageInfo.body);
		mSendMessageShowButton.setText("再次发送");
		messageTypeId = mMessageInfo.type.messageTypeID;
		mSendMessageShowButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sendReMessage(mContext,mMessageInfo.header, mMessageInfo.body);
			}
		});
		if (LocalStore.getIsVisitor(mContext)) {
			mSendMessageShowButton.setClickable(false);
			mSendMessageButton.setClickable(false);
		} else {
			mSendMessageShowButton.setClickable(true);
			mSendMessageButton.setClickable(true);
		}
	}

	// 查看收件箱内容详情
	public static void inBoxMessageContants(final Context mContext,final Handler mHandler,
			final MessageInfo mMessageInfo) {
		returnFlag = 1;
		topbar_left.setText("返回");
		topbar_left.setVisibility(View.VISIBLE);
		mEmailMessageAddLinearLayout.setVisibility(View.GONE);
		mEmailMessageShowLinearLayout.setVisibility(View.GONE);
		mEmailMessageInboxContentLinearLayout.setVisibility(View.VISIBLE);
		mEmailMessageOutboxListView.setVisibility(View.GONE);
		mEmailMessageInboxListView.setVisibility(View.GONE);
		mMessageInBoxContentThread = new MessageInBoxContentThread(mContext,
				mHandler, propertyid, LocalStore.getUserInfo().userId, mMessageInfo.messageID);
		mMessageInBoxContentThread.start();
	    // st(mContext,mHandler,mMessageInfo);
		if (3 == mMessageInfo.type.messageTypeID) {
			mSendInBoxMessageButton.setVisibility(View.GONE);
		} else {
			mSendInBoxMessageButton.setVisibility(View.VISIBLE);
			mSendInBoxMessageButton.setText("回复");
		}

		messageTypeId = mMessageInfo.type.messageTypeID;
		mSendInBoxMessageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sendReNewMessage(mContext, mMessageInfo);
			}
		});
		if (LocalStore.getIsVisitor(mContext)) {
			mSendMessageShowButton.setClickable(false);
			mSendMessageButton.setClickable(false);
		} else {
			mSendMessageShowButton.setClickable(true);
			mSendMessageButton.setClickable(true);
		}
	}

	// 回复
	public static void sendReNewMessage(final Context mContext,
			final MessageInfo mMessageInfo) {
		returnFlag = 1;
		topbar_left.setText("返回");
		topbar_left.setVisibility(View.VISIBLE);
		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(
				mContext, android.R.layout.simple_spinner_item);
		spinnerAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerAdapter.add(mMessageInfo.type.messageTypeName);
		mEmailMessageSpinner.setAdapter(spinnerAdapter);
		mEmailMessageTitleEditText.setText(mMessageInfo.header);
		mEmailMessageBodyEditText.setText("--原始邮件-- \n 在" + mMessageInfo.cTime
				+ "写道：\n" + mMessageInfo.body);
		mEmailMessageAddLinearLayout.setVisibility(View.VISIBLE);
		mEmailMessageShowLinearLayout.setVisibility(View.GONE);
		mEmailMessageOutboxListView.setVisibility(View.GONE);
		mEmailMessageInboxListView.setVisibility(View.GONE);
		mEmailMessageInboxContentLinearLayout.setVisibility(View.GONE);
		mSendMessageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d("MyTag","sendReNewMessage mSendMessageButton="+mMessageInfo.messageID);
				sendInBoxMessage(mContext, mMessageInfo.messageID,mEmailMessageTitleEditText
						.getText().toString().trim(), mEmailMessageBodyEditText
						.getText().toString().trim());
			}
		});
	}


	// 新建信息
	public void refreshNewMessage() {
		returnFlag = 0;
		topbar_left.setVisibility(View.GONE);
		mAllowGetBoxAgain = false;
		mItemSum = 0;
		mPageNum = 1;
		initResource();
		mEmailMessageSpinnerAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item);
		mEmailMessageSpinnerAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mEmailMessageSpinner.setAdapter(mEmailMessageSpinnerAdapter);
		mEmailMessageAddLinearLayout.setVisibility(View.VISIBLE);
		mEmailMessageShowLinearLayout.setVisibility(View.GONE);
		mEmailMessageOutboxListView.setVisibility(View.GONE);
		mEmailMessageInboxListView.setVisibility(View.GONE);
		mEmailMessageInboxContentLinearLayout.setVisibility(View.GONE);
	}

	// 发件箱
	public void refreshMessageOutBox() {
		returnFlag = 0;
		topbar_left.setVisibility(View.GONE);
		mAllowGetBoxAgain = false;
		mItemSum = 0;
		mPageNum = 1;
		mEmailMessageAddLinearLayout.setVisibility(View.GONE);
		mEmailMessageOutboxListView.setVisibility(View.GONE);
		mEmailMessageInboxListView.setVisibility(View.GONE);
		mEmailMessageInboxContentLinearLayout.setVisibility(View.GONE);
		mEmailMessageShowLinearLayout.setVisibility(View.GONE);
		mEmailMessageOutboxListView.addFooterView(showLayout());
		mEmailMessageOutboxListView.setOnScrollListener(mOutBoxScrollListner);
		freeResource();
		startProgressDialog();
		mMessageOutBoxThread = new MessageOutBoxThread(Radio3Activity.this,
				mHandler, propertyid, userid, mPageNum);
		mMessageOutBoxThread.start();
	}

	// 收件箱
	public void refreshMessageInBox() {
		returnFlag = 0;
		topbar_left.setVisibility(View.GONE);
		mAllowGetBoxAgain = false;
		mItemSum = 0;
		mPageNum = 1;
		mEmailMessageAddLinearLayout.setVisibility(View.GONE);
		mEmailMessageOutboxListView.setVisibility(View.GONE);
		mEmailMessageInboxListView.setVisibility(View.GONE);
		mEmailMessageShowLinearLayout.setVisibility(View.GONE);
		mEmailMessageInboxContentLinearLayout.setVisibility(View.GONE);
		mEmailMessageInboxListView.addFooterView(showLayout());
		mEmailMessageInboxListView.setOnScrollListener(mInBoxScrollListner);
		freeResource();
		startProgressDialog();
		mMessageInBoxThread = new MessageInBoxThread(Radio3Activity.this,
				mHandler, propertyid, userid, mPageNum);
		mMessageInBoxThread.start();
	}

	// 发送新建信息
	private static void sendMessage(final Context activity,
			final String mEmailMessageTitle, final String mEmailMessageBody) {

		if (messageTypeId == -1) {
			Toast.makeText(activity, "请选择分类", Toast.LENGTH_SHORT).show();
			return;
		}
		if (Util.isEmpty(mEmailMessageTitleEditText)) {
			Toast.makeText(activity, "标题不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		if (Util.isEmpty(mEmailMessageBodyEditText)) {
			Toast.makeText(activity, "内容不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		mWaitLoading = ProgressDialog.show(activity, null, "发送中，请稍候......",
				true);
		Thread registerThread = new Thread() {
			public void run() {
				RemoteApiImpl remote = new RemoteApiImpl();
				NetInfo netInfo = remote.sendMessage(activity,
						LocalStore.getUserInfo().userId, propertyid,
						messageTypeId, mEmailMessageTitle, mEmailMessageBody);

				Message msg = new Message();
				if (netInfo == null) {
					msg.what = 4;
				} else if (200 == netInfo.code) {
					msg.what = 3;
				} else {
					msg.what = 2;
					mErrorInfo = netInfo.desc;
				}
				mHandler1.sendMessage(msg);
			}
		};
		registerThread.start();
	}

	// 再次发送信息
	private static void sendReMessage(final Context activity,
			final String mEmailMessageTitle, final String mEmailMessageBody) {
		mWaitLoading = ProgressDialog.show(activity, null, "发送中，请稍候......",
				true);
		Thread registerThread = new Thread() {
			public void run() {
				RemoteApiImpl remote = new RemoteApiImpl();
				NetInfo netInfo = remote.sendMessage(activity,
						LocalStore.getUserInfo().userId, propertyid,
						messageTypeId, mEmailMessageTitle, mEmailMessageBody);


				Message msg = new Message();
				if (netInfo == null) {
					msg.what = 4;
				} else if (200 == netInfo.code) {
					msg.what = 3;
				} else {
					msg.what = 2;
					mErrorInfo = netInfo.desc;
				}
				mHandler1.sendMessage(msg);
			}
		};
		registerThread.start();
	}

	// 再次发送收件箱信息
		private static void sendInBoxMessage(final Context activity,
				final long messageId,final String mEmailMessageTitle, final String mEmailMessageBody) {
			mWaitLoading = ProgressDialog.show(activity, null, "发送中，请稍候......",
					true);
			Thread registerThread = new Thread() {
				public void run() {
					RemoteApiImpl remote = new RemoteApiImpl();
					NetInfo netInfo = remote.sendMessageReply(mContext, LocalStore.getUserInfo().userId, propertyid,
							(int)messageId, mEmailMessageTitle, mEmailMessageBody);

					Message msg = new Message();
					if (netInfo == null) {
						msg.what = 4;
					} else if (200 == netInfo.code) {
						msg.what = 3;
					} else {
						msg.what = 2;
						mErrorInfo = netInfo.desc;
					}
					mHandler1.sendMessage(msg);
				}
			};
			registerThread.start();
		}
	@Override
	protected void onNewIntent(Intent intent) {
		Boolean needInit = intent.getBooleanExtra("FromGroup", false);
		if (needInit) { // 重新获取资源
			startProgressDialog();
			endChildrenThreads();
			mMessageTypeThread = new MessageTypeThread(this, mHandler,
					propertyid);
			mMessageTypeThread.start();
		}
		super.onNewIntent(intent);
	}

	@Override
	protected void onDestroy() {
		endChildrenThreads();
		super.onDestroy();
	}

	private void startProgressDialog() {
		View view = findViewById(R.id.view_network_error);
		view.setVisibility(View.GONE);
		// mLogsListView.setVisibility(View.GONE);
		if (mProgressDialog != null)
			mProgressDialog.dismiss();
		mProgressDialog = new ProgressDialog(Radio3Activity.this);
		mProgressDialog.setMessage("加载中，请稍候...");
		mProgressDialog.setCancelable(true);
		mProgressDialog.show();
	}

	private void endChildrenThreads() {
		if (mMessageTypeThread != null) {
			mMessageTypeThread.stopRun();
			mMessageTypeThread = null;
		}
		if (mMessageInBoxThread != null) {
			mMessageInBoxThread.stopRun();
			mMessageInBoxThread = null;
		}
		if (mMessageInBoxThread != null) {
			mMessageInBoxThread.stopRun();
			mMessageInBoxThread = null;
		}
		mEmailMessageSpinnerAdapter = null;
		mMessageOutBoxAdapter = null;
		mMessageInBoxAdapter = null;
		mAllowGetBoxAgain = true;
		mPageNum = 1;
		mItemSum = 0;

	}

	private void stopProgressDialog() {

		if (mProgressDialog != null) {
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
	}

	public void showMessageOutBoxListView(List<MessageInfo> list) {
		searchLayout.setVisibility(View.GONE);
		if (list == null) {
			if (mProgressDialog != null) {
				mProgressDialog.dismiss();
				mProgressDialog = null;

			}
			searchLayout.setVisibility(View.GONE);
			return;
		}
		mEmailMessageOutboxListView.setVisibility(View.VISIBLE);
		if (mPageNum == 1) {
			mMessageOutBoxAdapter = new MessageOutBoxAdapter(this, list);
			mItemSum = mMessageOutBoxAdapter.getCount();
			if (mProgressDialog != null) {
				mProgressDialog.dismiss();
				mProgressDialog = null;
			}
			mEmailMessageOutboxListView.setAdapter(mMessageOutBoxAdapter);
		} else {
			mMessageOutBoxAdapter.appandAdapter(list);
			mMessageOutBoxAdapter.notifyDataSetChanged();
			mItemSum = mMessageOutBoxAdapter.getCount();
			int test12 = mItemSum % 10;
			if (test12 == 0)
				mEmailMessageOutboxListView.setSelection(mItemSum - 10);
			else
				mEmailMessageOutboxListView.setSelection(mItemSum - test12);
		}
		setAllowGetBoxAgain();
	}

	public void showMessageInBoxListView(List<MessageInfo> list) {
		searchLayout.setVisibility(View.GONE);
		if (list == null) {
			if (mProgressDialog != null) {
				mProgressDialog.dismiss();
				mProgressDialog = null;

			}
			searchLayout.setVisibility(View.GONE);
			return;
		}
		mEmailMessageInboxListView.setVisibility(View.VISIBLE);
		if (mPageNum == 1) {
			mMessageInBoxAdapter = new MessageInBoxAdapter(this,mHandler, list);
			mItemSum = mMessageInBoxAdapter.getCount();
			if (mProgressDialog != null) {
				mProgressDialog.dismiss();
				mProgressDialog = null;
			}
			mEmailMessageInboxListView.setAdapter(mMessageInBoxAdapter);
		} else {
			LogUtil.d("MyTag", "mMessageInBoxAdapter=" + mMessageInBoxAdapter
					+ "list=" + list);
			mMessageInBoxAdapter.appandAdapter(list);
			mMessageInBoxAdapter.notifyDataSetChanged();
			mItemSum = mMessageInBoxAdapter.getCount();
			int test12 = mItemSum % 10;
			if (test12 == 0)
				mEmailMessageInboxListView.setSelection(mItemSum - 10);
			else
				mEmailMessageInboxListView.setSelection(mItemSum - test12);
		}
		setAllowGetBoxAgain();
	}

	public void showMessageInBoxContentListView(List<MessageInfo> list) {
		if (list == null) {
			if (mProgressDialog != null) {
				mProgressDialog.dismiss();
				mProgressDialog = null;

			}
			return;
		}
		mEmailMessageInboxContentListView.setVisibility(View.VISIBLE);
			mMessageInBoxContentAdapter = new MessageInBoxContentAdapter(this, list);
			mEmailMessageInboxContentListView.setAdapter(mMessageInBoxContentAdapter);
			mMessageInBoxContentAdapter.notifyDataSetChanged();
			mItemSum = mMessageInBoxContentAdapter.getCount();
			mEmailMessageInboxContentListView.setSelection(mItemSum - 1);
	}
	// 以下为分页显示代码
	private static LinearLayout searchLayout = null;

	public LinearLayout showLayout() {
		searchLayout = new LinearLayout(this);
		searchLayout.setOrientation(LinearLayout.HORIZONTAL);

		ProgressBar progressBar = new ProgressBar(this);
		progressBar.setPadding(0, 0, 15, 0);
		progressBar.setIndeterminateDrawable(getResources().getDrawable(
				R.drawable.progress_medium));
		searchLayout.addView(progressBar, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));

		TextView textView = new TextView(this);
		textView.setText("加载中...");
		textView.setGravity(Gravity.CENTER_VERTICAL);
		searchLayout.addView(textView, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT));
		searchLayout.setGravity(Gravity.CENTER);

		LinearLayout loadingLayout = new LinearLayout(this);
		loadingLayout.addView(searchLayout, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		loadingLayout.setGravity(Gravity.CENTER);
		return loadingLayout;
	}

	private static boolean mAllowGetBoxAgain = false;
	private static int mItemSum = 0;
	private int mPageNum = 1;
	private final int mPagecount = 10;

	public static void setAllowGetBoxAgain() {

		int test12 = mItemSum % 10;
		LogUtil.d("setAllowGetBoxAgain",
				"setAllowGetBoxAgain mAllowGetBoxAgain = true" + mItemSum + "/"
						+ test12);
		if ((test12 != 0) || (mItemSum >= 96)) {
			mAllowGetBoxAgain = false;
			searchLayout.setVisibility(View.GONE);
		} else {
			if (mItemSum > 9) {
				LogUtil.d("setAllowGetBoxAgain",
						"setAllowGetBoxAgain mAllowGetBoxAgain = true"
								+ mItemSum);
				mAllowGetBoxAgain = true;
			} else {
				LogUtil.d("setAllowGetBoxAgain",
						"setAllowGetBoxAgain mAllowGetBoxAgain = false"
								+ mItemSum);
				mAllowGetBoxAgain = false;
				searchLayout.setVisibility(View.GONE);
			}

		}
	}

	private OnScrollListener mOutBoxScrollListner = new OnScrollListener() {
		private int lastItem = 0;

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			lastItem = firstVisibleItem + visibleItemCount - 1;
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			if (mMessageOutBoxAdapter != null
					&& lastItem >= (mMessageOutBoxAdapter.getCount() - 2)) {
				if (!mAllowGetBoxAgain)
					return;
				mAllowGetBoxAgain = false;
				mPageNum++;
				mMessageOutBoxThread = new MessageOutBoxThread(
						Radio3Activity.this, mHandler, propertyid, userid,
						mPageNum);
				mMessageOutBoxThread.start();
			}
		}
	};
	private OnScrollListener mInBoxScrollListner = new OnScrollListener() {
		private int lastItem = 0;

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			lastItem = firstVisibleItem + visibleItemCount - 1;

		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			if (mMessageInBoxAdapter != null
					&& lastItem >= (mMessageInBoxAdapter.getCount() - 2)) {
				if (!mAllowGetBoxAgain)
					return;
				mAllowGetBoxAgain = false;
				mPageNum++;
				mMessageInBoxThread = new MessageInBoxThread(
						Radio3Activity.this, mHandler, propertyid, userid,
						mPageNum);
				mMessageInBoxThread.start();
			}
		}
	};

	void freeResource() {
		// TODO Auto-generated method stub
		mMessageTypeThread = null;
		stopProgressDialog();
		endChildrenThreads();
	}

	void initResource() {
		// TODO Auto-generated method stub
		freeResource();
		if (mMessageTypeThread == null)
			startProgressDialog();
		mMessageTypeThread = new MessageTypeThread(this, mHandler, propertyid);
		mMessageTypeThread.start();
	}

	String TAG = "MyTag";

	@Override
	protected void onStart() {
		super.onStart();
		Log.e(TAG, "start onStart~~~");
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		Log.e(TAG, "start onRestart~~~");
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.e(TAG, "start onResume~~~");
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.e(TAG, "start onPause~~~");
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.e(TAG, "start onStop~~~");
	}

}
