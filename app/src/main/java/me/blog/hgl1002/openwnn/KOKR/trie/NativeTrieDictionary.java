package me.blog.hgl1002.openwnn.KOKR.trie;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NativeTrieDictionary extends NativeTrie implements TrieDictionary {

	static {
		System.loadLibrary("triedictionary-lib");
	}

	private boolean ready = true;
	protected Map<Character, String> keyMap;

	public NativeTrieDictionary() {
		super();
	}

	@Override
	public List<Word> searchStorkeStartsWith(String stroke, int limit) {
		return null;
	}

	@Override
	public List<Word> searchStroke(String stroke) {
		return null;
	}

	@Override
	public List<Word> searchStroke(String stroke, int limit) {
		return null;
	}

	@Override
	public void setKeyMap(Map<Character, String> keyMap) {
		this.keyMap = keyMap;
	}

	@Override
	public List<Word> searchStartsWith(String prefix, int limit) {
		Map<String, Integer> map = searchStartsWithNative(Normalizer.normalize(prefix, Normalizer.Form.NFD), limit);
		List<Word> result = new ArrayList<>();
		for(String word : map.keySet()) {
			result.add(new Word(Normalizer.normalize(word, Normalizer.Form.NFC), map.get(word)));
		}
		return result;
	}

	@Override
	public boolean isReady() {
		return ready;
	}

	@Override
	public void setReady(boolean ready) {
		this.ready = ready;
	}

}
