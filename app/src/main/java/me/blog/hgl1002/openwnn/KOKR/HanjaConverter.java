package me.blog.hgl1002.openwnn.KOKR;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.blog.hgl1002.openwnn.event.DisplayCandidatesEvent;

public class HanjaConverter extends SQLiteOpenHelper implements WordConverter {

	public static final String DATABASE_NAME = "hanja.db";
	public static final int DATABASE_VERSION = 1;

	private Context context;

	private HanjaConvertTask task;

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
		if(word.getEntireWord() == null) return null;
		if(task != null) {
			task.cancel(true);
		}
		task = new HanjaConvertTask(getReadableDatabase(), word);
		task.doInBackground();
		return null;
	}

	static class HanjaConvertTask extends AsyncTask<Void, Integer, Integer> {

		private SQLiteDatabase database;
		private ComposingWord word;
		private List<String> result = new ArrayList<>();

		public HanjaConvertTask(SQLiteDatabase database, ComposingWord word) {
			this.database = database;
			this.word = word;
		}

		@Override
		protected Integer doInBackground(Void... params) {
			StringBuilder sb = new StringBuilder();
			sb.append(" select * from `hanja` ");
			sb.append(" where reading = ? ");

			Cursor cursor = database.rawQuery(sb.toString(),
					new String[] {
							word.getEntireWord()
					}
			);

			if(isCancelled()) return null;

			publishProgress(10);

			int column = cursor.getColumnIndex("hanja");
			int count = cursor.getCount();
			while(cursor.moveToNext()) {
				if(isCancelled()) return null;
				result.add(cursor.getString(column));
				publishProgress(10 + 90 / count);
			}
			cursor.close();

			EventBus.getDefault().post(new DisplayCandidatesEvent(result));

			return 1;
		}

		@Override
		protected void onPostExecute(Integer integer) {
			super.onPostExecute(integer);
		}
	}

}
