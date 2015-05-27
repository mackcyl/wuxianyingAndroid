package com.wuxianyingke.property.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mantoto.property.R;

public class DownApp
{

	private static String updateUrl = "";
	public static AlertDialog dialog = null;
	public static AlertDialog waitDialog = null;
	public static Handler realHandler = null;
	public static Timer timer = new Timer();
	public static TimerTask task;
	public static ProgressBar downProgress = null;
	public static long appSumSize;
	public static long currentSize;
	public static HandlerThread downThread = null;

	public static void downApp(final Context context, final Activity activity, final Handler handler, String appUrl, String appSize)
	{
		updateUrl = appUrl;
		realHandler = handler;
		appSumSize= Long.valueOf(appSize);
		currentSize = 0;
		handler.post(new Runnable()
		{
			public void run()
			{
				try
				{
					waitDialog = new AlertDialog.Builder(context).create();
					LayoutInflater inflater = LayoutInflater.from(context);
					View backupExpandHeader = inflater.inflate(R.layout.sec_download_dialog, null);
					downProgress = (ProgressBar) backupExpandHeader.findViewById(R.id.downloadProgress);
					waitDialog.setCancelable(false);
					waitDialog.show();
					waitDialog.setContentView(backupExpandHeader);
					
					Button cancelBtn = (Button) backupExpandHeader.findViewById(R.id.downloadCancel);
					cancelBtn.setOnClickListener(new OnClickListener()
					{
						@Override
						public void onClick(View v)
						{
							waitDialog.dismiss();
							waitDialog = null;
							if(downThread != null && downThread.isAlive())
							{
								downThread.interrupt();
							}
						}
					});

					task = new TimerTask()
					{
						public void run()
						{
							realHandler.post(new Runnable()
							{
								public void run()
								{
									int currentProgress = (int) (currentSize*100/appSumSize);
									downProgress.setProgress(currentProgress);
								}
							});
						}
					};
					timer.schedule(task, 1000, 1000);

					installApp(updateUrl, activity, handler);

				} catch (Exception ex)
				{
					LogUtil.d("MyTag", "Dowan app error: "+ex.getMessage());
					handler.post(new Runnable()
					{
						public void run()
						{
							info(context, "下载错误", "下载失败，请稍后再试", context
									.getString(R.string.txt_ok), new OnClickListener()
							{
								@Override
								public void onClick(View v)
								{

									dialog.dismiss();
								}
							}, null, null);
						}
					});
				}
			}
		});
	}

	public static void info(Context ctx, String titleText, String infoText, String leftText, OnClickListener leftListener, String rightText, OnClickListener rightListener)
	{
		if ((waitDialog != null) && waitDialog.isShowing())
			waitDialog.dismiss();
		dialog = new AlertDialog.Builder(ctx).create();
		LayoutInflater inflater = LayoutInflater.from(ctx);
		View backupExpandHeader = inflater.inflate(R.layout.sec_note_dialog, null);
		TextView popTitle = (TextView) backupExpandHeader.findViewById(R.id.popDialogTitle);
		popTitle.setText(titleText);
		TextView popInfo = (TextView) backupExpandHeader.findViewById(R.id.popDialogInfo);
		popInfo.setText(infoText);
		Button okButton = (Button) backupExpandHeader.findViewById(R.id.ButtonOK);
		if (leftListener != null)
		{
			okButton.setText(leftText);
			okButton.setOnClickListener(leftListener);
		} else
			backupExpandHeader.findViewById(R.id.linearButtonLeft).setVisibility(View.GONE);
		Button cancelButton = (Button) backupExpandHeader.findViewById(R.id.ButtonCancel);
		if (rightListener != null)
		{
			cancelButton.setText(rightText);
			cancelButton.setOnClickListener(rightListener);
		} else
			backupExpandHeader.findViewById(R.id.linearButtonRight).setVisibility(View.GONE);

		dialog.show();
		dialog.setContentView(backupExpandHeader);
	}

	public static void installApp(final String url, final Activity activity, final Handler handler)
	{
		downThread = new HandlerThread("")
		{
			public void run()
			{
				FileOutputStream fos = null;
				InputStream bis = null;
				try
				{
					URL sourceUrl = new URL(url);
					String fileName = "1010101.apk";
					activity.deleteFile(fileName);
					fos = activity.openFileOutput(fileName, Context.MODE_WORLD_READABLE);
					int read = 0;
					byte[] buffer = new byte[512];
					bis = sourceUrl.openConnection().getInputStream();
					do
					{
						if (waitDialog == null)
						{
							fos.close();
							bis.close();
							return;
						}
						read = bis.read(buffer);
						if (read > 0)
						{
							currentSize = currentSize + 512;
							fos.write(buffer, 0, read);
						}
					} while (read != -1);
					fos.close();
					bis.close();

					if (waitDialog == null)
						return;
					final File realFilePath = activity.getFileStreamPath(fileName);
					Uri packageURI = Uri.fromFile(realFilePath);
					final String archiveFilePath = packageURI.getPath();
					File sourceFile = new File(archiveFilePath);
					if (!sourceFile.isFile())
					{
						LogUtil.d("MyTag", "sourceFile= " + archiveFilePath);
					}
					if ((waitDialog != null) && waitDialog.isShowing())
					{
						task.cancel();
						waitDialog.dismiss();
					}
					handler.post(new Runnable()
					{
						public void run()
						{
							Intent intent = new Intent(Intent.ACTION_VIEW);
							intent.setDataAndType(Uri.fromFile(realFilePath), "application/vnd.android.package-archive");
							activity.startActivity(intent);
							LogUtil.d("MyTag", "start install");
						}
					});
					
				} catch (final Exception ex)
				{
					ex.printStackTrace();
					LogUtil.d("MyTag", "install apk error");
				}
			}
		};
		downThread.start();
	}
}
