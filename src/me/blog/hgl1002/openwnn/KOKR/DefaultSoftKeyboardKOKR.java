package me.blog.hgl1002.openwnn.KOKR;

import java.util.Locale;

import android.content.Context;
import android.content.SharedPreferences;
import android.inputmethodservice.Keyboard;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import me.blog.hgl1002.openwnn.DefaultSoftKeyboard;
import me.blog.hgl1002.openwnn.OpenWnn;
import me.blog.hgl1002.openwnn.OpenWnnEvent;
import me.blog.hgl1002.openwnn.OpenWnnKOKR;
import me.blog.hgl1002.openwnn.R;

public class DefaultSoftKeyboardKOKR extends DefaultSoftKeyboard {

	public static final int KEYBOARD_EN_ALPHABET_QWERTY = 0;
	public static final int KEYBOARD_EN_ALPHABET_DVORAK = 1;

	public static final int KEYBOARD_EN_12KEY_ALPHABET = 4;
	
	public static final int KEYBOARD_KO_DUBUL_STANDARD = 2;
	public static final int KEYBOARD_KO_DUBUL_DANMOEUM_GOOGLE = 3;
	public static final int KEYBOARD_KO_DUBUL_DANMOEUM_MUE128= 4;
	
	public static final int KEYBOARD_KO_SEBUL_390 = 5;
	public static final int KEYBOARD_KO_SEBUL_391 = 6;
	public static final int KEYBOARD_KO_SEBUL_DANMOEUM = 7;
	
	public static final int KEYBOARD_KO_SEBUL_SHIN_ORIGINAL = 9;
	public static final int KEYBOARD_KO_SEBUL_SHIN_EDIT = 10;
	
	public static final int KEYBOARD_KO_SEBUL_MUNHWA = 11;
	public static final int KEYBOARD_KO_SEBUL_SENA = 12;
	public static final int KEYBOARD_KO_SEBUL_HANSON = 13;
	
	private static final int KEYMODE_LENGTH = 11;
	
	private static final int DEFAULT_FLICK_SENSITIVITY = 100;
	
	private static final int KEYCODE_NOP = -310;
	
	public static final int KEYCODE_KR12_ADDSTROKE = -310;
	
	private static final int INVALID_KEYMODE = -1;
	
	public static final int KEYMODE_HANGUL = 0;
	public static final int KEYMODE_ENGLISH = 0;
	public static final int KEYMODE_ALT_SYMBOLS = 1;
	
	private boolean mCapsLock;
	
	private int mLastInputType = 0;
	private int mLastKeyMode = -1;
	private int mReturnLanguage = -1;
	
	private int[] mCurrentKeyboards = new int[4];
	
	private int[] mLimitedKeyMode = null;
	
	private int mPreferenceKeyMode = INVALID_KEYMODE;
	private int mPreferenceLanguage = INVALID_KEYMODE;
	
	private boolean useFlick = true;
	private int flickSensitivity = DEFAULT_FLICK_SENSITIVITY;
	
	private int timeoutDelay = 250;
	
	int[] languageCycleTable = {
			LANG_EN, LANG_KO
	};
	int mCurrentLanguageIndex = 0;
	
	KeyIconParams[] keyIconParams = {
			new KeyIconParams(
					R.drawable.key_qwerty_shift,
					R.drawable.key_qwerty_enter,
					R.drawable.key_qwerty_space,
					R.drawable.key_qwerty_del),
			new KeyIconParams(
					R.drawable.key_qwerty_shift_b,
					R.drawable.key_qwerty_enter_b,
					R.drawable.key_qwerty_space_b,
					R.drawable.key_qwerty_del_b),
	};
	
	Handler mTimeoutHandler;
	class TimeOutHandler implements Runnable {
		@Override
		public void run() {
			mWnn.onEvent(new OpenWnnEvent(OpenWnnKOKR.TIMEOUT_EVENT));
		}
	}
	
	int ignore = KEYCODE_NOP;
	int longPressTimeout = 500;
	
