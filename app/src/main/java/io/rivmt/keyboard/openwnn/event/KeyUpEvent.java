package io.rivmt.keyboard.openwnn.event;

import android.view.KeyEvent;

public class KeyUpEvent extends OpenWnnEvent {

	private KeyEvent keyEvent;

	public KeyUpEvent(KeyEvent keyEvent) {
		this.keyEvent = keyEvent;
	}

	public KeyEvent getKeyEvent() {
		return keyEvent;
	}

}
