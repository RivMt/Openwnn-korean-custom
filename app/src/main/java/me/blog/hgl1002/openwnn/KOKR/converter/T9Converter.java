package me.blog.hgl1002.openwnn.KOKR.converter;

import android.os.AsyncTask;
import android.os.Build;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import me.blog.hgl1002.openwnn.KOKR.ComposingWord;
import me.blog.hgl1002.openwnn.KOKR.EngineMode;
import me.blog.hgl1002.openwnn.KOKR.HangulEngine;
import me.blog.hgl1002.openwnn.KOKR.TwelveHangulEngine;
import me.blog.hgl1002.openwnn.KOKR.WordConverter;
import me.blog.hgl1002.openwnn.KOKR.trie.Dictionaries;
import me.blog.hgl1002.openwnn.KOKR.trie.HashMapTrieDictionary;
import me.blog.hgl1002.openwnn.KOKR.trie.TrieDictionary;
import me.blog.hgl1002.openwnn.OpenWnnKOKR;
import me.blog.hgl1002.openwnn.event.AutoConvertEvent;
import me.blog.hgl1002.openwnn.event.DisplayCandidatesEvent;

public class T9Converter implements WordConverter {

	private List<Character> consonantList = new ArrayList<>();
	private List<Character> vowelList = new ArrayList<>();

	private EngineMode engineMode;
	private TwelveHangulEngine hangulEngine;

	private boolean convert;
	private T9ConvertTask task;

	private Map<Character, String> keyMap;

	private TrieDictionary dictionary;
	private TrieDictionary trailsDictionary;

	public T9Converter() {
		hangulEngine = new TwelveHangulEngine();
		hangulEngine.setMoachigi(false);
	}

	@Override
	public void convert(ComposingWord word) {
		if(task != null) {
			task.cancel(true);
		}
		if(!convert) return;

		hangulEngine.resetCycle();
		task = new T9ConvertTask(this, word);
		task.execute();

	}

	@Override
	public void setEngineMode(EngineMode engineMode) {
		this.engineMode = engineMode;
		hangulEngine.setJamoTable(engineMode.layout);
		hangulEngine.setAddStrokeTable(engineMode.addStroke);
		hangulEngine.setCombinationTable(engineMode.combination);

		switch(engineMode) {
		case TWELVE_ALPHABET_A_PREDICTIVE:
		case TWELVE_ALPHABET_B_PREDICTIVE:
		case TWELVE_DUBUL_CHEONJIIN_PREDICTIVE:
		case TWELVE_DUBUL_NARATGEUL_PREDICTIVE:
		case TWELVE_DUBUL_SKY2_PREDICTIVE:
			convert = true;
			break;
		default:
			convert = false;
			return;
		}

		this.keyMap = HashMapTrieDictionary.generateKeyMap(engineMode);

		this.dictionary = Dictionaries.getDictionary(engineMode.properties.languageCode, 0);
		this.trailsDictionary = Dictionaries.getDictionary(engineMode.properties.languageCode, 1);

		consonantList = new ArrayList<>();
		vowelList = new ArrayList<>();
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

	static class T9ConvertTask extends AsyncTask<Void, List<String>, Integer> implements HangulEngine.HangulEngineListener {

		private T9Converter converter;

		private ComposingWord word;
		private HangulEngine hangulEngine;

		private List<HashMapTrieDictionary.Word> result = new ArrayList<>();

		private String composing;
		private StringBuilder composingWord;

		T9ConvertTask(T9Converter converter, ComposingWord word) {
			this.converter = converter;
			this.word = word;
			this.composing = "";
			this.composingWord = new StringBuilder();
			this.hangulEngine = converter.hangulEngine;
			hangulEngine.resetComposition();
			hangulEngine.setListener(this);
		}

		public void execute() {
			if(Build.VERSION.SDK_INT >= 11) {
				super.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else {
				super.execute();
			}
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
			if(converter.dictionary == null || !converter.dictionary.isReady()) {
				this.result.add(new HashMapTrieDictionary.Word(rawCompose(word), 1));
				return 1;
			}

			if(converter.trailsDictionary != null && converter.trailsDictionary.isReady()) {
				List<String> trailSource = new ArrayList<>();
				char cho, jung, jong;
				cho = jung = jong = '\0';
				for(int i = 0 ; i <= 8 ; i++) {
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
					List<HashMapTrieDictionary.Word> trails = converter.trailsDictionary.searchStroke(converter.keyMap, trail.toString());
					if(trails != null) {
						Collections.sort(trails, Collections.reverseOrder());
						trails = trails.subList(0, trails.size() < 3 ? trails.size() : 3);
						if(isCancelled()) return null;
						String search = word.substring(0, word.length()-trail.length());
						List<HashMapTrieDictionary.Word> words = converter.dictionary.searchStroke(converter.keyMap, search);
						Collections.sort(words, Collections.reverseOrder());
						words = words.subList(0, words.size() < 4 ? words.size() : 4);
						for(HashMapTrieDictionary.Word tr : trails) {
							for(HashMapTrieDictionary.Word w : words) {
								result.add(new HashMapTrieDictionary.Word(w.getWord() + tr.getWord(),
										(w.getFrequency()/2 + tr.getFrequency()/2)));
							}
						}
					}
				}
			}

			if(isCancelled()) return null;

			List<HashMapTrieDictionary.Word> result = converter.dictionary.searchStroke(converter.keyMap, word);
			if(result != null) this.result.addAll(result);

			this.result.add(new HashMapTrieDictionary.Word(rawCompose(word), 1));

			return 1;
		}

		private String rawCompose(String stroke) {
			for(char ch : stroke.toCharArray()) {
				if(isCancelled()) return null;
				int code = ch;
				boolean shift = false;
				if(Character.isUpperCase(ch)) {
					code = Character.toLowerCase(ch);
					shift = true;
				}
				for(int[] item : OpenWnnKOKR.SHIFT_CONVERT) {
					if(item[1] == ch) {
						code = item[0];
						shift = true;
						break;
					}
				}
				if(code >= '1' && code <= '9') code = -code + '0' + -2000;
				if(code == '0') code = -2010;
				if(code == '-') code = -2011;
				if(code == '=') code = -2012;

				int jamo = hangulEngine.inputCode(code, 0);

				if(shift) {
					jamo = Character.toUpperCase(jamo);
					for(int[] item : OpenWnnKOKR.SHIFT_CONVERT) {
						if(item[0] == jamo) {
							jamo = item[1];
							break;
						}
					}
				}
				if(jamo != -1) hangulEngine.inputJamo(jamo);
			}
			return composingWord + composing;
		}

		@Override
		protected void onPostExecute(Integer integer) {
			super.onPostExecute(integer);
			if(integer == 1 && !result.isEmpty()) {
				List<String> result = new ArrayList<>();
				Collections.sort(this.result, Collections.reverseOrder());
				for(HashMapTrieDictionary.Word word : this.result) {
					result.add(word.getWord());
				}
				EventBus.getDefault().post(new DisplayCandidatesEvent(result));
				if(result.size() > 0) EventBus.getDefault().post(new AutoConvertEvent(result.get(0)));
			}
		}
	}

	public EngineMode getEngineMode() {
		return engineMode;
	}

}
