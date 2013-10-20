package com.delux.idata;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.delux.util.FileUtil;


public class IDataFragment extends Fragment {
	private String curParent = "smb://192.168.169.1/Share/";
	Map<Integer, ArrayList> categoryMap;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View contextView = inflater.inflate(R.layout.fragment_item, container, false);
		View photoRow = contextView.findViewById(R.id.photo);
		View videoRow = contextView.findViewById(R.id.video);
		View musicRow = contextView.findViewById(R.id.music);
		View docRow = contextView.findViewById(R.id.doc);
		View folderRow = contextView.findViewById(R.id.folder);
		final View categoryView = contextView.findViewById(R.id.category);
		final ListView filelistView = (ListView)contextView.findViewById(R.id.filelist_view);
		
	/*	this.getFragmentManager().addOnBackStackChangedListener(new OnBackStackChangedListener() {
			
			@Override
			public void onBackStackChanged() {
				Log.i("fragment_back", "addOnBackStackChangedListener");
			}
		});*/
		final FileListAdapter listAdapter = new FileListAdapter(getActivity(), null);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				categoryMap = getCategoryFiles();
			}
		}).start();
		
		
		photoRow.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						final SmbFile[] sf = getFileList(categoryMap, FileUtil.PHOTO);
						
						getActivity().runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								categoryView.setVisibility(View.GONE);
								listAdapter.setFileArray(sf);
								filelistView.setAdapter(listAdapter);
								filelistView.setVisibility(View.VISIBLE);
								
								filelistView.setOnItemClickListener(new OnItemClickListener() {

									@Override
									public void onItemClick(AdapterView<?> parent, View arg1,
											int position, long arg3) {
										SmbFile file = (SmbFile)parent.getItemAtPosition(position);
										try {
											String fileName = getName(file);
											
											openFileOrDir(file, null, null, fileName, listAdapter);
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								});
							}
						});
					
					}
				}).start();
				
			/*	Intent i = new Intent(getActivity(), IDataFilesActivity.class);
				i.putExtra("type", FileUtil.PHOTO);
//				i.putExtra("PHOTO", categoryMap.get(FileUtil.PHOTO));
				i.putExtra("curParent", curParent);
				startActivity(i);*/
			}
		});
		
		videoRow.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						final SmbFile[] sf = getFileList(categoryMap, FileUtil.VIDEO);
						final FileListAdapter listAdapter = new FileListAdapter(getActivity(), null);
						
						getActivity().runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								categoryView.setVisibility(View.GONE);
								listAdapter.setFileArray(sf);
								filelistView.setAdapter(listAdapter);
								filelistView.setVisibility(View.VISIBLE);
								
								filelistView.setOnItemClickListener(new OnItemClickListener() {

									@Override
									public void onItemClick(AdapterView<?> parent, View arg1,
											int position, long arg3) {
										SmbFile file = (SmbFile)parent.getItemAtPosition(position);
										try {
											String fileName = getName(file);
											
											openFileOrDir(file, null, null, fileName, listAdapter);
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								});
							}
						});
					
					}
				}).start();
			/*	Intent i = new Intent(getActivity(), IDataFilesActivity.class);
				i.putExtra("type", FileUtil.VIDEO);
//				i.putExtra("VIDEO", categoryMap.get(FileUtil.VIDEO));
				i.putExtra("curParent", curParent);
				startActivity(i);*/
			}
		});
		
		musicRow.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						final SmbFile[] sf = getFileList(categoryMap, FileUtil.MUSIC);
						final FileListAdapter listAdapter = new FileListAdapter(getActivity(), null);
						
						getActivity().runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								categoryView.setVisibility(View.GONE);
								listAdapter.setFileArray(sf);
								filelistView.setAdapter(listAdapter);
								filelistView.setVisibility(View.VISIBLE);
								
								filelistView.setOnItemClickListener(new OnItemClickListener() {

									@Override
									public void onItemClick(AdapterView<?> parent, View arg1,
											int position, long arg3) {
										SmbFile file = (SmbFile)parent.getItemAtPosition(position);
										try {
											String fileName = getName(file);
											
											openFileOrDir(file, null, null, fileName, listAdapter);
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								});
							}
						});
					
					}
				}).start();
		/*		Intent i = new Intent(getActivity(), IDataFilesActivity.class);
				i.putExtra("type", FileUtil.MUSIC);
//				i.putExtra("MUSIC", categoryMap.get(FileUtil.MUSIC));
				i.putExtra("curParent", curParent);
				
				startActivity(i);*/
			}
		});
		
		docRow.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						final SmbFile[] sf = getFileList(categoryMap, FileUtil.DOC);
						final FileListAdapter listAdapter = new FileListAdapter(getActivity(), null);
						
						getActivity().runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								categoryView.setVisibility(View.GONE);
								listAdapter.setFileArray(sf);
								filelistView.setAdapter(listAdapter);
								filelistView.setVisibility(View.VISIBLE);
								
								filelistView.setOnItemClickListener(new OnItemClickListener() {

									@Override
									public void onItemClick(AdapterView<?> parent, View arg1,
											int position, long arg3) {
										SmbFile file = (SmbFile)parent.getItemAtPosition(position);
										try {
											String fileName = getName(file);
											
											openFileOrDir(file, null, null, fileName, listAdapter);
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								});
							}
						});
					
					}
				}).start();
			/*	Intent i = new Intent(getActivity(), IDataFilesActivity.class);
				i.putExtra("type", FileUtil.DOC);
//				i.putExtra("DOC", categoryMap.get(FileUtil.DOC));
				i.putExtra("curParent", curParent);
				
				startActivity(i);*/
			}
		});
		
		folderRow.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						final SmbFile[] sf = getFileList(categoryMap, FileUtil.ROOT);
						final FileListAdapter listAdapter = new FileListAdapter(getActivity(), null);
						
						getActivity().runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								categoryView.setVisibility(View.GONE);
								listAdapter.setFileArray(sf);
								filelistView.setAdapter(listAdapter);
								filelistView.setVisibility(View.VISIBLE);
								
								filelistView.setOnItemClickListener(new OnItemClickListener() {

									@Override
									public void onItemClick(AdapterView<?> parent, View arg1,
											int position, long arg3) {
										SmbFile file = (SmbFile)parent.getItemAtPosition(position);
										try {
											String fileName = getName(file);
											
											openFileOrDir(file, null, null, fileName, listAdapter);
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								});
							}
						});
					
					}
				}).start();
				
			/*	Intent i = new Intent(getActivity(), IDataFilesActivity.class);
				i.putExtra("type", FileUtil.FOLDER);
//				i.putExtra("FOLDER", categoryMap.get(FileUtil.FOLDER));
				i.putExtra("curParent", curParent);
				
				startActivity(i);*/
			}
		});
		
		return contextView;
	}
	
	private void openFileOrDir(SmbFile file, ImageView backView, TextView titleTextView, String fileName, FileListAdapter listAdapter){
		try {
			if(file.isDirectory()){
				if(backView != null && titleTextView != null){
					backView.setVisibility(View.VISIBLE);
					titleTextView.setText(fileName);
				}
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
	
	private Map<Integer, ArrayList> getCategoryFiles(){
			try {
				jcifs.Config.setProperty( "jcifs.smb.lmCompatibility", "0");
		        NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(null, "admin", "admin");
		        
		        SmbFile file = new SmbFile("smb://192.168.169.1/Share/",auth);
		        SmbFile[] files = file.listFiles();
		        
		        Map<Integer, ArrayList> categoryMap = new HashMap<Integer, ArrayList>();
		        ArrayList<SmbFile> rootList = new ArrayList<SmbFile>();
		        for(SmbFile	smbFile : files){
		        	rootList.add(smbFile);
		        	categoryMap.put(FileUtil.ROOT, rootList);
		        }
		        
		        curParent = files[0].getPath();
		        final SmbFile[] subfiles = files[0].listFiles();
		        
		        if(subfiles.length > 0){
		        	
		        	ArrayList<SmbFile> photoList = new ArrayList<SmbFile>();
		        	ArrayList<SmbFile> videoList = new ArrayList<SmbFile>();
		        	ArrayList<SmbFile> musicList = new ArrayList<SmbFile>();
		        	ArrayList<SmbFile> docList = new ArrayList<SmbFile>();
//		        	ArrayList<SmbFile> folderList = new ArrayList<SmbFile>();
		        	
					for(SmbFile	subfile : subfiles){
						if(subfile.isDirectory()){
//							folderList.add(subfile);
						}else{
							int category = FileUtil.getFileCategory(FileUtil.getName(subfile));
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
//					categoryMap.put(FileUtil.FOLDER, folderList);
					categoryMap.put(FileUtil.PHOTO, photoList);
					categoryMap.put(FileUtil.MUSIC, musicList);
					categoryMap.put(FileUtil.VIDEO, videoList);
					
					return categoryMap;
		        }
		        Log.i("IDataFragment", "onCreateView");
			}catch(Exception e){
				e.printStackTrace();
			}
			return null;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.i("IDataFragment", "onActivityCreated");
		super.onActivityCreated(savedInstanceState);
	}
	
	private SmbFile[] getFileList(Map<Integer, ArrayList>categoryMap, int type){
		SmbFile[] sf = new SmbFile[categoryMap.get(type).size()];
		for(int i=0; i<categoryMap.get(type).size(); i++){
			Object[] obj = categoryMap.get(type).toArray();
			sf[i] = (SmbFile)obj[i];
		}
		return sf;
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
	
	

}
