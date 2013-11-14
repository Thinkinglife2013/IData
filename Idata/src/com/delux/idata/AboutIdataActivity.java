package com.delux.idata;

import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.os.Bundle;


public class AboutIdataActivity extends Activity{
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_idata);
		
	}
	
	@Override
	protected void onResume() {
		MobclickAgent.onResume(this);
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		MobclickAgent.onPause(this);
		super.onPause();
	}

}
