package me.blog.hgl1002.openwnn.KOKR;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AutoTextConverter implements WordConverter {

	private Map<String, String> autoTexts;

	public AutoTextConverter(Map<String, String> autoTexts) {
		this.autoTexts = autoTexts;
	}

	@Override
	public List<String> convert(ComposingWord word) {
		List<String> result = new ArrayList<>();
		result.add(autoTexts.get(word.getEntireWord()));
		return result;
	}

}
