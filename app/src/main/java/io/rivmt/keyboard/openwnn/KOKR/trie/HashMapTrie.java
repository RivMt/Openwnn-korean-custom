package io.rivmt.keyboard.openwnn.KOKR.trie;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

public class HashMapTrie implements Trie, Compressable {

	private static final String COMPAT_JAMO =   "ㄱㄲㄴㄷㄸㄹㅁㅂㅃㅅㅆㅇㅈㅉㅊㅋㅌㅍㅎㅏㅐㅑㅒㅓㅔㅕㅖㅗㅘㅙㅚㅛㅜㅝㅞㅟㅠㅡㅢㅣㆍㄱㄲㄳㄴㄵㄶㄷㄹㄺㄻㄼㄽㄾㄿㅀㅁㅂㅄㅅㅆㅇㅈㅊㅋㅌㅍㅎ";
	private static final String STANDARD_JAMO = "ᄀᄁᄂᄃᄄᄅᄆᄇᄈᄉᄊᄋᄌᄍᄎᄏᄐᄑ하ᅢᅣᅤᅥᅦᅧᅨᅩᅪᅫᅬᅭᅮᅯᅰᅱᅲᅳᅴᅵᆞᆨᆩᆪᆫᆬᆭᆮᆯᆰᆱᆲᆳᆴᆵᆶᆷᆸᆹᆺᆻᆼᆽᆾᆿᇀᇁᇂ";

	protected TrieNode root;

	public HashMapTrie() {
		root = new TrieNode('\0');
	}

	@Override
	public void insert(String word, int frequency) {
		word = Normalizer.normalize(word, Normalizer.Form.NFD);
		TrieNode p = root;
		for(char c : word.toCharArray()) {
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

	@Override
	public boolean search(String word) {
		TrieNode p = searchNode(word);
		return p != null && p.frequency != 0;
	}

	@Override
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

	@Override
	public void compress() {
		compress(root);
	}

	private TrieNode compress(TrieNode p) {
		if(p.children == null) return p;
		for(char ch : p.children.keySet()) {
			compress(p.children.get(ch));
		}
		if(p.children.size() == 1) {
			for(char ch : p.children.keySet()) {
				TrieNode child = p.children.get(ch);
				if(child.compressed != null) p.compressed = p.ch + child.compressed;
				else p.compressed = new String(new char[] {p.ch, child.ch});
				p.frequency = child.frequency;
				p.children = null;
			}
		}
		return p;
	}

	@Override
	public List<String> getAllWords() {
		return getAllWords(root, "");
	}

	private List<String> getAllWords(TrieNode p, String currentWord) {
		List<String> result = new ArrayList<>();
		if(p.frequency > 0) result.add(currentWord);
		for(char ch : p.children.keySet()) {
			TrieNode child = p.children.get(ch);
			if(child.compressed != null) result.add(currentWord + child.compressed);
			else result.addAll(getAllWords(child, currentWord + ch));
		}
		return result;
	}

	@Override
	public boolean isEmpty() {
		return root.children.isEmpty();
	}

	@Override
	public void serialize(OutputStream out) throws IOException {
		DataOutputStream dos = new DataOutputStream(out);
		serialize(dos, root);
	}

	private void serialize(DataOutputStream out, TrieNode p) throws IOException {
		out.writeChar(p.ch);
		out.writeInt(p.frequency);
		out.writeShort(p.children.size());
		for(char ch : p.children.keySet()) {
			TrieNode child = p.children.get(ch);
			serialize(out, child);
		}
		out.flush();
	}

	public void deserialize(InputStream in) throws IOException {
		DataInputStream dis = new DataInputStream(in);
		this.root = deserialize(dis);
	}

	private TrieNode deserialize(DataInputStream in) throws IOException {
		TrieNode p = new TrieNode(in.readChar());
		p.frequency = in.readInt();
		int size = in.readShort();
		for(int i = 0 ; i < size ; i++) {
			TrieNode child = deserialize(in);
			p.children.put(child.ch, child);
		}
		return p;
	}

}
