package me.blog.hgl1002.openwnn.KOKR.trie;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public interface TrieDictionary extends Trie {

	List<Word> searchStroke(String stroke);

	List<Word> searchStroke(String stroke, int limit);

	List<Word> searchStrokeStartsWith(String stroke, int limit);

	List<Word> searchStartsWith(String prefix, int limit);

	void setKeyMap(Map<Character, String>keyMap);

	boolean isReady();
	void setReady(boolean ready);

	class Word implements Comparable<Word> {
		private String word;
		private String stroke;
		private int frequency;

		public Word(String word, String stroke, int frequency) {
			this.word = word;
			this.stroke = stroke;
			this.frequency = frequency;
		}

		public Word(String word, int frequency) {
			this(word, "", frequency);
		}

		public String getWord() {
			return word;
		}

		public void setWord(String word) {
			this.word = word;
		}

		public String getStroke() {
			return stroke;
		}

		public void setStroke(String stroke) {
			this.stroke = stroke;
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

		@Override
		public boolean equals(Object obj) {
			return (obj instanceof Word && ((Word) obj).getWord().equals(this.word)) || super.equals(obj);
		}

		@Override
		public int hashCode() {
			int result = 17;
			result = 31 * result + word.hashCode();
			return result;
		}

	}

	class MultipleWords extends Word {

		private Word[] words;

		private MultipleWords(Word... words) {
			super(getWord(words), getStroke(words), getFrequency(words));
			this.words = words;
		}

		public static MultipleWords create(Word... words) {
			List<Word> result = new ArrayList<>();
			for(Word word : words) {
				if(word instanceof MultipleWords) {
					result.addAll(Arrays.asList(((MultipleWords) word).getWords()));
				} else {
					result.add(word);
				}
			}
			words = new Word[result.size()];
			words = result.toArray(words);
			return new MultipleWords(words);
		}

		private static String getWord(Word[] words) {
			StringBuilder result = new StringBuilder();
			for(Word word : words) {
				result.append(word.getWord());
			}
			return result.toString();
		}

		private static String getStroke(Word[] words) {
			StringBuilder result = new StringBuilder();
			for(Word word : words) {
				result.append(word.getStroke());
			}
			return result.toString();
		}

		private static int getFrequency(Word[] words) {
			int average = 0;
			for(Word word : words) {
				average += word.getFrequency() / words.length;
			}
			return average;
		}

		public Word[] getWords() {
			return words;
		}

		public int count() {
			return words.length;
		}

		@Override
		public int compareTo(Word word) {
			if(word instanceof MultipleWords) {
				if(words.length < ((MultipleWords) word).words.length) return 1;
				else if(words.length > ((MultipleWords) word).words.length) return -1;
			}
			return super.compareTo(word);
		}
	}

}
