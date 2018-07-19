package me.blog.hgl1002.openwnn.KOKR;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class T9DatabaseHelper extends SQLiteOpenHelper {

	private static T9DatabaseHelper INSTANCE;
	public static T9DatabaseHelper getInstance() {
		return INSTANCE;
	}

	public static final String DATABASE_NAME = "T9.db";
	public static final int DATABASE_VERSION = 1;

	public T9DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		INSTANCE = this;
	}

	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

	}

	public boolean hasTable(String tableName) {
		SQLiteDatabase database = getReadableDatabase();
		String sql = "select * from `" + tableName + "` limit 1";
		try {
			Cursor cursor = database.rawQuery(sql, new String[] {});
			if(cursor.moveToNext()) {
				cursor.close();
				return true;
			}
			cursor.close();
		} catch(SQLiteException notFound) {
		}
		return false;
	}

	public boolean hasDictionary(EngineMode mode) {
		return hasTable(mode.getPrefValues()[0]);
	}

	public void deleteDictionary(EngineMode mode) {
		SQLiteDatabase database = getWritableDatabase();
		String sql = "drop table if exists `" + mode.getPrefValues()[0] + "`";
		database.execSQL(sql);
	}

}
