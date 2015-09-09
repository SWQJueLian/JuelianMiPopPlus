package com.juelian.mipop.widget;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;

import com.juelian.mipop.animation.AnimationParking;

public abstract class MeterBase extends ImageView {
	private int mKeyCode;
	public static Map<String, MeterBase> MeterMap = new HashMap<String, MeterBase>();
	public static int baseX = 0;
	public static int baseY = Until.SCREEM_HEIGHT / 2;
	public static int mLeftMargin = 0;
	public static boolean mTouchDown = false;
	public static Paint paint = new Paint();
	public static Context mContext;
	private Handler handler4LongClick = new Handler();
	private boolean hasMoved = false;
	public boolean isLongClick = false;
	private final long mTime4LongClick = 1000L;
	public WindowManager mWindowManager = null;
	private int resId = 0;
	private int resIdPressed = 0;
	private Runnable runnable4LongClick = new Runnable() {
		public void run() {
			isLongClick = true;
			LongClick();
		}
	};
	private Handler mHandlerPosXY = new Handler();
	private Runnable mMemoryXY = new Runnable() {
		public void run() {
			if (!AnimationParking.mOriginSide) {
				Settings.System.putInt(MeterBase.mContext.getContentResolver(),
						"MipopPosX", Until.SCREEM_WIDTH - Until.IMAGE_WIDTH);
			} else {
				Settings.System.putInt(MeterBase.mContext.getContentResolver(),
						"MipopPosX", 0x0);
			}
			Settings.System.putInt(MeterBase.mContext.getContentResolver(),
					"MipopPosY", AnimationParking.baseY);
		}
	};
	public WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();

	public MeterBase(Context context) {
		super(context);
		mContext = context;
		baseX = Settings.System.getInt(mContext.getContentResolver(),
				"MipopPosX", 0x0);
		baseY = Settings.System.getInt(mContext.getContentResolver(),
				"MipopPosY", (Until.SCREEM_HEIGHT / 2));
		this.mWindowManager = ((WindowManager) context.getApplicationContext()
				.getSystemService(Context.WINDOW_SERVICE));
		this.wmParams.type = LayoutParams.TYPE_SYSTEM_ALERT;
		this.wmParams.format = PixelFormat.TRANSPARENT;
		wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE
				| LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
		this.wmParams.gravity = Gravity.LEFT | Gravity.TOP;
		this.wmParams.x = baseX;
		this.wmParams.y = baseY;
		this.wmParams.height = Until.IMAGE_WIDTH;
		this.wmParams.width = Until.IMAGE_WIDTH;
		this.mWindowManager.addView(this, wmParams);
	}

	public static Map<String, MeterBase> getMap() {
		return MeterMap;
	}

	protected abstract void Click();

	protected abstract void LongClick();

	public void Register(String name, MeterBase key) {
		MeterMap.put(name, key);
	}

	public void moved() {
		hasMoved = true;
		handler4LongClick.removeCallbacks(runnable4LongClick);
	}
	
	public static boolean isShark(){
		return AnimationParking.isShark;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		//°´ÏÂ
		case MotionEvent.ACTION_DOWN:
			Log.i("OUT", "base DOWN" + hasMoved);
			if (isShark()) {
				Log.d("mijl-->", "base DOWN"+"isShark:"+isShark());
				MeterBase.MeterMap.get(MeterHome.NAME).setClickable(false);
				MeterBase.MeterMap.get(MeterMenu.NAME).setClickable(false);
				MeterBase.MeterMap.get(MeterRecent.NAME).setClickable(false);
			}
			setImageResource(resIdPressed);
			handler4LongClick.postDelayed(runnable4LongClick, mTime4LongClick);
			AnimationParking.stop();
			return true;
			
		case MotionEvent.ACTION_MOVE:
			return true;
		
		//Ì§Æð
		case MotionEvent.ACTION_UP:
			Log.i("OUT", "base UP" + this.hasMoved);
			if (isShark()) {
				Log.d("mijl-->", "base UP"+"isShark:"+isShark());
				MeterBase.MeterMap.get(MeterHome.NAME).setClickable(true);
				MeterBase.MeterMap.get(MeterMenu.NAME).setClickable(true);
				MeterBase.MeterMap.get(MeterRecent.NAME).setClickable(true);
			}
			setImageResource(resId);
			this.handler4LongClick.removeCallbacks(runnable4LongClick);
			if (!hasMoved) {
				if (!isLongClick) {
					Log.i("Suhao.Click", "MeterBase.UP, Click");
					Click();
				}
			}
			if (isLongClick) {
				Log.i("Suhao.Click", "MeterBase.UP, Long click");
			} else if (hasMoved) {
				Log.i("Suhao.Click", "MeterBase.UP, has moved");
			}

			hasMoved = false;
			isLongClick = false;
			mHandlerPosXY.removeCallbacks(mMemoryXY);
			mHandlerPosXY.postDelayed(mMemoryXY, 1000L);
			AnimationParking.start();
			return true;

		default:
			break;
		}
		Log.i("OUT", "base ACTION_OUTSIDE" + this.hasMoved);
		AnimationParking.shrinkStart();
		return true;
	}

	public void setResId(int normal, int pressed) {
		resId = normal;
		resIdPressed = pressed;
	}

	public void update(int x, int y) {
		wmParams.x = x;
		wmParams.y = y;
		mWindowManager.updateViewLayout(this, wmParams);
	}

	public int getKeyCode() {
		return mKeyCode;
	}

	public void setKeyCode(int mKeyCode) {
		this.mKeyCode = mKeyCode;
	}
}
