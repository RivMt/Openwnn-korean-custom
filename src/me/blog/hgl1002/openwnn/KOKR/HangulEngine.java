package me.blog.hgl1002.openwnn.KOKR;

import me.blog.hgl1002.openwnn.OpenWnn;
import me.blog.hgl1002.openwnn.OpenWnnEvent;

public class HangulEngine {
	
	OpenWnn mWnn;
	
	String composing;
	int cho, jung, jong;
	int lastInputType = 0;
	int lastJamo = -1, firstJong = -1;

	public static final int INPUT_NON_HANGUL = 0;
	public static final int INPUT_CHO = 1;
	public static final int INPUT_JUNG = 2;
	public static final int INPUT_JONG = 3;
	public static final int INPUT_JUNG_JOHAB = 4;
	
	public static final int BASE_CODE = 0xac00;
	
	int[][] jamoTable;
	
	int[][] combinationTable;
	
	char[] choTable = {
			'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 
			'ㅃ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ',
			'ㅌ', 'ㅍ', 'ㅎ',
	};
	char[] jungTable = {
			'ㅏ', 'ㅐ', 'ㅑ', 'ㅒ', 'ㅓ', 'ㅔ', 'ㅕ', 'ㅖ', 
			'ㅗ', 'ㅘ', 'ㅙ', 'ㅚ', 'ㅛ', 'ㅜ', 'ㅝ', 'ㅞ', 
			'ㅟ', 'ㅠ', 'ㅡ', 'ㅢ', 'ㅣ', 
	};
	char[] jongTable = {
			' ',
			'ㄱ', 'ㄲ', 'ㄳ', 'ㄴ', 'ㄵ', 'ㄶ', 'ㄷ', 'ㄹ', 
			'ㄺ', 'ㄻ', 'ㄼ', 'ㄽ', 'ㄾ', 'ㄿ', 'ㅀ', 'ㅁ',  
			'ㅂ', 'ㅄ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅊ', 'ㅋ',  
			'ㅌ', 'ㅍ', 'ㅎ', 
	};
	
	int[][] dubulCho = {
			{0x3131, 0x1100},
			{0x3132, 0x1101},
			{0x3134, 0x1102},
			{0x3137, 0x1103},
			{0x3138, 0x1104},
			{0x3139, 0x1105},
			{0x3141, 0x1106},
			{0x3142, 0x1107},
			{0x3143, 0x1108},
			{0x3145, 0x1109},
			{0x3146, 0x110a},
			{0x3147, 0x110b},
			{0x3148, 0x110c},
			{0x3149, 0x110d},
			{0x314a, 0x110e},
			{0x314b, 0x110f},
			{0x314c, 0x1110},
			{0x314d, 0x1111},
			{0x314e, 0x1112},
	};
	
	int[][] dubulJung = {
			{0x314f, 0x1161},
			{0x3150, 0x1162},
			{0x3151, 0x1163},
			{0x3152, 0x1164},
			{0x3153, 0x1165},
			{0x3154, 0x1166},
			{0x3155, 0x1167},
			{0x3156, 0x1168},
			{0x3157, 0x1169},
			{0x3158, 0x116a},
			{0x3159, 0x116b},
			{0x315a, 0x116c},
			{0x315b, 0x116d},
			{0x315c, 0x116e},
			{0x315d, 0x116f},
			{0x315e, 0x1170},
			{0x315f, 0x1171},
			{0x3160, 0x1172},
			{0x3161, 0x1173},
			{0x3162, 0x1174},
			{0x3163, 0x1175},
	};

	int[][] dubulJong = {
			{0x3131, 0x11a8},
			{0x3132, 0x11a9},
			{0x3133, 0x11aa},
			{0x3134, 0x11ab},
			{0x3135, 0x11ac},
			{0x3136, 0x11ad},
			{0x3137, 0x11ae},
			{0x3139, 0x11af},		// ㄹ
			{0x313a, 0x11b0},
			{0x313b, 0x11b1},
			{0x313c, 0x11b2},
			{0x313d, 0x11b3},
			{0x313e, 0x11b4},
			{0x313f, 0x11b5},
			{0x3140, 0x11b6},
			{0x3141, 0x11b7},		// ㅁ
			{0x3142, 0x11b8},
			{0x3144, 0x11b9},
			{0x3145, 0x11ba},
			{0x3146, 0x11bb},
			{0x3147, 0x11bc},
			{0x3148, 0x11bd},
			{0x314a, 0x11be},
			{0x314b, 0x11bf},
			{0x314c, 0x11c0},
			{0x314d, 0x11c1},
			{0x314e, 0x11c2},
	};
	
