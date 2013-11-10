package com.delux.idata;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;


public class MainActivity extends FragmentActivity implements OnPageChangeListener{
	private ViewPager mViewPager;
	
	private List<Fragment> mFragmentList;
	
	ImageView localSelectedView;
	ImageView idataSelectedView;
	
	public static MainActivity mainActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mainActivity = this;
		
		mFragmentList =  new ArrayList<Fragment>();
		
		mViewPager = (ViewPager) findViewById(R.id.viewPager);
		
		mViewPager.setAdapter(new TabPagerAdapter(getSupportFragmentManager(), mFragmentList));
		
		mViewPager.setOnPageChangeListener(this);
		
		localSelectedView = (ImageView) findViewById(R.id.local_selected);
		idataSelectedView = (ImageView) findViewById(R.id.idata_selected);
		
		RelativeLayout localTab = (RelativeLayout) findViewById(R.id.local);
		RelativeLayout idataTab = (RelativeLayout) findViewById(R.id.idata);
		final LinearLayout bottomFunction = (LinearLayout) findViewById(R.id.bottom_function);
		final LinearLayout mutilFunction = (LinearLayout) findViewById(R.id.mutil_function);
		
		View settingView = findViewById(R.id.setting);
		View mutilChooseView = findViewById(R.id.mutil_choose);
		
		settingView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, SettingActivity.class));

			}
		});
		
		mutilChooseView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
//					bottomFunction.setVisibility(View.GONE);
//					mutilFunction.setVisibility(View.VISIBLE);
					
					  if(curSelectPosition == 0){
						  ((MutilChooseCallBack)mFragmentList.get(0)).onClick(bottomFunction, mutilFunction);
					  }else{
						  ((MutilChooseCallBack)mFragmentList.get(1)).onClick(bottomFunction, mutilFunction);
					  }
			}
		});
		
		localTab.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mViewPager.setCurrentItem(0);
			}
		});
		
		idataTab.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					mViewPager.setCurrentItem(1);
				}
			});
		
			Fragment localFragment = new LocalFragment();
			mFragmentList.add(localFragment);
		
			Fragment iDataFragment = new IDataFragment();
			mFragmentList.add(iDataFragment);
	}
	
	@Override
	public void onPageScrollStateChanged(int arg0) {
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		
	}

	@Override
	public void onPageSelected(int position) {
		
		if(position == 0){
			curSelectPosition = 0;
			localSelectedView.setVisibility(View.VISIBLE);
			idataSelectedView.setVisibility(View.GONE);
		}else{
			curSelectPosition = 1;
			localSelectedView.setVisibility(View.GONE);
			idataSelectedView.setVisibility(View.VISIBLE);
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.i("MainActivity", "onDestroy");
	}
	
	@Override
	public void finish() {
		Log.i("MainActivity", "finish");
		super.finish();
	}
	
	public interface BackKeyEvent{
		public void onBack();
	}
	
	public interface MutilChooseCallBack{
		public void onClick(LinearLayout bottomLayout, LinearLayout mutilChooseLayout);
	}
	
	private int curSelectPosition;
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	  if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
		  
		  if(curSelectPosition == 0){
			  ((BackKeyEvent)mFragmentList.get(0)).onBack();
		  }else{
			  ((BackKeyEvent)mFragmentList.get(1)).onBack();
		  }
          return false;
	  }
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		String fromPath = data.getStringExtra("fromPath");
		Log.i("fromPath", fromPath);
		File file = new File(fromPath);
		
		ListView filelistView;
		 if(curSelectPosition == 0){
			 filelistView = ((LocalFragment)mFragmentList.get(0)).filelistView;
		  }else{
			  filelistView = ((LocalFragment)mFragmentList.get(0)).filelistView;
		  }
		LocalFileListAdapter localFileListAdapter = (LocalFileListAdapter)filelistView.getAdapter();
		localFileListAdapter.setFileArray(file.getParentFile().listFiles());
		localFileListAdapter.notifyDataSetChanged();
		super.onActivityResult(requestCode, resultCode, data);
	}
}
