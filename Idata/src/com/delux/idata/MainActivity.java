package com.delux.idata;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
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
import android.widget.Toast;


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
		
		ListView filelistView;
		 if(curSelectPosition == 0){
			 File file = new File(fromPath);
			 filelistView = ((LocalFragment)mFragmentList.get(0)).filelistView;
			LocalFileListAdapter localFileListAdapter = (LocalFileListAdapter)filelistView.getAdapter();
			localFileListAdapter.setFileArray(file.getParentFile().listFiles());
			localFileListAdapter.notifyDataSetChanged();
		  }else{
			  SmbFile file = getSmbFile(fromPath);
			  filelistView = ((IDataFragment)mFragmentList.get(1)).filelistView;
				FileListAdapter fileListAdapter = (FileListAdapter)filelistView.getAdapter();
				try {
					fileListAdapter.setFileArray(getSmbFile(file.getParent()).listFiles());
				} catch (SmbException e) {
					e.printStackTrace();
				}
				fileListAdapter.notifyDataSetChanged();
		  }
	
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	/**
	 *  连接idata获取文件对象           
	 */
	private SmbFile getSmbFile(String url){
		 try {
			jcifs.Config.setProperty( "jcifs.smb.lmCompatibility", "0");
			jcifs.Config.setProperty( "jcifs.smb.client.responseTimeout", "5000");
			
	        NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(null, "admin", "admin");
        
			SmbFile file = new SmbFile(url, auth);
			return file;
		} catch (MalformedURLException e) {
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					Toast.makeText(MainActivity.this, R.string.not_connect_idata, 0).show();
				}
			});
			e.printStackTrace();
			return null;
		}
	}
}
