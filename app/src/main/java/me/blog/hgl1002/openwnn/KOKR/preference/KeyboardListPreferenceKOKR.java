/*
 * Copyright (C) 2008,2009  OMRON SOFTWARE Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.blog.hgl1002.openwnn.KOKR.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.util.AttributeSet;

import org.greenrobot.eventbus.EventBus;

import me.blog.hgl1002.openwnn.event.InputViewChangeEvent;

/**
 * The preference class of keyboard image list for Japanese IME.
 * This class notices to {@code OpenWnnJAJP} that the keyboard image is changed.
 * 
 * @author Copyright (C) 2009, OMRON SOFTWARE CO., LTD.
 */
public class KeyboardListPreferenceKOKR extends ListPreference {

	String[] keys;

	public KeyboardListPreferenceKOKR(Context context, AttributeSet attrs) {
        super(context, attrs);
        String key = attrs.getAttributeValue(null, "softLayoutKey");
        if(key != null) keys = key.split(",");
    }
    
    public KeyboardListPreferenceKOKR(Context context) {
        this(context, null);
    }

    /** @see android.preference.DialogPreference#onDialogClosed */
    @Override protected void onDialogClosed(boolean positiveResult) {
    	super.onDialogClosed(positiveResult);
    	
    	if (positiveResult) {
			if(keys != null) {
				SharedPreferences pref = getPreferenceManager().getSharedPreferences();
				SharedPreferences.Editor editor = pref.edit();
				String value = getContext().getResources().getStringArray(SoftLayoutPreference.getEntryValues(pref.getString(this.getKey(), "")))[0];
				for(String key : keys) editor.putString(key, value);
				editor.commit();
			}
			EventBus.getDefault().post(new InputViewChangeEvent());
    	}
    }
}
