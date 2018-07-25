package me.blog.hgl1002.openwnn.KOKR.trie;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NativeTrieDictionary extends NativeTrie implements POSSupport {

	static {
		System.loadLibrary("triedictionary-lib");
	}

	private boolean ready = true;
	protected Map<Character, String> keyMap;

	private native void setKeyMapNative(Map<Character, String> keyMap);
	private native Map<String, Integer> searchStrokeNative(String stroke, boolean fitLength, int limit);

	public NativeTrieDictionary() {
		super();
	}

	@Override
	public void insert(String word, int frequency, int pos) {
		this.insertNative(Normalizer.normalize(word, Normalizer.Form.NFD), frequency, pos);
	}

	@Override
	public List<TrieDictionary.Word> searchStroke(String stroke) {
		return this.searchStroke(stroke, true, 0);
	}

	@Override
	public List<TrieDictionary.Word> searchStroke(String stroke, int limit) {
		return this.searchStroke(stroke, true, limit);
	}

	@Override
	public List<TrieDictionary.Word> searchStrokeStartsWith(String stroke, int limit) {
		return this.searchStroke(stroke, false, limit);
	}

	private List<TrieDictionary.Word> searchStroke(String stroke, boolean fitLength, int limit) {
		Map<String, Integer> map = searchStrokeNative(stroke, fitLength, limit);
		List<TrieDictionary.Word> result = new ArrayList<>();
		for(String word : map.keySet()) {
			result.add(new TrieDictionary.Word(Normalizer.normalize(word, Normalizer.Form.NFC), map.get(word)));
		}
		return result;
	}

	@Override
	public void setKeyMap(Map<Character, String> keyMap) {
		this.keyMap = keyMap;
		this.setKeyMapNative(keyMap);
	}

	@Override
	public List<TrieDictionary.Word> searchStartsWith(String prefix, int limit) {
		Map<String, Integer> map = searchStartsWithNative(Normalizer.normalize(prefix, Normalizer.Form.NFD), limit);
		List<TrieDictionary.Word> result = new ArrayList<>();
		for(String word : map.keySet()) {
			result.add(new TrieDictionary.Word(Normalizer.normalize(word, Normalizer.Form.NFC), map.get(word)));
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
