package me.blog.hgl1002.openwnn.KOKR;

public class ComposingWord {

	private StringBuilder composingWord = new StringBuilder();
	private String composingChar = "";

	public void composeChar(String composingChar) {
		this.composingChar = composingChar;
	}

	public void commitComposingChar() {
		composingWord.append(composingChar);
		composingChar = "";
	}

	public String commitComposingWord() {
		commitComposingChar();
		String result = composingWord.toString();
		composingWord = new StringBuilder();
		composingChar = "";
		return result;
	}

	public boolean backspace() {
		if(composingWord.length() == 0) return false;
		else composingWord.deleteCharAt(composingWord.length()-1);
		return true;
	}

	public String getComposingChar() {
		return composingChar;
	}

	public String getComposingWord() {
		return composingWord.toString();
	}

	public void setComposingWord(String composingWord) {
		this.composingWord = new StringBuilder(composingWord);
	}

	public String getEntireWord() {
		return composingWord.toString() + composingChar;
	}

}
