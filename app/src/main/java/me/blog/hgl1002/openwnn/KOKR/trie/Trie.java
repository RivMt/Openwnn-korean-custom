package me.blog.hgl1002.openwnn.KOKR.trie;

import java.text.Normalizer;

public class Trie {

	private static final String COMPAT_JAMO =   "ㄱㄲㄴㄷㄸㄹㅁㅂㅃㅅㅆㅇㅈㅉㅊㅋㅌㅍㅎㅏㅐㅑㅒㅓㅔㅕㅖㅗㅘㅙㅚㅛㅜㅝㅞㅟㅠㅡㅢㅣㆍㄱㄲㄳㄴㄵㄶㄷㄹㄺㄻㄼㄽㄾㄿㅀㅁㅂㅄㅅㅆㅇㅈㅊㅋㅌㅍㅎ";
	private static final String STANDARD_JAMO = "ᄀᄁᄂᄃᄄᄅᄆᄇᄈᄉᄊᄋᄌᄍᄎᄏᄐᄑ하ᅢᅣᅤᅥᅦᅧᅨᅩᅪᅫᅬᅭᅮᅯᅰᅱᅲᅳᅴᅵᆞᆨᆩᆪᆫᆬᆭᆮᆯᆰᆱᆲᆳᆴᆵᆶᆷᆸᆹᆺᆻᆼᆽᆾᆿᇀᇁᇂ";

	protected TrieNode root;

	public Trie() {
		root = new TrieNode();
	}

	public void insert(String word, int frequency) {
		word = Normalizer.normalize(word, Normalizer.Form.NFD);
		TrieNode p = root;
		for(char c : word.toCharArray()) {
			c = Character.toLowerCase(c);
			int index = c - 'a';
			if(c >= 0x1100) index = c - '\u1100' + 26;
			if(index < 0 || index >= p.children.length) continue;
			if(p.children[index] == null) {
				TrieNode temp = new TrieNode();
				p.children[index] = temp;
				p = temp;
			} else {
				p = p.children[index];
			}
		}
		p.frequency = frequency;
	}

	public boolean search(String word) {
		TrieNode p = searchNode(word);
		return p != null && p.frequency != 0;
	}

	public boolean startsWith(String prefix) {
		TrieNode p = searchNode(prefix);
		return p != null;
	}

	public TrieNode searchNode(String s) {
		s = Normalizer.normalize(s, Normalizer.Form.NFD);
		TrieNode p = root;
		for(char c : s.toCharArray()) {
			int index = c - 'a';
			if(c >= 0x1100) index = c - '\u1100' + 26;
			if(p.children[index] != null) p = p.children[index];
			else return null;
		}
		if(p == root) return null;
		return p;
	}

}
