package io.rivmt.keyboard.openwnn.KOKR.preference;

import android.app.Service;
import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.inputmethod.InputMethodManager;

public class PickInputMethodPreference extends Preference {
	
	Context context;
	
	public PickInputMethodPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	@Override
	protected void onClick() {
		InputMethodManager inputMethodManager
		 = (InputMethodManager) context.getSystemService(Service.INPUT_METHOD_SERVICE);
		inputMethodManager.showInputMethodPicker();
	}

}
