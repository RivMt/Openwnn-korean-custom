package me.blog.hgl1002.openwnn.KOKR;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import me.blog.hgl1002.openwnn.KOKR.trie.TrieDictionary;
import me.blog.hgl1002.openwnn.OpenWnn;
import me.blog.hgl1002.openwnn.R;
import me.blog.hgl1002.openwnn.WnnWord;
import me.blog.hgl1002.openwnn.event.SelectCandidateEvent;

public class CandidatesViewManagerKOKR {

	OpenWnn parent;
	RecyclerView mainView;
	CandidatesViewAdapter adapter;

	public View initView(OpenWnn parent, int width, int height) {
		this.parent = parent;

		final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(parent);

		String skin = pref.getString("keyboard_skin", parent.getResources().getString(R.string.keyboard_skin_id_default));
		int id = parent.getResources().getIdentifier("candidates_view_" + skin, "layout", parent.getPackageName());
		int itemViewId = parent.getResources().getIdentifier("candidates_item_" + skin, "layout", parent.getPackageName());

		if (id == 0) id = R.layout.candidates_view;
		if (itemViewId == 0) itemViewId = R.layout.candidates_item;

		View view = parent.getLayoutInflater().inflate(id, null, false);
		mainView = view.findViewById(R.id.candidate_view_scroll);
		adapter = new CandidatesViewAdapter(itemViewId, parent);

		LinearLayoutManager manager = new LinearLayoutManager(parent);
		manager.setSmoothScrollbarEnabled(true);
		manager.setOrientation(LinearLayoutManager.HORIZONTAL);

		mainView.setLayoutManager(manager);
		mainView.setAdapter(adapter);

		return view;
	}

	public void clearCandidates() {
		displayCandidates(new ArrayList <>());
	}

	public void displayCandidates(List<TrieDictionary.Word> candidates) {
		this.displayCandidates(candidates, -1);
	}

	public void displayCandidates(List<TrieDictionary.Word> candidates, int position) {
		if(mainView == null || candidates == null) return;
		if(position < 0) adapter.set(candidates);
		else adapter.addAll(candidates, position);
	}

	public void addDisplayCandidates(List<TrieDictionary.Word> candidates, int position) {
		if(mainView == null || candidates == null) return;
		if(position < 0) adapter.addAll(candidates);
		else adapter.addAll(candidates, position);
	}

	public void setPreferences(SharedPreferences pref) {
	}

	private class CandidatesViewAdapter extends RecyclerView.Adapter<CandidatesViewHolder>{
		private List<TrieDictionary.Word> candidateList = new ArrayList <>();
		final int itemViewId;
		OpenWnn parent;

		CandidatesViewAdapter(int itemViewId, OpenWnn parent){
			this.itemViewId = itemViewId;
			this.parent = parent;
		}

		@NonNull
		@Override
		public CandidatesViewHolder onCreateViewHolder (@NonNull ViewGroup viewGroup, int i) {
			return new CandidatesViewHolder(parent
					.getLayoutInflater()
					.inflate(itemViewId, viewGroup, false));
		}

		@Override
		public void onBindViewHolder (@NonNull CandidatesViewHolder viewHolder, int i) {
			viewHolder.onBind(candidateList.get(i).getWord());
		}

		@Override
		public int getItemCount () {
			return candidateList.size();
		}

		public void add(TrieDictionary.Word string, int index){
			candidateList.add(index, string);
			notifyItemInserted(index);
		}

		public void add(TrieDictionary.Word string){
			add(string, getItemCount());
		}

		public void remove(int index) {
			candidateList.remove(index);
			notifyItemRemoved(index);
		}

		public void removeAll(){
			candidateList.clear();
			notifyDataSetChanged();
		}

		public void set(List<TrieDictionary.Word> strings){
			removeAll();
			addAll(strings);
		}

		public void addAll(List<TrieDictionary.Word> strings){
			addAll(strings, getItemCount());
		}

		public void addAll(List<TrieDictionary.Word> strings, int position){
			for(TrieDictionary.Word string: strings) {
				add(string, position);
				position++;
			}
		}
	}

	private class CandidatesViewHolder extends RecyclerView.ViewHolder {
		TextView candidateText;
		String candidate;

		CandidatesViewHolder (@NonNull View itemView) {
			super(itemView);
			candidateText = itemView.findViewById(R.id.candidate_text);
			candidateText.setOnClickListener((v) ->
				EventBus.getDefault().post(new SelectCandidateEvent(new WnnWord(candidate, "")))
			);
		}

		public void onBind (String candidateString){
			candidate = candidateString;
			candidateText.setText(candidateString);
		}
	}
}
