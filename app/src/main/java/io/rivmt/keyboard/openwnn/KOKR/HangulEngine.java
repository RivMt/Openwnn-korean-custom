package io.rivmt.keyboard.openwnn.KOKR;

import java.util.EmptyStackException;
import java.util.Stack;

/**
 * 한글 엔진 클래스. (버전 1, OpenWnn)
 * 한글 낱자를 받아 조합해서 출력하는 역할을 담당한다.
 */
public class HangulEngine {
	
	public static final int INPUT_NON_HANGUL = 0x0000;
	
	public static final int INPUT_CHO3 = 0x1011;
	public static final int INPUT_JUNG3 = 0x1012;
	public static final int INPUT_JONG3 = 0x1013;

	public static final int INPUT_CHO2 = 0x1021;
	public static final int INPUT_JUNG2 = 0x1022;
	public static final int INPUT_JONG2 = 0x1023;

	public static final int VIRTUAL_NON_HANGUL = 0;
	public static final int VIRTUAL_CHO = 1;
	public static final int VIRTUAL_JUNG = 2;
	public static final int VIRTUAL_JONG = 3;

	// 유니코드 낱자를 표시하기 위한 테이블.
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

	// 호환용 한글 자모를 표준 한글 자모로 변환하기 위한 테이블.
	public static int[] CHO_CONVERT = {
					0x1100, 0x1101, 0x0000, 0x1102, 0x0000, 0x115d, 0x1103,		// 0x3130 (0x115d: traditional)
			0x1104, 0x1105, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000,		// 0x3138
			0x111a, 0x1106, 0x1107, 0x1108, 0x0000, 0x1109, 0x110a, 0x110b,		// 0x3140 (0x111a: traditional)
			0x110c, 0x110d, 0x110e, 0x110f, 0x1110, 0x1111, 0x1112, 0x0000,		// 0x3148
			0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000,		// 0x3150
			0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000,		// 0x3158
			0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x1114, 0x1115, 0x0000,		// 0x3160
			0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x111c, 0x0000,		// 0x3168
			0x0000, 0x111d, 0x111e, 0x1120, 0x1122, 0x1123, 0x1127, 0x1129,		// 0x3170
			0x112b, 0x112c, 0x112d, 0x112e, 0x112f, 0x1132, 0x1136, 0x1140,		// 0x3178
			0x1147, 0x114c, 0x0000, 0x0000, 0x1157, 0x1158, 0x1159,				// 0x3180
	};

	public static int[] JONG_CONVERT = {
					0x11a8, 0x11a9, 0x11aa, 0x11ab, 0x11ac, 0x11ad, 0x11ae,		// 0x3130
			0x0000, 0x11af, 0x11b0, 0x11b1, 0x11b2, 0x11b3, 0x11b4, 0x11b5,		// 0x3138
			0x11b6, 0x11b7, 0x11b8, 0x0000, 0x11b9, 0x11ba, 0x11bb, 0x11bc,		// 0x3140
			0x11bd, 0x0000, 0x11be, 0x11bf, 0x11c0, 0x11c1, 0x11c2, 0x0000,		// 0x3148
			0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000,		// 0x3150
			0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000,		// 0x3158
			0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x11c6, 0x11c7,		// 0x3160
			0x11c8, 0x11cc, 0x11ce, 0x11d3, 0x11d7, 0x11d9, 0x11dc, 0x11dd,		// 0x3168
			0x11df, 0x11e2, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000,		// 0x3170
			0x11e6, 0x0000, 0x11e7, 0x0000, 0x11e8, 0x11ea, 0x0000, 0x11eb,		// 0x3178
			0x0000, 0x11f0, 0x11f1, 0x11f2, 0x11f4, 0x0000, 0x11f9				// 0x3180
	};

	// 호환용 옛한글 자모를 표준 옛한글 자모로 변환하기 위한 테이블.
	public static int[] TRAD_JUNG_CONVERT = {
			0x1184, 0x1185, 0x1188, 0x1191, 0x1192, 0x1194, 0x119e, 0x11a1		// 0x3187
	};

