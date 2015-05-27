package com.wuxianyingke.property.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Debug;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.mantoto.property.R;
import com.wuxianyingke.property.remote.RemoteApiImpl;

public class Util {
	public static int hitCount = 0;
	static int whichChecked = 0;

	private static int logNum = 0;

	public static void logHeap(Class clazz) {
		Double allocated = new Double(Debug.getNativeHeapAllocatedSize())
				/ new Double((1048576));
		Double available = new Double(Debug.getNativeHeapSize()) / (1048576.0f);
		Double free = new Double((double) Debug.getNativeHeapFreeSize()) / (1048576.0f);
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(2);
		df.setMinimumFractionDigits(2);

		Log.e("HeapLog", "debug.count = " + logNum);
		Log.e("HeapLog", "NativeHeapSize = " + df.format(available) + "MB"
				+ "   NativeHeapAllocatedSize = " + df.format(allocated) + "MB"
				+ "   NativeHeapFreeSize = " + df.format(free) + "MB" + " in ["
				+ clazz.getName() + "]");
		Log.e("HeapLog",
				"debug.memory: allocated: "
						+ df.format(new Double(Runtime.getRuntime()
								.totalMemory() / 1048576))
						+ "MB of "
						+ df.format(new Double(
								Runtime.getRuntime().maxMemory() / 1048576))
						+ "MB ("
						+ df.format(new Double(Runtime.getRuntime()
								.freeMemory() / 1048576)) + "MB free)");
		Log.e("HeapLog", "");
		logNum++;
	}

	public static Drawable getDrawableFromCache(Context ctx, String url)
			throws IOException {
		if (url == null || url.equals(""))
			return null;

		String urlPath = "";
		if (url.contains("http"))
			urlPath = url;
		/*else
			urlPath = Constants.PRODUCT_URL + url;*/
		return getDrawableFromCacheOrNet(ctx, urlPath);
	}

	public static Drawable getShopDrawableFromCache(Context ctx, String url)
			throws IOException {
		if (url == null || url.equals(""))
			return null;

		String urlPath = "";
		if (url.contains("http"))
			urlPath = url;
		/*else
			urlPath = Constants.SHOP_URL + url;*/
		return getDrawableFromCacheOrNet(ctx, urlPath);
	}

	public static Drawable getShareDrawableFromCache(Context ctx, String url)
			throws IOException {
		if (url == null || url.equals(""))
			return null;

		String urlPath = "";
		if (url.contains("http"))
			urlPath = url;
	/*	else
			urlPath = Constants.SHARE_URL + url;*/
		return getDrawableFromCacheOrNet(ctx, urlPath);
	}

	private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	public static String toHexString(byte[] b) { // String to byte
		StringBuilder sb = new StringBuilder(b.length * 2);
		for (int i = 0; i < b.length; i++) {
			sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
			sb.append(HEX_DIGITS[b[i] & 0x0f]);
		}
		return sb.toString();
	}

