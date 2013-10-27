package com.delux.idata;

import java.io.File;
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
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.delux.idata.MainActivity.BackKeyEvent;
import com.delux.util.FileUtil;


public class LocalFragment extends Fragment implements BackKeyEvent{
	private String curParent;
	Map<Integer, ArrayList> categoryMap = new HashMap<Integer, ArrayList>();
	ListView filelistView;
	View categoryView;
	private int curClickType;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		curParent = "/data/data";
		
		View contextView = inflater.inflate(R.layout.fragment_item2, container, false);
		View photoRow = contextView.findViewById(R.id.photo);
		View videoRow = contextView.findViewById(R.id.video);
		View musicRow = contextView.findViewById(R.id.music);
		View docRow = contextView.findViewById(R.id.doc);
		View folderRow = contextView.findViewById(R.id.folder);
		
		View internalStoreView = contextView.findViewById(R.id.internal_store);
		View sdcardView = contextView.findViewById(R.id.sdcard);
		
		internalStoreView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				curParent = "/data/data";
				getCategoryFilesOnThread(false);
			}
		});
		
		sdcardView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				curParent = "/mnt";
				getCategoryFilesOnThread(true);
			}
		});
		
		categoryView = contextView.findViewById(R.id.category);
		filelistView = (ListView)contextView.findViewById(R.id.filelist_view);
		
		if("/mnt".equals(curParent)){
			getCategoryFilesOnThread(true);
		}else{
			getCategoryFilesOnThread(false);
		}
		
		photoRow.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						curClickType = FileUtil.PHOTO;
						final File[] sf = getFileList(categoryMap, FileUtil.PHOTO);
						final LocalFileListAdapter listAdapter = new LocalFileListAdapter(getActivity(), null, curClickType);
						
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
										File file = (File)parent.getItemAtPosition(position);
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
						
						final File[] sf = getFileList(categoryMap, FileUtil.VIDEO);
						final LocalFileListAdapter listAdapter = new LocalFileListAdapter(getActivity(), null, curClickType);
						
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
										File file = (File)parent.getItemAtPosition(position);
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
		
		musicRow.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						curClickType = FileUtil.MUSIC;
						
						final File[] sf = getFileList(categoryMap, FileUtil.MUSIC);
						final LocalFileListAdapter listAdapter = new LocalFileListAdapter(getActivity(), null, curClickType);
						
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
										File file = (File)parent.getItemAtPosition(position);
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
		
		docRow.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						curClickType = FileUtil.DOC;
						
						final File[] sf = getFileList(categoryMap, FileUtil.DOC);
						final LocalFileListAdapter listAdapter = new LocalFileListAdapter(getActivity(), null, curClickType);
						
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
										File file = (File)parent.getItemAtPosition(position);
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
		
		folderRow.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						curClickType = FileUtil.ROOT;
						
						final File[] sf = getFileList(categoryMap, FileUtil.ROOT);
						final LocalFileListAdapter listAdapter = new LocalFileListAdapter(getActivity(), null, curClickType);
						
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
										File file = (File)parent.getItemAtPosition(position);
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
		return contextView;
	}
	
	/**
	 * 开子线程开始分类
	 */
	private void getCategoryFilesOnThread(final boolean isSdcard){
		final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, getString(R.string.load), true, false); 
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				if(isSdcard){
					categoryMap = getCategoryFiles(true);
				}else{
					categoryMap = getCategoryFiles(false);
				}
				
				getActivity().runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						progressDialog.dismiss();
					}
				});
			}
		}).start();
	}
	
	
	private void openFileOrDir(File file, ImageView backView, TextView titleTextView, String fileName, LocalFileListAdapter listAdapter){
		try {
			if(file.isDirectory()){
				if(backView != null && titleTextView != null){
					backView.setVisibility(View.VISIBLE);
					titleTextView.setText(fileName);
				}
				curParent = file.getParent();
				
				File[] subFiles = file.listFiles();
				listAdapter.setFileArray(subFiles);
				listAdapter.notifyDataSetChanged();
			}else{
				/*String path = file.getPath();
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
			    startActivity(intent);*/
			/*	Intent i = new Intent();
				i.setAction(Intent.ACTION_VIEW);
				i.setType("application/msword");
				Log.i("file_path", file.getPath());
				i.setData(Uri.parse("file:/"+file.getPath()));
				startActivity(i);*/
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private Map<Integer, ArrayList> getCategoryFiles(boolean isSdcard){
		try {
//			jcifs.Config.setProperty( "jcifs.smb.lmCompatibility", "0");
//	        NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(null, "admin", "admin");
//	        SmbFile file = new SmbFile("smb://192.168.169.1/Share/",auth);
			File rootFile;
			if(isSdcard){
				rootFile = Environment.getExternalStorageDirectory();
			}else{
				rootFile = new File("/data/data/com.delux.idata/");
			}
			
			File[] files = rootFile.listFiles();
			if(files != null && files.length > 0){
		        
		        Map<Integer, ArrayList> categoryMap = new HashMap<Integer, ArrayList>();
		        ArrayList<File> rootList = new ArrayList<File>();
		        ArrayList<File> photoList = new ArrayList<File>();
	        	ArrayList<File> videoList = new ArrayList<File>();
	        	ArrayList<File> musicList = new ArrayList<File>();
	        	ArrayList<File> docList = new ArrayList<File>();
	        	
		        for(File  file : files){
		        	rootList.add(file);
		        	categoryMap.put(FileUtil.ROOT, rootList);
		        	
						if(file.isDirectory()){
							File[] subfiles2 = file.listFiles();
							
							if(subfiles2 != null && subfiles2.length > 0){
								for(File  subfile2 : subfiles2){
									int category = FileUtil.getFileCategory(getName(subfile2));
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
							}
						}else{
							int category = FileUtil.getFileCategory(getName(file));
							if(FileUtil.PHOTO == category){
								photoList.add(file);
							}else if(FileUtil.MUSIC == category){
								musicList.add(file);
							}else if(FileUtil.VIDEO == category){
								videoList.add(file);
							}else if(FileUtil.DOC == category){
								docList.add(file);
							}
						}
		        }

				categoryMap.put(FileUtil.DOC, docList);
				categoryMap.put(FileUtil.PHOTO, photoList);
				categoryMap.put(FileUtil.MUSIC, musicList);
				categoryMap.put(FileUtil.VIDEO, videoList);
				
				return categoryMap;
//		        curParent = files[0].getPath();
//		        final SmbFile[] subfiles = files[0].listFiles();
//		        if(subfiles.length > 0){
		        
//		        }
			}

		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	private File[] getFileList(Map<Integer, ArrayList>categoryMap, int type){
		File[] sf = new File[categoryMap.get(type).size()];
		for(int i=0; i<categoryMap.get(type).size(); i++){
			Object[] obj = categoryMap.get(type).toArray();
			sf[i] = (File)obj[i];
		}
		return sf;
	}
	
	private String getName(File file){
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
			
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	public void onBack() {
		Log.i("IDataFragment", "curParent ="+curParent);
		if("/mnt".equals(curParent) || curClickType != FileUtil.ROOT || "/data/data".equals(curParent)){
			categoryView.setVisibility(View.VISIBLE);
			filelistView.setVisibility(View.GONE);
			return;
		}
		
		new Thread(new Runnable() {
			public void run() {
				try {
//					jcifs.Config.setProperty( "jcifs.smb.lmCompatibility", "0");
//			        NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(null, "admin", "admin");
					final File file = new File(curParent);
					final File[] files = file.listFiles();
					
				    getActivity().runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
//							if("smb://192.168.169.1/Share/".equals(curParent)){
//								backView.setVisibility(View.GONE);
//							}
							
							String tempPath = curParent;
							tempPath = tempPath.substring(0, tempPath.length()-1);
							tempPath = tempPath.substring(tempPath.lastIndexOf("/")+1);
							
							LocalFileListAdapter listAdapter = (LocalFileListAdapter)filelistView.getAdapter();
							listAdapter.setFileArray(files);
							filelistView.setAdapter(listAdapter);
//						      titleTextView.setText(tempPath);
						      
						      curParent = file.getParent();
						}
					});
				    
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

}
