package me.blog.hgl1002.openwnn.KOKR;

import android.app.AlertDialog;
import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import me.blog.hgl1002.openwnn.R;

public class EditAutotextPreference extends Preference {

	List<AutoTextItem> list;

	public EditAutotextPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onClick() {
		super.onClick();
		list = new ArrayList<>();
		String raw = getPersistedString("");
		try {
			JSONObject jsonObject = new JSONObject(raw);
			Iterator<String> keys = jsonObject.keys();
			while(keys.hasNext()) {
				String key = keys.next();
				String value = jsonObject.getString(key);
				list.add(new AutoTextItem(key, value));
			}
		} catch(JSONException ex) {
			ex.printStackTrace();
		}
		ListView view = new ListView(getContext());
		BaseAdapter adapter = new AutotextAdapter(getContext(), list);
		view.setAdapter(adapter);
		view.setLongClickable(true);
		view.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
				list.remove(i);
				adapter.notifyDataSetChanged();
				return false;
			}
		});
		new AlertDialog.Builder(getContext())
				.setView(view)
				.setTitle(R.string.preference_conversion_autotext_dialog_title)
				.setNeutralButton(R.string.dialog_button_add, (dialog, which) -> {
					showAddDialog();
				})
				.setPositiveButton(R.string.dialog_button_ok, (dialog, which) -> {
					save();
				})
				.setNegativeButton(R.string.dialog_button_cancel, (dialog, which) -> {
				})
				.show();
	}

	private void save() {
		try {
			JSONObject result = new JSONObject();
			for(AutoTextItem item : list) {
				result.put(item.getSource(), item.getResult());
			}
			persistString(result.toString(0));
		} catch (JSONException ex) {
			ex.printStackTrace();
		}
	}

	private void showAddDialog() {
		ViewGroup edit = (ViewGroup) ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE))
				.inflate(R.layout.auto_text_edit, null);
		EditText source = edit.findViewById(R.id.auto_text_source);
		EditText result = edit.findViewById(R.id.auto_text_result);
		new AlertDialog.Builder(getContext())
				.setView(edit)
				.setTitle(R.string.preference_conversion_autotext_edit_dialog_title)
				.setPositiveButton(R.string.dialog_button_ok, (dialog, which) -> {
					list.add(new AutoTextItem(source.getText().toString(), result.getText().toString()));
					save();
				})
				.setNegativeButton(R.string.dialog_button_cancel, (dialog, which) -> {
				})
				.show();
	}

	static class AutoTextItem {
		private String source, result;

		public AutoTextItem(String source, String result) {
			this.source = source;
			this.result = result;
		}

		public String getSource() {
			return source;
		}

		public String getResult() {
			return result;
		}

	}

	static class AutotextAdapter extends BaseAdapter {

		private Context context;
		private List<AutoTextItem> items;

		public AutotextAdapter(Context context, List<AutoTextItem> items) {
			this.context = context;
			this.items = items;
		}

		@Override
		public int getCount() {
			return items.size();
		}

		@Override
		public AutoTextItem getItem(int i) {
			return items.get(i);
		}

		@Override
		public long getItemId(int i) {
			return 0;
		}

		@Override
		public View getView(int i, View view, ViewGroup parent) {
			if(view == null) view = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.auto_text_item, parent, false);
			AutoTextItem item = items.get(i);
			((TextView) view.findViewById(R.id.auto_text_source)).setText(item.getSource());
			((TextView) view.findViewById(R.id.auto_text_result)).setText(item.getResult());
			return view;
		}
	}

}
