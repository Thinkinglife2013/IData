package com.delux.idata;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


public class MainActivity extends FragmentActivity implements OnPageChangeListener{
	private ViewPager mViewPager;
	
	private List<Fragment> mFragmentList;
	
	ImageView localSelectedView;
	ImageView idataSelectedView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
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
				// TODO Auto-generated method stub
			}
		});
		
		mutilChooseView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				bottomFunction.setVisibility(View.GONE);
				mutilFunction.setVisibility(View.VISIBLE);
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
	
	
	public interface BackKeyEvent{
		public void onBack();
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
	

}
