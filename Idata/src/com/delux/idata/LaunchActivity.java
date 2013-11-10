package com.delux.idata;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;

public class LaunchActivity extends Activity {
	
	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.launch);
		
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				startActivity(new Intent(LaunchActivity.this, MainActivity.class));
				finish();
			}
		}, 1000);
	}
}
