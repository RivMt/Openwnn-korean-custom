package me.blog.hgl1002.openwnn.KOKR;

import android.content.Context;
import android.inputmethodservice.Keyboard;

import me.blog.hgl1002.openwnn.R;

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
			int oldWidth = key.width;
			widthModifier = (double) keyWidth / (double) oldWidth * (double) oldWidth / (double) getKeyWidth();
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
			int oldHeight = key.height;
			heightModifier = (double) keyHeight / (double) oldHeight * (double) oldHeight / (double) getKeyHeight();
			key.height *= heightModifier;
			key.y *= heightModifier;
			height = key.height;
		}
		setKeyHeight(height);
		mTotalHeight *= heightModifier;
		getNearestKeys(0, 0);
	}

	public void resizeWidth(double widthModifier) {
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
			resizeWidth(ratio);
		} else {
			resizeWidthToRight(ratio);
		}
	}

	public void resizeHeight(double heightModifier) {
		mTotalHeight = getHeight();
		int height = 0;
		for(Key key : getKeys()) {
			height = key.width;
			int oldHeight = key.height;
			key.height *= heightModifier;
			key.y *= heightModifier;
			height = key.height;
		}
		setKeyHeight(height);
		mTotalHeight *= heightModifier;
		getNearestKeys(0, 0);
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
