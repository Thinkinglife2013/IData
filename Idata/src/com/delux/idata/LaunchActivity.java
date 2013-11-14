package com.delux.idata;

import com.umeng.analytics.MobclickAgent;

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
	
	@Override
	public void onResume() {
		MobclickAgent.onResume(this);
		super.onResume();
	}
	
	@Override
	public void onPause() {
		MobclickAgent.onPause(this);
		super.onPause();
	}
}
