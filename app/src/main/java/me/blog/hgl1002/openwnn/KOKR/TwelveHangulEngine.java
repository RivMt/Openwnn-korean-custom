package me.blog.hgl1002.openwnn.KOKR;

public class TwelveHangulEngine extends HangulEngine {
	
	int[][] addStrokeTable;
	
	int lastCode;
	
	boolean cycled;
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
		if(cycled) composing = "";
		if(jamo == DefaultSoftKeyboardKOKR.KEYCODE_KR12_ADDSTROKE) {
			boolean found = false;
//ykhong edit start			
			int cc, c2;
            switch(lastInputType) {
			case INPUT_CHO2:
			case INPUT_CHO3:
				c2 = CHO_TABLE[this.cho];
				cc = CHO_CONVERT[c2 - 0x3131];
				break;

			case INPUT_JUNG2:
			case INPUT_JUNG3:
				c2 = JUNG_TABLE[this.jung];
				cc = c2 - 0x314f + 0x1161;
				break;

			case INPUT_JONG2:
			case INPUT_JONG3:
				c2 = JONG_TABLE[this.jong];
				cc = JONG_CONVERT[c2 - 0x3131];
				break;

			default:
				return 0;

			}

/* 
			for(int[] item : addStrokeTable) {
				int index = 0;
				if(addStrokeIndex > 0 && addStrokeIndex < item.length) index = addStrokeIndex;
				if(item[index] == last) {
					if(++addStrokeIndex >= item.length) addStrokeIndex = 0;
					jamo = item[addStrokeIndex];
					cycled = true;
					found = true;
				}
			}
*/
			for(int[] item : addStrokeTable) {
				int index = 0;
				if(found)
					break;
				for(; index < item.length; index++) {
					if(item[index] == cc) {
						addStrokeIndex = index;
						if(++addStrokeIndex >= item.length)
							continue;
						jamo = item[addStrokeIndex];
						super.backspace();
						found = true;
						break; // 원하는 STROKE 으로 변환했다면 그만한다.
					}
					if( (index + 1) >= (item.length - 1) )
						break;
				}
			}
//ykhong edit end			
			if(!found) return 0;
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
		if(jung == -2003 || jung == -2004 || jung == -5003 || jung == -5004) {
			char displayJung = (char) ((jung == -5004 || jung == -2004) ? 0x2025 : 0x00b7);
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
	}

	@Override
	public boolean backspace() {
		resetCycle();
		return super.backspace();
	}

	public int[][] getAddStrokeTable() {
		return addStrokeTable;
	}

	public void setAddStrokeTable(int[][] addStrokeTable) {
		this.addStrokeTable = addStrokeTable;
	}

}
