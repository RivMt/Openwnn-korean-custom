package me.blog.hgl1002.openwnn.KOKR;

import java.util.EmptyStackException;
import java.util.Stack;

public class HangulEngine {
	
	public static final int INPUT_NON_HANGUL = 0x0000;
	
	public static final int INPUT_CHO3 = 0x1031;
	public static final int INPUT_JUNG3 = 0x1032;
	public static final int INPUT_JONG3 = 0x1033;

	public static final int INPUT_CHO2 = 0x1021;
	public static final int INPUT_JUNG2 = 0x1022;
	public static final int INPUT_JONG2 = 0x1023;

	public static final int VIRTUAL_DOUBLE_JUNG_O = -5000;
	public static final int VIRTUAL_DOUBLE_JUNG_U = -5001;
	public static final int VIRTUAL_DOUBLE_JUNG_I = -5002;
	public static final int VIRTUAL_JUNG_CHEON = -2003;
	public static final int VIRTUAL_JUNG_DOUBLE_CHEON = -2004;
	
	public static int[] CHO_TABLE = {
			'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ',
			'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ',
			'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ',
			'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ',
	};
	public static int[] JUNG_TABLE = {
			'ㅏ', 'ㅐ', 'ㅑ', 'ㅒ', 'ㅓ',
			'ㅔ', 'ㅕ', 'ㅖ', 'ㅗ', 'ㅘ',
			'ㅙ', 'ㅚ', 'ㅛ', 'ㅜ', 'ㅝ',
			'ㅞ', 'ㅟ', 'ㅠ', 'ㅡ', 'ㅢ',
			'ㅣ', 'ㆍ', 'ㆎ', 'ᆢ',
	};
	public static int[] JONG_TABLE = {
			' ', 'ㄱ', 'ㄲ', 'ㄳ', 'ㄴ',
			'ㄵ', 'ㄶ', 'ㄷ', 'ㄹ', 'ㄺ',
			'ㄻ', 'ㄼ', 'ㄽ', 'ㄾ', 'ㄿ',
			'ㅀ', 'ㅁ', 'ㅂ', 'ㅄ', 'ㅅ',
			'ㅆ', 'ㅇ', 'ㅈ', 'ㅊ', 'ㅋ',
			'ㅌ', 'ㅍ', 'ㅎ'
	};
	
	public static int[] CHO_CONVERT = {
			0x1100, 0x1101, 0x0000, 0x1102, 0x0000,
			0x0000, 0x1103, 0x1104, 0x1105, 0x0000,
			0x0000, 0x0000, 0x0000, 0x0000, 0x0000,
			0x0000, 0x1106, 0x1107, 0x1108, 0x0000,
			0x1109, 0x110a, 0x110b, 0x110c, 0x110d,
			0x110e, 0x110f, 0x1110, 0x1111, 0x1112,
	};
	
	public static int[] JONG_CONVERT = {
			0x11a8, 0x11a9, 0x11aa, 0x11ab, 0x11ac,
			0x11ad, 0x11ae, 0x0000, 0x11af, 0x11b0,
			0x11b1, 0x11b2, 0x11b3, 0x11b4, 0x11b5,
			0x11b6, 0x11b7, 0x11b8, 0x0000, 0x11b9,
			0x11ba, 0x11bb, 0x11bc, 0x11bd, 0x0000,
			0x11be, 0x11bf, 0x11c0, 0x11c1, 0x11c2,
	};
	
	boolean moachigi, firstMidEnd;
	HangulEngineListener listener;
	
	int cho, jung, jong;
	int last;
	String composing;
	
	int lastInputType;
	
	private static class History {
		int cho, jung, jong;
		int last;
		String composing;
		int lastInputType;
		public History(int cho, int jung, int jong, int last, String composing, int lastInputType) {
			super();
			this.cho = cho;
			this.jung = jung;
			this.jong = jong;
			this.last = last;
			this.composing = composing;
			this.lastInputType = lastInputType;
		}
	}
	
	Stack<History> histories = new Stack<History>();
	
	int[][] jamoTable;
	int[][] combinationTable;
	
	public HangulEngine() {
		resetJohab();
//		this.firstMidEnd = true;
	}
	
