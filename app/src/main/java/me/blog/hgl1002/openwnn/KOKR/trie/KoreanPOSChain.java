package me.blog.hgl1002.openwnn.KOKR.trie;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KoreanPOSChain {

	private static Map<String, List<KoreanPOS>> chains = new HashMap<>();

	public static void generate(InputStream is) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line;
			while((line = br.readLine()) != null) {
				try {
					int index = line.lastIndexOf('>');
					int resultPosCode = Integer.parseInt(line.substring(index+1));
					KoreanPOS resultPos = KoreanPOS.get(resultPosCode);
					String key = line.substring(0, index);
					if(!chains.containsKey(key)) chains.put(key, new ArrayList<>());
					chains.get(key).add(resultPos);
				} catch(NumberFormatException ignore) {}
			}
		} catch(IOException ex) {
			ex.printStackTrace();
		}
	}

	public static List<KoreanPOS> getAvailablePOS(List<KoreanPOS> chain) {
		if(chain.size() == 0) return null;
		int begin = chain.size() - 4;
		if(begin < 0) begin = 0;
		int until = chain.size();
		StringBuilder builder = new StringBuilder(String.valueOf(chain.get(begin++).getCode()));
		for(int i = begin ; i < until ; i++) {
			builder.append(">");
			builder.append(chain.get(i).getCode());
		}
		return chains.get(builder.toString());
	}

}
