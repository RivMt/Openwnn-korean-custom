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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import me.blog.hgl1002.openwnn.KOKR.trie.TrieDictionary;
import me.blog.hgl1002.openwnn.event.AutoConvertEvent;
import me.blog.hgl1002.openwnn.event.DisplayCandidatesEvent;

public class T9Converter implements WordConverter {

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

	private List<Character> consonantList = new ArrayList<>();
	private List<Character> vowelList = new ArrayList<>();

	private TwelveHangulEngine hangulEngine;

	private String tableName, columnName;

	private KoreanT9ConvertTask task;
	private SQLiteDatabase database;

	private TrieDictionary dictionary;

	public T9Converter(Context context, EngineMode engineMode) {
		hangulEngine = new TwelveHangulEngine();
		hangulEngine.setJamoTable(engineMode.layout);
		hangulEngine.setAddStrokeTable(engineMode.addStroke);
		hangulEngine.setCombinationTable(engineMode.combination);
		tableName = engineMode.getPrefValues()[0];
		columnName = "keys";
		hangulEngine.setMoachigi(false);
		this.database = T9DatabaseHelper.getInstance().getReadableDatabase();

		dictionary = new TrieDictionary(engineMode);

		for(int[] item : engineMode.layout) {
			char sourceChar = ' ';
			if(item[0] <= -2000 && item[0] > -2100) {
				sourceChar = (char) (-item[0] - 2000 + 0x30);
			} else if(item[0] <= -200 && item[0] > -300) {
				sourceChar = (char) (-item[0] - 200 + 0x30);
			} else continue;
			if(sourceChar == 0x30 + 10) sourceChar = '0';
			if(sourceChar == 0x30 + 11) sourceChar = '*';
			if(sourceChar == 0x30 + 12) sourceChar = '#';
			char jamo = (char) item[1];
			if(jamo >= 'ㄱ' && jamo <= 'ㅎ') {
				consonantList.add(sourceChar);
			} else if(jamo >= 'ㅏ' && jamo <= 'ㅣ') {
				vowelList.add(sourceChar);
			} else if(jamo == '\u318d') {			// 아래아
				vowelList.add(sourceChar);
			}
		}
	}

	public void generate(InputStream words, EngineMode mode) {
		BufferedReader br = new BufferedReader(new InputStreamReader(words));
		String line;
		try {
			int i = Integer.MAX_VALUE;
			while((line = br.readLine()) != null) {
				dictionary.insert(line, i--);
			}
		} catch(IOException ex) {
			ex.printStackTrace();
		}
	}

	public void generate(InputStream words, InputStream trails, EngineMode mode) {
		BufferedReader br = new BufferedReader(new InputStreamReader(words));
		String line;
		try {
			int i = Integer.MAX_VALUE;
			while((line = br.readLine()) != null) {
				dictionary.insert(line, i--);
			}
		} catch(IOException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void convert(ComposingWord word) {
		if(word.getEntireWord() == null) return;
		if(task != null) {
			task.cancel(true);
		}
		hangulEngine.resetCycle();
//		task = new KoreanT9ConvertTask(this, word, tableName, columnName);
//		task.execute();
		List<TrieDictionary.Word> words = dictionary.searchStroke(word.getEntireWord());
		List<String> result = new LinkedList<>();
		for(TrieDictionary.Word w : words) {
			result.add(w.getWord());
		}

		EventBus.getDefault().post(new DisplayCandidatesEvent(result));
		if(result.size() > 0) EventBus.getDefault().post(new AutoConvertEvent(result.get(0)));

	}

	static class KoreanT9ConvertTask extends AsyncTask<Void, Integer, Integer> implements HangulEngine.HangulEngineListener {

		private T9Converter converter;

		private ComposingWord word;
		private HangulEngine hangulEngine;

		private List<String> result = new ArrayList<>();

		private String tableName;
		private String columnName;

		private String composing;
		private StringBuilder composingWord;

		KoreanT9ConvertTask(T9Converter converter, ComposingWord word, String tableName, String columnName) {
			this.converter = converter;
			this.word = word;
			this.composing = "";
			this.composingWord = new StringBuilder();
			this.hangulEngine = converter.hangulEngine;
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
			String word = this.word.getEntireWord();
			if(T9DatabaseHelper.getInstance().hasTable(tableName + TRAILS)) {
				List<String> trailSource = new ArrayList<>();
				char cho, jung, jong;
				cho = jung = jong = '\0';
				for(int i = 0 ; i <= 4 ; i++) {
					if(isCancelled()) return null;
					if(word.length() <= i) break;
					char ch = word.charAt(word.length()-i-1);
					if(converter.vowelList.contains(ch)) {
						if(cho != '\0') {
							trailSource.add(new String(jong == '\0' ? new char[] {cho, jung} : new char[] {cho, jung, jong}));
							cho = jong = '\0';
							jung = ch;
						}
						else jung = ch;
					} else if(converter.consonantList.contains(ch)) {
						if(cho != '\0') {
							trailSource.add(new String(jong == '\0' ? new char[] {cho, jung} : new char[] {cho, jung, jong}));
							cho = jung = '\0';
							jong = ch;
						}
						else if(jung != '\0') cho = ch;
						else jong = ch;
					}
				}
				StringBuilder trail = new StringBuilder();
				for(String str : trailSource) {
					trail.insert(0, str);
					List<String> trails = getTrails(trail.toString());
					if(trails != null) {
						if(isCancelled()) return null;
						String search = word.substring(0, word.length()-trail.length());
						List<String> words = getWords(search, 4);
						for(String tr : trails) {
							for(String w : words) {
								result.add(w + tr);
							}
						}
					}
				}
			}

			if(isCancelled()) return null;

			result.addAll(getWords(word, 0));

			for(char ch : word.toCharArray()) {
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
			String w = composingWord + composing;
			if(!result.contains(w)) result.add(w);

			return 1;
		}

		private List<String> getWords(String search, int limit) {
			List<String> result = new ArrayList<>();

			StringBuilder sb = new StringBuilder();
			sb.append(" select * from `" + tableName + "` ");
			sb.append(" where `" + columnName + "` = ? ");
			if(limit > 0) sb.append(" limit " + limit + " ");

			Cursor cursor = converter.database.rawQuery(sb.toString(),
					new String[] {
							search
					}
			);

			if(cursor.getCount() == 0) return result;

			int column = cursor.getColumnIndex("word");
			while(cursor.moveToNext()) {
				result.add(cursor.getString(column));
			}
			cursor.close();

			return result;
		}

		private List<String> getTrails(String search) {
			List<String> result = new ArrayList<>();

			StringBuilder sb = new StringBuilder();
			sb.append(" select * from `" + tableName + TRAILS + "` ");
			sb.append(" where `" + columnName + "` = ? ");
			sb.append(" limit 3 ");

			Cursor cursor = converter.database.rawQuery(sb.toString(),
					new String[] {
							search
					}
			);

			if(cursor.getCount() == 0) return null;

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
