package com.juelian.mipop;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.AudioManager;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.juelian.mipop.widget.MeterBase;

public class JueLianUtils {

	public static Context mContext = MeterBase.mContext;
	private static AudioManager audioManager;
	
	public static int getAlpha() {
		return Settings.System.getInt(mContext.getContentResolver(),
				"juelian_button_alpha", 255);
	}

	public static ArrayList<String> getWhiteList() {
		BufferedReader br = null;
		ArrayList<String> aList = new ArrayList<String>();
		// default white list
		aList.add("com.android.phone");
		aList.add("com.android.systemui");
		aList.add("com.android.mms");
		aList.add("com.android.contacts");
		aList.add(mContext.getPackageName());
		File file = new File(mContext.getFilesDir().getAbsolutePath()
				+ "/white_list.txt");
		try {
			br = new BufferedReader(new FileReader(file));
			String packname = null;
			while ((packname = br.readLine()) != null) {
				if (!aList.contains(packname)) {
					aList.add(packname);
				}
			}
		} catch (FileNotFoundException e) {
			Log.e("mijl-->", "can't not found" + file.toString());
			Log.e("mijl-->", "add default packname list");
			e.printStackTrace();
		} catch (IOException e) {
			Log.e("mijl-->",
					"reader is closed or some other I/O error occurs");
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return aList;
	}

	public static void KillApp(Context context) {
		ArrayList<String> aList = getWhiteList();
		ActivityManager mActivityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> mList = mActivityManager.getRunningTasks(1);
		if (mList.iterator().hasNext()) {
			// 迭代获取一个元素
			PackageManager pm = context.getPackageManager();
			RunningTaskInfo info = mList.iterator().next();
			String packName;
			String appName;
			// 当前栈的活跃actibity
			packName = info.topActivity.getPackageName();
			//
			try {
				// ApplicationInfo ai = ;
				appName = pm.getApplicationLabel(
						pm.getApplicationInfo(packName, 0)).toString();
				if (aList.contains(packName)) {
					Toast.makeText(context,
							"无法结束白名单中的：" + "\"" + appName + "\"", 1).show();
					return;
				}
				/*
				 * for(String s : aList){ if (packName.equals(s)) {
				 * Log.d("mijl-->", "this"+ packName + "is whitelist");
				 * Toast.makeText(context, "无法结束白名单中的：" + "\""+appName + "\"",
				 * 1).show(); return; } }
				 */
				Toast.makeText(context, "已停止" + "\"" + appName + "\"", 1)
						.show();
				// 新建一个实例
				IActivityManager iActivityManager = ActivityManagerNative
						.getDefault();
				// 从浅里面移除（最近任务）
				iActivityManager.removeTask(info.id, 1);
				// 关闭程序
				iActivityManager.forceStopPackage(packName, -2);

			} catch (NameNotFoundException e1) {
				e1.printStackTrace();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	public static int getSystemInt(Context context, String strKey, int defValues) {
		return Settings.System.getInt(context.getContentResolver(), strKey,
				defValues);
	}
	/*
	public static boolean isMIUI(){
		return !SystemProperties.get("ro.miui.ui.version.code").isEmpty();
	}
	*/
	public static void switchFunction() {
		int firstkey = getSystemInt(mContext, "FirstKey", 0);
		String whatKey = "";
		if (firstkey == 0) {
			whatKey = "mipop_choose_what_back";
		} else if (firstkey == 1) {
			whatKey = "mipop_choose_what_home";
		}
		Log.e("mijl-->", firstkey + "---------");
		Log.e("mijl-->", whatKey + "---------");
		switchFunction(whatKey);
	}

	public static void switchFunction2() {
		int firstkey = getSystemInt(mContext, "FirstKey", 0);
		String whatKey = "";
		if (firstkey == 0) {
			whatKey = "mipop_choose_what_home";
		} else if (firstkey == 1) {
			whatKey = "mipop_choose_what_back";
		}
		Log.e("mijl-->", firstkey + "---------");
		Log.e("mijl-->", whatKey + "---------");
		switchFunction(whatKey);
	}

	public static void switchFunction(String whatKey) {
		switch (getSystemInt(mContext, whatKey, 0)) {
		case 0:// nothing
			break;

		case 1:// lock screen
			new Thread() {
				boolean flag = true;

				public void run() {
					if (flag) {
						try {
							new Instrumentation()
									.sendKeyDownUpSync(KeyEvent.KEYCODE_POWER);
						} catch (Exception e) {
							Log.e("mijl-->",
									"deadobjectException,sendKeyDown fail can not shutdown screen");
						}
						flag = false;
					}
				};
			}.start();
			break;

		case 2:// kill current app
			KillApp(mContext);
			break;
			
		case 3:
			audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
			audioManager.adjustVolume(AudioManager.ADJUST_RAISE,AudioManager.FLAG_PLAY_SOUND);
			break;
			
		case 4:
			audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
			audioManager.adjustVolume(AudioManager.ADJUST_LOWER,AudioManager.FLAG_PLAY_SOUND);
			break;
		}
		
	}
}
