package com.juelian.mipop.widget;

import android.app.Instrumentation;
import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;

import com.juelian.mipop.R;

public class MeterHome extends MeterBase {
	public static final String NAME = MeterHome.class.getSimpleName();

	public MeterHome(Context context) {
		super(context);
		Register(NAME, this);
		setSoundEffectsEnabled(true);
		//setImageResource(R.drawable.home_selector);
		//setResId(R.mipmap.home, R.mipmap.home_pressed);
	}

	public void Click() {
		Log.i("way", "home   click");
		playSoundEffect(0);
		new Thread() {
			public void run() {
				try {
					Instrumentation instrumentation = new Instrumentation();
					int keyCode = MeterHome.this.getKeyCode();
					if (keyCode==0) {
						instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_HOME);						
					}else if(keyCode==1) {
						instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
					}
				} catch (Exception e) {
					Log.d("shenzhan", e.toString());
				}
			}
		}.start();
	}

	public void LongClick() {
		Log.i("way", "home  long click");
	}
}
