package me.blog.hgl1002.openwnn.KOKR.trie;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class NativeTrie implements Trie, Compressable {

	static {
		System.loadLibrary("triedictionary-lib");
	}

	protected native void initNative();
	protected native void insertNative(String word, int frequency, int pos);
	protected native String[] getAllWordsNative();
	protected native Map<String, Integer> searchStartsWithNative(String search, int limit);
	protected native void compressNative();
	protected native void deinitNative();

	public NativeTrie() {
		this.initNative();
	}

	@Override
	public void insert(String word, int frequency) {
		this.insertNative(Normalizer.normalize(word, Normalizer.Form.NFD), frequency, 0);
	}

	@Override
	public boolean search(String word) {
		return false;
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

	@Override
	protected void finalize() throws Throwable {
		this.deinitNative();
		super.finalize();
	}
}
