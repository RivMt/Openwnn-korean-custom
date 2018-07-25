package me.blog.hgl1002.openwnn.KOKR.trie;

public enum KoreanPOS {

	POS_UNKNOWN(-100),

	POS_NOUN(101),
	POS_ABBREVIATION(102),
	POS_NOUN_PROPER(103),
	POS_NOUN_NAME(104),
	POS_NOUN_SURNAME(105),
	POS_NOUN_ORGANIZATION(106),
	POS_NOUN_GEOLOCATION(107),
	POS_NOUN_ETC(108),
	POS_NOUN_PREFIX(109),
	POS_SUFFIX(110),
	POS_NOUN_USER(199),

	POS_ADJECTIVE(200),
	POS_ADVERB(210),

	POS_VERB(300),
	POS_VERB_PREFIX(310),

	POS_CONJUNCTION(410),
	POS_DETERMINER(420),
	POS_EXCLAMATION(430),

	POS_JOSA(500),
	POS_EOMI(510),
	POS_PRE_EOMI(515),

	POS_SPACE(600);

	private int code;

	KoreanPOS(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public static KoreanPOS get(int code) {
		for(KoreanPOS pos : values()) {
			if(pos.code == code) return pos;
		}
		return POS_UNKNOWN;
	}

}
