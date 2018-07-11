package me.blog.hgl1002.openwnn.event;

public class InputCharEvent extends OpenWnnEvent {
	private char code;

	public InputCharEvent(char code) {
		this.code = code;
	}

	public char getCode() {
		return code;
	}

}
