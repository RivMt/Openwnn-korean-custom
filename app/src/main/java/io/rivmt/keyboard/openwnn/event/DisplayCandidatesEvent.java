package io.rivmt.keyboard.openwnn.event;

import java.util.List;

import io.rivmt.keyboard.openwnn.KOKR.trie.TrieDictionary;

public class DisplayCandidatesEvent extends OpenWnnEvent {

	private List<TrieDictionary.Word> candidates;
	private int position;

	public DisplayCandidatesEvent(List<TrieDictionary.Word> candidates, int position) {
		this.candidates = candidates;
		this.position = position;
	}

	public DisplayCandidatesEvent(List<TrieDictionary.Word> candidates) {
		this.candidates = candidates;
		this.position = -1;
	}

	public List<TrieDictionary.Word> getCandidates() {
		return candidates;
	}

	public void setCandidates(List<TrieDictionary.Word> candidates) {
		this.candidates = candidates;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}
}
