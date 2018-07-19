package me.blog.hgl1002.openwnn.KOKR.trie;

public class TrieNode {
	TrieNode[] children;
	int frequency;

	public TrieNode() {
		this.children = new TrieNode[26 + 0x100];
	}

}
