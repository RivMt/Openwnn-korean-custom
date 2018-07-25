package me.blog.hgl1002.openwnn.KOKR;

import android.util.SparseArray;

public class SoftKeyboardDisplay {
	SparseArray<SoftKeyDisplay> keyDisplays;

	public SoftKeyboardDisplay() {
		keyDisplays = new SparseArray<>();
	}

	public void add(int keyCode, SoftKeyDisplay keyDisplay) {
		keyDisplays.put(keyCode, keyDisplay);
	}

	public void remove(int keyCode) {
		keyDisplays.remove(keyCode);
	}

	public SoftKeyDisplay get(int keyCode) {
		return keyDisplays.get(keyCode);
	}

}
