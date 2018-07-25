package me.blog.hgl1002.openwnn.event;

import me.blog.hgl1002.openwnn.KOKR.trie.TrieDictionary;

public class AutoConvertEvent extends OpenWnnEvent {

	private TrieDictionary.Word candidate;

	public AutoConvertEvent(TrieDictionary.Word candidate) {
		this.candidate = candidate;
	}

	public TrieDictionary.Word getCandidate() {
		return candidate;
	}

}
