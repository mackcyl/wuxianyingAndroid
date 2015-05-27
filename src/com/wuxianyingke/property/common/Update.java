package com.wuxianyingke.property.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.StringReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mantoto.property.R;
import com.wuxianyingke.property.remote.HttpConnect;

public class Update {

	private static String updateUrl = "";
	private static String newFeature = "";
	private static int flag = 0;
	public static AlertDialog dialog = null;
	public static AlertDialog waitDialog = null;
	public static int featureCount = 0;
	public static Handler realHandler = null;
	public static TextView featureText = null;
	public static Timer timer = new Timer();
	public static TimerTask task;
    public static Handler mHandler;
    public static HandlerThread downThread = null;
    public static String newAppContent = "";// 升级app，显示的升级内容
	public static void checkVersion(final Context context, final Activity activity, final Handler handler, String packageName,
									String version, String softtype, int showUIFlag) 
	{
		try 
		{
			mHandler=handler;
			Map<String, String> params = new HashMap<String, String>();
			params.put("packagename", packageName);
			params.put("version", version);
			params.put("model", softtype);
			params.put("imei", Util.getIME(context));
			//params.put("packageversion", version);
			//params.put("imei", md5imei);
			//params.put("channelid", channelId);

			HttpConnect http = new HttpConnect();
			http.setConnectTimeout(60000);
			 String result = "";
			result=http.connectByGetString(Constants.URL + Constants.UPDATESERVER_FOLLOW, params);
			LogUtil.d("MyTag", "update get return = "+result);
			if (result == null || result.equals("") || result.equals("2001")) 
			{
				if (showUIFlag == 1) 
				{
					handler.post(new Runnable() 
					{
						public void run() 
						{
							info(context, R.drawable.sec_alert, context.getString(R.string.update_inavailable_title), context.getString(R.string.update_inavailable_info), 
									context.getString(R.string.txt_ok),
									new OnClickListener() 
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
			else
				getXml(result);
		} 
		catch (final Exception ex) 
		{
			if (showUIFlag == 1) 
			{
				handler.post(new Runnable() 
				{
					public void run() 
					{
						info(context, R.drawable.sec_alert, context.getString(R.string.update_error_title), context.getString(R.string.update_error_info), 
								context.getString(R.string.txt_ok),
								new OnClickListener() 
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
			return;
		}

		if (flag == 1) //强制更新
		{
			try 
			{
				handler.post(new Runnable() {
					public void run() {
						info(context,
								R.drawable.sec_alert,
								context.getString(R.string.update_available_title),

								Html.fromHtml(newAppContent) + "",
								context.getString(R.string.update_available_buttonLeft),
								new OnClickListener() {
									@Override
									public void onClick(View v) {
										dialog.dismiss();
										waitDialog = new AlertDialog.Builder(
												context).create();
										LayoutInflater inflater = LayoutInflater
												.from(context);
										View backupExpandHeader = inflater
												.inflate(
														R.layout.sec_update_dialog,
														null);
										waitDialog.setCancelable(false);
										waitDialog.show();
										waitDialog
												.setContentView(backupExpandHeader);
										installApp(updateUrl, activity, handler);
									}
								}, null, null);
					}
				});


			} 
			catch (final Exception ex) 
			{
				handler.post(new Runnable() 
				{
					public void run() 
					{
						info(context, R.drawable.sec_alert, context.getString(R.string.update_error_title), context.getString(R.string.update_error_info2), 
								context.getString(R.string.txt_ok),
								new OnClickListener() 
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
		else if(flag == 2) //提示是否更新
		{
			realHandler = handler;
			handler.post(new Runnable() 
			{
				public void run() 
				{
					try 
					{
						info(context, R.drawable.sec_alert, context.getString(R.string.update_available_title), 
								Html.fromHtml(newAppContent) + "", 
								context.getString(R.string.update_available_buttonLeft),
								new OnClickListener()
								{
									@Override
									public void onClick(View v)
									{
										dialog.dismiss();
										waitDialog = new AlertDialog.Builder(context).create();
										LayoutInflater inflater = LayoutInflater.from(context);
										View backupExpandHeader = inflater.inflate(R.layout.sec_update_dialog, null);
										featureText = (TextView) backupExpandHeader.findViewById(R.id.newFunText);
										int sumFeature = newFeature.split("#").length;
										featureCount = 0;
										if(sumFeature > 0)
											featureText.setText(newFeature.split("#")[featureCount]);
										waitDialog.setCancelable(false);
										waitDialog.show();
										waitDialog.setContentView(backupExpandHeader);
										
										task = new TimerTask()
										{   
											public void run() 
											{   
												realHandler.post(new Runnable() 
												{
													public void run() 
													{
														int sumFeature = newFeature.split("#").length;
														featureCount++;
														if(sumFeature > featureCount)
														{
															featureText.setText(newFeature.split("#")[featureCount]);
														}
														else
														{
															featureCount = 0;
															featureText.setText(newFeature.split("#")[featureCount]);
														}
													}
												});	
											}   
										}; 
										timer.schedule(task, 1000, 1000);
										
										installApp(updateUrl, activity, handler);
									}
								},
								context.getString(R.string.update_available_buttonRight),
								new OnClickListener() 
								{
									@Override
									public void onClick(View v)
									{
										dialog.dismiss();
									}
								});
					} 
					catch (Exception ex) 
					{
						handler.post(new Runnable() 
						{
							public void run() 
							{
								info(context, R.drawable.sec_alert, context.getString(R.string.update_error_title), context.getString(R.string.update_error_info2), 
										context.getString(R.string.txt_ok),
										new OnClickListener() 
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
	}

	public static void info(Context ctx, int iconTitleId, String titleText, String infoText, 
							String leftText, OnClickListener leftListener,
							String rightText, OnClickListener rightListener) 
	{	
		mHandler.sendEmptyMessage(11);
		if((waitDialog!=null) && waitDialog.isShowing())
			waitDialog.dismiss();
		dialog = new AlertDialog.Builder(ctx).create();
		LayoutInflater inflater = LayoutInflater.from(ctx);
		View backupExpandHeader = inflater.inflate(R.layout.sec_note_dialog, null);
		
		ImageView titleIcon = (ImageView) backupExpandHeader.findViewById(R.id.popDialogTitleIcon);
		titleIcon.setBackgroundResource(iconTitleId);
		TextView popTitle = (TextView) backupExpandHeader.findViewById(R.id.popDialogTitle);
		popTitle.setText(titleText);
		TextView popInfo = (TextView) backupExpandHeader.findViewById(R.id.popDialogInfo);
		popInfo.setText(infoText);
		Button okButton = (Button) backupExpandHeader.findViewById(R.id.ButtonOK);
		if(leftListener != null)
		{
			okButton.setText(leftText);
			okButton.setOnClickListener(leftListener);
		}
		else
			backupExpandHeader.findViewById(R.id.linearButtonLeft).setVisibility(View.GONE);
		Button cancelButton = (Button) backupExpandHeader.findViewById(R.id.ButtonCancel);
		if(rightListener != null)
		{
			cancelButton.setText(rightText);
			cancelButton.setOnClickListener(rightListener);
		}
		else
			backupExpandHeader.findViewById(R.id.linearButtonRight).setVisibility(View.GONE);
		
		dialog.show();
		dialog.setContentView(backupExpandHeader);
	}

	public static void getXml(String xml) 
	{
		try 
		{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder dbuilder = dbf.newDocumentBuilder();
			InputSource is = new InputSource(new StringReader(xml));
			Document dom = dbuilder.parse(is);
			String appDownloadUrl = dom.getElementsByTagName("optionalupdate").item(0).getAttributes().getNamedItem("data").getNodeValue();
			flag = Integer.valueOf(appDownloadUrl);
			updateUrl = dom.getElementsByTagName("appupdateurl").item(0).getAttributes().getNamedItem("data").getNodeValue();
			String urlStr = dom.getElementsByTagName("appnewinfo").item(0).getAttributes().getNamedItem("data").getNodeValue();
			newFeature = java.net.URLDecoder.decode(urlStr, "GBK");
			newAppContent = java.net.URLDecoder.decode(urlStr, "utf-8");
			newAppContent = newAppContent.replace("#", "<br>");
			LogUtil.d("MyTag", "flag ="+flag);
			LogUtil.d("MyTag", "updateUrl ="+updateUrl);
			LogUtil.d("MyTag", "newAppContent ="+newAppContent);

		} catch (Exception e1){

		}
	}
	public static void installApp(final String url, final Activity activity,
			final Handler handler) {
		new HandlerThread("") {
			public void run() {
				FileOutputStream fos = null;
				InputStream bis = null;
				try {
					URL sourceUrl = new URL(url);
					// 获取文件名
					String fileName = sourceUrl.getFile().substring(
							sourceUrl.getFile().lastIndexOf('/') + 1);
					LogUtil.d("MyTag", "fileName= " + fileName);

					// 保存文件
					String oldFileUrl = "/sdcard/wxyk/";
				File srcFile = new File(oldFileUrl);
				if (!srcFile.exists())
					srcFile.mkdirs();// 创建存在下载文件夹

				File f = new File(oldFileUrl+ fileName) ;
				if(f.exists()){
					f.delete() ;
				}
				
				RandomAccessFile oSavedFile = new RandomAccessFile(oldFileUrl+ fileName, "rws");
				
				bis = sourceUrl.openConnection().getInputStream();
				int read = -1;
				byte[] buffer = new byte[1024];

				while ((read = bis.read(buffer)) != -1)
				{
					oSavedFile.write(buffer, 0, read);
					
					
				}
					/*fos = activity.openFileOutput(fileName,
							Context.MODE_WORLD_READABLE);
					int read = 0;
					byte[] buffer = new byte[512];
					bis = sourceUrl.openConnection().getInputStream();
					do {
						read = bis.read(buffer);
						if (read > 0) {
							fos.write(buffer, 0, read);
						}
					} while (read != -1);*/
					bis.close();

					// 获取url,然后调用系统的Intent,来自动安装下载的apk文件
					final File saveNewFile = new File(oldFileUrl,
							fileName);// 构建保存文件
					
					
				/*	
					final File realFilePath = activity
							.getFileStreamPath(fileName);
					Uri packageURI = Uri.fromFile(realFilePath);
					final String archiveFilePath = packageURI.getPath();
					File sourceFile = new File(archiveFilePath);
					if (!sourceFile.isFile()) {
						Log.d("MyTag", "sourceFile= " + archiveFilePath);
					}*/
					LogUtil.d("MyTag", "111111111111111");
					Thread.sleep(7000);
					LogUtil.d("MyTag", "222222222222222");
					if ((waitDialog != null) && waitDialog.isShowing()) {
						task.cancel();
						waitDialog.dismiss();
					}
					handler.post(new Runnable() {
						public void run() {
							Intent intent = new Intent(Intent.ACTION_VIEW);
							intent.setDataAndType(Uri.fromFile(saveNewFile),
									"application/vnd.android.package-archive");
							activity.startActivity(intent);
							LogUtil.d("MyTag", "start install");
							
							
						}
					});
				} catch (final Exception ex) {
					ex.printStackTrace();
					LogUtil.d("MyTag", "install apk error");
				}
			}
		}.start();
	}

}
/*	public static void installApp(final String url, final Activity activity, final Handler handler) 
	{
		downThread = new HandlerThread("")
		{
			public void run() 
			{
				FileOutputStream fos = null;
				InputStream bis = null;
				try {
					URL sourceUrl = new URL(url);
					// 获取文件名
					String fileName = sourceUrl.getFile().substring(sourceUrl.getFile().lastIndexOf('/') + 1);
					Log.d("MyTag", "fileName= "+fileName);
					
					// 保存文件
					fos = activity.openFileOutput(fileName, Context.MODE_WORLD_READABLE);
					int read = 0;
					byte[] buffer = new byte[512];
					bis = sourceUrl.openConnection().getInputStream();
					do {
						read = bis.read(buffer);
						if (read > 0) {
							fos.write(buffer, 0, read);
						}
					} while (read != -1);
					fos.close();
					bis.close();
					
					// 获取url,然后调用系统的Intent,来自动安装下载的apk文件
					final File realFilePath = activity.getFileStreamPath(fileName);
					Uri packageURI = Uri.fromFile(realFilePath);
					final String archiveFilePath = packageURI.getPath();
					File sourceFile = new File(archiveFilePath);
					if (!sourceFile.isFile()) {
						Log.d("MyTag", "sourceFile= "+archiveFilePath);
					}
					Thread.sleep(6000);
					if((waitDialog!=null) && waitDialog.isShowing())
					{
						task.cancel();
						waitDialog.dismiss();
					}
					
					mHandler.post(new Runnable() {
						public void run() {
							Intent intent = new Intent(Intent.ACTION_VIEW);
							intent.setDataAndType(Uri.fromFile(realFilePath), "application/vnd.android.package-archive");
							activity.startActivity(intent);
							Log.d("MyTag", "start install");
						}
					});
				} catch (final Exception ex) {
					ex.printStackTrace();
					Log.d("MyTag", "install apk error");
				}
			}
		};
		downThread.start();
	}
}*/
