package io.rivmt.keyboard.openwnn;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OpenWnnControlPanelKOKR extends PreferenceActivity {

	private List<String> fragmentNames = new ArrayList<String>() {{
		add(InputMethodFragment.class.getName());
		add(KeyboardAppearanceFragment.class.getName());
		add(SoftKeyboardFragment.class.getName());
		add(HardKeyboardFragment.class.getName());
		add(ConversionFragment.class.getName());
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

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			addPreferencesFromResource(R.xml.openwnn_pref_ko_method);
			addPreferencesFromResource(R.xml.openwnn_pref_ko_appearance);
			addPreferencesFromResource(R.xml.openwnn_pref_ko_softkeyboard);
			addPreferencesFromResource(R.xml.openwnn_pref_ko_hardkeyboard);
			addPreferencesFromResource(R.xml.openwnn_pref_ko_conversion);
			addPreferencesFromResource(R.xml.openwnn_pref_ko_system);
			addPreferencesFromResource(R.xml.openwnn_pref_ko_about);
			if(prefs.getBoolean("hidden_settings_revealed", false)) {
				addPreferencesFromResource(R.xml.openwnn_pref_ko_developer);
			}
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		String skin = PreferenceManager.getDefaultSharedPreferences(this).getString("keyboard_skin", "white");
		Log.d("Prefs", "Skin changed");
		if (skin != null) {
			Log.d("Prefs", skin);
			toggleActivity(false, !skin.equals("eink"));
			toggleActivity(true, skin.equals("eink"));
		}
	}

	/**
	 * Toggle control panel activity among normal and eink
	 * @param isEink Bool value of target activity's icon is E-Ink or not
	 * @param value Visibility
	 */
	private void toggleActivity(boolean isEink, boolean value) {
		PackageManager manager = this.getPackageManager();
		manager.setComponentEnabledSetting(
				new ComponentName(
						this.getPackageName(),
						"io.rivmt.keyboard.openwnn.OpenWnnControlPanelKOKR" + (isEink ? "Eink": "")
				),
				value ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED : PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
				PackageManager.DONT_KILL_APP
		);
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
	public static class ConversionFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.openwnn_pref_ko_conversion);
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