	SparseArray<Handler> mLongClickHandlers = new SparseArray<>();
	class LongClickHandler implements Runnable {
		int keyCode;
		public LongClickHandler(int keyCode) {
			this.keyCode = keyCode;
		}
		public void run() {
			switch(keyCode) {
			case KEYCODE_QWERTY_SHIFT:
				toggleShiftLock();
				mWnn.onEvent(new OpenWnnEvent(OpenWnnEvent.INPUT_SOFT_KEY,
						new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_SHIFT_LEFT)));
				mCapsLock = true;
				ignore = keyCode;
				return;
			}
			mWnn.onEvent(new OpenWnnEvent(OpenWnnKOKR.LONG_CLICK_EVENT,
					new KeyEvent(KeyEvent.ACTION_DOWN, keyCode)));
			try { mVibrator.vibrate(50); } catch (Exception ex) { }
			ignore = keyCode;
		}
	}
	
	class OnKeyboardViewTouchListener implements View.OnTouchListener {
		float downX, downY;
		float dx, dy;
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch(event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				downX = event.getX();
				downY = event.getY();
				dx = 0;
				dy = 0;
				break;

			case MotionEvent.ACTION_MOVE:
				dx = event.getX() - downX;
				dy = event.getY() - downY;
				if(dy > flickSensitivity || dy < -flickSensitivity
						|| dx < -flickSensitivity || dx > flickSensitivity) {
					for(int i = 0 ; i < mLongClickHandlers.size() ; i++) {
						Handler handler = mLongClickHandlers.get(mLongClickHandlers.keyAt(i));
						handler.removeCallbacksAndMessages(null);
					}
				}
				return true;
				
			case MotionEvent.ACTION_UP:
				if(dy > flickSensitivity) {
					flickDown();
				}
				if(dy < -flickSensitivity) {
					flickUp();
				}
				if(dx < -flickSensitivity) {
					flickLeft();
				}
				if(dx > flickSensitivity) {
					flickRight();
				}
				break;
				
			}
			return false;
		}
	}
	
	public static class KeyIconParams {
		int shift, enter, space, delete;
		public KeyIconParams(int shift, int enter, int space, int delete) {
			this.shift = shift;
			this.enter = enter;
			this.space = space;
			this.delete = delete;
		}
	}
	
	public DefaultSoftKeyboardKOKR(OpenWnn parent) {
		mWnn = parent;
		mCurrentLanguage = languageCycleTable[mCurrentLanguageIndex];
		mCurrentKeyboardType = KEYBOARD_QWERTY;
		mShiftOn = KEYBOARD_SHIFT_OFF;
		
	}
	
	@Override
	protected void createKeyboards(OpenWnn parent) {
		/* Keyboard[# of Languages][portrait/landscape][# of keyboard type][shift off/on][max # of key-modes][subkeyboard] */
		mKeyboard = new Keyboard[4][2][4][2][KEYMODE_LENGTH][4];
		
		if(mHardKeyboardHidden) {
			if(mDisplayMode == PORTRAIT) {
				createKeyboardsPortrait(parent);
			} else {
				createKeyboardsLandscape(parent);
			}
			if(mCurrentKeyboardType == KEYBOARD_12KEY) {
				mWnn.onEvent(new OpenWnnEvent(OpenWnnEvent.CHANGE_MODE,
						OpenWnnKOKR.ENGINE_MODE_OPT_TYPE_12KEY));
			} else {
				mWnn.onEvent(new OpenWnnEvent(OpenWnnEvent.CHANGE_MODE,
						OpenWnnKOKR.ENGINE_MODE_OPT_TYPE_QWERTY));
			}
		} else {
			if(mDisplayMode == PORTRAIT) {
				createKeyboardsPortrait(parent);
			} else {
				createKeyboardsLandscape(parent);
			}
			mWnn.onEvent(new OpenWnnEvent(OpenWnnEvent.CHANGE_MODE,
					OpenWnnKOKR.ENGINE_MODE_OPT_TYPE_QWERTY));
		}
		
	}

	public void changeKeyMode(int keyMode) {
		int targetMode = filterKeyMode(keyMode);
		if(targetMode == INVALID_KEYMODE) {
			return;
		}
		
		mWnn.onEvent(new OpenWnnEvent(OpenWnnEvent.INPUT_SOFT_KEY,
				new KeyEvent(KeyEvent.ACTION_UP,
						KeyEvent.KEYCODE_SHIFT_LEFT)));
		if(mCapsLock) {
			mCapsLock = false;
		}
		mShiftOn = KEYBOARD_SHIFT_OFF;
		Keyboard kbd = getModeChangeKeyboard(targetMode);
		mCurrentKeyMode = targetMode;
		
		int mode = OpenWnnEvent.Mode.DIRECT;
		
		if(targetMode == KEYMODE_HANGUL || targetMode == KEYMODE_ENGLISH) {
			if(mCurrentKeyboardType == KEYBOARD_QWERTY) {
				switch(mCurrentKeyboards[mCurrentLanguage]) {
				case KEYBOARD_KO_DUBUL_STANDARD:
					mode = OpenWnnKOKR.ENGINE_MODE_DUBULSIK;
					break;
					
				case KEYBOARD_KO_SEBUL_390:
					mode = OpenWnnKOKR.ENGINE_MODE_SEBUL_390;
					break;
					
				case KEYBOARD_KO_SEBUL_391:
					mode = OpenWnnKOKR.ENGINE_MODE_SEBUL_391;
					break;
					
				case KEYBOARD_KO_SEBUL_DANMOEUM:
					mode = OpenWnnKOKR.ENGINE_MODE_SEBUL_DANMOEUM;
					break;
					
				case KEYBOARD_KO_SEBUL_SHIN_ORIGINAL:
					mode = OpenWnnKOKR.ENGINE_MODE_SEBUL_SHIN_ORIGINAL;
					break;
					
				case KEYBOARD_KO_SEBUL_SHIN_EDIT:
					mode = OpenWnnKOKR.ENGINE_MODE_SEBUL_SHIN_EDIT;
					break;
					
				case KEYBOARD_KO_DUBUL_DANMOEUM_GOOGLE:
					mode = OpenWnnKOKR.ENGINE_MODE_DUBUL_DANMOEUM;
					break;
					
				}
			} else {
				switch(mCurrentKeyboards[mCurrentLanguage]) {
				case KEYBOARD_EN_12KEY_ALPHABET:
					mode = OpenWnnKOKR.ENGINE_MODE_12KEY_ALPHABET;
					break;
					
				case KEYBOARD_EN_ALPHABET_DVORAK:
					mode = OpenWnnKOKR.ENGINE_MODE_ENGLISH_DVORAK;
					break;
					
				case KEYBOARD_KO_SEBUL_MUNHWA:
					mode = OpenWnnKOKR.ENGINE_MODE_12KEY_SEBUL_MUNHWA;
					break;
					
				case KEYBOARD_KO_SEBUL_HANSON:
					mode = OpenWnnKOKR.ENGINE_MODE_12KEY_SEBUL_HANSON;
					break;
					
				}
			}
		} else if(targetMode == KEYMODE_ALT_SYMBOLS) {
			mode = OpenWnnEvent.Mode.DIRECT;
		}
		changeKeyboard(kbd);
		mWnn.onEvent(new OpenWnnEvent(OpenWnnEvent.CHANGE_MODE, mode));
		
		mLastKeyMode = mCurrentKeyMode;
	}
	
	public void setDefaultKeyboard() {
		Locale locale = Locale.getDefault();
		int language = mCurrentLanguage;
		
		if(mReturnLanguage != -1) {
			mCurrentLanguageIndex = mReturnLanguage;
			language = languageCycleTable[mReturnLanguage];
			mReturnLanguage = -1;
		}
		if(mPreferenceLanguage != -1) {
			mReturnLanguage = mCurrentLanguageIndex;
			mCurrentLanguageIndex = mPreferenceLanguage;
			language = languageCycleTable[mPreferenceLanguage];
		}
		mCurrentLanguage = language;
		changeKeyMode(0);
	}

	@Override
	public View initView(OpenWnn parent, int width, int height) {
		View view = super.initView(parent, width, height);
		mKeyboardView.setOnTouchListener(new OnKeyboardViewTouchListener());
		
		return view;
	}

	@Override
	protected boolean changeKeyboard(Keyboard keyboard) {
		return super.changeKeyboard(keyboard);
	}

	@Override
	public void changeKeyboardType(int type) {
		super.changeKeyboardType(type);
	}

	@Override
	public void onKey(int primaryCode, int[] keyCodes) {
		if(mDisableKeyInput) {
			return;
		}
		
		if(mTimeoutHandler != null) {
			mTimeoutHandler.removeCallbacksAndMessages(null);
			mTimeoutHandler = null;
		}
		
		if((primaryCode <= -200 && primaryCode > -300) || (primaryCode <= -2000 && primaryCode > -3000)) {
			if(primaryCode == ignore) {
				ignore = KEYCODE_NOP;
			} else {
				mWnn.onEvent(new OpenWnnEvent(OpenWnnEvent.INPUT_SOFT_KEY,
						new KeyEvent(KeyEvent.ACTION_DOWN, primaryCode)));
				ignore = KEYCODE_NOP;
			}
		}
		switch(primaryCode) {
		case KEYCODE_CHANGE_LANG:
			if(primaryCode == ignore) {
				ignore = KEYCODE_NOP;
				return;
			}
			nextLanguage();
			break;
			
		case KEYCODE_JP12_BACKSPACE:
		case KEYCODE_QWERTY_BACKSPACE:
			mWnn.onEvent(new OpenWnnEvent(OpenWnnEvent.INPUT_SOFT_KEY,
					new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL)));
			break;
			
		case KEYCODE_QWERTY_SHIFT:
			mCapsLock = false;
			toggleShiftLock();
			if(mShiftOn == 0) {
				mWnn.onEvent(new OpenWnnEvent(OpenWnnEvent.INPUT_SOFT_KEY,
						new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_SHIFT_LEFT)));
			} else {
				mWnn.onEvent(new OpenWnnEvent(OpenWnnEvent.INPUT_SOFT_KEY,
						new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_SHIFT_LEFT)));
			}
			break;
			
		case KEYCODE_QWERTY_ALT:
			processAltKey();
			break;
			
		case KEYCODE_JP12_ENTER:
		case KEYCODE_QWERTY_ENTER:
			mWnn.onEvent(new OpenWnnEvent(OpenWnnEvent.INPUT_SOFT_KEY,
					new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER)));
			break;
			
		case KEYCODE_JP12_SPACE:
		case -10:
			mWnn.onEvent(new OpenWnnEvent(OpenWnnEvent.INPUT_SOFT_KEY,
					new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_SPACE)));
			break;
			
		default:
			if(primaryCode >= 0) {
				if(primaryCode == ignore) {
					ignore = KEYCODE_NOP;
					break;
				}
				if(mKeyboardView.isShifted()) {
					primaryCode = Character.toUpperCase(primaryCode);
				}
				mWnn.onEvent(new OpenWnnEvent(OpenWnnEvent.INPUT_CHAR, (char)primaryCode));
				
				switch(mCurrentKeyboards[LANG_KO]) {
				case KEYBOARD_KO_SEBUL_SHIN_ORIGINAL:
				case KEYBOARD_KO_SEBUL_SHIN_EDIT:
					if(mCurrentLanguage == LANG_KO) break;
					
				default:
					if(mKeyboardView.isShifted()) {
						if(!mCapsLock) onKey(KEYCODE_QWERTY_SHIFT, new int[]{KEYCODE_QWERTY_SHIFT});
					}
				}
				ignore = KEYCODE_NOP;
			}
			break;
		}
		if (!mCapsLock && (primaryCode != DefaultSoftKeyboard.KEYCODE_QWERTY_SHIFT)) {
			
		}
		if(mTimeoutHandler == null) {
			mTimeoutHandler = new Handler();
			mTimeoutHandler.postDelayed(new TimeOutHandler(), timeoutDelay);
		}
	}

	@Override
	public void onPress(int x) {
		super.onPress(x);
		if(mCapsLock) return;
		mLongClickHandlers.put(x, new Handler());
		mLongClickHandlers.get(x).postDelayed(new LongClickHandler(x), longPressTimeout);
	}

	@Override
	public void onRelease(int x) {
		super.onRelease(x);
		for(int i = 0 ; i < mLongClickHandlers.size() ; i++) {
			int key = mLongClickHandlers.keyAt(i);
			Handler handler = mLongClickHandlers.get(key);
			handler.removeCallbacksAndMessages(null);
			mLongClickHandlers.remove(key);
		}
	}

	public void flickUp() {
		if(!useFlick) return;
		for(int i = 0 ; i < mLongClickHandlers.size() ; i++) {
			int key = mLongClickHandlers.keyAt(i);
			if(mLongClickHandlers.get(key) != null) {
				mWnn.onEvent(new OpenWnnEvent(OpenWnnKOKR.FLICK_UP_EVENT,
						new KeyEvent(KeyEvent.ACTION_DOWN, key)));
				ignore = key;
			}
		}
	}
	
	public void flickDown() {
		if(!useFlick) return;
		for(int i = 0 ; i < mLongClickHandlers.size() ; i++) {
			int key = mLongClickHandlers.keyAt(i);
			if(mLongClickHandlers.get(key) != null) {
				mWnn.onEvent(new OpenWnnEvent(OpenWnnKOKR.FLICK_DOWN_EVENT,
						new KeyEvent(KeyEvent.ACTION_DOWN, key)));
				ignore = key;
			}
		}
	}
	
	public void flickLeft() {
		if(!useFlick) return;
		for(int i = 0 ; i < mLongClickHandlers.size() ; i++) {
			int key = mLongClickHandlers.keyAt(i);
			if(mLongClickHandlers.get(key) != null) {
				mWnn.onEvent(new OpenWnnEvent(OpenWnnKOKR.FLICK_LEFT_EVENT,
						new KeyEvent(KeyEvent.ACTION_DOWN, key)));
				ignore = key;
			}
		}
	}
	
	public void flickRight() {
		if(!useFlick) return;
		for(int i = 0 ; i < mLongClickHandlers.size() ; i++) {
			int key = mLongClickHandlers.keyAt(i);
			if(mLongClickHandlers.get(key) != null) {
				mWnn.onEvent(new OpenWnnEvent(OpenWnnKOKR.FLICK_RIGHT_EVENT,
						new KeyEvent(KeyEvent.ACTION_DOWN, key)));
				ignore = key;
			}
		}
	}
	
	public void nextLanguage() {
		int language = mCurrentLanguage;
		
		if(++mCurrentLanguageIndex >= languageCycleTable.length) mCurrentLanguageIndex = 0;
		language = languageCycleTable[mCurrentLanguageIndex];
		
		mCurrentLanguage = language;
		
		changeKeyMode(0);
		
	}

	@Override
	protected void processAltKey() {
		int altKeyMode = KEYMODE_ALT_SYMBOLS;
		if(mCurrentKeyboardType == KEYBOARD_12KEY) {
			altKeyMode = KEYMODE_ALT_SYMBOLS;
		}
		if(mCurrentKeyMode == altKeyMode) {
			changeKeyMode(KEYMODE_HANGUL);
		} else {
			changeKeyMode(altKeyMode);
		}
	}

	@Override
	public void setPreferences(SharedPreferences pref, EditorInfo editor) {
		super.setPreferences(pref, editor);
		
		longPressTimeout = pref.getInt("keyboard_long_press_timeout", 500);
		useFlick = pref.getBoolean("keyboard_use_flick", true);
		flickSensitivity = pref.getInt("keyboard_flick_sensitivity", DEFAULT_FLICK_SENSITIVITY);
		timeoutDelay = pref.getInt("keyboard_timeout_delay", 250);
		
		int inputType = editor.inputType;
		if(mHardKeyboardHidden) {
			
		}

		if(mCurrentKeyboardType == KEYBOARD_12KEY) {
			mWnn.onEvent(new OpenWnnEvent(OpenWnnEvent.CHANGE_MODE,
					OpenWnnKOKR.ENGINE_MODE_OPT_TYPE_12KEY));
		} else {
			mWnn.onEvent(new OpenWnnEvent(OpenWnnEvent.CHANGE_MODE,
					OpenWnnKOKR.ENGINE_MODE_OPT_TYPE_QWERTY));
		}
		
		mLimitedKeyMode = null;
		mPreferenceKeyMode = INVALID_KEYMODE;
		mPreferenceLanguage = -1;
		mNoInput = true;
		mDisableKeyInput = false;
		mCapsLock = false;
		
		switch(inputType & EditorInfo.TYPE_MASK_CLASS) {
		case EditorInfo.TYPE_CLASS_NUMBER:
		case EditorInfo.TYPE_CLASS_DATETIME:
			
			break;
		case EditorInfo.TYPE_CLASS_TEXT:
			switch(inputType & EditorInfo.TYPE_MASK_VARIATION) {
			case EditorInfo.TYPE_TEXT_VARIATION_PASSWORD:
			case EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD:
				mPreferenceLanguage = languageCycleTable[0];
				break;
				
			case EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS:
			case EditorInfo.TYPE_TEXT_VARIATION_URI:
				mPreferenceLanguage = languageCycleTable[0];
				break;
				
			default:
				break;
			}
			break;
			
		default:
			break;
		}
		
		if(inputType != mLastInputType) {
			mLastInputType = inputType;
		}
		setDefaultKeyboard();
	}

	private void createKeyboardsPortrait(OpenWnn parent) {
		
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mWnn);
		
		Keyboard[][][] keyList;
		
		boolean use12key = pref.getBoolean("keyboard_hangul_use_12key", false);
		boolean useAlphabetQwerty = pref.getBoolean("keyboard_alphabet_use_qwerty", true);
		
		if(!mHardKeyboardHidden) use12key = false;
		
		if(use12key) {
			keyList = mKeyboard[LANG_KO][PORTRAIT][KEYBOARD_12KEY];
			
			mCurrentKeyboardType = KEYBOARD_12KEY;
			String defaultLayout = pref.getString("keyboard_hangul_12key_layout", "keyboard_12key_sebul_munhwa");
			switch(defaultLayout) {
			case "keyboard_12key_sebul_munhwa":
				keyList[KEYBOARD_SHIFT_OFF][KEYMODE_HANGUL][0] = loadKeyboard(mWnn, R.xml.keyboard_ko_12key_sebul_munhwa);
				mCurrentKeyboards[LANG_KO] = KEYBOARD_KO_SEBUL_MUNHWA;
				break;
				
			case "keyboard_12key_sebul_hanson":
				keyList[KEYBOARD_SHIFT_OFF][KEYMODE_HANGUL][0] = loadKeyboard(mWnn, R.xml.keyboard_ko_12key_sebul_hanson);
				mCurrentKeyboards[LANG_KO] = KEYBOARD_KO_SEBUL_HANSON;
				break;
				
			}
		} else {

			keyList = mKeyboard[LANG_KO][PORTRAIT][KEYBOARD_QWERTY];
			
			mCurrentKeyboardType = KEYBOARD_QWERTY;
			useAlphabetQwerty = true;
			String defaultLayout = "keyboard_sebul_391";
			if(!mHardKeyboardHidden) {
				defaultLayout = pref.getString("hardware_hangul_layout", "keyboard_sebul_391");
			} else {
				defaultLayout = pref.getString("keyboard_hangul_layout", "keyboard_sebul_391");
			}
			switch(defaultLayout) {
			case "keyboard_sebul_390":
				keyList[KEYBOARD_SHIFT_OFF][KEYMODE_HANGUL][0] = loadKeyboard(mWnn, R.xml.keyboard_ko_sebul_390);
				keyList[KEYBOARD_SHIFT_ON][KEYMODE_HANGUL][0] = loadKeyboard(mWnn, R.xml.keyboard_ko_sebul_390_shift);
				mCurrentKeyboards[LANG_KO] = KEYBOARD_KO_SEBUL_390;
				break;
				
			case "keyboard_sebul_391":
				keyList[KEYBOARD_SHIFT_OFF][KEYMODE_HANGUL][0] = loadKeyboard(mWnn, R.xml.keyboard_ko_sebul_391);
				keyList[KEYBOARD_SHIFT_ON][KEYMODE_HANGUL][0] = loadKeyboard(mWnn, R.xml.keyboard_ko_sebul_391_shift);
				mCurrentKeyboards[LANG_KO] = KEYBOARD_KO_SEBUL_391;
				break;
				
			case "keyboard_sebul_danmoeum":
				keyList[KEYBOARD_SHIFT_OFF][KEYMODE_HANGUL][0] = loadKeyboard(mWnn, R.xml.keyboard_ko_sebul_danmoeum);
				keyList[KEYBOARD_SHIFT_ON][KEYMODE_HANGUL][0] = loadKeyboard(mWnn, R.xml.keyboard_ko_sebul_danmoeum_shift);
				mCurrentKeyboards[LANG_KO] = KEYBOARD_KO_SEBUL_DANMOEUM;
				break;
				
			case "keyboard_dubul_standard":
				keyList[KEYBOARD_SHIFT_OFF][KEYMODE_HANGUL][0] = loadKeyboard(mWnn, R.xml.keyboard_ko_dubul_standard);
				keyList[KEYBOARD_SHIFT_ON][KEYMODE_HANGUL][0] = loadKeyboard(mWnn, R.xml.keyboard_ko_dubul_standard_shift);
				mCurrentKeyboards[LANG_KO] = KEYBOARD_KO_DUBUL_STANDARD;
				break;
				
			case "keyboard_sebul_shin_original":
				keyList[KEYBOARD_SHIFT_OFF][KEYMODE_HANGUL][0] = loadKeyboard(mWnn, R.xml.keyboard_ko_sebul_shin_original);
				keyList[KEYBOARD_SHIFT_ON][KEYMODE_HANGUL][0] = loadKeyboard(mWnn, R.xml.keyboard_ko_sebul_shin_original_shift);
				mCurrentKeyboards[LANG_KO] = KEYBOARD_KO_SEBUL_SHIN_ORIGINAL;
				break;
				
			case "keyboard_sebul_shin_edit":
				keyList[KEYBOARD_SHIFT_OFF][KEYMODE_HANGUL][0] = loadKeyboard(mWnn, R.xml.keyboard_ko_sebul_shin_edit);
				keyList[KEYBOARD_SHIFT_ON][KEYMODE_HANGUL][0] = loadKeyboard(mWnn, R.xml.keyboard_ko_sebul_shin_edit_shift);
				mCurrentKeyboards[LANG_KO] = KEYBOARD_KO_SEBUL_SHIN_EDIT;
				break;
				
			case "keyboard_dubul_danmoeum_google":
				keyList[KEYBOARD_SHIFT_OFF][KEYMODE_HANGUL][0] = loadKeyboard(mWnn, R.xml.keyboard_ko_dubul_danmoeum_google);
				mCurrentKeyboards[LANG_KO] = KEYBOARD_KO_DUBUL_DANMOEUM_GOOGLE;
				break;
				
			}
		}
		
		keyList[KEYBOARD_SHIFT_OFF][KEYMODE_ALT_SYMBOLS][0] = loadKeyboard(mWnn, R.xml.keyboard_ko_alt_symbols);
		
		if(useAlphabetQwerty) {
			
			keyList = mKeyboard[LANG_EN][PORTRAIT][mCurrentKeyboardType];
			
			String defaultLayout = "keyboard_alphabet_qwerty";
			if(!mHardKeyboardHidden) {
				defaultLayout = pref.getString("hardware_alphabet_layout", "keyboard_alphabet_qwerty");
			} else {
				defaultLayout = pref.getString("keyboard_alphabet_layout", "keyboard_alphabet_qwerty");
			}
			switch(defaultLayout) {
			case "keyboard_alphabet_qwerty":
				keyList[KEYBOARD_SHIFT_OFF][KEYMODE_ENGLISH][0] = loadKeyboard(mWnn, R.xml.keyboard_ko_english_qwerty);
				keyList[KEYBOARD_SHIFT_ON][KEYMODE_ENGLISH][0] = loadKeyboard(mWnn, R.xml.keyboard_ko_english_qwerty_shift);
				mCurrentKeyboards[LANG_EN] = KEYBOARD_EN_ALPHABET_QWERTY;
				break;
				
			case "keyboard_alphabet_dvorak":
				keyList[KEYBOARD_SHIFT_OFF][KEYMODE_ENGLISH][0] = loadKeyboard(mWnn, R.xml.keyboard_ko_english_dvorak);
				keyList[KEYBOARD_SHIFT_ON][KEYMODE_ENGLISH][0] = loadKeyboard(mWnn, R.xml.keyboard_ko_english_dvorak_shift);
				mCurrentKeyboards[LANG_EN] = KEYBOARD_EN_ALPHABET_DVORAK;
				break;
				
			}
		} else {
			
			keyList = mKeyboard[LANG_EN][PORTRAIT][mCurrentKeyboardType];
			
			keyList[KEYBOARD_SHIFT_OFF][KEYMODE_ENGLISH][0] = loadKeyboard(mWnn, R.xml.keyboard_ko_12key_english);
			keyList[KEYBOARD_SHIFT_ON][KEYMODE_ENGLISH][0] = loadKeyboard(mWnn, R.xml.keyboard_ko_12key_english_shift);
			mCurrentKeyboards[LANG_EN] = KEYBOARD_EN_12KEY_ALPHABET;
		}
		
		keyList[KEYBOARD_SHIFT_OFF][KEYMODE_ALT_SYMBOLS][0] = loadKeyboard(mWnn, R.xml.keyboard_ko_alt_symbols);
		
	}

	private void createKeyboardsLandscape(OpenWnn parent) {
		
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mWnn);
		
		Keyboard[][][] keyList;
		
		{
			
			keyList = mKeyboard[LANG_KO][LANDSCAPE][KEYBOARD_QWERTY];
			
			mCurrentKeyboardType = KEYBOARD_QWERTY;
			String defaultLayout = "keyboard_sebul_391";
			if(!mHardKeyboardHidden) {
				defaultLayout = pref.getString("hardware_hangul_layout", "keyboard_sebul_391");
			} else {
				defaultLayout = pref.getString("keyboard_hangul_layout", "keyboard_sebul_391");
			}
			switch(defaultLayout) {
			case "keyboard_sebul_390":
				keyList[KEYBOARD_SHIFT_OFF][KEYMODE_HANGUL][0] = loadKeyboard(mWnn, R.xml.keyboard_ko_sebul_390);
				keyList[KEYBOARD_SHIFT_ON][KEYMODE_HANGUL][0] = loadKeyboard(mWnn, R.xml.keyboard_ko_sebul_390_shift);
				mCurrentKeyboards[LANG_KO] = KEYBOARD_KO_SEBUL_390;
				break;
				
			case "keyboard_sebul_391":
				keyList[KEYBOARD_SHIFT_OFF][KEYMODE_HANGUL][0] = loadKeyboard(mWnn, R.xml.keyboard_ko_sebul_391);
				keyList[KEYBOARD_SHIFT_ON][KEYMODE_HANGUL][0] = loadKeyboard(mWnn, R.xml.keyboard_ko_sebul_391_shift);
				mCurrentKeyboards[LANG_KO] = KEYBOARD_KO_SEBUL_391;
				break;
				
			case "keyboard_sebul_danmoeum":
				keyList[KEYBOARD_SHIFT_OFF][KEYMODE_HANGUL][0] = loadKeyboard(mWnn, R.xml.keyboard_ko_sebul_danmoeum);
				keyList[KEYBOARD_SHIFT_ON][KEYMODE_HANGUL][0] = loadKeyboard(mWnn, R.xml.keyboard_ko_sebul_danmoeum_shift);
				mCurrentKeyboards[LANG_KO] = KEYBOARD_KO_SEBUL_DANMOEUM;
				break;
				
			case "keyboard_dubul_standard":
				keyList[KEYBOARD_SHIFT_OFF][KEYMODE_HANGUL][0] = loadKeyboard(mWnn, R.xml.keyboard_ko_dubul_standard);
				keyList[KEYBOARD_SHIFT_ON][KEYMODE_HANGUL][0] = loadKeyboard(mWnn, R.xml.keyboard_ko_dubul_standard_shift);
				mCurrentKeyboards[LANG_KO] = KEYBOARD_KO_DUBUL_STANDARD;
				break;
				
			case "keyboard_sebul_shin_original":
				keyList[KEYBOARD_SHIFT_OFF][KEYMODE_HANGUL][0] = loadKeyboard(mWnn, R.xml.keyboard_ko_sebul_shin_original);
				keyList[KEYBOARD_SHIFT_ON][KEYMODE_HANGUL][0] = loadKeyboard(mWnn, R.xml.keyboard_ko_sebul_shin_original_shift);
				mCurrentKeyboards[LANG_KO] = KEYBOARD_KO_SEBUL_SHIN_ORIGINAL;
				break;
				
			case "keyboard_sebul_shin_edit":
				keyList[KEYBOARD_SHIFT_OFF][KEYMODE_HANGUL][0] = loadKeyboard(mWnn, R.xml.keyboard_ko_sebul_shin_edit);
				keyList[KEYBOARD_SHIFT_ON][KEYMODE_HANGUL][0] = loadKeyboard(mWnn, R.xml.keyboard_ko_sebul_shin_edit_shift);
				mCurrentKeyboards[LANG_KO] = KEYBOARD_KO_SEBUL_SHIN_EDIT;
				break;
				
			case "keyboard_dubul_danmoeum_google":
				keyList[KEYBOARD_SHIFT_OFF][KEYMODE_HANGUL][0] = loadKeyboard(mWnn, R.xml.keyboard_ko_dubul_danmoeum_google);
				mCurrentKeyboards[LANG_KO] = KEYBOARD_KO_DUBUL_DANMOEUM_GOOGLE;
				break;
				
			}
		}
		
		keyList[KEYBOARD_SHIFT_OFF][KEYMODE_ALT_SYMBOLS][0] = loadKeyboard(mWnn, R.xml.keyboard_ko_alt_symbols);
		
		{
			
			keyList = mKeyboard[LANG_EN][LANDSCAPE][KEYBOARD_QWERTY];
			
			String defaultLayout = "keyboard_alphabet_qwerty";
			if(!mHardKeyboardHidden) {
				defaultLayout = pref.getString("hardware_alphabet_layout", "keyboard_alphabet_qwerty");
			} else {
				defaultLayout = pref.getString("keyboard_alphabet_layout", "keyboard_alphabet_qwerty");
			}
			switch(defaultLayout) {
			case "keyboard_alphabet_qwerty":
				keyList[KEYBOARD_SHIFT_OFF][KEYMODE_ENGLISH][0] = loadKeyboard(mWnn, R.xml.keyboard_ko_english_qwerty);
				keyList[KEYBOARD_SHIFT_ON][KEYMODE_ENGLISH][0] = loadKeyboard(mWnn, R.xml.keyboard_ko_english_qwerty_shift);
				mCurrentKeyboards[LANG_EN] = KEYBOARD_EN_ALPHABET_QWERTY;
				break;
				
			case "keyboard_alphabet_dvorak":
				keyList[KEYBOARD_SHIFT_OFF][KEYMODE_ENGLISH][0] = loadKeyboard(mWnn, R.xml.keyboard_ko_english_dvorak);
				keyList[KEYBOARD_SHIFT_ON][KEYMODE_ENGLISH][0] = loadKeyboard(mWnn, R.xml.keyboard_ko_english_dvorak_shift);
				mCurrentKeyboards[LANG_EN] = KEYBOARD_EN_ALPHABET_DVORAK;
				break;
				
			}
		}
		
		keyList[KEYBOARD_SHIFT_OFF][KEYMODE_ALT_SYMBOLS][0] = loadKeyboard(mWnn, R.xml.keyboard_ko_alt_symbols);
		
	}
	
	@SuppressWarnings("deprecation")
	public Keyboard loadKeyboard(Context context, int xmlLayoutResId) {
		Keyboard keyboard = new Keyboard(context, xmlLayoutResId);
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mWnn);
		String skin = pref.getString("keyboard_skin",
				mWnn.getResources().getString(R.string.keyboard_skin_id_default));
		int icon = 0;
		switch(skin) {
		case "keyboard_white":
			icon = 1;
			break;
			
		default:
			icon = 0;
		}
		KeyIconParams params = keyIconParams[icon];
		for(Keyboard.Key key : keyboard.getKeys()) {
			switch(key.codes[0]) {
			case KEYCODE_QWERTY_SHIFT:
				key.icon = mWnn.getResources().getDrawable(params.shift);
				break;
				
			case KEYCODE_QWERTY_ENTER:
				key.icon = mWnn.getResources().getDrawable(params.enter);
				break;
				
			case -10:
				key.icon = mWnn.getResources().getDrawable(params.space);
				break;
				
			case KEYCODE_QWERTY_BACKSPACE:
				key.icon = mWnn.getResources().getDrawable(params.delete);
				break;
			}
		}
		
		return keyboard;
	}

	private int filterKeyMode(int keyMode) {
		int targetMode = keyMode;
		int[] limits = mLimitedKeyMode;
		
		if(limits != null) {
			boolean hasAccepted = false;
			boolean hasRequiredChange = false;
			int size = limits.length;
			int nowMode = mCurrentKeyMode;
			
			for(int i = 0 ; i < size ; i++) {
				if(targetMode == limits[i]) {
					hasAccepted = true;
					break;
				}
				if(nowMode == limits[i]) {
					hasRequiredChange = false;
				}
			}
			
			if(!hasAccepted) {
				if(hasRequiredChange) {
					targetMode = mLimitedKeyMode[0];
				} else {
					targetMode = INVALID_KEYMODE;
				}
			}
		}
		return targetMode;
	}

}
