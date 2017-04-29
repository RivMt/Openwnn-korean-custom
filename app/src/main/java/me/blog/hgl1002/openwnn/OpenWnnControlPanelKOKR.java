package me.blog.hgl1002.openwnn;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import me.blog.hgl1002.openwnn.R;

public class OpenWnnControlPanelKOKR extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if(OpenWnnKOKR.getInstance() == null) {
			new OpenWnnKOKR(this);
		}
		
		addPreferencesFromResource(R.xml.openwnn_pref_ko);
	}

}
