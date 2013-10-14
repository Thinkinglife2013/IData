package com.delux.idata;

import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.delux.util.FileUtil;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class IDataFilesActivity extends Activity {
	private String curParent;
	private ListView listView;
	private ImageView backView;
	private TextView titleTextView;
	Map<Integer, ArrayList> categoryMap = new HashMap<Integer, ArrayList>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.file_list);
		
		listView = (ListView)findViewById(R.id.subscription_list);
		titleTextView = (TextView)findViewById(R.id.title_text);
		backView = (ImageView)findViewById(R.id.imgback);
		backView.setVisibility(View.GONE);
		
		Intent intent = getIntent();
		curParent = intent.getStringExtra("curParent");
		final int type = intent.getIntExtra("type",0);
//		ArrayList<SmbFile> fileList = (ArrayList<SmbFile>)intent.getSerializableExtra(type);
		
		final FileListAdapter listAdapter = new FileListAdapter(IDataFilesActivity.this, null);
		   new Thread(new Runnable() {
				
				@Override
				public void run() {
				try {
					jcifs.Config.setProperty( "jcifs.smb.lmCompatibility", "0");
			        NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(null, "admin", "admin");
			        
			        SmbFile file = new SmbFile("smb://192.168.169.1/Share/",auth);
			        SmbFile[] files = file.listFiles();
			        
			        curParent = files[0].getPath();
			        final SmbFile[] subfiles = files[0].listFiles();
			        
			        if(subfiles.length > 0){
			        	
			        	ArrayList<SmbFile> photoList = new ArrayList<SmbFile>();
			        	ArrayList<SmbFile> videoList = new ArrayList<SmbFile>();
			        	ArrayList<SmbFile> musicList = new ArrayList<SmbFile>();
			        	ArrayList<SmbFile> docList = new ArrayList<SmbFile>();
			        	ArrayList<SmbFile> folderList = new ArrayList<SmbFile>();
			        	
						for(SmbFile	subfile : subfiles){
							if(subfile.isDirectory()){
								folderList.add(subfile);
							}else{
								int category = FileUtil.getFileCategory(getName(subfile));
								if(FileUtil.PHOTO == category){
									photoList.add(subfile);
								}else if(FileUtil.MUSIC == category){
									musicList.add(subfile);
								}else if(FileUtil.VIDEO == category){
									videoList.add(subfile);
								}else if(FileUtil.DOC == category){
									docList.add(subfile);
								}
							}
						}
						categoryMap.put(FileUtil.DOC, docList);
						categoryMap.put(FileUtil.FOLDER, folderList);
						categoryMap.put(FileUtil.PHOTO, photoList);
						categoryMap.put(FileUtil.MUSIC, musicList);
						categoryMap.put(FileUtil.VIDEO, videoList);
			        }
			        
			        IDataFilesActivity.this.runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							SmbFile[] sf = new SmbFile[categoryMap.get(type).size()];
							for(int i=0; i<categoryMap.get(type).size(); i++){
								Object[] obj = categoryMap.get(type).toArray();
								sf[i] = (SmbFile)obj[i];
							}
//							listAdapter.setFileArray((SmbFile[])categoryMap.get(type).toArray());
							listAdapter.setFileArray(sf);
						    listView.setAdapter(listAdapter);
						}
					});
			    	
			        Log.i("IDataFragment", "onCreateView");
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}).start();
		   
		
	    backView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				new Thread(new Runnable() {
					public void run() {
						try {
							jcifs.Config.setProperty( "jcifs.smb.lmCompatibility", "0");
					        NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(null, "admin", "admin");
							final SmbFile file = new SmbFile(curParent, auth);
							final SmbFile[] files = file.listFiles();
							
						    IDataFilesActivity.this.runOnUiThread(new Runnable() {
								
								@Override
								public void run() {
									if("smb://192.168.169.1/Share/".equals(curParent)){
										backView.setVisibility(View.GONE);
									}
									
									String tempPath = curParent;
									tempPath = tempPath.substring(0, tempPath.length()-1);
									tempPath = tempPath.substring(tempPath.lastIndexOf("/")+1);
									  listAdapter.setFileArray(files);
								      listView.setAdapter(listAdapter);
								      titleTextView.setText(tempPath);
								      
								      curParent = file.getParent();
								}
							});
						    
							
						} catch (MalformedURLException e) {
							e.printStackTrace();
						} catch (SmbException e) {
							e.printStackTrace();
						}
					}
				}).start();
			
				
			}
		});
		
        //杩涘叆涓嬩竴绾х洰褰�
        listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View arg1,
					int position, long arg3) {
				SmbFile file = (SmbFile)parent.getItemAtPosition(position);
				try {
					String fileName = getName(file);
					
					openFileOrDir(file, backView, titleTextView, fileName, listAdapter);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
        
	}
	
	private void openFileOrDir(SmbFile file, ImageView backView, TextView titleTextView, String fileName, FileListAdapter listAdapter){
		try {
			if(file.isDirectory()){
				backView.setVisibility(View.VISIBLE);
				titleTextView.setText(fileName);
				curParent = file.getParent();
				
				SmbFile[] subFiles = file.listFiles();
				listAdapter.setFileArray(subFiles);
				listAdapter.notifyDataSetChanged();
			}else{
				String path = file.getPath();
				Intent intent= new Intent();        
			    intent.setAction("android.intent.action.VIEW");    
			    
			    String openPath;
			    Uri content_url;
			    if(path.lastIndexOf("Share/Storage/") == -1){
			    	openPath = path.substring(path.lastIndexOf("Share/SD_Card/")+14);
			    	content_url = Uri.parse("http://192.168.169.1:8080/SD_Card/"+URLEncoder.encode(openPath));   
			    }else{
			    	openPath = path.substring(path.lastIndexOf("Share/Storage/")+14);
			    	content_url = Uri.parse("http://192.168.169.1:8080/Storage/"+URLEncoder.encode(openPath));   
			    }
			   
			    intent.setData(content_url);  
			    startActivity(intent);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private String getName(SmbFile file){
		try {
			String name = file.getName();
			if(file.isDirectory()){
				name = name.substring(0, name.length()-1);
				name = name.substring(name.lastIndexOf("/")+1);
				
			}else{
				name = name.substring(0, name.length());
				name = name.substring(name.lastIndexOf("/")+1);
			}
			return name;
			
		} catch (SmbException e) {
			e.printStackTrace();
			return "";
		}
		
	}

	private class FileListAdapter extends BaseAdapter{
		private SmbFile[] fileArray;
		private LayoutInflater mInflater;

		public FileListAdapter(Context context, SmbFile[] fileArray){
			this.mInflater = LayoutInflater.from(context);
			this.fileArray = fileArray;
		}
		
		public SmbFile[] getFileArray() {
			return fileArray;
		}

		public void setFileArray(SmbFile[] fileArray) {
			this.fileArray = fileArray;
		}

		@Override
		public int getCount() {
			return fileArray.length;
		}

		@Override
		public Object getItem(int position) {
			return fileArray[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		private class ViewHolder{
			public ImageView icon;
			public TextView name;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if(convertView == null){
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.file_item, null);
				holder.icon = (ImageView)convertView.findViewById(R.id.icon);
				holder.name = (TextView)convertView.findViewById(R.id.name);
				
				convertView.setTag(holder);
				
			}else{
				holder = (ViewHolder)convertView.getTag();
			}
			
			SmbFile file = fileArray[position];
			try {
				if(file.isDirectory()){
					holder.icon.setImageResource(R.drawable.folder);
				}else{
					holder.icon.setImageResource(R.drawable.default_fileicon);
				}
			} catch (SmbException e) {
				e.printStackTrace();
			}
			
			String name = getName(file);
			holder.name.setText(name);
			
			return convertView;
		}
		
	}
	
/*	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}*/

}
