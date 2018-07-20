package me.blog.hgl1002.openwnn.KOKR.trie;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

public class Trie {

	private static final String COMPAT_JAMO =   "ㄱㄲㄴㄷㄸㄹㅁㅂㅃㅅㅆㅇㅈㅉㅊㅋㅌㅍㅎㅏㅐㅑㅒㅓㅔㅕㅖㅗㅘㅙㅚㅛㅜㅝㅞㅟㅠㅡㅢㅣㆍㄱㄲㄳㄴㄵㄶㄷㄹㄺㄻㄼㄽㄾㄿㅀㅁㅂㅄㅅㅆㅇㅈㅊㅋㅌㅍㅎ";
	private static final String STANDARD_JAMO = "ᄀᄁᄂᄃᄄᄅᄆᄇᄈᄉᄊᄋᄌᄍᄎᄏᄐᄑ하ᅢᅣᅤᅥᅦᅧᅨᅩᅪᅫᅬᅭᅮᅯᅰᅱᅲᅳᅴᅵᆞᆨᆩᆪᆫᆬᆭᆮᆯᆰᆱᆲᆳᆴᆵᆶᆷᆸᆹᆺᆻᆼᆽᆾᆿᇀᇁᇂ";

	protected TrieNode root;

	public Trie() {
		root = new TrieNode('\0');
	}

	public void insert(String word, int frequency) {
		word = Normalizer.normalize(word, Normalizer.Form.NFD);
		TrieNode p = root;
		for(char c : word.toCharArray()) {
			c = Character.toLowerCase(c);
			TrieNode child = p.children.get(c);
			if(child == null) {
				TrieNode temp = new TrieNode(c);
				p.children.put(c, temp);
				p = temp;
			} else {
				p = child;
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
			if(p.children.containsKey(c)) p = p.children.get(c);
			else return null;
		}
		if(p == root) return null;
		return p;
	}

	public List<String> getAllWords() {
		return getAllWords(root, "");
	}

	private List<String> getAllWords(TrieNode p, String currentWord) {
		List<String> result = new ArrayList<>();
		if(p.frequency > 0) result.add(currentWord);
		for(char ch : p.children.keySet()) {
			TrieNode child = p.children.get(ch);
			result.addAll(getAllWords(child, currentWord + ch));
		}
		return result;
	}

}
