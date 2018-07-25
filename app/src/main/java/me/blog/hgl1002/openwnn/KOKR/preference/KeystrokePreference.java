package me.blog.hgl1002.openwnn.KOKR.preference;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Build;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.util.concurrent.atomic.AtomicInteger;

import me.blog.hgl1002.openwnn.R;

public class KeystrokePreference extends Preference {

	private String defaultValue;

	public KeystrokePreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public KeystrokePreference(Context context) {
		super(context);
	}

	@Override
	protected View onCreateView(ViewGroup parent) {
		return super.onCreateView(parent);
	}

	@Override
	protected void onClick() {
		View view = View.inflate(getContext(), R.layout.select_keystroke, null);
		final EditText display = (EditText) view.findViewById(R.id.key_display);
		final TextView description = (TextView) view.findViewById(R.id.key_description);
		description.setText(getSummary());

		final CheckBox control = (CheckBox) view.findViewById(R.id.key_control);
		final CheckBox alt= (CheckBox) view.findViewById(R.id.key_alt);
		final CheckBox win = (CheckBox) view.findViewById(R.id.key_win);
		final CheckBox shift = (CheckBox) view.findViewById(R.id.key_shift);
		final AtomicInteger code = new AtomicInteger(0);

		KeyStroke stroke = parseKeyStroke(getPersistedString(defaultValue == null ? "----0" : defaultValue));
		control.setChecked(stroke.control);
		alt.setChecked(stroke.alt);
		win.setChecked(stroke.win);
		shift.setChecked(stroke.shift);
		code.set(stroke.keyCode);
		if(Build.VERSION.SDK_INT >= 12) {
			display.setText(KeyEvent.keyCodeToString(stroke.keyCode));
		} else {
			display.setText(stroke.keyCode);
		}

		AlertDialog dialog = new AlertDialog.Builder(getContext())
				.setTitle(getTitle())
				.setView(view)
				.setOnKeyListener(new DialogInterface.OnKeyListener() {
					@Override
					public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
						code.set(keyCode);
						if(Build.VERSION.SDK_INT >= 12) {
							display.setText(KeyEvent.keyCodeToString(keyCode));
						} else {
							display.setText(keyCode);
						}
						return false;
					}
				})
				.setPositiveButton(R.string.dialog_button_ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						StringBuilder builder = new StringBuilder();
						builder.append(control.isChecked() ? 'c' : '-');
						builder.append(alt.isChecked() ? 'a' : '-');
						builder.append(win.isChecked() ? 'w' : '-');
						builder.append(shift.isChecked() ? 's' : '-');
						builder.append(String.valueOf(code.intValue()));
						persistString(builder.toString());
					}
				})
				.setNegativeButton(R.string.dialog_button_cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				})
				.create();
		dialog.show();
		super.onClick();
	}

	public static KeyStroke parseKeyStroke(String keyStrokeStr) {
		Integer keyCode = Integer.parseInt(keyStrokeStr.substring(4));
		if(keyCode == null) return null;
		boolean control = keyStrokeStr.charAt(0) == 'c';
		boolean alt = keyStrokeStr.charAt(1) == 'a';
		boolean win = keyStrokeStr.charAt(2) == 'w';
		boolean shift = keyStrokeStr.charAt(3) == 's';
		return new KeyStroke(control, alt, win, shift, keyCode);
	}

	@Override
	protected Object onGetDefaultValue(TypedArray a, int index) {
		return a.getString(index);
	}

	@Override
	protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
		super.onSetInitialValue(restorePersistedValue, defaultValue);
		if(!restorePersistedValue) this.defaultValue = (String) defaultValue;
	}

	public static class KeyStroke {
		boolean control, alt, win, shift;
		int keyCode;

		public KeyStroke(boolean control, boolean alt, boolean win, boolean shift, int keyCode) {
			this.control = control;
			this.alt = alt;
			this.win = win;
			this.shift = shift;
			this.keyCode = keyCode;
		}

		public boolean isControl() {
			return control;
		}

		public boolean isAlt() {
			return alt;
		}

		public boolean isWin() {
			return win;
		}

		public boolean isShift() {
			return shift;
		}

		public int getKeyCode() {
			return keyCode;
		}
	}

}
