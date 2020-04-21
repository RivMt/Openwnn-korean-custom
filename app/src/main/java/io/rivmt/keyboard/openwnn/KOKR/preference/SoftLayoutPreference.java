package io.rivmt.keyboard.openwnn.KOKR.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.view.View;

import org.greenrobot.eventbus.EventBus;

import io.rivmt.keyboard.openwnn.event.InputViewChangeEvent;
import io.rivmt.keyboard.openwnn.R;

public class SoftLayoutPreference extends ListPreference {

	String key;

	public SoftLayoutPreference(Context context, AttributeSet attrs) {
		super(context, attrs);

		key = attrs.getAttributeValue(null, "layoutKey");

	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);
		if (positiveResult) {
			EventBus.getDefault().post(new InputViewChangeEvent());
		}
	}

	@Override
	protected View onCreateDialogView() {

		SharedPreferences pref = getPreferenceManager().getSharedPreferences();

		this.setValue(pref.getString(this.getKey(), this.getValue()));

		if(key != null) {
			String layout = pref.getString(key, "");
			setEntries(getEntries(layout));
			setEntryValues(getEntryValues(layout));
		}
		return super.onCreateDialogView();
	}

	public static int getEntries(String layout) {
		switch(layout) {
		case "keyboard_sebul_390":
		case "keyboard_sebul_391":
		case "keyboard_sebul_sun_2014":
		case "keyboard_sebul_3_2015m":
		case "keyboard_sebul_3_2015":
		case "keyboard_sebul_3_p3":
		case "keyboard_sebul_shin_original":
		case "keyboard_sebul_shin_edit":
		case "keyboard_sebul_shin_m":
		case "keyboard_sebul_shin_p2":
		case "keyboard_sebul_3_2015y":
			return R.array.keyboard_soft_layout_l1_2;

		case "keyboard_sebul_393y":
			return R.array.keyboard_soft_layout_l1_3;

		case "keyboard_sebul_ahnmatae":
			return R.array.keyboard_soft_layout_l1_4;

		case "keyboard_alphabet_colemak":
			return R.array.keyboard_soft_layout_l1_9;

		case "keyboard_alphabet_dvorak":
			return R.array.keyboard_soft_layout_dvorak;

		case "keyboard_symbols_a":
		case "keyboard_symbols_b":
			return R.array.keyboard_soft_layout_alt;

		case "keyboard_alphabet_qwerty":
		case "keyboard_sebul_semoe_2016":
		case "keyboard_sebul_semoe":
		case "keyboard_sebul_danmoeum":
		case "keyboard_dubul_standard":
		case "keyboard_dubul_nk":
		case "keyboard_dubul_yet":
		default:
			return R.array.keyboard_soft_layout;

		}
	}

	public static int getEntryValues(String layout) {
		switch(layout) {
		case "keyboard_sebul_390":
		case "keyboard_sebul_391":
		case "keyboard_sebul_sun_2014":
		case "keyboard_sebul_3_2015m":
		case "keyboard_sebul_3_2015":
		case "keyboard_sebul_3_p3":
		case "keyboard_sebul_shin_original":
		case "keyboard_sebul_shin_edit":
		case "keyboard_sebul_shin_m":
		case "keyboard_sebul_shin_p2":
		case "keyboard_sebul_3_2015y":
			return R.array.keyboard_soft_layout_l1_2_id;

		case "keyboard_sebul_393y":
			return R.array.keyboard_soft_layout_l1_3_id;

		case "keyboard_sebul_ahnmatae":
			return R.array.keyboard_soft_layout_l1_4_id;

		case "keyboard_alphabet_colemak":
			return R.array.keyboard_soft_layout_l1_9_id;

		case "keyboard_alphabet_dvorak":
			return R.array.keyboard_soft_layout_dvorak_id;

		case "keyboard_symbols_a":
		case "keyboard_symbols_b":
			return R.array.keyboard_soft_layout_alt_id;

		case "keyboard_alphabet_qwerty":
		case "keyboard_sebul_semoe_2016":
		case "keyboard_sebul_semoe":
		case "keyboard_sebul_danmoeum":
		case "keyboard_dubul_standard":
		case "keyboard_dubul_nk":
		case "keyboard_dubul_yet":
		default:
			return R.array.keyboard_soft_layout_id;

		}
	}

}
