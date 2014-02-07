/**
 * BloobaForeground.java
 * Author: marek.brodziak@gmail.com
 * Created: Feb 6, 2014
 * Copyright 2014 by miniti
 */

package pl.miniti.android.blooba;

import pl.miniti.android.blooba.preferences.ImageAdapter;
import pl.miniti.android.blooba.preferences.Miniature;
import android.app.Activity;
import android.os.Bundle;
import android.widget.GridView;

/**
 */
public class BloobaForeground extends Activity {

	private static Miniature[] minis = new Miniature[]{
			new Miniature(R.drawable.bieber_xs, "Bieber"),
			new Miniature(R.drawable.earth_xs, "Earth"),
			new Miniature(R.drawable.ironman_xs, "Iron Man"),
			new Miniature(R.drawable.kenny_xs, "Kenny"),
			new Miniature(R.drawable.squish_xs, "Squishy")};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.grid_layout);

		GridView gridView = (GridView) findViewById(R.id.grid_view);

		gridView.setAdapter(new ImageAdapter(this, minis));
	}
}
