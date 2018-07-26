package me.blog.hgl1002.openwnn.KOKR.layout;

import me.blog.hgl1002.openwnn.KOKR.DefaultSoftKeyboardKOKR;

public class Layout12KeyDubul {

	public static final int[][] CYCLE_DUBUL_12KEY_CHEONJIIN = {
			{-2001, 0x3163},
			{-2002, 0x01318d},
			{-2003, 0x3161},
			{-2004, 0x3131, 0x314b, 0x3132},
			{-2005, 0x3134, 0x3139},
			{-2006, 0x3137, 0x314c, 0x3138},
			{-2007, 0x3142, 0x314d, 0x3143},
			{-2008, 0x3145, 0x314e, 0x3146},
			{-2009, 0x3148, 0x314a, 0x3149},
			{-2010, 0x3147, 0x3141},
			{-2011, 0x2e, 0x2c, 0x3f, 0x21},

			// flick(플릭) 동작에 의한 자모 정의
			{-2301, 0x3153},
			{-2401, 0x314f},
			{-2103, 0x3157},
			{-2203, 0x315c},
	};

	public static final int[][] COMB_DUBUL_12KEY_CHEONJIIN = {

			{0x1175, 0x01119e, 0x1161},	// ㅏ
			{0x1161, 0x1175, 0x1162},	// ㅐ
			{0x1161, 0x01119e, 0x1163},	// ㅑ
			{0x1163, 0x1175, 0x1164},	// ㅒ
			{0x01119e, 0x01119e, 0x0111a2},	// ᆢ
			{0x01119e, 0x1175, 0x1165},	// ㅓ
			{0x1165, 0x1175, 0x1166},	// ㅔ
			{0x0111a2, 0x1175, 0x1167},	// ㅕ (··+ㅣ)
			{0x01119e, 0x1165, 0x1167},	// ㅕ (·+ㅓ)
			{0x1165, 0x01119e, 0x1167},	// ㅕ (ㅓ+·)
			{0x1167, 0x1175, 0x1168},	// ㅖ
			{0x01119e, 0x1173, 0x1169},	// ㅗ
			{0x1169, 0x1162, 0x116b},	// ㅙ (ㅗ+ㅐ)
			{0x116a, 0x1175, 0x116b},	// ㅙ
			{0x1169, 0x1161, 0x116a},	// ㅘ (ㅗ+ㅏ)
			{0x116c, 0x01119e, 0x116a},	// ㅘ (ㅚ+·)
			{0x1169, 0x1175, 0x116c},	// ㅚ
			{0x0111a2, 0x1173, 0x116d},	// ㅛ (··+ㅡ)
			{0x01119e, 0x1169, 0x116d},	// ㅛ (·+ㅗ)
			{0x1169, 0x01119e, 0x116d},	// ㅛ (ㅗ+·)
			{0x1173, 0x01119e, 0x116e},	// ㅜ
			{0x116e, 0x1166, 0x1170},	// ㅞ (ㅜ+ㅔ)
			{0x116f, 0x1175, 0x1170},	// ㅞ
			{0x1172, 0x1175, 0x116f},	// ㅝ (ㅠ+ㅣ)
			{0x116e, 0x1165, 0x116f},	// ㅝ (ㅜ+ㅓ)
			{0x116e, 0x1175, 0x1171},	// ㅟ
			{0x116e, 0x01119e, 0x1172},	// ㅠ
			{0x1173, 0x1175, 0x1174},	// ㅢ

			{0x11a8, 0x11a8, 0x11a9},	// ㄲ
			{0x11a8, 0x11ba, 0x11aa},	// ㄳ
			{0x11ab, 0x11bd, 0x11ac},	// ㄵ
			{0x11ab, 0x11ba, 0x11ad},	// ㄶ
			{0x11ab, 0x11c2, 0x11ad},	// ㄶ
			{0x11af, 0x11a8, 0x11b0},	// ㄺ
			{0x11af, 0x11bc, 0x11b1},	// ㄻ
			{0x11af, 0x11b8, 0x11b2},	// ㄼ
			{0x11af, 0x11ba, 0x11b3},	// ㄽ
			{0x11af, 0x11ae, 0x11b4},	// ㄾ
			{0x11af, 0x11c1, 0x11b5},	// ㄿ
			{0x11af, 0x11c2, 0x11b6},	// ㅀ
			{0x11b8, 0x11ba, 0x11b9},	// ㅄ
			{0x11ba, 0x11ba, 0x11bb},	// ㅆ

	};

