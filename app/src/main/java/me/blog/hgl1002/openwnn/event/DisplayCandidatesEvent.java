package me.blog.hgl1002.openwnn.event;

import java.util.List;

public class DisplayCandidatesEvent extends OpenWnnEvent {

	private List<String> candidates;

	public DisplayCandidatesEvent(List<String> candidates) {
		this.candidates = candidates;
	}

	public List<String> getCandidates() {
		return candidates;
	}

	public void setCandidates(List<String> candidates) {
		this.candidates = candidates;
	}

}
