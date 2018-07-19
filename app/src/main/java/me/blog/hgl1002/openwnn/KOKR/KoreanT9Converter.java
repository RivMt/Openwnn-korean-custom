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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.blog.hgl1002.openwnn.event.AutoConvertEvent;
import me.blog.hgl1002.openwnn.event.DisplayCandidatesEvent;

public class KoreanT9Converter implements WordConverter {

	private static final String TRAILS = "_trails";

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

	private String tableName, columnName;

	private KoreanT9ConvertTask task;

	public KoreanT9Converter(Context context, EngineMode engineMode) {
		hangulEngine = new TwelveHangulEngine();
		hangulEngine.setJamoTable(engineMode.layout);
		hangulEngine.setAddStrokeTable(engineMode.addStroke);
		hangulEngine.setCombinationTable(engineMode.combination);
		tableName = engineMode.getPrefValues()[0];
		columnName = "keys";
		hangulEngine.setMoachigi(false);
	}

	public void generate(InputStream words, EngineMode mode) {
		T9DictionaryGenerator.generate(words, T9DatabaseHelper.getInstance().getWritableDatabase(), "", mode);
	}

	public void generate(InputStream words, InputStream trails, EngineMode mode) {
		T9DictionaryGenerator.generate(words, T9DatabaseHelper.getInstance().getWritableDatabase(), "", mode);
		T9DictionaryGenerator.generate(trails, T9DatabaseHelper.getInstance().getWritableDatabase(), TRAILS, mode);
	}

	@Override
	public void convert(ComposingWord word) {
		if(word.getEntireWord() == null) return;
		if(task != null) {
			task.cancel(true);
		}
		hangulEngine.resetCycle();
		task = new KoreanT9ConvertTask(T9DatabaseHelper.getInstance().getReadableDatabase(), word, hangulEngine, tableName, columnName);
		task.execute();
	}

	static class KoreanT9ConvertTask extends AsyncTask<Void, Integer, Integer> implements HangulEngine.HangulEngineListener {

		private SQLiteDatabase database;
		private ComposingWord word;
		private List<String> result = new ArrayList<>();
		private HangulEngine hangulEngine;

		private String tableName;
		private String columnName;

		private String composing;
		private StringBuilder composingWord;

		public KoreanT9ConvertTask(SQLiteDatabase database, ComposingWord word, HangulEngine hangulEngine, String tableName, String columnName) {
			this.database = database;
			this.word = word;
			this.composing = "";
			this.composingWord = new StringBuilder();
			this.hangulEngine = hangulEngine;
			this.tableName = tableName;
			this.columnName = columnName;
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
			if(T9DatabaseHelper.getInstance().hasTable(tableName + TRAILS)) {
				for(int i = 4 ; i >= 2; i--) {
					List<String> trails = getTrails(i);
					if(trails != null) {
						String search = word.getEntireWord();
						search = search.substring(0, search.length()-i);
						List<String> words = getWords(search, 3);
						for(String trail : trails) {
							for(String word : words) {
								result.add(word + trail);
							}
						}
					}
				}
			}

			if(isCancelled()) return null;

			result.addAll(getWords(word.getEntireWord(), 0));

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

		private List<String> getWords(String search, int limit) {
			List<String> result = new ArrayList<>();

			StringBuilder sb = new StringBuilder();
			sb.append(" select * from `" + tableName + "` ");
			sb.append(" where `" + columnName + "` = ? ");
			if(limit > 0) sb.append(" limit " + limit + " ");

			Cursor cursor = database.rawQuery(sb.toString(),
					new String[] {
							search
					}
			);

			int column = cursor.getColumnIndex("word");
			while(cursor.moveToNext()) {
				result.add(cursor.getString(column));
			}
			cursor.close();

			return result;
		}

		private List<String> getTrails(int length) {
			List<String> result = new ArrayList<>();

			StringBuilder sb = new StringBuilder();
			sb.append(" select * from `" + tableName + TRAILS + "` ");
			sb.append(" where `" + columnName + "` = ? ");
			sb.append(" limit 3 ");

			String search;
			try {
				search = word.getEntireWord();
				search = search.substring(search.length() - length);
			} catch(StringIndexOutOfBoundsException ex) {
				return null;
			}

			Cursor cursor = database.rawQuery(sb.toString(),
					new String[] {
							search
					}
			);

			int column = cursor.getColumnIndex("word");
			if(cursor.moveToNext()) {
				result.add(cursor.getString(column));
			}
			cursor.close();
			return  result;
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
