package me.blog.hgl1002.openwnn.event;

import java.util.List;

public class DisplayCandidatesEvent extends OpenWnnEvent {

	private List<String> candidates;
	private int position;

	public DisplayCandidatesEvent(List<String> candidates, int position) {
		this.candidates = candidates;
		this.position = position;
	}

	public DisplayCandidatesEvent(List<String> candidates) {
		this.candidates = candidates;
		this.position = -1;
	}

	public List<String> getCandidates() {
		return candidates;
	}

	public void setCandidates(List<String> candidates) {
		this.candidates = candidates;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}
}
