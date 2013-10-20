package com.delux.idata;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.ImageView;
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
				// TODO Auto-generated method stub
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
		
			Fragment IDataFragment = new IDataFragment();
			mFragmentList.add(IDataFragment);
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
			localSelectedView.setVisibility(View.VISIBLE);
			idataSelectedView.setVisibility(View.GONE);
		}else{
			localSelectedView.setVisibility(View.GONE);
			idataSelectedView.setVisibility(View.VISIBLE);
		}
	}
	

}
