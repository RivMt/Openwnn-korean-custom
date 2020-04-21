package io.rivmt.keyboard.openwnn.KOKR;

import android.graphics.Color;

public class SoftKeyDisplay {
	private int keyIcon;
	private int keyBackground;
	private int color;
	private boolean fixWidth;

	public SoftKeyDisplay(int keyIcon, int keyBackground, int color, boolean fixWidth) {
		this.keyIcon = keyIcon;
		this.keyBackground = keyBackground;
		this.color = color;
		this.fixWidth = fixWidth;
	}

	public SoftKeyDisplay(int keyIcon, int keyBackground, int color) {
		this(keyIcon, keyBackground, color, false);
	}

	public SoftKeyDisplay(int keyIcon, int keyBackground, boolean fixWidth) {
		this(keyIcon, keyBackground, Color.WHITE, fixWidth);
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

	public boolean getFixWidth() {
		return fixWidth;
	}

	public void setFixWidth(boolean fixWidth) {
		this.fixWidth = fixWidth;
	}

}
