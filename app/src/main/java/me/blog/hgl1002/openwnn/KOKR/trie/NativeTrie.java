package me.blog.hgl1002.openwnn.KOKR.trie;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;

public class NativeTrie implements Trie, Compressable {

	static {
		System.loadLibrary("triedictionary-lib");
	}

	private native void initNative();
	private native void insertNative(String word, int frequency);
	private native String[] getAllWordsNative();
	private native String[] searchStartsWithNative(String search, int limit);
	private native void compressNative();
	private native void deinitNative();

	public NativeTrie() {
		this.initNative();
	}

	@Override
	public void insert(String word, int frequency) {
		this.insertNative(Normalizer.normalize(word, Normalizer.Form.NFD), frequency);
	}

	@Override
	public boolean search(String word) {
		return false;
	}

	@Override
	public List<String> searchStartsWith(String prefix, int limit) {
		return Arrays.asList(searchStartsWithNative(Normalizer.normalize(prefix, Normalizer.Form.NFD), limit));
	}

	@Override
	public boolean startsWith(String prefix) {
		return false;
	}

	@Override
	public List<String> getAllWords() {
		return Arrays.asList(getAllWordsNative());
	}

	@Override
	public void compress() {
		compressNative();
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public void serialize(OutputStream out) throws IOException {

	}

	@Override
	public void deserialize(InputStream in) throws IOException {

	}

}
