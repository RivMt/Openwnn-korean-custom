package me.blog.hgl1002.openwnn.KOKR;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class T9DictionaryGenerator {

	private static final String COMPAT_CHO = "ㄱㄲㄳㄴㄵㄶㄷㄸㄹㄺㄻㄼㄽㄾㄿㅀㅁㅂㅃㅄㅅㅆㅇㅈㅉㅊㅋㅌㅍㅎ";

	private static final String CONVERT_CHO = "ᄀᄁ ᄂ  ᄃᄄᄅ       ᄆᄇᄈ ᄉᄊᄋᄌᄍᄎᄏᄐᄑᄒ";
	private static final String CONVERT_JONG = "ᆨᆩᆪᆫᆬᆭᆮ ᆯᆰᆱᆲᆳᆴᆵᆶᆷᆸ ᆹᆺᆻᆼᆽ ᆾᆿᇀᇁᇂ";

	private static final List<Character> DOUBLE_JUNGS = new ArrayList<Character>() {{
		add('ᅪ');
		add('ᅫ');
		add('ᅬ');
		add('ᅯ');
		add('ᅰ');
		add('ᅱ');
		add('ᅴ');
	}};

	public static void generate(InputStream is, SQLiteDatabase database, String tableSuffix, EngineMode mode) {
		new GenerateTask(is, database, tableSuffix, mode).execute();
	}

	private static class GenerateTask extends AsyncTask<Void, Void, Integer> {
		private InputStream is;
		private SQLiteDatabase database;
		private String tableSuffix;
		private EngineMode mode;

		public GenerateTask(InputStream is, SQLiteDatabase database, String tableSuffix, EngineMode mode) {
			this.is = is;
			this.database = database;
			this.tableSuffix = tableSuffix;
			this.mode = mode;
		}

		@Override
		protected Integer doInBackground(Void... voids) {
			generate(is, database, tableSuffix, mode);
			return 1;
		}

		public static void generate(InputStream is, SQLiteDatabase database, String tableSuffix, EngineMode mode) {
			String tableName = mode.getPrefValues()[0];

			Map<Character, String> map = new HashMap<>();

			for(int[] item : mode.layout) {
				char sourceChar = ' ';
				if(item[0] <= -2000 && item[0] > -2100) {
					sourceChar = (char) (-item[0] - 2000 + 0x30);
				} else if(item[0] <= -200 && item[0] > -300) {
					sourceChar = (char) (-item[0] - 200 + 0x30);
				} else continue;
				if(sourceChar == 0x30 + 10) sourceChar = '0';
				if(sourceChar == 0x30 + 11) sourceChar = '*';
				if(sourceChar == 0x30 + 12) sourceChar = '#';

				for(int i = 1 ; i < item.length ; i++) {
					char ch = (char) item[i];
					appendJamo(map, Character.toString(sourceChar), ch);
				}
			}

			if(mode.addStroke != null) {
				for(int[] item : mode.addStroke) {
					char strokeSource  = (char) item[0];
					String source  = map.get(strokeSource);
					for(int i = 1 ; i < item.length ; i++) {
						char ch = (char) item[i];
						appendJamo(map, source, ch);
					}
				}
			}

			if(mode.combination != null) {
				for(int[] item : mode.combination) {
					char a = (char) item[0];
					char b = (char) item[1];
					char result = (char) item[2];
					if(!map.containsKey(a) || !map.containsKey(b)) continue;
					String source;
					if(DOUBLE_JUNGS.contains(result)) source = map.get(a) + map.get(b);
					else source = map.get(a);
					appendJamo(map, source, result);
				}
			}

			String query = "create table if not exists `" + tableName + tableSuffix + "` ("
					+ "`word` text not null, "
					+ "`keys` text not null)";
			database.execSQL(query, new String[] {});

			query = "select * from `" + tableName + tableSuffix + "`";
			Cursor cursor = database.rawQuery(query, new String[] {});
			if(cursor.moveToNext()) return;
			cursor.close();

			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line;
			try {
				database.beginTransaction();
				while((line = br.readLine()) != null) {
					ContentValues values = new ContentValues();
					values.put("word", line);
					StringBuilder keys = new StringBuilder();
					for(char ch : Normalizer.normalize(line, Normalizer.Form.NFD).toCharArray()) {
						String source = map.get(ch);
						if(source != null) keys.append(source);
					}
					values.put("keys", keys.toString());
					database.insertWithOnConflict(tableName + tableSuffix, null, values, SQLiteDatabase.CONFLICT_IGNORE);
				}
				database.setTransactionSuccessful();
				database.endTransaction();
			} catch(IOException ex) {
				ex.printStackTrace();
			}
		}

		private static void appendJamo(Map<Character, String> map, String source, char jamo) {
			if(jamo >= 'ㄱ' && jamo <= 'ㅎ') {
				char cho = CONVERT_CHO.charAt(COMPAT_CHO.indexOf(jamo));
				char jong = CONVERT_JONG.charAt(COMPAT_CHO.indexOf(jamo));
				if(cho != ' ') {
					map.put(cho, source);
					switch(cho) {
					case 0x1100:
					case 0x1103:
					case 0x1107:
					case 0x1109:
					case 0x110c:
						map.put((char) (cho+1), source);
					}
				}
				if(jong != ' ') {
					map.put(jong, source);
					switch(jong) {
					case 0x11a8:
					case 0x11ba:
						map.put((char) (jong+1), source);
					}
				}
			} else if(jamo >= 'ㅏ' && jamo <= 'ㅣ') {
				char jung = (char) (jamo - 0x314f + 0x1161);
				map.put(jung, source);
			} else if(jamo == '\u318d') {			// 아래아
				map.put('\u119e', source);
			} else {
				if(jamo >= 'a' && jamo <= 'z') map.put(Character.toUpperCase(jamo), source);
				map.put(jamo, source);
			}
		}

	}

}
