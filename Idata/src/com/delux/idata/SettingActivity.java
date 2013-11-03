package com.delux.idata;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;


public class SettingActivity extends Activity{
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);
		
		LinearLayout lanuageLayout = (LinearLayout)findViewById(R.id.lanuage);
		
		lanuageLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(SettingActivity.this, LanuageActivity.class));
			}
		});
		
	}
	
}
