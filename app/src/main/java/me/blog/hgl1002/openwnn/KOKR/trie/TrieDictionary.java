package me.blog.hgl1002.openwnn.KOKR.trie;

import java.util.List;
import java.util.Map;

public interface TrieDictionary extends Trie {

	List<Word> searchStorkeStartsWith(Map<Character, String> keyMap, String stroke, int limit);

	List<Word> searchStroke(Map<Character, String> keyMap, String stroke);

	List<Word> searchStroke(Map<Character, String>keyMap, String stroke, int limit);

	List<Word> searchStartsWith(String prefix, int limit);

	boolean isReady();
	void setReady(boolean ready);

	class Word implements Comparable<Word> {
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
			if(frequency > word.frequency) return 1;
			else if(frequency < word.frequency) return -1;
			else return 0;
		}
	}

}
