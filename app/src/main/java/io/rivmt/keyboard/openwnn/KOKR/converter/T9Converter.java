package io.rivmt.keyboard.openwnn.KOKR.converter;

import android.os.AsyncTask;
import android.os.Build;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import io.rivmt.keyboard.openwnn.DefaultSoftKeyboard;
import io.rivmt.keyboard.openwnn.KOKR.ComposingWord;
import io.rivmt.keyboard.openwnn.KOKR.EngineMode;
import io.rivmt.keyboard.openwnn.KOKR.HangulEngine;
import io.rivmt.keyboard.openwnn.KOKR.TwelveHangulEngine;
import io.rivmt.keyboard.openwnn.KOKR.WordConverter;
import io.rivmt.keyboard.openwnn.KOKR.trie.Dictionaries;
import io.rivmt.keyboard.openwnn.KOKR.trie.HashMapTrieDictionary;
import io.rivmt.keyboard.openwnn.KOKR.trie.MiniPOS;
import io.rivmt.keyboard.openwnn.KOKR.trie.POSChain;
import io.rivmt.keyboard.openwnn.KOKR.trie.TrieDictionary;
import io.rivmt.keyboard.openwnn.OpenWnnKOKR;
import io.rivmt.keyboard.openwnn.event.AutoConvertEvent;
import io.rivmt.keyboard.openwnn.event.DisplayCandidatesEvent;

public class T9Converter implements WordConverter {

	private List<Character> consonantList = new ArrayList<>();
	private List<Character> vowelList = new ArrayList<>();

	private EngineMode engineMode;
	private TwelveHangulEngine hangulEngine;

	private boolean convert;
	private T9ConvertTask task;

	private Map<Character, String> keyMap;

	private int language;

	public T9Converter() {
		hangulEngine = new TwelveHangulEngine();
		hangulEngine.setMoachigi(false);
	}