	/**
	 * 모아치기 기능을 사용할지의 여부.
	 * {@code true}일 경우 세벌식 자모가 입력되면 초, 중, 종성 순서에 상관없이 음절 조합을 실행한다.
	 */
	boolean moachigi;
	/**
	 * 한 번에 모아치기 기능을 사용할지의 여부.
	 * {@code true}일 경우 초, 중, 종성 순서에 상관없이 한글 낱자 조합, 음절 조합을 실행한다.
	 */
	boolean fullMoachigi;
	/**
	 * 첫가끝 조합을 사용할지의 여부.
	 * {@code true}일 경우 초, 종성, 중, 종성만 있는 경우 등에 유니코드 첫가끝 조합을 한다.
	 * {@code false}일 경우 초성 - 중성 - 종성의 우선순위로 먼저 표시된다.
	 * 이 값에 상관없이 옛한글 자모가 있을 경우에는 무조건 첫가끝 조합을 실행한다.
	 */
	boolean firstMidEnd;

	HangulEngineListener listener;

	/**
	 * 현재 조합 중인 한글 성분. 초, 중은 -1, 종은 0이면 입력되지 않은 상태.
	 */
	int cho, jung, jong;
	/**
	 * 최근 입력된 한글 자모.
	 */
	int last;
	/**
	 * 이전까지 조합 중이던 한글 종성. (도깨비불 발생시 필요)
	 */
	 int beforeJong;
	/**
	 * 화면에 표시되는 조합 중인 글자.
	 */
	String composing = "";

	/**
	 * 최근에 입력된 글자의 성분 정보. (예: 세벌식 초성 / 두벌식 중성 / 비한글 문자 등)
	 */
	int lastInputType;

	/**
	 * History
	 * 한글 입력 기록을 저장하기 위한 객체의 클래스.
	 */
	private static class History {
		int cho, jung, jong;
		int last;
		int beforeJong;
		String composing;
		int lastInputType;
		public History(int cho, int jung, int jong, int last, int beforeJong, String composing, int lastInputType) {
			super();
			this.cho = cho;
			this.jung = jung;
			this.jong = jong;
			this.last = last;
			this.beforeJong = beforeJong;
			this.composing = composing;
			this.lastInputType = lastInputType;
		}
	}

	/**
	 * 한글 입력 기록을 저장하기 위한 스택. 백스페이스 처리에 이용된다.
	 */
	Stack<History> histories = new Stack<>();

	/**
	 * 자모 테이블.
	 * 어떤 글자에 어떤 한글 자모/비한글 문자가 대응하는지 정의한다.
	 * {@link #jamoSet}이 지정되었을 경우 {@code null}.
	 */
	int[][] jamoTable;
	/**
	 * 자모 세트 테이블.
	 * 신세벌식 자판 등에서 각 입력 상태마다 다른 자모 테이블을 두려면 사용한다.
	 * {@link #jamoTable}이 지정되었을 경우 {@code null}.
	 */
	int[][][] jamoSet;
	/**
	 * {@link #jamoSet}을 사용할 경우 현재 사용하고 있는 자모 테이블을 표시한다.
	 */
	int[][] currentJamoTable;
	/**
	 * 낱자 조합 테이블.
	 * 한글 낱자를 어떻게 조합할지 정의한다. (예: 0x1100 + 0x1100 = 0x1101, ㄱ+ㄱ=ㄲ)
	 */
    private int[][] combinationTable;

	public HangulEngine() {
		// 한글 조합 상태를 초기화한다.
		resetComposition();
	}
	
	public HangulEngine(boolean moachigi) {
		this();
		this.moachigi = moachigi;
	}
	
