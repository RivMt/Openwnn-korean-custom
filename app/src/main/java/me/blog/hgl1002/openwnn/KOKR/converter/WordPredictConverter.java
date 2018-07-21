package me.blog.hgl1002.openwnn.KOKR.converter;

import android.os.AsyncTask;
import android.os.Build;

import java.util.List;
import java.util.Map;

import me.blog.hgl1002.openwnn.KOKR.ComposingWord;
import me.blog.hgl1002.openwnn.KOKR.EngineMode;
import me.blog.hgl1002.openwnn.KOKR.WordConverter;
import me.blog.hgl1002.openwnn.KOKR.trie.Dictionaries;
import me.blog.hgl1002.openwnn.KOKR.trie.TrieDictionary;

public class WordPredictConverter implements WordConverter {

	private WordPredictConvertTask task;
	private TrieDictionary dictionary;
	private Map<Character, String> keyMap;

	@Override
	public void convert(ComposingWord word) {
		new WordPredictConvertTask(word, keyMap, dictionary).execute();
	}

	@Override
	public void setEngineMode(EngineMode engineMode) {
		this.keyMap = TrieDictionary.generateKeyMap(engineMode);
		this.dictionary = Dictionaries.getDictionary(engineMode.properties.languageCode, 0);
	}

	private static class WordPredictConvertTask extends AsyncTask<Void, Integer, Integer> {
		private ComposingWord word;
		private Map<Character, String> keyMap;
		private TrieDictionary dictionary;
		private List<TrieDictionary.Word> result;

		WordPredictConvertTask(ComposingWord word, Map<Character, String> keyMap, TrieDictionary dictionary) {
			this.word = word;
			this.keyMap = keyMap;
			this.dictionary = dictionary;
		}

		public void execute() {
			if(Build.VERSION.SDK_INT >= 11) {
				super.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else {
				super.execute();
			}
		}

		@Override
		protected Integer doInBackground(Void... voids) {
			String word = this.word.getEntireWord();
			this.result = dictionary.searchStorkeStartsWith(keyMap, word);
			return 1;
		}

		@Override
		protected void onPostExecute(Integer integer) {
			super.onPostExecute(integer);
			if(integer == 1) {

			}
		}

	}

}
