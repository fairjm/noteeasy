package org.cc.android.noteeasy.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class MyOpenHelper extends SQLiteOpenHelper {

	private static final String NAME="MYNOTE";    //使用DB1的数据库
	private static final int VERSION=1;
	private static final String TABLE="NOTES";
	public MyOpenHelper(Context context) {
		super(context, NAME, null, VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table "+TABLE+" (" +
				"_id integer primary key,"+
				"content text,"+
				"cdate text)");
		System.out.println("onCreate()执行");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String sql = "DROP TABLE IF EXISTS " + TABLE ;
		db.execSQL(sql) ;
		System.out.println("****************** 更新：onUpgrade()。");
		this.onCreate(db) ;

	}

}
