package me.blog.hgl1002.openwnn.event;

import android.view.KeyEvent;

public class InputKeyEvent extends OpenWnnEvent {

	private KeyEvent keyEvent;

	public InputKeyEvent(KeyEvent keyEvent) {
		this.keyEvent = keyEvent;
	}

	public KeyEvent getKeyEvent() {
		return keyEvent;
	}

}