	public static final int[][] CYCLE_DUBUL_12KEY_NARATGEUL = {
			{-2001, 0x3131},
			{-2002, 0x3134},
			{-2003, 0x314f, 0x3153},
			{-2004, 0x3139},
			{-2005, 0x3141},
			{-2006, 0x3157, 0x315c},
			{-2007, 0x3145},
			{-2008, 0x3147},
			{-2009, 0x3163},
			{-2010, 0x3161},
			{-2011, DefaultSoftKeyboardKOKR.KEYCODE_KR12_ADDSTROKE},
			{-2012, DefaultSoftKeyboardKOKR.KEYCODE_KR12_ADDSTROKE-1},

	};

	public static final int[][] STROKE_DUBUL_12KEY_NARATGEUL = {
			{0x1100, 0x110f},
			{0x1102, 0x1103, 0x1110},
			{0x1106, 0x1107, 0x1111},
			{0x1109, 0x110c, 0x110e},
			{0x110b, 0x1112},

			{0x1161, 0x3151},
			{0x1165, 0x3155},
			{0x1169, 0x315b},
			{0x116e, 0x3160},

			{0x11a8, 0x314b},
			{0x11ab, 0x3137, 0x314c},
			{0x11b7, 0x3142, 0x314d},
			{0x11ba, 0x3148, 0x314a},
			{0x11bc, 0x314e},

	};

	// ㄾ같이 획추가가 필요한 경우, ㄹ+ㄴ, ㄹ+ㄷ, ㄹ+ㅌ 모두 넣어주어야 한다.
	public static final int[][] COMB_DUBUL_12KEY_NARATGEUL = {

			{0x1161, 0x1175, 0x1162},
			{0x1163, 0x1175, 0x1164},
			{0x1165, 0x1175, 0x1166},
			{0x1167, 0x1175, 0x1168},

			{0x1169, 0x1162, 0x116b},	// ㅙ (ㅗ+ㅐ)
			{0x116a, 0x1175, 0x116b},	// ㅙ (ㅘ+ㅣ)
			{0x1169, 0x1161, 0x116a},	// ㅘ
			{0x1169, 0x1175, 0x116c},	// ㅚ
			{0x116e, 0x1166, 0x1170},	// ㅞ (ㅜ+ㅔ)
			{0x116f, 0x1175, 0x1170},	// ㅞ (ㅝ+ㅣ)
			{0x116e, 0x1161, 0x116f},	// ㅝ (ㅜ+ㅏ)
			{0x116e, 0x1165, 0x116f},	// ㅝ (ㅜ+ㅓ)
			{0x116e, 0x1175, 0x1171},	// ㅟ
			{0x1173, 0x1175, 0x1174},

			{0x11a8, 0x11a8, 0x11a9},	// ㄲ
			{0x11a8, 0x11ba, 0x11aa},	// ㄳ
			{0x11ab, 0x11ba, 0x11ac},	// ㄵ
			{0x11ab, 0x11bd, 0x11ac},	// ㄵ
			{0x11ab, 0x11bc, 0x11ad},	// ㄶ
			{0x11ab, 0x11c2, 0x11ad},	// ㄶ
			{0x11af, 0x11a8, 0x11b0},	// ㄺ
			{0x11af, 0x11b7, 0x11b1},	// ㄻ
			{0x11af, 0x11b8, 0x11b2},	// ㄼ
			{0x11af, 0x11ba, 0x11b3},	// ㄽ
			{0x11af, 0x11ab, 0x11b4},	// ㄾ
			{0x11af, 0x11ae, 0x11b4},	// ㄾ
			{0x11af, 0x11c0, 0x11b4},	// ㄾ
			{0x11af, 0x11c1, 0x11b5},	// ㄿ
			{0x11af, 0x11bc, 0x11b6},	// ㅀ
			{0x11af, 0x11c2, 0x11b6},	// ㅀ
			{0x11b8, 0x11ba, 0x11b9},	// ㅄ
			{0x11ba, 0x11ba, 0x11bb},	// ㅆ
	};

