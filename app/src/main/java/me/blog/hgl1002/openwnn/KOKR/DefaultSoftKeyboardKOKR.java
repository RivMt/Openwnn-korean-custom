package me.blog.hgl1002.openwnn.KOKR;

import java.util.Locale;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import me.blog.hgl1002.openwnn.DefaultSoftKeyboard;
import me.blog.hgl1002.openwnn.OpenWnn;
import me.blog.hgl1002.openwnn.event.*;
import me.blog.hgl1002.openwnn.OpenWnnKOKR;
import me.blog.hgl1002.openwnn.R;
import me.blog.hgl1002.openwnn.event.SoftKeyLongPressEvent;

public class DefaultSoftKeyboardKOKR extends DefaultSoftKeyboard {

	public static final int HARD_KEYMODE_LANG = 10000;
	public static final int HARD_KEYMODE_LANG_ENGLISH = HARD_KEYMODE_LANG + LANG_EN;
	public static final int HARD_KEYMODE_LANG_KOREAN = HARD_KEYMODE_LANG + LANG_KO;

	private static final int KEYMODE_LENGTH = 11;
	
	protected static final int DEFAULT_FLICK_SENSITIVITY = 100;

	protected static final int SPACE_SLIDE_UNIT = 30;
	protected static final int BACKSPACE_SLIDE_UNIT = 250;
	
	protected static final int KEYCODE_NOP = -310;
	
	public static final int KEYCODE_KR12_ADDSTROKE = -310;

	public static final int KEYCODE_RIGHT = -217;
	public static final int KEYCODE_LEFT = -218;
	public static final int KEYCODE_DOWN = -219;
	public static final int KEYCODE_UP = -220;

	public static final int KEYCODE_NON_SHIN_DEL = -510;
	public static final int KEYCODE_TOGGLE_ONE_HAND_SIDE = -520;

	protected static final int INVALID_KEYMODE = -1;
	
	public static final int KEYMODE_HANGUL = 1;
	public static final int KEYMODE_HANGUL_CHO = 2;
	public static final int KEYMODE_HANGUL_JUNG = 3;
	public static final int KEYMODE_HANGUL_JONG = 4;
	public static final int KEYMODE_ENGLISH = 1;
	public static final int KEYMODE_ALT_SYMBOLS = 0;

	protected KeyboardView mNumKeyboardView;
	protected Keyboard[][][][][][] mNumKeyboard;

	protected boolean mCapsLock;
	
	protected int mLastInputType = 0;
	protected int mLastKeyMode = -1;
	protected int mReturnLanguage = -1;

	protected EngineMode[] mCurrentKeyboards;
	protected EngineMode mAltKeyMode;
	
	protected int[] mLimitedKeyMode = null;
	
	protected int mPreferenceKeyMode = INVALID_KEYMODE;
	protected int mPreferenceLanguage = INVALID_KEYMODE;

	protected boolean mHardwareLayout;

	protected boolean mUse12Key = false;
	protected boolean mUseAlphabetQwerty = true;
	
	protected boolean mUseFlick = true;
	protected int mFlickSensitivity = DEFAULT_FLICK_SENSITIVITY;
	protected int mSpaceSlideSensitivity = DEFAULT_FLICK_SENSITIVITY;
	
	protected int mTimeoutDelay = 500;
	
	protected int mVibrateDuration = 30;

	protected int mKeyHeightPortrait = 50;
	protected int mKeyHeightLandscape = 42;

	protected boolean mOneHandedMode;
	protected int mOneHandedRatio;
	protected boolean mOneHandedSide;

	public static final boolean ONE_HAND_LEFT = false;
	public static final boolean ONE_HAND_RIGHT = true;
	
	protected boolean mShowSubView = true;

	protected boolean mShowNumKeyboardViewPortrait = true;
	protected boolean mShowNumKeyboardViewLandscape = true;

	protected boolean mShowKeyPreview = false;

	protected boolean mForceHangul;
	
	protected int[] mLanguageCycleTable = {
			LANG_EN, LANG_KO
	};
	int mCurrentLanguageIndex = 0;

	SparseArray<SparseArray<Integer>> mKeyIcons = new SparseArray<SparseArray<Integer>>() {{
		put(0, new SparseArray<Integer>() {{
			put(KEYCODE_QWERTY_SHIFT, R.drawable.key_qwerty_shift);
			put(KEYCODE_QWERTY_ENTER, R.drawable.key_qwerty_enter);
			put(-10, R.drawable.key_qwerty_space);
			put(KEYCODE_QWERTY_BACKSPACE, R.drawable.key_qwerty_del);
			put(KEYCODE_JP12_ENTER, R.drawable.key_12key_enter);
			put(KEYCODE_JP12_SPACE, R.drawable.key_12key_space);
			put(KEYCODE_JP12_BACKSPACE, R.drawable.key_12key_del);
		}});
		put(1, new SparseArray<Integer>() {{
			put(KEYCODE_QWERTY_SHIFT, R.drawable.key_qwerty_shift_b);
			put(KEYCODE_QWERTY_ENTER, R.drawable.key_qwerty_enter_b);
			put(-10, R.drawable.key_qwerty_space_b);
			put(KEYCODE_QWERTY_BACKSPACE, R.drawable.key_qwerty_del_b);
			put(KEYCODE_JP12_ENTER, R.drawable.key_12key_enter_b);
			put(KEYCODE_JP12_SPACE, R.drawable.key_12key_space_b);
			put(KEYCODE_JP12_BACKSPACE, R.drawable.key_12key_del_b);
		}});
	}};

	Handler mTimeoutHandler;
	class TimeOutHandler implements Runnable {
		@Override
		public void run() {
			EventBus.getDefault().post(new InputTimeoutEvent());
		}
	}

	int mLongPressTimeout = 500;
	