	public boolean backspace() {
		try {
			// 입력 기록을 하나 빼 온다.
			History history = histories.pop();
			// 현재 상태에 적용한다.
			this.cho = history.cho;
			this.jung = history.jung;
			this.jong = history.jong;
			this.last = history.last;
			this.beforeJong = history.beforeJong;
			this.composing = history.composing;
			this.lastInputType = history.lastInputType;
			
		} catch(EmptyStackException e) {
			// 스택이 비었을 경우 (입력된 낱자가 없을 경우)
			lastInputType = 0;
			if(composing == "") {
				// 일반적인 백스페이스로 동작.
				return false;
			}
			// 마지막 하나가 남았을 걥우 비운다.
			else composing = "";
		}
		// 백스페이스를 실행한 결과를 표시한다.
		if(listener != null) listener.onEvent(new SetComposingEvent(composing));
		// 신세벌식용. 자모 세트에서 테이블을 교환한다.
		changeJamoTable(((lastInputType & 0x1010) != 0) ? lastInputType & 0x000f : 0);
		return true;
	}

	/**
	 * 입력된 키의 코드를 받아서 대응하는 한글 낱자/비한글 문자를 돌려준다.
	 * @param code		입력된 키의 코드.
	 * @param shift	쉬프트가 눌렸을 경우 {@code true}.
	 * @return			대응하는 한글 낱자/비한글 문자.
	 */
	public int inputCode(int code, int shift) {
		int[][] table;
		if(jamoTable != null) table = jamoTable;
		else if(jamoSet != null) table = currentJamoTable;
		else table = null;
		if(table == null) return -1;
		for(int[] item : table) {
			if(item[0] == code) {
				return (shift == 0) ? item[1] : item[2];
			}
		}
		return -1;
	}

