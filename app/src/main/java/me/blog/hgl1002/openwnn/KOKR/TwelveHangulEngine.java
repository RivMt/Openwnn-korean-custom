package me.blog.hgl1002.openwnn.KOKR;

public class TwelveHangulEngine extends HangulEngine {
	
	int[][] addStrokeTable;
	
	int lastCode;
	
	int cycleIndex;
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
			int search = 0;
			if(isCho(last)) {
				search = cho;
				if(search >= 0) search += 0x1100;
			} else if(isJung(last)) {
				search = jung;
				if(search >= 0) search += 0x1161;
			} else if(isJong(last)) {
				search = jong;
				if(search >= 0) search += 0x11a7;
			}
			boolean found = false;
//ykhong edit start
			for(int[] item : addStrokeTable) {
				int index = 0;
				if(addStrokeIndex > 0 && addStrokeIndex < item.length) index = addStrokeIndex;
				if(item[index] == search || item[index] == last) {
					if(++addStrokeIndex >= item.length) addStrokeIndex = 0;
					jamo = item[addStrokeIndex];
					if(item[index] == last) super.backspace();
					else eraseJamo();
					found = true;
					break;
				}
			}
			/*
			for(int[] item : addStrokeTable) {
				int index = 0;
				if(found)
					break;
				for(; index < item.length; index++) {
					if(item[index] == last) {
						addStrokeIndex = index;
						if(++addStrokeIndex >= item.length) addStrokeIndex = 1;
						jamo = item[addStrokeIndex];
						super.backspace();
						found = true;
						break; // 원하는 STROKE 으로 변환했다면 그만한다.
					}
					if( (index + 1) >= (item.length - 1) )
						break;
				}
			}
			*/
//ykhong edit end			
			if(!found) return 0;
		} else if(jamo == DefaultSoftKeyboardKOKR.KEYCODE_KR12_ADDSTROKE-1){
			if((lastInputType == INPUT_CHO2 || lastInputType == INPUT_JONG3) && this.jong != -1) {
				switch(this.jong) {
				case 0x01:
					super.backspace();
					jamo = 0x11a9;
					break;

				case 0x11ba - 0x11a7:
					super.backspace();
					jamo = 0x11bb;
					break;

				default:
					return 0;
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
					return 0;
				}
			} else {
				return 0;
			}
		}
		int result = super.inputJamo(jamo);

		if(result == 0) {
			resetJohab();
			composing = String.valueOf((char) jamo);
			if(listener != null) listener.onEvent(new SetComposingEvent(composing));
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
	public boolean backspace() {
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
