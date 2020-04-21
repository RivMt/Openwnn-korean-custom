package io.rivmt.keyboard.openwnn.KOKR.converter;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.rivmt.keyboard.openwnn.KOKR.ComposingWord;
import io.rivmt.keyboard.openwnn.KOKR.EngineMode;
import io.rivmt.keyboard.openwnn.KOKR.WordConverter;
import io.rivmt.keyboard.openwnn.KOKR.trie.TrieDictionary;
import io.rivmt.keyboard.openwnn.event.DisplayCandidatesEvent;

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
		}
		EventBus.getDefault().post(new DisplayCandidatesEvent(result, 0));
	}

	@Override
	public void setEngineMode(EngineMode engineMode) {

	}
}