	/**
	 * 한글 자모를 입력받아 조합한다.
	 * @param code		한글 자모의 유니코드 / 가상 코드.
	 * @return			입력을 처리한 결과.
	 */
	public int inputJamo(int code) {

		// 입력 기록을 업데이트한다.
		if(composing.equals("")) histories.clear();
		else histories.push(new History(cho, jung, jong, last, beforeJong, composing, lastInputType));

		// 상태를 변환하지 않는다.
		boolean preserveState = (code & 0xffff0000) >> 16 == 1;
		int filteredCode = code & 0xffff;
		int result;
		int combination;
		// 세벌식 한글 초성.
		if(filteredCode >= 0x1100 && filteredCode <= 0x115f) {
			int choCode = code - 0x1100;
			if(!fullMoachigi && !moachigi && !isCho(last) && !isJung(last)) resetComposition();
			if(isCho(last) || fullMoachigi && this.cho != -1) {
				int source = this.cho + 0x1100;
				// 낱자 조합을 실햳한다.
				if((combination = getCombination(source, code)) != -1) {
					choCode = combination - 0x1100;
				} else {
					resetComposition();
				}
            // 낱자 조합에 실패했을 경우 (이미 초성이 입력되어 있음) 조합을 종료한다.
			} else if(this.cho != -1) resetComposition();
            this.cho = choCode;
			// 가상 낱자일 경우 다음 상태로 넘기지 않는다.
			if(lastInputType == 0) result = INPUT_CHO3;
			else if(preserveState) result = lastInputType;
			else result = INPUT_CHO3;
			last = code;
		// 세벌식 한글 중성.
		} else if(filteredCode >= 0x1161 && filteredCode <= 0x11a7) {
			int jungCode = code - 0x1161;
			if(!moachigi && !isCho(last) && !isJung(last)) resetComposition();
			if(isJung(last) || fullMoachigi && this.jung != -1) {
				int source = this.jung + 0x1161;
				if((combination = getCombination(source, code)) != -1) {
					jungCode = combination - 0x1161;
					this.jung = jungCode;
				} else {
					resetComposition();
					this.jung = jungCode;
				}
			} else {
				if(this.jung != -1) resetComposition();
				this.jung = jungCode;
			}
			if(lastInputType == 0) result = INPUT_JUNG3;
			else if(preserveState) result = lastInputType;
			else result = INPUT_JUNG3;
			last = code;
		// 세벌식 한글 종성.
		} else if(filteredCode >= 0x11a8 && filteredCode <= 0x11ff) {
			int jongCode = code - 0x11a7;
			if(!moachigi && !isJung(last) && !isJong(last)) resetComposition();
//			if(last <= -5100 && last >= -5199) resetComposition();
			if(isJong(last) || fullMoachigi && this.jong != -1) {
				int source = this.jong + 0x11a7;
				if((combination = getCombination(source, code)) != -1) {
					this.beforeJong = jong;
					jongCode = combination - 0x11a7;
					this.jong = jongCode;
				} else {
					resetComposition();
					this.jong = jongCode;
				}
			} else {
				if(this.jong != -1) resetComposition();
				this.jong = jongCode;
			}
			if(lastInputType == 0) result = INPUT_JONG3;
			else if(preserveState) result = lastInputType;
			else result = INPUT_JONG3;
			last = code;
		}
		// 두벌식 한글 초/종성.
		else if(filteredCode >= 0x3131 && filteredCode <= 0x314e || filteredCode >= 0x3165 && filteredCode <= 0x3186) {
			// 초, 중성이 모두 있을 경우
			if(this.cho != -1 && this.jung != -1) {
				int jongCode = JONG_CONVERT[code - 0x3131] - 0x11a7;
				// 종성이 이미 존재할 경우.
				if(isJong(last) && this.jong != -1) {
					// 이미 있는 종성과 낱자 조합을 시도.
					if((combination = getCombination(this.jong+0x11a7, jongCode+0x11a7)) != -1) {
						this.beforeJong = this.jong;
						this.jong = combination - 0x11a7;
						last = jongCode + 0x11a7;
					// 낱자 조합 불가/실패시
					} else {
						this.beforeJong = 0;
						resetComposition();
						this.cho = CHO_CONVERT[code - 0x3131] - 0x1100;
						// 대응하는 초성이 존재하지 않을 경우 비운다.
						if(this.cho == -0x1100) this.cho = -1;
						last = CHO_CONVERT[code - 0x3131];
					}
				// 종성이 없을 경우
				} else {
					// 해당하는 종성이 없을 경우 (ㄸ, ㅉ, ㅃ 등)
					if(jongCode == -0x11a7) {
						// 조합을 종료하고 새로운 초성으로 조합을 시작한다.
						resetComposition();
						this.cho = CHO_CONVERT[code - 0x3131] - 0x1100;
						if(this.cho == -0x1100) this.cho = -1;
						last = CHO_CONVERT[code - 0x3131];
					// 해당하는 종성이 있을 경우, 종성을 조합한다.
					} else {
						this.beforeJong = 0;
						this.jong = jongCode;
						last = jongCode + 0x11a7;
					}
				}
			// 초성이나 중성이 없을 경우
			} else {
				int choCode = CHO_CONVERT[code - 0x3131] - 0x1100;
				// 초성이 이미 존재할 경우
				if(isCho(last) && this.cho != -1) {
					// 이미 있는 초성과 낱자 결합을 시도한다.
					if((combination = getCombination(this.cho+0x1100, choCode+0x1100)) != -1) {
						this.cho = combination - 0x1100;
					} else {
						// 낱자 결합 불가 / 실패시 새로운 초성으로 조합 시작.
						resetComposition();
						this.cho = choCode;
						if(this.cho == -0x1100) this.cho = -1;
					}
				// 초성이 없을 경우, 새로운 초성으로 조합 시작.
				} else {
					if(!moachigi) resetComposition();
					this.cho = choCode;
				}
				last = choCode + 0x1100;
				// 해당하는 초성이 없을 경우 초성을 비운다.
				if(this.cho == -0x1100) this.cho = -1;
			}
			result = INPUT_CHO2;
		// 두벌식 중성.
		} else if(filteredCode >= 0x314f && filteredCode <= 0x3163 || filteredCode >= 0x3187 && filteredCode <= 0x318e) {
			// 조합 중인 종성이 없을 경우.
			if(this.jong == -1) {
				// 표준 한글 자모의 중성과 호환용 한글 자모의 중성은 배열 순서가 같음.
				int jungCode = code - 0x314f;
				// 천지인용 가상 낱자 '천'
				if(code == 0x01318d) jungCode = 0x01119e - 0x1161;
				// 옛한글 중성일 경우 따로 변환한다.
				if(code >= 0x3187 && code <= 0x318e) jungCode = TRAD_JUNG_CONVERT[code - 0x3187] - 0x1161;
				//조합 중인 중성이 존재할 경우.
				if(isJung(last) && this.jung != -1) {
					// 중성 낱자 결합을 시도한다.
					int source = (this.jung >= 0) ? this.jung + 0x1161 : this.jung;
					int with = (jungCode >= 0) ? jungCode + 0x1161 : jungCode;
					if((combination = getCombination(source, with)) != -1) {
						this.jung = combination;
						if(combination >= 0x1161) this.jung -= 0x1161;
					} else {
						// 조합 불가 / 실패시 새로운 중성으로 조합 시도.
						resetComposition();
						this.jung = jungCode;
					}
				} else {
					// 조합 중인 중성이 없을 경우 새로운 중성으로 조합을 시작한다.
					if(this.jung != -1) resetComposition();
					this.jung = jungCode;
				}
				last = (jungCode >= 0) ? jungCode + 0x1161 : jungCode;
			// 조합 중인 종성이 존재할 경우 (도깨비불 발생)
			} else {
				int jungCode = code - 0x314f;
				if(code >= 0x3187 && code <= 0x318e) jungCode = TRAD_JUNG_CONVERT[code - 0x3187] - 0x1161;
				// 천지인용 가상 낱자 '천'
				if(code == 0x01318d) jungCode = 0x01119e - 0x1161;
				if(this.cho != -1) {
					// 종성이 두 개 이상 결합되었을 경우
					if(beforeJong != 0) {
						// 앞 종성을 앞 글자의 종성으로 한다.
						this.jong = beforeJong;
						this.composing = getVisible(this.cho, this.jung, this.jong);
						if(listener != null) listener.onEvent(new SetComposingEvent(composing));
						// resetJohab시 last를 초기화하므로 백업한다.
						int last = this.last;
						// 그리고 조합을 종료한 뒤,
						resetComposition();
						// 뒷 종성을 초성으로 변환하여 적용한다.
						this.cho = convertToCho(last) - 0x1100;
						composing = getVisible(this.cho, this.jung, this.jong);
						// 도깨비불이 일어났으므로 기록을 하나 더 남긴다.
						histories.push(new History(cho, jung, jong, last, beforeJong, composing, lastInputType));
						this.jung = jungCode;
					// 결합된 종성이 아니었을 경우
					} else {
						// 종성을 초성으로 변환해서 적용한다.
						int convertedCho;
						if((convertedCho = convertToCho(this.jong+0x11a7)) >-1) {
							this.jong = -1;
							this.composing = getVisible(this.cho, this.jung, this.jong);
							if(listener != null) listener.onEvent(new SetComposingEvent(composing));
							resetComposition();
							this.cho = convertedCho - 0x1100;
							composing = getVisible(this.cho, this.jung, this.jong);
							histories.push(new History(cho, jung, jong, last, beforeJong, composing, lastInputType));
							this.jung = jungCode;
						}
					}
				// 예외 상황에는 조합을 종료하고 새로운 중성으로 조합을 시작한다.
				} else {
					resetComposition();
					this.jung = jungCode;
				}
				last = (jungCode >= 0) ? jungCode + 0x1161 : jungCode;
			}
			result = INPUT_JUNG2;
		}
		// 한글 낱자가 아닐 경우
		else {
			// 조합을 중단하고 처리 안함을 돌려준다.
			resetComposition();
			result = INPUT_NON_HANGUL;
			last = code;
			return result;
		}

		// 화면에 표시되는 문자를 계산해서 표시를 요청한다.
		this.composing = getVisible(this.cho, this.jung, this.jong);
		if(listener != null) listener.onEvent(new SetComposingEvent(composing));

		lastInputType = result;

		// 신세벌식용. 자모 세트에서 테이블을 교환한다.
		changeJamoTable(((lastInputType & 0x1010) != 0) ? lastInputType & 0x000f : 0);

		return result;
	}

