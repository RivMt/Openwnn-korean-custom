package me.blog.hgl1002.openwnn.KOKR;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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

	public View initView(OpenWnn parent, int width, int height) {
		this.parent = parent;

		final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(parent);

		mainView = (LinearLayout) parent.getLayoutInflater().inflate(R.layout.candidates_view, null);

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
		for(final String candidate : candidates) {
			TextView candidateView = (TextView) parent.getLayoutInflater().inflate(R.layout.candidates_item, null);
			candidateView.setText(candidate);
			candidateView.setOnClickListener((v) -> {
				EventBus.getDefault().post(new SelectCandidateEvent(new WnnWord(candidate, "")));
			});
			if(position < 0) firstView.addView(candidateView);
			else {
				firstView.addView(candidateView, position);
				position++;
			}
		}
	}

	public void setPreferences(SharedPreferences preferences) {

	}

}
