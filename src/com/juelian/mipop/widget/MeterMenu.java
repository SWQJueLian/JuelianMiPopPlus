package com.juelian.mipop.widget;

import android.app.Instrumentation;
import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;

import com.juelian.mipop.JueLianUtils;
import com.juelian.mipop.R;

public class MeterMenu extends MeterBase {
	public static final String NAME = MeterMenu.class.getSimpleName();

	public MeterMenu(Context context) {
		super(context);
		Register(NAME, this);
		setSoundEffectsEnabled(true);
		setImageResource(R.drawable.menu_selector);
		setResId(R.mipmap.menu, R.mipmap.menu_pressed);
	}

	public void Click() {
		Log.i("way", "menu click");
		playSoundEffect(0);
		new Thread() {
			public void run() {
				try {
					new Instrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
					Log.i("shenzhan", "MENU implement");
					return;
				} catch (Exception e) {
					Log.d("HouJiong", e.toString());
				}
			}
		}.start();
	}

	public void LongClick() {
		Log.i("juelian", "menu long click");
		JueLianUtils.switchFunction("mipop_choose_what_menu");
	}
}
