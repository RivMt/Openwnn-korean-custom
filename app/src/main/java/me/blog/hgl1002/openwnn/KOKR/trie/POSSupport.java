package me.blog.hgl1002.openwnn.KOKR.trie;

import java.util.List;

public interface POSSupport extends TrieDictionary {

	void insert(String word, int frequency, int pos);

	class Word extends TrieDictionary.Word {

		private KoreanPOS pos;

		public Word(String word, String stroke, int frequency, KoreanPOS pos) {
			super(word, stroke, frequency);
			this.pos = pos;
		}

		public KoreanPOS getPos() {
			return pos;
		}

		public void setPos(KoreanPOS pos) {
			this.pos = pos;
		}
	}

}
