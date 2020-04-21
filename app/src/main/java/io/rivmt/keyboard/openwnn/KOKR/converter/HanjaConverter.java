package io.rivmt.keyboard.openwnn.KOKR.converter;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Build;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.rivmt.keyboard.openwnn.KOKR.ComposingWord;
import io.rivmt.keyboard.openwnn.KOKR.EngineMode;
import io.rivmt.keyboard.openwnn.KOKR.WordConverter;
import io.rivmt.keyboard.openwnn.KOKR.trie.TrieDictionary;
import io.rivmt.keyboard.openwnn.event.DisplayCandidatesEvent;

public class HanjaConverter extends SQLiteOpenHelper implements WordConverter {

	public static final String DATABASE_NAME = "hanja.db";
	public static final int DATABASE_VERSION = 1;

	private HanjaConvertTask task;

	public static void copyDatabase(Context context) {
		File file = new File(new File(context.getFilesDir().getParentFile(), "databases"), DATABASE_NAME);
		file.getParentFile().mkdirs();
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
	}

	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

	}

	@Override
	public void convert(ComposingWord word) {
		if(word.getEntireWord() == null) return;
		if(task != null) {
			task.cancel(true);
		}
		task = new HanjaConvertTask(getReadableDatabase(), word);
		task.execute();
	}

	@Override
	public void setEngineMode(EngineMode engineMode) {

	}

	static class HanjaConvertTask extends AsyncTask<Void, Integer, Integer> {

		private SQLiteDatabase database;
		private ComposingWord word;
		private List<TrieDictionary.Word> result = new ArrayList<>();

		public HanjaConvertTask(SQLiteDatabase database, ComposingWord word) {
			this.database = database;
			this.word = word;
		}

		public void execute() {
			if(Build.VERSION.SDK_INT >= 11) {
				super.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else {
				super.execute();
			}
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
				result.add(new TrieDictionary.Word(cursor.getString(column), 1));
				publishProgress(10 + 90 / count);
			}
			cursor.close();

			return 1;
		}

		@Override
		protected void onPostExecute(Integer integer) {
			super.onPostExecute(integer);
			if(integer == 1) {
				EventBus.getDefault().post(new DisplayCandidatesEvent(result));
			}
		}
	}

}