	/**
	 * 조합을 종료하고 현재 상태를 초기화한다.
	 */
	public void resetComposition() {
		if(listener != null) listener.onEvent(new FinishComposingEvent());
		cho = jung = jong = -1;
		composing = "";
		lastInputType = 0;
		last = 0;
		histories.clear();
	}

	/**
	 * {@link #combinationTable}의 규칙에 따라 성분이 같은 낟자 두 가지를 결합한다.
	 * 예: 종성 ㄱ + ㅅ = ㄳ의 경우.
	 * @param a		종성 ㄱ.
	 * @param b		종성 ㅅ.
	 * @return		종성 ㄳ. 해당하는 조합이 없을 경우 {@code -1}.
	 */
    private int getCombination(int a, int b) {
		for(int[] item : combinationTable) {
			if(item[0] == a && item[1] == b) return item[2];
		}
		return -1;
	}

	/**
	 * {@link #combinationTable}의 규칙을 역행하여 결합된 낱자를 두 개로 분리한다.
	 * @param jong		결합된 중성.
	 * @return			분해된 {@link Pair}형 결과. 규칙이 없을 경우 {@code null}.
	 */
	Pair getJongseongPair(int jong) {
		for(int[] item : combinationTable) {
			if(item[2] == jong) {
				int resultJong = item[0];
				int resultCho = 0;
				for(int i = 0 ; i < JONG_CONVERT.length ; i++) {
					if(JONG_CONVERT[i] == item[1]) resultCho = CHO_CONVERT[i];
				}
				return new Pair(resultJong, resultCho);
			}
		}
		return null;
	}
	
