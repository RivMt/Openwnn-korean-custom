package me.blog.hgl1002.openwnn;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.method.MetaKeyKeyListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputMethodManager;
import me.blog.hgl1002.openwnn.KOKR.DefaultSoftKeyboardKOKR;
import me.blog.hgl1002.openwnn.KOKR.HangulEngine;
import me.blog.hgl1002.openwnn.KOKR.TwelveHangulEngine;
import me.blog.hgl1002.openwnn.KOKR.HangulEngine.*;

public class OpenWnnKOKR extends OpenWnn implements HangulEngineListener {

	public static final int[][] SHIFT_CONVERT = {
			{0x60, 0x7e},
			{0x31, 0x21},
			{0x32, 0x40},
			{0x33, 0x23},
			{0x34, 0x24},
			{0x35, 0x25},
			{0x36, 0x5e},
			{0x37, 0x26},
			{0x38, 0x2a},
			{0x39, 0x28},
			{0x30, 0x29},
			{0x2d, 0x5f},
			{0x3d, 0x2b},
			
			{0x5b, 0x7b},
			{0x5d, 0x7d},
			{0x5c, 0x7c},
			
			{0x3b, 0x3a},
			{0x27, 0x22},
			
			{0x2c, 0x3c},
			{0x2e, 0x3e},
			{0x2f, 0x3f},
	};
	
	public static final int[][] JAMO_DUBUL_STANDARD = {
			{49,0x31,0x21},
			{50,0x32,0x40},
			{51,0x33,0x23},
			{52,0x34,0x24},
			{53,0x35,0x25},
			{54,0x36,0x5e},
			{55,0x37,0x26},
			{56,0x38,0x2a},
			{57,0x39,0x28},
			{48,0x30,0x29},
			
			{113, 0x3142, 0x3143},		// q
			{119, 0x3148, 0x3149},		// w
			{101, 0x3137, 0x3138},		// e
			{114, 0x3131, 0x3132},		// r
			{116, 0x3145, 0x3146},		// t
			{121, 0x315b, 0x315b},		// y
			{117, 0x3155, 0x3155},		// u
			{105, 0x3151, 0x3151},		// i
			{111, 0x3150, 0x3152},		// o
			{112, 0x3154, 0x3156},			// p
			
			{97, 0x3141, 0x3141},		// a
			{115, 0x3134, 0x3134},		// s
			{100, 0x3147, 0x3147},		// d
			{102, 0x3139, 0x3139},		// f
			{103, 0x314e, 0x314e},		// g
			{104, 0x3157, 0x3157},		// h
			{106, 0x3153, 0x3153},		// j
			{107, 0x314f, 0x314f},		// k
			{108, 0x3163, 0x3163},		// l
			
			{122, 0x314b, 0x314b},		// z
			{120, 0x314c, 0x314c},		// x
			{99, 0x314a, 0x314a},		// c
			{118, 0x314d, 0x314d},		// v
			{98, 0x3160, 0x3160},			// b
			{110, 0x315c, 0x315c},		// n
			{109, 0x3161, 0x3161},		// m
	};
	
	public static final int[][] COMB_DUBUL_STANDARD = {
			{0x1169, 0x1161, 0x116a},
			{0x1169, 0x1162, 0x116b},
			{0x1169, 0x1175, 0x116c},
			{0x116e, 0x1165, 0x116f},
			{0x116e, 0x1166, 0x1170},
			{0x116e, 0x1175, 0x1171},
			{0x1173, 0x1175, 0x1174},
			
			{0x11a8, 0x11ba, 0x11aa},
			{0x11ab, 0x11bd, 0x11ac},
			{0x11ab, 0x11c2, 0x11ad},
			{0x11af, 0x11a8, 0x11b0},
			{0x11af, 0x11b7, 0x11b1},
			{0x11af, 0x11b8, 0x11b2},
			{0x11af, 0x11ba, 0x11b3},
			{0x11af, 0x11c0, 0x11b4},
			{0x11af, 0x11c1, 0x11b5},
			{0x11af, 0x11c2, 0x11b6},
			{0x11b8, 0x11ba, 0x11b9},
	};

	public static final int[][] JAMO_DUBUL_DANMOEUM_GOOGLE = {
			{49,0x31,0x21},
			{50,0x32,0x40},
			{51,0x33,0x23},
			{52,0x34,0x24},
			{53,0x35,0x25},
			{54,0x36,0x5e},
			{55,0x37,0x26},
			{56,0x38,0x2a},
			{57,0x39,0x28},
			{48,0x30,0x29},
			
			{113, 0x3142, 0x3142},		// q
			{119, 0x3148, 0x3148},		// w
			{101, 0x3137, 0x3137},		// e
			{114, 0x3131, 0x3131},		// r
			{116, 0x3145, 0x3145},		// t
			{105, 0x3157, 0x3157},		// i
			{111, 0x3150, 0x3150},		// o
			{112, 0x3154, 0x3154},		// p
			
			{97, 0x3141, 0x3141},		// a
			{115, 0x3134, 0x3134},		// s
			{100, 0x3147, 0x3147},		// d
			{102, 0x3139, 0x3139},		// f
			{104, 0x314e, 0x314e},		// h
			{106, 0x3153, 0x3153},		// j
			{107, 0x314f, 0x314f},		// k
			{108, 0x3163, 0x3163},		// l
			
			{120, 0x314b, 0x314b},		// x
			{99, 0x314c, 0x314c},		// c
			{118, 0x314a, 0x314a},		// v
			{98, 0x314d, 0x314d},		// b
			{110, 0x315c, 0x315c},		// n
			{109, 0x3161, 0x3161},		// m
	};
	
	public static final int[][] COMB_DUBUL_DANMOEUM_GOOGLE = {
			{0x1100, 0x1100, 0x1101},	// ㄲ
			{0x1103, 0x1103, 0x1104},	// ㄸ
			{0x1107, 0x1107, 0x1108},	// ㅃ
			{0x1109, 0x1109, 0x110a},	// ㅆ
			{0x110c, 0x110c, 0x110d},	// ㅉ
			
			{0x1161, 0x1161, 0x1163},	// ㅑ
			{0x1162, 0x1162, 0x1164},	// ㅒ
			{0x1163, 0x1175, 0x1164},	// ㅒ
			{0x1165, 0x1165, 0x1167},	// ㅕ
			{0x1167, 0x1175, 0x1168},	// ㅖ
			{0x1166, 0x1166, 0x1168},	// ㅖ
			{0x1169, 0x1161, 0x116a},	// ㅘ
			{0x1169, 0x1162, 0x116b},	// ㅙ
			{0x1169, 0x1175, 0x116c},	// ㅚ
			{0x1169, 0x1169, 0x116d},	// ㅛ
			{0x116e, 0x1165, 0x116f},	// ㅝ
			{0x116e, 0x1166, 0x1170},	// ㅞ
			{0x116e, 0x1175, 0x1171},	// ㅟ
			{0x116e, 0x116e, 0x1172},	// ㅠ
			{0x1173, 0x1175, 0x1174},	// ㅢ

			{0x11a8, 0x11a8, 0x11a9},	// ㄲ
			{0x11a8, 0x11ba, 0x11aa},	// ㄳ
			{0x11ab, 0x11bd, 0x11ac},	// ㄵ
			{0x11ab, 0x11c2, 0x11ad},	// ㄶ
			{0x11af, 0x11a8, 0x11b0},	// ㄺ
			{0x11af, 0x11b7, 0x11b1},	// ㄻ
			{0x11af, 0x11b8, 0x11b2},	// ㄼ
			{0x11af, 0x11ba, 0x11b3},	// ㄽ
			{0x11af, 0x11c0, 0x11b4},	// ㄾ
			{0x11af, 0x11c1, 0x11b5},	// ㄿ
			{0x11af, 0x11c2, 0x11b6},	// ㅀ
			{0x11b8, 0x11ba, 0x11b9},	// ㅄ
			{0x11ba, 0x11ba, 0x11bb},	// ㅆ
			
	};
	
