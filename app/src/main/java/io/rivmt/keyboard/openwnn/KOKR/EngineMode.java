package io.rivmt.keyboard.openwnn.KOKR;

import io.rivmt.keyboard.openwnn.KOKR.layout.LayoutSymbol;

import static io.rivmt.keyboard.openwnn.KOKR.layout.Layout12KeyDubul.COMB_DUBUL_12KEY_CHEONJIIN;
import static io.rivmt.keyboard.openwnn.KOKR.layout.Layout12KeyDubul.COMB_DUBUL_12KEY_NARATGEUL;
import static io.rivmt.keyboard.openwnn.KOKR.layout.Layout12KeyDubul.COMB_DUBUL_12KEY_SKY2;
import static io.rivmt.keyboard.openwnn.KOKR.layout.Layout12KeyDubul.CYCLE_DUBUL_12KEY_CHEONJIIN;
import static io.rivmt.keyboard.openwnn.KOKR.layout.Layout12KeyDubul.CYCLE_DUBUL_12KEY_NARATGEUL;
import static io.rivmt.keyboard.openwnn.KOKR.layout.Layout12KeyDubul.CYCLE_DUBUL_12KEY_SKY2;
import static io.rivmt.keyboard.openwnn.KOKR.layout.Layout12KeyDubul.STROKE_DUBUL_12KEY_NARATGEUL;
import static io.rivmt.keyboard.openwnn.KOKR.layout.Layout12KeySebul.COMB_SEBUL_12KEY_HANSON;
import static io.rivmt.keyboard.openwnn.KOKR.layout.Layout12KeySebul.COMB_SEBUL_12KEY_MUNHWA;
import static io.rivmt.keyboard.openwnn.KOKR.layout.Layout12KeySebul.COMB_SEBUL_12KEY_SENA;
import static io.rivmt.keyboard.openwnn.KOKR.layout.Layout12KeySebul.CYCLE_SEBUL_12KEY_HANSON;
import static io.rivmt.keyboard.openwnn.KOKR.layout.Layout12KeySebul.CYCLE_SEBUL_12KEY_MUNHWA;
import static io.rivmt.keyboard.openwnn.KOKR.layout.Layout12KeySebul.CYCLE_SEBUL_12KEY_SENA;
import static io.rivmt.keyboard.openwnn.KOKR.layout.Layout12KeySebul.STROKE_SEBUL_12KEY_HANSON;
import static io.rivmt.keyboard.openwnn.KOKR.layout.Layout12KeySebul.STROKE_SEBUL_12KEY_MUNHWA;
import static io.rivmt.keyboard.openwnn.KOKR.layout.Layout12KeySebul.STROKE_SEBUL_12KEY_SENA;
import static io.rivmt.keyboard.openwnn.KOKR.layout.LayoutAlphabet.CONVERT_ENGLISH_COLEMAK;
import static io.rivmt.keyboard.openwnn.KOKR.layout.LayoutAlphabet.CONVERT_ENGLISH_DVORAK;
import static io.rivmt.keyboard.openwnn.KOKR.layout.LayoutAlphabet.CYCLE_12KEY_ALPHABET_A;
import static io.rivmt.keyboard.openwnn.KOKR.layout.LayoutAlphabet.CYCLE_12KEY_ALPHABET_B;
import static io.rivmt.keyboard.openwnn.KOKR.layout.LayoutAlphabet.CYCLE_12KEY_ALPHABET_SMALLQWERTY;
import static io.rivmt.keyboard.openwnn.KOKR.layout.LayoutDev.COMB_NEBUL_1969;
import static io.rivmt.keyboard.openwnn.KOKR.layout.LayoutDev.JAMO_NEBUL_1969;
import static io.rivmt.keyboard.openwnn.KOKR.layout.LayoutDubul.COMB_DUBUL_DANMOEUM_GOOGLE;
import static io.rivmt.keyboard.openwnn.KOKR.layout.LayoutDubul.COMB_DUBUL_STANDARD;
import static io.rivmt.keyboard.openwnn.KOKR.layout.LayoutDubul.JAMO_DUBUL_DANMOEUM_GOOGLE;
import static io.rivmt.keyboard.openwnn.KOKR.layout.LayoutDubul.JAMO_DUBUL_NK;
import static io.rivmt.keyboard.openwnn.KOKR.layout.LayoutDubul.JAMO_DUBUL_STANDARD;
import static io.rivmt.keyboard.openwnn.KOKR.layout.LayoutDubul.JAMO_DUBUL_YET;
import static io.rivmt.keyboard.openwnn.KOKR.layout.LayoutGongSebul.COMB_FULL;
import static io.rivmt.keyboard.openwnn.KOKR.layout.LayoutGongSebul.COMB_SEBULSIK;
import static io.rivmt.keyboard.openwnn.KOKR.layout.LayoutGongSebul.COMB_SEBUL_DANMOEUM;
import static io.rivmt.keyboard.openwnn.KOKR.layout.LayoutGongSebul.COMB_SEBUL_SUN_2014;
import static io.rivmt.keyboard.openwnn.KOKR.layout.LayoutGongSebul.JAMO_SEBUL_390;
import static io.rivmt.keyboard.openwnn.KOKR.layout.LayoutGongSebul.JAMO_SEBUL_391;
import static io.rivmt.keyboard.openwnn.KOKR.layout.LayoutGongSebul.JAMO_SEBUL_393Y;
import static io.rivmt.keyboard.openwnn.KOKR.layout.LayoutGongSebul.JAMO_SEBUL_DANMOEUM;
import static io.rivmt.keyboard.openwnn.KOKR.layout.LayoutGongSebul.JAMO_SEBUL_SUN_2014;
import static io.rivmt.keyboard.openwnn.KOKR.layout.LayoutMoachigiSebul.COMB_SEBUL_AHNMATAE;
import static io.rivmt.keyboard.openwnn.KOKR.layout.LayoutMoachigiSebul.COMB_SEBUL_SEMOE;
import static io.rivmt.keyboard.openwnn.KOKR.layout.LayoutMoachigiSebul.JAMO_SEBUL_AHNMATAE;
import static io.rivmt.keyboard.openwnn.KOKR.layout.LayoutMoachigiSebul.JAMO_SEBUL_SEMOE_2016;
import static io.rivmt.keyboard.openwnn.KOKR.layout.LayoutShinSebul.COMB_SEBUL_3_2015;
import static io.rivmt.keyboard.openwnn.KOKR.layout.LayoutShinSebul.COMB_SEBUL_3_P3;
import static io.rivmt.keyboard.openwnn.KOKR.layout.LayoutShinSebul.COMB_SEBUL_SHIN_ORIGINAL;
import static io.rivmt.keyboard.openwnn.KOKR.layout.LayoutShinSebul.JAMOSET_SEBUL_3_2015;
import static io.rivmt.keyboard.openwnn.KOKR.layout.LayoutShinSebul.JAMOSET_SEBUL_3_2015M;
import static io.rivmt.keyboard.openwnn.KOKR.layout.LayoutShinSebul.JAMOSET_SEBUL_3_P3;
import static io.rivmt.keyboard.openwnn.KOKR.layout.LayoutShinSebul.JAMOSET_SHIN_EDIT;
import static io.rivmt.keyboard.openwnn.KOKR.layout.LayoutShinSebul.JAMOSET_SHIN_M;
import static io.rivmt.keyboard.openwnn.KOKR.layout.LayoutShinSebul.JAMOSET_SHIN_ORIGINAL;
import static io.rivmt.keyboard.openwnn.KOKR.layout.LayoutShinSebul.JAMOSET_SHIN_P2;
import static io.rivmt.keyboard.openwnn.KOKR.layout.LayoutShinSebul.JAMO_SEBUL_3_2015Y;

