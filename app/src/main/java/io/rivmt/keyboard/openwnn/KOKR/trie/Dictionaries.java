package io.rivmt.keyboard.openwnn.KOKR.trie;

import android.os.AsyncTask;
import android.os.Build;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Dictionaries {

	private static TrieDictionary[][] dictionaries = new TrieDictionary[5][10];

	public static TrieDictionary getDictionary(int language, int index) {
		try {
			return dictionaries[language][index];
		} catch(ArrayIndexOutOfBoundsException ex) {
			return null;
		}
	}

	public static void setDictionary(int language, int index, TrieDictionary dictionary) {
		dictionaries[language][index] = dictionary;
	}

	public static TrieDictionary generate(int language, int index, InputStream words) {
		return generate(language, index, words, true);
	}

	public static TrieDictionary generate(int language, int index, InputStream words, boolean force) {
		if(force || dictionaries[language][index] == null || dictionaries[language][index].isEmpty()) {
			new DictionaryGenerateTask(words, dictionaries[language][index]).execute();
		}
		return dictionaries[language][index];
	}

	static class DictionaryGenerateTask extends AsyncTask<Void, Void, Integer> {

		private InputStream is;
		private TrieDictionary dictionary;

		public DictionaryGenerateTask(InputStream is, TrieDictionary dictionary) {
			this.is = is;
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
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			try {
				dictionary.setReady(false);
				String line;
				int i = Integer.MAX_VALUE;
				while((line = br.readLine()) != null) {
					dictionary.insert(line, i--);
				}
				if(dictionary instanceof Compressable) ((Compressable) dictionary).compress();
				dictionary.setReady(true);
				return 1;
			} catch(IOException ex) {
				ex.printStackTrace();
			}
			return -1;
		}

		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
		}
	}

}