	public static final int[][] JAMO_SEBUL_DANMOEUM = {
			{97, 0x11bc, 0x11ae},		// a
			{115, 0x11ab, 0x11ad},		// s
			{100, 0x1175, 0x11ac},		// d
			{102, 0x1161, 0x11b1},		// f
			{103, 0x1173, 0x25},		// g
			{104, 0x1102, 0x5e},		// h
			{106, 0x110b, 0x26},		// j
			{107, 0x1100, 0x110f},		// k
			{108, 0x110c, 0x110e},		// l
			
			{113, 0x11ba, 0x11c1},		// q
			{119, 0x11af, 0x11c0},		// w
			{101, 0x11b8, 0x11bd},		// e
			{114, 0x1162, 0x23},		// r
			{116, 0x1165, 0x3c},		// t
			{121, 0x1105, 0x3e},		// y
			{117, 0x1103, 0x1110},		// u
			{105, 0x1106, 0xb7},		// i
			{111, 0x1107, 0x1111},		// o
			{112, 0x116e, 0x27},		// p
			
			{122, 0x11b7, 0x11be},		// z
			{120, 0x11a8, 0x11b9},		// x
			{99, 0x1166, 0x11bf},		// c
			{118, 0x1169, 0x11c2},		// v
			{98, 0x116e, 0x3f},			// b
			{110, 0x1109, 0x2d},		// n
			{109, 0x1112, 0x22},		// m
			{36, 0x1169, 0x21},			// $
			{0x20ac, 0x21, 0x21}
	};
	
	public static final int[][] COMB_SEBUL_DANMOEUM = {
			{0x1100, 0x1100, 0x1101},	// ㄲ
			{0x1103, 0x1103, 0x1104},	// ㄸ
			{0x1107, 0x1107, 0x1108},	// ㅃ
			{0x1109, 0x1109, 0x110a},	// ㅆ
			{0x110c, 0x110c, 0x110d},	// ㅉ
			
			{0x1161, 0x1161, 0x1163},	// ㅑ
			{0x1162, 0x1162, 0x1164},	// ㅒ
			{0x1163, 0x1175, 0x1164},	// ㅒ
			{0x1165, 0x1165, 0x1167},	// ㅕ
			{0x1167, 0x1175, 0x1168},	// ㅖ
			{0x1166, 0x1166, 0x1168},	// ㅖ
			{0x1169, 0x1161, 0x116a},	// ㅘ
			{0x1169, 0x1162, 0x116b},	// ㅙ
			{0x1169, 0x1175, 0x116c},	// ㅚ
			{0x1169, 0x1169, 0x116d},	// ㅛ
			{0x116e, 0x1165, 0x116f},	// ㅝ
			{0x116e, 0x1166, 0x1170},	// ㅞ
			{0x116e, 0x1175, 0x1171},	// ㅟ
			{0x116e, 0x116e, 0x1172},	// ㅠ
			{0x1173, 0x1175, 0x1174},	// ㅢ

			{0x11a8, 0x11a8, 0x11a9},	// ㄲ
			{0x11a8, 0x11ba, 0x11aa},	// ㄳ
			{0x11ab, 0x11bd, 0x11ac},	// ㄵ
			{0x11ab, 0x11c2, 0x11ad},	// ㄶ
			{0x11af, 0x11a8, 0x11b0},	// ㄺ
			{0x11af, 0x11b7, 0x11b1},	// ㄻ
			{0x11af, 0x11b8, 0x11b2},	// ㄼ
			{0x11af, 0x11ba, 0x11b3},	// ㄽ
			{0x11af, 0x11c0, 0x11b4},	// ㄾ
			{0x11af, 0x11c1, 0x11b5},	// ㄿ
			{0x11af, 0x11c2, 0x11b6},	// ㅀ
			{0x11b8, 0x11ba, 0x11b9},	// ㅄ
			{0x11ba, 0x11ba, 0x11bb},	// ㅆ
	};

	public static final int[][] JAMO_SEBUL_391 = {
			{49,0x11c2,0x11a9},
			{50,0x11bb,0x11b0},
			{51,0x11b8,0x11bd},
			{52,0x116d,0x11b5},
			{53,0x1172,0x11b4},
			{54,0x1163,0x3d},
			{55,0x1168,0x201c},
			{56,0x1174,0x201d},
			{57,0x116e,0x27},
			{48,0x110f,0x7e},
			
			{113, 0x11ba, 0x11c1},		// q
			{119, 0x11af, 0x11c0},		// w
			{101, 0x1167, 0x11ac},		// e
			{114, 0x1162, 0x11b6},		// r
			{116, 0x1165, 0x11b3},		// t
			{121, 0x1105, 0x35},		// y
			{117, 0x1103, 0x36},		// u
			{105, 0x1106, 0x37},		// i
			{111, 0x110e, 0x38},		// o
			{112, 0x1111, 0x39},		// p
			{40, 0x28, 0x29},

			{97, 0x11bc, 0x11ae},		// a
			{115, 0x11ab, 0x11ad},		// s
			{100, 0x1175, 0x11b2},		// d
			{102, 0x1161, 0x11b1},		// f
			{103, 0x1173, 0x1164},		// g
			{104, 0x1102, 0x30},		// h
			{106, 0x110b, 0x31},		// j
			{107, 0x1100, 0x32},		// k
			{108, 0x110c, 0x33},		// l
			{59, 0x1107, 0x34},
			{39, 0x1110, 0x00b7},
			
			{122, 0x11b7, 0x11be},		// z
			{120, 0x11a8, 0x11b9},		// x
			{99, 0x1166, 0x11bf},		// c
			{118, 0x1169, 0x11aa},		// v
			{98, 0x116e, 0x3f},			// b
			{110, 0x1109, 0x2d},		// n
			{109, 0x1112, 0x22},		// m
			{44, 0x2c, 0x2c},
			{46, 0x2e, 0x2e},
			{47, 0x1169, 0x21},
			
			{0x2d, 0x29, 0x3b},
			{0x3d, 0x3e, 0x2b},
			{0x5b, 0x28, 0x25},
			{0x5d, 0x3c, 0x2f},
			{0x5c, 0x3a, 0x5c},
	};

	public static final int[][] JAMO_SEBUL_390 = {
			{49,0x11c2,0x11bd},
			{50,0x11bb,0x40},
			{51,0x11b8,0x23},
			{52,0x116d,0x24},
			{53,0x1172,0x25},
			{54,0x1163,0x5e},
			{55,0x1168,0x26},
			{56,0x1174,0x2a},
			{57,0x116e,0x28},
			{48,0x110f,0x29},
			
			{113, 0x11ba, 0x11c1},		// q
			{119, 0x11af, 0x11c0},		// w
			{101, 0x1167, 0x11bf},		// e
			{114, 0x1162, 0x1164},		// r
			{116, 0x1165, 0x3b},		// t
			{121, 0x1105, 0x3c},		// y
			{117, 0x1103, 0x37},		// u
			{105, 0x1106, 0x38},		// i
			{111, 0x110e, 0x39},		// o
			{112, 0x1111, 0x3e},		// p
			{40, 0x28, 0x29},

			{97, 0x11bc, 0x11ae},		// a
			{115, 0x11ab, 0x11ad},		// s
			{100, 0x1175, 0x11b0},		// d
			{102, 0x1161, 0x11a9},		// f
			{103, 0x1173, 0x2f},		// g
			{104, 0x1102, 0x27},		// h
			{106, 0x110b, 0x34},		// j
			{107, 0x1100, 0x35},		// k
			{108, 0x110c, 0x36},		// l
			{59, 0x1107, 0x3a},
			{39, 0x1110, 0x22},
			
			{122, 0x11b7, 0x11be},		// z
			{120, 0x11a8, 0x11b9},		// x
			{99, 0x1166, 0x11b1},		// c
			{118, 0x1169, 0x11b6},		// v
			{98, 0x116e, 0x21},			// b
			{110, 0x1109, 0x30},		// n
			{109, 0x1112, 0x31},		// m
			{44, 0x2c, 0x32},
			{46, 0x2e, 0x33},
			{47, 0x1169, 0x3f}
	};

