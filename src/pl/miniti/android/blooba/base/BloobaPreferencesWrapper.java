/**
 * BloobaPreferencesWrapper.java
 * Author: marek.brodziak@gmail.com
 * Created: Feb 5, 2014
 * Copyright 2014 by miniti
 */
package pl.miniti.android.blooba.base;

import pl.miniti.android.blooba.preferences.Miniature;
import android.content.SharedPreferences;

/**
 * Class wrapping user prefernces for the Blooba
 */
public class BloobaPreferencesWrapper {

	private boolean touchEnabled;
	private boolean gravityInverted;
	private int quality;
	private int speed;
	private float size;
	private float relaxFactor;
	private String foreground;
	private int foregroundType;
	private String background;
	private int backgroundType;

	/**
	 * Private constructor - use factory method
	 */
	private BloobaPreferencesWrapper() {
	}

	/**
	 * Creates new object based on user preferences
	 * 
	 * @param prefs
	 *            shared prefences
	 * @return initialized object with all preferences in place
	 */
	public static BloobaPreferencesWrapper fromPreferences(
			SharedPreferences prefs) {
		BloobaPreferencesWrapper settings = new BloobaPreferencesWrapper();
		settings.touchEnabled = prefs.getBoolean(Preferences.ENABLE_TOUCH,
				Boolean.TRUE);
		settings.gravityInverted = prefs.getBoolean(Preferences.INVERT_GRAVITY,
				Boolean.FALSE);
		settings.quality = Integer.valueOf(prefs.getString("quality", "40"));
		settings.size = Float.valueOf(prefs.getString("size", "0.5"));
		settings.relaxFactor = Float.valueOf(prefs.getString(
				Preferences.RELAX_FACTOR, "0.9"));
		settings.speed = Integer.valueOf(prefs.getString(Preferences.SPEED,
				"10"));
		settings.foreground = prefs.getString(Preferences.FOREGROUND_NAME,
				"earth");
		settings.foregroundType = prefs.getInt("foreground_type",
				Miniature.Type.IMAGE.ordinal());
		settings.background = prefs.getString(Preferences.BACKGROUND_NAME,
				"stars");
		settings.backgroundType = prefs.getInt("background_type",
				Miniature.Type.IMAGE.ordinal());
		return settings;
	}

	public boolean isTouchEnabled() {
		return touchEnabled;
	}

	public boolean isGravityInverted() {
		return gravityInverted;
	}

	public int getQuality() {
		return quality;
	}

	public float getSize() {
		return size;
	}

	public float getRelaxFactor() {
		return relaxFactor;
	}

	public void setTouchEnabled(boolean touchEnabled) {
		this.touchEnabled = touchEnabled;
	}

	public void setGravityInverted(boolean gravityInverted) {
		this.gravityInverted = gravityInverted;
	}

	public void setQuality(int quality) {
		this.quality = quality;
	}

	public void setSize(float size) {
		this.size = size;
	}

	public void setRelaxFactor(float relaxFactor) {
		this.relaxFactor = relaxFactor;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public String getForeground() {
		return foreground;
	}

	public void setForeground(String foreground) {
		this.foreground = foreground;
	}

	public int getForegroundType() {
		return foregroundType;
	}

	public void setForegroundType(int foregroundType) {
		this.foregroundType = foregroundType;
	}

	public String getBackground() {
		return background;
	}

	public void setBackground(String background) {
		this.background = background;
	}

	public int getBackgroundType() {
		return backgroundType;
	}

	public void setBackgroundType(int backgroundType) {
		this.backgroundType = backgroundType;
	}

}
