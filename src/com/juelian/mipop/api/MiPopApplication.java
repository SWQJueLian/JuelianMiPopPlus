package com.juelian.mipop.api;

import android.app.Application;
import android.database.ContentObserver;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import com.juelian.mipop.JueLianUtils;
import com.juelian.mipop.R;
import com.juelian.mipop.animation.AnimationParking;
import com.juelian.mipop.animation.AnimationTransparent;
import com.juelian.mipop.widget.MeterBack;
import com.juelian.mipop.widget.MeterBase;
import com.juelian.mipop.widget.MeterHome;
import com.juelian.mipop.widget.MeterMenu;
import com.juelian.mipop.widget.MeterRecent;
import com.juelian.mipop.widget.Until;

public class MiPopApplication extends Application {
	
	static{
		mInstance = null;
	}
	
	private static MiPopApplication mInstance;

	private ContentObserver mFirstKeyObserver = new ContentObserver(
			new Handler()) {
		public void onChange(boolean paramAnonymousBoolean) {
			switchTheme();
		}
	};

	private ContentObserver mButtonAlpha = new ContentObserver(new Handler()) {
		public void onChange(boolean paramAnonymousBoolean) {
			setAlphas();
			AnimationTransparent.start();
		}
	};
	
	private ContentObserver mTheme = new ContentObserver(new Handler()) {
		public void onChange(boolean paramAnonymousBoolean) {
			switchTheme();
		}
	};
	
	  private ContentObserver mMipopOn = new ContentObserver(new Handler())
	  {
	    public void onChange(boolean paramAnonymousBoolean)
	    {
	    	switchMipop();
	    }
	  };
	  
	public static MiPopApplication getInstance(){
		return mInstance;
	}

	public static void hideMipop() {
		MeterBase.MeterMap.get(MeterHome.NAME).setVisibility(View.GONE);
		MeterBase.MeterMap.get(MeterRecent.NAME).setVisibility(View.GONE);
		MeterBase.MeterMap.get(MeterMenu.NAME).setVisibility(View.GONE);
		MeterBase.MeterMap.get(MeterBack.NAME).setVisibility(View.GONE);
	}

	public static void showMipop() {
		AnimationParking.stop();
		MeterBase.MeterMap.get(MeterBack.NAME).setVisibility(View.VISIBLE);
		MeterBase.MeterMap.get(MeterHome.NAME).setVisibility(View.GONE);
		MeterBase.MeterMap.get(MeterRecent.NAME).setVisibility(View.GONE);
		MeterBase.MeterMap.get(MeterMenu.NAME).setVisibility(View.GONE);
		AnimationParking.shrinkStart();
	}

	public void onCreate() {
		super.onCreate();
		mInstance = this;
		Until.initialPop(this);
		new MeterMenu(this);
		new MeterRecent(this);
		new MeterHome(this);
		new MeterBack(this);
		//switchFirstKey();
		switchTheme();
		setAlphas();
		switchMipop();
		getApplicationContext().getContentResolver().registerContentObserver(
				Settings.System.getUriFor(JueLianUtils.FIRKEY), false,
				this.mFirstKeyObserver);
		getApplicationContext().getContentResolver().registerContentObserver(
				Settings.System.getUriFor("juelian_button_alpha_md"), false,
				this.mButtonAlpha);
		getApplicationContext().getContentResolver().registerContentObserver(
				Settings.System.getUriFor("juelian_button_theme"), false,
				this.mTheme);
		getApplicationContext().getContentResolver().registerContentObserver(
				Settings.System.getUriFor("juelian_mipop_on"), true,
				this.mMipopOn);
	}

	/*
	private void switchFirstKey() {
		if (Settings.System.getInt(
				getApplicationContext().getContentResolver(), "FirstKey", 0) == 1) {
			// back use home
			((MeterBase) MeterBase.MeterMap.get(MeterBack.NAME))
					.setImageResource(R.drawable.home_selector);
			((MeterBase) MeterBase.MeterMap.get(MeterBack.NAME)).setResId(
					R.mipmap.home, R.mipmap.home_pressed);
			((MeterBase) MeterBase.MeterMap.get(MeterHome.NAME))
					.setImageResource(R.drawable.back_selector);
			((MeterBase) MeterBase.MeterMap.get(MeterHome.NAME)).setResId(
					R.mipmap.back, R.mipmap.back_pressed);
			((MeterBase) MeterBase.MeterMap.get(MeterBack.NAME)).setKeyCode(1);
			((MeterBase) MeterBase.MeterMap.get(MeterHome.NAME)).setKeyCode(1);
		} else {
			// normal
			((MeterBase) MeterBase.MeterMap.get(MeterBack.NAME))
					.setImageResource(R.drawable.back_selector);
			((MeterBase) MeterBase.MeterMap.get(MeterBack.NAME)).setResId(
					R.mipmap.back, R.mipmap.back_pressed);
			((MeterBase) MeterBase.MeterMap.get(MeterHome.NAME))
					.setImageResource(R.drawable.home_selector);
			((MeterBase) MeterBase.MeterMap.get(MeterHome.NAME)).setResId(
					R.mipmap.home, R.mipmap.home_pressed);
			((MeterBase) MeterBase.MeterMap.get(MeterBack.NAME)).setKeyCode(0);
			((MeterBase) MeterBase.MeterMap.get(MeterHome.NAME)).setKeyCode(0);
		}
	}
	*/

