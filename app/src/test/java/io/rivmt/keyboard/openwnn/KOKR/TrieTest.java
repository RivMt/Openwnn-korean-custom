package io.rivmt.keyboard.openwnn.KOKR;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import io.rivmt.keyboard.openwnn.KOKR.trie.HashMapTrieDictionary;

public class TrieTest {

	@Test
	public void test() {
//		String[] words = "안녕하세요:1,반갑습니다:1,안녕:2,가방:2,가맹:1,개명:1,거병:1".split(",");
		HashMapTrieDictionary dictionary = new HashMapTrieDictionary();
		Map<Character, String> keyMap = HashMapTrieDictionary.generateKeyMap(EngineMode.TWELVE_DUBUL_NARATGEUL_PREDICTIVE);
//		for(String word : words) {
//			String[] splitted = word.split(":");
//			dictionary.insert(splitted[0], splitted.length >= 2 ? Integer.parseInt(splitted[1]) : 1);
//		}
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("src\\main\\assets\\words\\korean.txt")));
			String line;
			int frequency = Integer.MAX_VALUE;
			while((line = br.readLine()) != null) {
				dictionary.insert(line, frequency--);
			}
			dictionary.compress();
			System.out.println(dictionary.searchStroke(keyMap, "89137"));
		} catch(IOException ex) {
			ex.printStackTrace();
		}

	}

}
