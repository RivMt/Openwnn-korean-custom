package me.blog.hgl1002.openwnn.KOKR;

import android.content.Context;
import android.inputmethodservice.Keyboard;

public class KeyboardKOKR extends Keyboard {
	
	int mTotalHeight;
	
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
	
	public void resize(int keyHeight) {
		mTotalHeight = getHeight();
		double heightModifier = 1;
		int height = 0;
		for(Key key : getKeys()) {
			if(key.codes[0] == 0) System.out.println("-----\n" + key.height + "," + key.y);
			int oldHeight = key.height;
			heightModifier = (double) keyHeight / (double) oldHeight * (double) oldHeight / (double) getKeyHeight();
			key.height *= heightModifier;
			key.y *= heightModifier;
			height = key.height;
			if(key.codes[0] == 0) System.out.println(key.height + "," + key.y);
		}
		setKeyHeight(height);
		mTotalHeight *= heightModifier;
		getNearestKeys(0, 0);
	}

	@Override
	public int getHeight() {
		if(mTotalHeight == 0) return super.getHeight();
		else return mTotalHeight;
	}
	
}
