package me.pontue.cliente.activity;

import me.pontue.cliente.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class SplashActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.splash);

		Handler h = new Handler();
		h.postDelayed(new Runnable() {

			@Override
			public void run() {
				startActivity(new Intent(SplashActivity.this, HomeActivity.class));
				finish();
			}
		}, 3000);

		Handler h2 = new Handler();
		final ImageView img = (ImageView) findViewById(R.id.imgLogoParte2);
		h2.postDelayed(new Runnable() {

			@Override
			public void run() {
				img.setVisibility(View.VISIBLE);
			}
		}, 1500);
	}

	public void run() {

	}
}