	public static String md5(String s) {
		try {
			// Create MD5 Hash
			MessageDigest digest = java.security.MessageDigest
					.getInstance("MD5");
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();

			return toHexString(messageDigest);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return "";
	}

	private static Drawable getDrawableFromCacheOrNet(Context ctx,
			String urlPath) throws FileNotFoundException {
		Uri uri = null;
		File cacheFile = ctx.getCacheDir();
		File file = new File(cacheFile, md5(urlPath));
		try {
			if (file.exists()) {
				hitCount++;
				if ((hitCount % 100) == 0)
					Log.d("MyTag", "HitCount = " + hitCount);
				uri = Uri.fromFile(file);
			} else {
				Log.d("MyTag", "Down url = " + urlPath);
				FileOutputStream outStream = new FileOutputStream(file);
				HttpURLConnection conn = (HttpURLConnection) new URL(urlPath)
						.openConnection();
				conn.setConnectTimeout(10 * 1000);
				conn.setRequestMethod("GET");
				int status = conn.getResponseCode();
				if (status == 200) {
					InputStream inStream = conn.getInputStream();
					byte[] buffer = new byte[1024];
					int len = 0;
					while ((len = inStream.read(buffer)) != -1) {
						outStream.write(buffer, 0, len);
					}
					outStream.close();
					inStream.close();
					uri = Uri.fromFile(file);
				} else
					return null;
			}

		} catch (Exception ex) {
			Log.d("MyTag", "getDrawableFromCache error = " + ex.getMessage());
			ex.printStackTrace();
		} finally {
			if (file.length() < 10) {
				file.delete();
				return null;
			}
		}
		return Drawable.createFromStream(ctx.getContentResolver()
				.openInputStream(uri), null);
	}

	public static void clearLargeCache(Context ctx) throws IOException {
		File cacheFile = ctx.getCacheDir();
		File[] files = cacheFile.listFiles();
		if (dirSize(files) >= 6 * 1024 * 1024) {
			Arrays.sort(files, new Comparator<File>() {
				@Override
				public int compare(File object1, File object2) {
					File file1 = (File) object1;
					File file2 = (File) object2;
					long diff = file1.lastModified() - file2.lastModified();
					if (diff > 0)
						return 1;
					else if (diff == 0)
						return 0;
					else
						return -1;
				}
			});

			int count = files.length / 2;
			for (int i = count; i >= 0; i--) {
				if (!files[i].isDirectory())
					files[i].delete();
			}
		}
	}

	/* Return the size of a directory in bytes */
	private static long dirSize(File[] fileList) {
		long result = 0;
		for (int i = 0; i < fileList.length; i++) {
			if (!fileList[i].isDirectory()) {
				// Sum the file size in bytes
				result += fileList[i].length();
			}
		}
		Log.d("MyTag", "Cache size(K) =" + result / 1024);
		return result;
	}

	// 检测网络状态
	// 0:无网络连接 1:WIFI 2:GPRG 3:3G或其它网络
	public static int getNetworkState(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null)
			return 0;
		else {
			NetworkInfo info = connectivity.getActiveNetworkInfo();
			if (info == null || !info.isAvailable())
				return 0;
			else if (info.getTypeName().toUpperCase().equals("WIFI"))
				return 1;
			else if (info.getTypeName().toUpperCase().equals("GPRS"))
				return 2;
			else
				return 3;
		}
	}

	public static boolean isEmpty(EditText... editTextes) {
		for (EditText et : editTextes) {
			if (et.getText() == null)
				return true;
			if (et.getText().toString().trim().equals(""))
				return true;
		}
		return false;
	}

	/* 时间转换 */
	public static String transferTimeFormat(final Context ctx, String oldTime)
			throws ParseException {
		String newTime = "";
		if (oldTime == null || oldTime.length() == 0)
			return newTime;
		SimpleDateFormat aa = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar time = Calendar.getInstance();
		time.setTime(aa.parse(oldTime));

		Calendar curr = Calendar.getInstance();
		long timeGapInMin = (curr.getTimeInMillis() - time.getTimeInMillis()) / 60000;

		if (timeGapInMin <= 0) {
			newTime = ctx.getString(R.string.time_now);
		} else if (timeGapInMin > 0 && timeGapInMin < 60) {
			int min = (int) (timeGapInMin / 1);
			newTime = Integer.toString(min) + ctx.getString(R.string.time_min);
		} else if (timeGapInMin >= 60 && timeGapInMin <= 1440) {
			int hour = (int) (timeGapInMin / 60);
			newTime = Integer.toString(hour)
					+ ctx.getString(R.string.time_hour);
		} else {
			int day = (int) (timeGapInMin / (60 * 24));
			newTime = Integer.toString(day) + ctx.getString(R.string.time_day);
		}
		return newTime;
	}

	public static Bitmap drawableToBitmap(Drawable drawable) {

		Bitmap bitmap = Bitmap
				.createBitmap(
						drawable.getIntrinsicWidth(),
						drawable.getIntrinsicHeight(),
						drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
								: Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight());
		drawable.draw(canvas);
		return bitmap;
	}

	// 验证固定电话
	public static boolean isPhone(String strPhone) {
		String phoneRegexp = "^\\d{3,4}\\d{7,8}$";// 固话的匹配模式
		Pattern p = Pattern.compile(phoneRegexp);
		Matcher m = p.matcher(strPhone);
		return m.matches();
	}

	// 验证邮编
	public static boolean isPostCode(String strPostCode) {
		String postCodeRegexp = "^[0-9]{6}$"; // 邮政编码的匹配模式
		Pattern p = Pattern.compile(postCodeRegexp);
		Matcher m = p.matcher(strPostCode);
		return m.matches();
	}

	// 验证用户名
	public static boolean isUserName(String strName) {
		Pattern p = Pattern.compile("^\\w{6,12}$");
		Matcher m = p.matcher(strName);
		return m.matches();
	}

