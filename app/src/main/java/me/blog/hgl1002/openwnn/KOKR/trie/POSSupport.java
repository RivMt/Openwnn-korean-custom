package me.blog.hgl1002.openwnn.KOKR.trie;

import java.util.List;

public interface POSSupport extends TrieDictionary {

	void insert(String word, int frequency, int pos);

	class Word extends TrieDictionary.Word {

		private int pos;

		public Word(String word, int frequency, int pos) {
			super(word, frequency);
			this.pos = pos;
		}

		public int getPos() {
			return pos;
		}

		public void setPos(int pos) {
			this.pos = pos;
		}

	}

}