	public void switchMipop(){
		int i = Settings.System.getInt(getApplicationContext()
				.getContentResolver(), "juelian_mipop_on", 0);
		if (i==0) {
			hideMipop();
		}else {
			showMipop();
		}
	}

	public void setAlphas() {
		int i = Settings.System.getInt(getApplicationContext()
				.getContentResolver(), "juelian_button_alpha_md", 255);
		((MeterBase) MeterBase.MeterMap.get(MeterBack.NAME)).setAlpha(i);
		((MeterBase) MeterBase.MeterMap.get(MeterHome.NAME)).setAlpha(i);
		((MeterBase) MeterBase.MeterMap.get(MeterMenu.NAME)).setAlpha(i);
		((MeterBase) MeterBase.MeterMap.get(MeterRecent.NAME)).setAlpha(i);
		Log.i("mijl-->", "mipop button alpha: " + i);
	}
	
	public void switchTheme(){
		switch (Settings.System.getInt(getApplicationContext().getContentResolver(), "juelian_button_theme", 0)) {
		
		case 0:
			if (Settings.System.getInt(
					getApplicationContext().getContentResolver(), JueLianUtils.FIRKEY, 0) == 1) {
				// back use home
				MeterBase.MeterMap.get(MeterBack.NAME).setImageResource(
						R.drawable.home_selector);
				MeterBase.MeterMap.get(MeterBack.NAME).setResId(R.mipmap.home,
						R.mipmap.home_pressed);
				MeterBase.MeterMap.get(MeterHome.NAME).setImageResource(
						R.drawable.back_selector);
				MeterBase.MeterMap.get(MeterHome.NAME).setResId(R.mipmap.back,
						R.mipmap.back_pressed);
			} else {
				// normal
				MeterBase.MeterMap.get(MeterBack.NAME).setImageResource(
						R.drawable.back_selector);
				MeterBase.MeterMap.get(MeterBack.NAME).setResId(R.mipmap.back,
						R.mipmap.back_pressed);
				MeterBase.MeterMap.get(MeterHome.NAME).setImageResource(
						R.drawable.home_selector);
				MeterBase.MeterMap.get(MeterHome.NAME).setResId(R.mipmap.home,
						R.mipmap.home_pressed);
			}
			MeterBase.MeterMap.get(MeterMenu.NAME).setImageResource(
					R.drawable.menu_selector);
			MeterBase.MeterMap.get(MeterMenu.NAME).setResId(R.mipmap.menu,
					R.mipmap.menu_pressed);
			MeterBase.MeterMap.get(MeterRecent.NAME).setImageResource(
					R.drawable.recent_selector);
			MeterBase.MeterMap.get(MeterRecent.NAME).setResId(R.mipmap.recent,
					R.mipmap.recent_pressed);
			break;
			
		case 1:
			if (Settings.System.getInt(
					getApplicationContext().getContentResolver(), JueLianUtils.FIRKEY, 0) == 1) {
				// back use home
				MeterBase.MeterMap.get(MeterBack.NAME).setImageResource(
						R.drawable.mhome_selector);
				MeterBase.MeterMap.get(MeterBack.NAME).setResId(R.mipmap.mhome,
						R.mipmap.mhomehome_pressed);
				MeterBase.MeterMap.get(MeterHome.NAME).setImageResource(
						R.drawable.mback_selector);
				MeterBase.MeterMap.get(MeterHome.NAME).setResId(
						R.mipmap.mhomeback, R.mipmap.mhomeback_pressed);
			} else {
				// normal
				MeterBase.MeterMap.get(MeterBack.NAME).setImageResource(
						R.drawable.mback_selector);
				MeterBase.MeterMap.get(MeterBack.NAME).setResId(
						R.mipmap.mhomeback, R.mipmap.mhomeback_pressed);
				MeterBase.MeterMap.get(MeterHome.NAME).setImageResource(
						R.drawable.mhome_selector);
				MeterBase.MeterMap.get(MeterHome.NAME).setResId(R.mipmap.mhome,
						R.mipmap.mhomehome_pressed);
			}
			MeterBase.MeterMap.get(MeterMenu.NAME).setImageResource(
					R.drawable.mmenu_selector);
			MeterBase.MeterMap.get(MeterMenu.NAME).setResId(R.mipmap.mhomemenu,
					R.mipmap.mhomemenu_pressed);
			MeterBase.MeterMap.get(MeterRecent.NAME).setImageResource(
					R.drawable.mrecent_selector);
			MeterBase.MeterMap.get(MeterRecent.NAME).setResId(R.mipmap.mhomerecent,
					R.mipmap.mhomerecent_pressed);
			break;
		}
		
	}

}