	class LongClickHandler implements Runnable {
		int keyCode;
		boolean performed = false;
		public LongClickHandler(int keyCode) {
			this.keyCode = keyCode;
		}
		public void run() {
			setPreviewEnabled(keyCode);
			switch(keyCode) {
			case KEYCODE_QWERTY_SHIFT:
				if(mShiftOn > 0) return;
				toggleShiftLock();
				EventBus.getDefault().post(new InputSoftKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_SHIFT_LEFT)));
				mCapsLock = true;
				performed = true;
				updateKeyLabels();
				return;

			case KEYCODE_JP12_BACKSPACE:
			case KEYCODE_QWERTY_BACKSPACE:
				mBackspaceLongClickHandler.postDelayed(new BackspaceLongClickHandler(), 50);
				return;
			}
			EventBus.getDefault().post(new SoftKeyLongPressEvent(keyCode));
			try { mVibrator.vibrate(mVibrateDuration*2); } catch (Exception ex) { }
			performed = true;
		}
	}

	Handler mBackspaceLongClickHandler = new Handler();
	class BackspaceLongClickHandler implements Runnable {
		@Override
		public void run() {
			EventBus.getDefault().post(new InputSoftKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KEYCODE_NON_SHIN_DEL)));
			mBackspaceLongClickHandler.postDelayed(new BackspaceLongClickHandler(), 50);
		}
	}

	private SparseArray<TouchPoint> mTouchPoints = new SparseArray<>();
	class TouchPoint {
		Keyboard.Key key;
		int keyCode;

		float downX, downY;
		float dx, dy;
		float beforeX, beforeY;
		int space = -1;
		int spaceDistance;
		int backspace = -1;
		int backspaceDistance;

		LongClickHandler longClickHandler;
		Handler handler;

		public TouchPoint(Keyboard.Key key, float downX, float downY) {
			this.key = key;
			this.keyCode = key.codes[0];
			this.downX = downX;
			this.downY = downY;

			key.onPressed();
			mKeyboardView.invalidateAllKeys();

			setPreviewEnabled(keyCode);

			handler = new Handler();
			handler.postDelayed(longClickHandler = new LongClickHandler(keyCode), mLongPressTimeout);

			/* key click sound & vibration */
			if (mVibrator != null) {
				try { mVibrator.vibrate(mVibrateDuration); } catch (Exception ex) { }
			}
			if (mSound != null) {
				try { mSound.seekTo(0); mSound.start(); } catch (Exception ex) { }
			}
		}

		public boolean onMove(float x, float y) {
			dx = x - downX;
			dy = y - downY;
			switch(keyCode) {
			case KEYCODE_JP12_SPACE:
			case -10:
				if(Math.abs(dx) >= mSpaceSlideSensitivity) space = keyCode;
				break;

			case KEYCODE_JP12_BACKSPACE:
			case KEYCODE_QWERTY_BACKSPACE:
				if(Math.abs(dx) >= BACKSPACE_SLIDE_UNIT) {
					backspace = keyCode;
					mBackspaceLongClickHandler.removeCallbacksAndMessages(null);
				}
				break;

			default:
				space = -1;
				backspace = -1;
				break;
			}
			if(dy > mFlickSensitivity || dy < -mFlickSensitivity
					|| dx < -mFlickSensitivity || dx > mFlickSensitivity || space != -1) {
				handler.removeCallbacksAndMessages(null);
			}
			if(space != -1) {
				spaceDistance += x - beforeX;
				if(spaceDistance < -SPACE_SLIDE_UNIT) {
					spaceDistance = 0;
					EventBus.getDefault().post(new InputSoftKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_LEFT)));
					EventBus.getDefault().post(new InputSoftKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_LEFT)));
				}
				if(spaceDistance > +SPACE_SLIDE_UNIT) {
					spaceDistance = 0;
					EventBus.getDefault().post(new InputSoftKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_RIGHT)));
					EventBus.getDefault().post(new InputSoftKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_RIGHT)));
				}
			}
			if(backspace != -1) {
				backspaceDistance += x - beforeX;
				if(backspaceDistance < -BACKSPACE_SLIDE_UNIT) {
					backspaceDistance = 0;
					EventBus.getDefault().post(new SoftKeyGestureEvent(KeyEvent.KEYCODE_DEL, SoftKeyGestureEvent.Type.SLIDE_LEFT));
				}
				if(backspaceDistance > +BACKSPACE_SLIDE_UNIT) {
					backspaceDistance = 0;
					EventBus.getDefault().post(new SoftKeyGestureEvent(KeyEvent.KEYCODE_DEL, SoftKeyGestureEvent.Type.SLIDE_RIGHT));
				}
			}
			beforeX = x;
			beforeY = y;
			return true;
		}

		public boolean onUp() {
			key.onReleased(true);
			mKeyboardView.setPreviewEnabled(false);
			mBackspaceLongClickHandler.removeCallbacksAndMessages(null);
			mKeyboardView.invalidateAllKeys();
			handler.removeCallbacksAndMessages(null);
			if(space != -1) {
				space = -1;
				return false;
			}
			if(backspace != -1) {
				EventBus.getDefault().post(new SoftKeyGestureEvent(KeyEvent.KEYCODE_DEL, SoftKeyGestureEvent.Type.RELEASE));
				backspace = -1;
				return false;
			}
			// Swipe Detection
			if(dx < -mFlickSensitivity*5) {
				if(Math.abs(dx) > Math.abs(dy)) {
					swipeLeft();
				}
				return false;
			}
			if(dx > mFlickSensitivity*5) {
				if(Math.abs(dx) > Math.abs(dy)) {
					swipeRight();
				}
				return false;
			}

			//Flick detection
			if(dy > mFlickSensitivity) {
				if(Math.abs(dy) > Math.abs(dx)) {
					EventBus.getDefault().post(new SoftKeyFlickEvent(keyCode, SoftKeyFlickEvent.Direction.DOWN));
				}
				return false;
			}
			if(dy < -mFlickSensitivity) {
				if(Math.abs(dy) > Math.abs(dx)) {
					EventBus.getDefault().post(new SoftKeyFlickEvent(keyCode, SoftKeyFlickEvent.Direction.UP));
				}
				return false;
			}
			if(dx < -mFlickSensitivity) {
				if(Math.abs(dx) > Math.abs(dy)) {
					EventBus.getDefault().post(new SoftKeyFlickEvent(keyCode, SoftKeyFlickEvent.Direction.LEFT));
				}
				return false;
			}
			if(dx > mFlickSensitivity) {
				if(Math.abs(dx) > Math.abs(dy)) {
					EventBus.getDefault().post(new SoftKeyFlickEvent(keyCode, SoftKeyFlickEvent.Direction.RIGHT));
				}
				return false;
			}
			if(!longClickHandler.performed) onKey(keyCode);
			return false;
		}

	}
	
	class OnKeyboardViewTouchListener implements View.OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if(Build.VERSION.SDK_INT >= 8) {
				int pointerIndex = event.getActionIndex();
				int pointerId = event.getPointerId(pointerIndex);
				int action = event.getActionMasked();
				float x = event.getX(pointerIndex), y = event.getY(pointerIndex);
				switch(action) {
				case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_POINTER_DOWN:
					TouchPoint point = new TouchPoint(findKey(mCurrentKeyboard, (int) x, (int) y), x, y);
					mTouchPoints.put(pointerId, point);
					return true;

				case MotionEvent.ACTION_MOVE:
					return mTouchPoints.get(pointerId).onMove(x, y);

				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_POINTER_UP:
					mTouchPoints.get(pointerId).onUp();
					mTouchPoints.remove(pointerId);
					return true;

				}
			} else {
				float x = event.getX(), y = event.getY();
				switch(event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					TouchPoint point = new TouchPoint(findKey(mCurrentKeyboard, (int) x, (int) y), x, y);
					mTouchPoints.put(0, point);
					return true;

				case MotionEvent.ACTION_MOVE:
					return mTouchPoints.get(0).onMove(x, y);

				case MotionEvent.ACTION_UP:
					mTouchPoints.get(0).onUp();
					mTouchPoints.remove(0);
					return true;

				}
			}
			return false;
		}

		private Keyboard.Key findKey(Keyboard keyboard, int x, int y) {
			for(Keyboard.Key key : keyboard.getKeys()) {
				if(key.isInside(x, y)) return key;
			}
			return null;
		}

	}

	public DefaultSoftKeyboardKOKR(OpenWnn parent) {
		mWnn = parent;
		mCurrentLanguage = mLanguageCycleTable[mCurrentLanguageIndex];
		mCurrentKeyboardType = KEYBOARD_QWERTY;
		mShiftOn = KEYBOARD_SHIFT_OFF;
		
	}
	
	@Override
	protected void createKeyboards(OpenWnn parent) {
		/* Keyboard[# of Languages][portrait/landscape][# of keyboard type][shift off/on][max # of key-modes][subkeyboard] */
		mKeyboard = new Keyboard[4][2][4][2][KEYMODE_LENGTH][4];
		mNumKeyboard = new Keyboard[4][2][4][2][1][4];

		mCurrentKeyboards = new EngineMode[4];

		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mWnn);

		Keyboard[][][] keyList;

		mUse12Key = pref.getBoolean("keyboard_hangul_use_12key", false);
		mUseAlphabetQwerty = pref.getBoolean("keyboard_alphabet_use_qwerty", true);

		boolean use12Key = mUse12Key, useAlphabetQwerty = mUseAlphabetQwerty;

		if(!mHardKeyboardHidden) {
			mHardwareLayout = true;
			use12Key = false;
		} else {
			mHardwareLayout = false;
		}

		if(mDisplayMode == LANDSCAPE) {
			use12Key = false;
		}

		if(use12Key) {
			keyList = mKeyboard[LANG_KO][mDisplayMode][KEYBOARD_12KEY];

			mCurrentKeyboardType = KEYBOARD_12KEY;
			String defaultLayout = pref.getString("keyboard_hangul_12key_layout", "keyboard_12key_sebul_munhwa");

			switch(defaultLayout) {
			case "keyboard_12key_dubul_cheonjiin":
			case "keyboard_12key_dubul_cheonjiin_predictive":
				keyList[KEYBOARD_SHIFT_OFF][KEYMODE_HANGUL][0] = loadKeyboardLayout(mWnn, R.xml.keyboard_ko_12key_dubul_cheonjiin);
				break;

			case "keyboard_12key_dubul_naratgeul":
			case "keyboard_12key_dubul_naratgeul_predictive":
				keyList[KEYBOARD_SHIFT_OFF][KEYMODE_HANGUL][0] = loadKeyboardLayout(mWnn, R.xml.keyboard_ko_12key_dubul_naratgeul);
				break;

			case "keyboard_12key_dubul_sky2":
			case "keyboard_12key_dubul_sky2_predictive":
				keyList[KEYBOARD_SHIFT_OFF][KEYMODE_HANGUL][0] = loadKeyboardLayout(mWnn, R.xml.keyboard_ko_12key_dubul_sky2);
				break;

			case "keyboard_dubul_danmoeum_google":
				keyList[KEYBOARD_SHIFT_OFF][KEYMODE_HANGUL][0] = loadKeyboardLayout(mWnn, R.xml.keyboard_ko_dubul_danmoeum_google);
				break;

			case "keyboard_12key_sebul_munhwa":
				keyList[KEYBOARD_SHIFT_OFF][KEYMODE_HANGUL][0] = loadKeyboardLayout(mWnn, R.xml.keyboard_ko_12key_sebul_munhwa);
				break;

			case "keyboard_12key_sebul_hanson":
				keyList[KEYBOARD_SHIFT_OFF][KEYMODE_HANGUL][0] = loadKeyboardLayout(mWnn, R.xml.keyboard_ko_12key_sebul_hanson);
				break;

			case "keyboard_12key_sebul_sena":
				keyList[KEYBOARD_SHIFT_OFF][KEYMODE_HANGUL][0] = loadKeyboardLayout(mWnn, R.xml.keyboard_ko_12key_sebul_sena);
				break;

			}
			mCurrentKeyboards[LANG_KO] = getEngineMode(defaultLayout);

		} else {

			keyList = mKeyboard[LANG_KO][mDisplayMode][KEYBOARD_QWERTY];

			mCurrentKeyboardType = KEYBOARD_QWERTY;
			useAlphabetQwerty = true;
			String defaultLayout = "keyboard_sebul_391";
			if(!mHardKeyboardHidden) {
				if(pref.getBoolean("keyboard_dev_use_hangul_hard", false)) {
					defaultLayout = pref.getString("keyboard_dev_hard_layout", defaultLayout);
				} else {
					defaultLayout = pref.getString("hardware_hangul_layout", defaultLayout);
				}
			} else {
				if(pref.getBoolean("keyboard_dev_use_hangul_soft", false)) {
					defaultLayout = pref.getString("keyboard_dev_soft_layout", defaultLayout);
				} else {
					defaultLayout = pref.getString("keyboard_hangul_layout", defaultLayout);
				}
			}
			mCurrentKeyboards[LANG_KO] = getEngineMode(defaultLayout);

			String softLayout = "l1.2";
			if(mDisplayMode == PORTRAIT) softLayout = pref.getString("keyboard_hangul_soft_layout_portrait", softLayout);
			else softLayout = pref.getString("keyboard_hangul_soft_layout_landscape", softLayout);
			loadSoftLayout(keyList, softLayout);

		}

		//TODO: load these somewhere else
		keyList[KEYBOARD_SHIFT_OFF][KEYMODE_ALT_SYMBOLS][0] = loadKeyboardLayout(mWnn, R.xml.keyboard_ko_l1_1_mobile_num);
		keyList[KEYBOARD_SHIFT_ON][KEYMODE_ALT_SYMBOLS][0] = loadKeyboardLayout(mWnn, R.xml.keyboard_ko_l1_1_mobile_num);
		mAltKeyMode = EngineMode.SYMBOL_B;

		if(useAlphabetQwerty) {

			keyList = mKeyboard[LANG_EN][mDisplayMode][mCurrentKeyboardType];

			String defaultLayout = "keyboard_alphabet_qwerty";
			if(!mHardKeyboardHidden) {
				defaultLayout = pref.getString("hardware_alphabet_layout", "keyboard_alphabet_qwerty");
			} else {
				defaultLayout = pref.getString("keyboard_alphabet_layout", "keyboard_alphabet_qwerty");
			}
			mCurrentKeyboards[LANG_EN] = getEngineMode(defaultLayout);

			String softLayout = "l1.0";
			if(mDisplayMode == PORTRAIT) softLayout = pref.getString("keyboard_alphabet_soft_layout_portrait", softLayout);
			else softLayout = pref.getString("keyboard_alphabet_soft_layout_portrait", softLayout);
			loadSoftLayout(keyList, softLayout);

		} else {

			keyList = mKeyboard[LANG_EN][mDisplayMode][mCurrentKeyboardType];

			String defaultLayout = pref.getString("keyboard_alphabet_12key_layout", "keyboard_12key_alphabet_narrow_a");

			switch(defaultLayout) {
			case "keyboard_12key_alphabet_wide_a":
			case "keyboard_12key_alphabet_wide_a_predictive":
				keyList[KEYBOARD_SHIFT_OFF][KEYMODE_ENGLISH][0] = loadKeyboardLayout(mWnn, R.xml.keyboard_ko_12key_english_wide_a);
				keyList[KEYBOARD_SHIFT_ON][KEYMODE_ENGLISH][0] = loadKeyboardLayout(mWnn, R.xml.keyboard_ko_12key_english_wide_a_shift);
				break;

			case "keyboard_12key_alphabet_wide_b":
			case "keyboard_12key_alphabet_wide_b_predictive":
				keyList[KEYBOARD_SHIFT_OFF][KEYMODE_ENGLISH][0] = loadKeyboardLayout(mWnn, R.xml.keyboard_ko_12key_english_wide_b);
				keyList[KEYBOARD_SHIFT_ON][KEYMODE_ENGLISH][0] = loadKeyboardLayout(mWnn, R.xml.keyboard_ko_12key_english_wide_b_shift);
				break;

			case "keyboard_12key_alphabet_narrow_a":
			case "keyboard_12key_alphabet_narrow_a_predictive":
				keyList[KEYBOARD_SHIFT_OFF][KEYMODE_ENGLISH][0] = loadKeyboardLayout(mWnn, R.xml.keyboard_ko_12key_english_narrow_a);
				keyList[KEYBOARD_SHIFT_ON][KEYMODE_ENGLISH][0] = loadKeyboardLayout(mWnn, R.xml.keyboard_ko_12key_english_narrow_a_shift);
				break;

			case "keyboard_12key_alphabet_narrow_b":
			case "keyboard_12key_alphabet_narrow_b_predictive":
				keyList[KEYBOARD_SHIFT_OFF][KEYMODE_ENGLISH][0] = loadKeyboardLayout(mWnn, R.xml.keyboard_ko_12key_english_narrow_b);
				keyList[KEYBOARD_SHIFT_ON][KEYMODE_ENGLISH][0] = loadKeyboardLayout(mWnn, R.xml.keyboard_ko_12key_english_narrow_b_shift);
				break;

			}
			mCurrentKeyboards[LANG_EN] = getEngineMode(defaultLayout);

		}

		//TODO: load these somewhere else
		keyList[KEYBOARD_SHIFT_OFF][KEYMODE_ALT_SYMBOLS][0] = loadKeyboardLayout(mWnn, R.xml.keyboard_ko_l1_1_mobile_num);
		keyList[KEYBOARD_SHIFT_ON][KEYMODE_ALT_SYMBOLS][0] = loadKeyboardLayout(mWnn, R.xml.keyboard_ko_l1_1_mobile_num);
		mAltKeyMode = EngineMode.SYMBOL_B;

		keyList = mNumKeyboard[LANG_KO][mDisplayMode][mCurrentKeyboardType];
		keyList[KEYBOARD_SHIFT_OFF][0][0] = loadKeyboardLayout(mWnn, R.xml.keyboard_ko_special_number);
		keyList[KEYBOARD_SHIFT_ON][0][0] = loadKeyboardLayout(mWnn, R.xml.keyboard_ko_special_number_shift);

		keyList = mNumKeyboard[LANG_EN][mDisplayMode][mCurrentKeyboardType];
		keyList[KEYBOARD_SHIFT_OFF][0][0] = loadKeyboardLayout(mWnn, R.xml.keyboard_ko_special_number);
		keyList[KEYBOARD_SHIFT_ON][0][0] = loadKeyboardLayout(mWnn, R.xml.keyboard_ko_special_number_shift);

	}

	public void changeKeyMode(int keyMode) {
		int targetMode = filterKeyMode(keyMode);
		if(targetMode == INVALID_KEYMODE) {
			return;
		}

		EventBus.getDefault().post(new InputSoftKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_SHIFT_LEFT)));
		if(mCapsLock) {
			mCapsLock = false;
		}
		mShiftOn = KEYBOARD_SHIFT_OFF;
		Keyboard kbd = getModeChangeKeyboard(targetMode);
		mCurrentKeyMode = targetMode;

		EngineMode mode = EngineMode.DIRECT;
		
		if(targetMode == KEYMODE_HANGUL || targetMode == KEYMODE_ENGLISH) {
			mode = mCurrentKeyboards[mCurrentLanguage];
		} else if(targetMode == KEYMODE_ALT_SYMBOLS) {
			mode = mAltKeyMode;
		}

