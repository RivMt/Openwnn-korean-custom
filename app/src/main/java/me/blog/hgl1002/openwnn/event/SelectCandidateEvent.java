package me.blog.hgl1002.openwnn.event;

import me.blog.hgl1002.openwnn.WnnWord;

public class SelectCandidateEvent extends OpenWnnEvent {

	private WnnWord word;

	public SelectCandidateEvent(WnnWord word) {
		this.word = word;
	}

	public WnnWord getWord() {
		return word;
	}

}