	public static final int[][] JAMO_SEBUL_SHIN_ORIGINAL = {
			
			{113, 0x11ba, 0x1174},		// q
			{119, 0x11af, 0x1163},		// w
			{101, 0x11b8, 0x1167},		// e
			{114, 0x11ae, 0x1162},		// r
			{116, 0x11c0, 0x1165},		// t
			{121, 0x1105, 0x1105},		// y
			{117, 0x1103, 0x1103},		// u
			{105, 0x1106, -5001},		// i
			{111, 0x110e, -5001},		// o
			{112, 0x1111, -5000},		// p
			
			{97, 0x11bc, 0x1164},		// a
			{115, 0x11ab, 0x1168},		// s
			{100, 0x11c2, 0x1175},		// d
			{102, 0x11bd, 0x1161},		// f
			{103, 0x11c1, 0x1173},		// g
			{104, 0x1102, 0x1102},		// h
			{106, 0x110b, 0x110b},		// j
			{107, 0x1100, 0x1100},		// k
			{108, 0x110c, 0x110c},		// l
			{59, 0x1107, 0x1107},
			{39, 0x1110, 0x1110},
			
			{122, 0x11b7, 0x1172},		// z
			{120, 0x11a8, 0x116d},		// x
			{99, 0x11be, 0x1166},		// c
			{118, 0x11bf, 0x1169},		// v
			{98, 0x11bb, 0x116e},			// b
			{110, 0x1109, 0x1109},		// n
			{109, 0x1112, 0x1112},		// m
			{47, 0x110f, -5000},
			{36, 0x110f, -5000},			// $
			{0x20ac, 0x110f, -5000},
	};
	
	public static final int[][] COMB_SEBUL_SHIN_ORIGINAL = {
			{0x1100, 0x1100, 0x1101},	// ㄲ
			{0x1103, 0x1103, 0x1104},	// ㄸ
			{0x1107, 0x1107, 0x1108},	// ㅃ
			{0x1109, 0x1109, 0x110a},	// ㅆ
			{0x110c, 0x110c, 0x110d},	// ㅉ
			
			{0x1169, 0x1161, 0x116a},	// ㅘ
			{0x1169, 0x1162, 0x116b},	// ㅙ
			{0x1169, 0x1175, 0x116c},	// ㅚ
			{0x116e, 0x1165, 0x116f},	// ㅝ
			{0x116e, 0x1166, 0x1170},	// ㅞ
			{0x116e, 0x1175, 0x1171},	// ㅟ
			{0x1173, 0x1175, 0x1174},	// ㅢ

			{0x11a8, 0x11a8, 0x11a9},	// ㄲ
			{0x11a8, 0x11ba, 0x11aa},	// ㄳ
			{0x11ab, 0x11bd, 0x11ac},	// ㄵ
			{0x11ab, 0x11c2, 0x11ad},	// ㄶ
			{0x11af, 0x11a8, 0x11b0},	// ㄺ
			{0x11af, 0x11b7, 0x11b1},	// ㄻ
			{0x11af, 0x11b8, 0x11b2},	// ㄼ
			{0x11af, 0x11ba, 0x11b3},	// ㄽ
			{0x11af, 0x11c0, 0x11b4},	// ㄾ
			{0x11af, 0x11c1, 0x11b5},	// ㄿ
			{0x11af, 0x11c2, 0x11b6},	// ㅀ
			{0x11b8, 0x11ba, 0x11b9},	// ㅄ
			{0x11ba, 0x11ba, 0x11bb},	// ㅆ
	};

	public static final int[][] JAMO_SEBUL_SHIN_EDIT = {
			
			{113, 0x11ba, 0x1164},		// q
			{119, 0x11af, 0x1163},		// w
			{101, 0x11b8, 0x1167},		// e
			{114, 0x11c0, 0x1162},		// r
			{116, 0x11c1, 0x1165},		// t
			{121, 0x1105, 0x1105},		// y
			{117, 0x1103, 0x1103},		// u
			{105, 0x1106, 0x1174},		// i
			{111, 0x110e, -5001},		// o
			{112, 0x1111, -5000},		// p
			
			{97, 0x11bc, 0x1172},		// a
			{115, 0x11ab, 0x1168},		// s
			{100, 0x11ae, 0x1175},		// d
			{102, 0x11bb, 0x1161},		// f
			{103, 0x11bd, 0x1173},		// g
			{104, 0x1102, 0x1102},		// h
			{106, 0x110b, 0x110b},		// j
			{107, 0x1100, 0x1100},		// k
			{108, 0x110c, 0x110c},		// l
			{59, 0x1107, 0x1107},
			{39, 0x1110, 0x1110},
			
			{122, 0x11b7, 0x203b},		// z
			{120, 0x11a8, 0x116d},		// x
			{99, 0x11be, 0x1166},		// c
			{118, 0x11c2, 0x1169},		// v
			{98, 0x11bf, 0x116e},			// b
			{110, 0x1109, 0x1109},		// n
			{109, 0x1112, 0x1112},		// m
			{47, 0x110f, -5000},
			{36, 0x110f, -5000},			// $
			{0x20ac, 0x110f, -5000},
	};
	
	public static final int[][] CONVERT_ENGLISH_DVORAK = {
			{49,0x31,0x21},
			{50,0x32,0x40},
			{51,0x33,0x23},
			{52,0x34,0x24},
			{53,0x35,0x25},
			{54,0x36,0x5e},
			{55,0x37,0x26},
			{56,0x38,0x2a},
			{57,0x39,0x28},
			{48,0x30,0x29},
			
			{113, 0x27, 0x22},		// q
			{119, 0x2c, 0x2c},		// w
			{101, 0x2e, 0x2c},		// e
			{114, 112, 80},		// r
			{116, 121, 89},		// t
			{121, 102, 70},		// y
			{117, 103, 71},		// u
			{105, 99, 67},		// i
			{111, 114, 82},		// o
			{112, 108, 76},			// p
			
			{97, 97, 65},		// a
			{115, 111, 79},		// s
			{100, 101, 69},		// d
			{102, 117, 85},		// f
			{103, 105, 73},		// g
			{104, 100, 68},		// h
			{106, 104, 72},		// j
			{107, 116, 84},		// k
			{108, 110, 78},		// l
			{0x3b, 115, 83},
			
			{122, 0x3b, 0x3a},		// z
			{120, 113, 81},		// x
			{99, 106, 74},		// c
			{118, 107, 75},		// v
			{98, 120, 88},			// b
			{110, 98, 66},		// n
			{109, 109, 77},		// m
			{0x2c, 119, 87},
			{0x2e, 118, 86},
			{0x2f, 122, 90},
	};
	
	public static final int[][] COMB_SEBULSIK = {
			{0x1100, 0x1100, 0x1101},	// ㄲ
			{0x1103, 0x1103, 0x1104},	// ㄸ
			{0x1107, 0x1107, 0x1108},	// ㅃ
			{0x1109, 0x1109, 0x110a},	// ㅆ
			{0x110c, 0x110c, 0x110d},	// ㅉ
			
			{0x1169, 0x1161, 0x116a},	// ㅘ
			{0x1169, 0x1162, 0x116b},	// ㅙ
			{0x1169, 0x1175, 0x116c},	// ㅚ
			{0x116e, 0x1165, 0x116f},	// ㅝ
			{0x116e, 0x1166, 0x1170},	// ㅞ
			{0x116e, 0x1175, 0x1171},	// ㅟ
			{0x1173, 0x1175, 0x1174},	// ㅢ

			{0x11a8, 0x11a8, 0x11a9},	// ㄲ
			{0x11a8, 0x11ba, 0x11aa},	// ㄳ
			{0x11ab, 0x11bd, 0x11ac},	// ㄵ
			{0x11ab, 0x11c2, 0x11ad},	// ㄶ
			{0x11af, 0x11a8, 0x11b0},	// ㄺ
			{0x11af, 0x11b7, 0x11b1},	// ㄻ
			{0x11af, 0x11b8, 0x11b2},	// ㄼ
			{0x11af, 0x11ba, 0x11b3},	// ㄽ
			{0x11af, 0x11c0, 0x11b4},	// ㄾ
			{0x11af, 0x11c1, 0x11b5},	// ㄿ
			{0x11af, 0x11c2, 0x11b6},	// ㅀ
			{0x11b8, 0x11ba, 0x11b9},	// ㅄ
			{0x11ba, 0x11ba, 0x11bb},	// ㅆ
	};
	