	// 验证密码
	public static boolean isPassword(String strPwd) {
		Pattern p = Pattern.compile("^\\w{6,12}$");
		Matcher m = p.matcher(strPwd);
		return m.matches();
	}

	// 验证email
	public static boolean isEmail(String strEmail) {
		Pattern p = Pattern
				.compile("^[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9].[a-zA-Z]{2,3}$");
		Matcher m = p.matcher(strEmail);
		return m.matches();
	}

	// 验证手机号码
	public static boolean isMobile(String strMobile) {
		Pattern p = Pattern.compile("^1\\d{10}$");
		Matcher m = p.matcher(strMobile);
		return m.matches();
	}

	// 验证注册用户名
	public static boolean isRegisterUserName(String strUserName) {
		Pattern p = Pattern.compile("^[\\u4e00-\\u9fa5_a-zA-Z0-9\\-]+$");
		Matcher m = p.matcher(strUserName);
		return m.matches();
	}

	public static void alert(Activity activity, String msg) {
		try {
			new AlertDialog.Builder(activity).setTitle("提示").setMessage(msg)
					.setPositiveButton("确定", null).create().show();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void alert(Activity activity, String title, String msg) {
		try {
			new AlertDialog.Builder(activity).setTitle(title).setMessage(msg)
					.setPositiveButton("确定", null).create().show();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}


	public static void shareBySms(final Activity activity, String shareContent) {
		Uri uri = Uri.parse("smsto:");
		Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
		intent.putExtra("sms_body", shareContent);
		activity.startActivity(intent);
	}

	public static void shareByEmail(final Activity activity, String shareContent) {
		Intent it = new Intent(Intent.ACTION_SEND);
		it.putExtra(Intent.EXTRA_EMAIL, "");
		// it.putExtra(Intent.EXTRA_TEXT,
		// activity.getResources().getString(R.string.share_content));
		it.putExtra(Intent.EXTRA_TEXT, shareContent);
		it.setType("text/plain");
		String prompt = activity.getResources().getString(
				R.string.please_choose_email);
		activity.startActivity(Intent.createChooser(it, prompt));
	}



	public static String getPackageVersion(Context ctx, final String pkgName) {

		String version = null;
		try {
			PackageManager pm = ctx.getPackageManager();
			PackageInfo info = pm.getPackageInfo(pkgName,
					PackageManager.GET_ACTIVITIES);
			version = info.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return version;
	}

	// 修改购物车显示数量
	public static void modifyCarNumber(Activity activity,
			ImageView cartImageview) {
	}

	// 放大缩小图片
	public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scaleWidth = ((float) w) / width;
		float scaleHeight = ((float) h) / height;
		// matrix.setScale(scaleWidth,scaleHeight);
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap newBmp = Bitmap.createBitmap(bitmap, 0, 0, width, height,
				matrix, true);
		return newBmp;
	}

	// 获取所有adapter高度
	public static void setListViewHeightBasedOnChildren(ListView listView) {
		// 获取ListView对应的Adapter
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}

		int totalHeight = 0;
		for (int i = 0, len = listAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0); // 计算子项View 的宽高
			totalHeight += listItem.getMeasuredHeight() + 6; // 统计所有子项的总高度
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		// listView.getDividerHeight()获取子项间分隔符占用的高度
		// params.height最后得到整个ListView完整显示需要的高度
		listView.setLayoutParams(params);
	}
	
	/*
	 * 获取手机IMEI
	 */
	public static String getIME(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String imei = md5(tm.getDeviceId());
		return imei;
	}
	/**
	 * 计算两个坐标之间的距离
	 * @param lat_a
	 * @param lng_a
	 * @param lat_b
	 * @param lng_b
	 * @return
	 */
	public static double getDistanceFromXtoY(double lat_a, double lng_a,


			double lat_b, double lng_b)
			{
			double pk = (double) (180 / 3.14169);

			double a1 = lat_a / pk;
			double a2 = lng_a / pk;
			double b1 = lat_b / pk;
			double b2 = lng_b / pk;

			double t1 = Math.cos(a1) * Math.cos(a2) * Math.cos(b1) * Math.cos(b2);
			double t2 = Math.cos(a1) * Math.sin(a2) * Math.cos(b1) * Math.sin(b2);
			double t3 = Math.sin(a1) * Math.sin(b1);
			double tt = Math.acos(t1 + t2 + t3);

			return 6366000 * tt;
			 }
}