	public HangulEngine(OpenWnn parent) {
		this.mWnn = parent;
		resetJohab();
	}
	
	public boolean backspace() {
		if(jong != -1) {
			jong = -1;
			lastInputType = INPUT_JUNG;
		} else if(jung != -1) {
			jung = -1;
			lastInputType = INPUT_CHO;
		} else if(cho != -1) {
			cho = -1;
			lastInputType = INPUT_NON_HANGUL;
		} else {
			return false;
		}
		lastJamo = -1;
		
		processVisible();
		return true;
	}

	public int inputCode(int code, int shift) {
		for(int[] item : jamoTable) {
			if(item[0] == code) {
				return (shift == 0) ? item[1] : item[2];
			}
		}
		return -1;
	}
	
	public void resetCJJ() {
		cho = -1;
		jung = -1;
		jong = -1;
	}
	
	public void resetJohab() {
		resetCJJ();
		mWnn.onEvent(new OpenWnnEvent(OpenWnnEvent.COMMIT_COMPOSING_TEXT));
		composing = "";
		lastJamo = -1;
		lastInputType = INPUT_NON_HANGUL;
	}

	public boolean inputJamo(int jamo) {
		if(!((jamo >= 0x1100 && jamo <= 0x11ff)
				|| (jamo >= 0x3131 && jamo <= 0x3163))
				&& jamo != -5000 && jamo != -5001) {
			return false;
		}
		
		boolean overwrite = false;
		
		if(jamo >= 0x1100 && jamo <= 0x11ff || jamo == -5000 || jamo == -5001) {
			if(jamo >= 0x1100 && jamo <= 0x1112) {
				if(cho != -1) {
					if(lastInputType != INPUT_CHO) resetJohab();
					int combination = getCombination(cho+0x1100, jamo);
					if(combination == -1) {
						if(!overwrite) resetJohab();
						cho = jamo - 0x1100;
					}
					else cho = combination - 0x1100;
				}
				else {
					//resetJohab();
					cho = jamo - 0x1100;
				}
				lastInputType = INPUT_CHO;
			} else if(jamo >= 0x1161 && jamo <= 0x1175) {
				if(jung != -1) {
					int combination = getCombination(jung+0x1161, jamo);
					if(combination == -1) {
						resetJohab();
						jung = jamo - 0x1161;
					}
					else jung = combination - 0x1161;
				}
				else jung = jamo - 0x1161;
				lastInputType = INPUT_JUNG;
			} else if(jamo == -5000 || jamo == -5001) {
				if(jamo == -5000) jamo = 0x1169;
				if(jamo == -5001) jamo = 0x116e;
				if(jung != -1) {
					int combination = getCombination(jung+0x1161, jamo);
					if(combination == -1) {
						resetJohab();
						jung = jamo - 0x1161;
					}
					else jung = combination - 0x1161;
				}
				else jung = jamo - 0x1161;
				lastInputType = INPUT_JUNG_JOHAB;
			} else if(jamo >= 0x11a8 && jamo <= 0x11c2) {
				if(jong != -1) {
					int combination = getCombination(jong-1+0x11a8, jamo);
					if(combination == -1) {
						resetJohab();
						jong = jamo - 0x11a8 + 1;
					}
					else jong = combination - 0x11a8 + 1;
				}
				else jong = jamo - 0x11a8 + 1;
				lastInputType = INPUT_JONG;
			} else {
				return false;
			}
		}
		// Dubul Automata
		else if(jamo >= 0x3131 && jamo <= 0x3163) {
			if(jamo >= 0x3131 && jamo <= 0x314e) {
				boolean combineOrNew = false;
				// Add jong to existing syllable
				if(cho != -1 && jung != -1 && jong == -1) {
					int addJong = getDubul(dubulJong, jamo);
					if(addJong != -1) jong = addJong - 0x11a8 + 1;
					else combineOrNew = true;
				} else {
					combineOrNew = true;
				}
				if(combineOrNew) {
					boolean combine = false;
					if(lastJamo >= 0x3131 && lastJamo <= 0x314e) combine = true;
					if(jung == -1) combine = false;
					if(combine) {
						int combination = getCombination(lastJamo, jamo);
						if(combination != -1) {
							firstJong = jong;
							jong = getDubul(dubulJong, combination) - 0x11a8 + 1;
						} else {
							combine = false;
						}
					}
					// Start new syllable by adding cho
					if(!combine) {
						int addCho = getDubul(dubulCho, jamo);
						int combination = getCombination(lastJamo, jamo);
						if(combination != -1) {
							cho = getDubul(dubulCho, combination) - 0x1100;
						} else if(addCho != -1) {
							processVisible();
							resetJohab();
							cho = addCho - 0x1100;
						}
					}
				}
			} else if(jamo >= 0x314f && jamo <= 0x3163) {
				// De-attach jong from syllable and start new syllable with new jung
				if(cho != -1 && jung != -1 && jong != -1) {
					int convertedCho = getDubul(dubulCho, lastJamo);
					if(firstJong != -1) jong = firstJong;
					else jong = -1;
					processVisible();
					mWnn.getCurrentInputConnection().setComposingText(composing, 1);
					resetJohab();
					cho = convertedCho - 0x1100;
					jung = getDubul(dubulJung, jamo) - 0x1161;
				}
				// Stick to existing cho
				else {
					int combination = jamo;
					if(jung != -1) {
						combination = getCombination(getDubul(dubulJung, jung + 0x1161, true), jamo);
						if(combination == -1) {
							resetJohab();
							mWnn.getCurrentInputConnection().setComposingText(composing, 1);
							combination = jamo;
						}
					}
					jung = getDubul(dubulJung, combination) - 0x1161;
				}
				firstJong = -1;
			}
		}
		
		processVisible();
		
		lastJamo = jamo;
		return true;
	}

