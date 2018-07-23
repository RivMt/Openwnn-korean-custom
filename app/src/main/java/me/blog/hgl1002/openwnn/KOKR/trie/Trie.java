package me.blog.hgl1002.openwnn.KOKR.trie;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

public interface Trie {

	void insert(String word, int frequency);

	boolean search(String word);

	List<String> searchStartsWith(String prefix, int limit);

	boolean startsWith(String prefix);

	List<String> getAllWords();

	boolean isEmpty();

	void serialize(OutputStream out) throws IOException;

	void deserialize(InputStream in) throws IOException;

}
