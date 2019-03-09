package me.blog.hgl1002.openwnn.KOKR;

import android.content.Context;
import android.inputmethodservice.Keyboard;

public class KeyboardKOKR extends Keyboard {
	
	int mTotalWidth, mTotalHeight;
	
	public KeyboardKOKR(Context context, int layoutTemplateResId, CharSequence characters, int columns,
			int horizontalPadding) {
		super(context, layoutTemplateResId, characters, columns, horizontalPadding);
	}

	public KeyboardKOKR(Context context, int xmlLayoutResId, int modeId) {
		super(context, xmlLayoutResId, modeId);
	}

	public KeyboardKOKR(Context context, int xmlLayoutResId) {
		super(context, xmlLayoutResId);
	}

	public void resizeWidth(int keyWidth) {
		mTotalWidth = getWidth();
		double widthModifier = 1;
		int width = 0;
		for(Key key : getKeys()) {
			widthModifier = (double) keyWidth / (double) getKeyWidth();
			key.width *= widthModifier;
			key.x *= widthModifier;
		}
		setKeyWidth(width);
		mTotalWidth *= widthModifier;
		getNearestKeys(0, 0);
	}

	public void resizeHeight(int keyHeight) {
		mTotalHeight = getHeight();
		double heightModifier = 1;
		int height = 0;
		for(Key key : getKeys()) {
			heightModifier = (double) keyHeight / (double) getKeyHeight();
			key.height *= heightModifier;
			key.y *= heightModifier;
			height = key.height;
		}
		setKeyHeight(height);
		mTotalHeight *= heightModifier;
		getNearestKeys(0, 0);
	}

	public void resizeWidthToLeft(double widthModifier) {
		mTotalWidth = getWidth();
		int width = 0;
		for(Key key : getKeys()) {
			key.width *= widthModifier;
			key.x *= widthModifier;
		}
		setKeyWidth(width);
		mTotalWidth *= widthModifier;
		getNearestKeys(0, 0);
	}

	public void resizeWidthToRight(double widthModifier) {
		mTotalWidth = getWidth();
		int offX = (int) (mTotalWidth - mTotalWidth*widthModifier);
		int width = 0;
		for(Key key : getKeys()) {
			key.width *= widthModifier;
			key.x *= widthModifier;
			key.x += offX;
		}
		setKeyWidth(width);
		mTotalWidth *= widthModifier;
		getNearestKeys(0, 0);
	}

	public void oneHandedMode(Context context, boolean direction, double ratio) {
		if(direction == DefaultSoftKeyboardKOKR.ONE_HAND_LEFT) {
			resizeWidthToLeft(ratio);
		} else {
			resizeWidthToRight(ratio);
		}
	}

	public void resizeHeight(double heightModifier) {
		mTotalHeight = getHeight();
		int height = 0;
		for(Key key : getKeys()) {
			key.height *= heightModifier;
			key.y *= heightModifier;
			height = key.height;
		}
		setKeyHeight(height);
		mTotalHeight *= heightModifier;
		getNearestKeys(0, 0);
	}

	public void setMargins(int left, int right, int bottom) {
		mTotalWidth = getWidth();
		mTotalHeight = getHeight();
		double widthModifier = (double) (mTotalWidth - left - right) / mTotalWidth;
		double heightModifier = (double) (mTotalHeight - bottom) / mTotalHeight;
		for(Key key : getKeys()) {
			key.width *= widthModifier;
			key.x *= widthModifier;
			key.x += left;
			key.height *= heightModifier;
			key.y *= heightModifier;
		}
	}

	public int getWidth() {
		if(mTotalWidth == 0) return super.getMinWidth();
		else return mTotalWidth;
	}

	@Override
	public int getHeight() {
		if(mTotalHeight == 0) return super.getHeight();
		else return mTotalHeight;
	}
	
}