	public static final int[][] CYCLE_PREDICTIVE = {
			{-2001, '1'},
			{-2002, '2'},
			{-2003, '3'},
			{-2004, '4'},
			{-2005, '5'},
			{-2006, '6'},
			{-2007, '7'},
			{-2008, '8'},
			{-2009, '9'},
			{-2010, '0'},
			{-2011, '-'},
			{-2012, '='},

	};

	public static final int[][] CYCLE_DUBUL_12KEY_SKY2 = {
			{-2001, 0x3131, 0x314b, 0x3132},
			{-2002, 0x3163, 0x3161, 0x3162},
			{-2003, 0x314f, 0x3151},
			{-2004, 0x3137, 0x314c, 0x3138},
			{-2005, 0x3134, 0x3139},
			{-2006, 0x3153, 0x3155},
			{-2007, 0x3141, 0x3145, 0x3146},
			{-2008, 0x3142, 0x314d, 0x3143},
			{-2009, 0x3157, 0x315b},
			{-2011, 0x3148, 0x314a, 0x3149},
			{-2010, 0x3147, 0x314e},
			{-2012, 0x315c, 0x3160},

	};

	public static final int[][] COMB_DUBUL_12KEY_SKY2 = {

			{0x1161, 0x1175, 0x1162},
			{0x1163, 0x1175, 0x1164},
			{0x1165, 0x1175, 0x1166},
			{0x1167, 0x1175, 0x1168},

			{0x1169, 0x1162, 0x116b},	// ㅙ (ㅗ+ㅐ)
			{0x1169, 0x1161, 0x116a},	// ㅘ
			{0x116a, 0x1175, 0x116b},	// ㅙ (ㅘ+ㅣ)
			{0x1169, 0x1175, 0x116c},	// ㅚ
			{0x116e, 0x1166, 0x1170},	// ㅞ (ㅜ+ㅔ)
			{0x116e, 0x1161, 0x116f},	// ㅝ (ㅜ+ㅏ)
			{0x116e, 0x1165, 0x116f},	// ㅝ (ㅜ+ㅓ)
			{0x116f, 0x1175, 0x1170},	// ㅞ (ㅝ+ㅣ)
			{0x116e, 0x1175, 0x1171},	// ㅟ

			{0x11a8, 0x11b7, 0x11aa},	// ㄳ
			{0x11a8, 0x11ba, 0x11aa},	// ㄳ
			{0x11ab, 0x11bd, 0x11ac},	// ㄵ
			{0x11ab, 0x11bc, 0x11ad},	// ㄶ
			{0x11ab, 0x11c2, 0x11ad},	// ㄶ
			{0x11af, 0x11a8, 0x11b0},	// ㄺ
			{0x11af, 0x11b7, 0x11b1},	// ㄻ
			{0x11af, 0x11b8, 0x11b2},	// ㄼ
			{0x11af, 0x11ba, 0x11b3},	// ㄽ
			{0x11af, 0x11ae, 0x11b4},	// ㄾ
			{0x11af, 0x11c0, 0x11b4},	// ㄾ
			{0x11af, 0x11c1, 0x11b5},	// ㄿ
			{0x11af, 0x11bc, 0x11b6},	// ㅀ
			{0x11af, 0x11c2, 0x11b6},	// ㅀ
			{0x11b8, 0x11b7, 0x11b9},	// ㅄ
			{0x11b8, 0x11ba, 0x11b9},	// ㅄ
	};

}