	@Override
	public void convert(ComposingWord word) {
		if(task != null) {
			task.cancel(true);
		}
		if(!convert) {
			EventBus.getDefault().post(new DisplayCandidatesEvent(new ArrayList <>()));
			return;
		}
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

		convert = engineMode.properties.predictive;

		this.keyMap = HashMapTrieDictionary.generateKeyMap(engineMode);

		this.language = engineMode.properties.languageCode;

		for(int i = 0 ; i < 22 ; i++) {
			TrieDictionary dictionary = Dictionaries.getDictionary(language, i);
			if(dictionary != null) dictionary.setKeyMap(keyMap);
		}

		if(engineMode.layout == null) return;

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
			if(sourceChar == 0x30 + 11) sourceChar = '-';
			if(sourceChar == 0x30 + 12) sourceChar = '=';
			char jamo = (char) item[1];
			if(jamo >= 'ㄱ' && jamo <= 'ㅎ' || jamo >= '\u1100' && jamo <= '\u115f' || jamo >= '\u11a8' && jamo <= '\u11ff') {
				consonantList.add(sourceChar);
			} else if(jamo >= 'ㅏ' && jamo <= 'ㅣ' || jamo >= '\u1160' && jamo <= '\u11a7') {
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
			TrieDictionary mainDictionary = Dictionaries.getDictionary(converter.language, 0);

			if(mainDictionary == null || !mainDictionary.isReady()) {
				String rawComposed = rawCompose(word);
				if(rawComposed != null && !rawComposed.isEmpty()) this.result.add(new HashMapTrieDictionary.Word(rawComposed, 1));
				return 1;
			}

			if(converter.language == DefaultSoftKeyboard.LANG_KO) {

				List<String> syllables = getSyllables(word);
				if(syllables == null) return null;

				List<TrieDictionary.Word> result = new ArrayList<>();
				for(POSChain chain : POSChain.values()) {
					if(isCancelled()) return null;
					result.addAll(searchSyllables(syllables, chain, 0, 0));
				}
				this.result.addAll(result);
			} else {
				List<TrieDictionary.Word> result = mainDictionary.searchStroke(word);
				if(result != null) this.result.addAll(result);

			}

			if(isCancelled()) return null;

			String rawComposed = rawCompose(word);
			if(rawComposed != null && !rawComposed.isEmpty()) this.result.add(new HashMapTrieDictionary.Word(rawComposed, 1));

			Collections.sort(this.result, Collections.reverseOrder());
			this.result = new ArrayList<>(new LinkedHashSet<>(this.result));

			return 1;
		}

		private List<TrieDictionary.Word> searchSyllables(List<String> syllables, POSChain chain, int posIndex, int syllableIndex) {
			List<TrieDictionary.Word> result = new ArrayList<>();
			if(posIndex >= chain.getPosList().length || syllableIndex >= syllables.size()) return result;
			MiniPOS pos = chain.getPosList()[posIndex];
			if(pos == MiniPOS.SPACE) {
				result.add(new TrieDictionary.Word(" ", "", Integer.MAX_VALUE/2));
			}
			if(pos.getDictionaryIndex() <= 0) return result;
			TrieDictionary dictionary = Dictionaries.getDictionary(DefaultSoftKeyboard.LANG_KO, pos.getDictionaryIndex());
			if(dictionary == null) return result;
			StringBuilder stroke = new StringBuilder();
			for(int i = syllableIndex ; i < syllables.size() ; i++) {
				stroke.append(syllables.get(i));
				List<TrieDictionary.Word> front = dictionary.searchStroke(stroke.toString());
				List<TrieDictionary.Word> back = searchSyllables(syllables, chain, posIndex+1, i+1);
				for(TrieDictionary.Word w1 : front) {
					for(TrieDictionary.Word w2 : back) {
						TrieDictionary.MultipleWords multipleWords = TrieDictionary.MultipleWords.create(w1, w2);
						if(multipleWords.getWord().length() + syllableIndex == syllables.size()) {
							result.add(multipleWords);
						}
					}
				}
				if(back.isEmpty()) {
					for(TrieDictionary.Word w1 : front) {
						if(w1.getWord().length() + syllableIndex == syllables.size()) result.add(w1);
					}
				} else if(front.isEmpty()) {
					for(TrieDictionary.Word w1 : back) {
						if(w1.getWord().length() + syllableIndex == syllables.size()) result.add(w1);
					}
				}
				Collections.sort(result, Collections.reverseOrder());
				if(result.size() > 4) result = result.subList(0, 5);
			}
			return result;
		}

		private List<String> getSyllables(String stroke) {
			List<String> result = new ArrayList<>();
			char cho, jung, jung2, jong;
			cho = jung = jung2 = jong = '\0';
			for(int i = stroke.length()-1 ; i >= 0 ; i--) {
				if(isCancelled()) return null;
				char ch = stroke.charAt(i);
				if(converter.vowelList.contains(ch)) {
					if(cho != '\0') {
						result.add(0, cho + "" + (jung2 != '\0' ? jung2 + "" + jung : jung) + "" + (jong != '\0' ? jong : ""));
						cho = jong = jung2 = '\0';
						jung = ch;
					}
					else if(jung != '\0') {
						jung2 = ch;
					}
					else jung = ch;
				} else if(converter.consonantList.contains(ch)) {
					if(cho != '\0') {
						result.add(0, cho + "" + (jung2 != '\0' ? jung2 + "" + jung : jung) + "" + (jong != '\0' ? jong : ""));
						cho = jung = jung2 = '\0';
						jong = ch;
					}
					else if(jung != '\0') cho = ch;
					else jong = ch;
				}
			}
			if(cho != '\0') {
				result.add(0, cho + "" + (jung2 != '\0' ? jung2 + "" + jung : jung) + "" + (jong != '\0' ? jong : ""));
			}
			return result;
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
			if(integer == 1) {
				EventBus.getDefault().post(new DisplayCandidatesEvent(result));
				if(result.size() > 0) EventBus.getDefault().post(new AutoConvertEvent(result.get(0)));
				else EventBus.getDefault().post(new AutoConvertEvent(new TrieDictionary.Word("", 1)));
			}
		}
	}

	public EngineMode getEngineMode() {
		return engineMode;
	}

	public static List<MiniPOS.Word> convertToPOSWord(List<TrieDictionary.Word> words, MiniPOS pos) {
		List<MiniPOS.Word> result = new ArrayList<>();
		for(TrieDictionary.Word word : words) {
			result.add(new MiniPOS.Word(word.getWord(), word.getStroke(), word.getFrequency(), pos));
		}
		return result;
	}

}