public enum EngineMode {

	DIRECT(null, null, null, null, null, null),

	SEBUL_390		(new Properties(), JAMO_SEBUL_390, null, COMB_SEBULSIK, null, "keyboard_sebul_390"),
	SEBUL_391		(new Properties(), JAMO_SEBUL_391, null, COMB_SEBULSIK, null, "keyboard_sebul_391"),
	SEBUL_DANMOEUM	(new Properties(), JAMO_SEBUL_DANMOEUM, null, COMB_SEBUL_DANMOEUM, null, "keyboard_sebul_danmoeum"),
	DUBULSIK		(new Properties(), JAMO_DUBUL_STANDARD, null, COMB_DUBUL_STANDARD, null, "keyboard_dubul_standard"),
	DUBULSIK_NK		(new Properties(), JAMO_DUBUL_NK, null, COMB_DUBUL_STANDARD, null, "keyboard_dubul_nk"),

	SEBUL_SUN_2014		(new Properties(), JAMO_SEBUL_SUN_2014, null, COMB_SEBUL_SUN_2014, null, "keyboard_sebul_sun_2014"),
	SEBUL_3_2015M		(new Properties(), null, JAMOSET_SEBUL_3_2015M, COMB_SEBUL_3_2015, null, "keyboard_sebul_3_2015m"),
	SEBUL_3_2015		(new Properties(), null, JAMOSET_SEBUL_3_2015, COMB_SEBUL_3_2015, null, "keyboard_sebul_3_2015"),
	SEBUL_3_P3			(new Properties(), null, JAMOSET_SEBUL_3_P3, COMB_SEBUL_3_P3, null, "keyboard_sebul_3_p3"),
	SEBUL_SHIN_ORIGINAL	(new Properties(), null, JAMOSET_SHIN_ORIGINAL, COMB_SEBUL_SHIN_ORIGINAL, null, "keyboard_sebul_shin_original"),
	SEBUL_SHIN_EDIT		(new Properties(), null, JAMOSET_SHIN_EDIT, COMB_SEBUL_SHIN_ORIGINAL, null, "keyboard_sebul_shin_edit"),
	SEBUL_SHIN_M		(new Properties(), null, JAMOSET_SHIN_M, COMB_SEBUL_SHIN_ORIGINAL, null, "keyboard_sebul_shin_m"),
	SEBUL_SHIN_P2		(new Properties(), null, JAMOSET_SHIN_P2, COMB_FULL, null, "keyboard_sebul_shin_p2"),

