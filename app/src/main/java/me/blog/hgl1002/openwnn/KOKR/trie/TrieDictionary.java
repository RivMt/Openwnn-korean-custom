package me.blog.hgl1002.openwnn.KOKR.trie;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import me.blog.hgl1002.openwnn.KOKR.EngineMode;

public class TrieDictionary extends Trie {

	private static final String COMPAT_CHO = "ㄱㄲㄳㄴㄵㄶㄷㄸㄹㄺㄻㄼㄽㄾㄿㅀㅁㅂㅃㅄㅅㅆㅇㅈㅉㅊㅋㅌㅍㅎ";
	private static final String CONVERT_CHO = "ᄀᄁ ᄂ  ᄃᄄᄅ       ᄆᄇᄈ ᄉᄊᄋᄌᄍᄎᄏᄐᄑᄒ";
	private static final String CONVERT_JONG = "ᆨᆩᆪᆫᆬᆭᆮ ᆯᆰᆱᆲᆳᆴᆵᆶᆷᆸ ᆹᆺᆻᆼᆽ ᆾᆿᇀᇁᇂ";
	private static final String COMPAT_JUNG = "ㅏㅐㅑㅒㅓㅔㅕㅖㅗㅘㅙㅚㅛㅜㅝㅞㅟㅠㅡㅢㅣㆍ";
	private static final String CONVERT_JUNG = "ᅢᅣᅤᅥᅦᅧᅨᅩᅪᅫᅬᅭᅮᅯᅰᅱᅲᅳᅴᅵᆞ";

	private static final List<Character> DOUBLE_JUNGS = new ArrayList<Character>() {{
		add('ᅪ');
		add('ᅫ');
		add('ᅬ');
		add('ᅯ');
		add('ᅰ');
		add('ᅱ');
		add('ᅴ');
	}};

	Map<Character, String> keyMap = new HashMap<>();

	public TrieDictionary(EngineMode engineMode) {
		super();
		this.keyMap = generateKeyMap(engineMode);
	}

	public List<String> searchStorkeStartsWith(String stroke) {
		List<String> result = new LinkedList<>();

		return result;
	}

	public List<Word> searchStroke(String stroke) {
		return searchStroke(stroke, 0);
	}

	public List<Word> searchStroke(String stroke, int limit) {
		if(stroke == null || stroke.length() == 0) return null;
		return searchStroke(stroke, root, "", new ArrayList<>(), 0, true, limit);
	}

	private List<Word> searchStroke(String stroke, TrieNode p, String currentWord, List<Word> words, int depth, boolean fitLength, int limit) {
		if(p.frequency > 0 && (fitLength && depth == stroke.length()))
			words.add(new Word(Normalizer.normalize(currentWord, Normalizer.Form.NFC), p.frequency));
		if(limit > 0 && words.size() >= limit) return words;
		for(char ch : p.children.keySet()) {
			TrieNode child = p.children.get(ch);
			String charStroke = keyMap.get(ch);
			checkStroke:
			if(charStroke != null
					&& depth + charStroke.length() - 1 < stroke.length()
					&& charStroke.charAt(0) == stroke.charAt(depth)) {
				for(int j = 1 ; j < charStroke.length() ; j++) {
					if(charStroke.charAt(j) != stroke.charAt(depth + j)) {
						break checkStroke;
					}
				}
				searchStroke(stroke, child, currentWord + ch, words, depth + 1, fitLength, limit);
			}
		}
		return words;
	}

	private Map<Character, String> generateKeyMap(EngineMode engineMode) {
		Map<Character, String> map = new HashMap<>();
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

			for(int i = 1 ; i < item.length ; i++) {
				char ch = (char) item[i];
				putJamo(map, Character.toString(sourceChar), ch);
			}
		}

		if(engineMode.addStroke != null) {
			for(int[] item : engineMode.addStroke) {
				char strokeSource  = (char) item[0];
				String source  = map.get(strokeSource);
				for(int i = 1 ; i < item.length ; i++) {
					char ch = (char) item[i];
					putJamo(map, source, ch);
				}
			}
		}

		if(engineMode.combination != null) {
			for(int[] item : engineMode.combination) {
				char a = (char) item[0];
				char b = (char) item[1];
				char result = (char) item[2];
				if(!map.containsKey(a) || !map.containsKey(b)) continue;
				String source;
				if(DOUBLE_JUNGS.contains(result)) source = map.get(a) + map.get(b);
				else source = map.get(a);
				putJamo(map, source, result);
			}
		}
		return map;
	}

	private static void putJamo(Map<Character, String> map, String source, char jamo) {
		if(jamo >= 'ㄱ' && jamo <= 'ㅎ') {
			char cho = CONVERT_CHO.charAt(COMPAT_CHO.indexOf(jamo));
			char jong = CONVERT_JONG.charAt(COMPAT_CHO.indexOf(jamo));
			if(cho != ' ') {
				map.put(cho, source);
				switch(cho) {
				case 0x1100:
				case 0x1103:
				case 0x1107:
				case 0x1109:
				case 0x110c:
					map.put((char) (cho+1), source);
				}
			}
			if(jong != ' ') {
				map.put(jong, source);
				switch(jong) {
				case 0x11a8:
				case 0x11ba:
					map.put((char) (jong+1), source);
				}
			}
		} else if(jamo >= 'ㅏ' && jamo <= 'ㅣ') {
			char jung = (char) (jamo - 0x314f + 0x1161);
			map.put(jung, source);
		} else if(jamo == '\u318d') {			// 아래아
			map.put('\u119e', source);
		} else {
			if(jamo >= 'a' && jamo <= 'z') map.put(Character.toUpperCase(jamo), source);
			map.put(jamo, source);
		}
	}

	public static class Word implements Comparable<Word> {
		private String word;
		private int frequency;

		public Word(String word, int frequency) {
			this.word = word;
			this.frequency = frequency;
		}

		public String getWord() {
			return word;
		}

		public void setWord(String word) {
			this.word = word;
		}

		public int getFrequency() {
			return frequency;
		}

		public void setFrequency(int frequency) {
			this.frequency = frequency;
		}

		@Override
		public String toString() {
			return word;
		}

		@Override
		public int compareTo(Word word) {
			return frequency - word.frequency;
		}
	}

}