	public HangulEngine(boolean moachigi) {
		this();
		this.moachigi = moachigi;
	}
	
	public boolean backspace() {
		try {
			History history = histories.pop();
			this.cho = history.cho;
			this.jung = history.jung;
			this.jong = history.jong;
			this.last = history.last;
			this.composing = history.composing;
			this.lastInputType = history.lastInputType;
			
		} catch(EmptyStackException e) {
			if(composing == "") {
				return false;
			}
			else composing = "";
		}
		if(listener != null) listener.onEvent(new SetComposingEvent(composing));
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
	
	public int inputJamo(int code) {
		
		if(composing == "") histories.clear();
		else histories.push(new History(cho, jung, jong, last, composing, lastInputType));

		boolean virtual = false;
		if(code <= -4000) virtual = true;
		int result;
		int combination;
		if(code >= 0x1100 && code <= 0x115e || code <= -1000 && code > -2000 || code <= -4000 && code > -5000) {
			// 마이너스 가상 낱자이면 코드를 그대로 넘긴다.
			int choCode = code;
			if(choCode >= 0x1100) choCode-= 0x1100;
			if(!moachigi && !isCho(last) && !isJung(last)) resetJohab();
			if(!moachigi && !isCho(last)) resetJohab();
			if(this.cho != -1) {
				// 마이너스 가상 낱자이면 코드를 그대로 넘긴다.
				int source = this.cho;
				if(source >= 0) source += 0x1100;
				if((combination = getCombination(source, code)) != -1) {
					if(!isCho(last)) {
						resetJohab();
						this.cho = choCode;
					} else {
						// 마이너스 가상 낱자이면 코드를 그대로 넘긴다.
						choCode = combination;
						if(choCode >= 0) choCode-= 0x1100;
						this.cho = choCode;
					}
				} else {
					resetJohab();
					this.cho = choCode;
				}
			} else {
				this.cho = choCode;
			}
			result = INPUT_CHO3;
			last = code;
		} else if(code >= 0x1161 && code <= 0x11a7 || code <= -2000 && code > -3000 || code <= -5000 && code > -6000) {
			// 마이너스 가상 낱자이면 코드를 그대로 넘긴다.
			int jungCode = code;
			if(jungCode >= 0x1161) jungCode -= 0x1161;
			if(!moachigi && !isCho(last) && !isJung(last)) resetJohab();
			if(this.jung != -1) {
				// 마이너스 가상 낱자이면 코드를 그대로 넘긴다.
				int source = this.jung;
				if(source >= 0) source += 0x1161;
				if((combination = getCombination(source, code)) != -1) {
					jungCode = combination;
					if(jungCode >= 0x1161) jungCode -= 0x1161;
					this.jung = jungCode;
				} else {
					resetJohab();
					this.jung = jungCode;
				}
			} else {
				this.jung = jungCode;
			}
			if(lastInputType == 0) result = INPUT_JUNG3;
			else if(virtual) result = lastInputType;
			else result = INPUT_JUNG3;
			last = code;
		} else if(code >= 0x11a8 && code <= 0x11ff|| code <= -3000 && code > -4000 || code <= -6000 && code > -7000) {
			int jongCode = code;
			if(jongCode >= 0x11a8) jongCode -= 0x11a7;
			if(!moachigi && !isJung(last) && !isJong(last)) resetJohab();
			if(this.jong != -1) {
				int source = this.jong;
				if(source >= 0) source += 0x11a7;
				if((combination = getCombination(source, code)) != -1) {
					jongCode = combination;
					if(jongCode >= 0x11a8) jongCode -= 0x11a7;
					this.jong = jongCode;
				} else {
					resetJohab();
					this.jong = jongCode;
				}
			} else {
				this.jong = jongCode;
			}
			result = INPUT_JONG3;
			last = code;
		}
		else if(code >= 0x3131 && code <= 0x314e) {
			if(this.cho != -1 && this.jung != -1) {
				int jongCode = JONG_CONVERT[code - 0x3131] - 0x11a7;
				if(isJong(last) && this.jong != -1) {
					if((combination = getCombination(this.jong+0x11a7, jongCode+0x11a7)) != -1) {
						this.jong = combination - 0x11a7;
						last = jongCode + 0x11a7;
					} else {
						resetJohab();
						this.cho = CHO_CONVERT[code - 0x3131] - 0x1100;
						last = CHO_CONVERT[code - 0x3131];
					}
				} else {
					if(jongCode <= 0) {
						resetJohab();
						this.cho = CHO_CONVERT[code - 0x3131] - 0x1100;
						last = CHO_CONVERT[code - 0x3131];
					} else {
						this.jong = jongCode;
						last = jongCode + 0x11a7;						
					}
				}
			} else {
				int choCode = CHO_CONVERT[code - 0x3131] - 0x1100;
				if(isCho(last) && this.cho != -1) {
					if((combination = getCombination(this.cho+0x1100, choCode+0x1100)) != -1) {
						this.cho = combination - 0x1100;
					} else {
						resetJohab();
						this.cho = choCode;
					}
				} else {
					resetJohab();
					this.cho = choCode;
				}
				last = choCode + 0x1100;
			}
			result = INPUT_CHO2;
		} else if(code >= 0x314f && code <= 0x3163) {
			if(this.jong < 0) {
				int jungCode = code - 0x314f;
				if(isJung(last) && this.jung != -1) {
					if((combination = getCombination(this.jung+0x1161, jungCode+0x1161)) != -1) {
						this.jung = combination - 0x1161;
					} else {
						resetJohab();
						this.jung = jungCode;
					}
				} else {
					this.jung = jungCode;
				}
				last = jungCode + 0x1161;
			} else {
				int jungCode = code - 0x314f;
				if(this.jong != -1 && this.cho != -1) {
					Disassembled dis;
					if((dis = getDisassembled(this.jong + 0x11a7)) != null) {
						this.jong = dis.jong - 0x11a7;
						this.composing = getVisible(this.cho, this.jung, this.jong);
						if(listener != null) listener.onEvent(new SetComposingEvent(composing));
						resetJohab();
						this.cho = dis.cho - 0x1100;
						composing = getVisible(this.cho, this.jung, this.jong);
						histories.push(new History(cho, jung, jong, last, composing, lastInputType));
						this.jung = jungCode;
					} else {
						int convertedCho;
						if((convertedCho = convertToCho(this.jong+0x11a7)) >-1) {
							this.jong = -1;
							this.composing = getVisible(this.cho, this.jung, this.jong);
							if(listener != null) listener.onEvent(new SetComposingEvent(composing));
							resetJohab();
							this.cho = convertedCho - 0x1100;
							composing = getVisible(this.cho, this.jung, this.jong);
							histories.push(new History(cho, jung, jong, last, composing, lastInputType));
							this.jung = jungCode;
						}
					}
				} else {
					resetJohab();
					this.jung = jungCode;
				}
				last = jungCode + 0x1161;
			}
			result = INPUT_JUNG2;
		}
		else {
			resetJohab();
			result = 0;
			last = code;
			return result;
		}
		
		this.composing = getVisible(this.cho, this.jung, this.jong);
		if(listener != null) listener.onEvent(new SetComposingEvent(composing));
		
		lastInputType = result;
		
		return result;
	}
	
	public void resetJohab() {
		if(listener != null) listener.onEvent(new FinishComposingEvent());
		cho = jung = jong = -1;
		composing = "";
		lastInputType = 0;
		histories.clear();
	}
	
	int getCombination(int a, int b) {
		for(int[] item : combinationTable) {
			if(item[0] == a && item[1] == b) return item[2];
		}
		return -1;
	}
	
	Disassembled getDisassembled(int jong) {
		for(int[] item : combinationTable) {
			if(item[2] == jong) {
				int resultJong = item[0];
				int resultCho = 0;
				for(int i = 0 ; i < JONG_CONVERT.length ; i++) {
					if(JONG_CONVERT[i] == item[1]) resultCho = CHO_CONVERT[i];
				}
				return new Disassembled(resultJong, resultCho);
			}
		}
		return null;
	}
	
	private static class Disassembled {
		int jong, cho;
		public Disassembled(int jong, int cho) {
			this.jong = jong;
			this.cho = cho;
		}
	}
	
	int convertToCho(int jong) {
		for(int i = 0 ; i < JONG_CONVERT.length ; i++) {
			if(JONG_CONVERT[i] == jong) return CHO_CONVERT[i];
		}
		return -1;
	}
	
	int combineHangul(int cho, int jung, int jong) {
		return 0xac00 + (cho * 588) + (jung * 28) + jong;
	}
	
	String getVisible(int cho, int jung, int jong) {
		String visible;
		if(jung == -5000) jung = 0x1169 - 0x1161;
		if(jung == -5001) jung = 0x116e - 0x1161;
		if(jung == -5002) jung = 0x1173 - 0x1161;
		if(jung == -5010) jung = 0x119e - 0x1161;
		if(cho > 0x12 || jung > 0x14 || jong > 0x1a) {
			if(cho == -1) cho = 0x5f;
			visible = new String(new char[] {(char) (cho + 0x1100)})
					+ new String(new char[] {(char) (jung + 0x1161)});
			if(jong != -1) visible += new String(new char[] {(char) (jong + 0x11a8 - 1)});
		} else if(cho != -1 && jung != -1 && jong != -1) {
			visible = String.valueOf((char) combineHangul(cho, jung, jong));
		} else if(cho != -1 && jung != -1) {
			visible = String.valueOf((char) combineHangul(cho, jung, 0));
		} else if(firstMidEnd) {
			if(cho != -1 && jung == -1 && jong == -1) {
				visible = String.valueOf((char) CHO_TABLE[cho]);
			} else if(cho == -1 && jung != -1 && jong == -1) {
				visible = String.valueOf((char) JUNG_TABLE[jung]);
			} else if(cho == -1 && jung == -1 && jong != -1) {
				visible = String.valueOf((char) JONG_TABLE[jong]);
			} else {
				if (cho == -1) cho = 0x5f;
				visible = new String(new char[]{(char) (cho + 0x1100)})
						+ new String(new char[]{(char) (jung + 0x1161)});
				if (jong != -1) visible += new String(new char[]{(char) (jong + 0x11a8 - 1)});
			}
		} else {
			if (cho != -1) {
				visible = String.valueOf((char) CHO_TABLE[cho]);
			} else if (jung != -1) {
				visible = String.valueOf((char) JUNG_TABLE[jung]);
			} else if (jong != -1) {
				visible = String.valueOf((char) JONG_TABLE[jong]);
			} else {
				visible = "";
			}
		}
		return visible;
	}
	
	public boolean isCho(int code) {
		return code >= 0x1100 && code <= 0x115e || code <= -1000 && code > -2000 || code <= -4000 && code > -5000;
	}
	
	public boolean isJung(int code) {
		return code >= 0x1161 && code <= 0x11a7 || code <= -2000 && code > -3000 || code <= -5000 && code > -6000;
	}
	
	public boolean isJong(int code) {
		return code >= 0x11a8 && code <= 0x11ff|| code <= -3000 && code > -4000 || code <= -6000 && code > -7000;
	}
	
	public boolean isMoachigi() {
		return moachigi;
	}

	public void setMoachigi(boolean moachigi) {
		this.moachigi = moachigi;
	}

	public boolean isFirstMidEnd() {
		return firstMidEnd;
	}

	public void setFirstMidEnd(boolean firstMidEnd) {
		this.firstMidEnd = firstMidEnd;
	}

	public HangulEngineListener getListener() {
		return listener;
	}

	public void setListener(HangulEngineListener listener) {
		this.listener = listener;
	}

	public String getComposing() {
		return composing;
	}

	public void setComposing(String composing) {
		this.composing = composing;
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

	public void setCombinationTable(int[][] combinations) {
		this.combinationTable = combinations;
	}

	public static interface HangulEngineListener {
		public void onEvent(HangulEngineEvent event);
	}
	
	public static abstract class HangulEngineEvent {
	}
	
	public static class FinishComposingEvent extends HangulEngineEvent {
	}
	
	public static class SetComposingEvent extends HangulEngineEvent {
		String composing;
		public SetComposingEvent(String composing) {
			this.composing = composing;
		}
		public String getComposing() {
			return composing;
		}
	}

	public int getLastInputType() {
		return lastInputType;
	}
	
}