	public static final int[][] CYCLE_12KEY_ALPHABET = {
			{-201, '.', '@'},
			{-202, 'a', 'b', 'c'},
			{-203, 'd', 'e', 'f'},
			{-204, 'g', 'h', 'i'},
			{-205, 'j', 'k', 'l'},
			{-206, 'm', 'n', 'o'},
			{-207, 'p', 'q', 'r', 's'},
			{-208, 't', 'u', 'v'},
			{-209, 'w', 'x', 'y', 'z'},
			{-210, '-'},
			{-211, '.', ','},
	};
	
	public static final int[][] CYCLE_SEBUL_12KEY_MUNHWA = {
			{-201, 0x11bb, 0x11bd},
			{-202, 0x1169, 0x116e},
			{-203, 0x1109, 0x110c},
			{-204, 0x11ab, 0x11bc},
			{-205, 0x1161, 0x1165},
			{-206, 0x110b, 0x1106, 0x1107},
			{-207, 0x11af, 0x11b7, 0x11ae},
			{-208, 0x1175, 0x1173},
			{-209, 0x1102, 0x1103},
			{-213, 0x11a8, 0x11b8},
			{-210, DefaultSoftKeyboardKOKR.KEYCODE_KR12_ADDSTROKE},
			{-211, 0x1100, 0x1105},
	};
	
	public static final int[][] COMB_SEBUL_12KEY_MUNHWA = {
			
			{0x1161, 0x1175, 0x1162},	// ㅐ
			{0x1163, 0x1175, 0x1164},	// ㅒ
			{0x1165, 0x1175, 0x1166},	// ㅔ
			{0x1167, 0x1175, 0x1168},	// ㅖ
			{0x1169, 0x1161, 0x116a},	// ㅘ
			{0x116a, 0x1175, 0x116b},	// ㅙ
			{0x1169, 0x1175, 0x116c},	// ㅚ
			{0x116e, 0x1161, 0x116f},	// ㅝ
			{0x116f, 0x1175, 0x1170},	// ㅞ
			{0x116e, 0x1175, 0x1171},	// ㅟ

			{0x11a8, 0x11a8, 0x11a9},	// ㄲ
			{0x11a8, 0x11ba, 0x11aa},	// ㄳ
			{0x11ab, 0x11bd, 0x11ac},	// ㄵ
			{0x11af, 0x11a8, 0x11b0},	// ㄺ
			{0x11af, 0x11b8, 0x11b2},	// ㄼ
			{0x11af, 0x11ba, 0x11b3},	// ㄽ
			{0x11af, 0x11c1, 0x11b5},	// ㄿ
			{0x11af, 0x11c2, 0x11b6},	// ㅀ
			{0x11b8, 0x11ba, 0x11b9},	// ㅄ
			{0x11ba, 0x11ba, 0x11bb},	// ㅆ
	};
	
	public static final int[][] STROKE_SEBUL_12KEY_MUNHWA = {
			{0x11bb, 0x11ba},			// ㅆ ㅅ
			{0x11bd, 0x11be},			// ㅈ ㅊ
			{0x1169, 0x116d},			// ㅗ ㅛ
			{0x116e, 0x1172},			// ㅜ ㅠ
			{0x1109, 0x110a},			// ㅅ ㅆ
			{0x110c, 0x110e, 0x110d},	// ㅈ ㅊ ㅉ
			{0x11ab, 0x11ad},			// ㄴ ㄶ
			{0x11bc, 0x11c2},			// ㅇ ㅎ
			{0x1161, 0x1163},			// ㅏ ㅑ
			{0x1165, 0x1167},			// ㅓ ㅕ
			{0x110b, 0x1112},			// ㅇ ㅎ
			{0x1106, 0x1111, 0x1108},	// ㅁ ㅍ ㅃ
			{0x1107, 0x1108},			// ㅂ ㅃ
			{0x11af, 0x11b1, 0x11b4},	// ㄹ ㄻ ㄾ
			{0x11b7, 0x11c0},			// ㅁ ㅌ
			{0x11ae, 0x11c0},			// ㄷ ㅌ
			{0x1175, 0x1174},			// ㅣ ㅢ
			{0x1173, 0x1174},			// ㅡ ㅢ
			{0x1102, 0x1110, 0x1104},	// ㄴ ㅌ ㄸ
			{0x1103, 0x1110, 0x1104},	// ㄷ ㅌ ㄸ
			{0x11a8, 0x11a9, 0x11bf},	// ㄱ ㄲ ㅋ
			{0x11b8, 0x11c1},			// ㅂ ㅍ
			{0x1100, 0x110f, 0x1101},	// ㄱ ㄲ ㅋ
		};

	public static final int[][] CYCLE_SEBUL_12KEY_HANSON = {
			{-2001, 0x1100},	// ㄱ
			{-2002, 0x110b},	// ㅇ
			{-2003, 0x1175},	// ㅣ
			{-2403, 0x1161},
			{-2303, 0x1165},
			{-2004, 0x11bc},	// ㅇ
			{-2005, 0x11a8},	// ㄱ
			{-2006, 0x1106},	// ㅁ
			{-2007, 0x1102},	// ㄴ
			{-2008, 0x119e},	// ᆞ
			{-2009, 0x11ab},	// ㄴ
			{-2010, 0x11b7},	// ㅁ
			{-2011, DefaultSoftKeyboardKOKR.KEYCODE_KR12_ADDSTROKE},
			{-2012, 0x1109},	// ㅅ
			{-2013, 0x1173},	// ㅡ
			{-2113, 0x1169},
			{-2213, 0x116e},
			{-2014, 0x11ba},	// ㅅ
			{-2015, 0x2e},
			{-2515, 0x2c},
			
			{-2502, 0x31},
			{-2503, 0x32},
			{-2504, 0x33},
			{-2507, 0x34},
			{-2508, 0x35},
			{-2509, 0x36},
			{-2512, 0x37},
			{-2513, 0x38},
			{-2514, 0x39},
			{-2511, 0x30},
	};
	
