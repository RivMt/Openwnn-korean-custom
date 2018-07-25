package me.blog.hgl1002.openwnn.KOKR;

import android.graphics.Color;

public class SoftKeyDisplay {
	private int keyIcon;
	private int keyBackground;
	private int color;

	public SoftKeyDisplay(int keyIcon, int keyBackground, int color) {
		this.keyIcon = keyIcon;
		this.keyBackground = keyBackground;
		this.color = color;
	}

	public SoftKeyDisplay(int keyIcon, int keyBackground) {
		this(keyIcon, keyBackground, Color.WHITE);
	}

	public SoftKeyDisplay(int keyIcon) {
		this(keyIcon, 0);
	}

	public int getKeyIcon() {
		return keyIcon;
	}

	public void setKeyIcon(int keyIcon) {
		this.keyIcon = keyIcon;
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
