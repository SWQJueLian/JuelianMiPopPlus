package com.juelian.mipop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
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
public class PreferenceSettings extends PreferenceActivity implements
		OnPreferenceChangeListener {
	private String TAG = "Mipop-PreferenceSettings";
	private CheckBoxPreference mFullScreen;
	private CheckBoxPreference mMiPop;
	private SharedPreferences mSharedPreferences;
	private ListPreference mFirstKeyListPreference;
	private EditTextPreference mMiPopButtonAlpha;
	private ListPreference mBackKeyListPreference;
	private ListPreference mHomeKeyListPreference;
	private ListPreference mMenuKeyListPreference;
	private ListPreference mReclKeyListPreference;
	private ListPreference mThemeListPreference;
	private ProgressDialog mProgressDialog;
	private List<AppInfo> mAppInfos =null;
	private PackageManager pm;
	List<ResolveInfo> resolveInfos;
	private AppInfo appInfo = null;
	private String appPackNameString = "";
	private String appClassNameString = "";
	private String appNameString = "";

	/* KEY */
	public static final String KEY_SWITCH_STRING = "mipop_switch";
	public static final String KEY_FULLSCREEN_STRING = "mipop_fullscreen";
	public static final String KEY_FIRST_KEY_STRING = "firstkey";
	public static final String KEY_ALPHA_STRING = "alpha";
	public static final String KEY_BACK_STRING = "backkey";
	public static final String KEY_HOME_STRING = "homekey";
	public static final String KEY_MENU_STRING = "menukey";
	public static final String KEY_RECL_STRING = "reclkey";
	public static final String KEY_THEME_STRING = "theme";
	
	private String alphaSummaryFormat;

	public void onCreate(Bundle bundle) {
		Log.i(TAG, "onCreate()...");
		super.onCreate(bundle);
		addPreferencesFromResource(R.xml.mipop_settings);
		mProgressDialog = new ProgressDialog(PreferenceSettings.this);
		mProgressDialog.setCancelable(false);
		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.setMessage(getResources().getString(R.string.loading));
		pm = this.getPackageManager();
		alphaSummaryFormat = getResources().getString(R.string.alpha_summary);
		mSharedPreferences = getPreferenceManager()
				.getDefaultSharedPreferences(PreferenceSettings.this);
		mFirstKeyListPreference = (ListPreference) findPreference(KEY_FIRST_KEY_STRING);
		mFirstKeyListPreference
				.setSummary(mFirstKeyListPreference.getEntries()[Settings.System
						.getInt(getContentResolver(), "FirstKey", 0)]);
		mFirstKeyListPreference.setOnPreferenceChangeListener(this);
		mMiPop = ((CheckBoxPreference) findPreference(KEY_SWITCH_STRING));
		mFullScreen = ((CheckBoxPreference) findPreference(KEY_FULLSCREEN_STRING));
		if (!mSharedPreferences.getBoolean(KEY_FULLSCREEN_STRING, false)) {
			getPreferenceScreen().removePreference(
					findPreference(KEY_FULLSCREEN_STRING));
		}

		mMiPopButtonAlpha = (EditTextPreference) findPreference(KEY_ALPHA_STRING);
		mMiPopButtonAlpha.getEditText().setInputType(
				InputType.TYPE_CLASS_NUMBER);
		mMiPopButtonAlpha.getEditText().setHint(JueLianUtils.getAlpha() + "");
		mMiPopButtonAlpha
				.setDialogMessage(R.string.mipop_button_alpha_dialog_msg);
		mMiPopButtonAlpha.setSummary(String.format(alphaSummaryFormat,
				JueLianUtils.getAlpha()));
		mMiPopButtonAlpha.setOnPreferenceChangeListener(this);

		mBackKeyListPreference = (ListPreference) findPreference(KEY_BACK_STRING);
		setListPreferenceSummary(mBackKeyListPreference, "mipop_choose_what_back");
		mBackKeyListPreference.setOnPreferenceChangeListener(this);

		mHomeKeyListPreference = (ListPreference) findPreference(KEY_HOME_STRING);
		setListPreferenceSummary(mHomeKeyListPreference, "mipop_choose_what_home");
		mHomeKeyListPreference.setOnPreferenceChangeListener(this);

		mMenuKeyListPreference = (ListPreference) findPreference(KEY_MENU_STRING);
		setListPreferenceSummary(mMenuKeyListPreference, "mipop_choose_what_menu");
		mMenuKeyListPreference.setOnPreferenceChangeListener(this);

		mReclKeyListPreference = (ListPreference) findPreference(KEY_RECL_STRING);
		setListPreferenceSummary(mReclKeyListPreference, "mipop_choose_what_recl");
		mReclKeyListPreference.setOnPreferenceChangeListener(this);
		
		mThemeListPreference = (ListPreference) findPreference(KEY_THEME_STRING);
		mThemeListPreference
				.setSummary(mThemeListPreference.getEntries()[Settings.System
						.getInt(getContentResolver(), "juelian_button_theme", 0)]);
		mThemeListPreference.setOnPreferenceChangeListener(this);
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
		if (preference == mFirstKeyListPreference) {
			int index = mFirstKeyListPreference
					.findIndexOfValue((String) newValue);
			mFirstKeyListPreference.setSummary(mFirstKeyListPreference
					.getEntries()[index]);
			Settings.System.putInt(getContentResolver(), "FirstKey", index);
			return true;
		}
		if (preference == mMiPopButtonAlpha) {
			String valueString = (String) newValue;
			if (!TextUtils.isEmpty(valueString)) {
				int index = Integer.parseInt(valueString);
				if (index < 60 || index > 255) {
					Toast.makeText(getApplicationContext(),
							R.string.alpha_waring_msg, 1).show();
					return false;
				} else {
					Settings.System.putInt(getContentResolver(),
							"juelian_button_alpha", index);
					String beenFormat = String
							.format(alphaSummaryFormat, index);
					mMiPopButtonAlpha.setSummary(beenFormat);
					return true;
				}
			} else {
				Toast.makeText(getApplicationContext(),
						R.string.alpha_null_waring_msg, 1).show();
				return false;
			}
		}
		if (preference == mBackKeyListPreference) {
			int index = mBackKeyListPreference
					.findIndexOfValue((String) newValue);
			//setListPreferenceSummary(mBackKeyListPreference,"mipop_choose_what_back");
			Settings.System.putInt(getContentResolver(),
					"mipop_choose_what_back", index);
			if (index == 5) {
				new myAsyncTask(mBackKeyListPreference,
						"mipop_choose_what_back").execute();
			}
			return true;
		}

		if (preference == mHomeKeyListPreference) {
			int index = mHomeKeyListPreference
					.findIndexOfValue((String) newValue);
			//setListPreferenceSummary(mHomeKeyListPreference,"mipop_choose_what_home");
			Settings.System.putInt(getContentResolver(),
					"mipop_choose_what_home", index);
			if (index == 5) {
				new myAsyncTask(mHomeKeyListPreference,
						"mipop_choose_what_home").execute();
			}
			return true;
		}

		if (preference == mMenuKeyListPreference) {
			int index = mMenuKeyListPreference
					.findIndexOfValue((String) newValue);
			//setListPreferenceSummary(mMenuKeyListPreference,"mipop_choose_what_menu");

			Settings.System.putInt(getContentResolver(),
					"mipop_choose_what_menu", index);

			if (index == 5) {
				new myAsyncTask(mMenuKeyListPreference,
						"mipop_choose_what_menu").execute();
			}

			return true;
		}

		if (preference == mReclKeyListPreference) {
			int index = mReclKeyListPreference
					.findIndexOfValue((String) newValue);
			//setListPreferenceSummary(mReclKeyListPreference,"mipop_choose_what_recl");
			Settings.System.putInt(getContentResolver(),
					"mipop_choose_what_recl", index);

			if (index == 5) {
				new myAsyncTask(mReclKeyListPreference,
						"mipop_choose_what_recl").execute();
			}

			return true;
		}
		
		if (preference == mThemeListPreference) {
			int index = mThemeListPreference
					.findIndexOfValue((String) newValue);
			mThemeListPreference.setSummary(mThemeListPreference
					.getEntries()[index]);
			Settings.System.putInt(getContentResolver(), "juelian_button_theme", index);
			return true;
		}
		
		return false;
	}

	private void setupFloatIcon() {
		boolean isMipopShow = mSharedPreferences.getBoolean(KEY_SWITCH_STRING,
				true);
		mMiPop.setChecked(isMipopShow);
		if (mFullScreen.isChecked()) {
			mMiPop.setEnabled(false);
		}
	}

	private void setupFullScreen() {
		String str = Settings.System.getString(getContentResolver(),
				"showNavigationBar");
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
	
	public void queryFilterAppInfo() {
		Log.d(TAG, "queryFilterAppInfo()");
		
		Intent mainIntent = new Intent(Intent.ACTION_MAIN);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		
		resolveInfos = pm.queryIntentActivities(mainIntent, 0);
		
		Collections.sort(resolveInfos,new ResolveInfo.DisplayNameComparator(pm));// 排序
		
		List<AppInfo> appInfos = new ArrayList<AppInfo>();
		
		if (!appInfos.isEmpty()) {
			appInfos.clear();
		}
		for (ResolveInfo reInfo : resolveInfos) {
			String clsName = reInfo.activityInfo.name; // 获得该应用程序的启动Activity的name
			String pkgName = reInfo.activityInfo.packageName; // 获得应用程序的包名
			String appLabel = (String) reInfo.loadLabel(pm); // 获得应用程序的Label
			Drawable icon = reInfo.loadIcon(pm); // 获得应用程序图标
			AppInfo appInfo = new AppInfo();
			appInfo.setAppLabel(appLabel);
			appInfo.setPkgName(pkgName);
			appInfo.setClsName(clsName);
			appInfo.setAppIcon(icon);
			appInfos.add(appInfo);
		}
		mAppInfos = appInfos;
	}
	
	private void setListPreferenceSummary(ListPreference mListPreference,String keyString){
		String summarylabel = mSharedPreferences.getString(keyString+"_summary","");
		int a = Settings.System.getInt(getContentResolver(), keyString,0);
		if (a==5) {
			if (summarylabel.isEmpty()) {
				mListPreference.setSummary(getResources().getString(R.string.not_select_app));
			}else {
				mListPreference.setSummary(getResources().getString(R.string.start)+summarylabel);
			}
		}else {
			mListPreference.setSummary(mListPreference.getEntries()[a]);
		}
	}
	
	public void showAlertDialog(final ListPreference listPreference,final String keyString){
		AlertDialog.Builder mBuilder = new AlertDialog.Builder(PreferenceSettings.this);
		mBuilder.setTitle(R.string.chooseapp);
		mBuilder.setSingleChoiceItems(new ListViewAdapter(PreferenceSettings.this, mAppInfos), -1, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				appInfo = mAppInfos.get(which);
				appPackNameString = appInfo.getPkgName();
				appClassNameString = appInfo.getClsName();
				appNameString = appInfo.getAppLabel();
				Editor editor = mSharedPreferences.edit();
				editor.putString(keyString+"_packname", appPackNameString);
				editor.putString(keyString+"_classname", appClassNameString);
				editor.putString(keyString+"_summary", appNameString);
				editor.commit();
				listPreference.setSummary(getResources().getString(R.string.start)+appNameString+"");
				dialog.cancel();
			}
		}).setNegativeButton(android.R.string.cancel, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		
		mBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				String summarylabel = mSharedPreferences.getString(keyString+"_summary","");
				if (summarylabel.isEmpty()) {
					listPreference.setSummary(getResources().getString(R.string.not_select_app));
				}else {
					listPreference.setSummary(getResources().getString(R.string.start)+summarylabel);
				}
			}
		});
		
		mBuilder.show();
	}
	
	class myAsyncTask extends AsyncTask<Void, Void, Void>{
		
		private ListPreference mListPreference;
		private String keyString;
		
		public myAsyncTask(ListPreference mListPreference,String keyString){
			this.mListPreference = mListPreference;
			this.keyString = keyString;
		}
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			mProgressDialog.show();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			queryFilterAppInfo();
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			mProgressDialog.cancel();
			showAlertDialog(mListPreference, keyString);
			super.onPostExecute(result);
		}
	}
}
