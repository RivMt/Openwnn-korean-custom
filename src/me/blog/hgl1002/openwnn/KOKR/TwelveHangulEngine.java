package me.blog.hgl1002.openwnn.KOKR;

import me.blog.hgl1002.openwnn.OpenWnn;

public class TwelveHangulEngine extends HangulEngine {
	
	int[][] addStrokeTable;
	
	int lastCode;
	int firstJamo, lastCycleJamo, lastJamo;
	
	boolean cycled;
	int cycleIndex;
	int addStrokeIndex;
	
	public TwelveHangulEngine(OpenWnn parent) {
		super(parent);
	}

	@Override
	public int inputCode(int code, int shift) {
		int ret = -1;
		if((lastCode != code)) {
			resetCycle();
		}
		for(int[] item : jamoTable) {
			if(item[0] == code) {
				cycled = true;
				if(item.length == 2) cycled = false;
				firstJamo = item[1];
				if(++cycleIndex >= item.length) cycleIndex = 1;
				ret = item[cycleIndex];
				if(firstJamo != DefaultSoftKeyboardKOKR.KEYCODE_KR12_ADDSTROKE)
					lastCycleJamo = item[cycleIndex];
			}
		}
		if(lastCode != code) cycled = false;
		lastCode = code;
		return ret;
	}

	@Override
	public boolean inputJamo(int jamo) {
		if(cycled) composing = "";
		if(jamo == DefaultSoftKeyboardKOKR.KEYCODE_KR12_ADDSTROKE) {
			boolean found = false;
			for(int[] item : addStrokeTable) {
				int index = 0;
				if(addStrokeIndex > 0 && addStrokeIndex < item.length) index = addStrokeIndex;
				if(item[index] == lastJamo) {
					if(++addStrokeIndex >= item.length) addStrokeIndex = 0;
					jamo = item[addStrokeIndex];
					cycled = true;
					found = true;
				}
			}
			if(!found) return true;
		}
		int combination = -1;
		if(jamo >= 0x1100 && jamo <= 0x11ff) {
			if(jamo >= 0x1100 && jamo <= 0x1112) {
				if(cho != -1) {
					if(lastInputType != INPUT_CHO) resetJohab();
					combination = getCombination(cho+0x1100, jamo);
					if(combination == -1) {
						if(!cycled) resetJohab();
						cho = jamo - 0x1100;
					}
					else cho = combination - 0x1100;
				}
				else {
					//resetJohab();
					cho = jamo - 0x1100;
				}
				lastInputType = INPUT_CHO;
			} else if(jamo >= 0x1161 && jamo <= 0x1175 || jamo == 0x119e) {
				if(jung != -1) {
					combination = getCombination(jung+0x1161, jamo);
					if(combination == -1) {
						if(!cycled) resetJohab();
						jung = jamo - 0x1161;
					}
					else jung = combination - 0x1161;
				}
				else jung = jamo - 0x1161;
				lastInputType = INPUT_JUNG;
			} else if(jamo >= 0x11a8 && jamo <= 0x11c2) {
				if(jong != -1) {
					combination = getCombination(jong-1+0x11a8, jamo);
					if(combination == -1) {
						if(!cycled) resetJohab();
						jong = jamo - 0x11a8 + 1;
					}
					else jong = combination - 0x11a8 + 1;
				}
				else jong = jamo - 0x11a8 + 1;
				lastInputType = INPUT_JONG;
			}

			processVisible();
			
		} else {
			if(!cycled) {
				resetJohab();
			}
			composing = String.valueOf((char) jamo);
		}
		
		if(combination != -1) lastJamo = combination;
		else lastJamo = jamo;
		
		return true;
	}
	
	@Override
	public void processVisible() {
		if(jung == 0x119e - 0x1161 || jung == 0x11a2 - 0x1161) {
			char displayJung = (char) ((jung + 0x1161 == 0x11a2) ? 0x2025 : 0x00b7);
			if(cho != -1 && jung != -1 && jong != -1) {
				composing = choTable[cho] + "" + displayJung + "" + jongTable[jong];
			}
			else if(cho != -1 && jung != -1) {
				composing = choTable[cho] + "" + displayJung;
			}
			else if(cho != -1) {
				composing = String.valueOf(choTable[cho]);
			}
			else if(jung != -1) {
				composing = String.valueOf(displayJung);
			}
			else if(jong != -1) {
				composing = String.valueOf(jongTable[jong]);
			}
			else {
				composing = "";
			}
		} else {
			super.processVisible();
		}
	}

	@Override
	public void resetCJJ() {
		super.resetCJJ();
	}
	
	public void resetCycle() {
		cycleIndex = 0;
		addStrokeIndex = 0;
	}

	@Override
	public boolean backspace() {
		cycleIndex = 0;
		addStrokeIndex = 0;
		lastJamo = 0;
		lastCycleJamo = 0;
		return super.backspace();
	}

	public int[][] getAddStrokeTable() {
		return addStrokeTable;
	}

	public void setAddStrokeTable(int[][] addStrokeTable) {
		this.addStrokeTable = addStrokeTable;
	}

}