//		changeEngineOption();
		EventBus.getDefault().post(new EngineModeChangeEvent(mode));

		changeKeyboard(kbd);
		if(mNumKeyboard != null) {
			changeNumKeyboard(mNumKeyboard[mCurrentLanguage][mDisplayMode][mCurrentKeyboardType][mShiftOn][0][0]);
		}

		mLastKeyMode = mCurrentKeyMode;
	}
	
	public void setDefaultKeyboard() {
		if(mForceHangul) {
			mCurrentLanguage = LANG_KO;
			mCurrentLanguageIndex = 1;
			changeKeyMode(KEYMODE_HANGUL);
			return;
		}
		Locale locale = Locale.getDefault();
		int language = mCurrentLanguage;
		
		if(mReturnLanguage != -1) {
			mCurrentLanguageIndex = mReturnLanguage;
			language = mLanguageCycleTable[mReturnLanguage];
			mReturnLanguage = -1;
		}
		if(mPreferenceLanguage != -1) {
			mReturnLanguage = mCurrentLanguageIndex;
			mCurrentLanguageIndex = mPreferenceLanguage;
			language = mLanguageCycleTable[mPreferenceLanguage];
		}
		mCurrentLanguage = language;
		changeKeyMode(KEYMODE_HANGUL);
	}

	@Override
	public View initView(OpenWnn parent, int width, int height) {
		mWnn = parent;
		mDisplayMode =
				(parent.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
						? LANDSCAPE : PORTRAIT;

        /*
         * create keyboards & the view.
         * To re-display the input view when the display mode is changed portrait <-> landscape,
         * create keyboards every time.
         */
		createKeyboards(parent);

		final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(parent);
		String skin = pref.getString("keyboard_skin",
				mWnn.getResources().getString(R.string.keyboard_skin_id_default));
		int id = parent.getResources().getIdentifier(skin, "layout", "me.blog.hgl1002.openwnn");

		mKeyboardView = (KeyboardView) mWnn.getLayoutInflater().inflate(id, null);
		mKeyboardView.setOnKeyboardActionListener(this);
		mCurrentKeyboard = null;

		mNumKeyboardView = (KeyboardView) mWnn.getLayoutInflater().inflate(id, null);
		mNumKeyboardView.setOnKeyboardActionListener(this);

		mMainView = (ViewGroup) parent.getLayoutInflater().inflate(R.layout.keyboard_default_main, null);
		mSubView = (ViewGroup) parent.getLayoutInflater().inflate(R.layout.keyboard_default_sub, null);

		boolean initialLaunch = pref.getBoolean("initial_launch", true);
		if(initialLaunch) {
			final View help = parent.getLayoutInflater().inflate(R.layout.initial_launch_helper, null);
			mMainView.addView(help);
			Button close = (Button) mMainView.findViewById(R.id.close);
			close.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mMainView.removeView(help);
					SharedPreferences.Editor editor = pref.edit();
					editor.putBoolean("initial_launch", false);
					editor.commit();
				}
			});
		}

		if (!mHardKeyboardHidden) {
			if(mShowSubView) mMainView.addView(mSubView);
			if(mShowNumKeyboardViewPortrait && mDisplayMode == PORTRAIT) mMainView.addView(mNumKeyboardView);
			if(mShowNumKeyboardViewLandscape && mDisplayMode == LANDSCAPE) mMainView.addView(mNumKeyboardView);
		} else if (mKeyboardView != null) {
			mMainView.addView(mKeyboardView);
		}
		mKeyboardView.setOnTouchListener(new OnKeyboardViewTouchListener());
		mNumKeyboardView.setOnTouchListener(new OnKeyboardViewTouchListener());
		TextView langView = mSubView.findViewById(R.id.lang);
		langView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN) {
					if(mVibrator != null) {
						mVibrator.vibrate(30);
					}
					nextLanguage();
					updateIndicator(HARD_KEYMODE_LANG + mCurrentLanguage);
				}
				return false;
			}
		});

		return mMainView;
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
	public void swipeRight() {
		if(!mOneHandedMode) return;
		this.mOneHandedSide = ONE_HAND_RIGHT;
		EventBus.getDefault().post(new InputViewChangeEvent());
	}

	@Override
	public void swipeLeft() {
		if(!mOneHandedMode) return;
		this.mOneHandedSide = ONE_HAND_LEFT;
		EventBus.getDefault().post(new InputViewChangeEvent());
	}

	@Override
	public void onKey(int primaryCode, int[] keyCodes) {
		return;
	}

	public void onKey(int primaryCode) {
		if(mDisableKeyInput) {
			return;
		}

		if(mTimeoutHandler != null) {
			mTimeoutHandler.removeCallbacksAndMessages(null);
			mTimeoutHandler = null;
		}

		switch(primaryCode) {
		case KEYCODE_CHANGE_LANG:
			EventBus.getDefault().post(new InputSoftKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KEYCODE_CHANGE_LANG)));
			break;

		case KEYCODE_UP:
			EventBus.getDefault().post(new InputSoftKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_UP)));
			break;

		case KEYCODE_DOWN:
			EventBus.getDefault().post(new InputSoftKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_DOWN)));
			break;

		case KEYCODE_LEFT:
			EventBus.getDefault().post(new InputSoftKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_LEFT)));
			break;

		case KEYCODE_RIGHT:
			EventBus.getDefault().post(new InputSoftKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_RIGHT)));
			break;

		case KEYCODE_JP12_BACKSPACE:
		case KEYCODE_QWERTY_BACKSPACE:
			EventBus.getDefault().post(new InputSoftKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL)));
			break;

		case KEYCODE_QWERTY_SHIFT:
			mCapsLock = false;
			toggleShiftLock();
			updateKeyLabels();
			if(mShiftOn == 0) {
				EventBus.getDefault().post(new InputSoftKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_SHIFT_LEFT)));
			} else {
				EventBus.getDefault().post(new InputSoftKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_SHIFT_RIGHT)));;
			}
			break;

		case KEYCODE_QWERTY_ALT:
			processAltKey();
			break;

		case KEYCODE_JP12_ENTER:
		case KEYCODE_QWERTY_ENTER:
			EventBus.getDefault().post(new InputSoftKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER)));
			break;

		case KEYCODE_JP12_SPACE:
		case -10:
			EventBus.getDefault().post(new InputSoftKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_SPACE)));
			break;

		default:
			if((primaryCode <= -200 && primaryCode > -300) || (primaryCode <= -2000 && primaryCode > -3000)) {
				EventBus.getDefault().post(new InputSoftKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, primaryCode)));
			} else if(primaryCode >= 0) {
				if(mKeyboardView.isShifted()) {
					primaryCode = Character.toUpperCase(primaryCode);
				}
				EventBus.getDefault().post(new InputCharEvent((char) primaryCode));

				if(mKeyboardView.isShifted()) {
					if(!mCapsLock) {
						onKey(KEYCODE_QWERTY_SHIFT);
						OpenWnnKOKR kokr = (OpenWnnKOKR) mWnn;
						if(!mHardKeyboardHidden) kokr.resetHardShift(false);
						kokr.updateMetaKeyStateDisplay();
					}
				}
			}
			break;
		}
		if (!mCapsLock && (primaryCode != DefaultSoftKeyboard.KEYCODE_QWERTY_SHIFT)) {

		}
		if(mTimeoutHandler == null && mTimeoutDelay > 0) {
			mTimeoutHandler = new Handler();
			mTimeoutHandler.postDelayed(new TimeOutHandler(), mTimeoutDelay);
		}
	}

	public void setPreviewEnabled(int x) {
		switch(x) {
		case KEYCODE_QWERTY_SHIFT:
		case KEYCODE_QWERTY_ENTER:
		case KEYCODE_JP12_ENTER:
		case KEYCODE_QWERTY_BACKSPACE:
		case KEYCODE_JP12_BACKSPACE:
		case -10:
		case KEYCODE_JP12_SPACE:
			break;
		default:
			mKeyboardView.setPreviewEnabled(mShowKeyPreview);
		}
	}

	public void nextLanguage() {
		int language = mCurrentLanguage;
		
		if(++mCurrentLanguageIndex >= mLanguageCycleTable.length) mCurrentLanguageIndex = 0;
		language = mLanguageCycleTable[mCurrentLanguageIndex];
		
		mCurrentLanguage = language;
		
		changeKeyMode(KEYMODE_HANGUL);
		
	}

	public void setShiftState(int shiftState) {
		mShiftOn = (shiftState == 0) ? 1 : 0;
		toggleShiftLock();
	}

	public void setCapsLock(boolean capsLock) {
		mCapsLock = capsLock;
	}

	public boolean isCapsLock() {
		return mCapsLock;
	}

	@Override
	public void toggleShiftLock() {
		super.toggleShiftLock();
		if(mShiftOn != 0) {
			Keyboard newKeyboard = getShiftChangeNumKeyboard(KEYBOARD_SHIFT_ON);
			if(newKeyboard != null) {
				changeNumKeyboard(newKeyboard);
			}
		} else {
			Keyboard newKeyboard = getShiftChangeNumKeyboard(KEYBOARD_SHIFT_OFF);
			if(newKeyboard != null) {
				changeNumKeyboard(newKeyboard);
			}
		}
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

	protected boolean changeNumKeyboard(Keyboard keyboard) {

		if (keyboard == null) {
			return false;
		}
		if (mCurrentKeyboard != keyboard) {
			mNumKeyboardView.setKeyboard(keyboard);
			mNumKeyboardView.setShifted((mShiftOn == 0) ? false : true);
			return true;
		} else {
			mNumKeyboardView.setShifted((mShiftOn == 0) ? false : true);
			return false;
		}
	}

	public Keyboard getShiftChangeNumKeyboard(int shift) {
		try {
			Keyboard[] kbd = mNumKeyboard[mCurrentLanguage][mDisplayMode][mCurrentKeyboardType][shift][0];

			if (!mNoInput && kbd[1] != null) {
				return kbd[1];
			}
			return kbd[0];
		} catch (Exception ex) {
			return null;
		}
	}

	@Override
	public void setPreferences(SharedPreferences pref, EditorInfo editor) {
		super.setPreferences(pref, editor);
		
		int keyHeightPortrait = pref.getInt("key_height_portrait", mKeyHeightPortrait);
		int keyHeightLandscape = pref.getInt("key_height_landscape", mKeyHeightLandscape);
		if(keyHeightPortrait != mKeyHeightPortrait || keyHeightLandscape != mKeyHeightLandscape) {
			mKeyHeightPortrait = keyHeightPortrait;
			mKeyHeightLandscape = keyHeightLandscape;
			EventBus.getDefault().post(new InputViewChangeEvent());
		}
		boolean oneHandedMode = pref.getBoolean("keyboard_one_hand", false);
		int oneHandedRatio = pref.getInt("keyboard_one_hand_ratio", 100);
		if(oneHandedMode != mOneHandedMode || oneHandedRatio != mOneHandedRatio) {
			mOneHandedMode = oneHandedMode;
			mOneHandedRatio = oneHandedRatio;
			EventBus.getDefault().post(new InputViewChangeEvent());
		}

		boolean use12Key = pref.getBoolean("keyboard_hangul_use_12key", false);
		boolean useAlphabetQwerty = pref.getBoolean("keyboard_alphabet_use_qwerty", true);
		if(mUse12Key != use12Key || useAlphabetQwerty != mUseAlphabetQwerty) {
			EventBus.getDefault().post(new InputViewChangeEvent());
		}
		mLongPressTimeout = pref.getInt("keyboard_long_press_timeout", 500);
		mUseFlick = pref.getBoolean("keyboard_use_flick", true);
		mFlickSensitivity = pref.getInt("keyboard_flick_sensitivity", DEFAULT_FLICK_SENSITIVITY);
		mTimeoutDelay = pref.getInt("keyboard_timeout_delay", 500);
		mSpaceSlideSensitivity = mFlickSensitivity;
		mVibrateDuration = pref.getInt("key_vibration_duration", mVibrateDuration);
		boolean showSubView = pref.getBoolean("hardware_use_subview", true);
		if(showSubView != mShowSubView) {
			mShowSubView = showSubView;
			EventBus.getDefault().post(new InputViewChangeEvent());
		}
		mKeyboardView.setPreviewEnabled(false);
		mNumKeyboardView.setPreviewEnabled(false);
		boolean showNum = pref.getBoolean("hardware_use_numkeyboard", true);
		if(showNum != mShowNumKeyboardViewPortrait || showNum != mShowNumKeyboardViewLandscape) {
			mShowNumKeyboardViewLandscape = mShowNumKeyboardViewPortrait = showNum;
			EventBus.getDefault().post(new InputViewChangeEvent());
		}
		mShowKeyPreview = pref.getBoolean("popup_preview", true);

		mForceHangul = pref.getBoolean("system_force_hangul", false);
		
		int inputType = editor.inputType;
		if(mHardKeyboardHidden) {
			
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
				mPreferenceLanguage = mLanguageCycleTable[0];
				break;
				
			case EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS:
			case EditorInfo.TYPE_TEXT_VARIATION_URI:
				mPreferenceLanguage = mLanguageCycleTable[0];
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

	protected void loadSoftLayout(Keyboard[][][] keyList, String softLayout) {
		switch(softLayout) {
		case "l1.0":
			keyList[KEYBOARD_SHIFT_OFF][KEYMODE_HANGUL][0] = loadKeyboardLayout(mWnn, R.xml.keyboard_ko_l1_0_mobile);
			keyList[KEYBOARD_SHIFT_ON][KEYMODE_HANGUL][0] = loadKeyboardLayout(mWnn, R.xml.keyboard_ko_l1_0_mobile);
			break;

		case "l1.1":
			keyList[KEYBOARD_SHIFT_OFF][KEYMODE_HANGUL][0] = loadKeyboardLayout(mWnn, R.xml.keyboard_ko_l1_1_mobile_num);
			keyList[KEYBOARD_SHIFT_ON][KEYMODE_HANGUL][0] = loadKeyboardLayout(mWnn, R.xml.keyboard_ko_l1_1_mobile_num);
			break;

		case "l1.2":
			keyList[KEYBOARD_SHIFT_OFF][KEYMODE_HANGUL][0] = loadKeyboardLayout(mWnn, R.xml.keyboard_ko_l1_2_mod_quote);
			keyList[KEYBOARD_SHIFT_ON][KEYMODE_HANGUL][0] = loadKeyboardLayout(mWnn, R.xml.keyboard_ko_l1_2_mod_quote);
			break;

		case "l1.3":
			keyList[KEYBOARD_SHIFT_OFF][KEYMODE_HANGUL][0] = loadKeyboardLayout(mWnn, R.xml.keyboard_ko_l1_3_punc_grave);
			keyList[KEYBOARD_SHIFT_ON][KEYMODE_HANGUL][0] = loadKeyboardLayout(mWnn, R.xml.keyboard_ko_l1_3_punc_grave);
			break;

		case "l1.4":
			keyList[KEYBOARD_SHIFT_OFF][KEYMODE_HANGUL][0] = loadKeyboardLayout(mWnn, R.xml.keyboard_ko_l1_4_punc_slash);
			keyList[KEYBOARD_SHIFT_ON][KEYMODE_HANGUL][0] = loadKeyboardLayout(mWnn, R.xml.keyboard_ko_l1_4_punc_slash);
			break;

		case "l1.9":
			keyList[KEYBOARD_SHIFT_OFF][KEYMODE_HANGUL][0] = loadKeyboardLayout(mWnn, R.xml.keyboard_ko_l1_9_colemak);
			keyList[KEYBOARD_SHIFT_ON][KEYMODE_HANGUL][0] = loadKeyboardLayout(mWnn, R.xml.keyboard_ko_l1_9_colemak);
			break;

		case "l1.10":
			keyList[KEYBOARD_SHIFT_OFF][KEYMODE_HANGUL][0] = loadKeyboardLayout(mWnn, R.xml.keyboard_ko_l1_10_dvorak);
			keyList[KEYBOARD_SHIFT_ON][KEYMODE_HANGUL][0] = loadKeyboardLayout(mWnn, R.xml.keyboard_ko_l1_10_dvorak);
			break;

		case "l2.0":
			keyList[KEYBOARD_SHIFT_OFF][KEYMODE_HANGUL][0] = loadKeyboardLayout(mWnn, R.xml.keyboard_ko_l2_0_11cols);
			keyList[KEYBOARD_SHIFT_ON][KEYMODE_HANGUL][0] = loadKeyboardLayout(mWnn, R.xml.keyboard_ko_l2_0_11cols);
			break;

		case "pc1":
			keyList[KEYBOARD_SHIFT_OFF][KEYMODE_HANGUL][0] = loadKeyboardLayout(mWnn, R.xml.keyboard_ko_pc1_alphanumeric);
			keyList[KEYBOARD_SHIFT_ON][KEYMODE_HANGUL][0] = loadKeyboardLayout(mWnn, R.xml.keyboard_ko_pc1_alphanumeric);
			break;

		}
	}

	protected EngineMode getEngineMode(String defaultLayout) {
		for(EngineMode mode : EngineMode.values()) {
			if(mode.getPrefValues() == null) continue;
			for(String prefValue : mode.getPrefValues()) {
				if(prefValue.equals(defaultLayout)) {
					return mode;
				}
			}
		}
		return EngineMode.DIRECT;
	}

	@SuppressWarnings("deprecation")
	public Keyboard loadKeyboardLayout(Context context, int xmlLayoutResId) {
		KeyboardKOKR keyboard = new KeyboardKOKR(context, xmlLayoutResId);
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
		DisplayMetrics metrics = mWnn.getResources().getDisplayMetrics();
		int height = (mDisplayMode == PORTRAIT) ? mKeyHeightPortrait : mKeyHeightLandscape;
		height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, height, metrics);
		keyboard.resizeHeight(height);
		if(mOneHandedMode) {
			keyboard.oneHandedMode(mWnn, mOneHandedSide, mOneHandedRatio / 100d);
//			if(mOneHandedSide == ONE_HAND_LEFT) {
//				keyboard.resizeWidth(mOneHandedRatio / 100d);
//			} else {
//				keyboard.resizeWidthToRight(mOneHandedRatio / 100d);
//			}
		}
		SparseArray<Integer> keyIcons = mKeyIcons.get(icon);
		for(Keyboard.Key key : keyboard.getKeys()) {
			Integer keyIcon = keyIcons.get(key.codes[0]);
			if(keyIcon != null) {
				Drawable drawable = mWnn.getResources().getDrawable(keyIcon);
				key.icon = drawable;
				key.iconPreview = drawable;
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

	public void fixHardwareLayoutState() {
		if(mHardwareLayout == mHardKeyboardHidden) {
			EventBus.getDefault().post(new InputViewChangeEvent());
		}
	}

	@Override
	public void updateIndicator(int mode) {
		if(mSubView == null) return;
		TextView text = (TextView) mSubView.findViewById(R.id.lang);
		switch(mode) {
		case HARD_KEYMODE_LANG_ENGLISH:
			text.setText(R.string.indicator_lang_en);
			break;
			
		case HARD_KEYMODE_LANG_KOREAN:
			text.setText(R.string.indicator_lang_ko);
			break;
			
		default:
			super.updateIndicator(mode);
			break;
			
		}
	}

	public void updateKeyLabels() {
		if(mCurrentKeyboardType == KEYBOARD_12KEY
				&& mCurrentKeyMode != KEYMODE_ALT_SYMBOLS
				&& !(mCurrentLanguage == LANG_EN && mUseAlphabetQwerty)) return;
		int[][] layout;
		if(mCurrentKeyMode != KEYMODE_ALT_SYMBOLS) {
			HangulEngine hangulEngine = ((OpenWnnKOKR) mWnn).getHangulEngine();
			layout = hangulEngine.getJamoTable();
		} else {
			layout = ((OpenWnnKOKR) mWnn).getAltSymbols();
		}
		updateLabels(mKeyboard[mCurrentLanguage][mDisplayMode][mCurrentKeyboardType][mShiftOn][mCurrentKeyMode][0], layout);
		mKeyboardView.invalidateAllKeys();
		mKeyboardView.requestLayout();
	}

	protected void updateLabels(Keyboard kbd, int[][] layout) {
		if(!(kbd instanceof KeyboardKOKR)) return;
		if(layout == null) {
			for(Keyboard.Key key : kbd.getKeys()) {
				String label = getKeyLabel(key.codes[0], mShiftOn > 0);
				if(label != null) key.label = label;
			}
			return;
		}
		for(Keyboard.Key key : kbd.getKeys()) {
			boolean found = false;
			for(int i = 0 ; i < layout.length ; i++) {
				if(key.codes[0] == 128) break;
				if(key.codes[0] == layout[i][0]) {
					int code = layout[i][mShiftOn + 1] & 0xffff;
					String label = getKeyLabel(code, false);
					if(label != null) key.label = label;
					found = true;
					break;
				}
			}
			if(!found) {
				String label = getKeyLabel(key.codes[0], mShiftOn > 0);
				if(label != null) key.label = label;
			}
		}
	}

	private String getKeyLabel(int code, boolean shift) {
		switch(code) {
		case KEYCODE_CHANGE_LANG:
			return mCurrentLanguage == LANG_KO ? "A" : "가";

		case 128:
			return (shift) ? "," : ". ,";

		default:
			if(code >= 0) {
				if(shift) {
					for (int[] item : OpenWnnKOKR.SHIFT_CONVERT) {
						if(item[0] == code) {
							code = item[1];
							break;
						}
					}
				}
				return String.valueOf(Character.toChars(code));
			}
			else return null;
		}
	}

}
