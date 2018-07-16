package me.blog.hgl1002.openwnn.event;

public class AutoConvertEvent extends OpenWnnEvent {

	private String candidate;

	public AutoConvertEvent(String candidate) {
		this.candidate = candidate;
	}

	public String getCandidate() {
		return candidate;
	}

}
