package io.rivmt.keyboard.openwnn.KOKR;

public class TwelveHangulEngine extends HangulEngine {
	
	int[][] addStrokeTable;
	
	int lastCode;
	
	int cycleIndex;
	int addStrokeBase, addStrokeBaseCombined;
	int addStrokeIndex;
	
	public TwelveHangulEngine() {
		super();
	}

	@Override
	public int inputCode(int code, int shift) {
		int ret = -1;
		if((lastCode != code)) {
			resetCycle();
		}
		int[][] table;
		if(jamoTable != null) table = jamoTable;
		else if(jamoSet != null) table = currentJamoTable;
		else table = null;
		if(table == null) return -1;
		for(int[] item : table) {
			if(item[0] == code) {
				if(item.length == 2) {
					ret = item[1];
					break;
				}
				cycleIndex++;
				if(cycleIndex >= item.length) cycleIndex = 1;
				ret = item[cycleIndex];
				if(lastCode == code) super.backspace();
			}
		}
		lastCode = code;
		return ret;
	}

	@Override
	public int inputJamo(int jamo) {
		// 획추가 키.
		if(jamo == DefaultSoftKeyboardKOKR.KEYCODE_KR12_ADDSTROKE) {
			boolean found = false;
			for(int[] item : addStrokeTable) {
				if(item[0] == addStrokeBase || item[0] == addStrokeBaseCombined) {
					if(++addStrokeIndex >= item.length) addStrokeIndex = item.length-1;
					jamo = item[addStrokeIndex];
					if(item[0] == addStrokeBaseCombined) eraseJamo();
					else super.backspace();
					found = true;
					break;
				}
			}
			if(!found) return -1;
		} else if(jamo == DefaultSoftKeyboardKOKR.KEYCODE_KR12_ADDSTROKE-1) {
			if((lastInputType == INPUT_CHO2 || lastInputType == INPUT_JONG3) && this.jong != -1) {
				switch(last) {
				case 0x11a8:
					super.backspace();
					jamo = 0x3132;
					break;

				case 0x11ae:
					super.backspace();
					jamo = 0x3138;
					break;

				case 0x11b8:
					super.backspace();
					jamo = 0x3143;
					break;

				case 0x11ba:
					super.backspace();
					jamo = 0x3146;
					break;

				case 0x11bd:
					super.backspace();
					jamo = 0x3149;
					break;

				default:
					return -1;
				}
			} else if(lastInputType == INPUT_CHO3 || lastInputType == INPUT_CHO2) {
				switch(this.cho) {
				case 0x00:
				case 0x03:
				case 0x07:
				case 0x09:
				case 0x0c:
					super.backspace();
					jamo = this.cho + 0x1101;
					break;

				default:
					return -1;
				}
			} else {
				return -1;
			}
		} else {
			addStrokeBase = 0;
			addStrokeBaseCombined = 0;
		}

		int result = super.inputJamo(jamo);

		if(addStrokeBase == 0) {
			addStrokeBase = last;
			addStrokeBaseCombined = isCho(last) ? cho + 0x1100 : isJung(last) ? jung + 0x1161 : isJong(last) ? jong + 0x11a7 : 0;
		}

		if(result == 0) {
			composing = String.valueOf((char) jamo);
			if(listener != null) listener.onEvent(new SetComposingEvent(composing));
			return 1;
		}
		
		return result;
	}
	
	@Override
	public String getVisible(int cho, int jung, int jong) {
		String composing;
		// process virtual 'Cheon' of cheonjiin.
		if(jung == 0x01119e - 0x1161 || jung == 0x0111a2 - 0x1161) {
			char displayJung = (char) ((jung == 0x0111a2 - 0x1161) ? 0x2025 : 0x00b7);
			if(cho != -1 && jung != -1 && jong != -1) {
				composing = (char) CHO_TABLE[cho] + "" + displayJung + "" + (char) JONG_TABLE[jong];
			}
			else if(cho != -1 && jung != -1) {
				composing = (char) CHO_TABLE[cho] + "" + displayJung;
			}
			else if(cho != -1) {
				composing = String.valueOf((char) CHO_TABLE[cho]);
			}
			else if(jung != -1) {
				composing = String.valueOf(displayJung);
			}
			else if(jong != -1) {
				composing = String.valueOf((char) JONG_TABLE[jong]);
			}
			else {
				composing = "";
			}
		} else {
			return super.getVisible(cho, jung, jong);
		}
		return composing;
	}

	public void resetCycle() {
		cycleIndex = 0;
		addStrokeIndex = 0;
		lastCode = 0;
	}

	@Override
	public void resetComposition() {
		if(!composing.equals("")) addStrokeBase = addStrokeBaseCombined = 0;
		super.resetComposition();
	}

	@Override
	public boolean backspace() {
		addStrokeBase = addStrokeBaseCombined = 0;
		resetCycle();
		return super.backspace();
	}

	public void eraseJamo() {
		int inputType = lastInputType;
		while(lastInputType == inputType && lastInputType != 0) {
			super.backspace();
		}
	}

	public int[][] getAddStrokeTable() {
		return addStrokeTable;
	}

	public void setAddStrokeTable(int[][] addStrokeTable) {
		this.addStrokeTable = addStrokeTable;
	}

}