	public static final int[][] COMB_SEBUL_12KEY_HANSON = {
			{0x1100, 0x1100, 0x1101},	// ㄲ
			{0x1103, 0x1102, 0x1104},	// ㄸ
			{0x1102, 0x1102, 0x1105},	// ㄹ (ㄴ+ㄴ)
			{0x1100, 0x1102, 0x1105},	// ㄹ (ㄱ+ㄴ)
			{0x1107, 0x1106, 0x1108},	// ㅃ
			{0x1109, 0x1109, 0x110a},	// ㅆ
			{0x110c, 0x1109, 0x110d},	// ㅉ
			
			{0x1106, 0x1106, 0x1107},	// ㅂ (ㅁ+ㅁ)
			{0x110b, 0x110b, 0x1112},	// ㅎ (ㅇ+ㅇ)
			{0x1107, 0x1106, 0x1108},	// ㅃ (ㅁ+ㅁ+ㅁ) -> (ㅂ+ㅁ)
			
			{0x1175, 0x119e, 0x1161},	// ㅏ
			{0x1161, 0x1175, 0x1162},	// ㅐ
			{0x1161, 0x119e, 0x1163},	// ㅑ
			{0x1163, 0x1175, 0x1164},	// ㅒ
			{0x119e, 0x119e, 0x11a2},	// ᆢ
			{0x119e, 0x1175, 0x1165},	// ㅓ
			{0x1165, 0x1175, 0x1166},	// ㅔ
			{0x11a2, 0x1175, 0x1167},	// ㅕ (··+ㅣ)
			{0x119e, 0x1165, 0x1167},	// ㅕ (·+ㅓ)
			{0x1165, 0x119e, 0x1167},	// ㅕ (ㅓ+·)
			{0x1167, 0x1175, 0x1168},	// ㅖ
			{0x119e, 0x1173, 0x1169},	// ㅗ
			{0x116c, 0x119e, 0x116a},	// ㅘ (ㅚ+·)
			{0x1169, 0x1161, 0x116a},	// ㅘ (ㅗ+ㅏ)
			{0x116a, 0x1175, 0x116b},	// ㅙ
			{0x1169, 0x1175, 0x116c},	// ㅚ
			{0x11a2, 0x1173, 0x116d},	// ㅛ (··+ㅡ)
			{0x119e, 0x1169, 0x116d},	// ㅛ (·+ㅗ)
			{0x1169, 0x119e, 0x116d},	// ㅛ (ㅗ+·)
			{0x1173, 0x119e, 0x116e},	// ㅜ
			{0x1172, 0x1175, 0x116f},	// ㅝ (ㅠ+ㅣ)
			{0x116e, 0x1165, 0x116f},	// ㅝ (ㅜ+ㅓ)
			{0x116f, 0x1175, 0x1170},	// ㅞ
			{0x116e, 0x1175, 0x1171},	// ㅟ
			{0x116e, 0x119e, 0x1172},	// ㅠ
			{0x1173, 0x1175, 0x1174},	// ㅢ
			
			{0x11ab, 0x11ab, 0x11af},
			{0x11a8, 0x11ab, 0x11af},
			{0x11b7, 0x11b7, 0x11b8},	// ㅂ
			{0x11bc, 0x11bc, 0x11c2},	// ㅎ
			
			{0x11a8, 0x11a8, 0x11a9},	// ㄲ
			{0x11a8, 0x11ba, 0x11aa},	// ㄳ
			{0x11ab, 0x11ba, 0x11ac},	// ㄵ
			{0x11ab, 0x11bc, 0x11ad},	// ㄶ
			{0x11af, 0x11a8, 0x11b0},	// ㄺ
			{0x11af, 0x11b7, 0x11b1},	// ㄻ
			{0x11b1, 0x11b7, 0x11b2},	// ㄼ
			{0x11af, 0x11ba, 0x11b3},	// ㄽ
			{0x11af, 0x11ab, 0x11b4},	// ㄾ
			{0x11af, 0x11bc, 0x11b6},	// ㅀ
			{0x11b8, 0x11ba, 0x11b9},	// ㅄ
			{0x11ba, 0x11ba, 0x11bb},	// ㅆ
			
	};
	
	public static final int[][] STROKE_SEBUL_12KEY_HANSON = {
			{0x1100, 0x110f},			// ㄱ ㅋ
			{0x1102, 0x1103, 0x1110},	// ㄴ ㄷ ㅌ
			{0x1106, 0x1107, 0x1111},	// ㅁ ㅂ ㅍ
			{0x1109, 0x110c, 0x110e},	// ㅅ ㅈ ㅊ
			{0x110b, 0x1112},			// ㅇ ㅎ
			{0x1105, 0x1104},			// ㄴ+ㄴ+가획=ㄸ
			{0x110a, 0x110d},			// ㅅ+ㅅ+가획=ㅉ
			
			{0x11ab, 0x11ae},			// ㄷ
			{0x11b7, 0x11b8},			// ㅁ
			{0x11ba, 0x11bd},			// ㅈ
			{0x11bd, 0x11be},			// ㅊ
			{0x11a8, 0x11bf},			// ㅋ
			{0x11ae, 0x11c0},			// ㅌ
			{0x11b8, 0x11c1},			// ㅍ
			{0x11bc, 0x11c2},			// ㅎ
			
			{0x11b1, 0x11b2},			// ㄼ
			{0x11b2, 0x11b5},			// ㄾ
			
			{0x2e, 0x3f},
			{0x2c, 0x21},
	};
	
	public static final int[][] ALT_SYMBOLS_TABLE = {
			{113, 0x21},		// q
			{119, 0x40},		// w
			{101, 0x23},		// e
			{114, 0x24},		// r
			{116, 0x25},		// t
			{121, 0x5e},		// y
			{117, 0x26},		// u
			{105, 0x2a},		// i
			{111, 0x28},		// o
			{112, 0x29},		// p
			
			{97, 0x7e},			// a
			{115, 0x27},		// s
			{100, 0x5b},		// d
			{102, 0x5d},		// f
			{103, 0x2f},		// g
			{104, 0x3c},		// h
			{106, 0x3e},		// j
			{107, 0x3a},		// k
			{108, 0x3b},		// l
			
			{122, 0x5f},		// z
			{120, 0x7b},		// x
			{99, 0x7d},			// c
			{118, 0x2b},		// v
			{98, 0x3f},			// b
			{110, 0x2d},		// n
			{109, 0x22},		// m
			
			{-201, 0x31},
			{-202, 0x32},
			{-203, 0x33},
			{-204, 0x34},
			{-205, 0x35},
			{-206, 0x36},
			{-207, 0x37},
			{-208, 0x38},
			{-209, 0x39},
			{-213, 0x2e},
			{-210, 0x30},
			{-211, 0x3f},
			
	};
	
	public static final int[][] FLICK_TABLE_12KEY = {
			{-201, 0x31},
			{-202, 0x32},
			{-203, 0x33},
			{-204, 0x34},
			{-205, 0x35},
			{-206, 0x36},
			{-207, 0x37},
			{-208, 0x38},
			{-209, 0x39},
			{-213, 0x2c},
			{-210, 0x30},
			{-211, 0x21},
	};
	
	public static final int ENGINE_MODE_SEBUL_390 = 101;
	public static final int ENGINE_MODE_SEBUL_391 = 102;
	public static final int ENGINE_MODE_SEBUL_DANMOEUM = 103;
	public static final int ENGINE_MODE_DUBULSIK = 104;
	public static final int ENGINE_MODE_DUBUL_DANMOEUM = 105;
	public static final int ENGINE_MODE_SEBUL_SHIN_ORIGINAL = 106;
	public static final int ENGINE_MODE_SEBUL_SHIN_EDIT = 107;
	
	public static final int ENGINE_MODE_12KEY_ALPHABET = 150;
	public static final int ENGINE_MODE_12KEY_SEBUL_MUNHWA = 151;
	public static final int ENGINE_MODE_12KEY_SEBUL_HANSON = 153;
	public static final int ENGINE_MODE_12KEY_DUBUL = 155;

	public static final int ENGINE_MODE_ENGLISH_DVORAK = 192;
	
	public static final int ENGINE_MODE_OPT_TYPE_QWERTY = 200;
	public static final int ENGINE_MODE_OPT_TYPE_12KEY = 201;
	
	public static final int LONG_CLICK_EVENT = 0xFF000100;
	public static final int FLICK_UP_EVENT = 0xFF000101;
	public static final int FLICK_DOWN_EVENT = 0xFF000102;
	public static final int FLICK_LEFT_EVENT = 0xFF000103;
	public static final int FLICK_RIGHT_EVENT = 0xFF000104;

	public static final int TIMEOUT_EVENT = 0xFF00001;
	
	HangulEngine mHangulEngine;
	HangulEngine mQwertyEngine, m12keyEngine;
	
	int mHardShift;
	int mHardAlt;
	
	boolean mShiftPressing;
	boolean mAltPressing;
	
	int mCurrentEngineMode;
	
	private static final int[] mShiftKeyToggle = {0, MetaKeyKeyListener.META_SHIFT_ON, MetaKeyKeyListener.META_CAP_LOCKED};
	
