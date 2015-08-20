package com.juelian.mipop;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import com.juelian.mipop.api.App;

@SuppressWarnings("deprecation")
public class PreferenceSettings extends PreferenceActivity {
	private String TAG = "MainActivity";
	private CheckBoxPreference mFullScreen;
	private CheckBoxPreference mMiPop;
	private SharedPreferences mSharedPreferences;

	private void setupFloatIcon() {
		boolean isMipopShow = mSharedPreferences.getBoolean("mipop_switch", true);
		mMiPop.setChecked(isMipopShow);
		if (mFullScreen.isChecked()){
			mMiPop.setEnabled(false);
		}
	}

	private void setupFullScreen() {
		boolean isEnable = mSharedPreferences.getBoolean("mipop_fullscreen", false);
		mFullScreen.setEnabled(isEnable);
		String str = Settings.System.getString(getContentResolver(),"showNavigationBar");
		if ("show".equals(str)) {
			mFullScreen.setChecked(false);
		} else if ("hide".equals(str)) {
			mFullScreen.setChecked(true);
		}
	}

	public void onCreate(Bundle bundle) {
		AppLog.i(TAG, "onCreate()...");
		super.onCreate(bundle);
		addPreferencesFromResource(R.xml.mipop_settings);
		mSharedPreferences = getPreferenceManager().getDefaultSharedPreferences(PreferenceSettings.this);
		mMiPop = ((CheckBoxPreference) findPreference("mipop_switch"));
		mFullScreen = ((CheckBoxPreference) findPreference("mipop_fullscreen"));
	}

	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {
		AppLog.i(TAG, "onPreferenceTreeClick");
		if (preference == mMiPop) {
			AppLog.i(TAG, "onPreferenceTreeClick preference == mMiPop");
			if (mMiPop.isChecked()) {
				App.showMipop();
			} else {
				App.hideMipop();
			}
		} else if (preference == mFullScreen) {
			AppLog.i(TAG, "onPreferenceTreeClick preference == mFullScreen");
			if (mFullScreen.isChecked()) {
				AppLog.i(TAG, "mFullScreen checked mipop = true");
				mMiPop.setChecked(true);
				mMiPop.setEnabled(false);
				App.showMipop();
			} else {
				mMiPop.setEnabled(true);
			}
		}
		return super.onPreferenceTreeClick(preferenceScreen, preference);

	}

	protected void onResume() {
		super.onResume();
		AppLog.i(TAG, "onResume()...");
		setupFloatIcon();
		setupFullScreen();
	}

}
