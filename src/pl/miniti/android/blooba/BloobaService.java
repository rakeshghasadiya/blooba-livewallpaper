/**
 * BloobaService.java
 * Author: marek.brodziak@gmail.com
 * Created: Feb 4, 2014
 * Copyright 2014 by miniti
 */
package pl.miniti.android.blooba;

import pl.miniti.android.blooba.base.Blooba;
import pl.miniti.android.blooba.base.BloobaPreferencesWrapper;
import pl.miniti.android.blooba.base.Preferences;
import pl.miniti.android.blooba.base.foreground.ForegroundProvider;
import pl.miniti.android.blooba.base.foreground.ImageForegroundProvider;
import pl.miniti.android.blooba.base.foreground.ReflectionForegroundProvider;
import pl.miniti.android.blooba.preferences.Miniature;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.service.wallpaper.WallpaperService;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

/**
 * Wallpaper service for Blooba which handles instantiation of the Blooba object
 * according to user prefernces and callbacks in regards to the external systems
 * like touch events, sensors, etc.
 */
public class BloobaService extends WallpaperService {

	private SensorManager sensorManager;
	private Sensor gravitySensor;

	/**
	 * 
	 */
	@Override
	public Engine onCreateEngine() {
		return new BloobaEngine();
	}

	/**
	 */
	private class BloobaEngine extends Engine
			implements
				SensorEventListener,
				OnSharedPreferenceChangeListener {
		private final Handler handler = new Handler();
		private final Runnable drawRunner = new Runnable() {

			@Override
			public void run() {
				draw();
			}

		};
		private Blooba blooba;
		private BloobaPreferencesWrapper bloobaPreferences;
		private boolean visible = true;
		private Bitmap background;
		private int width;
		private int height;

		/**
		 * 
		 */
		private BloobaEngine() {
			SharedPreferences preferences = PreferenceManager
					.getDefaultSharedPreferences(BloobaService.this);
			preferences.registerOnSharedPreferenceChangeListener(this);
			handler.post(drawRunner);
		}

		@Override
		public void onSharedPreferenceChanged(
				SharedPreferences sharedPreferences, String key) {
			bloobaPreferences = BloobaPreferencesWrapper
					.fromPreferences(sharedPreferences);
			if (key.equals(Preferences.ENABLE_TOUCH)) {
				// do nothing -- handled in the onTouchEvent() method
			} else if (key.equals(Preferences.INVERT_GRAVITY)) {
				blooba.setInvertGravity(bloobaPreferences.isGravityInverted());
			} else if (key.equals(Preferences.RELAX_FACTOR)) {
				blooba.setRelaxFactor(bloobaPreferences.getRelaxFactor());
			} else if (key.equals(Preferences.SPEED)) {
				blooba.setSpeed(bloobaPreferences.getSpeed());
			} else if (key.equals(Preferences.FOREGROUND_NAME)) {
				blooba.setfProvider(getProvider());
			} else if (key.equals(Preferences.BACKGROUND_NAME)) {
				loadBackground();
				if (blooba.getfProvider().isDynamic()) {
					((ReflectionForegroundProvider) blooba.getfProvider())
							.setBackground(background);
				}
			} else {
				// size && quality
				newBlooba();
			}
		}

		@Override
		public void onVisibilityChanged(boolean visible) {
			this.visible = visible;
			if (visible) {
				handler.post(drawRunner);
				if (gravitySensor != null) {
					sensorManager.registerListener(this, gravitySensor,
							SensorManager.SENSOR_DELAY_FASTEST);
				}
			} else {
				handler.removeCallbacks(drawRunner);
				sensorManager.unregisterListener(this);
			}
		}

		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder) {
			super.onSurfaceDestroyed(holder);
			this.visible = false;
			handler.removeCallbacks(drawRunner);
		}

		@Override
		public void onSurfaceChanged(SurfaceHolder holder, int format,
				int width, int height) {
			this.width = width;
			this.height = height;
			newBlooba();
			super.onSurfaceChanged(holder, format, width, height);
		}

		@Override
		public void onTouchEvent(MotionEvent event) {
			if (blooba != null && bloobaPreferences.isTouchEnabled()) {
				blooba.registerMotionEvent(event);
			} else {
				super.onTouchEvent(event);
			}
		}

		@Override
		public void onAccuracyChanged(Sensor arg0, int arg1) {
			// do nothing
		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			if (blooba != null) {
				blooba.registerSensorEvent(event);
			}
		}

		/**
		 * Renders the background and invokes Blooba callback. Afterwards a new
		 * rendering frame will be requested by the engine.
		 */
		private void draw() {
			SurfaceHolder holder = getSurfaceHolder();
			Canvas canvas = null;
			try {
				canvas = holder.lockCanvas();
				if (canvas != null && blooba != null) {
					canvas.drawBitmap(this.background, 0f, 0f, null);
					blooba.requestAnimationFrame(canvas);
				}
			} finally {
				if (canvas != null)
					holder.unlockCanvasAndPost(canvas);
			}
			handler.removeCallbacks(drawRunner);
			if (visible) {
				handler.postDelayed(drawRunner, 41);
			}
		}

		private void newBlooba() {
			if (blooba != null) {
				background.recycle();
				background = null;
				blooba.destroy();
				blooba = null;
			}

			SharedPreferences preferences = PreferenceManager
					.getDefaultSharedPreferences(BloobaService.this);
			bloobaPreferences = BloobaPreferencesWrapper
					.fromPreferences(preferences);
			sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
			gravitySensor = sensorManager
					.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			if (gravitySensor != null) {
				sensorManager.registerListener(this, gravitySensor,
						SensorManager.SENSOR_DELAY_FASTEST);
			}

			loadBackground();

			blooba = new Blooba(getProvider(), width, height, bloobaPreferences);

		}

		private int resolveResource(String name) {
			if (name.equals("moon")) {
				return R.drawable.moon;
			} else if (name.equals("kenny")) {
				return R.drawable.kenny;
			} else if (name.equals("squish")) {
				return R.drawable.squish;
			} else if (name.equals("stars")) {
				return R.drawable.bg_stars;
			} else if (name.equals("bubble")) {
				return R.drawable.bubble;
			} else {
				return R.drawable.earth;
			}
		}

		private void loadBackground() {
			if (background != null) {
				background.recycle();
			}
			int resource = resolveResource(bloobaPreferences.getBackground());
			if (width > height) {
				Matrix matrix = new Matrix();
				matrix.setRotate(90f);
				background = Bitmap.createBitmap(
						BitmapFactory.decodeResource(getResources(), resource),
						0, 0, width, height, matrix, false);
			} else {
				background = Bitmap.createScaledBitmap(
						BitmapFactory.decodeResource(getResources(), resource),
						width, height, false);
			}
		}

		private ForegroundProvider getProvider() {
			ForegroundProvider fProvider = null;
			Miniature.Type fType = Miniature.Type.values()[bloobaPreferences
					.getForegroundType()];
			switch (fType) {
				case REFLECTION :
					fProvider = new ReflectionForegroundProvider(
							BitmapFactory.decodeResource(getResources(),
									resolveResource(bloobaPreferences
											.getForeground())), this.background);
					break;
				case IMAGE :
				default :
					fProvider = new ImageForegroundProvider(
							BitmapFactory.decodeResource(getResources(),
									resolveResource(bloobaPreferences
											.getForeground())));
			}
			return fProvider;
		}

	}

}
