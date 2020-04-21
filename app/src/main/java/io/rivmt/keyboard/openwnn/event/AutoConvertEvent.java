package io.rivmt.keyboard.openwnn.event;

import io.rivmt.keyboard.openwnn.KOKR.trie.TrieDictionary;

public class AutoConvertEvent extends OpenWnnEvent {

	private TrieDictionary.Word candidate;

	public AutoConvertEvent(TrieDictionary.Word candidate) {
		this.candidate = candidate;
	}

	public TrieDictionary.Word getCandidate() {
		return candidate;
	}

}
