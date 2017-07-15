package me.blog.hgl1002.openwnn;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Handler;
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

	public static final int[][] JAMO_DUBUL_NK = {

			{113, 0x3142, 0x3143},		// q
			{119, 0x3141, 0x3141},		// w
			{101, 0x3137, 0x3138},		// e
			{114, 0x3139, 0x3139},		// r
			{116, 0x314e, 0x314e},		// t
			{121, 0x3155, 0x3155},		// y
			{117, 0x315C, 0x315C},		// u
			{105, 0x3153, 0x3153},		// i
			{111, 0x3150, 0x3152},		// o
			{112, 0x3154, 0x3156},			// p
			
			{97, 0x3148, 0x3149},		// a
			{115, 0x3131, 0x3132},		// s
			{100, 0x3147, 0x3147},		// d
			{102, 0x3134, 0x3134},		// f
			{103, 0x3145, 0x3146},		// g
			{104, 0x3157, 0x3157},		// h
			{106, 0x314f, 0x314f},		// j
			{107, 0x3163, 0x3163},		// k
			{108, 0x3161, 0x3161},		// l
			
			{122, 0x314b, 0x314b},		// z
			{120, 0x314c, 0x314c},		// x
			{99, 0x314a, 0x314a},		// c
			{118, 0x314d, 0x314d},		// v
			{98, 0x3160, 0x3160},			// b
			{110, 0x315b, 0x315b},		// n
			{109, 0x3151, 0x3151},		// m
	};
	
		
	public static final int[][] JAMO_DUBUL_YET = {

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
			
			{97, 0x3141, 0x317f},		// a
			{115, 0x3134, 0x3136},		// s
			{100, 0x3147, 0x3181},		// d
			{102, 0x3139, 0x3140},		// f
			{103, 0x314e, 0x3186},		// g
			{104, 0x3157, 0x1183},		// h
			{106, 0x3153, 0x115f},		// j
			{107, 0x314f, 0x318d},		// k
			{108, 0x3163, 0x318c},		// l
			
			{122, 0x314b, 0x113c},		// z
			{120, 0x314c, 0x113e},		// x
			{99, 0x314a, 0x114e},		// c
			{118, 0x314d, 0x1150},		// v
			{98, 0x3160, 0x1154},			// b
			{110, 0x315c, 0x1155},		// n
			{109, 0x3161, 0x3161},		// m
	};
	
	public static final int[][] COMB_FULL = { // 새로 추가된 자판 코드들의 출처는 3beol 님의 libhangul과 Pat-al 님의 OHI 입니다. 
			{0x1100, 0x1100, 0x1101}, /* choseong kiyeok + kiyeok          = ssangkiyeok */
			{0x1100, 0x1103, 0x115a}, /* choseong kiyeok + tikeut          = kiyeok-tikeut */
			{0x1102, 0x1100, 0x1113}, /* choseong nieun + kiyeok           = nieun-kiyeok */
			{0x1102, 0x1102, 0x1114}, /* choseong nieun + nieun            = ssangnieun */
			{0x1102, 0x1103, 0x1115}, /* choseong nieun + tikeut           = nieun-tikeut */
			{0x1102, 0x1107, 0x1116}, /* choseong nieun + pieup            = nieun-pieup */
			{0x1102, 0x1109, 0x115b}, /* choseong nieun + sios             = nieun-sios */
			{0x1102, 0x110c, 0x115c}, /* choseong nieun + cieuc            = nieun-cieuc */
			{0x1102, 0x1112, 0x115d}, /* choseong nieun + hieuh            = nieun-hieuh */
			{0x1103, 0x1100, 0x1117}, /* choseong tikeut + kiyeok          = tikeut-kiyeok */
			{0x1103, 0x1103, 0x1104}, /* choseong tikeut + tikeut          = ssangtikeut */
			{0x1103, 0x1105, 0x115e}, /* choseong tikeut + rieul           = tikeut-rieul */
			{0x1103, 0x1106, 0xa960}, /* choseong tikeut + mieum           = tikeut-mieum */
			{0x1103, 0x1107, 0xa961}, /* choseong tikeut + pieup           = tikeut-pieup */
			{0x1103, 0x1109, 0xa962}, /* choseong tikeut + sios            = tikeut-sios */
			{0x1103, 0x110c, 0xa963}, /* choseong tikeut + cieuc           = tikeut-cieuc */
			{0x1105, 0x1100, 0xa964}, /* choseong rieul + kiyeok           = rieul-kiyeok */
			{0x1105, 0x1101, 0xa965}, /* choseong rieul + ssangkiyeok      = rieul-ssangkiyeok */
			{0x1105, 0x1102, 0x1118}, /* choseong rieul + nieun            = rieul-nieun */
			{0x1105, 0x1103, 0xa966}, /* choseong rieul + tikeut           = rieul-tikeut */
			{0x1105, 0x1104, 0xa967}, /* choseong rieul + ssangtikeut      = rieul-ssangtikeut */
			{0x1105, 0x1105, 0x1119}, /* choseong rieul + rieul            = ssangrieul */
			{0x1105, 0x1106, 0xa968}, /* choseong rieul + mieum            = rieul-mieum */
			{0x1105, 0x1107, 0xa969}, /* choseong rieul + pieup            = rieul-pieup */
			{0x1105, 0x1108, 0xa96a}, /* choseong rieul + ssangpieup       = rieul-ssangpieup */
			{0x1105, 0x1109, 0xa96c}, /* choseong rieul + sios             = rieul-sios */
			{0x1105, 0x110b, 0x111b}, /* choseong rieul + ieung            = kapyeounrieul */
			{0x1105, 0x110c, 0xa96d}, /* choseong rieul + cieuc            = rieul-cieuc */
			{0x1105, 0x110f, 0xa96e}, /* choseong rieul + khieukh          = rieul-khieukh */
			{0x1105, 0x1112, 0x111a}, /* choseong rieul + hieuh            = rieul-hieuh */
			{0x1105, 0x112b, 0xa96b}, /* choseong rieul + kapyeounpieup    = rieul-kapyeounpieup */
			{0x1106, 0x1100, 0xa96f}, /* choseong mieum + kiyeok           = mieum-kiyeok */
			{0x1106, 0x1103, 0xa970}, /* choseong mieum + tikeut           = mieum-tikeut */
			{0x1106, 0x1107, 0x111c}, /* choseong mieum + pieup            = mieum-pieup */
			{0x1106, 0x1109, 0xa971}, /* choseong mieum + sios             = mieum-sios */
			{0x1106, 0x110b, 0x111d}, /* choseong mieum + ieung            = kapyeounmieum */
			{0x1107, 0x1100, 0x111e}, /* choseong pieup + kiyeok           = pieup-kiyeok */
			{0x1107, 0x1102, 0x111f}, /* choseong pieup + nieun            = pieup-nieun */
			{0x1107, 0x1103, 0x1120}, /* choseong pieup + tikeut           = pieup-tikeut */
			{0x1107, 0x1107, 0x1108}, /* choseong pieup + pieup            = ssangpieup */
			{0x1107, 0x1109, 0x1121}, /* choseong pieup + sios             = pieup-sios */
			{0x1107, 0x110a, 0x1125}, /* choseong pieup + ssangsios        = pieup-ssangsios */
			{0x1107, 0x110b, 0x112b}, /* choseong pieup + ieung            = kapyeounpieup */
			{0x1107, 0x110c, 0x1127}, /* choseong pieup + cieuc            = pieup-cieuc */
			{0x1107, 0x110e, 0x1128}, /* choseong pieup + chieuch          = pieup-chieuch */
			{0x1107, 0x110f, 0xa973}, /* choseong pieup + khieukh          = pieup-khieukh */
			{0x1107, 0x1110, 0x1129}, /* choseong pieup + thieuth          = pieup-thieuth */
			{0x1107, 0x1111, 0x112a}, /* choseong pieup + phieuph          = pieup-phieuph */
			{0x1107, 0x1112, 0xa974}, /* choseong pieup + hieuh            = pieup-hieuh */
			{0x1107, 0x112b, 0x112c}, /* choseong pieup + kapyeounpieup    = kapyeounssangpieup */
			{0x1107, 0x112d, 0x1122}, /* choseong pieup + sios-kiyeok      = pieup-sios-kiyeok */
			{0x1107, 0x112f, 0x1123}, /* choseong pieup + sios-tikeut      = pieup-sios-tikeut */
			{0x1107, 0x1132, 0x1124}, /* choseong pieup + sios-pieup       = pieup-sios-pieup */
			{0x1107, 0x1136, 0x1126}, /* choseong pieup + sios-cieuc       = pieup-sios-cieuc */
			{0x1107, 0x1139, 0xa972}, /* choseong pieup + sios-thieuth     = pieup-sios-thieuth */
			{0x1108, 0x110b, 0x112c}, /* choseong ssangpieup + ieung       = kapyeounssangpieup */
			{0x1109, 0x1100, 0x112d}, /* choseong sios + kiyeok            = sios-kiyeok */
			{0x1109, 0x1102, 0x112e}, /* choseong sios + nieun             = sios-nieun */
			{0x1109, 0x1103, 0x112f}, /* choseong sios + tikeut            = sios-tikeut */
			{0x1109, 0x1105, 0x1130}, /* choseong sios + rieul             = sios-rieul */
			{0x1109, 0x1106, 0x1131}, /* choseong sios + mieum             = sios-mieum */
			{0x1109, 0x1107, 0x1132}, /* choseong sios + pieup             = sios-pieup */
			{0x1109, 0x1109, 0x110a}, /* choseong sios + sios              = ssangsios */
			{0x1109, 0x110a, 0x1134}, /* choseong sios + ssangsios         = sios-ssangsios */
			{0x1109, 0x110b, 0x1135}, /* choseong sios + ieung             = sios-ieung */
			{0x1109, 0x110c, 0x1136}, /* choseong sios + cieuc             = sios-cieuc */
			{0x1109, 0x110e, 0x1137}, /* choseong sios + chieuch           = sios-chieuch */
			{0x1109, 0x110f, 0x1138}, /* choseong sios + khieukh           = sios-khieukh */
			{0x1109, 0x1110, 0x1139}, /* choseong sios + thieuth           = sios-thieuth */
			{0x1109, 0x1111, 0x113a}, /* choseong sios + phieuph           = sios-phieuph */
			{0x1109, 0x1112, 0x113b}, /* choseong sios + hieuh             = sios-hieuh */
			{0x1109, 0x111e, 0x1133}, /* choseong sios + pieup-kiyeok      = sios-pieup-kiyeok */
			{0x1109, 0x1132, 0xa975}, /* choseong sios + sios-pieup        = ssangsios-pieup */
			{0x110a, 0x1107, 0xa975}, /* choseong ssangsios + pieup        = ssangsios-pieup */
			{0x110a, 0x1109, 0x1134}, /* choseong ssangsios + sios         = sios-ssangsios */
			{0x110b, 0x1100, 0x1141}, /* choseong ieung + kiyeok           = ieung-kiyeok */
			{0x110b, 0x1103, 0x1142}, /* choseong ieung + tikeut           = ieung-tikeut */
			{0x110b, 0x1105, 0xa976}, /* choseong ieung + rieul            = ieung-rieul */
			{0x110b, 0x1106, 0x1143}, /* choseong ieung + mieum            = ieung-mieum */
			{0x110b, 0x1107, 0x1144}, /* choseong ieung + pieup            = ieung-pieup */
			{0x110b, 0x1109, 0x1145}, /* choseong ieung + sios             = ieung-sios */
			{0x110b, 0x110b, 0x1147}, /* choseong ieung + ieung            = ssangieung */
			{0x110b, 0x110c, 0x1148}, /* choseong ieung + cieuc            = ieung-cieuc */
			{0x110b, 0x110e, 0x1149}, /* choseong ieung + chieuch          = ieung-chieuch */
			{0x110b, 0x1110, 0x114a}, /* choseong ieung + thieuth          = ieung-thieuth */
			{0x110b, 0x1111, 0x114b}, /* choseong ieung + phieuph          = ieung-phieuph */
			{0x110b, 0x1112, 0xa977}, /* choseong ieung + hieuh            = ieung-hieuh */
			{0x110b, 0x1140, 0x1146}, /* choseong ieung + pansios          = ieung-pansios */
			{0x110c, 0x110b, 0x114d}, /* choseong cieuc + ieung            = cieuc-ieung */
			{0x110c, 0x110c, 0x110d}, /* choseong cieuc + cieuc            = ssangcieuc */
			{0x110d, 0x1112, 0xa978}, /* choseong ssangcieuc + hieuh       = ssangcieuc-hieuh */
			{0x110e, 0x110f, 0x1152}, /* choseong chieuch + khieukh        = chieuch-khieukh */
			{0x110e, 0x1112, 0x1153}, /* choseong chieuch + hieuh          = chieuch-hieuh */
			{0x1110, 0x1110, 0xa979}, /* choseong thieuth + thieuth        = ssangthieuth */
			{0x1111, 0x1107, 0x1156}, /* choseong phieuph + pieup          = phieuph-pieup */
			{0x1111, 0x110b, 0x1157}, /* choseong phieuph + ieung          = kapyeounphieuph */
			{0x1111, 0x1112, 0xa97a}, /* choseong phieuph + hieuh          = phieuph-hieuh */
			{0x1112, 0x1109, 0xa97b}, /* choseong hieuh + sios             = hieuh-sios */
			{0x1112, 0x1112, 0x1158}, /* choseong hieuh + hieuh            = ssanghieuh */
			{0x1121, 0x1100, 0x1122}, /* choseong pieup-sios + kiyeok      = pieup-sios-kiyeok */
			{0x1121, 0x1103, 0x1123}, /* choseong pieup-sios + tikeut      = pieup-sios-tikeut */
			{0x1121, 0x1107, 0x1124}, /* choseong pieup-sios + pieup       = pieup-sios-pieup */
			{0x1121, 0x1109, 0x1125}, /* choseong pieup-sios + sios        = pieup-ssangsios */
			{0x1121, 0x110c, 0x1126}, /* choseong pieup-sios + cieuc       = pieup-sios-cieuc */
			{0x1121, 0x1110, 0xa972}, /* choseong pieup-sios + thieuth     = pieup-sios-thieuth */
			{0x1132, 0x1100, 0x1133}, /* choseong sios-pieup + kiyeok      = sios-pieup-kiyeok */
			{0x113c, 0x113c, 0x113d}, /* choseong chitueumsios + chitueumsios = chitueumssangsios */
			{0x113e, 0x113e, 0x113f}, /* choseong ceongchieumsios + ceongchieumsios = ceongchieumssangsios */
			{0x114e, 0x114e, 0x114f}, /* choseong chitueumcieuc + chitueumcieuc = chitueumssangcieuc */
			{0x1150, 0x1150, 0x1151}, /* choseong ceongchieumcieuc + ceongchieumcieuc = ceongchieumssangcieuc */
			{0x1159, 0x1159, 0xa97c}, /* choseong yeorinhieuh + yeorinhieuh = ssangyeorinhieuh */
			
			{0x1161, 0x1161, 0x119e}, /* jungseong a + a                   = arae-a */
			{0x1161, 0x1169, 0x1176}, /* jungseong a + o                   = a-o */
			{0x1161, 0x116e, 0x1177}, /* jungseong a + u                   = a-u */
			{0x1161, 0x1173, 0x11a3}, /* jungseong a + eu                  = a-eu */
			{0x1161, 0x1175, 0x1162}, /* jungseong a + i                   = ae */
			{0x1163, 0x1169, 0x1178}, /* jungseong ya + o                  = ya-o */
			{0x1163, 0x116d, 0x1179}, /* jungseong ya + yo                 = ya-yo */
			{0x1163, 0x116e, 0x11a4}, /* jungseong ya + u                  = ya-u */
			{0x1163, 0x1175, 0x1164}, /* jungseong ya + i                  = yae */
			{0x1165, 0x1169, 0x117a}, /* jungseong eo + o                  = eo-o */
			{0x1165, 0x116e, 0x117b}, /* jungseong eo + u                  = eo-u */
			{0x1165, 0x1173, 0x117c}, /* jungseong eo + eu                 = eo-eu */
			{0x1165, 0x1175, 0x1166}, /* jungseong eo + i                  = e */
			{0x1167, 0x1163, 0x11a5}, /* jungseong yeo + ya                = yeo-ya */
			{0x1167, 0x1169, 0x117d}, /* jungseong yeo + o                 = yeo-o */
			{0x1167, 0x116e, 0x117e}, /* jungseong yeo + u                 = yeo-u */
			{0x1167, 0x1175, 0x1168}, /* jungseong yeo + i                 = ye */
			{0x1169, 0x1161, 0x116a}, /* jungseong o + a                   = wa */
			{0x1169, 0x1162, 0x116b}, /* jungseong o + ae                  = wae */
			{0x1169, 0x1163, 0x11a6}, /* jungseong o + ya                  = o-ya */
			{0x1169, 0x1164, 0x11a7}, /* jungseong o + yae                 = o-yae */
			{0x1169, 0x1165, 0x117f}, /* jungseong o + eo                  = o-eo */
			{0x1169, 0x1166, 0x1180}, /* jungseong o + e                   = o-e */
			{0x1169, 0x1167, 0xd7b0}, /* jungseong o + yeo                 = o-yeo */
			{0x1169, 0x1168, 0x1181}, /* jungseong o + ye                  = o-ye */
			{0x1169, 0x1169, 0x1182}, /* jungseong o + o                   = o-o */
			{0x1169, 0x116e, 0x1183}, /* jungseong o + u                   = o-u */
			{0x1169, 0x1175, 0x116c}, /* jungseong o + i                   = oe */
			{0x116a, 0x1175, 0x116b}, /* jungseong wa + i                  = wae */
			{0x116d, 0x1161, 0xd7b2}, /* jungseong yo + a                  = yo-a */
			{0x116d, 0x1162, 0xd7b3}, /* jungseong yo + ae                 = yo-ae */
			{0x116d, 0x1163, 0x1184}, /* jungseong yo + ya                 = yo-ya */
			{0x116d, 0x1164, 0x1185}, /* jungseong yo + yae                = yo-yae */
			{0x116d, 0x1165, 0xd7b4}, /* jungseong yo + eo                 = yo-eo */
			{0x116d, 0x1167, 0x1186}, /* jungseong yo + yeo                = yo-yeo */
			{0x116d, 0x1169, 0x1187}, /* jungseong yo + o                  = yo-o */
			{0x116d, 0x1175, 0x1188}, /* jungseong yo + i                  = yo-i */
			{0x116e, 0x1161, 0x1189}, /* jungseong u + a                   = u-a */
			{0x116e, 0x1162, 0x118a}, /* jungseong u + ae                  = u-ae */
			{0x116e, 0x1165, 0x116f}, /* jungseong u + eo                  = weo */
			{0x116e, 0x1166, 0x1170}, /* jungseong u + e                   = we */
			{0x116e, 0x1167, 0xd7b5}, /* jungseong u + yeo                 = u-yeo */
			{0x116e, 0x1168, 0x118c}, /* jungseong u + ye                  = u-ye */
			{0x116e, 0x116e, 0x118d}, /* jungseong u + u                   = u-u */
			{0x116e, 0x1175, 0x1171}, /* jungseong u + i                   = wi */
			{0x116e, 0x117c, 0x118b}, /* jungseong u + eo-eu               = u-eo-eu */
			{0x116e, 0xd7c4, 0xd7b6}, /* jungseong u + i-i                 = u-i-i */
			{0x116f, 0x1173, 0x118b}, /* jungseong weo + eu                = u-eo-eu */
			{0x116f, 0x1175, 0x1170}, /* jungseong weo + i                 = we */
			{0x1171, 0x1175, 0xd7b6}, /* jungseong wi + i                  = u-i-i */
			{0x1172, 0x1161, 0x118e}, /* jungseong yu + a                  = yu-a */
			{0x1172, 0x1162, 0xd7b7}, /* jungseong yu + ae                 = yu-ae */
			{0x1172, 0x1165, 0x118f}, /* jungseong yu + eo                 = yu-eo */
			{0x1172, 0x1166, 0x1190}, /* jungseong yu + e                  = yu-e */
			{0x1172, 0x1167, 0x1191}, /* jungseong yu + yeo                = yu-yeo */
			{0x1172, 0x1168, 0x1192}, /* jungseong yu + ye                 = yu-ye */
			{0x1172, 0x1169, 0xd7b8}, /* jungseong yu + o                  = yu-o */
			{0x1172, 0x116e, 0x1193}, /* jungseong yu + u                  = yu-u */
			{0x1172, 0x1175, 0x1194}, /* jungseong yu + i                  = yu-i */
			{0x1173, 0x1161, 0xd7b9}, /* jungseong eu + a                  = eu-a */
			{0x1173, 0x1165, 0xd7ba}, /* jungseong eu + eo                 = eu-eo */
			{0x1173, 0x1166, 0xd7bb}, /* jungseong eu + e                  = eu-e */
			{0x1173, 0x1169, 0xd7bc}, /* jungseong eu + o                  = eu-o */
			{0x1173, 0x116e, 0x1195}, /* jungseong eu + u                  = eu-u */
			{0x1173, 0x1173, 0x1196}, /* jungseong eu + eu                 = eu-eu */
			{0x1173, 0x1175, 0x1174}, /* jungseong eu + i                  = yi */
			{0x1174, 0x116e, 0x1197}, /* jungseong yi + u                  = yi-u */
			{0x1175, 0x1161, 0x1198}, /* jungseong i + a                   = i-a */
			{0x1175, 0x1163, 0x1199}, /* jungseong i + ya                  = i-ya */
			{0x1175, 0x1164, 0xd7be}, /* jungseong i + yae                 = i-yae */
			{0x1175, 0x1167, 0xd7bf}, /* jungseong i + yeo                 = i-yeo */
			{0x1175, 0x1168, 0xd7c0}, /* jungseong i + ye                  = i-ye */
			{0x1175, 0x1169, 0x119a}, /* jungseong i + o                   = i-o */
			{0x1175, 0x116d, 0xd7c2}, /* jungseong i + yo                  = i-yo */
			{0x1175, 0x116e, 0x119b}, /* jungseong i + u                   = i-u */
			{0x1175, 0x1172, 0xd7c3}, /* jungseong i + yu                  = i-yu */
			{0x1175, 0x1173, 0x119c}, /* jungseong i + eu                  = i-eu */
			{0x1175, 0x1175, 0xd7c4}, /* jungseong i + i                   = i-i */
			{0x1175, 0x1178, 0xd7bd}, /* jungseong i + ya-o                = i-ya-o */
			{0x1175, 0x119e, 0x119d}, /* jungseong i + araea               = i-araea */
			{0x1182, 0x1175, 0xd7b1}, /* jungseong o-o + i                 = o-o-i */
			{0x1199, 0x1169, 0xd7bd}, /* jungseong i-ya + o                = i-ya-o */
			{0x119a, 0x1175, 0xd7c1}, /* jungseong i-o + i                 = i-o-i */
			{0x119e, 0x1161, 0xd7c5}, /* jungseong araea + a               = araea-a */
			{0x119e, 0x1165, 0x119f}, /* jungseong araea + eo              = araea-eo */
			{0x119e, 0x1166, 0xd7c6}, /* jungseong araea + e               = araea-e */
			{0x119e, 0x116e, 0x11a0}, /* jungseong araea + u               = araea-u */
			{0x119e, 0x1175, 0x11a1}, /* jungseong araea + i               = araea-i */
			{0x119e, 0x119e, 0x11a2}, /* jungseong araea + araea           = ssangaraea */
			
			{0x11a8, 0x11a8, 0x11a9}, /* jongseong kiyeok + kiyeok         = ssangkiyeok */
			{0x11a8, 0x11ab, 0x11fa}, /* jongseong kiyeok + nieun          = kiyeok-nieun */
			{0x11a8, 0x11af, 0x11c3}, /* jongseong kiyeok + rieul          = kiyeok-rieul */
			{0x11a8, 0x11b8, 0x11fb}, /* jongseong kiyeok + pieup          = kiyeok-pieup */
			{0x11a8, 0x11ba, 0x11aa}, /* jongseong kiyeok + sios           = kiyeok-sios */
			{0x11a8, 0x11be, 0x11fc}, /* jongseong kiyeok + chieuch        = kiyeok-chieuch */
			{0x11a8, 0x11bf, 0x11fd}, /* jongseong kiyeok + khieukh        = kiyeok-khieukh */
			{0x11a8, 0x11c2, 0x11fe}, /* jongseong kiyeok + hieuh          = kiyeok-hieuh */
			{0x11a8, 0x11e7, 0x11c4}, /* jongseong kiyeok + sios-kiyeok    = kiyeok-sios-kiyeok */
			{0x11aa, 0x11a8, 0x11c4}, /* jongseong kiyeok-sios + kiyeok    = kiyeok-sios-kiyeok */
			{0x11ab, 0x11a8, 0x11c5}, /* jongseong nieun + kiyeok          = nieun-kiyeok */
			{0x11ab, 0x11ab, 0x11ff}, /* jongseong nieun + nieun           = ssangnieun */
			{0x11ab, 0x11ae, 0x11c6}, /* jongseong nieun + tikeut          = nieun-tikeut */
			{0x11ab, 0x11af, 0xd7cb}, /* jongseong nieun + rieul           = nieun-rieul */
			{0x11ab, 0x11ba, 0x11c7}, /* jongseong nieun + sios            = nieun-sios */
			{0x11ab, 0x11bd, 0x11ac}, /* jongseong nieun + cieuc           = nieun-cieuc */
			{0x11ab, 0x11be, 0xd7cc}, /* jongseong nieun + chieuch         = nieun-chieuch */
			{0x11ab, 0x11c0, 0x11c9}, /* jongseong nieun + thieuth         = nieun-thieuth */
			{0x11ab, 0x11c2, 0x11ad}, /* jongseong nieun + hieuh           = nieun-hieuh */
			{0x11ab, 0x11eb, 0x11c8}, /* jongseong nieun + pansios         = nieun-pansios */
			{0x11ae, 0x11a8, 0x11ca}, /* jongseong tikeut + kiyeok         = tikeut-kiyeok */
			{0x11ae, 0x11ae, 0xd7cd}, /* jongseong tikeut + tikeut         = ssangtikeut */
			{0x11ae, 0x11af, 0x11cb}, /* jongseong tikeut + rieul          = tikeut-rieul */
			{0x11ae, 0x11b8, 0xd7cf}, /* jongseong tikeut + pieup          = tikeut-pieup */
			{0x11ae, 0x11ba, 0xd7d0}, /* jongseong tikeut + sios           = tikeut-sios */
			{0x11ae, 0x11bd, 0xd7d2}, /* jongseong tikeut + cieuc          = tikeut-cieuc */
			{0x11ae, 0x11be, 0xd7d3}, /* jongseong tikeut + chieuch        = tikeut-chieuch */
			{0x11ae, 0x11c0, 0xd7d4}, /* jongseong tikeut + thieuth        = tikeut-thieuth */
			{0x11ae, 0x11e7, 0xd7d1}, /* jongseong tikeut + sios-kiyeok    = tikeut-sios-kiyeok */
			{0x11ae, 0xd7cf, 0xd7ce}, /* jongseong tikeut + tikeut-pieup   = ssangtikeut-pieup */
			{0x11af, 0x11a8, 0x11b0}, /* jongseong rieul + kiyeok          = rieul-kiyeok */
			{0x11af, 0x11a9, 0xd7d5}, /* jongseong rieul + ssangkiyeok     = rieul-ssangkiyeok */
			{0x11af, 0x11aa, 0x11cc}, /* jongseong rieul + kiyeok-sios     = rieul-kiyeok-sios */
			{0x11af, 0x11ab, 0x11cd}, /* jongseong rieul + nieun           = rieul-nieun */
			{0x11af, 0x11ae, 0x11ce}, /* jongseong rieul + tikeut          = rieul-tikeut */
			{0x11af, 0x11af, 0x11d0}, /* jongseong rieul + rieul           = ssangrieul */
			{0x11af, 0x11b7, 0x11b1}, /* jongseong rieul + mieum           = rieul-mieum */
			{0x11af, 0x11b8, 0x11b2}, /* jongseong rieul + pieup           = rieul-pieup */
			{0x11af, 0x11b9, 0x11d3}, /* jongseong rieul + pieup-sios      = rieul-pieup-sios */
			{0x11af, 0x11ba, 0x11b3}, /* jongseong rieul + sios            = rieul-sios */
			{0x11af, 0x11bb, 0x11d6}, /* jongseong rieul + ssangsios       = rieul-ssangsios */
			{0x11af, 0x11bc, 0xd7dd}, /* jongseong rieul + ieung           = kapyeounrieul */
			{0x11af, 0x11bf, 0x11d8}, /* jongseong rieul + khieukh         = rieul-khieukh */
			{0x11af, 0x11c0, 0x11b4}, /* jongseong rieul + thieuth         = rieul-thieuth */
			{0x11af, 0x11c1, 0x11b5}, /* jongseong rieul + phieuph         = rieul-phieuph */
			{0x11af, 0x11c2, 0x11b6}, /* jongseong rieul + hieuh           = rieul-hieuh */
			{0x11af, 0x11d8, 0xd7d7}, /* jongseong rieul + rieul-khieukh   = ssangrieul-khieukh */
			{0x11af, 0x11da, 0x11d1}, /* jongseong rieul + mieum-kiyeok    = rieul-mieum-kiyeok */
			{0x11af, 0x11dd, 0x11d2}, /* jongseong rieul + mieum-sios      = rieul-mieum-sios */
			{0x11af, 0x11e1, 0xd7d8}, /* jongseong rieul + mieum-hieuh     = rieul-mieum-hieuh */
			{0x11af, 0x11e4, 0xd7da}, /* jongseong rieul + pieup-phieuph   = rieul-pieup-phieuph */
			{0x11af, 0x11e5, 0x11d4}, /* jongseong rieul + pieup-hieuh     = rieul-pieup-hieuh */
			{0x11af, 0x11e6, 0x11d5}, /* jongseong rieul + kapyeounpieup   = rieul-kapyeounpieup */
			{0x11af, 0x11eb, 0x11d7}, /* jongseong rieul + pansios         = rieul-pansios */
			{0x11af, 0x11f0, 0xd7db}, /* jongseong rieul + yesieung        = rieul-yesieung */
			{0x11af, 0x11f9, 0x11d9}, /* jongseong rieul + yeorinhieuh     = rieul-yeorinhieuh */
			{0x11af, 0x11fe, 0xd7d6}, /* jongseong rieul + kiyeok-hieuh    = rieul-kiyeok-hieuh */
			{0x11af, 0xd7e3, 0xd7d9}, /* jongseong rieul + pieup-tikeut    = rieul-pieup-tikeut */
			{0x11b0, 0x11a8, 0xd7d5}, /* jongseong rieul-kiyeok + kiyeok   = rieul-ssangkiyeok */
			{0x11b0, 0x11ba, 0x11cc}, /* jongseong rieul-kiyeok + sios     = rieul-kiyeok-sios */
			{0x11b0, 0x11c2, 0xd7d6}, /* jongseong rieul-kiyeok + hieuh    = rieul-kiyeok-hieuh */
			{0x11b1, 0x11a8, 0x11d1}, /* jongseong rieul-mieum + kiyeok    = rieul-mieum-kiyeok */
			{0x11b1, 0x11ba, 0x11d2}, /* jongseong rieul-mieum + sios      = rieul-mieum-sios */
			{0x11b1, 0x11c2, 0xd7d8}, /* jongseong rieul-mieum + hieuh     = rieul-mieum-hieuh */
			{0x11b2, 0x11ae, 0xd7d9}, /* jongseong rieul-pieup + tikeut    = rieul-pieup-tikeut */
			{0x11b2, 0x11ba, 0x11d3}, /* jongseong rieul-pieup + sios      = rieul-pieup-sios */
			{0x11b2, 0x11bc, 0x11d5}, /* jongseong rieul-pieup + ieung     = rieul-kapyeounpieup */
			{0x11b2, 0x11c1, 0xd7da}, /* jongseong rieul-pieup + phieuph   = rieul-pieup-phieuph */
			{0x11b2, 0x11c2, 0x11d4}, /* jongseong rieul-pieup + hieuh     = rieul-pieup-hieuh */
			{0x11b3, 0x11ba, 0x11d6}, /* jongseong rieul-sios + sios       = rieul-ssangsios */
			{0x11b7, 0x11a8, 0x11da}, /* jongseong mieum + kiyeok          = mieum-kiyeok */
			{0x11b7, 0x11ab, 0xd7de}, /* jongseong mieum + nieun           = mieum-nieun */
			{0x11b7, 0x11af, 0x11db}, /* jongseong mieum + rieul           = mieum-rieul */
			{0x11b7, 0x11b7, 0xd7e0}, /* jongseong mieum + mieum           = ssangmieum */
			{0x11b7, 0x11b8, 0x11dc}, /* jongseong mieum + pieup           = mieum-pieup */
			{0x11b7, 0x11b9, 0xd7e1}, /* jongseong mieum + pieup-sios      = mieum-pieup-sios */
			{0x11b7, 0x11ba, 0x11dd}, /* jongseong mieum + sios            = mieum-sios */
			{0x11b7, 0x11bb, 0x11de}, /* jongseong mieum + ssangsios       = mieum-ssangsios */
			{0x11b7, 0x11bc, 0x11e2}, /* jongseong mieum + ieung           = kapyeounmieum */
			{0x11b7, 0x11bd, 0xd7e2}, /* jongseong mieum + cieuc           = mieum-cieuc */
			{0x11b7, 0x11be, 0x11e0}, /* jongseong mieum + chieuch         = mieum-chieuch */
			{0x11b7, 0x11c2, 0x11e1}, /* jongseong mieum + hieuh           = mieum-hieuh */
			{0x11b7, 0x11eb, 0x11df}, /* jongseong mieum + pansios         = mieum-pansios */
			{0x11b7, 0x11ff, 0xd7df}, /* jongseong mieum + ssangnieun      = mieum-ssangnieun */
			{0x11b8, 0x11ae, 0xd7e3}, /* jongseong pieup + tikeut          = pieup-tikeut */
			{0x11b8, 0x11af, 0x11e3}, /* jongseong pieup + rieul           = pieup-rieul */
			{0x11b8, 0x11b5, 0xd7e4}, /* jongseong pieup + rieul-phieuph   = pieup-rieul-phieuph */
			{0x11b8, 0x11b7, 0xd7e5}, /* jongseong pieup + mieum           = pieup-mieum */
			{0x11b8, 0x11b8, 0xd7e6}, /* jongseong pieup + pieup           = ssangpieup */
			{0x11b8, 0x11ba, 0x11b9}, /* jongseong pieup + sios            = pieup-sios */
			{0x11b8, 0x11bc, 0x11e6}, /* jongseong pieup + ieung           = kapyeounpieup */
			{0x11b8, 0x11bd, 0xd7e8}, /* jongseong pieup + cieuc           = pieup-cieuc */
			{0x11b8, 0x11be, 0xd7e9}, /* jongseong pieup + chieuch         = pieup-chieuch */
			{0x11b8, 0x11c1, 0x11e4}, /* jongseong pieup + phieuph         = pieup-phieuph */
			{0x11b8, 0x11c2, 0x11e5}, /* jongseong pieup + hieuh           = pieup-hieuh */
			{0x11b8, 0x11e8, 0xd7e7}, /* jongseong pieup + sios-tikeut     = pieup-sios-tikeut */
			{0x11b9, 0x11ae, 0xd7e7}, /* jongseong pieup-sios + tikeut     = pieup-sios-tikeut */
			{0x11ba, 0x11a8, 0x11e7}, /* jongseong sios + kiyeok           = sios-kiyeok */
			{0x11ba, 0x11ae, 0x11e8}, /* jongseong sios + tikeut           = sios-tikeut */
			{0x11ba, 0x11af, 0x11e9}, /* jongseong sios + rieul            = sios-rieul */
			{0x11ba, 0x11b7, 0xd7ea}, /* jongseong sios + mieum            = sios-mieum */
			{0x11ba, 0x11b8, 0x11ea}, /* jongseong sios + pieup            = sios-pieup */
			{0x11ba, 0x11ba, 0x11bb}, /* jongseong sios + sios             = ssangsios */
			{0x11ba, 0x11bd, 0xd7ef}, /* jongseong sios + cieuc            = sios-cieuc */
			{0x11ba, 0x11be, 0xd7f0}, /* jongseong sios + chieuch          = sios-chieuch */
			{0x11ba, 0x11c0, 0xd7f1}, /* jongseong sios + thieuth          = sios-thieuth */
			{0x11ba, 0x11c2, 0xd7f2}, /* jongseong sios + hieuh            = sios-hieuh */
			{0x11ba, 0x11e6, 0xd7eb}, /* jongseong sios + kapyeounpieup    = sios-kapyeounpieup */
			{0x11ba, 0x11e7, 0xd7ec}, /* jongseong sios + sios-kiyeok      = ssangsios-kiyeok */
			{0x11ba, 0x11e8, 0xd7ed}, /* jongseong sios + sios-tikeut      = ssangsios-tikeut */
			{0x11ba, 0x11eb, 0xd7ee}, /* jongseong sios + pansios          = sios-pansios */
			{0x11bb, 0x11a8, 0xd7ec}, /* jongseong ssangsios + kiyeok      = ssangsios-kiyeok */
			{0x11bb, 0x11ae, 0xd7ed}, /* jongseong ssangsios + tikeut      = ssangsios-tikeut */
			{0x11bd, 0x11b8, 0xd7f7}, /* jongseong cieuc + pieup           = cieuc-pieup */
			{0x11bd, 0x11bd, 0xd7f9}, /* jongseong cieuc + cieuc           = ssangcieuc */
			{0x11bd, 0xd7e6, 0xd7f8}, /* jongseong cieuc + ssangpieup      = cieuc-ssangpieup */
			{0x11c1, 0x11b8, 0x11f3}, /* jongseong phieuph + pieup         = phieuph-pieup */
			{0x11c1, 0x11ba, 0xd7fa}, /* jongseong phieuph + sios          = phieuph-sios */
			{0x11c1, 0x11bc, 0x11f4}, /* jongseong phieuph + ieung         = kapyeounphieuph */
			{0x11c1, 0x11c0, 0xd7fb}, /* jongseong phieuph + thieuth       = phieuph-thieuth */
			{0x11c2, 0x11ab, 0x11f5}, /* jongseong hieuh + nieun           = hieuh-nieun */
			{0x11c2, 0x11af, 0x11f6}, /* jongseong hieuh + rieul           = hieuh-rieul */
			{0x11c2, 0x11b7, 0x11f7}, /* jongseong hieuh + mieum           = hieuh-mieum */
			{0x11c2, 0x11b8, 0x11f8}, /* jongseong hieuh + pieup           = hieuh-pieup */
			{0x11ce, 0x11c2, 0x11cf}, /* jongseong rieul-tikeut + hieuh    = rieul-tikeut-hieuh */
			{0x11d0, 0x11bf, 0xd7d7}, /* jongseong ssangrieul + khieukh    = ssangrieul-khieukh */
			{0x11d9, 0x11c2, 0xd7dc}, /* jongseong rieul-yeorinhieuh + hieuh = rieul-yeorinhieuh-hieuh */
			{0x11dc, 0x11ba, 0xd7e1}, /* jongseong mieum-pieup + sios      = mieum-pieup-sios */
			{0x11dd, 0x11ba, 0x11de}, /* jongseong mieum-sios + sios       = mieum-ssangsios */
			{0x11e3, 0x11c1, 0xd7e4}, /* jongseong pieup-rieul + phieuph   = pieup-rieul-phieuph */
			{0x11ea, 0x11bc, 0xd7eb}, /* jongseong sios-pieup + ieung      = sios-kapyeounpieup */
			{0x11eb, 0x11b8, 0xd7f3}, /* jongseong pansios + pieup         = pansios-pieup */
			{0x11eb, 0x11e6, 0xd7f4}, /* jongseong pansios + kapyeounpieup = pansios-kapyeounpieup */
			{0x11ec, 0x11a8, 0x11ed}, /* jongseong ieung-kiyeok + kiyeok   = ieung-ssangkiyeok */
			{0x11f0, 0x11a8, 0x11ec}, /* jongseong yesieung + kiyeok       = yesieung-kiyeok */
			{0x11f0, 0x11a9, 0x11ed}, /* jongseong yesieung + ssangkiyeok  = yesieung-ssangkiyeok */
			{0x11f0, 0x11b7, 0xd7f5}, /* jongseong yesieung + mieum        = yesieung-mieum */
			{0x11f0, 0x11ba, 0x11f1}, /* jongseong yesieung + sios         = yesieung-sios */
			{0x11f0, 0x11bf, 0x11ef}, /* jongseong yesieung + khieukh      = yesieung-khieukh */
			{0x11f0, 0x11c2, 0xd7f6}, /* jongseong yesieung + hieuh        = yesieung-hieuh */
			{0x11f0, 0x11eb, 0x11f2}, /* jongseong yesieung + pansios      = yesieung-pansios */
			{0x11f0, 0x11f0, 0x11ee}, /* jongseong yesieung + yesieung     = ssangyesieung */
			
			{0xa964, 0x1100, 0xa965}, /* choseong rieul-kiyeok + kiyeok    = rieul-ssangkiyeok */
			{0xa966, 0x1103, 0xa967}, /* choseong rieul-tikeut + tikeut    = rieul-ssangtikeut */
			{0xa969, 0x1107, 0xa96a}, /* choseong rieul-pieup + pieup      = rieul-ssangpieup */
			{0xa969, 0x110b, 0xa96b}, /* choseong rieul-pieup + ieung      = rieul-kapyeounpieup */
			
			{0xd7c5, 0x1161, 0x11a2}, /* jungseong araea-a + a             = ssangaraea */
			
			{0xd7cd, 0x11b8, 0xd7ce}, /* jongseong ssangtikeut + pieup     = ssangtikeut-pieup */
			{0xd7d0, 0x11a8, 0xd7d1}, /* jongseong tikeut-sios + kiyeok    = tikeut-sios-kiyeok */
			{0xd7de, 0x11ab, 0xd7df}, /* jongseong mieum-nieun + nieun     = mieum-ssangnieun */
			{0xd7f3, 0x11bc, 0xd7f4}, /* jongseong pansios-pieup + ieung   = pansios-kapyeounpieup */
			{0xd7f7, 0x11b8, 0xd7f8}, /* jongseong cieuc-pieup + pieup     = cieuc-ssangpieup */
			
			// 아래는 신세벌식 P2 만의 조합이지만 위의 코드를 함께 쓰는 신세벌식 P2 자판의 코드 중복을 방지하기 위해 여기에 붙였습니다.
			
			{0x1100, 0x1109, 0x1140}, /* choseong gieug + siues = ssanggieug */
			{0x1100, 0x110B, 0x114C}, /* choseong gieug + ieung = yesieung */
			{0x1100, 0x1112, 0x1159}, /* choseong gieug + hiueh = yeolinhieuh */
			{0x1159, 0x1112, 0xA97C}, /* choseong yeolinhieuh + hiueh = ssangyeolinhieuh */
			{0x1141, 0x1109, 0x1146}, /* choseong ieung-gieug + sieus = ieung-yeolinsieus */
			{0x110C, 0x1109, 0x113C}, /* choseong jieuj + siues = ab-sieus */
			{0x113C, 0x1109, 0x113D}, /* choseong ab-sieus + sieus = ssang-ab-sieus */
			{0x110E, 0x1109, 0x113E}, /* choseong chieuch + siues = dwis-sieus */
			{0x113E, 0x1109, 0x113F}, /* choseong dwis-sieus + sieus = ssang-dwis-sieus */
			{0x110C, 0x1103, 0x114E}, /* choseong jieuj + dieug = ab-jieuj */
			{0x110D, 0x1103, 0x114F}, /* choseong ssangjieuj + dieud = ssang-ab-jieuj */
			{0x110C, 0x1100, 0x1150}, /* choseong jieuj + gieug = dwis-jieuj */
			{0x110D, 0x1100, 0x1151}, /* choseong ssangjieuj + gieug = ssang-dwis-jieuj */
			{0x110E, 0x1103, 0x1154}, /* choseong chieuch + dieug = ab-chieuch */
			{0x110E, 0x1100, 0x1155}, /* choseong chieuch + gieug = dwis-chieuch */
			
			{-5000, 0x1161, 0x116a}, /* jungseong o + a                   = wa */
			{-5000, 0x1162, 0x116b}, /* jungseong o + ae                  = wae */
			{-5000, 0x1163, 0x11a6}, /* jungseong o + ya                  = o-ya */
			{-5000, 0x1164, 0x11a7}, /* jungseong o + yae                 = o-yae */
			{-5000, 0x1165, 0x117f}, /* jungseong o + eo                  = o-eo */
			{-5000, 0x1166, 0x1180}, /* jungseong o + e                   = o-e */
			{-5000, 0x1167, 0xd7b0}, /* jungseong o + yeo                 = o-yeo */
			{-5000, 0x1168, 0x1181}, /* jungseong o + ye                  = o-ye */
			{-5000, 0x1169, 0x1182}, /* jungseong o + o                   = o-o */
			{-5000, 0x116e, 0x1183}, /* jungseong o + u                   = o-u */
			{-5000, 0x1175, 0x116c}, /* jungseong o + i                   = oe */
			{-5001, 0x1161, 0x1189}, /* jungseong u + a                   = u-a */
			{-5001, 0x1162, 0x118a}, /* jungseong u + ae                  = u-ae */
			{-5001, 0x1165, 0x116f}, /* jungseong u + eo                  = weo */
			{-5001, 0x1166, 0x1170}, /* jungseong u + e                   = we */
			{-5001, 0x1167, 0xd7b5}, /* jungseong u + yeo                 = u-yeo */
			{-5001, 0x1168, 0x118c}, /* jungseong u + ye                  = u-ye */
			{-5001, 0x116e, 0x118d}, /* jungseong u + u                   = u-u */
			{-5001, 0x1175, 0x1171}, /* jungseong u + i                   = wi */
			{-5001, 0x117c, 0x118b}, /* jungseong u + eo-eu               = u-eo-eu */
			{-5002, 0x1161, 0xd7b9}, /* jungseong eu + a                  = eu-a */
			{-5002, 0x1165, 0xd7ba}, /* jungseong eu + eo                 = eu-eo */
			{-5002, 0x1166, 0xd7bb}, /* jungseong eu + e                  = eu-e */
			{-5002, 0x1169, 0xd7bc}, /* jungseong eu + o                  = eu-o */
			{-5002, 0x116e, 0x1195}, /* jungseong eu + u                  = eu-u */
			{-5002, 0x1173, 0x1196}, /* jungseong eu + eu                 = eu-eu */
			{-5002, 0x1175, 0x1174}, /* jungseong eu + i                  = yi */
			{-5010, 0x1161, 0xd7c5}, /* jungseong araea + a               = araea-a */
			{-5010, 0x1165, 0x119f}, /* jungseong araea + eo              = araea-eo */
			{-5010, 0x1166, 0xd7c6}, /* jungseong araea + e               = araea-e */
			{-5010, 0x116e, 0x11a0}, /* jungseong araea + u               = araea-u */
			{-5010, 0x1175, 0x11a1}, /* jungseong araea + i               = araea-i */
			{-5010, 0x119e, 0x11a2}, /* jungseong araea + araea           = ssangaraea */
						
			{0x11BA, 0x11C1, 0x11EB}, /* jongseong sieus + pieup = yeolinsieus */
			{0x110A, 0x11C1, 0xD7EE}, /* jongseong ssangsieus + pieup = sieus-yeolinsieus */
			{0x11C7, 0x11C1, 0x11C8}, /* jongseong nieun-sieus + pieup = nieun-yeolinsieus */
			{0x11B3, 0x11C1, 0x11D7}, /* jongseong lieul-sieus + pieup = lieul-yeolinsieus */
			{0x11DD, 0x11C1, 0x11DF}, /* jongseong mieum-sieus + pieup = mieum-yeolinsieus */
			{0x11BB, 0x11C1, 0xD7EE}, /* jongseong ssangsiues + pieup = sieus-yeolinsieus */
			{0x11F1, 0x11C1, 0x11F2}, /* jongseong yesieung-sieus + pieup = yesieung-yeolinsieus */
			{0x11BC, 0x11C1, 0x11F0}, /* jongseong ieung + pieup = yesieung */
			{0x11F0, 0x11C1, 0x11EE}, /* jongseong yesieung + pieup = ssangyesieung */
			{0xD7DD, 0x11C1, 0xD7DB}, /* jongseong yeolinlieul + pieup = lieul-yesieung */
			{0x11C2, 0x11C1, 0x11F9}, /* jongseong hieuh + pieup = yeolinhieuh */
			{0x11B6, 0x11C1, 0x11D9}, /* jongseong lieul-hieuh + pieup = lieul-yeolinhieuh */
		
			{0x11BC, 0x11A8, 0x11EC}, /* jongseong ieung + gieug = yesieung-gieug */
			{0x11BC, 0x11A9, 0x11EC}, /* jongseong ieung + ssanggieug = yesieung-ssanggieug */
			{0x11BC, 0x11B7, 0xD7F5}, /* jongseong ieung + mieum = yesieung-mieum */
			{0x11BC, 0x11BA, 0x11F1}, /* jongseong ieung + sieus = yesieung-sieus */
			{0x11BC, 0x11BC, 0x11EE}, /* jongseong ieung + ieung = ssangyesieung */
			{0x11BC, 0x11BF, 0x11EF}, /* jongseong ieung + ieung = ssangyesieung */
			{0x11BC, 0x11C2, 0xD7F6}  /* jongseong ieung + ieung = ssangyesieung */
			
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
			{39, 0x1110, 0xb7},
			
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

	public static final int[][] JAMO_SEBUL_393Y = {
			{96,0x11f9,0x11f0},
			{49,0x11c2,0x11bd},
			{50,0x11bb,0x11eb},
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
			{121, 0x1105, 0x302f},		// y
			{117, 0x1103, 0x302e},		// u
			{105, 0x1106, 0x1154},		// i
			{111, 0x110e, 0x1155},		// o
			{112, 0x1111, 0xb7},		// p
			{40, 0x28, 0x29},

			{97, 0x11bc, 0x11ae},		// a
			{115, 0x11ab, 0x11ad},		// s
			{100, 0x1175, 0x11b0},		// d
			{102, 0x1161, 0x11a9},		// f
			{103, 0x1173, 0x119e},		// g
			{104, 0x1102, 0x27},		// h
			{106, 0x110b, 0x114c},		// j
			{107, 0x1100, 0x114e},		// k
			{108, 0x110c, 0x1150},		// l
			{59, 0x1107, 0x3a},
			{39, 0x1110, 0x22},
			
			{122, 0x11b7, 0x11be},		// z
			{120, 0x11a8, 0x11b9},		// x
			{99, 0x1166, 0x11b1},		// c
			{118, 0x1169, 0x11b6},		// v
			{98, 0x116e, 0x21},			// b
			{110, 0x1109, 0x1140},		// n
			{109, 0x1112, 0x1159},		// m
			{44, 0x2c, 0x113c},
			{46, 0x2e, 0x113e},
			{47, 0x1169, 0x3f}
	};
	
	public static final int[][] JAMO_SEBUL_SUN_2014 = {
			{49,0x11c2,0x21},
			{50,0x11bb,0x40},
			{51,0x11b8,0x23},
			{52,0x116d,0x24},
			{53,0x1172,0x25},
			{54,0x1163,0x5e},
			{55,0x1168,0x26},
			{56,0x1174,0x2a},
			{57,0x116e,0x28},
			{48,0x110f,0x29},
			
			{113, 0x11ba, 0x11bd},		// q
			{119, 0x11af, 0x11be},		// w
			{101, 0x1167, 0x1167},		// e
			{114, 0x1162, 0x1164},		// r
			{116, 0x1165, 0x3b},		// t
			{121, 0x1105, 0x3c},		// y
			{117, 0x1103, 0x37},		// u
			{105, 0x1106, 0x38},		// i
			{111, 0x110e, 0x39},		// o
			{112, 0x1111, 0x3e},		// p
			{40, 0x28, 0x29},

			{97, 0x11bc, 0x11ae},		// a
			{115, 0x11ab, 0x11c0},		// s
			{100, 0x1175, 0x1175},		// d
			{102, 0x1161, 0x1161},		// f
			{103, 0x1173, 0x2f},		// g
			{104, 0x1102, 0x27},		// h
			{106, 0x110b, 0x34},		// j
			{107, 0x1100, 0x35},		// k
			{108, 0x110c, 0x36},		// l
			{59, 0x1107, 0x3a},
			{39, 0x1110, 0x22},
			
			{122, 0x11b7, 0x11c1},		// z
			{120, 0x11a8, 0x11bf},		// x
			{99, 0x1166, 0x1166},		// c
			{118, 0x1169, 0x1169},		// v
			{98, 0x116e, 0x116e},		// b
			{110, 0x1109, 0x30},		// n
			{109, 0x1112, 0x31},		// m
			{44, 0x2c, 0x32},
			{46, 0x2e, 0x33},
			{47, 0x1169, 0x3f},
			
			{128, 0x2e, 0x2c},
	};
	
	public static final int[][] COMB_SEBUL_SUN_2014 = {
		
			{0x1162, 0x1162, 0x1164}, 		// ㅐ + ㅐ = ㅒ
			
			{0x11a9, 0x11a8, 0x11bf}, 		// ㄲ + ㄱ = ㅋ
			{0x11ab, 0x11ab, 0x11c0}, 		// ㄴ + ㄴ = ㅌ
			{0x11ab, 0x11ba, 0x11ac}, 		// ㄴ + ㅅ = ㄵ
			{0x11af, 0x11ab, 0x11b4},  		// ㄹ + ㄴ = ㄾ
			{0x11af, 0x11af, 0x11be}, 		// ㄹ + ㄹ = ㅊ
			{0x11b1, 0x11b7, 0x11b5}, 		// ㄻ + ㅁ = ㄿ
			{0x11b7, 0x11b7, 0x11c1}, 		// ㅁ + ㅁ = ㅍ
			{0x11ba, 0x11ba, 0x11bd}, 		// ㅅ + ㅅ = ㅈ
			{0x11bc, 0x11bc, 0x11ae}, 		// ㅇ + ㅇ = ㄷ
			
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
	
	public static final int[][] JAMO_SEBUL_3_2015M_CHOJONG = {
			{49,0x11ae,0x21},
			{50,0x11bb,0x40},
			{51,0x11b8,0x23},
			{52,0x116d,0x24},
			{53,0x1172,0x25},
			{54,0x1163,0x5e},
			{55,0x1168,0x26},
			{56,0x1174,0x2a},
			{57,-5001,0x28},
			{48,0x110f,0x29},
			
			{113, 0x11ba, 0x11a9},		// q
			{119, 0x11af, 0x11b0},		// w
			{101, 0x11bd, 0x1167},		// e
			{114, 0x11be, 0x1162},		// r
			{116, 0x1164, 0x1165},		// t
			{121, 0x1105, 0x35},		// y
			{117, 0x1103, 0x36},		// u
			{105, 0x1106, 0x37},		// i
			{111, 0x110e, 0x38},		// o
			{112, 0x1111, 0x39},		// p

			{97, 0x11bc, 0x11b4},		// a
			{115, 0x11ab, 0x11ad},		// s
			{100, 0x11c2, 0x1175},		// d
			{102, 0x11c1, 0x1161},		// f
			{103, 0x1173, 0x3a},		// g
			{104, 0x1102, 0x30},		// h
			{106, 0x110b, 0x31},		// j
			{107, 0x1100, 0x32},		// k
			{108, 0x110c, 0x33},		// l
			{59, 0x1107, 0x34},
			{39, 0x1110, 0x2f},
			
			{122, 0x11b7, 0x11b3},		// z
			{120, 0x11a8, 0x11b9},		// x
			{99, 0x11c0, 0x1166},		// c
			{118, 0x11bf, 0x1169},		// v
			{98, 0x116e, 0x3b},		// b
			{110, 0x1109, 0x27},		// n
			{109, 0x1112, 0x22},		// m
			{44, 0x2c, 0x3c},
			{46, 0x2e, 0x3e},
			{47, -5000, 0x3f},
			
			{128, 0x2e, 0x2c},
	};
	
	public static final int[][] JAMO_SEBUL_3_2015M_CHOJUNG = {
			{49,0x11ae,0x21},
			{50,0x11bb,0x40},
			{51,0x11b8,0x23},
			{52,0x116d,0x24},
			{53,0x1172,0x25},
			{54,0x1163,0x5e},
			{55,0x1168,0x26},
			{56,0x1174,0x2a},
			{57,-5001,0x28},
			{48,0x110f,0x29},
			
			{113, 0x11ba, 0x11a9},		// q
			{119, 0x11af, 0x11b0},		// w
			{101, 0x1167, 0x11bd},		// e
			{114, 0x1162, 0x11be},		// r
			{116, 0x1165, 0x1164},		// t
			{121, 0x1105, 0x0035},		// y
			{117, 0x1103, 0x0036},		// u
			{105, 0x1106, 0x0037},		// i
			{111, 0x110e, 0x0038},		// o
			{112, 0x1111, 0x0039},		// p

			{97, 0x11bc, 0x11b4},		// a
			{115, 0x11ab, 0x11ad},		// s
			{100, 0x1175, 0x11c2},		// d
			{102, 0x1161, 0x11c1},		// f
			{103, 0x1173, 0x3a},		// g
			{104, 0x1102, 0x30},		// h
			{106, 0x110b, 0x31},		// j
			{107, 0x1100, 0x32},		// k
			{108, 0x110c, 0x33},		// l
			{59, 0x1107, 0x34},
			{39, 0x1110, 0x2f},
			
			{122, 0x11b7, 0x11b3},		// z
			{120, 0x11a8, 0x11b9},		// x
			{99, 0x1166, 0x11c0},		// c
			{118, 0x1169, 0x11bf},		// v
			{98, 0x116e, 0x3b},		// b
			{110, 0x1109, 0x27},		// n
			{109, 0x1112, 0x22},		// m
			{44, 0x2c, 0x3c},
			{46, 0x2e, 0x3e},
			{47, -5000, 0x3f},
			
			{128, 0x2e, 0x2c},
	};
	
	public static final int[][][] JAMOSET_SEBUL_3_2015M = {
			JAMO_SEBUL_3_2015M_CHOJONG,
			JAMO_SEBUL_3_2015M_CHOJUNG,
			JAMO_SEBUL_3_2015M_CHOJONG,
			JAMO_SEBUL_3_2015M_CHOJONG
	};
	
	
	public static final int[][] JAMO_SEBUL_3_2015_CHOJONG = {
			{49,0x11ae,0x21},
			{50,0x11bb,0x40},
			{51,0x11b8,0x23},
			{52,0x116d,0x24},
			{53,0x1172,0x25},
			{54,0x1163,0x5e},
			{55,0x1168,0x26},
			{56,0x1174,0x2a},
			{57,-5001,0x28},
			{48,0x110f,0x29},
			
			{113, 0x11ba, 0x11a9},		// q
			{119, 0x11af, 0x11b0},		// w
			{101, 0x11bd, 0x1167},		// e
			{114, 0x11be, 0x1165},		// r
			{116, 0x1164, 0x1162},		// t
			{121, 0x1105, 0x35},		// y
			{117, 0x1103, 0x36},		// u
			{105, 0x1106, 0x37},		// i
			{111, 0x110e, 0x38},		// o
			{112, 0x1111, 0x39},		// p

			{97, 0x11bc, 0x11b4},		// a
			{115, 0x11ab, 0x11ad},		// s
			{100, 0x11c2, 0x1175},		// d
			{102, 0x11c1, 0x1161},		// f
			{103, 0x1173, 0x3a},		// g
			{104, 0x1102, 0x30},		// h
			{106, 0x110b, 0x31},		// j
			{107, 0x1100, 0x32},		// k
			{108, 0x110c, 0x33},		// l
			{59, 0x1107, 0x34},
			{39, 0x1110, 0x2f},
			
			{122, 0x11b7, 0x11b3},		// z
			{120, 0x11a8, 0x11b9},		// x
			{99, 0x11c0, 0x1166},		// c
			{118, 0x11bf, 0x1169},		// v
			{98, 0x116e, 0x3b},			// b
			{110, 0x1109, 0x27},		// n
			{109, 0x1112, 0x22},		// m
			{44, 0x2c, 0x3c},
			{46, 0x2e, 0x3e},
			{47, -5000, 0x3f},
			
			{128, 0x2e, 0x2c},
	};
	
	public static final int[][] JAMO_SEBUL_3_2015_CHOJUNG = {
			{49,0x11ae,0x21},
			{50,0x11bb,0x40},
			{51,0x11b8,0x23},
			{52,0x116d,0x24},
			{53,0x1172,0x25},
			{54,0x1163,0x5e},
			{55,0x1168,0x26},
			{56,0x1174,0x2a},
			{57,-5001,0x28},
			{48,0x110f,0x29},
			
			{113, 0x11ba, 0x11a9},		// q
			{119, 0x11af, 0x11b0},		// w
			{101, 0x1167, 0x11bd},		// e
			{114, 0x1165, 0x11be},		// r
			{116, 0x1162, 0x1164},		// t
			{121, 0x1105, 0x0035},		// y
			{117, 0x1103, 0x0036},		// u
			{105, 0x1106, 0x0037},		// i
			{111, 0x110e, 0x0038},		// o
			{112, 0x1111, 0x0039},		// p

			{97, 0x11bc, 0x11b4},		// a
			{115, 0x11ab, 0x11ad},		// s
			{100, 0x1175, 0x11c2},		// d
			{102, 0x1161, 0x11c1},		// f
			{103, 0x1173, 0x3a},		// g
			{104, 0x1102, 0x30},		// h
			{106, 0x110b, 0x31},		// j
			{107, 0x1100, 0x32},		// k
			{108, 0x110c, 0x33},		// l
			{59, 0x1107, 0x34},
			{39, 0x1110, 0x2f},
			
			{122, 0x11b7, 0x11b3},		// z
			{120, 0x11a8, 0x11b9},		// x
			{99, 0x1166, 0x11c0},		// c
			{118, 0x1169, 0x11bf},		// v
			{98, 0x116e, 0x3b},			// b
			{110, 0x1109, 0x27},		// n
			{109, 0x1112, 0x22},		// m
			{44, 0x2c, 0x3c},
			{46, 0x2e, 0x3e},
			{47, -5000, 0x3f},
			
			{128, 0x2e, 0x2c},
	};
	
	public static final int[][][] JAMOSET_SEBUL_3_2015 = {
			JAMO_SEBUL_3_2015_CHOJONG,
			JAMO_SEBUL_3_2015_CHOJUNG,
			JAMO_SEBUL_3_2015_CHOJONG,
			JAMO_SEBUL_3_2015_CHOJONG
	};
	
	
	public static final int[][] JAMO_SEBUL_3_2015Y = {
			{49,0x11c2,0x11f9},
			{50,0x11bb,0x40},
			{51,0x11b8,0x23},
			{52,0x116d,0x24},
			{53,0x1172,0x25},
			{54,0x1163,0x5e},
			{55,0x1168,0x26},
			{56,0x1174,0x2a},
			{57,0x116e,0x28},
			{48,0x110f,0x29},
			
			{113, 0x11ba, 0x11eb},		// q
			{119, 0x11af, 0x11af},		// w
			{101, 0x1167, 0x11bd},		// e
			{114, 0x1165, 0x11be},		// r
			{116, 0x1162, 0x1164},		// t
			{121, 0x1105, 0x302f},		// y
			{117, 0x1103, 0x302e},		// u
			{105, 0x1106, 0x1154},		// i
			{111, 0x110e, 0x1155},		// o
			{112, 0x1111, 0x3b},		// p
			{129, 0x27, 0x22},
			{91, 0x5b, 0x27},
			{93, 0x5d, 0x22},

			{97, 0x11bc, 0x11f0},		// a
			{115, 0x11ab, 0x11ab},		// s
			{100, 0x1175, 0x11ae},		// d
			{102, 0x1161, 0x11c1},		// f
			{103, 0x1173, 0x119e},		// g
			{104, 0x1102, 0xb7},		// h
			{106, 0x110b, 0x114c},		// j
			{107, 0x1100, 0x114e},		// k
			{108, 0x110c, 0x1150},		// l
			{59, 0x1107, 0x3a},
			{39, 0x1110, 0x2f},
			
			{122, 0x11b7, 0x11b7},		// z
			{120, 0x11a8, 0x11a8},		// x
			{99, 0x1166, 0x11c0},		// c
			{118, 0x1169, 0x11bf},		// v
			{98, 0x116e, 0x0021},		// b
			{110, 0x1109, 0x1140},		// n
			{109, 0x1112, 0x1159},		// m
			{44, 0x2c, 0x113c},
			{46, 0x2e, 0x113e},
			{47, 0x1169, 0x3f},
			
			{128, 0x2e, 0x2c},
	};
	
	public static final int[][] COMB_SEBUL_3_2015 = {

			{0x1100, 0x110b, 0x1101}, // ㄱ + ㅇ = ㄲ
			{0x1100, 0x110c, 0x110d}, // ㄱ + ㅈ = ㅉ
			{0x1103, 0x1106, 0x1104}, // ㄷ + ㅁ = ㄸ
			{0x1106, 0x1103, 0x1104}, // ㅁ + ㄷ = ㄸ
			{0x1107, 0x110c, 0x1108}, // ㅂ + ㅈ = ㅃ
			{0x1109, 0x1112, 0x110a}, // ㅅ + ㅎ = ㅆ
			{0x110b, 0x1100, 0x1101}, // ㅇ + ㄱ = ㄲ
			{0x110c, 0x1100, 0x110d}, // ㅈ + ㄱ = ㅉ
			{0x110c, 0x1107, 0x1108}, // ㅈ + ㅂ = ㅃ
			{0x1112, 0x1109, 0x110a}, // ㅎ + ㅅ = ㅆ

			{0x1162, 0x1164, 0x1164}, // ㅐ + ㅒ = ㅒ
			{0x1165, 0x1164, 0x1164}, // ㅓ + ㅒ = ㅒ
			
			{0x11a8, 0x11af, 0x11b0}, // ㄱ + ㄹ = ㄺ
			{0x11a8, 0x11b7, 0x11a9}, // ㄱ + ㅁ = ㄲ
			{0x11b7, 0x11a8, 0x11a9}, // ㅁ + ㄱ = ㄲ
			{0x11b7, 0x11af, 0x11b1}, // ㅁ + ㄹ = ㄻ
			{0x11b8, 0x11af, 0x11b2}, // ㅂ + ㄹ = ㄼ
			{0x11ba, 0x11a8, 0x11aa}, // ㅅ + ㄱ = ㄳ
			{0x11ba, 0x11af, 0x11b3}, // ㅅ + ㄹ = ㄽ
			{0x11ba, 0x11b8, 0x11b9}, // ㅅ + ㅂ = ㅄ
			{0x11bd, 0x11ab, 0x11ac}, // ㅈ + ㄴ = ㄵ
			{0x11c0, 0x11af, 0x11b4}, // ㅌ + ㄹ = ㄾ
			{0x11c1, 0x11af, 0x11b5}, // ㅍ + ㄹ = ㄿ
			{0x11c2, 0x11ab, 0x11ad}, // ㅎ + ㄴ = ㄶ
			{0x11c2, 0x11af, 0x11b6}, // ㅎ + ㄹ = ㅀ
			
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

			// 가상 낱자 조합
			{-5000, 0x1161, 0x116a},	// ㅘ
			{-5000, 0x1162, 0x116b},	// ㅙ
			{-5000, 0x1175, 0x116c},	// ㅚ
			{-5001, 0x1165, 0x116f},	// ㅝ
			{-5001, 0x1166, 0x1170},	// ㅞ
			{-5001, 0x1175, 0x1171},	// ㅟ

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
	
	public static final int[][] JAMO_SEBUL_3_P3_CHOJONG = {
			{49,0x11bf,0x21},
			{50,0x11bb,0x40},
			{51,0x11b8,0x23},
			{52,0x116d,0x24},
			{53,0x1172,0x25},
			{54,0x1163,0x5e},
			{55,0x1168,0x26},
			{56,-5002,0x2a},
			{57,-5001,0x28},
			{48,0x110f,0x29},
			
			{113, 0x11ba, 0x11b6},		// q
			{119, 0x11af, 0x11b0},		// w
			{101, 0x11c0, 0x1167},		// e
			{114, 0x11be, 0x1165},		// r
			{116, 0x1164, 0x1162},		// t
			{121, 0x1105, 0x2f},		// y
			{117, 0x1103, 0x37},		// u
			{105, 0x1106, 0x38},		// i
			{111, 0x110e, 0x39},		// o
			{112, 0x1111, 0x3b},		// p

			{97, 0x11bc, 0x11b9},		// a
			{115, 0x11ab, 0x11ad},		// s
			{100, 0x11c2, 0x1175},		// d
			{102, 0x11c1, 0x1161},		// f
			{103, 0x1173, 0x3c},		// g
			{104, 0x1102, 0x27},		// h
			{106, 0x110b, 0x34},		// j
			{107, 0x1100, 0x35},		// k
			{108, 0x110c, 0x36},		// l
			{59, 0x1107, 0x3a},
			{39, 0x1110, 0x22},
			
			{122, 0x11b7, 0x11b1},		// z
			{120, 0x11a8, 0x11a9},		// x
			{99, 0x11ae, 0x1166},		// c
			{118, 0x11bd, 0x1169},		// v
			{98, 0x116e, 0x3e},			// b
			{110, 0x1109, 0x30},		// n
			{109, 0x1112, 0x31},		// m
			{44, 0x2c, 0x32},
			{46, 0x2e, 0x33},
			{47, -5000, 0x3f},
			
			{128, 0x2e, 0x2c},
	};
	
	public static final int[][] JAMO_SEBUL_3_P3_CHOJUNG = {
			{49,0x11bf,0x21},
			{50,0x11bb,0x40},
			{51,0x11b8,0x23},
			{52,0x116d,0x24},
			{53,0x1172,0x25},
			{54,0x1163,0x5e},
			{55,0x1168,0x26},
			{56,-5002,0x2a},
			{57,-5001,0x28},
			{48,0x110f,0x29},
			
			{113, 0x11ba, 0x11b6},		// q
			{119, 0x11af, 0x11b0},		// w
			{101, 0x1167, 0x11c0},		// e
			{114, 0x1165, 0x11be},		// r
			{116, 0x1162, 0x1164},		// t
			{121, 0x1105, 0x2f},		// y
			{117, 0x1103, 0x37},		// u
			{105, 0x1106, 0x38},		// i
			{111, 0x110e, 0x39},		// o
			{112, 0x1111, 0x3b},		// p

			{97, 0x11bc, 0x11b9},		// a
			{115, 0x11ab, 0x11ad},		// s
			{100, 0x1175, 0x11c2},		// d
			{102, 0x1161, 0x11c1},		// f
			{103, 0x1173, 0x3c},		// g
			{104, 0x1102, 0x27},		// h
			{106, 0x110b, 0x34},		// j
			{107, 0x1100, 0x35},		// k
			{108, 0x110c, 0x36},		// l
			{59, 0x1107, 0x3a},
			{39, 0x1110, 0x22},
			
			{122, 0x11b7, 0x11b1},		// z
			{120, 0x11a8, 0x11a9},		// x
			{99, 0x1166, 0x11ae},		// c
			{118, 0x1169, 0x11bd},		// v
			{98, 0x116e, 0x3e},			// b
			{110, 0x1109, 0x30},		// n
			{109, 0x1112, 0x31},		// m
			{44, 0x2c, 0x32},
			{46, 0x2e, 0x33},
			{47, -5000, 0x3f},
			
			{128, 0x2e, 0x2c},
	};
	
	public static final int[][][] JAMOSET_SEBUL_3_P3 = {
			JAMO_SEBUL_3_P3_CHOJONG,
			JAMO_SEBUL_3_P3_CHOJUNG,
			JAMO_SEBUL_3_P3_CHOJONG,
			JAMO_SEBUL_3_P3_CHOJONG
	};
	
	public static final int[][] COMB_SEBUL_3_P3 = {

			{0x1162, 0x1164, 0x1164}, // ㅐ + ㅒ = ㅒ
			{0x1175, 0x1164, 0x1164}, // ㅣ + ㅒ = ㅒ
			
			{0x11a8, 0x11af, 0x11b0}, // ㄱ + ㄹ = ㄺ
			{0x11b7, 0x11af, 0x11b1}, // ㅁ + ㄹ = ㄻ
			{0x11b8, 0x11af, 0x11b2}, // ㅂ + ㄹ = ㄼ
			{0x11ba, 0x11a8, 0x11aa}, // ㅅ + ㄱ = ㄳ
			{0x11ba, 0x11af, 0x11b3}, // ㅅ + ㄹ = ㄽ
			{0x11ba, 0x11b8, 0x11b9}, // ㅅ + ㅂ = ㅄ
			{0x11bd, 0x11ab, 0x11ac}, // ㅈ + ㄴ = ㄵ
			{0x11c0, 0x11af, 0x11b4}, // ㅌ + ㄹ = ㄾ
			{0x11c1, 0x11af, 0x11b5}, // ㅍ + ㄹ = ㄿ
			{0x11c2, 0x11ab, 0x11ad}, // ㅎ + ㄴ = ㄶ
			{0x11c2, 0x11af, 0x11b6}, // ㅎ + ㄹ = ㅀ
			
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

			// 가상 낱자 조합
			{-5000, 0x1161, 0x116a},	// ㅘ
			{-5000, 0x1162, 0x116b},	// ㅙ
			{-5000, 0x1175, 0x116c},	// ㅚ
			{-5001, 0x1165, 0x116f},	// ㅝ
			{-5001, 0x1166, 0x1170},	// ㅞ
			{-5001, 0x1175, 0x1171},	// ㅟ

			{0x119e, 0x1175, 0x11a1},	// ㆎ
			{0x119e, 0x119e, 0x11a2},	// ᆢ

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
	
	public static final int[][] JAMO_SEBUL_SHIN_ORIGINAL_CHOJONG = {

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
			{106, 0x110b, 0x3b},		// j
			{107, 0x1100, 0x27},		// k
			{108, 0x110c, 0x110c},		// l
			{59, 0x1107, 0x3a},
			{39, 0x1110, 0x22},
			
			{122, 0x11b7, 0x1172},		// z
			{120, 0x11a8, 0x116d},		// x
			{99, 0x11be, 0x1166},		// c
			{118, 0x11bf, 0x1169},		// v
			{98, 0x11bb, 0x116e},			// b
			{110, 0x1109, 0x1109},		// n
			{109, 0x1112, 0x2f},		// m
			{47, 0x110f, 0x3f},

			{128, 0x2e, 0x2c},
	};

	public static final int[][] JAMO_SEBUL_SHIN_ORIGINAL_CHOJUNG = {

			{113, 0x1174, 0x11ba},		// q
			{119, 0x1163, 0x11af},		// w
			{101, 0x1167, 0x11b8},		// e
			{114, 0x1162, 0x11ae},		// r
			{116, 0x1165, 0x11c0},		// t
			{121, 0x1105, 0x1105},		// y
			{117, 0x1103, 0x1103},		// u
			{105, -5001, 0x1106},		// i
			{111, -5001, 0x110e},		// o
			{112, -5000, 0x1111},		// p

			{97, 0x1164, 0x11bc},		// a
			{115, 0x1168, 0x11ab},		// s
			{100, 0x1175, 0x11c2},		// d
			{102, 0x1161, 0x11bd},		// f
			{103, 0x1173, 0x11c1},		// g
			{104, 0x1102, 0x1102},		// h
			{106, 0x110b, 0x3b},		// j
			{107, 0x1100, 0x27},		// k
			{108, 0x110c, 0x110c},		// l
			{59, 0x1107, 0x3a},
			{39, 0x1110, 0x22},

			{122, 0x1172, 0x11b7},		// z
			{120, 0x116d, 0x11a8},		// x
			{99, 0x1166, 0x11be},		// c
			{118, 0x1169, 0x11bf},		// v
			{98, 0x116e, 0x11bb},			// b
			{110, 0x1109, 0x1109},		// n
			{109, 0x1112, 0x2f},		// m
			{47, -5000, 0x3f},

			{128, 0x2e, 0x2c},
	};

	public static final int[][][] JAMOSET_SHIN_ORIGINAL = {
			JAMO_SEBUL_SHIN_ORIGINAL_CHOJONG,
			JAMO_SEBUL_SHIN_ORIGINAL_CHOJUNG,
			JAMO_SEBUL_SHIN_ORIGINAL_CHOJONG,
			JAMO_SEBUL_SHIN_ORIGINAL_CHOJONG
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
			{0x119e, 0x1175, 0x11a1},	// ㆎ
			{0x119e, 0x119e, 0x11a2},	// ᆢ

			// 가상 낱자 조합
			{-5000, 0x1161, 0x116a},	// ㅘ
			{-5000, 0x1162, 0x116b},	// ㅙ
			{-5000, 0x1175, 0x116c},	// ㅚ
			{-5001, 0x1165, 0x116f},	// ㅝ
			{-5001, 0x1166, 0x1170},	// ㅞ
			{-5001, 0x1175, 0x1171},	// ㅟ
			{-5002, 0x1175, 0x1174},	// ㅢ
			{-5010, 0x1175, 0x11a1},	// ㆎ
			{-5010, 0x119e, 0x11a2},	// ᆢㆎ
			{-5010, -5010, 0x11a2},	// ᆢ

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

	public static final int[][] VIRTUAL_SEBUL_SHIN_ORIGINAL = {
			{HangulEngine.VIRTUAL_JUNG, -5000, 0x1169},
			{HangulEngine.VIRTUAL_JUNG, -5001, 0x116e},
			{HangulEngine.VIRTUAL_JUNG, -5002, 0x1173},
			{HangulEngine.VIRTUAL_JUNG, -5010, 0x119e},
	};

	public static final int[][] JAMO_SEBUL_SHIN_EDIT_CHOJONG = {
			
			{113, 0x11ba, 0x1164},		// q
			{119, 0x11af, 0x1163},		// w
			{101, 0x11b8, 0x1167},		// e
			{114, 0x11c0, 0x1162},		// r
			{116, 0x11c1, 0x1165},		// t
			{121, 0x1105, 0x201c},		// y
			{117, 0x1103, 0x201d},		// u
			{105, 0x1106, 0x1174},		// i
			{111, 0x110e, -5001},		// o
			{112, 0x1111, -5000},		// p
			
			{97, 0x11bc, 0x1172},		// a
			{115, 0x11ab, 0x1168},		// s
			{100, 0x11ae, 0x1175},		// d
			{102, 0x11bb, 0x1161},		// f
			{103, 0x11bd, 0x1173},		// g
			{104, 0x1102, 0x2018},		// h
			{106, 0x110b, 0x2019},		// j
			{107, 0x1100, 0x3b},		// k
			{108, 0x110c, 0x27},		// l
			{59, 0x1107, 0x3a},
			{39, 0x1110, 0x22},
			
			{122, 0x11b7, 0x203b},		// z
			{120, 0x11a8, 0x116d},		// x
			{99, 0x11be, 0x1166},		// c
			{118, 0x11c2, 0x1169},		// v
			{98, 0x11bf, 0x116e},			// b
			{110, 0x1109, 0x00b7},		// n
			{109, 0x1112, 0x2f},		// m
			{47, 0x110f, 0x3f},

			{128, 0x2e, 0x2c},
	};

	public static final int[][] JAMO_SEBUL_SHIN_EDIT_CHOJUNG = {

			{113, 0x1164, 0x11ba},		// q
			{119, 0x1163, 0x11af},		// w
			{101, 0x1167, 0x11b8},		// e
			{114, 0x1162, 0x11c0},		// r
			{116, 0x1165, 0x11c1},		// t
			{121, 0x1105, 0x201c},		// y
			{117, 0x1103, 0x201d},		// u
			{105, 0x1174, 0x1106},		// i
			{111, -5001, 0x110e},		// o
			{112, -5000, 0x1111},		// p

			{97, 0x1172, 0x11bc},		// a
			{115, 0x1168, 0x11ab},		// s
			{100, 0x1175, 0x11ae},		// d
			{102, 0x1161, 0x11bb},		// f
			{103, 0x1173, 0x11bd},		// g
			{104, 0x1102, 0x2018},		// h
			{106, 0x110b, 0x2019},		// j
			{107, 0x1100, 0x3b},		// k
			{108, 0x110c, 0x27},		// l
			{59, 0x1107, 0x3a},
			{39, 0x1110, 0x22},

			{122, 0x203b, 0x11b7},		// z
			{120, 0x116d, 0x11a8},		// x
			{99, 0x1166, 0x11be},		// c
			{118, 0x1169, 0x11c2},		// v
			{98, 0x116e, 0x11bf},			// b
			{110, 0x1109, 0x00b7},		// n
			{109, 0x1112, 0x2f},		// m
			{47, -5000, 0x3f},

			{128, 0x2e, 0x2c},
	};

	public static final int[][][] JAMOSET_SHIN_EDIT = {
			JAMO_SEBUL_SHIN_EDIT_CHOJONG,
			JAMO_SEBUL_SHIN_EDIT_CHOJUNG,
			JAMO_SEBUL_SHIN_EDIT_CHOJONG,
			JAMO_SEBUL_SHIN_EDIT_CHOJONG
	};

	public static final int[][] JAMO_SEBUL_SHIN_M_CHOJONG = {
			
			{113, 0x11ba, 0x1164},		// q
			{119, 0x11af, 0x1163},		// w
			{101, 0x11b8, 0x1167},		// e
			{114, 0x11bd, 0x1162},		// r
			{116, 0x11bf, 0x1165},		// t
			{121, 0x1105, 0x201c},		// y
			{117, 0x1103, 0x201d},		// u
			{105, 0x1106, 0x203b},		// i
			{111, 0x110e,  -5001},		// o
			{112, 0x1111,  -5000},		// p
			
			{97,  0x11bc, 0x1172},		// a
			{115, 0x11ab, 0x1174},		// s
			{100, 0x11bb, 0x1175},		// d
			{102, 0x11c0, 0x1161},		// f
			{103, 0x11ae, 0x1173},		// g
			{104, 0x1102, 0x300a},		// h
			{106, 0x110b, 0x300b},		// j
			{107, 0x1100, 0xb7},		// k
			{108, 0x110c, 0x3b},		// l
			{59,  0x1107, 0x3a},		// ;
			{39,  0x1110, 0x2f},		// '
			
			{122, 0x11b7, 0x1168},		// z
			{120, 0x11a8, 0x116d},		// x
			{99,  0x11c2, 0x1166},		// c
			{118, 0x11c1, 0x1169},		// v
			{98,  0x11be, 0x116e},		// b
			{110, 0x1109, 0x27},		// n
			{109, 0x1112, 0x22},		// m
			{44,    0x2c,   0x3c},		// ,
			{46,    0x2e,   0x3e},		// .
			{47,  0x110f,  0x3f},		// /
			
			{128, 0x2e, 0x2c},
	};
	
	public static final int[][] JAMO_SEBUL_SHIN_M_CHOJUNG = {
			
			{113, 0x1164, 0x11ba},		// q
			{119, 0x1163, 0x11af},		// w
			{101, 0x1167, 0x11b8},		// e
			{114, 0x1162, 0x11bd},		// r
			{116, 0x1165, 0x11bf},		// t
			{121, 0x1105, 0x201c},		// y
			{117, 0x1103, 0x201d},		// u
			{105, 0x1106, 0x203b},		// i
			{111, -5001,  0x110e},		// o
			{112, -5000,  0x1111},		// p
			
			{97,  0x1172, 0x11bc},		// a
			{115, 0x1174, 0x11ab},		// s
			{100, 0x1175, 0x11bb},		// d
			{102, 0x1161, 0x11c0},		// f
			{103, 0x1173, 0x11ae},		// g
			{104, 0x1102, 0x300a},		// h
			{106, 0x110b, 0x300b},		// j
			{107, 0x1100, 0xb7},		// k
			{108, 0x110c, 0x3b},		// l
			{59,  0x1107, 0x3a},		// ;
			{39,  0x1110, 0x2f},		// '
			
			{122, 0x1168, 0x11b7},		// z
			{120, 0x116d, 0x11a8},		// x
			{99,  0x1166, 0x11c2},		// c
			{118, 0x1169, 0x11c1},		// v
			{98,  0x116e, 0x11be},		// b
			{110, 0x1109, 0x27},		// n
			{109, 0x1112, 0x22},		// m
			{44,    0x2c,   0x3c},		// ,
			{46,    0x2e,   0x3e},		// .
			{47,   -5000, 0x110f},		// /
			
			{128, 0x2e, 0x2c},
	};
	
	public static final int[][][] JAMOSET_SHIN_M = {
			JAMO_SEBUL_SHIN_M_CHOJONG,
			JAMO_SEBUL_SHIN_M_CHOJUNG,
			JAMO_SEBUL_SHIN_M_CHOJONG,
			JAMO_SEBUL_SHIN_M_CHOJONG
	};

	public static final int[][] JAMO_SEBUL_SHIN_P2_CHOJONG = {
			
			{113, 0x11ba, 0x1164},		// q
			{119, 0x11af, 0x1163},		// w
			{101, 0x11b8, 0x1162},		// e
			{114, 0x11c0, 0x1165},		// r
			{116, 0x11bf, 0x1167},		// t
			{121, 0x1105, 0x302f},		// y
			{117, 0x1103, 0x302e},		// u
			{105, 0x1106, -5002},		// i
			{111, 0x110e, -5001},		// o
			{112, 0x1111, -5010},		// p
			
			{97, 0x11bc, 0x1172},		// a
			{115, 0x11ab, 0x1168},		// s
			{100, 0x11c2, 0x1175},		// d
			{102, 0x11c1, 0x1161},		// f
			{103, 0x11ae, 0x1173},		// g
			{104, 0x1102, 0x25a1},		// h
			{106, 0x110b, 0x27},		// j
			{107, 0x1100, 0xb7},		// k
			{108, 0x110c, 0x3b},		// l
			{59, 0x1107, 0x3a},
			{39, 0x1110, 0x2f},
			
			{122, 0x11b7, 0x119e},		// z
			{120, 0x11bb, 0x116d},		// x
			{99, 0x11a8, 0x1166},		// c
			{118, 0x11bd, 0x1169},		// v
			{98, 0x11be, 0x116e},		// b
			{110, 0x1109, 0x2015},		// n
			{109, 0x1112, 0x22},		// m
			{44, 0x2c, 0x3c},
			{46, 0x2e, 0x3e},
			{47, 0x110f, 0x3f},
			
			{128, 0x2e, 0x2c},
	};
	
	public static final int[][] JAMO_SEBUL_SHIN_P2_CHOJUNG = {
			
			{113, 0x1164, 0x11ba},		// q
			{119, 0x1163, 0x11af},		// w
			{101, 0x1162, 0x11b8},		// e
			{114, 0x1165, 0x11c0},		// r
			{116, 0x1167, 0x11bf},		// t
			{121, 0x1105, 0x302f},		// y
			{117, 0x1103, 0x302e},		// u
			{105, -5002, 0x1106},		// i
			{111, -5001, 0x110e},		// o
			{112, -5010, 0x1111},		// p
			
			{97, 0x1172, 0x11bc},		// a
			{115, 0x1168, 0x11ab},		// s
			{100, 0x1175, 0x11c2},		// d
			{102, 0x1161, 0x11c1},		// f
			{103, 0x1173, 0x11ae},		// g
			{104, 0x1102, 0x25a1},		// h
			{106, 0x110b, 0x27},		// j
			{107, 0x1100, 0xb7},		// k
			{108, 0x110c, 0x3b},		// l
			{59, 0x1107, 0x3a},
			{39, 0x1110, 0x2f},
			
			{122, 0x119e, 0x11b7},		// z
			{120, 0x116d, 0x11bb},		// x
			{99, 0x1166, 0x11a8},		// c
			{118, 0x1169, 0x11bd},		// v
			{98, 0x116e, 0x11be},		// b
			{110, 0x1109, 0x2015},		// n
			{109, 0x1112, 0x22},		// m
			{44, 0x2c, 0x3c},
			{46, 0x2e, 0x3e},
			{47, -5000, 0x110f},
			
			{128, 0x2e, 0x2c},
	};

	public static final int[][][] JAMOSET_SHIN_P2 = {
			JAMO_SEBUL_SHIN_P2_CHOJONG,
			JAMO_SEBUL_SHIN_P2_CHOJUNG,
			JAMO_SEBUL_SHIN_P2_CHOJONG,
			JAMO_SEBUL_SHIN_P2_CHOJONG
	};
	
	public static final int[][] JAMO_SEBUL_AHNMATAE = {
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
			

			{113, 0x1106, 0x1106},		// q
			{119, 0x1109, 0x1140},		// w
			{101, 0x1102, 0x1102},		// e
			{114, 0x1105, 0x1105},		// r
			{116, 0x1112, 0x1159},		// t
			{121, 0x1167, 0x3b},		// y
			{117, 0x1163, 0x27},		// u
			{105, 0x1173, 0x2f},		// i
			{111, 0x116d, 0x5b},		// o
			{112, 0x1172, 0x5d},		// p
			{91, 0x2c, 0x7b},
			{93, 0x3f, 0x7d},

			{97, 0x1107, 0x1111},		// a
			{115, 0x110c, 0x110e},		// s
			{100, 0x1103, 0x1110},		// d
			{102, 0x1100, 0x110f},		// f
			{103, 0x110b, 0x114c},		// g
			{104, 0x1165, 0x1165},		// h
			{106, 0x1161, 0x119e},		// j
			{107, 0x1175, 0x1175},		// k
			{108, 0x1169, 0x1169},		// l
			{59, 0x116e, 0x3a},
			{39, 0x2e, 0x22},
			
			{122, 0x11bd, 0x11be},		// z
			{120, 0x11ae, 0x11c0},		// x
			{99, 0x11b8, 0x11c1},		// c
			{118, 0x11a8, 0x11bf},		// v
			{98, 0x11bc, 0x11f0},		// b
			{110, 0x11ba, 0x11eb},		// n
			{109, 0x11ab, 0x11ab},		// m
			{44, 0x11b7, 0x3c},
			{46, 0x11af, 0x3e},
			{47, 0x11c2, 0x11f9},
			
			{128, 0x2e, 0x2c},
	};
	
	public static final int[][] COMB_SEBUL_AHNMATAE = {

			{0x1100, 0x1103, 0x1104}, // ㄱ + ㄷ = ㄸ
			{0x1100, 0x110b, 0x1101}, // ㄱ + ㅇ = ㄲ
			{0x1100, 0x1112, 0x110f}, // ㄱ + ㅎ = ㅋ
			{0x1102, 0x1109, 0x110a}, // ㄴ + ㅅ = ㅆ
			{0x1103, 0x1100, 0x1104}, // ㄷ + ㄱ = ㄸ
			{0x1103, 0x110c, 0x110d}, // ㄷ + ㅈ = ㅉ
			{0x1103, 0x1112, 0x1110}, // ㄷ + ㅎ = ㅌ
			{0x1107, 0x110c, 0x1108}, // ㅂ + ㅈ = ㅃ
			{0x1107, 0x1112, 0x1111}, // ㅂ + ㅎ = ㅍ
			{0x1109, 0x1102, 0x110a}, // ㅅ + ㄴ = ㅆ
			{0x110b, 0x1100, 0x1101}, // ㅇ + ㄱ = ㄲ
  			{0x110c, 0x1103, 0x110d}, // ㅈ + ㄷ = ㅉ
  			{0x110c, 0x1107, 0x1108}, // ㅈ + ㅂ = ㅃ
  			{0x110c, 0x1112, 0x110e}, // ㅈ + ㅎ = ㅊ
			{0x1112, 0x1100, 0x110f}, // ㅎ + ㄱ = ㅋ
  			{0x1112, 0x1103, 0x1110}, // ㅎ + ㄷ = ㅌ
  			{0x1112, 0x1107, 0x1111}, // ㅎ + ㅂ = ㅍ
  			{0x1112, 0x110c, 0x110e}, // ㅎ + ㅈ + ㅊ
  			
			{0x1161, 0x1169, 0x116a}, // ㅏ + ㅗ = ㅘ
  			{0x1161, 0x1175, 0x1162}, // ㅏ + ㅣ = ㅐ
  			{0x1162, 0x1169, 0x116b}, // ㅐ + ㅗ = ㅙ
  			{0x1163, 0x1175, 0x1164}, // ㅑ + ㅣ = ㅒ
  			{0x1165, 0x116e, 0x116f}, // ㅓ + ㅜ = ㅝ
  			{0x1165, 0x1175, 0x1166}, // ㅓ + ㅣ = ㅔ
  			{0x1166, 0x116e, 0x1170}, // ㅔ + ㅜ = ㅞ
  			{0x1167, 0x1175, 0x1168}, // ㅕ + ㅣ = ㅖ
  			{0x1169, 0x1161, 0x116a}, // ㅗ + ㅏ = ㅘ
  			{0x1169, 0x1175, 0x116c}, // ㅗ + ㅣ = ㅚ
  			{0x116a, 0x1175, 0x116b}, // ㅘ + ㅣ = ㅙ
  			{0x116c, 0x1161, 0x116b}, // ㅚ + ㅏ = ㅙ
  			{0x116e, 0x1165, 0x116f}, // ㅜ + ㅓ = ㅝ
  			{0x116e, 0x1175, 0x1171}, // ㅜ + ㅣ = ㅟ
  			{0x116f, 0x1175, 0x1170}, // ㅝ + ㅣ = ㅞ
  			{0x1171, 0x1165, 0x1170}, // ㅟ + ㅓ = ㅞ
  			{0x1173, 0x1175, 0x1174}, // ㅡ + ㅣ = ㅢ
  			{0x1175, 0x1161, 0x1162}, // ㅣ + ㅏ = ㅐ
  			{0x1175, 0x1163, 0x1164}, // ㅣ + ㅑ = ㅒ
  			{0x1175, 0x1165, 0x1166}, // ㅣ + ㅓ = ㅔ
  			{0x1175, 0x1167, 0x1168}, // ㅣ + ㅕ = ㅖ
  			{0x1175, 0x1169, 0x116c}, // ㅣ + ㅗ = ㅚ
  			{0x1175, 0x116e, 0x1171}, // ㅣ + ㅜ = ㅟ
			{0x1175, 0x1173, 0x1174}, // ㅣ + ㅡ = ㅢ
			{0x119e, 0x1175, 0x11a1}, // ㆍ + ㅣ = ㆎ
			{0x119e, 0x119e, 0x11a2}, // ㆍ + ㆍ = ᆢ
  			
			{0x11a8, 0x11af, 0x11b0}, // ㄱ + ㄹ = ㄺ
  			{0x11a8, 0x11ba, 0x11aa}, // ㄱ + ㅅ = ㄳ
  			{0x11a8, 0x11bc, 0x11a9}, // ㄱ + ㅇ = ㄲ
  			{0x11a8, 0x11c2, 0x11bf}, // ㄱ + ㅎ = ㅋ
  			{0x11ab, 0x11ba, 0x11bb}, // ㄴ + ㅅ = ㅆ
  			{0x11ab, 0x11bd, 0x11ac}, // ㄴ + ㅈ = ㄵ
  			{0x11ab, 0x11c2, 0x11ad}, // ㄴ + ㅎ = ㄶ
  			{0x11ae, 0x11af, 0x11ce}, // ㄷ + ㄹ = ᇎ
  			{0x11ae, 0x11c2, 0x11c0}, // ㄷ + ㅎ = ㅌ
  			{0x11af, 0x11a8, 0x11b0}, // ㄹ + ㄱ = ㄺ
  			{0x11af, 0x11ae, 0x11ce}, // ㄹ + ㄷ = ᇎ
  			{0x11af, 0x11b7, 0x11b1}, // ㄹ + ㅁ = ㄻ
			{0x11af, 0x11b8, 0x11b2}, // ㄹ + ㅂ = ㄼ
  			{0x11af, 0x11ba, 0x11b3}, // ㄹ + ㅅ = ㄽ
  			{0x11af, 0x11c2, 0x11b6}, // ㄹ + ㅎ = ㅀ
  			{0x11b2, 0x11c2, 0x11b5}, // ㄼ + ㅎ = ㄿ
  			{0x11b6, 0x11ae, 0x11b4}, // ㅀ + ㄷ = ㄾ
			{0x11b6, 0x11b8, 0x11b5}, // ㅀ + ㅂ = ㄿ
  			{0x11b7, 0x11af, 0x11b1}, // ㅁ + ㄹ = ㄻ
  			{0x11b8, 0x11af, 0x11b2}, // ㅂ + ㄹ = ㄼ
  			{0x11b8, 0x11ba, 0x11b9}, // ㅂ + ㅅ = ㅄ
  			{0x11b8, 0x11c2, 0x11c1}, // ㅂ + ㅎ = ㅍ
  			{0x11ba, 0x11a8, 0x11aa}, // ㅅ + ㄱ = ㄳ
  			{0x11ba, 0x11ab, 0x11bb}, // ㅅ + ㄴ = ㅆ
  			{0x11ba, 0x11af, 0x11b3}, // ㅅ + ㄹ = ㄽ
  			{0x11ba, 0x11b8, 0x11b9}, // ㅅ + ㅂ = ㅄ
  			{0x11bc, 0x11a8, 0x11a9}, // ㅇ + ㄱ = ㄲ
  			{0x11bd, 0x11ab, 0x11ac}, // ㅈ + ㄴ = ㄵ
  			{0x11bd, 0x11c2, 0x11be}, // ㅈ + ㅎ = ㅊ
  			{0x11c0, 0x11af, 0x11b4}, // ㅌ + ㄹ = ㄾ
  			{0x11c1, 0x11af, 0x11b5}, // ㅍ + ㄹ = ㄿ
  			{0x11c2, 0x11a8, 0x11bf}, // ㅎ + ㄱ = ㅋ
  			{0x11c2, 0x11ab, 0x11ad}, // ㅎ + ㄴ = ㄶ
  			{0x11c2, 0x11ae, 0x11c0}, // ㅎ + ㄷ = ㅌ
  			{0x11c2, 0x11af, 0x11b6}, // ㅎ + ㄹ = ㅀ
  			{0x11c2, 0x11b8, 0x11c1}, // ㅎ + ㅂ = ㅍ
  			{0x11c2, 0x11bd, 0x11be}, // ㅎ + ㅈ = ㅊ
  			{0x11ce, 0x11c2, 0x11b4}, // ᇎ + ㅎ = ㄾ

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
			{0x119e, 0x1175, 0x11a1},	// ㆎ
			{0x119e, 0x119e, 0x11a2},	// ᆢ

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
	
	public static final int[][] JAMO_SEBUL_SEMOE = {
			
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
			
			{113, 0x11ba, 0x11be},		// q
			{119, 0x11b8, 0x11c1},		// w
			{101, 0x11af, 0x11bd},		// e
			{114, 0x1165, 0x1163},		// r
			{116, 0x1167, 0x1164},		// t
			{121, 0x1106, 0x1106},		// y
			{117, 0x1102, 0x1102},		// u
			{105, 0x1103, 0x1110},		// i
			{111, 0x1107, 0x1111},		// o
			{112, 0x116e, 0x3b},		// p

			{97, 0x11bc, 0x11c0},		// a
			{115, 0x11ab, 0x11c2},		// s
			{100, 0x1175, 0x1175},		// d
			{102, 0x1161, 0x119e},		// f
			{103, 0x1173, 0x1173},		// g
			{104, 0x1112, 0xb7},		// h
			{106, 0x110b, 0x110b},		// j
			{107, 0x1100, 0x110f},		// k
			{108, 0x110c, 0x110e},		// l
			{59, 0x11bb, 0x3a},
			{39, 0x2c, 0x22},
			
			{122, 0x11b7, 0x11ae},		// z
			{120, 0x11a8, 0x11bf},		// x
			{99, 0x1166, 0x1168},		// c
			{118, 0x1169, 0x116d},		// v
			{98, 0x116e, 0x1172},		// b
			{110, 0x1109, 0x1109},		// n
			{109, 0x1105, 0x27},		// m
			{44, 0x2e, 0x3c},
			{46, 0x1169, 0x3e},
			
			{128, 0x2e, 0x2c},
	};

	
	public static final int[][] COMB_SEBUL_SEMOE = {
			
			{0x1100, 0x1100, 0x1101}, // ㄲ
			{0x1100, 0x110b, 0x1101}, // ㄱ + ㅇ = ㄲ
			{0x1100, 0x1112, 0x110f}, // ㄱ + ㅎ = ㅋ
			{0x1101, 0x1100, 0x110f}, // ㄲ + ㄱ = ㅋ
			{0x1103, 0x1103, 0x1104}, // ㄸ
			{0x1103, 0x110b, 0x1104}, // ㄷ + ㅇ = ㄸ
			{0x1103, 0x1112, 0x1110}, // ㄷ + ㅎ = ㅌ
			{0x1104, 0x1103, 0x1110}, // ㄸ + ㄷ = ㅌ
			{0x1107, 0x1107, 0x1108}, // ㅃ
			{0x1107, 0x110b, 0x1108}, // ㅂ + ㅇ = ㅃ
			{0x1107, 0x1112, 0x1111}, // ㅂ + ㅎ = ㅍ
			{0x1108, 0x1107, 0x1111}, // ㅃ + ㅂ = ㅍ
			{0x1109, 0x1109, 0x110a}, // ㅆ
			{0x1109, 0x110b, 0x110a}, // ㅅ + ㅇ = ㅆ
			{0x110b, 0x1100, 0x1101}, // ㅇ + ㄱ = ㄲ
			{0x110b, 0x1103, 0x1104}, // ㅇ + ㄷ = ㄸ
			{0x110b, 0x1107, 0x1108}, // ㅇ + ㅂ = ㅃ
			{0x110b, 0x1109, 0x110a}, // ㅇ + ㅅ = ㅆ
			{0x110b, 0x110c, 0x110d}, // ㅇ + ㅈ = ㅉ
			{0x110c, 0x110b, 0x110d}, // ㅈ + ㅇ = ㅉ
			{0x110c, 0x110c, 0x110d}, // ㅉ
			{0x110c, 0x1112, 0x110e}, // ㅈ + ㅎ = ㅊ
			{0x110d, 0x110c, 0x110e}, // ㅉ + ㅈ = ㅊ
			{0x1112, 0x1100, 0x110f}, // ㅎ + ㄱ = ㅋ
			{0x1112, 0x1103, 0x1110}, // ㅎ + ㄷ = ㅌ
			{0x1112, 0x1107, 0x1111}, // ㅎ + ㅂ = ㅍ
			{0x1112, 0x110c, 0x110e}, // ㅎ + ㅈ = ㅊ
	
			{0x1161, 0x1161, 0x1163}, // ㅏ + ㅏ = ㅑ
			{0x1161, 0x1165, 0x116d}, // ㅏ + ㅓ = ㅛ
			{0x1161, 0x1169, 0x116a}, // ㅏ + ㅗ = ㅘ
			{0x1161, 0x116c, 0x116b}, // ㅏ + ㅚ = ㅙ
			{0x1161, 0x1173, 0x119e}, // ㅏ + ㅡ = ㆍ
			{0x1161, 0x1174, 0x11a1}, // ㅏ + ㅢ = ㆎ
			{0x1161, 0x1175, 0x1162}, // ㅏ + ㅣ = ㅐ
			{0x1162, 0x1169, 0x116b}, // ㅐ + ㅗ = ㅙ
			{0x1162, 0x1173, 0x11a1}, // ㅐ + ㅡ = ㆎ
			{0x1163, 0x1175, 0x1164}, // ㅑ + ㅣ = ㅒ
			{0x1165, 0x1161, 0x116d}, // ㅓ + ㅏ = ㅛ
			{0x1165, 0x1165, 0x1163}, // ㅓ + ㅓ = ㅑ
			{0x1165, 0x1169, 0x1163}, // ㅓ + ㅗ = ㅑ
			{0x1165, 0x116e, 0x116f}, // ㅓ + ㅜ = ㅝ
			{0x1165, 0x1173, 0x11a2}, // ㅓ + ㅡ = ᆢ
			{0x1166, 0x1166, 0x1168}, // ㅔ + ㅔ = ㅖ
			{0x1166, 0x1169, 0x1168}, // ㅔ + ㅗ = ㅖ
			{0x1166, 0x116e, 0x1170}, // ㅔ + ㅜ = ㅞ
			{0x1167, 0x1167, 0x1164}, // ㅕ + ㅕ = ㅒ
			{0x1167, 0x1169, 0x1164}, // ㅕ + ㅗ = ㅒ
			{0x1169, 0x1161, 0x116a}, // ㅗ + ㅏ = ㅘ
			{0x1169, 0x1162, 0x116b}, // ㅗ + ㅐ = ㅙ
			{0x1169, 0x1165, 0x1163}, // ㅗ + ㅓ = ㅑ
			{0x1169, 0x1166, 0x1168}, // ㅗ + ㅔ = ㅖ
			{0x1169, 0x1167, 0x1164}, // ㅗ + ㅕ = ㅒ
			{0x1169, 0x1169, 0x116d}, // ㅗ + ㅗ = ㅛ
			{0x1169, 0x116e, 0x1172}, // ㅗ + ㅜ = ㅠ
			{0x1169, 0x1175, 0x116c}, // ㅗ + ㅣ = ㅚ
			{0x116a, 0x1175, 0x116b}, // ㅘ + ㅣ = ㅙ
			{0x116c, 0x1161, 0x116b}, // ㅚ + ㅏ = ㅙ
			{0x116e, 0x1165, 0x116f}, // ㅜ + ㅓ = ㅝ
			{0x116e, 0x1166, 0x1170}, // ㅜ + ㅔ = ㅞ
			{0x116e, 0x1169, 0x1172}, // ㅜ + ㅗ = ㅠ
			{0x116e, 0x116e, 0x1172}, // ㅜ + ㅜ = ㅠ
			{0x116e, 0x1175, 0x1171}, // ㅜ + ㅣ = ㅟ
			{0x1173, 0x1161, 0x119e}, // ㅡ + ㅏ = ㆍ
			{0x1173, 0x1162, 0x11a1}, // ㅡ + ㅐ = ㆎ
			{0x1173, 0x1165, 0x11a2}, // ㅡ + ㅓ = ᆢ
			{0x1173, 0x1175, 0x1174}, // ㅡ + ㅣ = ㅢ
			{0x1174, 0x1161, 0x11a1}, // ㅢ + ㅏ = ㆎ
			{0x1175, 0x1161, 0x1162}, // ㅣ + ㅏ = ㅐ
			{0x1175, 0x1169, 0x116c}, // ㅣ + ㅗ = ㅚ
			{0x1175, 0x116a, 0x116b}, // ㅣ + ㅘ = ㅙ
			{0x1175, 0x116e, 0x1171}, // ㅣ + ㅜ = ㅟ
			{0x1175, 0x1173, 0x1174}, // ㅣ + ㅡ = ㅢ
			{0x1175, 0x119e, 0x11a1}, // ㅣ + ㆍ = ㆎ
			{0x119e, 0x1175, 0x11a1}, // ㆍ + ㅣ = ㆎ
			{0x119e, 0x119e, 0x11a2}, // ㆍ + ㆍ = ᆢ
			
			{0x11a8, 0x11a8, 0x11a9}, // ㄲ
			{0x11a8, 0x11ae, 0x11aa}, // ㄱ + ㄷ = ㄳ
			{0x11a8, 0x11af, 0x11b0}, // ㄱ + ㄹ = ㄺ
			{0x11a8, 0x11b7, 0x11b0}, // ㄱ + ㅁ = ㄺ
			{0x11a8, 0x11ba, 0x11aa}, // ㄱ + ㅅ = ㄳ
			{0x11a8, 0x11bb, 0x11bf}, // ㄱ + ㅆ = ㅋ
			{0x11a8, 0x11bc, 0x11a9}, // ㄱ + ㅇ = ㄲ
			{0x11a8, 0x11c2, 0x11bf}, // ㄱ + ㅎ = ㅋ
			{0x11a9, 0x11a8, 0x11bf}, // ㄲ + ㄱ = ㅋ
			{0x11ab, 0x11ab, 0x11c2}, // ㄴ + ㄴ = ㅎ
			{0x11ab, 0x11af, 0x11ac}, // ㄴ + ㄹ = ㄵ
			{0x11ab, 0x11b7, 0x11c0}, // ㄴ + ㅁ = ㅌ
			{0x11ab, 0x11bb, 0x11c2}, // ㄴ + ㅆ = ㅎ
			{0x11ab, 0x11bc, 0x11ad}, // ㄴ + ㅇ = ㄶ
			{0x11ab, 0x11bd, 0x11ac}, // ㄵ
			{0x11ab, 0x11c2, 0x11ad}, // ㄴ + ㅎ = ㄶ
			{0x11ac, 0x11ab, 0x11b6}, // ㄵ + ㄴ = ㅀ
			{0x11ae, 0x11a8, 0x11aa}, // ㄷ + ㄱ = ㄳ
			{0x11ae, 0x11b7, 0x11c0}, // ㄷ + ㅁ = ㅌ
			{0x11ae, 0x11c2, 0x11c0}, // ㄷ + ㅎ = ㅌ
			{0x11af, 0x11a8, 0x11b0}, // ㄹ + ㄱ = ㄺ
			{0x11af, 0x11ab, 0x11ac}, // ㄹ + ㄴ = ㄵ
			{0x11af, 0x11af, 0x11bd}, // ㄹ + ㄹ = ㅈ
			{0x11af, 0x11b7, 0x11b1}, // ㄹ + ㅁ = ㄻ
			{0x11af, 0x11b8, 0x11b2}, // ㄹ + ㅂ + ㄼ
			{0x11af, 0x11ba, 0x11b3}, // ㄹ + ㅅ = ㄽ
			{0x11af, 0x11bb, 0x11bd}, // ㄹ + ㅆ = ㅈ
			{0x11af, 0x11bc, 0x11a8}, // ㄹ + ㅇ = ㄱ
			{0x11af, 0x11be, 0x11a9}, // ㄹ + ㅊ = ㄲ
			{0x11af, 0x11c0, 0x11b4}, // ㄾ
			{0x11af, 0x11c1, 0x11b5}, // ㄹ + ㅍ = ㄿ
			{0x11af, 0x11c2, 0x11b6}, // ㅀ
			{0x11b0, 0x11bb, 0x11aa}, // ㄺ + ㅆ = ㄳ
			{0x11b1, 0x11b7, 0x11b4}, // ㄻ + ㅁ = ㄾ
			{0x11b2, 0x11b8, 0x11b5}, // ㄼ + ㅂ = ㄿ
			{0x11b2, 0x11bb, 0x11c0}, // ㄼ + ㅆ = ㅌ
			{0x11b3, 0x11bb, 0x11a9}, // ㄽ + ㅆ = ㄲ
			{0x11b7, 0x11a8, 0x11b0}, // ㅁ + ㄱ = ㄺ
			{0x11b7, 0x11ab, 0x11c0}, // ㅁ + ㄴ = ㅌ
			{0x11b7, 0x11af, 0x11b1}, // ㅁ + ㄹ = ㄻ
			{0x11b7, 0x11b7, 0x11ae}, // ㅁ + ㅁ = ㄷ
			{0x11b7, 0x11bb, 0x11ae}, // ㅁ + ㅆ = ㄷ
			{0x11b7, 0x11bc, 0x11b4}, // ㅁ + ㅇ = ㄾ
			{0x11b7, 0x11bf, 0x11aa}, // ㅁ + ㅋ = ㄳ
			{0x11b8, 0x11af, 0x11b2}, // ㅂ + ㄹ = ㄼ
			{0x11b8, 0x11b8, 0x11c1}, // ㅂ + ㅂ = ㅍ
			{0x11b8, 0x11ba, 0x11b9}, // ㅂ + ㅅ = ㅄ
			{0x11b8, 0x11bb, 0x11c1}, // ㅂ + ㅆ = ㅍ
			{0x11b8, 0x11bc, 0x11b5}, // ㅂ + ㅇ = ㄿ
			{0x11b8, 0x11bd, 0x11c0}, // ㅂ + ㅈ = ㅌ
			{0x11b8, 0x11be, 0x11b1}, // ㅂ + ㅊ = ㄻ
			{0x11b8, 0x11c2, 0x11c1}, // ㅂ + ㅎ = ㅍ
			{0x11b9, 0x11bb, 0x11b1}, // ㅄ + ㅆ = ㄻ
			{0x11ba, 0x11a8, 0x11aa}, // ㅅ + ㄱ = ㄳ
			{0x11ba, 0x11af, 0x11b3}, // ㅅ + ㄹ = ㄽ
			{0x11ba, 0x11b8, 0x11b9}, // ㅅ + ㅂ = ㅄ
			{0x11ba, 0x11bb, 0x11be}, // ㅅ + ㅆ = ㅊ
			{0x11ba, 0x11ba, 0x11bb}, // ㅅ + ㅅ = ㅆ
			{0x11ba, 0x11bc, 0x11bb}, // ㅅ + ㅇ = ㅆ
			{0x11ba, 0x11bd, 0x11a9}, // ㅅ + ㅈ = ㄲ
			{0x11ba, 0x11c1, 0x11b1}, // ㅅ + ㅍ = ㄻ
			{0x11bb, 0x11a8, 0x11bf}, // ㅆ + ㄱ = ㅋ
			{0x11bb, 0x11ab, 0x11c2}, // ㅆ + ㄴ = ㅎ
			{0x11bb, 0x11af, 0x11bd}, // ㅆ + ㄹ = ㅈ
			{0x11bb, 0x11b0, 0x11aa}, // ㅆ + ㄺ = ㄳ
			{0x11bb, 0x11b2, 0x11c0}, // ㅆ + ㄼ = ㅌ
			{0x11bb, 0x11b3, 0x11a9}, // ㅆ + ㄽ = ㄲ
			{0x11bb, 0x11b7, 0x11ae}, // ㅆ + ㅁ = ㄷ
			{0x11bb, 0x11b8, 0x11c1}, // ㅆ + ㅂ = ㅍ
			{0x11bb, 0x11b9, 0x11b1}, // ㅆ + ㅄ = ㄻ
			{0x11bb, 0x11ba, 0x11be}, // ㅆ + ㅅ = ㅊ
			{0x11bb, 0x11bc, 0x11b6}, // ㅆ + ㅇ = ㅀ
			{0x11bc, 0x11a8, 0x11a9}, // ㅇ + ㄱ = ㄲ
			{0x11bc, 0x11ab, 0x11ad}, // ㅇ + ㄴ = ㄶ
			{0x11bc, 0x11af, 0x11a8}, // ㅇ + ㄹ = ㄱ
			{0x11bc, 0x11b7, 0x11b4}, // ㅇ + ㅁ = ㄾ
			{0x11bc, 0x11b8, 0x11b5}, // ㅇ + ㅂ = ㄿ
			{0x11bc, 0x11ba, 0x11bb}, // ㅇ + ㅅ = ㅆ
			{0x11bc, 0x11bb, 0x11b6}, // ㅇ + ㅆ = ㅀ
			{0x11bc, 0x11bc, 0x11c0}, // ㅇ + ㅇ = ㅌ
			{0x11bd, 0x11b8, 0x11c0}, // ㅈ + ㅂ = ㅌ
			{0x11bd, 0x11ba, 0x11a9}, // ㅈ + ㅅ = ㄲ
			{0x11be, 0x11af, 0x11a9}, // ㅊ + ㄹ = ㄲ
			{0x11be, 0x11b8, 0x11b1}, // ㅊ + ㅂ = ㄻ
			{0x11bf, 0x11b7, 0x11aa}, // ㅋ + ㅁ = ㄳ
			{0x11c0, 0x11bc, 0x11b6}, // ㅌ + ㅇ = ㅀ
			{0x11c1, 0x11af, 0x11c0}, // ㅍ + ㄹ = ㅌ
			{0x11c1, 0x11ba, 0x11b1}, // ㅍ + ㅅ = ㄻ
			{0x11c2, 0x11a8, 0x11bf}, // ㅎ + ㄱ = ㅋ
			{0x11c2, 0x11ab, 0x11ad}, // ㅎ + ㄴ = ㄶ
			{0x11c2, 0x11ae, 0x11c0}, // ㅎ + ㄷ = ㅌ
			{0x11c2, 0x11b8, 0x11c1}, // ㅎ + ㅂ = ㅍ
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
			{119, 0x2c, 0x3c},		// w
			{101, 0x2e, 0x3e},		// e
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
			{0x3b, 115, 83},	// ;
			{0x27, 0x2d, 0x5f},	// '
			
			{122, 0x3b, 0x3a},		// z
			{120, 113, 81},		// x
			{99, 106, 74},		// c
			{118, 107, 75},		// v
			{98, 120, 88},			// b
			{110, 98, 66},		// n
			{109, 109, 77},		// m
			{0x2c, 119, 87},	// ,
			{0x2e, 118, 86},	// .
			{0x2f, 122, 90},	// /

			{0x2d, 0x5b, 0x7b},
			{0x3d, 0x5d, 0x7d},
			{0x5b, 0x2f, 0x3f},
			{0x5d, 0x3d, 0x2b},
	};

	public static final int[][] CONVERT_ENGLISH_COLEMAK = {
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
			
			{113, 113, 81},		// q
			{119, 119, 87},		// w
			{101, 102, 70},		// e
			{114, 112, 80},		// r
			{116, 103, 71},		// t
			{121, 106, 74},		// y
			{117, 108, 76},		// u
			{105, 117, 85},		// i
			{111, 121, 89},		// o
			{112, 0x3b, 0x3a},			// p
			
			{97, 97, 65},		// a
			{115, 114, 82},		// s
			{100, 115, 83},		// d
			{102, 116, 84},		// f
			{103, 100, 68},		// g
			{104, 104, 72},		// h
			{106, 110, 78},		// j
			{107, 101, 69},		// k
			{108, 105, 73},		// l
			{0x3b, 111, 79},
			
			{122, 122, 90},		// z
			{120, 120, 88},		// x
			{99, 99, 67},		// c
			{118, 118, 86},		// v
			{98, 98, 66},			// b
			{110, 107, 75},		// n
			{109, 109, 77},		// m
			{0x2c, 0x2c, 0x3c},
			{0x2e, 0x2e, 0x3e},
			{0x2f, 0x2f, 0x3f},
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
			{0x11a8, 0x11bb, 0x11aa},	// ㄳ
			{0x11ab, 0x11bb, 0x11ac},	// ㄵ
			{0x11af, 0x11a8, 0x11b0},	// ㄺ
			{0x11af, 0x11b8, 0x11b2},	// ㄼ
			{0x11af, 0x11bb, 0x11b3},	// ㄽ
			{0x11af, 0x11c1, 0x11b5},	// ㄿ
			{0x11af, 0x11ab, 0x11b6},	// ㅀ
			{0x11b8, 0x11bb, 0x11b9},	// ㅄ
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
			{0x11b0, 0x11b2, 0x11b5},	// ㄺ ㄼ ㄿ
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
			{-2008, -2003},	// ᆞ
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
			
			{0x1175, -2003, 0x1161},	// ㅏ
			{0x1161, 0x1175, 0x1162},	// ㅐ
			{0x1161, -2003, 0x1163},	// ㅑ
			{0x1163, 0x1175, 0x1164},	// ㅒ
			{-2003, -2003, -2004},	// ᆢ
			{-2003, 0x1175, 0x1165},	// ㅓ
			{0x1165, 0x1175, 0x1166},	// ㅔ
			{-2004, 0x1175, 0x1167},	// ㅕ (··+ㅣ)
			{-2003, 0x1165, 0x1167},	// ㅕ (·+ㅓ)
			{0x1165, -2003, 0x1167},	// ㅕ (ㅓ+·)
			{0x1167, 0x1175, 0x1168},	// ㅖ
			{-2003, 0x1173, 0x1169},	// ㅗ
			{0x116c, -2003, 0x116a},	// ㅘ (ㅚ+·)
			{0x1169, 0x1161, 0x116a},	// ㅘ (ㅗ+ㅏ)
			{0x116a, 0x1175, 0x116b},	// ㅙ
			{0x1169, 0x1175, 0x116c},	// ㅚ
			{-2004, 0x1173, 0x116d},	// ㅛ (··+ㅡ)
			{-2003, 0x1169, 0x116d},	// ㅛ (·+ㅗ)
			{0x1169, -2003, 0x116d},	// ㅛ (ㅗ+·)
			{0x1173, -2003, 0x116e},	// ㅜ
			{0x1172, 0x1175, 0x116f},	// ㅝ (ㅠ+ㅣ)
			{0x116e, 0x1165, 0x116f},	// ㅝ (ㅜ+ㅓ)
			{0x116f, 0x1175, 0x1170},	// ㅞ
			{0x116e, 0x1175, 0x1171},	// ㅟ
			{0x116e, -2003, 0x1172},	// ㅠ
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
	
	
	public static final int[][] CYCLE_SEBUL_12KEY_SENA = {
			{-2001, 0x1100},	// ㄱ
			{-2002, 0x110b},	// ㅇ
			{-2003, 0x1169},	//* ㅗ
			{-2004, 0x1161},	//* ㅏ
			{-2005, 0x11bc},	// ㅇ
			{-2006, 0x11a8},	// ㄱ
			
			{-2007, 0x1106},	// ㅁ
			{-2008, 0x1102},	// ㄴ
			{-2009, 0x1173},	//* ㅡ
			{-2010, 0x1175},	//* ㅣ
			{-2011, 0x11ab},	// ㄴ
			{-2012, 0x11b7},	// ㅁ
			
			{-2013, DefaultSoftKeyboardKOKR.KEYCODE_KR12_ADDSTROKE},
			{-2014, 0x1109},	// ㅅ
			{-2015, 0x116e},	//* ㅜ
			{-2016, 0x1165},	//* ㅓ
			{-2017, 0x11ba},	// ㅅ
			{-2018, 0x2e},
			{-2518, 0x2c},

			{-2503, 0x31},
			{-2504, 0x32},
			{-2505, 0x33},
			{-2509, 0x34},
			{-2510, 0x35},
			{-2511, 0x36},
			{-2515, 0x37},
			{-2516, 0x38},
			{-2517, 0x39},
			{-2514, 0x30},
	};
	
	public static final int[][] COMB_SEBUL_12KEY_SENA = {
			// --초성----------------------------------------
			{0x1100, 0x1100, 0x1101},	// ㄲ [ㄱ + ㄱ]
			{0x1100, 0x1106, 0x110f},	//* ㅋ [ㄱ + ㅁ]
			{0x1102, 0x1102, 0x1105},	// ㄹ (ㄴ + ㄴ)
			{0x1102, 0x1109, 0x1103},	//* ㄷ (ㄴ + ㅅ)
			{0x1102, 0x110b, 0x1110},	//* ㅌ (ㄴ + ㅇ)
			{0x1105, 0x1109, 0x1104},	//* ㄸ (ㄹ + ㅅ)
			{0x1107, 0x1106, 0x1108},	// ㅃ (ㅂ + ㅁ)
			{0x1109, 0x1109, 0x110a},	// ㅆ [ㅅ + ㅅ]
			{0x1109, 0x1102, 0x110c},	//* ㅈ [ㅅ + ㄴ]
			{0x110c, 0x1102, 0x110e},	//* ㅊ [ㅈ + ㄴ]
			{0x110a, 0x1102, 0x110d},	//* ㅉ [ㅆ + ㄴ]
			{0x1109, 0x1106, 0x110e},	//* ㅊ [ㅅ + ㅁ]
			{0x1106, 0x1106, 0x1107},	// ㅂ (ㅁ + ㅁ)
			{0x1106, 0x1100, 0x1111},	//* ㅍ (ㅁ + ㄱ)
			{0x110b, 0x110b, 0x1112},	// ㅎ (ㅇ + ㅇ)
			{0x110b, 0x1102, 0x1112},	//* ㅎ (ㅇ + ㄴ)

			// --중성----------------------------------------
			{0x1161, 0x1175, 0x1162},	// ㅐ [ㅏ + ㅣ]
			{0x1161, 0x1161, 0x1163},	// ㅑ [ㅏ + ㅏ]
			{0x1163, 0x1175, 0x1164},	// ㅒ [ㅑ + ㅣ]
			{0x1165, 0x1175, 0x1166},	// ㅔ [ㅓ + ㅣ]
			{0x1165, 0x1165, 0x1167},	// ㅕ (ㅓ + ㅓ)
			{0x1167, 0x1175, 0x1168},	// ㅖ [ㅕ + ㅣ]
			{0x1169, 0x1161, 0x116a},	// ㅘ (ㅗ + ㅏ)
			{0x116a, 0x1175, 0x116b},	// ㅙ [ㅘ + ㅣ]
			{0x1169, 0x1175, 0x116c},	// ㅚ [ㅗ + ㅣ]
			{0x1169, 0x1169, 0x116d},	// ㅛ (ㅗ + ㅗ)
			{0x116e, 0x1165, 0x116f},	// ㅝ (ㅜ + ㅓ)
			{0x116f, 0x1175, 0x1170},	// ㅞ [ㅝ + ㅣ]
			{0x116e, 0x1175, 0x1171},	// ㅟ [ㅜ + ㅣ]
			{0x116e, 0x116e, 0x1172},	// ㅠ [ㅜ + ㅜ]
			{0x1173, 0x1175, 0x1174},	// ㅢ [ㅡ + ㅣ]

			// --종성----------------------------------------
			{0x11ab, 0x11ab, 0x11af},	// ㄹ [ㄴ + ㄴ]
			{0x11b7, 0x11b7, 0x11b8},	// ㅂ [ㅁ + ㅁ]
			{0x11b7, 0x11a8, 0x11c1},	//* ㅍ (ㅁ + ㄱ)
			{0x11bc, 0x11bc, 0x11c2},	// ㅎ [ㅇ + ㅇ]
			{0x11bc, 0x11AB, 0x11c2},	//* ㅎ [ㅇ + ㄴ]
			{0x11a8, 0x11a8, 0x11a9},	// ㄲ [ㄱ + ㄱ]
			{0x11a8, 0x11ba, 0x11aa},	// ㄳ [ㄱ + ㅅ]
			{0x11A8, 0x11B7, 0x11BF},	//* ㅋ [ㄱ + ㅁ]
			{0x11ab, 0x11ba, 0x11ac},	// ㄵ [ㄴ + ㅅ]
			{0x11ab, 0x11bc, 0x11ad},	// ㄶ [ㄴ + ㅇ]
			{0x11af, 0x11a8, 0x11b0},	// ㄺ [ㄹ + ㄱ]
			{0x11af, 0x11b7, 0x11b1},	// ㄻ [ㄹ + ㅁ]
			{0x11b1, 0x11b7, 0x11b2},	// ㄼ [ㄻ + ㅁ]
			{0x11b2, 0x11b7, 0x11b5},	// ㄿ [ㄼ + ㅁ]
			{0x11af, 0x11ba, 0x11b3},	// ㄽ [ㄹ + ㅅ]
			{0x11af, 0x11ab, 0x11b4},	// ㄾ [ㄹ + ㄴ]
			{0x11af, 0x11bc, 0x11b6},	// ㅀ [ㄹ + ㅇ]
			{0x11b8, 0x11ba, 0x11b9},	// ㅄ [ㅂ + ㅅ]
			{0x11ba, 0x11ba, 0x11bb},	// ㅆ [ㅅ + ㅅ]
			{0x11BA, 0x11AB, 0x11BD},	//* ㅈ [ㅅ + ㄴ]
			{0x11BA, 0x11b7, 0x11be},	//* ㅊ [ㅅ + ㅁ]
			{0x11BD, 0x11AB, 0x11be},	//* ㅊ [ㅈ + ㄴ]
			
	};
	
	public static final int[][] STROKE_SEBUL_12KEY_SENA = {
			// -------------------초성
			{0x1100, 0x110f},			// ㄱ ㅋ
			{0x1102, 0x1103, 0x1110},	// ㄴ ㄷ ㅌ
			{0x1106, 0x1107, 0x1111},	// ㅁ ㅂ ㅍ
			{0x1109, 0x110c, 0x110e},	// ㅅ ㅈ ㅊ
			{0x110b, 0x1112},			// ㅇ ㅎ
			{0x1105, 0x1104},			// ㄹ ㄸ
			{0x110a, 0x110d},			// ㅆ ㅉ
			// -------------------종성
			{0x11ab, 0x11ae, 0x11c0},	// ㄴ ㄷ ㅌ
			{0x11b7, 0x11b8, 0x11c1},	// ㅁ ㅂ ㅍ
			{0x11ba, 0x11bd, 0x11be},	// ㅅ ㅈ ㅊ
			{0x11a8, 0x11bf},			// ㄱ ㅋ
			{0x11bc, 0x11c2},			// ㅇ ㅎ

			{0x11b1, 0x11b2, 0x11b5},	// ㄻ ㄼ ㄿ
			
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
			{120, 0xb7},		// x
			{99, 0x3d},			// c
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
	public static final int ENGINE_MODE_DUBULSIK_NK = 105;
	public static final int ENGINE_MODE_DUBUL_DANMOEUM = 106;
	public static final int ENGINE_MODE_SEBUL_SUN_2014 = 107;
	public static final int ENGINE_MODE_SEBUL_3_2015M = 108;	
	public static final int ENGINE_MODE_SEBUL_3_2015 = 109;	
	public static final int ENGINE_MODE_SEBUL_3_P3 = 110;	
	public static final int ENGINE_MODE_SEBUL_SHIN_ORIGINAL = 111;
	public static final int ENGINE_MODE_SEBUL_SHIN_EDIT = 112;
	public static final int ENGINE_MODE_SEBUL_SHIN_M = 113;
	public static final int ENGINE_MODE_SEBUL_SHIN_P2 = 114;
	public static final int ENGINE_MODE_SEBUL_AHNMATAE = 115;
	public static final int ENGINE_MODE_SEBUL_SEMOE = 116;
	
	public static final int ENGINE_MODE_DUBULSIK_YET = 117;
	public static final int ENGINE_MODE_SEBUL_393Y = 118;
	public static final int ENGINE_MODE_SEBUL_3_2015Y = 119;	
	
	public static final int ENGINE_MODE_12KEY_ALPHABET = 150;
	public static final int ENGINE_MODE_12KEY_SEBUL_MUNHWA = 151;
	public static final int ENGINE_MODE_12KEY_SEBUL_HANSON = 153;
	public static final int ENGINE_MODE_12KEY_DUBUL = 155;
	public static final int ENGINE_MODE_12KEY_SEBUL_SENA = 156;

	public static final int ENGINE_MODE_ENGLISH_DVORAK = 192;
	public static final int ENGINE_MODE_ENGLISH_COLEMAK = 193;
	
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
	boolean mCapsLock;
	boolean mShiftOnCapsLock;
	
	boolean mShiftPressing;
	boolean mAltPressing;
	
	int mCurrentEngineMode;
	
	private static final int[] mShiftKeyToggle = {0, MetaKeyKeyListener.META_SHIFT_ON, MetaKeyKeyListener.META_CAP_LOCKED};
	
	private static final int[] mAltKeyToggle = {0, MetaKeyKeyListener.META_ALT_ON, MetaKeyKeyListener.META_ALT_LOCKED};
	
	boolean mDirectInputMode;
	boolean mEnableTimeout;
	
	boolean mMoachigi;
	boolean mHardwareMoachigi;
	boolean mFullMoachigi = true;
	int mMoachigiDelay;

	boolean mStandardJamo;

	boolean mAltDirect;
	
	boolean selectionMode;
	int selectionStart, selectionEnd;

	Handler mTimeOutHandler;

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
			updateNumKeyboardShiftState();
		}
		
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		
		boolean hardKeyboardHidden = ((DefaultSoftKeyboard) mInputViewManager).mHardKeyboardHidden;
		
		mMoachigi = pref.getBoolean("keyboard_use_moachigi", mMoachigi);
		mHardwareMoachigi = pref.getBoolean("hardware_use_moachigi", mHardwareMoachigi);
		mFullMoachigi = pref.getBoolean("hardware_full_moachigi", mFullMoachigi);
		mMoachigiDelay = pref.getInt("hardware_full_moachigi_delay", 100);
		mStandardJamo = pref.getBoolean("system_use_standard_jamo", mStandardJamo);
		
		if(hardKeyboardHidden) mQwertyEngine.setMoachigi(mMoachigi);
		else mQwertyEngine.setMoachigi(mHardwareMoachigi);
		mQwertyEngine.setFullMoachigi(mFullMoachigi && !hardKeyboardHidden);
		mQwertyEngine.setFirstMidEnd(mStandardJamo);
		m12keyEngine.setFirstMidEnd(mStandardJamo);

		mAltDirect = pref.getBoolean("hardware_alt_direct", true);
		
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
		if(mInputConnection == null) return;
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
				if(mCapsLock) {
					mHardShift = 0;
					mShiftPressing = false;
					mShiftOnCapsLock = true;
				}
				updateMetaKeyStateDisplay();
				updateNumKeyboardShiftState();
				return true;

			case KeyEvent.KEYCODE_CAPS_LOCK:
				mCapsLock = !mCapsLock;
				if(mCapsLock) {
					mHardShift = 2;
					mShiftPressing = true;
				} else {
					mHardShift = 0;
					mShiftPressing = false;
				}
				updateMetaKeyStateDisplay();
				updateNumKeyboardShiftState();
				return true;

			}
			if((keyEvent.getMetaState() & KeyEvent.META_CAPS_LOCK_ON) != 0) {
				if(!mShiftOnCapsLock) {
					mCapsLock = true;
					mHardShift = 2;
					mShiftPressing = true;
					updateMetaKeyStateDisplay();
					updateNumKeyboardShiftState();
				}
			} else {
				if(mCapsLock == true) {
					mCapsLock = false;
					mHardShift = 0;
					mShiftPressing = false;
					updateMetaKeyStateDisplay();
					updateNumKeyboardShiftState();
				}
			}
			((DefaultSoftKeyboardKOKR) mInputViewManager).fixHardwareLayoutState();
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
					updateNumKeyboardShiftState();
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
		
		if(key >= KeyEvent.KEYCODE_NUMPAD_0 && key <= KeyEvent.KEYCODE_NUMPAD_RIGHT_PAREN) {
			mHangulEngine.resetJohab();
			return false;
		}
		
		if(ev.isPrintingKey()) {
			
			int code = ev.getUnicodeChar(mShiftKeyToggle[mHardShift] | mAltKeyToggle[mHardAlt]);
			this.inputChar((char) code, (mHardAlt != 0 && mAltDirect) ? true : false);
			
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
				updateNumKeyboardShiftState();
			}
			return true;
			
		} else if(key == KeyEvent.KEYCODE_SPACE) {
			mHangulEngine.resetJohab();
			if(mHardShift == 1) {
				((DefaultSoftKeyboardKOKR) mInputViewManager).nextLanguage();
				mHardShift = 0;
				mShiftPressing = false;
				updateMetaKeyStateDisplay();
				updateNumKeyboardShiftState();
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
			updateNumKeyboardShiftState();
			return false;
		} else {
			mHangulEngine.resetJohab();
		}
		
		return false;
	}

	private void changeEngineMode(int mode) {
		boolean hardHidden = ((DefaultSoftKeyboardKOKR) mInputViewManager).mHardKeyboardHidden;
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
			
		case ENGINE_MODE_ENGLISH_COLEMAK:
			mDirectInputMode = false;
			mEnableTimeout = false;
			mHangulEngine = mQwertyEngine;
			mHangulEngine.setJamoTable(CONVERT_ENGLISH_COLEMAK);
			break;
			
		case ENGINE_MODE_DUBULSIK:
			mDirectInputMode = false;
			mEnableTimeout = false;
			mHangulEngine.setJamoTable(JAMO_DUBUL_STANDARD);
			mHangulEngine.setCombinationTable(COMB_DUBUL_STANDARD);
			break;
			
		case ENGINE_MODE_DUBULSIK_NK:
			mDirectInputMode = false;
			mEnableTimeout = false;
			mHangulEngine.setJamoTable(JAMO_DUBUL_NK);
			mHangulEngine.setCombinationTable(COMB_DUBUL_STANDARD);
			break;
			
		case ENGINE_MODE_DUBULSIK_YET:
			mDirectInputMode = false;
			mEnableTimeout = false;
			mHangulEngine.setJamoTable(JAMO_DUBUL_YET);
			mHangulEngine.setCombinationTable(COMB_FULL);
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
			
		case ENGINE_MODE_SEBUL_393Y:
			mDirectInputMode = false;
			mEnableTimeout = false;
			mHangulEngine.setJamoTable(JAMO_SEBUL_393Y);
			mHangulEngine.setCombinationTable(COMB_FULL);
			break;
	
		case ENGINE_MODE_SEBUL_SUN_2014:
			mDirectInputMode = false;
			mEnableTimeout = false;
			mHangulEngine.setJamoTable(JAMO_SEBUL_SUN_2014);
			mHangulEngine.setCombinationTable(COMB_SEBUL_SUN_2014);
			break;
	
		case ENGINE_MODE_SEBUL_3_2015M:
			mDirectInputMode = false;
			mEnableTimeout = false;
			mHangulEngine.setJamoSet(JAMOSET_SEBUL_3_2015M);
			mHangulEngine.setCombinationTable(COMB_SEBUL_3_2015);
			break;
	
		case ENGINE_MODE_SEBUL_3_2015:
			mDirectInputMode = false;
			mEnableTimeout = false;
			mHangulEngine.setJamoSet(JAMOSET_SEBUL_3_2015);
			mHangulEngine.setCombinationTable(COMB_SEBUL_3_2015);
			break;	
			
		case ENGINE_MODE_SEBUL_3_2015Y:
			mDirectInputMode = false;
			mEnableTimeout = false;
			mHangulEngine.setJamoTable(JAMO_SEBUL_3_2015Y);
			mHangulEngine.setCombinationTable(COMB_FULL);
			break;	
	
		case ENGINE_MODE_SEBUL_3_P3:
			mDirectInputMode = false;
			mEnableTimeout = false;
			mHangulEngine.setJamoSet(JAMOSET_SEBUL_3_P3);
			mHangulEngine.setCombinationTable(COMB_SEBUL_3_P3);
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
			mHangulEngine.setJamoSet(JAMOSET_SHIN_ORIGINAL);
			mHangulEngine.setCombinationTable(COMB_SEBUL_SHIN_ORIGINAL);
			mHangulEngine.setVirtualJamoTable(VIRTUAL_SEBUL_SHIN_ORIGINAL);
			break;
			
		case ENGINE_MODE_SEBUL_SHIN_EDIT:
			mDirectInputMode = false;
			mEnableTimeout = false;
			mHangulEngine.setJamoSet(JAMOSET_SHIN_EDIT);
			mHangulEngine.setCombinationTable(COMB_SEBUL_SHIN_ORIGINAL);
			mHangulEngine.setVirtualJamoTable(VIRTUAL_SEBUL_SHIN_ORIGINAL);
			break;
			
		case ENGINE_MODE_SEBUL_SHIN_M:
			mDirectInputMode = false;
			mEnableTimeout = false;
			mHangulEngine.setJamoSet(JAMOSET_SHIN_M);
			mHangulEngine.setCombinationTable(COMB_SEBUL_SHIN_ORIGINAL);
			mHangulEngine.setVirtualJamoTable(VIRTUAL_SEBUL_SHIN_ORIGINAL);
			break;
		
		case ENGINE_MODE_SEBUL_SHIN_P2:
			mDirectInputMode = false;
			mEnableTimeout = false;
			mHangulEngine.setJamoSet(JAMOSET_SHIN_P2);
			mHangulEngine.setCombinationTable(COMB_FULL);
			mHangulEngine.setVirtualJamoTable(VIRTUAL_SEBUL_SHIN_ORIGINAL);
			break;
		
		case ENGINE_MODE_SEBUL_AHNMATAE:
			mDirectInputMode = false;
			if(mFullMoachigi && !hardHidden) mEnableTimeout = true;
			else mEnableTimeout = false;
			mHangulEngine.setJamoTable(JAMO_SEBUL_AHNMATAE);
			mHangulEngine.setCombinationTable(COMB_SEBUL_AHNMATAE);
			break;	
		
		case ENGINE_MODE_SEBUL_SEMOE:
			mDirectInputMode = false;
			if(mFullMoachigi && !hardHidden) mEnableTimeout = true;
			else mEnableTimeout = false;
			mHangulEngine.setJamoTable(JAMO_SEBUL_SEMOE);
			mHangulEngine.setCombinationTable(COMB_SEBUL_SEMOE);
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
			
		case ENGINE_MODE_12KEY_SEBUL_SENA:
			mDirectInputMode = false;
			mEnableTimeout = false;
			mHangulEngine.setJamoTable(CYCLE_SEBUL_12KEY_SENA);
			mHangulEngine.setCombinationTable(COMB_SEBUL_12KEY_SENA);
			((TwelveHangulEngine) mHangulEngine).setAddStrokeTable(STROKE_SEBUL_12KEY_SENA);
			break;
			
		}
	}
	
	private void onKeyUpEvent(KeyEvent ev) {
		int key = ev.getKeyCode();
		if(!mShiftPressing){
			if(key == KeyEvent.KEYCODE_SHIFT_LEFT || key == KeyEvent.KEYCODE_SHIFT_RIGHT){
				mHardShift = 0;
				mShiftPressing = true;
				if(mShiftOnCapsLock) {
					mHardShift = 2;
					mShiftPressing = true;
					mShiftOnCapsLock = false;
				}
				updateMetaKeyStateDisplay();
				updateNumKeyboardShiftState();
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
		int type = mHangulEngine.getLastInputType();
		switch(mCurrentEngineMode) {
		/* case ENGINE_MODE_SEBUL_SHIN_M:
		case ENGINE_MODE_SEBUL_SHIN_P2:
		case ENGINE_MODE_SEBUL_3_2015M:
		case ENGINE_MODE_SEBUL_3_2015:
		case ENGINE_MODE_SEBUL_3_P3:
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
		*/
		case ENGINE_MODE_SEBUL_SHIN_ORIGINAL:
		case ENGINE_MODE_SEBUL_SHIN_EDIT:
		case ENGINE_MODE_SEBUL_SHIN_M:
		case ENGINE_MODE_SEBUL_SHIN_P2:
		case ENGINE_MODE_SEBUL_3_2015M:
		case ENGINE_MODE_SEBUL_3_2015:
		case ENGINE_MODE_SEBUL_3_P3:
			DefaultSoftKeyboardKOKR kokr = (DefaultSoftKeyboardKOKR) mInputViewManager;
			if(type == HangulEngine.INPUT_CHO3) {
				kokr.changeKeyMode(DefaultSoftKeyboardKOKR.KEYMODE_HANGUL_CHO);
			} else if(type == HangulEngine.INPUT_JUNG3) {
				kokr.changeKeyMode(DefaultSoftKeyboardKOKR.KEYMODE_HANGUL_JUNG);
			} else if(type == HangulEngine.INPUT_JONG3) {
				kokr.changeKeyMode(DefaultSoftKeyboardKOKR.KEYMODE_HANGUL_JONG);
			} else {
				kokr.changeKeyMode(DefaultSoftKeyboardKOKR.KEYMODE_HANGUL);
			}
			break;

		default:
		}
		updateMetaKeyStateDisplay();
		updateNumKeyboardShiftState();
	}

	protected int getShiftKeyState(EditorInfo editor) {
		return (getCurrentInputConnection().getCursorCapsMode(editor.inputType) == 0) ? 0 : 1;
	}

	public void updateMetaKeyStateDisplay() {
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

	private void updateNumKeyboardShiftState() {
		if(!(mInputViewManager instanceof DefaultSoftKeyboardKOKR)) return;
		DefaultSoftKeyboardKOKR softKeyboard = (DefaultSoftKeyboardKOKR) mInputViewManager;
		if(softKeyboard.mHardKeyboardHidden) return;
		softKeyboard.setShiftState(mHardShift);
		softKeyboard.setCapsLock(mHardShift == 2);
	}

	@Override
	public void hideWindow() {
		
		mInputViewManager.closing();
		
		super.hideWindow();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(mTimeOutHandler == null) {
			mTimeOutHandler = new Handler();
			mTimeOutHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					onEvent(new OpenWnnEvent(OpenWnnKOKR.TIMEOUT_EVENT));
					mTimeOutHandler = null;
				}
			}, mMoachigiDelay);
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onEvaluateFullscreenMode() {
		return false;
	}

	@Override
	public boolean onEvaluateInputViewShown() {
		return true;
	}

	public void resetHardShift(boolean force) {
		if(mHardShift == 2 && !force) return;
		mHardShift = 0;
		mShiftPressing = false;
	}

}
