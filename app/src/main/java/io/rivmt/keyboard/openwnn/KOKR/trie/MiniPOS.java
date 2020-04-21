package io.rivmt.keyboard.openwnn.KOKR.trie;

public enum MiniPOS {

	UNKNOWN(-100, -1),
	SPACE(100, -1),

	SUBSTANTIVE(200, 1, "words/korean/substantive.txt"),
	PREDICATE(300, 2, "words/korean/predicate.txt"),

	JOSA(400, 3, "words/korean/josa.txt"),
	EOMI(410, 4, "words/korean/eomi.txt"),
	PRE_EOMI(415, 5, "words/korean/preeomi.txt"),

	MODIFIER(500, 6, "words/korean/modifier.txt"),
	INDEPENDENT(550, 7, "words/korean/independent.txt");

	private final int code;
	private final int dictionaryIndex;
	private final String[] dictionaryFiles;

	MiniPOS(int code, int dictionaryIndex, String... dictionaryFile) {
		this.code = code;
		this.dictionaryIndex = dictionaryIndex;
		this.dictionaryFiles = dictionaryFile;
	}

	public int getCode() {
		return code;
	}

	public int getDictionaryIndex() {
		return dictionaryIndex;
	}

	public String[] getDictionaryFiles() {
		return dictionaryFiles;
	}

	public static MiniPOS get(int code) {
		for(MiniPOS pos : values()) {
			if(pos.code == code) return pos;
		}
		return UNKNOWN;
	}

	public static class Word extends TrieDictionary.Word {

		private MiniPOS pos;

		public Word(String word, String stroke, int frequency, MiniPOS pos) {
			super(word, stroke, frequency);
			this.pos = pos;
		}

		public MiniPOS getPos() {
			return pos;
		}

		public void setPos(MiniPOS pos) {
			this.pos = pos;
		}
	}

}
