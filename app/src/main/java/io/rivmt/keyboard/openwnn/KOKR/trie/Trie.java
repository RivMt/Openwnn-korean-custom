package io.rivmt.keyboard.openwnn.KOKR.trie;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public interface Trie {

	void insert(String word, int frequency);

	boolean search(String word);

	boolean startsWith(String prefix);

	List<String> getAllWords();

	boolean isEmpty();

	void serialize(OutputStream out) throws IOException;

	void deserialize(InputStream in) throws IOException;

}
