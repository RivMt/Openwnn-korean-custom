package io.rivmt.keyboard.openwnn.KOKR;

import android.graphics.Color;
import android.util.SparseArray;

public class SoftKeyboardDisplay {

	private int background;
	private int keyBackground;
	private int color;
	private SparseArray<SoftKeyDisplay> keyDisplays;

	public SoftKeyboardDisplay(int background, int keyBackground, int color) {
		this.background = background;
		this.keyBackground = keyBackground;
		keyDisplays = new SparseArray<>();
	}

	public SoftKeyboardDisplay() {
		this(0, 0, Color.WHITE);
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

	public int getBackground() {
		return background;
	}

	public void setBackground(int background) {
		this.background = background;
	}

	public int getKeyBackground() {
		return keyBackground;
	}

	public void setKeyBackground(int keyBackground) {
		this.keyBackground = keyBackground;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}
}
