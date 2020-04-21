package io.rivmt.keyboard.openwnn.event;

import io.rivmt.keyboard.openwnn.KOKR.trie.TrieDictionary;
import io.rivmt.keyboard.openwnn.WnnWord;

public class SelectCandidateEvent extends OpenWnnEvent {

	private WnnWord wnnWord;
	private TrieDictionary.Word word;

	public SelectCandidateEvent(WnnWord word) {
		this.wnnWord = word;
	}

	public SelectCandidateEvent(TrieDictionary.Word word) {
		this.word = word;
	}

	public WnnWord getWnnWord() {
		return wnnWord;
	}

	public TrieDictionary.Word getWord() {
		return word;
	}
}