	private static final int[] mAltKeyToggle = {0, MetaKeyKeyListener.META_ALT_ON, MetaKeyKeyListener.META_ALT_LOCKED};
	
	boolean mDirectInputMode;
	boolean mEnableTimeout;
	
	boolean mMoachigi;
	boolean mHardwareMoachigi;
	
	boolean selectionMode;
	int selectionStart, selectionEnd;
	
	private static OpenWnnKOKR mSelf;
	public static OpenWnnKOKR getInstance() {
		return mSelf;
	}
	
	public OpenWnnKOKR() {
		super();
		mSelf = this;
		mInputViewManager = new DefaultSoftKeyboardKOKR(this);
		mQwertyEngine = new HangulEngine();
		m12keyEngine = new TwelveHangulEngine();
		mHangulEngine = mQwertyEngine;
		mQwertyEngine.setListener(this);
		m12keyEngine.setListener(this);
		
		mAutoHideMode = false;
	}
	
	public OpenWnnKOKR(Context context) {
		this();
		attachBaseContext(context);
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public View onCreateInputView() {
		int hiddenState = getResources().getConfiguration().hardKeyboardHidden;
		boolean hidden = (hiddenState == Configuration.HARDKEYBOARDHIDDEN_YES);
		((DefaultSoftKeyboardKOKR) mInputViewManager).setHardKeyboardHidden(hidden);
		return super.onCreateInputView();
	}

	@Override
	public void onStartInputView(EditorInfo attribute, boolean restarting) {
		mHangulEngine.resetJohab();
		if(restarting) {
			super.onStartInputView(attribute, restarting);
		} else {
			
			((DefaultSoftKeyboard) mInputViewManager).resetCurrentKeyboard();
			
			super.onStartInputView(attribute, restarting);
			
			mHardShift = 0;
			mHardAlt = 0;
			updateMetaKeyStateDisplay();
		}
		
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		
		boolean hardKeyboardHidden = ((DefaultSoftKeyboard) mInputViewManager).mHardKeyboardHidden;
		
		mMoachigi = pref.getBoolean("keyboard_use_moachigi", mMoachigi);
		mHardwareMoachigi = pref.getBoolean("hardware_use_moachigi", mHardwareMoachigi);
		
		if(hardKeyboardHidden) mQwertyEngine.setMoachigi(mMoachigi);
		else mQwertyEngine.setMoachigi(mHardwareMoachigi);
		
	}

	@Override
	public void onStartInput(EditorInfo attribute, boolean restarting) {
		super.onStartInput(attribute, restarting);
		mHangulEngine.setComposing("");
	}

	@Override
	public View onCreateCandidatesView() {
		return super.onCreateCandidatesView();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		try {
			super.onConfigurationChanged(newConfig);
			
			if (mInputConnection != null) {				
				/* Hardware keyboard */
				int hiddenState = newConfig.hardKeyboardHidden;
				boolean hidden = (hiddenState == Configuration.HARDKEYBOARDHIDDEN_YES);
				((DefaultSoftKeyboardKOKR) mInputViewManager).setHardKeyboardHidden(hidden);
			}
		} catch (Exception ex) {
		}
	}

	@Override
	public void onFinishInput() {
		mHangulEngine.resetJohab();
		super.onFinishInput();
	}

	@Override
	public void onViewClicked(boolean focusChanged) {
		mInputConnection.finishComposingText();
		super.onViewClicked(focusChanged);
		mHangulEngine.setComposing("");
		mHangulEngine.resetJohab();
	}

	@Override
	public void onEvent(HangulEngineEvent event) {
		if(event instanceof FinishComposingEvent) {
			if(mInputConnection != null) mInputConnection.finishComposingText();
		}
		if(event instanceof SetComposingEvent) {
			SetComposingEvent composingEvent = (SetComposingEvent) event;
			if(mInputConnection != null) mInputConnection.setComposingText(composingEvent.getComposing(), 1);
		}
	}

	@Override
	public boolean onEvent(OpenWnnEvent ev) {
		if(mInputConnection == null) return false;
		
		switch(ev.code) {
		case OpenWnnEvent.KEYUP:
			onKeyUpEvent(ev.keyEvent);
			return true;
			
		case OpenWnnEvent.CHANGE_MODE:
			mHangulEngine.resetJohab();
			changeEngineMode(ev.mode);
			return true;
			
		case OpenWnnEvent.CHANGE_INPUT_VIEW:
			setInputView(onCreateInputView());
			onStartInputView(getCurrentInputEditorInfo(), false);
			return true;
			
		case TIMEOUT_EVENT:
			if(mEnableTimeout) {
				mHangulEngine.resetJohab();
				if(mHangulEngine instanceof TwelveHangulEngine) {
					((TwelveHangulEngine) mHangulEngine).forceResetJohab();
					((TwelveHangulEngine) mHangulEngine).resetCycle();
				}
			}
			break;
			
		default:
			break;
		}
		
		KeyEvent keyEvent = ev.keyEvent;
		int keyCode = 0;
		if(keyEvent != null) {
			keyCode = keyEvent.getKeyCode();
		}
		
		boolean ret = false;
		switch(ev.code) {
		case LONG_CLICK_EVENT:
			if(keyCode < 0) {
				if(keyCode <= -2000) {
					this.onEvent(new OpenWnnEvent(OpenWnnEvent.INPUT_SOFT_KEY,
							new KeyEvent(KeyEvent.ACTION_DOWN, keyCode - 500)));
				}
				switch(keyCode) {
				case DefaultSoftKeyboard.KEYCODE_QWERTY_ALT:
					Intent intent = new Intent(this, OpenWnnControlPanelKOKR.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
					break;
					
				case DefaultSoftKeyboard.KEYCODE_CHANGE_LANG:
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.showInputMethodPicker();
					break;
				}
			} else {
				mHardShift = 1;
				this.onEvent(new OpenWnnEvent(OpenWnnEvent.INPUT_CHAR, (char) keyCode));
				mHardShift = 0;
			}
			break;
			
		case FLICK_UP_EVENT:
			if(keyCode < 0) {
				if(keyCode <= -2000) {
					this.onEvent(new OpenWnnEvent(OpenWnnEvent.INPUT_SOFT_KEY,
							new KeyEvent(KeyEvent.ACTION_DOWN, keyCode - 100)));
				} else {
					for(int[] item : FLICK_TABLE_12KEY) {
						if(item[0] == keyCode) {
							this.onEvent(new OpenWnnEvent(OpenWnnEvent.INPUT_CHAR, (char) item[1]));
							break;
						}
					}
				}
			} else {
				switch(keyCode) {
				case DefaultSoftKeyboard.KEYCODE_QWERTY_SHIFT:
					break;
					
				default:
					this.mHardShift = 1;
					this.onEvent(new OpenWnnEvent(OpenWnnEvent.INPUT_CHAR, (char) keyCode));
					this.mHardShift = 0;
					break;
					
				}
			}
			break;
			
		case FLICK_DOWN_EVENT:
			boolean alt = true;
			if(keyCode < 0) {
				if(keyCode <= -2000) {
					this.onEvent(new OpenWnnEvent(OpenWnnEvent.INPUT_SOFT_KEY,
							new KeyEvent(KeyEvent.ACTION_DOWN, keyCode - 200)));
					alt = false;
				}
			}
			if(alt) {
				for(int[] item : ALT_SYMBOLS_TABLE) {
					if(item[0] == keyCode) {
						this.inputChar((char) item[1], true);
						break;
					}
				}
			}
			break;
			
		case FLICK_LEFT_EVENT:
			if(keyCode < 0) {
				if(keyCode <= -2000) {
					this.onEvent(new OpenWnnEvent(OpenWnnEvent.INPUT_SOFT_KEY,
							new KeyEvent(KeyEvent.ACTION_DOWN, keyCode - 300)));
				}
			} else {
				
			}
			break;
			
		case FLICK_RIGHT_EVENT:
			if(keyCode < 0) {
				if(keyCode <= -2000) {
					this.onEvent(new OpenWnnEvent(OpenWnnEvent.INPUT_SOFT_KEY,
							new KeyEvent(KeyEvent.ACTION_DOWN, keyCode - 400)));
				}
			} else {
				
			}
			break;
			
		case OpenWnnEvent.INPUT_CHAR:
			char code = new String(ev.chars).charAt(0);
			inputChar(code);
			shinShift();
			ret = true;
			break;
			
		case OpenWnnEvent.INPUT_KEY:
			switch(keyCode) {
			case KeyEvent.KEYCODE_ALT_LEFT:
			case KeyEvent.KEYCODE_ALT_RIGHT:
				if (keyEvent.getRepeatCount() == 0) {
					if (++mHardAlt > 2) { mHardAlt = 0; }
				}
				mAltPressing   = true;
				updateMetaKeyStateDisplay();
				return true;
				
			case KeyEvent.KEYCODE_SHIFT_LEFT:
			case KeyEvent.KEYCODE_SHIFT_RIGHT:
				if (keyEvent.getRepeatCount() == 0) {
					if (++mHardShift > 2) { mHardShift = 0; }
				}
				mShiftPressing = true;
				updateMetaKeyStateDisplay();
				return true;
			}
			ret = processKeyEvent(keyEvent);
			shinShift();
			break;
			
		case OpenWnnEvent.INPUT_SOFT_KEY:
			switch(keyCode) {
			case KeyEvent.KEYCODE_SHIFT_LEFT:
			case KeyEvent.KEYCODE_SHIFT_RIGHT:
				switch(keyEvent.getAction()) {
				case KeyEvent.ACTION_UP:
					mHardShift = 0;
					mShiftPressing = false;
					updateMetaKeyStateDisplay();
					return true;
				case KeyEvent.ACTION_DOWN:
					mHardShift = 1;
					mShiftPressing = true;
					updateMetaKeyStateDisplay();
					return true;
				}
			case KeyEvent.KEYCODE_SPACE:
				mHangulEngine.resetJohab();
				mInputConnection.commitText(" ", 1);
				shinShift();
				return true;
			}
			ret = processKeyEvent(keyEvent);
			if(!ret) {
				int c = keyEvent.getKeyCode();
				mInputConnection.sendKeyEvent(keyEvent);
				mInputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, c));
				ret = true;
			}
			shinShift();
			break;

		case OpenWnnEvent.COMMIT_COMPOSING_TEXT:
			mInputConnection.finishComposingText();
			break;
			
		}
		return ret;
	}
	
