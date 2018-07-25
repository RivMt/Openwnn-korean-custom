package me.blog.hgl1002.openwnn.event;

import me.blog.hgl1002.openwnn.KOKR.trie.TrieDictionary;
import me.blog.hgl1002.openwnn.WnnWord;

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