	public void processVisible() {
		if(cho != -1 && jung != -1 && jong != -1) {
			composing = String.valueOf(combineHangul(cho, jung, jong-1));
		}
		else if(cho != -1 && jung != -1) {
			composing = String.valueOf(combineHangul(cho, jung));
		}
		else if(cho != -1) {
			composing = String.valueOf(choTable[cho]);
		}
		else if(jung != -1) {
			composing = String.valueOf(jungTable[jung]);
		}
		else if(jong != -1) {
			composing = String.valueOf(jongTable[jong]);
		}
		else {
			composing = "";
		}
	}
	
	public int getCombination(int a, int b) {
		for(int[] item : combinationTable) {
			if(item[0] == a && item[1] == b) {
				return item[2];
			}
		}
		return -1;
	}
	
	public int getDubul(int[][] table, int jamo, boolean revert) {
		for(int[] item : table) {
			if(!revert) {
				if(item[0] == jamo) {
					return item[1];
				}				
			}
			else {
				if(item[1] == jamo) {
					return item[0];
				}
			}
		}
		return -1;
	}
	
	public int getDubul(int[][] table, int jamo) {
		return this.getDubul(table, jamo, false);
	}
	
	protected char combineHangul(int cho, int jung, int jong) {
		return (char) (BASE_CODE + (cho * 588) + (jung * 28) + jong+1);
	}
	
	protected char combineHangul(int cho, int jung) {
		return (char) (BASE_CODE + (cho * 588) + (jung * 28));
	}

	public int[][] getJamoTable() {
		return jamoTable;
	}

	public void setJamoTable(int[][] jamoTable) {
		this.jamoTable = jamoTable;
	}

	public int[][] getCombinationTable() {
		return combinationTable;
	}

	public void setCombinationTable(int[][] combinationTable) {
		this.combinationTable = combinationTable;
	}

	public String getComposing() {
		return composing;
	}

	public void setComposing(String composing) {
		this.composing = composing;
	}

	public int getLastInputType() {
		return lastInputType;
	}

}
