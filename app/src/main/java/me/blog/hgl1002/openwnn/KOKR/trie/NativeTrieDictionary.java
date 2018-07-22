package me.blog.hgl1002.openwnn.KOKR.trie;

import java.util.List;
import java.util.Map;

public class NativeTrieDictionary extends NativeTrie implements TrieDictionary {

	private boolean ready = true;

	public NativeTrieDictionary() {
		super();
	}

	@Override
	public List<HashMapTrieDictionary.Word> searchStorkeStartsWith(Map<Character, String> keyMap, String stroke, int limit) {
		return null;
	}

	@Override
	public List<HashMapTrieDictionary.Word> searchStroke(Map<Character, String> keyMap, String stroke) {
		return null;
	}

	@Override
	public List<HashMapTrieDictionary.Word> searchStroke(Map<Character, String> keyMap, String stroke, int limit) {
		return null;
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
