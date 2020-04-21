package io.rivmt.keyboard.openwnn.KOKR.preference;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.widget.Toast;

import io.rivmt.keyboard.openwnn.R;

public class RevealHiddenPreference extends Preference {

	public static final int REVEAL = 10;
	public static final int ALERT = 7;

	Context context;
	int clickCount;
	Toast showingToast;

	public RevealHiddenPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	@Override
	protected void onClick() {
		clickCount++;
		if(clickCount >= REVEAL) {
			persistBoolean(true);
		} else if(clickCount >= ALERT) {
			String message = String.format(context.getString(R.string.preference_reveal_hidden_msg), REVEAL - clickCount);
			if(showingToast != null) showingToast.cancel();
			showingToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
			showingToast.show();
		}
		super.onClick();
	}
}
