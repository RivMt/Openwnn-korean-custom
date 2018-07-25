package me.blog.hgl1002.openwnn.KOKR;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;

import java.lang.reflect.Field;
import java.util.List;

public class DefaultSoftKeyboardViewKOKR extends KeyboardView {

	private Context context;
	private SoftKeyboardDisplay keyboardDisplay;

	public DefaultSoftKeyboardViewKOKR(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		Keyboard keyboard = this.getKeyboard();
		if(keyboard == null) return;
		if(keyboardDisplay == null) return;

		for(Keyboard.Key key : keyboard.getKeys()) {
			int keyCode = key.codes[0];
			SoftKeyDisplay keyDisplay = keyboardDisplay.get(keyCode);
			if(keyDisplay == null) continue;
			if(keyDisplay.getKeyBackground() != 0) {
				onDrawBackground(keyDisplay.getKeyBackground(), canvas, key);
				onDrawForeground(keyDisplay.getKeyIcon(), keyDisplay.getColor(), canvas, key);
			}
		}

	}

	private void onDrawBackground(int drawableId, Canvas canvas, Keyboard.Key key) {
		Drawable npd = context.getResources().getDrawable(drawableId);
		int[] drawableState = key.getCurrentDrawableState();
		if(key.codes[0] != 0) npd.setState(drawableState);
		npd.setBounds(key.x, key.y, key.x + key.width, key.y + key.height);
		npd.draw(canvas);
	}

	private void onDrawForeground(int drawableId, int textColor, Canvas canvas, Keyboard.Key key) {
		Rect bounds = new Rect();
		Paint paint = new Paint();
		paint.setTextAlign(Paint.Align.CENTER);
		paint.setAntiAlias(true);
		if(textColor != 0) paint.setColor(textColor);
		else paint.setColor(Color.WHITE);

		if(drawableId != 0) {
			Drawable icon = context.getResources().getDrawable(drawableId);
			int x = key.x + (key.width - key.icon.getIntrinsicWidth())/2;
			int y = key.y + (key.height - key.icon.getIntrinsicHeight())/2;
			icon.setBounds(
					x, y, x + key.icon.getIntrinsicWidth(), y + key.icon.getIntrinsicHeight());
			icon.draw(canvas);
		} else if(key.icon != null) {
			int x = key.x + (key.width - key.icon.getIntrinsicWidth())/2;
			int y = key.y + (key.height - key.icon.getIntrinsicHeight())/2;
			key.icon.setBounds(
					x, y, x + key.icon.getIntrinsicWidth(), y + key.icon.getIntrinsicHeight());
			key.icon.draw(canvas);
		} else if(key.label != null) {
			String label = key.label.toString();

			Field field;
			int minimumTextSize = 0;
			try {
				field = KeyboardView.class.getDeclaredField("mLabelTextSize");
				field.setAccessible(true);
				minimumTextSize = (int) field.get(this);
			} catch(NoSuchFieldException e) {
				e.printStackTrace();
			} catch(IllegalAccessException e) {
				e.printStackTrace();
			}
			paint.getTextBounds(label, 0, label.length(), bounds);

			int labelTextSize = 4;
			float desiredTextSize;
			if(key.width < key.height) desiredTextSize = labelTextSize * key.width / bounds.width();
			else desiredTextSize = labelTextSize * key.height / bounds.height();
			if(desiredTextSize < minimumTextSize) desiredTextSize = minimumTextSize;
			paint.setTextSize(desiredTextSize);
			if(label.length() > 1 && key.codes.length <= 1) paint.setTypeface(Typeface.DEFAULT);
			else paint.setTypeface(Typeface.DEFAULT_BOLD);

			canvas.drawText(label, key.x + key.width/2, key.y + key.height/2 + desiredTextSize/3, paint);
		}

	}

	public SoftKeyboardDisplay getKeyboardDisplay() {
		return keyboardDisplay;
	}

	public void setKeyboardDisplay(SoftKeyboardDisplay keyboardDisplay) {
		this.keyboardDisplay = keyboardDisplay;
	}
}