	SEBUL_AHNMATAE	(new Properties(true, false, false, false),
			JAMO_SEBUL_AHNMATAE, null, COMB_SEBUL_AHNMATAE, null, "keyboard_sebul_ahnmatae"),
	SEBUL_SEMOE_2016(new Properties(true, false, false, false),
			JAMO_SEBUL_SEMOE_2016, null, COMB_SEBUL_SEMOE, null, "keyboard_sebul_semoe_2016"),
	SEBUL_SEMOE		(new Properties(true, false, false, false),
			JAMO_SEBUL_SEMOE_2016, null, COMB_SEBUL_SEMOE, null, "keyboard_sebul_semoe"),

	NEBUL_1969(new Properties(), JAMO_NEBUL_1969, null, COMB_NEBUL_1969, null, "keyboard_nebul_1969"),

	DUBULSIK_YET	(new Properties(), JAMO_DUBUL_YET, null, COMB_FULL, null, "keyboard_dubul_yet"),
	SEBUL_393Y		(new Properties(), JAMO_SEBUL_393Y, null, COMB_FULL, null, "keyboard_sebul_393y"),
	SEBUL_3_2015Y		(new Properties(), JAMO_SEBUL_3_2015Y, null, COMB_FULL, null, "keyboard_sebul_3_2015y"),

	TWELVE_ALPHABET_A			(new Properties(DefaultSoftKeyboardKOKR.LANG_EN, false, false, false, false, true, true),
			CYCLE_12KEY_ALPHABET_A, null, null, null, "keyboard_12key_alphabet_wide_a", "keyboard_12key_alphabet_narrow_a"),
	TWELVE_ALPHABET_B			(new Properties(DefaultSoftKeyboardKOKR.LANG_EN, false, false, false, false, true, true),
			CYCLE_12KEY_ALPHABET_B, null, null, null, "keyboard_12key_alphabet_wide_b", "keyboard_12key_alphabet_narrow_b"),
	TWELVE_ALPHABET_SMALLQWERTY	(new Properties(DefaultSoftKeyboardKOKR.LANG_EN, false, false, false, false, true, true),
			CYCLE_12KEY_ALPHABET_SMALLQWERTY, null, null, null, "keyboard_12key_alphabet_smallqwerty"),

