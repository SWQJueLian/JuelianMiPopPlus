package com.juelian.mipop.api;

import android.app.Application;
import android.view.View;

import com.juelian.mipop.animation.AnimationParking;
import com.juelian.mipop.animation.AnimationTransparent;
import com.juelian.mipop.widget.MeterBack;
import com.juelian.mipop.widget.MeterBase;
import com.juelian.mipop.widget.MeterHome;
import com.juelian.mipop.widget.MeterMenu;
import com.juelian.mipop.widget.MeterRecent;
import com.juelian.mipop.widget.Until;

public class App extends Application {

	public static void hideMipop() {
		MeterBase.MeterMap.get(MeterHome.NAME).setVisibility(View.GONE);
		MeterBase.MeterMap.get(MeterRecent.NAME).setVisibility(View.GONE);
		MeterBase.MeterMap.get(MeterMenu.NAME).setVisibility(View.GONE);
		MeterBase.MeterMap.get(MeterBack.NAME).setVisibility(View.GONE);
	}

	public static void showMipop() {
		AnimationParking.stop();
		//默认出现按钮的位置，左或右边
		AnimationParking.mOriginSide = AnimationParking.LEFT;
		AnimationParking.baseX = -1;
		AnimationParking.updateAll(-1, MeterBack.baseY);
		MeterBase.MeterMap.get(MeterBack.NAME).setVisibility(View.VISIBLE);
		MeterBase.MeterMap.get(MeterHome.NAME).setVisibility(View.GONE);
		MeterBase.MeterMap.get(MeterRecent.NAME).setVisibility(View.GONE);
		MeterBase.MeterMap.get(MeterMenu.NAME).setVisibility(View.GONE);
		MeterBase.MeterMap.get(MeterBack.NAME).setAlpha(0.4f);
		AnimationParking.shrinkStart();
	}

	public void onCreate() {
		super.onCreate();
		Until.initialPop(this);
		new MeterMenu(this);
		new MeterRecent(this);
		new MeterHome(this);
		new MeterBack(this);
	}
}
