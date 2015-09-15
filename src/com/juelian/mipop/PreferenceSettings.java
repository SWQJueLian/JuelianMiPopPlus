package com.juelian.mipop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
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
		pm = getPackageManager();
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
		mBackKeyListPreference
				.setSummary(mBackKeyListPreference.getEntries()[Settings.System
						.getInt(getContentResolver(), "mipop_choose_what_back",
								0)]);
		mBackKeyListPreference.setOnPreferenceChangeListener(this);

		mHomeKeyListPreference = (ListPreference) findPreference(KEY_HOME_STRING);
		mHomeKeyListPreference
				.setSummary(mHomeKeyListPreference.getEntries()[Settings.System
						.getInt(getContentResolver(), "mipop_choose_what_home",
								0)]);
		mHomeKeyListPreference.setOnPreferenceChangeListener(this);

		mMenuKeyListPreference = (ListPreference) findPreference(KEY_MENU_STRING);
		mMenuKeyListPreference
				.setSummary(mMenuKeyListPreference.getEntries()[Settings.System
						.getInt(getContentResolver(), "mipop_choose_what_menu",
								0)]);
		mMenuKeyListPreference.setOnPreferenceChangeListener(this);

		mReclKeyListPreference = (ListPreference) findPreference(KEY_RECL_STRING);
		mReclKeyListPreference
				.setSummary(mReclKeyListPreference.getEntries()[Settings.System
						.getInt(getContentResolver(), "mipop_choose_what_recl",
								0)]);
		mReclKeyListPreference.setOnPreferenceChangeListener(this);
		
		mThemeListPreference = (ListPreference) findPreference(KEY_THEME_STRING);
		mThemeListPreference
				.setSummary(mThemeListPreference.getEntries()[Settings.System
						.getInt(getContentResolver(), "juelian_button_theme", 0)]);
		mThemeListPreference.setOnPreferenceChangeListener(this);
		queryFilterAppInfo();
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
			mBackKeyListPreference.setSummary(mBackKeyListPreference
					.getEntries()[index]);
			Settings.System.putInt(getContentResolver(),
					"mipop_choose_what_back", index);
			if (index==5) {
				/*
				Intent intent = new Intent();
				intent.setClassName("com.juelian.mipop", "com.juelian.mipop.tools.FilterListViewActivity");
				intent.putExtra("key", "mipop_choose_what_back");
				startActivityForResult(intent, 1);
				*/
				//queryFilterAppInfo();
				showAlertDialog("mipop_choose_what_back");
			}
			return true;
		}

		if (preference == mHomeKeyListPreference) {
			int index = mHomeKeyListPreference
					.findIndexOfValue((String) newValue);
			mHomeKeyListPreference.setSummary(mHomeKeyListPreference
					.getEntries()[index]);
			Settings.System.putInt(getContentResolver(),
					"mipop_choose_what_home", index);
			if (index==5) {
				/*
				Intent intent = new Intent();
				intent.setClassName("com.juelian.mipop", "com.juelian.mipop.tools.FilterListViewActivity");
				intent.putExtra("key", "mipop_choose_what_home");
				startActivityForResult(intent, 2);
				*/
				//queryFilterAppInfo();
				//showAlertDialog("mipop_choose_what_home");
			}
			return true;
		}

		if (preference == mMenuKeyListPreference) {
			int index = mMenuKeyListPreference
					.findIndexOfValue((String) newValue);
			mMenuKeyListPreference.setSummary(mMenuKeyListPreference
					.getEntries()[index]);
			Settings.System.putInt(getContentResolver(),
					"mipop_choose_what_menu", index);
			/*
			if (index==5) {
				Intent intent = new Intent("juelian.filter.start");
				intent.putExtra("key", "mipop_choose_what_menu");
				startActivityForResult(intent, 3);
			}
			*/
			return true;
		}

		if (preference == mReclKeyListPreference) {
			int index = mReclKeyListPreference
					.findIndexOfValue((String) newValue);
			mReclKeyListPreference.setSummary(mReclKeyListPreference
					.getEntries()[index]);
			Settings.System.putInt(getContentResolver(),
					"mipop_choose_what_recl", index);
			/*
			if (index==5) {
				Intent intent = new Intent("juelian.filter.start");
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra("key", "mipop_choose_what_recl");
				startActivityForResult(intent, 4);
			}
			*/
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
	/*
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		String appName = data.getStringExtra("labelName");
		if(requestCode==1 && resultCode==2){
			mBackKeyListPreference.setSummary(appName);
		}else if (requestCode==2 && resultCode==2) {
			mHomeKeyListPreference.setSummary(appName);
		}else if (requestCode==3 && resultCode==2) {
			mMenuKeyListPreference.setSummary(appName);
		}else {
			mReclKeyListPreference.setSummary(appName);
		}
		Log.d(TAG, "result:"+appName);
	}
	*/
	
	public void queryFilterAppInfo() {
		Log.d(TAG, "queryFilterAppInfo()");
		
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		
		resolveInfos = pm
				.queryIntentActivities(mainIntent, PackageManager.MATCH_DEFAULT_ONLY);
		
		Collections.sort(resolveInfos,
				new ResolveInfo.DisplayNameComparator(pm));// 排序
		List<AppInfo> appInfos = new ArrayList<AppInfo>();
		if (!appInfos.isEmpty()) {
			appInfos.clear();
		}
		for (ResolveInfo reInfo : resolveInfos) {
			String clsName = reInfo.activityInfo.name; // 获得该应用程序的启动Activity的name
			String pkgName = reInfo.activityInfo.packageName; // 获得应用程序的包名
			String appLabel = (String) reInfo.loadLabel(pm); // 获得应用程序的Label
			Log.d(TAG, "clsName: "+clsName+"; PkgName: "+pkgName+"; applabel: "+appLabel);

			Drawable icon = reInfo.loadIcon(pm); // 获得应用程序图标
			// 创建一个AppInfo对象，并赋值
			AppInfo appInfo = new AppInfo();
			appInfo.setAppLabel(appLabel);
			appInfo.setPkgName(pkgName);
			appInfo.setClsName(clsName);
			appInfo.setAppIcon(icon);
			appInfos.add(appInfo);

			//Log.d("queryFilterAppInfo", mAppInfos.get(0).getAppLabel());

		}
		mAppInfos = appInfos;
		//Log.d("queryFilterAppInfo", mAppInfos.get(0).getAppLabel());
	}
	
	public void showAlertDialog(final String keyString){
		AlertDialog.Builder mBuilder = new AlertDialog.Builder(PreferenceSettings.this);
		//mBuilder.setMessage("sadasdasdadas");
		mBuilder.setTitle("选择一个选项");
		mBuilder.setSingleChoiceItems(new ListViewAdapter(PreferenceSettings.this, mAppInfos), -1, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				appInfo = mAppInfos.get(which);
				appPackNameString = appInfo.getPkgName();
				appClassNameString = appInfo.getClsName();
				appNameString = appInfo.getAppLabel();
				Editor editor = mSharedPreferences.edit();
				editor.putString(keyString+"_packname", appPackNameString);
				editor.putString(keyString+"_classname", appClassNameString);
				editor.commit();
				dialog.cancel();
			}
		});
		
		mBuilder.show();
	}
}
