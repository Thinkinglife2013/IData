package com.delux.idata;

import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.delux.idata.MainActivity.BackKeyEvent;
import com.delux.util.FileUtil;


public class IDataFragment extends Fragment implements BackKeyEvent{
	private String curParent = "smb://192.168.169.1/Share/";
	Map<Integer, ArrayList> categoryMap = new HashMap<Integer, ArrayList>();
	ListView filelistView;
	View categoryView;
	private int curClickType;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View contextView = inflater.inflate(R.layout.fragment_item, container, false);
		View photoRow = contextView.findViewById(R.id.photo);
		View videoRow = contextView.findViewById(R.id.video);
		View musicRow = contextView.findViewById(R.id.music);
		View docRow = contextView.findViewById(R.id.doc);
		View folderRow = contextView.findViewById(R.id.folder);
		categoryView = contextView.findViewById(R.id.category);
		filelistView = (ListView)contextView.findViewById(R.id.filelist_view);
		
		final FileListAdapter listAdapter = new FileListAdapter(getActivity(), null);
		final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, getString(R.string.load), true, false); 
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				categoryMap = getCategoryFiles();
				getActivity().runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						progressDialog.dismiss();
					}
				});
			}
		}).start();
		
		
		photoRow.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						curClickType = FileUtil.PHOTO;
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
				
			}
		});
		
		videoRow.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						curClickType = FileUtil.VIDEO;
						
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
						curClickType = FileUtil.MUSIC;
						
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
						curClickType = FileUtil.DOC;
						
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
						curClickType = FileUtil.ROOT;
						curParent = "smb://192.168.169.1/";
						
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
							SmbFile[] subfiles2 = subfile.listFiles();
							
							for(SmbFile	subfile2 : subfiles2){
								int category = FileUtil.getFileCategory(FileUtil.getName(subfile2));
								if(FileUtil.PHOTO == category){
									photoList.add(subfile2);
								}else if(FileUtil.MUSIC == category){
									musicList.add(subfile2);
								}else if(FileUtil.VIDEO == category){
									videoList.add(subfile2);
								}else if(FileUtil.DOC == category){
									docList.add(subfile2);
								}
							}
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
	
	public void onBack() {
		Log.i("IDataFragment", "curParent ="+curParent);
		if("smb://192.168.169.1/".equals(curParent) || curClickType != FileUtil.ROOT){
			categoryView.setVisibility(View.VISIBLE);
			filelistView.setVisibility(View.GONE);
		}
		
		new Thread(new Runnable() {
			public void run() {
				try {
					jcifs.Config.setProperty( "jcifs.smb.lmCompatibility", "0");
			        NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(null, "admin", "admin");
					final SmbFile file = new SmbFile(curParent, auth);
					final SmbFile[] files = file.listFiles();
					
				    getActivity().runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
//							if("smb://192.168.169.1/Share/".equals(curParent)){
//								backView.setVisibility(View.GONE);
//							}
							
							String tempPath = curParent;
							tempPath = tempPath.substring(0, tempPath.length()-1);
							tempPath = tempPath.substring(tempPath.lastIndexOf("/")+1);
							
							FileListAdapter listAdapter = (FileListAdapter)filelistView.getAdapter();
							listAdapter.setFileArray(files);
							filelistView.setAdapter(listAdapter);
//						      titleTextView.setText(tempPath);
						      
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

}
