package me.blog.hgl1002.openwnn.KOKR;

import android.content.Context;
import android.content.Intent;
import android.preference.Preference;
import android.provider.Settings;
import android.util.AttributeSet;

public class EnableInputMethodPreference extends Preference {
	
	Context context;
	
	public EnableInputMethodPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	@Override
	protected void onClick() {
		context.startActivity(new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS));
	}

}