	TWELVE_SEBUL_MUNHWA					(new Properties(true, false, false),
			CYCLE_SEBUL_12KEY_MUNHWA, null, COMB_SEBUL_12KEY_MUNHWA, STROKE_SEBUL_12KEY_MUNHWA, "keyboard_12key_sebul_munhwa"),
	TWELVE_SEBUL_HANSON					(new Properties(true, false, false),
			CYCLE_SEBUL_12KEY_HANSON, null, COMB_SEBUL_12KEY_HANSON, STROKE_SEBUL_12KEY_HANSON, "keyboard_12key_sebul_hanson"),
	TWELVE_SEBUL_SENA					(new Properties(true, false, false),
			CYCLE_SEBUL_12KEY_SENA, null, COMB_SEBUL_12KEY_SENA, STROKE_SEBUL_12KEY_SENA, "keyboard_12key_sebul_sena"),
	TWELVE_DUBUL_CHEONJIIN				(new Properties(true, false, true),
			CYCLE_DUBUL_12KEY_CHEONJIIN, null, COMB_DUBUL_12KEY_CHEONJIIN, null, "keyboard_12key_dubul_cheonjiin"),
	TWELVE_DUBUL_NARATGEUL				(new Properties(true, false, false),
			CYCLE_DUBUL_12KEY_NARATGEUL, null, COMB_DUBUL_12KEY_NARATGEUL, STROKE_DUBUL_12KEY_NARATGEUL, "keyboard_12key_dubul_naratgeul"),
	TWELVE_DUBUL_SKY2					(new Properties(true, false, true),
			CYCLE_DUBUL_12KEY_SKY2, null, COMB_DUBUL_12KEY_SKY2, null, "keyboard_12key_dubul_sky2"),
	TWELVE_DUBUL_DANMOEUM				(new Properties(true, false, true),
			JAMO_DUBUL_DANMOEUM_GOOGLE, null, COMB_DUBUL_DANMOEUM_GOOGLE, null, "keyboard_dubul_danmoeum_google"),

	ENGLISH_QWERTY	(new Properties(DefaultSoftKeyboardKOKR.LANG_EN, false, false, false, false, false, false),
			null, null, null, null, "keyboard_alphabet_qwerty"),
	ENGLISH_DVORAK	(new Properties(DefaultSoftKeyboardKOKR.LANG_EN, false, false, false, false, false, false),
			CONVERT_ENGLISH_DVORAK, null, null, null, "keyboard_alphabet_dvorak"),
	ENGLISH_COLEMAK	(new Properties(DefaultSoftKeyboardKOKR.LANG_EN, false, false, false, false, false, false),
			CONVERT_ENGLISH_COLEMAK, null, null, null, "keyboard_alphabet_colemak"),

	SYMBOLS_A(new Properties(true, false, false, false, false, false),
			LayoutSymbol.SYMBOL_A, null, null, null, "keyboard_symbols_a"),
	SYMBOLS_B(new Properties(true, false, false, false, false, false),
			LayoutSymbol.SYMBOL_B, null, null, null, "keyboard_symbols_b");

	public Properties properties;
	public int[][] layout, combination, addStroke;
	public int[][][] jamoset;
	private String[] prefValues;

	EngineMode(Properties properties, int[][] layout, int[][][] jamoset, int[][] combination, int[][] addStroke, String... prefValues) {
		this.properties = properties;
		this.layout = layout;
		this.jamoset = jamoset;
		this.combination = combination;
		this.addStroke = addStroke;
		this.prefValues = prefValues;
	}

	public String[] getPrefValues() {
		return prefValues;
	}

	public static EngineMode get(String defaultLayout) {
		for(EngineMode mode : EngineMode.values()) {
			if(mode.getPrefValues() == null) continue;
			for(String prefValue : mode.getPrefValues()) {
				if(prefValue.equals(defaultLayout)) {
					return mode;
				}
			}
		}
		return EngineMode.DIRECT;
	}

	public static class Properties {
		public final int languageCode;
		public final boolean altMode, direct, predictive, fullMoachigi, twelveEngine, timeout;

		public Properties(int languageCode, boolean altMode, boolean direct, boolean predictive,
						  boolean fullMoachigi, boolean twelveEngine, boolean timeout) {
			this.languageCode = languageCode;
			this.altMode = altMode;
			this.direct = direct;
			this.predictive = predictive;
			this.fullMoachigi = fullMoachigi;
			this.twelveEngine = twelveEngine;
			this.timeout = timeout;
		}

		public Properties(boolean altMode, boolean direct, boolean predictive, boolean fullMoachigi, boolean twelveEngine, boolean timeout) {
			this(DefaultSoftKeyboardKOKR.LANG_KO, altMode, direct, predictive, fullMoachigi, twelveEngine, timeout);
		}

		public Properties(boolean direct, boolean predictive, boolean fullMoachigi, boolean twelveEngine, boolean timeout) {
			this(false, direct, predictive, fullMoachigi, twelveEngine, timeout);
		}

		public Properties(boolean predictive, boolean fullMoachigi, boolean twelveEngine, boolean timeout) {
			this(false, fullMoachigi, predictive, twelveEngine, timeout);
		}

		public Properties(boolean twelveEngine, boolean predictive, boolean timeout) {
			this(false, predictive, false, twelveEngine, timeout);
		}

		public Properties() {
			this(false, false, false, false, false);
		}

	}
}
