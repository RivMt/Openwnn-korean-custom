package me.blog.hgl1002.openwnn.KOKR.converter;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.blog.hgl1002.openwnn.KOKR.ComposingWord;
import me.blog.hgl1002.openwnn.KOKR.EngineMode;
import me.blog.hgl1002.openwnn.KOKR.WordConverter;
import me.blog.hgl1002.openwnn.KOKR.trie.TrieDictionary;
import me.blog.hgl1002.openwnn.event.DisplayCandidatesEvent;

public class AutoTextConverter implements WordConverter {

	private Map<String, String> autoTexts;

	public AutoTextConverter(Map<String, String> autoTexts) {
		this.autoTexts = autoTexts;
	}

	@Override
	public void convert(ComposingWord word) {
		List<TrieDictionary.Word> result = new ArrayList<>();
		String str = autoTexts.get(word.getEntireWord());
		if(str != null) {
			result.add(new TrieDictionary.Word(str, 1));
			EventBus.getDefault().post(new DisplayCandidatesEvent(result, 0));
		}
	}

	@Override
	public void setEngineMode(EngineMode engineMode) {

	}
}