	private void inputChar(char code) {
		this.inputChar(code, false);
	}
	
	private void inputChar(char code, boolean direct) {
		int shift = mHardShift;
		char originalCode = code;
		for(int[] item : SHIFT_CONVERT) {
			if(code == item[1]) {
				code = (char) item[0];
				shift = 1;
			}
		}
		if(mDirectInputMode || direct) {
			code = originalCode;
			mHangulEngine.resetJohab();
			code = (shift == 0) ? code : Character.toUpperCase(code);
			mInputConnection.commitText(String.valueOf(code), 1);
			return;
		}
		int jamo = mHangulEngine.inputCode(Character.toLowerCase(code), shift);
		if(jamo != -1) {
			if(mHangulEngine.inputJamo(jamo) != 0) {
				mInputConnection.setComposingText(mHangulEngine.getComposing(), 1);
			} else {
				mHangulEngine.resetJohab();
				sendKeyChar((char) jamo);
			}
		} else {
			mHangulEngine.resetJohab();
			sendKeyChar(originalCode);
		}
	}
	
	@SuppressLint("NewApi")
	private boolean processKeyEvent(KeyEvent ev) {
		int key = ev.getKeyCode();

		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			if(ev.isCtrlPressed()) return false;
		}
		
		if(ev.isShiftPressed()) {
			switch(key) {
			case KeyEvent.KEYCODE_DPAD_UP:
			case KeyEvent.KEYCODE_DPAD_DOWN:
			case KeyEvent.KEYCODE_DPAD_LEFT:
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				if(!selectionMode) {
					selectionEnd = mInputConnection.getTextBeforeCursor(Integer.MAX_VALUE, 0).length();
					selectionStart = selectionEnd;
					selectionMode = true;
				}
				if(selectionMode) {
					if(key == KeyEvent.KEYCODE_DPAD_LEFT) selectionEnd--;
					if(key == KeyEvent.KEYCODE_DPAD_RIGHT) selectionEnd++;
					int start = selectionStart, end = selectionEnd;
					if(selectionStart > selectionEnd) {
						start = selectionEnd;
						end = selectionStart;
					}
					mInputConnection.setSelection(start, end);
					mHardShift = 0;
					updateMetaKeyStateDisplay();
				}
				return true;
				
			default:
				selectionMode = false;
				break;
			}
		} else {
			selectionMode = false;
		}
		
		if((key <= -200 && key > -300) || (key <= -2000 && key > -3000)) {
			int jamo = mHangulEngine.inputCode(key, mHardShift);
			if(jamo != -1) {
				if(mHardShift != 0) jamo = Character.toUpperCase(jamo);
				if(mHangulEngine.inputJamo(jamo) != 0) {
					mInputConnection.setComposingText(mHangulEngine.getComposing(), 1);
				}
			}
			return true;
		}
		
		if(ev.isPrintingKey()) {
			
			int code = ev.getUnicodeChar(mShiftKeyToggle[mHardShift] | mAltKeyToggle[mHardAlt]);
			this.inputChar((char) code, (mHardAlt == 0) ? false : true);
			
			if(mHardShift == 1){
				mShiftPressing = false;
			}
			if(mHardAlt == 1){
				mAltPressing   = false;
			}
			if (!ev.isAltPressed()) {
				if (mHardAlt == 1) {
					mHardAlt = 0;
				}
			}
			if (!ev.isShiftPressed()) {
				if (mHardShift == 1) {
					mHardShift = 0;
				}
			}
			if (!ev.isShiftPressed() && !ev.isShiftPressed()) {
				updateMetaKeyStateDisplay();
			}
			return true;
			
		} else if(key == KeyEvent.KEYCODE_SPACE) {
			mHangulEngine.resetJohab();
			if(mHardShift != 0) {
				((DefaultSoftKeyboardKOKR) mInputViewManager).nextLanguage();
				mHardShift = 0;
				mShiftPressing = false;
				updateMetaKeyStateDisplay();
				return true;
			}
			mInputConnection.commitText(" ", 1);
			return true;
		} else if(key == KeyEvent.KEYCODE_DEL) {
			if(!mHangulEngine.backspace()) {
				mHangulEngine.resetJohab();
				return false;
			}
			if(mHangulEngine.getComposing() == "")
				mHangulEngine.resetJohab();
			return true;
		} else if(key == KeyEvent.KEYCODE_ENTER) {
			mHangulEngine.resetJohab();
			mHardShift = 0;
			mHardAlt = 0;
			updateMetaKeyStateDisplay();
			return false;
		} else {
			mHangulEngine.resetJohab();
		}
		
