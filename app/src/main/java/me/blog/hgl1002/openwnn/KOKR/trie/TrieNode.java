package me.blog.hgl1002.openwnn.KOKR.trie;

import java.util.HashMap;
import java.util.Map;

public class TrieNode {
	char ch;
	Map<Character, TrieNode> children;
	int frequency;

	public TrieNode(char ch) {
		children = new HashMap<>();
		this.ch = ch;
	}

}
