package me.blog.hgl1002.openwnn.KOKR;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import me.blog.hgl1002.openwnn.OpenWnn;
import me.blog.hgl1002.openwnn.R;
import me.blog.hgl1002.openwnn.WnnWord;
import me.blog.hgl1002.openwnn.event.SelectCandidateEvent;

public class CandidatesViewManagerKOKR {

	OpenWnn parent;
	LinearLayout mainView;

	int itemViewId;

	public View initView(OpenWnn parent, int width, int height) {
		this.parent = parent;

		final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(parent);
		String skin = pref.getString("keyboard_skin", parent.getResources().getString(R.string.keyboard_skin_id_default));
		int id = parent.getResources().getIdentifier("candidates_view_" + skin, "layout", parent.getPackageName());
		itemViewId = parent.getResources().getIdentifier("candidates_item_" + skin, "layout", parent.getPackageName());
		if(id != 0) mainView = (LinearLayout) parent.getLayoutInflater().inflate(id, null);
		else mainView = (LinearLayout) parent.getLayoutInflater().inflate(R.layout.candidates_view, null);

		return mainView;
	}

	public void clearCandidates() {
		if(mainView == null) return;
		LinearLayout firstView = mainView.findViewById(R.id.candidates_1st_view);
		firstView.removeAllViews();
	}

	public void displayCandidates(List<String> candidates) {
		this.displayCandidates(candidates, -1);
	}

	public void displayCandidates(List<String> candidates, int position) {
		if(mainView == null || candidates == null) return;
		LinearLayout firstView = mainView.findViewById(R.id.candidates_1st_view);
		LayoutInflater inflater = parent.getLayoutInflater();
		for(final String candidate : candidates) {
			TextView candidateItemView;
			if(itemViewId == 0) candidateItemView = (TextView) inflater.inflate(R.layout.candidates_item, null);
			else candidateItemView = (TextView) inflater.inflate(itemViewId, null);
			candidateItemView.setText(candidate);
			candidateItemView.setOnClickListener((v) -> {
				EventBus.getDefault().post(new SelectCandidateEvent(new WnnWord(candidate, "")));
			});
			if(position < 0) firstView.addView(candidateItemView);
			else {
				firstView.addView(candidateItemView, position);
				position++;
			}
		}
	}

	public void setPreferences(SharedPreferences pref) {
	}

}
