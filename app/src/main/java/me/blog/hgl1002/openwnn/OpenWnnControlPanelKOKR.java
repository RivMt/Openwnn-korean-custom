package me.blog.hgl1002.openwnn;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

import me.blog.hgl1002.openwnn.R;

public class OpenWnnControlPanelKOKR extends PreferenceActivity {

	private List<String> fragmentNames = new ArrayList<String>() {{
		add(InputMethodFragment.class.getName());
		add(KeyboardAppearanceFragment.class.getName());
		add(SoftKeyboardFragment.class.getName());
		add(HardKeyboardFragment.class.getName());
		add(SystemFragment.class.getName());
		add(AboutFragment.class.getName());
		add(DeveloperFragment.class.getName());
	}};

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if(OpenWnnKOKR.getInstance() == null) {
			new OpenWnnKOKR(this);
		}

		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			addPreferencesFromResource(R.xml.openwnn_pref_ko_method);
			addPreferencesFromResource(R.xml.openwnn_pref_ko_appearance);
			addPreferencesFromResource(R.xml.openwnn_pref_ko_softkeyboard);
			addPreferencesFromResource(R.xml.openwnn_pref_ko_hardkeyboard);
			addPreferencesFromResource(R.xml.openwnn_pref_ko_system);
			addPreferencesFromResource(R.xml.openwnn_pref_ko_about);
			if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("hidden_settings_revealed", false)) {
				addPreferencesFromResource(R.xml.openwnn_pref_ko_developer);
			}
		}
	}

	@TargetApi(11)
	@Override
	public void onBuildHeaders(List<Header> target) {
		if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("reveal_dev_settings", false)) {
			loadHeadersFromResource(R.xml.openwnn_pref_ko_headers_dev, target);
		} else {
			loadHeadersFromResource(R.xml.openwnn_pref_ko_headers, target);
		}
	}

	@TargetApi(11)
	@Override
	protected boolean isValidFragment(String fragmentName) {
		return fragmentNames.contains(fragmentName);
	}

	@TargetApi(11)
	public static class InputMethodFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.openwnn_pref_ko_method);
		}
	}

	@TargetApi(11)
	public static class KeyboardAppearanceFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.openwnn_pref_ko_appearance);
		}
	}

	@TargetApi(11)
	public static class SoftKeyboardFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.openwnn_pref_ko_softkeyboard);
		}
	}

	@TargetApi(11)
	public static class HardKeyboardFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.openwnn_pref_ko_hardkeyboard);
		}
	}

	@TargetApi(11)
	public static class SystemFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.openwnn_pref_ko_system);
		}
	}

	@TargetApi(11)
	public static class AboutFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.openwnn_pref_ko_about);
		}
	}

	@TargetApi(11)
	public static class DeveloperFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.openwnn_pref_ko_developer);
		}
	}

}
