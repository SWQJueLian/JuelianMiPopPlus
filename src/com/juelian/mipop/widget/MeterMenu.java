package com.juelian.mipop.widget;

import android.app.Instrumentation;
import android.content.Context;
import android.util.Log;

import com.juelian.mipop.R;

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
		Log.i("way", "menu click");
		playSoundEffect(0);
		new Thread() {
			public void run() {
				try {
					new Instrumentation().sendKeyDownUpSync(82);
					Log.i("shenzhan", "MENU implement");
					return;
				} catch (Exception e) {
					Log.d("HouJiong", e.toString());
				}
			}
		}.start();
	}

	public void LongClick() {
		Log.i("way", "menu  long click");
	}
}
