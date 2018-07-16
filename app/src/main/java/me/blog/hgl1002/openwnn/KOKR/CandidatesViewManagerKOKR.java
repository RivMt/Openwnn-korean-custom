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

	public void displayCandidates(List<String> candidates) {
		if(mainView == null) return;
		LinearLayout firstView = mainView.findViewById(R.id.candidates_1st_view);
		firstView.removeAllViews();
		for(final String candidate : candidates) {
			TextView candidateView = (TextView) parent.getLayoutInflater().inflate(R.layout.candidates_item, null);
			candidateView.setText(candidate);
			candidateView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					EventBus.getDefault().post(new SelectCandidateEvent(new WnnWord(candidate, "")));
				}
			});
			firstView.addView(candidateView);
		}
	}

	public void setPreferences(SharedPreferences preferences) {

	}

}
