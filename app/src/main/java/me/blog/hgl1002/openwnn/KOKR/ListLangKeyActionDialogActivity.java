package me.blog.hgl1002.openwnn.KOKR;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;

import me.blog.hgl1002.openwnn.OpenWnnKOKR;
import me.blog.hgl1002.openwnn.R;

import static me.blog.hgl1002.openwnn.OpenWnnKOKR.*;

public class ListLangKeyActionDialogActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final String[] labels = new String[] {
				getResources().getString(R.string.preference_system_list_methods),
				getResources().getString(R.string.preference_system_toggle_12key_mode),
				getResources().getString(R.string.preference_system_toggle_one_handed_mode),
				getResources().getString(R.string.preference_system_open_settings)
		};
		final String[] actions = new String[] {
				LANGKEY_LIST_METHODS,
				LANGKEY_TOGGLE_12KEY_MODE,
				LANGKEY_TOGGLE_ONE_HAND_MODE,
				LANGKEY_OPEN_SETTINGS
		};
		AlertDialog.Builder builder;
		if( Build.VERSION.SDK_INT >= 21) builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog);
		else if( Build.VERSION.SDK_INT >= 11) builder = new AlertDialog.Builder(this, android.R.style.Theme_Dialog);
		else builder = new AlertDialog.Builder(this);
		builder
				.setTitle(R.string.preference_system_list_actions_title)
				.setItems(labels, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						OpenWnnKOKR.getInstance().onLangKey(actions[which]);
						dialog.dismiss();
						finish();
					}
				})
				.setOnCancelListener(new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialogInterface) {
						finish();
					}
				})
				.show();
	}
}
