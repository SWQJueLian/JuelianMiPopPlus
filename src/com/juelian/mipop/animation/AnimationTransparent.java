package com.juelian.mipop.animation;

import android.os.Handler;
import android.view.View;

import com.juelian.mipop.JueLianUtils;
import com.juelian.mipop.widget.MeterBack;
import com.juelian.mipop.widget.MeterBase;
import com.juelian.mipop.widget.MeterHome;
import com.juelian.mipop.widget.MeterMenu;
import com.juelian.mipop.widget.MeterRecent;

public class AnimationTransparent {
	private static int customAlpha = 255;
	private static int currentAlpha = 255;
	private static int endAlpha = 115;
	private static int startAlpha = 255;
	private static long time4Trans = 2000L;
	private static Handler handler4Transparent = new Handler();
	private static int periodTime = 10;
	private static Runnable runnable4Transparent = new Runnable() {
		@Override
		public void run() {
			transparenting();
		}
	};

	public static void start() {
		customAlpha = JueLianUtils.getAlpha();
		if (customAlpha!=255) {
			int subtract= customAlpha-30;
			currentAlpha = customAlpha;
			endAlpha = subtract;
			periodTime = (int) (time4Trans / Math.abs(customAlpha - subtract));
			//Log.d("juelian", "run start()"+"currentAlpha:"+currentAlpha+"; endAlpha:"+endAlpha+"; periodTime: "+periodTime);
		}else {
			periodTime = (int) (time4Trans / Math.abs(startAlpha - endAlpha));						
		}
		//Log.d("juelian", "periodTime="+periodTime);
		handler4Transparent.postDelayed(runnable4Transparent, 1L);
		MeterBase.MeterMap.get(MeterHome.NAME).setVisibility(View.GONE);
		MeterBase.MeterMap.get(MeterMenu.NAME).setVisibility(View.GONE);
		MeterBase.MeterMap.get(MeterRecent.NAME).setVisibility(View.GONE);
	}

	public static void stop() {
		currentAlpha = customAlpha;//restore
		handler4Transparent.removeCallbacks(runnable4Transparent);
		//Log.d("juelian", "run stop()"+"currentAlpha="+currentAlpha+"; customAlpha:"+customAlpha);
		MeterBase.MeterMap.get(MeterBack.NAME).setAlpha(customAlpha);
		MeterBase.MeterMap.get(MeterHome.NAME).setVisibility(View.VISIBLE);
		MeterBase.MeterMap.get(MeterMenu.NAME).setVisibility(View.VISIBLE);
		MeterBase.MeterMap.get(MeterRecent.NAME).setVisibility(View.VISIBLE);
	}

	private static void transparenting() {
		if (currentAlpha <= endAlpha) {
			//Log.d("juelian", "currentAlphaµÈÓÚendalpha:"+"cu-->"+currentAlpha+"en-->"+endAlpha);
			handler4Transparent.removeCallbacks(runnable4Transparent);
			return;
		}
		currentAlpha--;
		//Log.d("juelian", "currentAlpha--: "+currentAlpha);
		MeterBase.MeterMap.get(MeterBack.NAME).setAlpha(currentAlpha);
		handler4Transparent.postDelayed(runnable4Transparent, periodTime);
	}
}