	private static class Pair {
		int jong, cho;
		public Pair(int jong, int cho) {
			this.jong = jong;
			this.cho = cho;
		}
	}

	/**
	 * 종성 코드를 초성 코드로 변환한다.
	 */
	int convertToCho(int jong) {
		for(int i = 0 ; i < JONG_CONVERT.length ; i++) {
			if(JONG_CONVERT[i] == jong) return CHO_CONVERT[i];
		}
		return -1;
	}

	/**
	 * 초, 중, 종성 코드를 한글 음절 코드로 합친다.
	 */
	int combineHangul(int cho, int jung, int jong) {
		return 0xac00 + (cho * 588) + (jung * 28) + jong;
	}

	/**
	 * 현재 화면에 표시되는 문자를 계산한다.
	 */
	String getVisible(int cho, int jung, int jong) {
		String visible;
		// 옛한글 성분이 포함된 경우 첫가끝으로 조합한다.
		if(cho > 0x12 || jung > 0x14 || jong > 0x1b) {
			if(cho != -1 && jung == -1 && jong == -1) {
				visible = new String(new char[] {(char) (cho + 0x1100)});
			} else if(cho == -1 && jung != -1 && jong == -1) {
				visible = new String(new char[] {(char) (jung + 0x1161)});
			} else if(cho == -1 && jung == -1 && jong != -1) {
				visible = new String(new char[] {(char) (jong + 0x11a8 - 1)});
			} else {
				if(cho == -1) cho = 0x5f;
				visible = new String(new char[] {(char) (cho + 0x1100)})
						+ new String(new char[] {(char) (jung + 0x1161)});
				if(jong != -1) visible += new String(new char[] {(char) (jong + 0x11a8 - 1)});
			}
		// 초성 + 중성 + 종성
		} else if(cho != -1 && jung != -1 && jong != -1) {
			visible = String.valueOf((char) combineHangul(cho, jung, jong));
		// 초성 + 중성
		} else if(cho != -1 && jung != -1) {
			visible = String.valueOf((char) combineHangul(cho, jung, 0));
		// 첫가끝 조합이 가능한 경우
		} else if(firstMidEnd) {
			// 초성
			if(cho != -1 && jung == -1 && jong == -1) {
				visible = String.valueOf((char) CHO_TABLE[cho]);
			// 중성
			} else if(cho == -1 && jung != -1 && jong == -1) {
				visible = String.valueOf((char) JUNG_TABLE[jung]);
			// 종성
			} else if(cho == -1 && jung == -1 && jong != -1) {
				visible = String.valueOf((char) JONG_TABLE[jong]);
			// 나머지 경우에는 첫가끝 조합.
			} else {
				if (cho == -1) cho = 0x5f;
				visible = new String(new char[]{(char) (cho + 0x1100)})
						+ new String(new char[]{(char) (jung + 0x1161)});
				if (jong != -1) visible += new String(new char[]{(char) (jong + 0x11a8 - 1)});
			}
		// 첫가끝 조합을 사용하지 않도록 한 경우
		} else {
			// 초성이 있으면 초성 표시
			if (cho != -1) {
				visible = String.valueOf((char) CHO_TABLE[cho]);
			// 중성이 있으면 중성 표시
			} else if (jung != -1) {
				visible = String.valueOf((char) JUNG_TABLE[jung]);
			// 종성이 있으면 종성 표시
			} else if (jong != -1) {
				visible = String.valueOf((char) JONG_TABLE[jong]);
			// 예외 경우에는 아무것도 표시 안함.
			} else {
				visible = "";
			}
		}
		return visible;
	}

