package com.juelian.mipop;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.juelian.mipop.api.MiPopApplication;

@SuppressWarnings("deprecation")
public class PreferenceSettings extends PreferenceActivity implements OnPreferenceChangeListener {
	private String TAG = "Mipop-PreferenceSettings";
	private CheckBoxPreference mFullScreen;
	private CheckBoxPreference mMiPop;
	private SharedPreferences mSharedPreferences;
	private ListPreference mFirstKeyListPreference;
	private EditTextPreference mMiPopButtonAlpha;

	/* KEY */
	public static final String KEY_SWITCH_STRING = "mipop_switch";
	public static final String KEY_FULLSCREEN_STRING = "mipop_fullscreen";
	public static final String KEY_FIRST_KEY_STRING = "firstkey";
	public static final String KEY_ALPHA_STRING = "alpha";
	private String alphaSummaryFormat;
	
	public void onCreate(Bundle bundle) {
		Log.i(TAG, "onCreate()...");
		super.onCreate(bundle);
		addPreferencesFromResource(R.xml.mipop_settings);
		alphaSummaryFormat =  getResources().getString(R.string.alpha_summary);
		mSharedPreferences = getPreferenceManager().getDefaultSharedPreferences(PreferenceSettings.this);
		mFirstKeyListPreference = (ListPreference) findPreference(KEY_FIRST_KEY_STRING);
		mFirstKeyListPreference.setSummary(mFirstKeyListPreference.getEntries()[Settings.System.getInt(getContentResolver(),"FirstKey", 0)]);
		mFirstKeyListPreference.setOnPreferenceChangeListener(this);
		mMiPop = ((CheckBoxPreference) findPreference(KEY_SWITCH_STRING));
		mFullScreen = ((CheckBoxPreference) findPreference(KEY_FULLSCREEN_STRING));
		if (!mSharedPreferences.getBoolean(KEY_FULLSCREEN_STRING, false)) {
			getPreferenceScreen().removePreference(findPreference(KEY_FULLSCREEN_STRING));			
		}
		
		mMiPopButtonAlpha = (EditTextPreference) findPreference(KEY_ALPHA_STRING);
		mMiPopButtonAlpha.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
		mMiPopButtonAlpha.getEditText().setHint(JueLianUtils.getAlpha()+"");
		mMiPopButtonAlpha.setDialogMessage(R.string.mipop_button_alpha_dialog_msg);
		mMiPopButtonAlpha.setSummary(String.format(alphaSummaryFormat, JueLianUtils.getAlpha()));
		mMiPopButtonAlpha.setOnPreferenceChangeListener(this);
	}

	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {
		Log.i(TAG, "onPreferenceTreeClick");
		if (preference == mMiPop) {
			Log.i(TAG, "onPreferenceTreeClick preference == mMiPop");
			if (mMiPop.isChecked()) {
				MiPopApplication.showMipop();
			} else {
				MiPopApplication.hideMipop();
			}
		} else if (preference == mFullScreen) {
			Log.i(TAG, "onPreferenceTreeClick preference == mFullScreen");
			if (mFullScreen.isChecked()) {
				Log.i(TAG, "mFullScreen checked mipop = true");
				mMiPop.setChecked(true);
				mMiPop.setEnabled(false);
				MiPopApplication.showMipop();
			} else {
				mMiPop.setEnabled(true);
			}
		}
		return super.onPreferenceTreeClick(preferenceScreen, preference);

	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		// TODO Auto-generated method stub
		if (preference==mFirstKeyListPreference) {
			int index = mFirstKeyListPreference.findIndexOfValue((String) newValue);
			mFirstKeyListPreference.setSummary(mFirstKeyListPreference.getEntries()[index]);
			Settings.System.putInt(getContentResolver(),"FirstKey", index);
			return true;
		}
		if (preference == mMiPopButtonAlpha) {
			String valueString = (String)newValue;
			if (!TextUtils.isEmpty(valueString)) {
				int index = Integer.parseInt(valueString);
				if (index < 60 || index > 255) {
					Toast.makeText(getApplicationContext(), R.string.alpha_waring_msg, 1).show();
					return false;
				}else {
					Settings.System.putInt(getContentResolver(), "juelian_button_alpha", index);
					String beenFormat = String.format(alphaSummaryFormat, index);
					mMiPopButtonAlpha.setSummary(beenFormat);
					return true;
				}
			}else {
				Toast.makeText(getApplicationContext(), R.string.alpha_null_waring_msg, 1).show();
				return false;
			}
		}
		return false;
	}
	
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
