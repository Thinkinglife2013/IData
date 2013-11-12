package com.delux.idata;

import java.net.URLEncoder;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;


public class SettingActivity extends Activity{
	public static SettingActivity settingActivity; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);
		
		settingActivity = this;
		
		LinearLayout lanuageLayout = (LinearLayout)findViewById(R.id.lanuage);
		LinearLayout routeLayout = (LinearLayout)findViewById(R.id.route);
		LinearLayout aboutLayout = (LinearLayout)findViewById(R.id.about);
		
		lanuageLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(SettingActivity.this, LanuageActivity.class));
			}
		});
		
		routeLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent= new Intent();        
			    intent.setAction("android.intent.action.VIEW");    
			    
			    Uri content_url;
			    	content_url = Uri.parse("http://192.168.169.1/");   
			   
			    intent.setData(content_url);  
			    startActivity(intent);
			}
		});
		
		aboutLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(SettingActivity.this, AboutIdataActivity.class));
			}
		});
		
	}
	
}