	/**
	 * 유니코드 낱자가 한글 세벌식 초성 혹은 가상 낱자 초성인지 확인한다.
	 */
	public boolean isCho(int code) {
		return (code & 0xffff) >= 0x1100 && (code & 0xffff) <= 0x115f;
	}

	/**
	 * 유니코드 낱자가 한글 세벌식 중성 혹은 가상 낱자 중성인지 확인한다.
	 */
	public boolean isJung(int code) {
		return (code & 0xffff) >= 0x1161 && (code & 0xffff) <= 0x11a7;
	}

	/**
	 * 유니코드 낱자가 한글 세벌식 종성 혹은 가상 낱자 종성인지 확인한다.
	 */
	public boolean isJong(int code) {
		return (code & 0xffff) >= 0x11a8 && (code & 0xffff) <= 0x11ff;
	}
	
	public boolean isMoachigi() {
		return moachigi;
	}

	public void setMoachigi(boolean moachigi) {
		this.moachigi = moachigi;
	}

	public boolean isFullMoachigi() {
		return fullMoachigi;
	}

	public void setFullMoachigi(boolean fullMoachigi) {
		this.fullMoachigi = fullMoachigi;
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
		if(currentJamoTable != null) return currentJamoTable;
		if(jamoTable != null) return jamoTable;
		return null;
	}

	public void setJamoTable(int[][] jamoTable) {
		this.jamoTable = jamoTable;
		this.jamoSet = null;
		this.currentJamoTable = null;
	}

	public int[][][] getJamoSet() {
		return jamoSet;
	}

	public void setJamoSet(int[][][] jamoSet) {
		this.jamoSet = jamoSet;
		this.jamoTable = null;
		this.changeJamoTable(lastInputType);
	}

	public void changeJamoTable(int num) {
		if(jamoSet == null) return;
		int[][] table = jamoSet[num];
		if(table == null) return;
		currentJamoTable = table;
	}

	public int[][] getCombinationTable() {
		return combinationTable;
	}

	public void setCombinationTable(int[][] combinations) {
		this.combinationTable = combinations;
	}

	/**
	 * 한글 엔진에서 발생하는 이벤트를 처리하기 위한 리스너.
	 */
	public static interface HangulEngineListener {
		public void onEvent(HangulEngineEvent event);
	}

	/**
	 * 한글 엔진 이벤트 클래스.
	 */
	public static abstract class HangulEngineEvent {
	}

	/**
	 * 조합이 종료되었을 경우 호출되는 이벤트.
	 */
	public static class FinishComposingEvent extends HangulEngineEvent {
	}

	/**
	 * 조합 중인 글자가 바뀌었을 경우 발생하는 이벤트.
	 */
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
