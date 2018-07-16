package me.blog.hgl1002.openwnn.KOKR;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.blog.hgl1002.openwnn.event.DisplayCandidatesEvent;

public class AutoTextConverter implements WordConverter {

	private Map<String, String> autoTexts;

	public AutoTextConverter(Map<String, String> autoTexts) {
		this.autoTexts = autoTexts;
	}

	@Override
	public void convert(ComposingWord word) {
		List<String> result = new ArrayList<>();
		result.add(autoTexts.get(word.getEntireWord()));
		EventBus.getDefault().post(new DisplayCandidatesEvent(result, 0));
	}

}
