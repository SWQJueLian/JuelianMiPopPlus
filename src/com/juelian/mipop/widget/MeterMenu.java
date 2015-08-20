package com.juelian.mipop.widget;

import com.juelian.mipop.AppLog;
import com.juelian.mipop.R;

import android.app.Instrumentation;
import android.content.Context;

public class MeterMenu extends MeterBase {
	public static final String NAME = MeterMenu.class.getSimpleName();

	public MeterMenu(Context context) {
		super(context);
		Register(NAME, this);
		setSoundEffectsEnabled(true);
		setImageResource(R.drawable.menu_selector);
		setResId(R.drawable.menu, R.drawable.menu_pressed);
	}

	public void Click() {
		AppLog.i("way", "menu click");
		playSoundEffect(0);
		new Thread() {
			public void run() {
				try {
					new Instrumentation().sendKeyDownUpSync(82);
					AppLog.i("shenzhan", "MENU implement");
					return;
				} catch (Exception e) {
					AppLog.d("HouJiong", e.toString());
				}
			}
		}.start();
	}

	public void LongClick() {
		AppLog.i("way", "menu  long click");
	}
}
