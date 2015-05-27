package com.wuxianyingke.progerty.databases;

import java.util.ArrayList;

import com.wuxianyingke.property.remote.RemoteApi.LivingItem;
import com.wuxianyingke.property.remote.RemoteApi.LivingItemPicture;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class LivingDB extends SQLiteOpenHelper
{
	private static final String DATABASE_NAME = "canyin.db";
	private static final int DATABASE_VERSION = 1;// not used

	private static final String LIVING_TABLE = "living_table";

	public final static String YEAR = "year";
	public final static String MONTH = "month";
	public final static String CALL_IN = "call_duration_in";
	public final static String CALL_OUT = "call_duration_out";
	public final static String SMS_IN = "incomming_sms_count";
	public final static String SMS_OUT = "sent_sms_count";
	public final static String MOBILE_SIZE = "mobile_traffic";
	public final static String WIFI_SIZE = "wifi_traffic";
	
	
	public final static String LivingItemID="LivingItemID";
	public final static String LivingItemName="LivingItemName";
	public final static String address="address";
	public final static String telephone="telephone";
	public final static String categories="categories";
	public final static String distance="distance";
	public final static String avg_price="avg_price";
	public final static String has_coupon="has_coupon";
	public final static String has_deal="has_deal";
	public final static String hours="hours";
	public final static String has_activity="has_activity";
	public final static String priority="priority";
	public final static String latitude="latitude";
	public final static String longitude="longitude";
	public final static String ForExpress="ForExpress";
	public final static String source="source";
	public final static String Description="Description";
	public final static String Ctime="Ctime";
	public final static String path="path";
	public final static String flag="flag";//1:canyin;2:gouwu;3:shenghuofuwu
	
	public LivingDB(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{

		db.execSQL("CREATE TABLE IF NOT EXISTS " + LIVING_TABLE + 
				"(" + "id integer primary key autoincrement, "
				+ flag+ " integer, "
				+ LivingItemID+ " integer, "
				+ LivingItemName + " integer, " 
				+ address + " text, "
				+ telephone + " text, "
				+ categories + " text, "
				+ distance + " integer, "
				+ avg_price + " integer, " 
				+ has_coupon+ " integer, "
				+ has_deal + " integer," 
				+ hours + " text,"
				+ has_activity + " integer,"
				+ priority + " integer,"
				+ latitude + " float,"
				+ longitude + " float,"
				+ ForExpress + " integer,"
				+ source + " text,"
				+ Description + " text,"
				+ Ctime + " text,"
				+ path + " text"
				+ " );");
		

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		db.execSQL("DROP TABLE IF EXISTS " + LIVING_TABLE);
		onCreate(db);
	}

	// Application
	public int insertOneItem(LivingItem item)
	{
		int ret = -1;

		SQLiteDatabase db = this.getWritableDatabase();
        Log.d("MyTag","insertOneItem flag="+item.flag );
        Log.d("MyTag","insertOneItem distance 103="+item.distance);
		// 首先根据年月判断是否存在该记录
		String sql = "select * from " + LIVING_TABLE + " where  " + LivingItemID + "=" + item.LivingItemID + ";";
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.moveToFirst())
		{
			db.close();
			return cursor.getInt(0);
		}
		long result = db.insert(LIVING_TABLE, null, getContentCalues(item));

		ret = (int) result;
		cursor.close();
		db.close();
		return ret;
	}

	public void deleteOneApp(int id)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		String sql = "delete from " + LIVING_TABLE + " where LivingItemID=\"" + id + "\";";

		Log.d("MyTag","deleteOneApp Query sql="+sql );
		try
		{
			db.execSQL(sql);
		} catch (Exception ex)
		{
			ex.printStackTrace();
		} finally
		{
			db.close();
		}
	}
	public ArrayList<LivingItem> getAllItem(int shoucangflag)
	{
		 Log.d("MyTag","insertOneItem getAllItem(int flag)="+flag );
		SQLiteDatabase db = this.getReadableDatabase();
		String sql = "select * from " + LIVING_TABLE + " where  " + flag + "=" + shoucangflag + ";";

		Log.d("MyTag","LivingDb sql 142 ="+sql );
		Cursor c = db.rawQuery(sql, null);
		ArrayList<LivingItem> list = new ArrayList<LivingItem>();
		if (c.moveToFirst())
		{
			do
			{

				 Log.d("MyTag","insertOneItem getAllItem="+c.getInt(1) );
				 Log.d("MyTag","distance151 ="+c.getInt(7) );
				LivingItem item = new LivingItem();
				item.flag = c.getInt(1);
				item.LivingItemID = c.getInt(2);
				item.LivingItemName = c.getString(3);
				item.address = c.getString(4);
				item.telephone = c.getString(5);
				item.categories = c.getString(6);
				item.distance = c.getInt(7);
				item.avg_price = c.getInt(8);
				item.has_coupon = c.getInt(9);
				item.has_deal = c.getInt(10);
				item.priority = c.getInt(13);
				item.latitude = c.getFloat(14);
				item.longitude = c.getFloat(15);
				item.source = c.getString(17);
				item.Description = c.getString(18);
				item.Ctime = c.getString(19);
				item.path = c.getString(20);
				LivingItemPicture ft=new LivingItemPicture();
				ft.path=item.path;
				item.FrontCover=ft;
				list.add(item);
				
			} while (c.moveToNext());
		}
		c.close();
		db.close();
		return list;
	}
	// Application
		public int getOneItem(int mLivingItemID)
		{
			int ret = -1;

			SQLiteDatabase db = this.getWritableDatabase();
	        Log.d("MyTag","getOneItem mLivingItemID="+mLivingItemID );
			// 首先根据年月判断是否存在该记录
			String sql = "select * from " + LIVING_TABLE + " where  " + LivingItemID + "=" + mLivingItemID + ";";
			Cursor cursor = db.rawQuery(sql, null);
			if (cursor.moveToFirst())
			{
				db.close();
				return cursor.getInt(0);
			}
			cursor.close();
			db.close();
			return ret;
		}

	/*public int updateOneItemByMonth(LivingItem item)
	{
		int ret = -1;

		SQLiteDatabase db = this.getWritableDatabase();

		// 首先根据年月判断是否存在该记录
		String sql = "select * from " + MONTH_TABLE + " where " + YEAR + "=" + item.year + " and " + MONTH + "="
				+ item.month + ";";
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.moveToFirst())
		{
			int id = cursor.getInt(0);

			// 记录存在，更新该记录
			db.beginTransaction();

			ContentValues cv = new ContentValues();
			cv.put(CALL_IN, item.call_in);
			cv.put(CALL_OUT, item.call_out);
			cv.put(SMS_IN, item.sms_in);
			cv.put(SMS_OUT, item.sms_out);
			cv.put(MOBILE_SIZE, item.mobile_size);
			cv.put(WIFI_SIZE, item.wifi_size);
			
			String where = "id =?";
			String[] whereValue = { Integer.toString(id) };
			int columns = db.update(MONTH_TABLE, cv, where, whereValue);
			db.setTransactionSuccessful();

			db.endTransaction();

			ret = id;
		}
		else
		{
			// 记录不存在，添加记录
			ContentValues cv = new ContentValues();
			cv.put(YEAR, item.year);
			cv.put(MONTH, item.month);
			cv.put(CALL_IN, item.call_in);
			cv.put(CALL_OUT, item.call_out);
			cv.put(SMS_IN, item.sms_in);
			cv.put(SMS_OUT, item.sms_out);
			cv.put(MOBILE_SIZE, item.mobile_size);
			cv.put(WIFI_SIZE, item.wifi_size);
			long result = db.insert(MONTH_TABLE, null, cv);
			ret = (int) result;
		}
		cursor.close();
		db.close();
		return ret;
	}*/

	/*public MonthLogItem getOneItemByMonth(MonthLogItem item)
	{
		MonthLogItem ret = new MonthLogItem();
		ret.id = -1;
		
		SQLiteDatabase db = this.getWritableDatabase();

		// 首先根据年月判断是否存在该记录
		String sql = "select * from " + MONTH_TABLE + " where " + YEAR + "=" + item.year + " and " + MONTH + "="
				+ item.month + ";";
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.moveToFirst())
		{
			ret.id = cursor.getInt(0);
			ret.year = cursor.getInt(1);
			ret.month = cursor.getInt(2);
			ret.call_in = cursor.getInt(3);
			ret.call_out = cursor.getInt(4);
			ret.sms_in = cursor.getInt(5);
			ret.sms_out = cursor.getInt(6);
			ret.mobile_size = cursor.getInt(7);
			ret.wifi_size = cursor.getInt(8);
		}
		cursor.close();
		db.close();
		return ret;
	}
	*/
	
	
	public ContentValues getContentCalues(LivingItem item) {
		ContentValues cv = new ContentValues();
		cv.put(flag, item.flag);
		cv.put(LivingItemID, item.LivingItemID);
		cv.put(LivingItemName, item.LivingItemName);
		cv.put(address, item.address);
		cv.put(telephone, item.telephone);
		cv.put(categories, item.categories);
		cv.put(distance, item.distance);
		cv.put(avg_price, item.avg_price);
		cv.put(has_coupon, item.has_coupon);
		cv.put(has_deal, item.has_deal);
		cv.put(priority, item.priority);
		cv.put(latitude, item.latitude);
		cv.put(longitude, item.longitude);
		cv.put(ForExpress, item.ForExpress);
		cv.put(source, item.source);
		cv.put(Description, item.Description);
		cv.put(Ctime, item.Ctime);
		cv.put(path, item.FrontCover.path);

		return cv;
	}

}
