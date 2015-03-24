package com.steelmantools.smartear.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.steelmantools.smartear.db.DataBaseHelper;

public class DBDeviceAdapter 
{
	static final String DATABASE_NAME = "devices.db";
	static final int DATABASE_VERSION = 1;
	public static final int NAME_COLUMN = 1;
	static final String DATABASE_CREATE = "create table "+"DEVICES"+
	                             "( " +"_id"+" integer primary key autoincrement,"+ "ADDRESS text); ";
	public static  SQLiteDatabase db;
	private final Context context;
	private DataBaseHelper dbHelper;
	public  DBDeviceAdapter(Context _context) 
	{
		String c = "create table "+"DEVICES"+
                "( " +"_id"+" integer primary key autoincrement,"+ "ADDRESS text); ";
		context = _context;
		dbHelper = new DataBaseHelper(context, DATABASE_NAME, null, DATABASE_VERSION, c);
	}
	public  DBDeviceAdapter open() throws SQLException 
	{
		db = dbHelper.getWritableDatabase();
		return this;
	}
	public static void close() 
	{
		db.close();
	}
	public  SQLiteDatabase getDatabaseInstance()
	{
		return db;
	}
	public void insertEntry(String address)
	{
       ContentValues newValues = new ContentValues();
        newValues.put("ADDRESS",address);
		
		db.insert("DEVICES", null, newValues);
		//Toast.makeText(context, "DBADAPTER INSERT ENTRY FIRED" + address + channel + channelassign, Toast.LENGTH_LONG).show();
	}
	public void deleteDatabase()
	{
		context.deleteDatabase(DATABASE_NAME);
	}	
	public void deleteAllRowsButOne()
	{
	    String idField = "1";
	    db.delete("BLUETOOTH", "ID != ?",
                new String[] { String.valueOf(idField) });
	}	
	public long getSinlgeEntry()
	{	
		Cursor mCursor = db.rawQuery("Select * from  BLUETOOTH", null);
		mCursor.moveToLast();
		long lastID = mCursor.getLong(0);
		return lastID;
	}
	
	public Cursor getAllRows()
	{
		Cursor cursor=db.rawQuery("Select * from  BLUETOOTH", null);
		
		if (cursor != null) 
		{
		cursor.moveToFirst();
		}
		return cursor;	
	}
	
	public boolean getSinlgeEntryCheck()
	{
		Cursor mCursor = db.rawQuery("Select * from  BLUETOOTH", null);
		Boolean rowExists;
		if (mCursor.moveToFirst())
		{
		   // DO SOMETHING WITH CURSOR
		  rowExists = true;
		} else {
		   // I AM EMPTY
		   rowExists = false;
		}
		return rowExists;
	}

	public boolean getDatabaseCheck()
	{
		Cursor mCursor = db.rawQuery("Select * from  BLUETOOTH", null);
		Boolean rowExists;
		if(mCursor != null && mCursor.getCount()>0){
		}
		if (mCursor.moveToFirst())
		{
		   // DO SOMETHING WITH CURSOR
		  rowExists = true;
		} else {
		   // I AM EMPTY
		   rowExists = false;
		}
		return rowExists;
	}
	
	public String getDatabaseCheckString()
	{
		Cursor mCursor = db.rawQuery("Select * from  DEVICES", null);
		String rowExists;
		if(mCursor != null && mCursor.getCount()>0){
			mCursor.moveToFirst();
			rowExists = "TRUE";
		} else{
			rowExists = "FALSE";
		}
		return rowExists;
	}
	
	public String areThereRows(){
		Cursor mCursor = db.rawQuery("Select * from  BLUETOOTH", null);
		mCursor.getCount();
		int theCount = mCursor.getCount();
		String rowCount = String.valueOf(theCount);
		return rowCount;
	}
	
	public String getRowCount(){
		Cursor mCursor = db.rawQuery("Select * from  BLUETOOTH", null);
		mCursor.getCount();
		int theCount = mCursor.getCount();
		String rowCount = String.valueOf(theCount);
		return rowCount;
	}
	
	public String getSinlgeEntryCheck(String checkDB)
	{
		Cursor cursor=db.query("BLUETOOTH", null, " ID=?", new String[]{checkDB}, null, null, null);
        if(cursor.getCount()<1)
        {
        	cursor.close();
        	return "NOT EXIST";
        }
	    cursor.moveToFirst();
	    String type= cursor.getString(cursor.getColumnIndex("ID"));
		cursor.close();
		return type;				
	}
	
	public String getSingleEntryAddr(String getEntryByAddr)
	{
		Cursor cursor=db.query("DEVICES", null, " ADDRESS=?", new String[]{getEntryByAddr}, null, null, null);
        if(cursor.getCount()<1)
        {
        	cursor.close();
        	return "NOT EXIST";
        }
//	    cursor.moveToFirst();
//	    String type= cursor.getString(cursor.getColumnIndex("ADDRESS"));
		cursor.close();
		return "EXIST";		
	}
	
	public String getSingleEntryAddrCheck(String getEntryByAddr)
	{
		Cursor cursor=db.query("DEVICES", null, " ADDRESS=?", new String[]{getEntryByAddr}, null, null, null);
        if(cursor.getCount()<1)
        {
        	cursor.close();
        	return "NOT EXIST";
        }
	    cursor.moveToFirst();
	    String type= cursor.getString(cursor.getColumnIndex("ADDRESS"));
		cursor.close();
		return type;		
	}
	
	public void updateEntry(String address,String channel,String channelassign)
	{
		// Define the updated row content.
		ContentValues updatedValues = new ContentValues();
		// Assign values for each row.
		updatedValues.put("ADDRESS", address);
		updatedValues.put("CHANNEL",channel);
		updatedValues.put("CHANNELASSIGN",channelassign);
		
        String where="ADDRESS = ?";
	    db.update("BLUETOOTH",updatedValues, where, new String[]{address});		
	    Log.d("updateEntry", "updateEntry address: " + (address)); 
	    Log.d("updateEntry", "updateEntry channel: " + (channel)); 
	    Log.d("updateEntry", "updateEntry channelassign: " + (channelassign)); 
	}
}
