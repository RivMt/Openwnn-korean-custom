package me.blog.hgl1002.openwnn.KOKR;

public class TwelveHangulEngine extends HangulEngine {
	
	int[][] addStrokeTable;
	
	int lastCode;
	int firstJamo, lastCycleJamo;
	
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
	public int inputJamo(int jamo) {
		if(cycled) composing = "";
		if(jamo == DefaultSoftKeyboardKOKR.KEYCODE_KR12_ADDSTROKE) {
			boolean found = false;
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
		if(jung == 0x119e - 0x1161 || jung == 0x11a2 - 0x1161) {
			char displayJung = (char) ((jung + 0x1161 == 0x11a2) ? 0x2025 : 0x00b7);
			if(cho != -1 && jung != -1 && jong != -1) {
				composing = CHO_TABLE[cho] + "" + displayJung + "" + JONG_TABLE[jong];
			}
			else if(cho != -1 && jung != -1) {
				composing = CHO_TABLE[cho] + "" + displayJung;
			}
			else if(cho != -1) {
				composing = String.valueOf(CHO_TABLE[cho]);
			}
			else if(jung != -1) {
				composing = String.valueOf(displayJung);
			}
			else if(jong != -1) {
				composing = String.valueOf(JONG_TABLE[jong]);
			}
			else {
				composing = "";
			}
		} else {
			return super.getVisible(cho, jung, jong);
		}
		return composing;
	}

	@Override
	public void resetJohab() {
		if(cycled) return;
		super.resetJohab();
	}
	
	public void forceResetJohab() {
		super.resetJohab();
	}

	public void resetCycle() {
		cycleIndex = 0;
		addStrokeIndex = 0;
	}

	@Override
	public boolean backspace() {
		cycleIndex = 0;
		addStrokeIndex = 0;
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
