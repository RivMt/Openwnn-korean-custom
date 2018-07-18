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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.blog.hgl1002.openwnn.event.AutoConvertEvent;
import me.blog.hgl1002.openwnn.event.DisplayCandidatesEvent;

public class KoreanT9Converter extends SQLiteOpenHelper implements WordConverter {

	public static final String DATABASE_NAME = "naratgeul.db";
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

	private static final Map<Character, Integer> KEY_MAP = new HashMap<Character, Integer>() {{
		put('1', -2001);
		put('2', -2002);
		put('3', -2003);
		put('4', -2004);
		put('5', -2005);
		put('6', -2006);
		put('7', -2007);
		put('8', -2008);
		put('9', -2009);
		put('*', -2011);
		put('0', -2010);
		put('#', -2012);
	}};

	private TwelveHangulEngine hangulEngine;

	private KoreanT9ConvertTask task;

	public KoreanT9Converter(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		hangulEngine = new TwelveHangulEngine();
		hangulEngine.setJamoTable(Layout12KeyDubul.CYCLE_DUBUL_12KEY_NARATGEUL);
		hangulEngine.setAddStrokeTable(Layout12KeyDubul.STROKE_DUBUL_12KEY_NARATGEUL);
		hangulEngine.setCombinationTable(Layout12KeyDubul.COMB_DUBUL_12KEY_NARATGEUL);
		hangulEngine.setMoachigi(false);
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
		task = new KoreanT9ConvertTask(getReadableDatabase(), word, hangulEngine);
		task.execute();
	}

	static class KoreanT9ConvertTask extends AsyncTask<Void, Integer, Integer> implements HangulEngine.HangulEngineListener {

		private SQLiteDatabase database;
		private ComposingWord word;
		private List<String> result = new ArrayList<>();
		private HangulEngine hangulEngine;

		private String composing;
		private StringBuilder composingWord;

		public KoreanT9ConvertTask(SQLiteDatabase database, ComposingWord word, HangulEngine hangulEngine) {
			this.database = database;
			this.word = word;
			this.composing = "";
			this.composingWord = new StringBuilder();
			this.hangulEngine = hangulEngine;
			hangulEngine.resetComposition();
			hangulEngine.setListener(this);
		}

		@Override
		public void onEvent(HangulEngine.HangulEngineEvent event) {
			if(event instanceof HangulEngine.SetComposingEvent) {
				composing = ((HangulEngine.SetComposingEvent) event).getComposing();
			} else if(event instanceof HangulEngine.FinishComposingEvent) {
				composingWord.append(composing);
				composing = "";
			}
		}

		@Override
		protected Integer doInBackground(Void... params) {
			StringBuilder sb = new StringBuilder();
			sb.append(" select * from `words` ");
			sb.append(" where keys = ? ");

			Cursor cursor = database.rawQuery(sb.toString(),
					new String[] {
							word.getEntireWord()
					}
			);

			if(isCancelled()) return null;

			publishProgress(10);

			int column = cursor.getColumnIndex("word");
			int count = cursor.getCount();

			while(cursor.moveToNext()) {
				if(isCancelled()) return null;
				result.add(cursor.getString(column));
				publishProgress(10 + 90 / count);
			}
			cursor.close();

			for(char ch : word.getEntireWord().toCharArray()) {
				if(isCancelled()) return null;
				Integer code = KEY_MAP.get(ch);
				if(code != null) {
					int jamo = hangulEngine.inputCode(code, 0);
					if(jamo != -1) hangulEngine.inputJamo(jamo);
				} else {
					hangulEngine.resetComposition();
					composingWord.append(ch);
				}
			}
			String word = composingWord + composing;
			if(!result.contains(word)) result.add(word);

			return 1;
		}

		@Override
		protected void onPostExecute(Integer integer) {
			super.onPostExecute(integer);
			if(integer == 1 && !result.isEmpty()) {
				EventBus.getDefault().post(new DisplayCandidatesEvent(result));
				EventBus.getDefault().post(new AutoConvertEvent(result.get(0)));
			}
		}
	}

}
