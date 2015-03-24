package com.steelmantools.smartear.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataBaseHelper extends SQLiteOpenHelper
{
	public String dbcreate="";
	
	DBAdapter DBAdapter;
	
	public DataBaseHelper(Context context, String name,CursorFactory factory, int version, String dbcreate) 
    {
	           super(context, name, factory, version);
	           this.dbcreate=dbcreate;
	}
	@Override
	public void onCreate(SQLiteDatabase _db) 
	{
		_db.execSQL(this.dbcreate);
			
	}
	@Override
	public void onUpgrade(SQLiteDatabase _db, int _oldVersion, int _newVersion) 
	{
			// Log the version upgrade.
			Log.w("TaskDBAdapter", "Upgrading from version " +_oldVersion + " to " +_newVersion + ", which will destroy all old data");
	
			_db.execSQL("DROP TABLE IF EXISTS " + "TEMPLATE");
			onCreate(_db);
	}
    @Override
    public synchronized void close() {

    if (DBAdapter != null)
    	com.steelmantools.smartear.db.DBAdapter.close();
    
    super.close();

    }

}
