package io.rivmt.keyboard.openwnn.event;

public class SoftKeyLongPressEvent extends OpenWnnEvent {

	private int keyCode;

	public SoftKeyLongPressEvent(int keyCode) {
		this.keyCode = keyCode;
	}

	public int getKeyCode() {
		return keyCode;
	}
	
}
