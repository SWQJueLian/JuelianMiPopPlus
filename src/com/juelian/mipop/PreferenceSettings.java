package com.juelian.mipop;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.util.Log;

import com.juelian.mipop.api.App;

@SuppressWarnings("deprecation")
public class PreferenceSettings extends PreferenceActivity {
	private String TAG = "Mipop-PreferenceSettings";
	private CheckBoxPreference mFullScreen;
	private CheckBoxPreference mMiPop;
	private SharedPreferences mSharedPreferences;

	/* KEY */
	public static final String KEY_SWITCH_STRING = "mipop_switch";
	public static final String KEY_FULLSCREEN_STRING = "mipop_fullscreen";
	
	private void setupFloatIcon() {
		boolean isMipopShow = mSharedPreferences.getBoolean(KEY_SWITCH_STRING, true);
		mMiPop.setChecked(isMipopShow);
		if (mFullScreen.isChecked()){
			mMiPop.setEnabled(false);
		}
	}

	private void setupFullScreen() {
		String str = Settings.System.getString(getContentResolver(),"showNavigationBar");
		if ("show".equals(str)) {
			mFullScreen.setChecked(false);
		} else if ("hide".equals(str)) {
			mFullScreen.setChecked(true);
		}
	}

	public void onCreate(Bundle bundle) {
		Log.i(TAG, "onCreate()...");
		super.onCreate(bundle);
		addPreferencesFromResource(R.xml.mipop_settings);
		mSharedPreferences = getPreferenceManager().getDefaultSharedPreferences(PreferenceSettings.this);
		mMiPop = ((CheckBoxPreference) findPreference(KEY_SWITCH_STRING));
		mFullScreen = ((CheckBoxPreference) findPreference(KEY_FULLSCREEN_STRING));
		if (!mSharedPreferences.getBoolean(KEY_FULLSCREEN_STRING, false)) {
			getPreferenceScreen().removePreference(findPreference(KEY_FULLSCREEN_STRING));			
		}
	}

	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {
		Log.i(TAG, "onPreferenceTreeClick");
		if (preference == mMiPop) {
			Log.i(TAG, "onPreferenceTreeClick preference == mMiPop");
			if (mMiPop.isChecked()) {
				App.showMipop();
			} else {
				App.hideMipop();
			}
		} else if (preference == mFullScreen) {
			Log.i(TAG, "onPreferenceTreeClick preference == mFullScreen");
			if (mFullScreen.isChecked()) {
				Log.i(TAG, "mFullScreen checked mipop = true");
				mMiPop.setChecked(true);
				mMiPop.setEnabled(false);
				App.showMipop();
			} else {
				mMiPop.setEnabled(true);
			}
		}
		return super.onPreferenceTreeClick(preferenceScreen, preference);

	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i(TAG, "onResume()...");
		setupFloatIcon();
		setupFullScreen();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.i(TAG, "ondestory-->");
		this.mMiPop = null;
		this.mFullScreen = null;
		this.mSharedPreferences = null;
	}

}
