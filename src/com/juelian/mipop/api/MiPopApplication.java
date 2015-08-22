package com.juelian.mipop.api;

import android.app.Application;
import android.database.ContentObserver;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

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
	
	  private ContentObserver mFirstKeyObserver = new ContentObserver(new Handler())
	  {
	    public void onChange(boolean paramAnonymousBoolean)
	    {
	      MiPopApplication.this.switchFirstKey();
	      /*
	      if (Settings.System.getInt(App.this.getApplicationContext().getContentResolver(), "MIPOP", App.this.getApplicationContext().getResources().getInteger(2131165184)) == 1) {
	    	  App.this.showMipop();
	      }
	      */
	    }
	  };

	public static void hideMipop() {
		MeterBase.MeterMap.get(MeterHome.NAME).setVisibility(View.GONE);
		MeterBase.MeterMap.get(MeterRecent.NAME).setVisibility(View.GONE);
		MeterBase.MeterMap.get(MeterMenu.NAME).setVisibility(View.GONE);
		MeterBase.MeterMap.get(MeterBack.NAME).setVisibility(View.GONE);
	}

	public static void showMipop() {
		AnimationParking.stop();
		/*
		AnimationParking.mOriginSide = AnimationParking.LEFT;
		AnimationParking.baseX = -1;
		AnimationParking.updateAll(-1, MeterBack.baseY);
		*/
		MeterBase.MeterMap.get(MeterBack.NAME).setVisibility(View.VISIBLE);
		MeterBase.MeterMap.get(MeterHome.NAME).setVisibility(View.GONE);
		MeterBase.MeterMap.get(MeterRecent.NAME).setVisibility(View.GONE);
		MeterBase.MeterMap.get(MeterMenu.NAME).setVisibility(View.GONE);
		//MeterBase.MeterMap.get(MeterBack.NAME).setAlpha(0.4f);
		AnimationParking.shrinkStart();
	}

	public void onCreate() {
		super.onCreate();
		Until.initialPop(this);
		new MeterMenu(this);
		new MeterRecent(this);
		new MeterHome(this);
		new MeterBack(this);
		switchFirstKey();
		getApplicationContext().getContentResolver().registerContentObserver(Settings.System.getUriFor("FirstKey"), false, this.mFirstKeyObserver);
	}
	
	private void switchFirstKey(){
		if (Settings.System.getInt(getApplicationContext().getContentResolver(), "FirstKey", 0) == 1){
			// back use home
			((MeterBase) MeterBase.MeterMap.get(MeterBack.NAME)).setImageResource(R.drawable.home_selector);
			((MeterBase) MeterBase.MeterMap.get(MeterBack.NAME)).setResId(R.mipmap.home, R.mipmap.home_pressed);
			((MeterBase) MeterBase.MeterMap.get(MeterHome.NAME)).setImageResource(R.drawable.back_selector);
			((MeterBase) MeterBase.MeterMap.get(MeterHome.NAME)).setResId(R.mipmap.back, R.mipmap.back_pressed);
			((MeterBase) MeterBase.MeterMap.get(MeterBack.NAME)).setKeyCode(1);
			((MeterBase) MeterBase.MeterMap.get(MeterHome.NAME)).setKeyCode(1);
	    }else {
	    	//normal
	    	((MeterBase) MeterBase.MeterMap.get(MeterBack.NAME)).setImageResource(R.drawable.back_selector);
	    	((MeterBase) MeterBase.MeterMap.get(MeterBack.NAME)).setResId(R.mipmap.back, R.mipmap.back_pressed);
	    	((MeterBase) MeterBase.MeterMap.get(MeterHome.NAME)).setImageResource(R.drawable.home_selector);
	    	((MeterBase) MeterBase.MeterMap.get(MeterHome.NAME)).setResId(R.mipmap.home, R.mipmap.home_pressed);
	    	((MeterBase) MeterBase.MeterMap.get(MeterBack.NAME)).setKeyCode(0);
	    	((MeterBase) MeterBase.MeterMap.get(MeterHome.NAME)).setKeyCode(0);
		}
	}
	
}
