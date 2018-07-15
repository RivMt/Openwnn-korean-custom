package me.blog.hgl1002.openwnn.KOKR;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HanjaConverter extends SQLiteOpenHelper implements WordConverter {

	private Context context;

	public static final String DATABASE_NAME = "hanja.db";
	public static final int DATABASE_VERSION = 1;

	public static void copyDatabase(Context context) {
		File file = new File(new File(context.getFilesDir().getParentFile(), "databases"), DATABASE_NAME);
		if(!file.exists()) {
			AssetManager assetManager = context.getAssets();
			try {
				BufferedInputStream bis = new BufferedInputStream(assetManager.open("db/" + DATABASE_NAME));
				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
				int read = -1;
				byte[] buffer = new byte[1024];
				while((read = bis.read(buffer, 0, 1024)) != -1) {
					bos.write(buffer, 0, read);
				}
				bos.flush();

				bos.close();
				bis.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}

	public HanjaConverter(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

	}

	@Override
	public List<String> convert(ComposingWord word) {
		SQLiteDatabase db = getReadableDatabase();

		StringBuilder sb = new StringBuilder();
		sb.append(" select * from `hanja` ");
		sb.append(" where reading = ? ");

		Cursor cursor = db.rawQuery(sb.toString(),
				new String[] {
						word.getEntireWord()
				}
		);

		List<String> result = new ArrayList<>();
		int column = cursor.getColumnIndex("hanja");
		while(cursor.moveToNext()) {
			result.add(cursor.getString(column));
		}
		cursor.close();

		return result;
	}

}
