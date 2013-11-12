package com.delux.idata;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.delux.idata.MainActivity.BackKeyEvent;
import com.delux.idata.MainActivity.MutilChooseCallBack;
import com.delux.util.DialogUtil;
import com.delux.util.FileUtil;


public class LocalFragment extends Fragment implements BackKeyEvent, MutilChooseCallBack{
	private String curParent;
	Map<Integer, ArrayList> categoryMap = new HashMap<Integer, ArrayList>();
	ListView filelistView;
	View categoryView;
	private int curClickType;
	private boolean isRoot = true; 

	
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
		
		final View internalStoreView = contextView.findViewById(R.id.internal_store);
		final View sdcardView = contextView.findViewById(R.id.sdcard);
		
		internalStoreView.setBackgroundResource(R.color.click_bg);
		internalStoreView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				curParent = "/data/data";
				v.setBackgroundResource(R.color.click_bg);
				sdcardView.setBackgroundResource(android.R.color.transparent);
				getCategoryFilesOnThread(false);
			}
		});
		
		sdcardView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				curParent = "/mnt";
				v.setBackgroundResource(R.color.click_bg);
				internalStoreView.setBackgroundResource(android.R.color.transparent);
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
						isRoot = false;
						curClickType = FileUtil.PHOTO;
						
						//获取分类下的文件
						getSubCategoryFilesOnThread();
					
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
						isRoot = false;
						curClickType = FileUtil.VIDEO;
						
						getSubCategoryFilesOnThread();
					
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
						isRoot = false;
						curClickType = FileUtil.MUSIC;
						
						getSubCategoryFilesOnThread();
					
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
						isRoot = false;
						curClickType = FileUtil.DOC;
						
						getSubCategoryFilesOnThread();
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
						isRoot = false;
						curClickType = FileUtil.ROOT;
						
						getSubCategoryFilesOnThread();
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
	
	/**
	 *  获取某个分类下的文件           
	 */
	private void getSubCategoryFilesOnThread(){
		final File[] sf = getFileList(categoryMap, curClickType);
		final LocalFileListAdapter listAdapter = new LocalFileListAdapter(getActivity(), null, curClickType, categoryMap);
		
		getActivity().runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				categoryView.setVisibility(View.GONE);
				listAdapter.setFileArray(sf);
				filelistView.setAdapter(listAdapter);
				filelistView.setVisibility(View.VISIBLE);
				
				filelistView.setOnScrollListener(new OnScrollListener() {
					
					@Override
					public void onScrollStateChanged(AbsListView view, int scrollState) {
						int curShowToolPosition = listAdapter.getCurShowToolPosition();
//						Log.i("onScroll", "firstVisibleItem ="+firstVisibleItem+"; visibleItemCount="+visibleItemCount+"; totalItemCount="+totalItemCount+"; getCurShowToolPosition="+curShowToolPosition);
						
						if(curShowToolPosition > -1){
							View toolLineView =filelistView.findViewWithTag("visible");
							toolLineView.setVisibility(View.INVISIBLE);
							toolLineView.setTag("");
//							View view2 = filelistView.getChildAt(curShowToolPosition);
//							view2.findViewById(R.id.tool_line).setVisibility(View.INVISIBLE);
							listAdapter.setCurShowToolPosition(-1);
						}
						
					}
					
					@Override
					public void onScroll(AbsListView view, int firstVisibleItem,
							int visibleItemCount, int totalItemCount) {
					
					}
				});
				
				filelistView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View arg1,
							int position, long arg3) {
						int curShowToolPosition = listAdapter.getCurShowToolPosition();
						if(curShowToolPosition > -1){
//							View view = parent.getChildAt(curShowToolPosition);
//							view.findViewById(R.id.tool_line).setVisibility(View.INVISIBLE);
							View toolLineView =filelistView.findViewWithTag("visible");
							toolLineView.setVisibility(View.INVISIBLE);
							toolLineView.setTag("");
							listAdapter.setCurShowToolPosition(-1);
						}
						File file = (File)parent.getItemAtPosition(position);
						try {
							String fileName = LocalFileListAdapter.getName(file);
							
							openFileOrDir(file, null, null, fileName, listAdapter);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
	}
	
	private void openFileOrDir(File file, ImageView backView, TextView titleTextView, String fileName, LocalFileListAdapter listAdapter){
		try {
			isRoot = false;
			if(file.isDirectory()){
				if(backView != null && titleTextView != null){
					backView.setVisibility(View.VISIBLE);
					titleTextView.setText(fileName);
				}
				curParent = file.getParent();
				
				File[] subFiles = file.listFiles();
				if(subFiles == null){
					subFiles = new File[]{};
				}	
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
				
				 Intent intent = FileUtil.getOpenLocalAppIntent(file);
				 
				 if(intent == null){
					 Toast.makeText(getActivity(), R.string.open_file_tip, 1).show();
				 }else{
					 startActivity(intent);
				 }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 把文件进行分类
	 */
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
									int category = FileUtil.getFileCategory(LocalFileListAdapter.getName(subfile2));
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
							int category = FileUtil.getFileCategory(LocalFileListAdapter.getName(file));
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
	

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	
	public void onBack() {
		Log.i("IDataFragment", "curParent ="+curParent);
		
		if(!isMutilChooseMode){
			if(isRoot){
				DialogUtil.showExitDialog(getActivity());
			}
			if("/mnt".equals(curParent) || curClickType != FileUtil.ROOT || "/data/data".equals(curParent)){
				isRoot = true;
				categoryView.setVisibility(View.VISIBLE);
				filelistView.setVisibility(View.GONE);
				return;
			}else{
				isRoot = false;
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
		}else{
			bottomLayout.setVisibility(View.VISIBLE);
			mutilChooseLayout.setVisibility(View.GONE);
			isMutilChooseMode = false;
			
			LocalFileListAdapter listAdapter = (LocalFileListAdapter)filelistView.getAdapter();
			listAdapter.setMutilMode(false);
			listAdapter.selectFiles.clear();
			listAdapter.notifyDataSetChanged();
		}
	}
	
	private boolean isMutilChooseMode; //当前是否在多选模式下
	protected LinearLayout bottomLayout;
	protected LinearLayout mutilChooseLayout;

	@Override
	public void onClick(LinearLayout bottomLayout,
			LinearLayout mutilChooseLayout) {
		Log.i("LocalFragment", "isRoot ="+isRoot);
		if(!isRoot){
			this.bottomLayout = bottomLayout;
			this.mutilChooseLayout = mutilChooseLayout;
			
			LinearLayout delMany = (LinearLayout)mutilChooseLayout.findViewById(R.id.del);
			delMany.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					LocalFileListAdapter localFileListAdapter = (LocalFileListAdapter)filelistView.getAdapter();
					if(localFileListAdapter.selectFiles.size() == 0){
//						Toast.makeText(context, resId, duration)
						return;
					}
					localFileListAdapter.delMany(localFileListAdapter.selectFiles, localFileListAdapter.convertToList(localFileListAdapter.getFileArray()));
				}
			});
			
			LinearLayout copyMany = (LinearLayout)mutilChooseLayout.findViewById(R.id.copy);
			copyMany.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					LocalFileListAdapter localFileListAdapter = (LocalFileListAdapter)filelistView.getAdapter();
					if(localFileListAdapter.selectFiles.size() == 0){
//						Toast.makeText(context, resId, duration)
						return;
					}
					localFileListAdapter.copyMany(localFileListAdapter.selectFiles.values());
				}
			});
			
			LinearLayout moveMany = (LinearLayout)mutilChooseLayout.findViewById(R.id.move);
			moveMany.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
						LocalFileListAdapter localFileListAdapter = (LocalFileListAdapter)filelistView.getAdapter();
						if(localFileListAdapter.categoryType == FileUtil.ROOT){
							if(localFileListAdapter.selectFiles.size() == 0){
		//						Toast.makeText(context, resId, duration)
								return;
							}
							localFileListAdapter.moveMany(localFileListAdapter.selectFiles.values());
						}else{
							Toast.makeText(getActivity(), R.string.no_move_tip, 1).show();
						}
				}
			});
			
			bottomLayout.setVisibility(View.GONE);
			mutilChooseLayout.setVisibility(View.VISIBLE);
			isMutilChooseMode = true;
			
			LocalFileListAdapter listAdapter = (LocalFileListAdapter)filelistView.getAdapter();
			listAdapter.setMutilMode(true);
			listAdapter.notifyDataSetChanged();
		}
	}
	
//	public static void onPostFresh(String path) {
//		Log.i("LocalFragment", "onPostFresh");
//		File file = new File(path);
//		LocalFileListAdapter localFileListAdapter = (LocalFileListAdapter)filelistView.getAdapter();
//		localFileListAdapter.setFileArray(file.listFiles());
//		localFileListAdapter.notifyDataSetChanged();
//	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i("LocalFragment", "onDestroy");
		
	}

}
