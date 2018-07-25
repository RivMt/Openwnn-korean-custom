package me.blog.hgl1002.openwnn.KOKR.trie;

public enum KoreanPOS {

	POS_UNKNOWN(-100, -1),

	POS_NOUN(101, 1, "words/noun/nouns.txt"),
	POS_ABBREVIATION(102, 2),
	POS_NOUN_PROPER(103, 3, "words/noun/entities.txt"),
	POS_NOUN_NAME(104, 4, "words/substantives/given_names.txt"),
	POS_NOUN_SURNAME(105, 5, "words/substantives/family_names.txt"),
	POS_NOUN_ORGANIZATION(106, 6, "words/noun/company_names.txt"),
	POS_NOUN_GEOLOCATION(107, 7, "words/noun/geolocations.txt"),
	POS_NOUN_ETC(108, 8, "words/noun/foreign.txt"),
	POS_NOUN_PREFIX(109, 9, "words/noun/substantives/noun_prefix.txt"),
	POS_SUFFIX(110, 10, "words/substantives/suffix.txt"),
	POS_NOUN_USER(199, 11),

	POS_ADJECTIVE(200, 12, "words/adjective/adjective.txt"),
	POS_ADVERB(210, 13, "words/adverb/adverb.txt"),

	POS_VERB(300, 14, "words/verb/verb.txt"),
	POS_VERB_PREFIX(310, 15, "words/verb/verb_prefix.txt"),

	POS_CONJUNCTION(410, 16, "words/auxiliary/conjunctions.txt"),
	POS_DETERMINER(420, 17, "words/auxiliary/determiner.txt"),
	POS_EXCLAMATION(430, 18, "words/auxiliary/exclamation.txt"),

	POS_JOSA(500, 19, "words/josa/josa.txt"),
	POS_EOMI(510, 20, "words/verb/eomi.txt"),
	POS_PRE_EOMI(515, 21, "words/verb/pre_eomi.txt"),

	POS_SPACE(600, -1);

	private final int code;
	private final int dictionaryIndex;
	private final String[] dictionaryFiles;

	KoreanPOS(int code, int dictionaryIndex, String... dictionaryFile) {
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

	public static KoreanPOS get(int code) {
		for(KoreanPOS pos : values()) {
			if(pos.code == code) return pos;
		}
		return POS_UNKNOWN;
	}

}
