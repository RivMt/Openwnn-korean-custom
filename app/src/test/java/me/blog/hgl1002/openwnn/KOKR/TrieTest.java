package me.blog.hgl1002.openwnn.KOKR;

import org.junit.Test;

import me.blog.hgl1002.openwnn.KOKR.trie.TrieDictionary;

public class TrieTest {

	@Test
	public void test() {
		String[] words = "안녕하세요:1,반갑습니다:1,안녕:2,가방:2,가맹:1,개명:1,거병:1".split(",");
		TrieDictionary dictionary = new TrieDictionary(EngineMode.TWELVE_DUBUL_NARATGEUL_PREDICTIVE);
		for(String word : words) {
			String[] splitted = word.split(":");
			dictionary.insert(splitted[0], splitted.length >= 2 ? Integer.parseInt(splitted[1]) : 1);
		}
		System.out.println(dictionary.getAllWords());
		System.out.println(dictionary.searchStroke("13538"));
	}

}
