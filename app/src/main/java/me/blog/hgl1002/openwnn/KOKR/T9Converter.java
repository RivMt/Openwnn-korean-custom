package me.blog.hgl1002.openwnn.KOKR;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
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
	private TrieDictionary trailsDictionary;

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
		trailsDictionary = new TrieDictionary(engineMode);

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
		if(!dictionary.isEmpty()) return;
		new DictionaryGenerateTask(words, dictionary).execute();
	}

	public void generate(InputStream words, InputStream trails, EngineMode mode) {
		if(!dictionary.isEmpty() && !trailsDictionary.isEmpty()) return;
		new DictionaryGenerateTask(words, dictionary).execute();
		new DictionaryGenerateTask(trails, trailsDictionary).execute();
	}

	@Override
	public void convert(ComposingWord word) {
		if(word.getEntireWord() == null) return;
		if(task != null) {
			task.cancel(true);
		}
		hangulEngine.resetCycle();
		task = new KoreanT9ConvertTask(this, word);
		task.execute();

	}

	static class KoreanT9ConvertTask extends AsyncTask<Void, Integer, Integer> implements HangulEngine.HangulEngineListener {

		private T9Converter converter;

		private ComposingWord word;
		private HangulEngine hangulEngine;

		private List<TrieDictionary.Word> result = new ArrayList<>();

		private String composing;
		private StringBuilder composingWord;

		KoreanT9ConvertTask(T9Converter converter, ComposingWord word) {
			this.converter = converter;
			this.word = word;
			this.composing = "";
			this.composingWord = new StringBuilder();
			this.hangulEngine = converter.hangulEngine;
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
			if(converter.trailsDictionary != null && !converter.trailsDictionary.isEmpty()) {
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
					List<TrieDictionary.Word> trails = converter.trailsDictionary.searchStroke(trail.toString(), 2);
					if(trails != null) {
						if(isCancelled()) return null;
						String search = word.substring(0, word.length()-trail.length());
						List<TrieDictionary.Word> words = converter.dictionary.searchStroke(search, 4);
						for(TrieDictionary.Word tr : trails) {
							for(TrieDictionary.Word w : words) {
								result.add(new TrieDictionary.Word(w.getWord() + tr.getWord(),
										(w.getFrequency() + tr.getFrequency()) / 2));
							}
						}
					}
				}
			}

			if(isCancelled()) return null;

			List<TrieDictionary.Word> result = converter.dictionary.searchStroke(word);
			if(result != null) this.result.addAll(result);

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
			this.result.add(new TrieDictionary.Word(w, 1));

			return 1;
		}

		@Override
		protected void onPostExecute(Integer integer) {
			super.onPostExecute(integer);
			if(integer == 1 && !result.isEmpty()) {
				List<String> result = new ArrayList<>();
				Collections.sort(this.result, Collections.reverseOrder());
				for(TrieDictionary.Word word : this.result) {
					result.add(word.getWord());
				}
				EventBus.getDefault().post(new DisplayCandidatesEvent(result));
				EventBus.getDefault().post(new AutoConvertEvent(result.get(0)));
			}
		}
	}

	static class DictionaryGenerateTask extends AsyncTask<Void, Void, Integer> {

		private InputStream is;
		private TrieDictionary dictionary;

		public DictionaryGenerateTask(InputStream is, TrieDictionary dictionary) {
			this.is = is;
			this.dictionary = dictionary;
		}

		@Override
		protected Integer doInBackground(Void... voids) {
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line;
			try {
				int i = Integer.MAX_VALUE;
				while((line = br.readLine()) != null) {
					dictionary.insert(line, i--);
				}
				return 1;
			} catch(IOException ex) {
				ex.printStackTrace();
			}
			return -1;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
		}
	}

}
