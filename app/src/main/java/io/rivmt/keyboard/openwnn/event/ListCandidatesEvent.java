package io.rivmt.keyboard.openwnn.event;

public class ListCandidatesEvent extends OpenWnnEvent {

	private boolean full;

	public ListCandidatesEvent(boolean full) {
		this.full = full;
	}

	public boolean isFull() {
		return full;
	}

}
