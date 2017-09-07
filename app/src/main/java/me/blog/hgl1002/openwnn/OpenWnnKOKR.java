package me.blog.hgl1002.openwnn;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
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

import static me.blog.hgl1002.openwnn.KOKR.LayoutAlphabet.*;
import static me.blog.hgl1002.openwnn.KOKR.LayoutDubul.*;
import static me.blog.hgl1002.openwnn.KOKR.Layout12KeySebul.*;
import static me.blog.hgl1002.openwnn.KOKR.LayoutGongSebul.*;
import static me.blog.hgl1002.openwnn.KOKR.LayoutShinSebul.*;
import static me.blog.hgl1002.openwnn.KOKR.LayoutMoachigiSebul.*;

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

	public static final int[][] ALT_SYMBOLS_TABLE = {
			{0x31, 0x31},
			{0x32, 0x32},
			{0x33, 0x33},
			{0x34, 0x34},
			{0x35, 0x35},
			{0x36, 0x36},
			{0x37, 0x37},
			{0x38, 0x38},
			{0x39, 0x39},
			{0x30, 0x30},

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
	public static final int ENGINE_MODE_SEBUL_SEMOE_2016 = 116;
	public static final int ENGINE_MODE_SEBUL_SEMOE = 117;
	
	public static final int ENGINE_MODE_DUBULSIK_YET = 118;
	public static final int ENGINE_MODE_SEBUL_393Y = 119;
	public static final int ENGINE_MODE_SEBUL_3_2015Y = 120;	
	
	public static final int ENGINE_MODE_12KEY_ALPHABET = 150;
	public static final int ENGINE_MODE_12KEY_SEBUL_MUNHWA = 151;
	public static final int ENGINE_MODE_12KEY_SEBUL_HANSON = 153;
	public static final int ENGINE_MODE_12KEY_DUBUL = 155;
	public static final int ENGINE_MODE_12KEY_SEBUL_SENA = 156;

	public static final int ENGINE_MODE_ENGLISH_DVORAK = 192;
	public static final int ENGINE_MODE_ENGLISH_COLEMAK = 193;
	
	public static final int ENGINE_MODE_OPT_TYPE_QWERTY = 200;
	public static final int ENGINE_MODE_OPT_TYPE_12KEY = 201;
	
	public static final int LONG_CLICK_EVENT = OpenWnnEvent.PRIVATE_EVENT_OFFSET | 0x100;
	public static final int FLICK_UP_EVENT = OpenWnnEvent.PRIVATE_EVENT_OFFSET | 0x101;
	public static final int FLICK_DOWN_EVENT = OpenWnnEvent.PRIVATE_EVENT_OFFSET | 0x102;
	public static final int FLICK_LEFT_EVENT = OpenWnnEvent.PRIVATE_EVENT_OFFSET | 0x103;
	public static final int FLICK_RIGHT_EVENT = OpenWnnEvent.PRIVATE_EVENT_OFFSET | 0x104;

	public static final int TIMEOUT_EVENT = OpenWnnEvent.PRIVATE_EVENT_OFFSET | 0x1;

	public static final int BACKSPACE_LEFT_EVENT = OpenWnnEvent.PRIVATE_EVENT_OFFSET | 0x211;
	public static final int BACKSPACE_RIGHT_EVENT = OpenWnnEvent.PRIVATE_EVENT_OFFSET | 0x212;
	public static final int BACKSPACE_COMMIT_EVENT = OpenWnnEvent.PRIVATE_EVENT_OFFSET | 0x210;

	public static final String LANGKEY_SWITCH_KOR_ENG = "switch_kor_eng";
	public static final String LANGKEY_SWITCH_NEXT_METHOD = "switch_next_method";
	public static final String LANGKEY_LIST_METHODS = "list_methods";
	
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
	boolean mQuickPeriod;

	boolean mStandardJamo;
	String mLangKeyAction;
	String mLangKeyLongAction;

	boolean mAltDirect;
	
	boolean selectionMode;
	int selectionStart, selectionEnd;

	boolean mSpace, mCharInput;
	boolean mInput;

	boolean mBackspaceSelectionMode;
	int mBackspaceSelectionStart;
	int mBackspaceSelectionEnd;

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
		if(mHangulEngine instanceof TwelveHangulEngine) ((TwelveHangulEngine) mHangulEngine).forceResetJohab();
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
		mQuickPeriod = pref.getBoolean("keyboard_quick_period", false);

		mStandardJamo = pref.getBoolean("system_use_standard_jamo", mStandardJamo);
		mLangKeyAction = pref.getString("system_action_on_lang_key_press", LANGKEY_SWITCH_KOR_ENG);
		mLangKeyLongAction = pref.getString("system_action_on_lang_key_long_press", LANGKEY_LIST_METHODS);
		
		if(hardKeyboardHidden) mQwertyEngine.setMoachigi(mMoachigi);
		else mQwertyEngine.setMoachigi(mHardwareMoachigi);
		mQwertyEngine.setFirstMidEnd(mStandardJamo);
		m12keyEngine.setFirstMidEnd(mStandardJamo);

		// [2017/7/24 ykhong] : 한글 Moachigi 설정 적용
		if(hardKeyboardHidden)
			m12keyEngine.setMoachigi(mMoachigi);

		mAltDirect = pref.getBoolean("hardware_alt_direct", true);


		mCharInput = false;

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

		case BACKSPACE_LEFT_EVENT:
			if(!mBackspaceSelectionMode) {
				mBackspaceSelectionMode = true;
				mBackspaceSelectionEnd = mInputConnection.getTextBeforeCursor(Integer.MAX_VALUE, 0).length();
				mBackspaceSelectionStart = mBackspaceSelectionEnd;
				mHangulEngine.resetJohab();
			}
			while(true) {
				mBackspaceSelectionStart--;
				mInputConnection.setSelection(mBackspaceSelectionStart, mBackspaceSelectionEnd);
				if(mInputConnection.getTextBeforeCursor(1, 0).equals(" ")
						|| mBackspaceSelectionStart <= 0
						|| mBackspaceSelectionStart >= mBackspaceSelectionEnd) {
					break;
				}
			}
			return true;

		case BACKSPACE_RIGHT_EVENT:
			if(!mBackspaceSelectionMode) {
				return true;
			}
			while(true) {
				mBackspaceSelectionStart++;
				mInputConnection.setSelection(mBackspaceSelectionStart, mBackspaceSelectionEnd);
				if(mInputConnection.getTextBeforeCursor(1, 0).equals(" ")
						|| mBackspaceSelectionStart <= 0
						|| mBackspaceSelectionStart >= mBackspaceSelectionEnd) {
					break;
				}
			}
			return true;

		case BACKSPACE_COMMIT_EVENT:
			if(!mBackspaceSelectionMode) {
				return true;
			}
			mInputConnection.setSelection(mBackspaceSelectionEnd, mBackspaceSelectionEnd);
			mInputConnection.deleteSurroundingText(mBackspaceSelectionEnd - mBackspaceSelectionStart, 0);
			mBackspaceSelectionMode = false;
			return true;
			
		case TIMEOUT_EVENT:
			if(mEnableTimeout) {
				mHangulEngine.resetJohab();
				if(mHangulEngine instanceof TwelveHangulEngine) {
					((TwelveHangulEngine) mHangulEngine).forceResetJohab();
					((TwelveHangulEngine) mHangulEngine).resetCycle();
				}
			}
			if(mQuickPeriod) {
				mSpace = false;
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
					onLangKey(mLangKeyLongAction);
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
			mCharInput = true;
			mInput = true;
			mSpace = false;
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
			case DefaultSoftKeyboard.KEYCODE_CHANGE_LANG:
				onLangKey(mLangKeyAction);
				return true;

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
				if(mQuickPeriod && mSpace && mCharInput) {
					mInputConnection.deleteSurroundingText(1, 0);
					mInputConnection.commitText(". ", 1);
					mSpace = false;
					mCharInput = false;
				} else {
					mInputConnection.commitText(" ", 1);
					mSpace = true;
				}
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

	private void onLangKey(String action) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		IBinder token = getWindow().getWindow().getAttributes().token;
		switch(action) {
		case LANGKEY_SWITCH_KOR_ENG:
			((DefaultSoftKeyboardKOKR) mInputViewManager).nextLanguage();
			break;

		case LANGKEY_SWITCH_NEXT_METHOD:
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
				if (mInput) {
					mInput = false;
					imm.switchToLastInputMethod(token);
				} else {
					imm.switchToNextInputMethod(token, false);
				}
			}
			break;

		case LANGKEY_LIST_METHODS:
			imm.showInputMethodPicker();
			break;
		}
	}
	
	@SuppressLint("NewApi")
	private boolean processKeyEvent(KeyEvent ev) {
		int key = ev.getKeyCode();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			if (ev.isCtrlPressed()) return false;
		}

		if (ev.isShiftPressed()) {
			switch (key) {
			case KeyEvent.KEYCODE_DPAD_UP:
			case KeyEvent.KEYCODE_DPAD_DOWN:
			case KeyEvent.KEYCODE_DPAD_LEFT:
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				if (!selectionMode) {
					selectionEnd = mInputConnection.getTextBeforeCursor(Integer.MAX_VALUE, 0).length();
					selectionStart = selectionEnd;
					selectionMode = true;
				}
				if (selectionMode) {
					if (key == KeyEvent.KEYCODE_DPAD_LEFT) selectionEnd--;
					if (key == KeyEvent.KEYCODE_DPAD_RIGHT) selectionEnd++;
					int start = selectionStart, end = selectionEnd;
					if (selectionStart > selectionEnd) {
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

		if ((key <= -200 && key > -300) || (key <= -2000 && key > -3000)) {
			int jamo = mHangulEngine.inputCode(key, mHardShift);
			if (jamo != -1) {
				if (mHardShift != 0) jamo = Character.toUpperCase(jamo);
				if (mHangulEngine.inputJamo(jamo) != 0) {
					mInputConnection.setComposingText(mHangulEngine.getComposing(), 1);
				}
			}
			return true;
		}

		if (key >= KeyEvent.KEYCODE_NUMPAD_0 && key <= KeyEvent.KEYCODE_NUMPAD_RIGHT_PAREN) {
			mHangulEngine.resetJohab();
			return false;
		}

		if (ev.isPrintingKey()) {

			int code = ev.getUnicodeChar(mShiftKeyToggle[mHardShift] | mAltKeyToggle[mHardAlt]);
			this.inputChar((char) code, (mHardAlt != 0 && mAltDirect) ? true : false);

			if (mHardShift == 1) {
				mShiftPressing = false;
			}
			if (mHardAlt == 1) {
				mAltPressing = false;
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

		} else if (key == KeyEvent.KEYCODE_SPACE) {
			mHangulEngine.resetJohab();
			if (mHardShift == 1) {
				((DefaultSoftKeyboardKOKR) mInputViewManager).nextLanguage();
				mHardShift = 0;
				mShiftPressing = false;
				updateMetaKeyStateDisplay();
				updateNumKeyboardShiftState();
				return true;
			}
			mInputConnection.commitText(" ", 1);
			return true;
		} else if (key == KeyEvent.KEYCODE_DEL) {
			if (!mHangulEngine.backspace()) {
				mHangulEngine.resetJohab();
				return false;
			}
			if (mHangulEngine.getComposing() == "")
				mHangulEngine.resetJohab();
			return true;
		} else if (key == DefaultSoftKeyboardKOKR.KEYCODE_NON_SHIN_DEL) {
			mHangulEngine.resetJohab();
			mInputConnection.deleteSurroundingText(1, 0);
			return true;
		} else if(key == KeyEvent.KEYCODE_ENTER) {
			mHangulEngine.resetJohab();
			mHardShift = 0;
			mHardAlt = 0;
			updateMetaKeyStateDisplay();
			updateNumKeyboardShiftState();
			EditorInfo editorInfo = getCurrentInputEditorInfo();
			switch(editorInfo.imeOptions & EditorInfo.IME_MASK_ACTION) {
			case EditorInfo.IME_ACTION_SEARCH:
			case EditorInfo.IME_ACTION_GO:
				sendDefaultEditorAction(true);
				return true;

			default:
				return false;
			}
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
			mHangulEngine.setVirtualJamoTable(VIRTUAL_SEBUL_SHIN_ORIGINAL);
			break;
	
		case ENGINE_MODE_SEBUL_3_2015:
			mDirectInputMode = false;
			mEnableTimeout = false;
			mHangulEngine.setJamoSet(JAMOSET_SEBUL_3_2015);
			mHangulEngine.setCombinationTable(COMB_SEBUL_3_2015);
			mHangulEngine.setVirtualJamoTable(VIRTUAL_SEBUL_SHIN_ORIGINAL);
			break;	
			
		case ENGINE_MODE_SEBUL_3_2015Y:
			mDirectInputMode = false;
			mEnableTimeout = false;
			mHangulEngine.setJamoTable(JAMO_SEBUL_3_2015Y);
			mHangulEngine.setCombinationTable(COMB_FULL);
			mHangulEngine.setVirtualJamoTable(VIRTUAL_SEBUL_SHIN_ORIGINAL);
			break;	
	
		case ENGINE_MODE_SEBUL_3_P3:
			mDirectInputMode = false;
			mEnableTimeout = false;
			mHangulEngine.setJamoSet(JAMOSET_SEBUL_3_P3);
			mHangulEngine.setCombinationTable(COMB_SEBUL_3_P3);
			mHangulEngine.setVirtualJamoTable(VIRTUAL_SEBUL_SHIN_ORIGINAL);
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
			mQwertyEngine.setFullMoachigi(mFullMoachigi && !hardHidden);
			if(mFullMoachigi && !hardHidden) mEnableTimeout = true;
			else mEnableTimeout = false;
			mHangulEngine.setJamoTable(JAMO_SEBUL_AHNMATAE);
			mHangulEngine.setCombinationTable(COMB_SEBUL_AHNMATAE);
			break;	
		
		case ENGINE_MODE_SEBUL_SEMOE_2016:
			mDirectInputMode = false;
			if(mFullMoachigi && !hardHidden) mEnableTimeout = true;
			else mEnableTimeout = false;
			mHangulEngine.setJamoTable(JAMO_SEBUL_SEMOE_2016);
			mHangulEngine.setCombinationTable(COMB_SEBUL_SEMOE);
			break;	
		
		case ENGINE_MODE_SEBUL_SEMOE:
			mDirectInputMode = false;
			mQwertyEngine.setFullMoachigi(mFullMoachigi && !hardHidden);
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
		case ENGINE_MODE_SEBUL_SHIN_ORIGINAL:
		case ENGINE_MODE_SEBUL_SHIN_EDIT:
		case ENGINE_MODE_SEBUL_SHIN_M:
		case ENGINE_MODE_SEBUL_SHIN_P2:
		case ENGINE_MODE_SEBUL_3_2015M:
		case ENGINE_MODE_SEBUL_3_2015:
		case ENGINE_MODE_SEBUL_3_P3:
			DefaultSoftKeyboardKOKR kokr = (DefaultSoftKeyboardKOKR) mInputViewManager;
			boolean capsLock = kokr.isCapsLock();
			if(mHardShift == 2) capsLock = true;
			boolean shift = !kokr.mHardKeyboardHidden && mHardShift == 1;
			if(type == HangulEngine.INPUT_CHO3) {
				kokr.changeKeyMode(DefaultSoftKeyboardKOKR.KEYMODE_HANGUL_CHO);
			} else if(type == HangulEngine.INPUT_JUNG3) {
				kokr.changeKeyMode(DefaultSoftKeyboardKOKR.KEYMODE_HANGUL_JUNG);
			} else if(type == HangulEngine.INPUT_JONG3) {
				kokr.changeKeyMode(DefaultSoftKeyboardKOKR.KEYMODE_HANGUL_JONG);
			} else {
				kokr.changeKeyMode(DefaultSoftKeyboardKOKR.KEYMODE_HANGUL);
			}
			if(capsLock) {
				kokr.setCapsLock(capsLock);
				kokr.setShiftState(1);
				mHardShift = 2;
				mShiftPressing = true;
			} else if(shift) {
				mHardShift = 1;
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
		super.onEvaluateInputViewShown();
		return true;
	}

	public void resetHardShift(boolean force) {
		if(mHardShift == 2 && !force) return;
		mHardShift = 0;
		mShiftPressing = false;
	}

}
