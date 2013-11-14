package com.delux.idata;

import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;


public class LanuageActivity extends Activity{
	 private final String action = "com.delux.idata.language";
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lanuage_setting);
		
		RadioButton CNButton = (RadioButton)findViewById(R.id.cn);
		RadioButton ENButton = (RadioButton)findViewById(R.id.en);
		RadioButton TWButton = (RadioButton)findViewById(R.id.tw);
		
		Intent i = getIntent();
		int curLanuge = i.getIntExtra("curLanuage", 0);
		
		switch (curLanuge) {
			case 0:
				CNButton.setChecked(true);
				break;
			case 1:
				TWButton.setChecked(true);
				break;
			case 2:
				ENButton.setChecked(true);
				break;
			default:
				break;
		}
		
		CNButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					 Intent i = new Intent(action);
					  switchLanguage(Locale.CHINA);
                      finish();
                      sendBroadcast(i);
				}
			}
		});
		
		ENButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if(isChecked){
						 Intent i = new Intent(action);
						  switchLanguage(Locale.US);
	                      finish();
	                      sendBroadcast(i);
					}
				}
		});
		
		TWButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					 Intent i = new Intent(action);
					  switchLanguage(Locale.TAIWAN);
                     finish();
                     sendBroadcast(i);
				}
			}
		});
	}
	
	  public void switchLanguage(Locale locale) {
          Resources resources = getResources();// 获得res资源对象
          Configuration config = resources.getConfiguration();// 获得设置对象
          DisplayMetrics dm = resources.getDisplayMetrics();// 获得屏幕参数：主要是分辨率，像素等。
          config.locale = locale; // 简体中文
          resources.updateConfiguration(config, dm);
  }
	

}