		return false;
	}

	private void changeEngineMode(int mode) {
		switch(mode) {
		case ENGINE_MODE_OPT_TYPE_12KEY:
			mHangulEngine = m12keyEngine;
			return;
		case ENGINE_MODE_OPT_TYPE_QWERTY:
			mHangulEngine = mQwertyEngine;
			return;
		
		}
		
		mCurrentEngineMode = mode;
		
		switch(mode) {
		case OpenWnnEvent.Mode.DIRECT:
			mDirectInputMode = true;
			mEnableTimeout = false;
			break;
			
		case ENGINE_MODE_ENGLISH_DVORAK:
			mDirectInputMode = false;
			mEnableTimeout = false;
			mHangulEngine = mQwertyEngine;
			mHangulEngine.setJamoTable(CONVERT_ENGLISH_DVORAK);
			break;
			
		case ENGINE_MODE_DUBULSIK:
			mDirectInputMode = false;
			mEnableTimeout = false;
			mHangulEngine.setJamoTable(JAMO_DUBUL_STANDARD);
			mHangulEngine.setCombinationTable(COMB_DUBUL_STANDARD);
			break;
			
		case ENGINE_MODE_SEBUL_390:
			mDirectInputMode = false;
			mEnableTimeout = false;
			mHangulEngine.setJamoTable(JAMO_SEBUL_390);
			mHangulEngine.setCombinationTable(COMB_SEBULSIK);
			break;
			
		case ENGINE_MODE_SEBUL_391:
			mDirectInputMode = false;
			mEnableTimeout = false;
			mHangulEngine.setJamoTable(JAMO_SEBUL_391);
			mHangulEngine.setCombinationTable(COMB_SEBULSIK);
			break;
			
		case ENGINE_MODE_SEBUL_DANMOEUM:
			mDirectInputMode = false;
			mEnableTimeout = false;
			mHangulEngine.setJamoTable(JAMO_SEBUL_DANMOEUM);
			mHangulEngine.setCombinationTable(COMB_SEBUL_DANMOEUM);
			break;
			
		case ENGINE_MODE_SEBUL_SHIN_ORIGINAL:
			mDirectInputMode = false;
			mEnableTimeout = false;
			mHangulEngine.setJamoTable(JAMO_SEBUL_SHIN_ORIGINAL);
			mHangulEngine.setCombinationTable(COMB_SEBUL_SHIN_ORIGINAL);
			break;
			
		case ENGINE_MODE_SEBUL_SHIN_EDIT:
			mDirectInputMode = false;
			mEnableTimeout = false;
			mHangulEngine.setJamoTable(JAMO_SEBUL_SHIN_EDIT);
			mHangulEngine.setCombinationTable(COMB_SEBUL_SHIN_ORIGINAL);
			break;
			
		case ENGINE_MODE_DUBUL_DANMOEUM:
			mDirectInputMode = false;
			mEnableTimeout = true;
			mHangulEngine.setJamoTable(JAMO_DUBUL_DANMOEUM_GOOGLE);
			mHangulEngine.setCombinationTable(COMB_DUBUL_DANMOEUM_GOOGLE);
			break;
			
		case ENGINE_MODE_12KEY_ALPHABET:
			mDirectInputMode = false;
			mEnableTimeout = true;
			mHangulEngine.setJamoTable(CYCLE_12KEY_ALPHABET);
			break;
			
		case ENGINE_MODE_12KEY_SEBUL_MUNHWA:
			mDirectInputMode = false;
			mEnableTimeout = false;
			mHangulEngine.setJamoTable(CYCLE_SEBUL_12KEY_MUNHWA);
			mHangulEngine.setCombinationTable(COMB_SEBUL_12KEY_MUNHWA);
			((TwelveHangulEngine) mHangulEngine).setAddStrokeTable(STROKE_SEBUL_12KEY_MUNHWA);
			break;
			
		case ENGINE_MODE_12KEY_SEBUL_HANSON:
			mDirectInputMode = false;
			mEnableTimeout = false;
			mHangulEngine.setJamoTable(CYCLE_SEBUL_12KEY_HANSON);
			mHangulEngine.setCombinationTable(COMB_SEBUL_12KEY_HANSON);
			((TwelveHangulEngine) mHangulEngine).setAddStrokeTable(STROKE_SEBUL_12KEY_HANSON);
			break;
			
		}
	}
	
	private void onKeyUpEvent(KeyEvent ev) {
		int key = ev.getKeyCode();
		if(!mShiftPressing){
			if(key == KeyEvent.KEYCODE_SHIFT_LEFT || key == KeyEvent.KEYCODE_SHIFT_RIGHT){
				mHardShift = 0;
				mShiftPressing = true;
				updateMetaKeyStateDisplay();
			}
		}
		if(!mAltPressing ){
			if(key == KeyEvent.KEYCODE_ALT_LEFT || key == KeyEvent.KEYCODE_ALT_RIGHT){
				mHardAlt = 0;
				mAltPressing   = true;
				updateMetaKeyStateDisplay();
			}
		}
	}

	private void shinShift() {
		switch(mCurrentEngineMode) {
		case ENGINE_MODE_SEBUL_SHIN_ORIGINAL:
		case ENGINE_MODE_SEBUL_SHIN_EDIT:
			int type = mHangulEngine.getLastInputType();
			if(type == HangulEngine.INPUT_CHO3) {
				((DefaultSoftKeyboardKOKR) mInputViewManager).mShiftOn = DefaultSoftKeyboard.KEYBOARD_SHIFT_OFF;
				((DefaultSoftKeyboardKOKR) mInputViewManager).onKey(DefaultSoftKeyboard.KEYCODE_QWERTY_SHIFT, new int[] {});
				mHardShift = 2;
			} else if(type == HangulEngine.INPUT_JUNG3 || type == 0) {
				((DefaultSoftKeyboardKOKR) mInputViewManager).mShiftOn = DefaultSoftKeyboard.KEYBOARD_SHIFT_ON;
				((DefaultSoftKeyboardKOKR) mInputViewManager).onKey(DefaultSoftKeyboard.KEYCODE_QWERTY_SHIFT, new int[] {});
				mHardShift = 0;
			}
			break;
			
		default:
		}
	}

	protected int getShiftKeyState(EditorInfo editor) {
		return (getCurrentInputConnection().getCursorCapsMode(editor.inputType) == 0) ? 0 : 1;
	}

	private void updateMetaKeyStateDisplay() {
		int mode = 0;
		if(mHardShift == 0 && mHardAlt == 0){
			mode = DefaultSoftKeyboard.HARD_KEYMODE_SHIFT_OFF_ALT_OFF;
		} else if(mHardShift == 1 && mHardAlt == 0) {
			mode = DefaultSoftKeyboard.HARD_KEYMODE_SHIFT_ON_ALT_OFF;
		} else if(mHardShift == 2  && mHardAlt == 0) {
			mode = DefaultSoftKeyboard.HARD_KEYMODE_SHIFT_LOCK_ALT_OFF;
		} else if(mHardShift == 0 && mHardAlt == 1) {
			mode = DefaultSoftKeyboard.HARD_KEYMODE_SHIFT_OFF_ALT_ON;
		} else if(mHardShift == 0 && mHardAlt == 2) {
			mode = DefaultSoftKeyboard.HARD_KEYMODE_SHIFT_OFF_ALT_LOCK;
		} else if(mHardShift == 1 && mHardAlt == 1) {
			mode = DefaultSoftKeyboard.HARD_KEYMODE_SHIFT_ON_ALT_ON;
		} else if(mHardShift == 1 && mHardAlt == 2) {
			mode = DefaultSoftKeyboard.HARD_KEYMODE_SHIFT_ON_ALT_LOCK;
		} else if(mHardShift == 2 && mHardAlt == 1) {
			mode = DefaultSoftKeyboard.HARD_KEYMODE_SHIFT_LOCK_ALT_ON;
		} else if(mHardShift == 2 && mHardAlt == 2) {
			mode = DefaultSoftKeyboard.HARD_KEYMODE_SHIFT_LOCK_ALT_LOCK;
		} else{
			mode = DefaultSoftKeyboard.HARD_KEYMODE_SHIFT_OFF_ALT_OFF;
		}
		((DefaultSoftKeyboard) mInputViewManager).updateIndicator(mode);
		mode = DefaultSoftKeyboardKOKR.HARD_KEYMODE_LANG
				+ ((DefaultSoftKeyboard) mInputViewManager).mCurrentLanguage;
		((DefaultSoftKeyboard) mInputViewManager).updateIndicator(mode);
	}

	@Override
	public void hideWindow() {
		
		mInputViewManager.closing();
		
		super.hideWindow();
	}

	@Override
	public boolean onEvaluateFullscreenMode() {
		return false;
	}

	@Override
	public boolean onEvaluateInputViewShown() {
		return true;
	}

}
