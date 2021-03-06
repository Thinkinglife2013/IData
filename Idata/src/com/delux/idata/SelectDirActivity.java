package com.delux.idata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import jcifs.smb.SmbFileOutputStream;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.delux.util.FileUtil;
import com.delux.util.MyFIleFilter;
import com.delux.util.MyIdataFIleFilter;
import com.enterprisedt.net.ftp.FTPFile;
import com.enterprisedt.net.ftp.FileTransferClient;
import com.umeng.analytics.MobclickAgent;

public class SelectDirActivity extends Activity {
	private String curParent;
	ListView filelistView;
	private boolean isRoot = true; //是否在文件目录的根目录（排除虚拟目录）
	private View localpartitionView;
	private TextView oneFolder;
	private TextView twoFolder;
	TextView pathView;
	private boolean isCanWrite;
	private TextView copyOrMoveView;
	private String fromFile;
	private ArrayList<String> fromManyFile;
	private String prefix = "/mnt";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_folderlist);
		
		localpartitionView = findViewById(R.id.dir);
		View localOrInternalView = findViewById(R.id.local);
		View idataOrSdcardView = findViewById(R.id.idata);
		oneFolder = (TextView)findViewById(R.id.one);
		twoFolder = (TextView)findViewById(R.id.two);
		filelistView = (ListView)findViewById(R.id.file_list);
		pathView = (TextView)findViewById(R.id.path);
		TextView cancleView = (TextView)findViewById(R.id.cancle);
		copyOrMoveView = (TextView)findViewById(R.id.copy_or_move);
		
		cancleView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		//拷贝或者移动的路径
		Intent i = getIntent();
		String moveOrCopy = i.getStringExtra("moveOrCopy");
		fromFile = i.getStringExtra("fromFile");
		fromManyFile = (ArrayList<String>)i.getSerializableExtra("fromManyFile");
		
		if("move".equalsIgnoreCase(moveOrCopy)){
			copyOrMoveView.setText(R.string.move_to_here);
			copyOrMoveView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Log.i("copyOrMove", "move");
					final ProgressDialog progressDialog = ProgressDialog.show(SelectDirActivity.this, null, getString(R.string.moving), true, false); 
					new Thread(new Runnable() {
						public void run() {
							String path = pathView.getText().toString();
							if(fromManyFile != null){
								fromFile = fromManyFile.get(0);
							}
							
							if(path.startsWith("smb://") && fromFile.startsWith("smb://")){
								if(fromManyFile == null){
									if(!moveIdataFile(fromFile, path)){
										moveIdataDirectory(fromFile, path);
									}
								}else{
									for(String  from : fromManyFile){
										if(!moveIdataFile(from, path)){
											moveIdataDirectory(from, path);
										}
									}
								}
								
							}else if(!path.startsWith("smb://") && !fromFile.startsWith("smb://")){
								if(fromManyFile == null){
									if(!moveFile(fromFile, path)){
										moveDirectory(fromFile, path);
									}
								}else{
									for(String  from : fromManyFile){
										if(!moveFile(from, path)){
											moveDirectory(from, path);
										}
									}
								}
								
							}else if(!path.startsWith("smb://") && fromFile.startsWith("smb://")){
								//TODO
							}else if(path.startsWith("smb://") && !fromFile.startsWith("smb://")){
								//TODO
							}
					
							runOnUiThread(new Runnable() {
								
								@Override
								public void run() {
									progressDialog.dismiss();
									finish();
								}
							});
						}
					}).start();
				
				}
			});
		}else{
			copyOrMoveView.setText(R.string.copy_to_here);
			copyOrMoveView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Log.i("copyOrMove", "copy");
					final ProgressDialog progressDialog = ProgressDialog.show(SelectDirActivity.this, null, getString(R.string.copying), true, false); 
					new Thread(new Runnable() {
						public void run() {
							String path = pathView.getText().toString();
							if(fromManyFile != null){
								fromFile = fromManyFile.get(0);
							}
							
							if(path.startsWith("smb://") && fromFile.startsWith("smb://")){
								if(fromManyFile == null){
									copyIdata(fromFile, path);
								}else{
									for(String  from : fromManyFile){
										copyIdata(from, path);
									}
								}
							}else if(!path.startsWith("smb://") && !fromFile.startsWith("smb://")){
								if(fromManyFile == null){
									copy(fromFile, path+"/");
								}else{
									for(String  from : fromManyFile){
										copy(from, path+"/");
									}
								}
								
							}else if(!path.startsWith("smb://") && fromFile.startsWith("smb://")){
								if(fromManyFile == null){
									copyIdataToLocal(fromFile, path+"/");
								}else{
									for(String  from : fromManyFile){
										copyIdataToLocal(from, path+"/");
									}
								}
							}else if(path.startsWith("smb://") && !fromFile.startsWith("smb://")){
								if(fromManyFile == null){
									copyLocalToIdata(fromFile, path);
								}else{
									for(String  from : fromManyFile){
										copyLocalToIdata(from, path);
									}
								}
							}
							
							runOnUiThread(new Runnable() {
								
								@Override
								public void run() {
									progressDialog.dismiss();
									finish();
								}
							});
						}
					}).start();
					
				}
			});
		}
		copyOrMoveView.setClickable(false);
		
		localOrInternalView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(getString(R.string.internal_store).
						equalsIgnoreCase(oneFolder.getText().toString())){
					localpartitionView.setVisibility(View.GONE);
					filelistView.setVisibility(View.VISIBLE);
					pathView.setVisibility(View.VISIBLE);
					
					getSubCategoryFilesOnThread(false, filelistView);
				}else{
					oneFolder.setText(R.string.internal_store);
					twoFolder.setText(R.string.sdcard);
				}
			
			}
		});
		
		idataOrSdcardView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//SDcard
				if(getString(R.string.internal_store).
						equalsIgnoreCase(oneFolder.getText().toString())){
					localpartitionView.setVisibility(View.GONE);
					filelistView.setVisibility(View.VISIBLE);
					pathView.setVisibility(View.VISIBLE);
					
					getSubCategoryFilesOnThread(true, filelistView);
					
				//idata存储
				}else{
					localpartitionView.setVisibility(View.GONE);
					filelistView.setVisibility(View.VISIBLE);
					pathView.setVisibility(View.VISIBLE);
					
					getIDataCategoryFilesOnThread(filelistView);
					
				}
			}
		});
		
	}
	
	@Override
	public void onResume() {
		MobclickAgent.onResume(this);
		super.onResume();
	}
	
	@Override
	public void onPause() {
		MobclickAgent.onPause(this);
		super.onPause();
	}
	
	/**
	 *  获取idata目录下的文件           
	 */
	private void getIDataCategoryFilesOnThread(final ListView filelistView){
		final ProgressDialog progressDialog = ProgressDialog.show(this, null, getString(R.string.load), true, false); 
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Map<Integer, ArrayList> categoryMap = new HashMap<Integer, ArrayList>();
				try {
						jcifs.Config.setProperty( "jcifs.smb.lmCompatibility", "0");
						jcifs.Config.setProperty( "jcifs.smb.client.responseTimeout", "5000");
						
				        NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(null, "admin", "admin");
				        
				        SmbFile file = new SmbFile("smb://192.168.169.1/Share/",auth);
				        final SmbFile[] files = file.listFiles(new MyIdataFIleFilter());
				        
				        ArrayList<SmbFile> rootList = new ArrayList<SmbFile>();
				        if(files != null && files.length > 0){
					        for(SmbFile	smbFile : files){
					        	if(file.isDirectory())
					        		rootList.add(smbFile);
					        }
				        }
				        categoryMap.put(FileUtil.ROOT, rootList);
				        
				        curParent = file.getPath();
//				        final SmbFile[] files = files[0].listFiles(new MyIdataFIleFilter());
					
					final SmbFile[] sf = getIdataFileList(categoryMap, FileUtil.ROOT);
					final FileListAdapter listAdapter = new FileListAdapter(SelectDirActivity.this, null, FileUtil.ROOT, null);
					final SmbFile curFile = file;
					
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
		//					categoryView.setVisibility(View.GONE);
							progressDialog.dismiss();
							listAdapter.setFileArray(sf);
							listAdapter.setMoveOrCopy(true);
							filelistView.setAdapter(listAdapter);
							filelistView.setVisibility(View.VISIBLE);
							pathView.setVisibility(View.VISIBLE);
							pathView.setText(curFile.getPath());
							try {
								isCanWrite = curFile.canWrite();
							} catch (SmbException e1) {
								e1.printStackTrace();
							}
							if(isCanWrite){
								copyOrMoveView.setClickable(true);
								copyOrMoveView.setTextColor(getResources().getColor(R.color.category_color));
							}else{
								copyOrMoveView.setClickable(false);
								copyOrMoveView.setTextColor(getResources().getColor(android.R.color.darker_gray));
							}
							
							filelistView.setOnItemClickListener(new OnItemClickListener() {
			
								@Override
								public void onItemClick(AdapterView<?> parent, View arg1,
										int position, long arg3) {
									SmbFile file = (SmbFile)parent.getItemAtPosition(position);
									try {
										String fileName = FileUtil.getName(file);
										
										openIdataFileOrDir(file, null, null, fileName, listAdapter);
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							});
						}
					});
				
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}).start();
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
					Toast.makeText(SelectDirActivity.this, R.string.not_connect_idata, 0).show();
				}
			});
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 *  获取当前手机目录下的文件           
	 */
	private void getSubCategoryFilesOnThread(final boolean isSdcard, final ListView filelistView){
		final ProgressDialog progressDialog = ProgressDialog.show(this, null, getString(R.string.load), true, false); 
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Map<Integer, ArrayList> categoryMap = new HashMap<Integer, ArrayList>();
				try {
						File rootFile;
						if(isSdcard){
							File externalDir = Environment.getExternalStorageDirectory();
							//获取手机闪存或者外置SD卡的根路径
							if(externalDir != null){
								String sdcardPath = externalDir.getPath();
								prefix = sdcardPath.substring(0, sdcardPath.indexOf("/", 1));
							}
							curParent = prefix;
							rootFile = Environment.getExternalStorageDirectory();
						}else{
							curParent = "/data/data";
							rootFile = new File("/data/data/com.delux.idata/");
						}
						
						File[] files = rootFile.listFiles(new MyFIleFilter());
						if(files != null && files.length > 0){
					        
					        ArrayList<File> rootList = new ArrayList<File>();
				        	
					        for(File  file : files){
					        	if(file.isDirectory())
					        		rootList.add(file);
					        }
					        categoryMap.put(FileUtil.ROOT, rootList);
						}
					
					final File[] sf = getFileList(categoryMap, FileUtil.ROOT);
					final LocalFileListAdapter listAdapter = new LocalFileListAdapter(SelectDirActivity.this, null, FileUtil.ROOT, null);
					final File curFile = rootFile;
					
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
		//					categoryView.setVisibility(View.GONE);
							progressDialog.dismiss();
							listAdapter.setFileArray(sf);
							listAdapter.setMoveOrCopy(true);
							filelistView.setAdapter(listAdapter);
							filelistView.setVisibility(View.VISIBLE);
							pathView.setVisibility(View.VISIBLE);
							pathView.setText(curFile.getPath());
							isCanWrite = curFile.canWrite();
							if(isCanWrite){
								copyOrMoveView.setClickable(true);
								copyOrMoveView.setTextColor(getResources().getColor(R.color.category_color));
							}else{
								copyOrMoveView.setClickable(false);
								copyOrMoveView.setTextColor(getResources().getColor(android.R.color.darker_gray));
							}
							
							filelistView.setOnItemClickListener(new OnItemClickListener() {
			
								@Override
								public void onItemClick(AdapterView<?> parent, View arg1,
										int position, long arg3) {
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
				
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}).start();
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
				pathView.setText(file.getPath());
				isCanWrite = file.canWrite();
				if(isCanWrite){
					copyOrMoveView.setClickable(true);
					copyOrMoveView.setTextColor(getResources().getColor(R.color.category_color));
				}else{
					copyOrMoveView.setClickable(false);
					copyOrMoveView.setTextColor(getResources().getColor(android.R.color.darker_gray));
				}
				
				File[] subFiles = file.listFiles(new MyFIleFilter());
				if(subFiles == null){
					subFiles = new File[]{};
				}	
				listAdapter.setFileArray(subFiles);
				listAdapter.setMoveOrCopy(true);
				listAdapter.notifyDataSetChanged();
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void openIdataFileOrDir(SmbFile file, ImageView backView, TextView titleTextView, String fileName, FileListAdapter listAdapter){
		try {
			isRoot = false;
			if(file.isDirectory()){
				if(backView != null && titleTextView != null){
					backView.setVisibility(View.VISIBLE);
					titleTextView.setText(fileName);
				}
				curParent = file.getParent();
				pathView.setText(file.getPath());
				isCanWrite = file.canWrite();
				if(isCanWrite){
					copyOrMoveView.setClickable(true);
					copyOrMoveView.setTextColor(getResources().getColor(R.color.category_color));
				}else{
					copyOrMoveView.setClickable(false);
					copyOrMoveView.setTextColor(getResources().getColor(android.R.color.darker_gray));
				}
				
				SmbFile[] subFiles = file.listFiles(new MyIdataFIleFilter());
				if(subFiles == null){
					subFiles = new SmbFile[]{};
				}	
				listAdapter.setFileArray(subFiles);
				listAdapter.setMoveOrCopy(true);
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
//				Intent intent = FileUtil.getOpenIdataAppIntent(file);
			    startActivity(intent);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	  if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
		  		//虚拟目录
		  		if(localpartitionView.getVisibility() == View.VISIBLE){
		  			copyOrMoveView.setClickable(false);
					copyOrMoveView.setTextColor(getResources().getColor(android.R.color.darker_gray));
					
		  			if(getString(R.string.internal_store).
							equalsIgnoreCase(oneFolder.getText().toString())){
//						localpartitionView.setVisibility(View.GONE);
//						filelistView.setVisibility(View.VISIBLE);
		  				oneFolder.setText(R.string.tab_local);
						twoFolder.setText(R.string.tab_idata);
					}else{
						finish();
					}
		  			
		  		//真实目录
		  		}else{
					if(prefix.equals(curParent) || "/data/data".equals(curParent) || "smb://192.168.169.1/".equals(curParent)){
//						isRoot = true;
						localpartitionView.setVisibility(View.VISIBLE);
						filelistView.setVisibility(View.GONE);
						pathView.setVisibility(View.INVISIBLE);
						return false;
					}else{
//						isRoot = false;
					}
					
					new Thread(new Runnable() {
						public void run() {
							try {
								//idata
								if(curParent.startsWith("smb://")){
									jcifs.Config.setProperty( "jcifs.smb.lmCompatibility", "0");
							        NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(null, "admin", "admin");
									final SmbFile file = new SmbFile(curParent, auth);
									final SmbFile[] files = file.listFiles(new MyIdataFIleFilter());
									
								    runOnUiThread(new Runnable() {
										
										@Override
										public void run() {
											String tempPath = curParent;
											tempPath = tempPath.substring(0, tempPath.length()-1);
											tempPath = tempPath.substring(tempPath.lastIndexOf("/")+1);
											
											FileListAdapter listAdapter = (FileListAdapter)filelistView.getAdapter();
											listAdapter.setFileArray(files);
											listAdapter.setMoveOrCopy(true);
											filelistView.setAdapter(listAdapter);
										      
										    curParent = file.getParent();
										     pathView.setText(file.getPath());
										      
										  	try {
												isCanWrite = file.canWrite();
											} catch (SmbException e) {
												e.printStackTrace();
											}
											if(isCanWrite){
												copyOrMoveView.setClickable(true);
												copyOrMoveView.setTextColor(getResources().getColor(R.color.category_color));
											}else{
												copyOrMoveView.setClickable(false);
												copyOrMoveView.setTextColor(getResources().getColor(android.R.color.darker_gray));
											}
										}
									});
								 //手机
								}else{
									final File file = new File(curParent);
									final File[] files = file.listFiles(new MyFIleFilter());
									
								    runOnUiThread(new Runnable() {
										
										@Override
										public void run() {
											String tempPath = curParent;
											tempPath = tempPath.substring(0, tempPath.length()-1);
											tempPath = tempPath.substring(tempPath.lastIndexOf("/")+1);
											
											LocalFileListAdapter listAdapter = (LocalFileListAdapter)filelistView.getAdapter();
											listAdapter.setFileArray(files);
											listAdapter.setMoveOrCopy(true);
											filelistView.setAdapter(listAdapter);
										      
									       curParent = file.getParent();
									       pathView.setText(file.getPath());
										      
										  	isCanWrite = file.canWrite();
											if(isCanWrite){
												copyOrMoveView.setClickable(true);
												copyOrMoveView.setTextColor(getResources().getColor(R.color.category_color));
											}else{
												copyOrMoveView.setClickable(false);
												copyOrMoveView.setTextColor(getResources().getColor(android.R.color.darker_gray));
											}
										}
									});
								}
							}catch (Exception e) {
								e.printStackTrace();
							}
						}
					}).start();
		  		}
          return false;
	  }
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public void finish() {
		Intent i = new Intent();
		i.putExtra("fromPath", fromFile);
		setResult(2, i);
		super.finish();
	}
	
	public void moveIdataDirectory(String srcDirName, String destDirName) {
		try {
			SmbFile srcDir = getSmbFile(srcDirName);
			
			if(!srcDir.exists() || !srcDir.isDirectory())  
				return;
		   
			SmbFile destDir = getSmbFile(destDirName + srcDir.getName());
		   if(!destDir.exists())
			   destDir.mkdirs();
		   
		   /**
		    * 如果是文件则移动，否则递归移动文件夹。删除最终的空源文件夹
		    * 注意移动文件夹时保持文件夹的树状结构
		    */
		   SmbFile[] sourceFiles = srcDir.listFiles();
		   for (SmbFile sourceFile : sourceFiles) {
			   if (sourceFile.isFile()){
				   Log.i("moveDirectory", "file----sourcePath ="+sourceFile.getPath() +"; destPath ="+destDir.getPath());
				   moveIdataFile(sourceFile.getPath(), destDir.getPath());
			   }else if (sourceFile.isDirectory()){
				   Log.i("moveDirectory", "dir----sourcePath ="+sourceFile.getPath() +"; destPath ="+destDir.getPath());
				   moveIdataDirectory(sourceFile.getPath(), 
						   destDir.getPath());
			   }else{}
				   
		   }
		   srcDir.delete();
		} catch (SmbException e) {
			e.printStackTrace();
		}  
	}
	
	public boolean moveIdataFile(String srcFileName, String destDirName) {
		try {
		SmbFile srcFile = getSmbFile(srcFileName);
		
		if(!srcFile.exists() || !srcFile.isFile()) 
		    return false;
		
		SmbFile destDir = getSmbFile(destDirName);
		if (!destDir.exists())
			destDir.mkdirs();
		
		srcFile.renameTo(getSmbFile(destDirName + srcFile.getName()));
		return true;
		} catch (SmbException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/** 
	* 移动文件 
	* @param srcFileName 	源文件完整路径
	* @param destDirName 	目的目录完整路径
	* @return 文件移动成功返回true，否则返回false 
	*/  
	public boolean moveFile(String srcFileName, String destDirName) {
		
		File srcFile = new File(srcFileName);
		if(!srcFile.exists() || !srcFile.isFile()) 
		    return false;
		
		File destDir = new File(destDirName);
		if (!destDir.exists())
			destDir.mkdirs();
		
		return srcFile.renameTo(new File(destDirName + File.separator + srcFile.getName()));
	}
	
	/** 
	* 移动目录 
	* @param srcDirName 	源目录完整路径
	* @param destDirName 	目的目录完整路径
	* @return 目录移动成功返回true，否则返回false 
	*/  
	public  boolean moveDirectory(String srcDirName, String destDirName) {
		
		File srcDir = new File(srcDirName);
		if(!srcDir.exists() || !srcDir.isDirectory())  
			return false;  
	   
	   File destDir = new File(destDirName + File.separator + srcDir.getName());
	   if(!destDir.exists())
		   destDir.mkdirs();
	   
	   /**
	    * 如果是文件则移动，否则递归移动文件夹。删除最终的空源文件夹
	    * 注意移动文件夹时保持文件夹的树状结构
	    */
	   File[] sourceFiles = srcDir.listFiles();
	   for (File sourceFile : sourceFiles) {
		   if (sourceFile.isFile()){
			   Log.i("moveDirectory", "file----sourcePath ="+sourceFile.getAbsolutePath() +"; destPath ="+destDir.getAbsolutePath());
			   moveFile(sourceFile.getAbsolutePath(), destDir.getAbsolutePath());
		   }else if (sourceFile.isDirectory()){
			   Log.i("moveDirectory", "dir----sourcePath ="+sourceFile.getAbsolutePath() +"; destPath ="+destDir.getAbsolutePath());
			   moveDirectory(sourceFile.getAbsolutePath(), 
					   destDir.getAbsolutePath());
		   }else{}
			   
	   }
	   return srcDir.delete();
	}
	
	/**
	 * 拷贝idata目录或文件
	 */
	public int copyLocalToIdata(String fromFile, String toFile)
    {
		try{
			Log.i("idataPath", "copyLocalToIdata------fromFile ="+fromFile+"; toFile ="+toFile);
	        //要复制的文件目录
	        File[] currentFiles;
	        
	        File root = new File(fromFile);
//	        SmbFile root = new SmbFile(fromFile);
	        //如同判断SD卡是否存在或者文件是否存在
	        //如果不存在则 return出去
	        if(root == null || !root.exists())
	        {
	            return -1;
	        }else if(root.exists() && root.isDirectory()){
	        	toFile += root.getName() + "/";
	        	  //目标目录
	        	SmbFile targetDir = getSmbFile(toFile);
	        	
//	        	SmbFile targetDir = new SmbFile(toFile);
	            //创建目录
	            if(!targetDir.exists())
	            {
	                targetDir.mkdirs();
	            }
	            //如果存在则获取当前目录下的全部文件 填充数组
	            currentFiles = root.listFiles();
	             
	            //遍历要复制该目录下的全部文件
	            for(int i= 0;i<currentFiles.length;i++)
	            {
	                if(currentFiles[i].isDirectory())//如果当前项为子目录 进行递归
	                {
	                	copyLocalToIdata(currentFiles[i].getPath() + "/", toFile);
	                     
	                }else//如果当前项为文件则进行文件拷贝
	                {
	                	CopyLocalToIdataFile(currentFiles[i], toFile + currentFiles[i].getName());
	                }
	            }
	        }else if(root.exists() && root.isFile()){
	        	//如果当前项为文件则进行文件拷贝
	        	CopyLocalToIdataFile(root, toFile + root.getName());
	        }
		}catch(Exception e){
			e.printStackTrace();
			return -1;
		}
        return 0;
    }
	
    //idata文件拷贝
    //要复制的目录下的所有非子目录(文件夹)文件拷贝
    public int CopyLocalToIdataFile(File fromFile, String toFile)
    {
/*        try
        {
        	Log.i("idataPath", "CopyLocalToIdataFile----------fromFile ="+fromFile+"; toFile ="+toFile);
//        	File fromFile = new File(fromFile);
        	SmbFile toSmb = getSmbFile(toFile);
        	FileInputStream fosfrom = new FileInputStream(fromFile);
        	SmbFileOutputStream fosto = new SmbFileOutputStream(toSmb);
            byte bt[] = new byte[102400];
            int c;
            while ((c = fosfrom.read(bt)) > 0) 
            {
//            	Log.i("idataPath", "CopyLocalToIdataFile----------per_read_size="+c);
                fosto.write(bt, 0, c);
//                Log.i("idataPath", "CopyLocalToIdataFile----------write end");
            }
            Log.i("idataPath", "CopyLocalToIdataFile----------close");
            fosfrom.close();
            fosto.close();
            return 0;
             
        } catch (Exception ex) 
        {
        	ex.printStackTrace();
            return -1;
        }*/
        // extract command-line arguments
        String host = "192.168.169.1";
        String username = "admin";
        String password = "admin";
        String filename = "UploadDownloadFiles.java";

        FileTransferClient ftp = null;

        try {
            // create client
//            log.info("Creating FTP client");
            ftp = new FileTransferClient();

            // set remote host
            ftp.setRemoteHost(host);
            ftp.setUserName(username);
            ftp.setPassword(password);

            // connect to the server
            ftp.connect();
              
//              FTPFile[] files = ftp.directoryList();
              FTPFile[] files = ftp.directoryList("Storage");
              Log.i("idataPath", "CopyLocalToIdataFile----------permission="+files[0].getPermissions()+"; owner ="+files[0].getOwner()+"; raw ="+files[0].getRaw());

              for(FTPFile  file : files){
            	  String name = file.getName();
	              if("html".equals(name)){
	            	    Log.i("idataPath", "CopyLocalToIdataFile----------permission="+file.getPermissions()+"; owner ="+file.getOwner()+"; raw ="+file.getRaw());
//	            	  	file.setPermissions("d---------");
	            	    Log.i("idataPath", "CopyLocalToIdataFile----------permission="+file.getPermissions()+"; owner ="+file.getOwner()+"; raw ="+file.getRaw());
	                    Log.i("idataPath", "CopyLocalToIdataFile----------fromFile="+fromFile.getPath()+"; remoteFile="+name);
	                    ftp.uploadFile(fromFile.getPath(), "Storage/"+name+"/"+fromFile.getName());
//	                    ftp.uploadURLFile(fromFile.getPath(), "ftp://192.168.169.1/Storage/"+name+"/"+fromFile.getName());//login error
	                    break;
	              }
              }

//            log.info("Downloading file");
//              ftp.downloadFile(filename, filename);
//            log.info("File downloaded");

//            log.info("Deleting remote file");
//            ftp.deleteFile(filename);
//            log.info("Deleted remote file");

//            File file = new File(filename + ".copy");
//            file.delete();
//            log.info("Deleted local file copy");

            // Shut down client
            ftp.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
	
	/**
	 * 拷贝idata目录或文件
	 */
	public int copyIdataToLocal(String fromFile, String toFile)
    {
		try{
			Log.i("idataPath", "copyIdataToLocal------fromFile ="+fromFile+"; toFile ="+toFile);
	        //要复制的文件目录
	        SmbFile[] currentFiles;
	        
	        SmbFile root = getSmbFile(fromFile);
//	        SmbFile root = new SmbFile(fromFile);
	        //如同判断SD卡是否存在或者文件是否存在
	        //如果不存在则 return出去
	        if(root == null || !root.exists())
	        {
	            return -1;
	        }else if(root.exists() && root.isDirectory()){
	        	toFile += root.getName() + "/";
	        	  //目标目录
	        	File targetDir = new File(toFile);
//	        	SmbFile targetDir = new SmbFile(toFile);
	            //创建目录
	            if(!targetDir.exists())
	            {
	                targetDir.mkdirs();
	            }
	            //如果存在则获取当前目录下的全部文件 填充数组
	            currentFiles = root.listFiles();
	             
	            //遍历要复制该目录下的全部文件
	            for(int i= 0;i<currentFiles.length;i++)
	            {
	                if(currentFiles[i].isDirectory())//如果当前项为子目录 进行递归
	                {
	                    copyIdataToLocal(currentFiles[i].getPath() + "/", toFile);
	                     
	                }else//如果当前项为文件则进行文件拷贝
	                {
	                    CopyIdataToLocalFile(currentFiles[i].getPath(), toFile + currentFiles[i].getName());
	                }
	            }
	        }else if(root.exists() && root.isFile()){
	        	//如果当前项为文件则进行文件拷贝
	        	 CopyIdataToLocalFile(root.getPath(), toFile + root.getName());
	        }
		}catch(Exception e){
			e.printStackTrace();
			return -1;
		}
        return 0;
    }
	
    //idata文件拷贝
    //要复制的目录下的所有非子目录(文件夹)文件拷贝
    public int CopyIdataToLocalFile(String fromFile, String toFile)
    {
        try
        {
        	Log.i("idataPath", "CopyIdataToLocalFile----------fromFile ="+fromFile+"; toFile ="+toFile);
        	SmbFile fromSmb = getSmbFile(fromFile);
//        	SmbFile toSmb = getSmbFile(toFile);
        	SmbFileInputStream fosfrom = new SmbFileInputStream(fromSmb);
        	FileOutputStream fosto = new FileOutputStream(toFile);
            byte bt[] = new byte[102400];
            int c;
            while ((c = fosfrom.read(bt)) > 0) 
            {
//            	Log.i("idataPath", "CopyIdataToLocalFile----------per_read_size="+c);
                fosto.write(bt, 0, c);
            }
            Log.i("idataPath", "CopyIdataToLocalFile----------close");
            fosfrom.close();
            fosto.close();
            return 0;
             
        } catch (Exception ex) 
        {
        	ex.printStackTrace();
            return -1;
        }
    }
    
	/**
	 * 拷贝idata目录或文件
	 */
	public int copyIdata(String fromFile, String toFile)
    {
		try{
			Log.i("idataPath", "copyIdata------fromFile ="+fromFile+"; toFile ="+toFile);
	        //要复制的文件目录
	        SmbFile[] currentFiles;
	        
	        SmbFile root = getSmbFile(fromFile);
//	        SmbFile root = new SmbFile(fromFile);
	        //如同判断SD卡是否存在或者文件是否存在
	        //如果不存在则 return出去
	        if(root == null || !root.exists())
	        {
	            return -1;
	        }else if(root.exists() && root.isDirectory()){
	        	toFile += root.getName() + "/";
	        	  //目标目录
	        	SmbFile targetDir = getSmbFile(toFile);
//	        	SmbFile targetDir = new SmbFile(toFile);
	            //创建目录
	            if(!targetDir.exists())
	            {
	                targetDir.mkdirs();
	            }
	            //如果存在则获取当前目录下的全部文件 填充数组
	            currentFiles = root.listFiles();
	             
	            //遍历要复制该目录下的全部文件
	            for(int i= 0;i<currentFiles.length;i++)
	            {
	                if(currentFiles[i].isDirectory())//如果当前项为子目录 进行递归
	                {
	                    copyIdata(currentFiles[i].getPath() + "/", toFile);
	                     
	                }else//如果当前项为文件则进行文件拷贝
	                {
	                    CopyIdataFile(currentFiles[i].getPath(), toFile + currentFiles[i].getName());
	                }
	            }
	        }else if(root.exists() && root.isFile()){
	        	//如果当前项为文件则进行文件拷贝
	        	 CopyIdataFile(root.getPath(), toFile + root.getName());
	        }
		}catch(Exception e){
			e.printStackTrace();
			return -1;
		}
        return 0;
    }
	
    //idata文件拷贝
    //要复制的目录下的所有非子目录(文件夹)文件拷贝
    public int CopyIdataFile(String fromFile, String toFile)
    {
        try
        {
        	Log.i("idataPath", "CopyIdataFile----------fromFile ="+fromFile+"; toFile ="+toFile);
        	SmbFile fromSmb = getSmbFile(fromFile);
        	SmbFile toSmb = getSmbFile(toFile);
        	SmbFileInputStream fosfrom = new SmbFileInputStream(fromSmb);
        	SmbFileOutputStream fosto = new SmbFileOutputStream(toSmb);
            byte bt[] = new byte[102400];
            int c;
            while ((c = fosfrom.read(bt)) > 0) 
            {
            	
                fosto.write(bt, 0, c);
            }
            Log.i("idataPath", "CopyIdataFile----------close");
            fosfrom.close();
            fosto.close();
            return 0;
             
        } catch (Exception ex) 
        {
        	ex.printStackTrace();
            return -1;
        }
    }
	
	/**
	 * 拷贝本地目录或文件
	 */
	public int copy(String fromFile, String toFile)
    {
        //要复制的文件目录
        File[] currentFiles;
        File root = new File(fromFile);
        //如同判断SD卡是否存在或者文件是否存在
        //如果不存在则 return出去
        if(!root.exists())
        {
            return -1;
        }else if(root.exists() && root.isDirectory()){
        	toFile += root.getName() + "/";
        	  //目标目录
            File targetDir = new File(toFile);
            //创建目录
            if(!targetDir.exists())
            {
                targetDir.mkdirs();
            }
            //如果存在则获取当前目录下的全部文件 填充数组
            currentFiles = root.listFiles();
             
        /*    //目标目录
            File targetDir = new File(toFile);
            //创建目录
            if(!targetDir.exists())
            {
                targetDir.mkdirs();
            }*/
            //遍历要复制该目录下的全部文件
            for(int i= 0;i<currentFiles.length;i++)
            {
                if(currentFiles[i].isDirectory())//如果当前项为子目录 进行递归
                {
                    copy(currentFiles[i].getPath() + "/", toFile);
                     
                }else//如果当前项为文件则进行文件拷贝
                {
                    CopyFile(currentFiles[i].getPath(), toFile + currentFiles[i].getName());
                }
            }
        }else if(root.exists() && root.isFile()){
        	//如果当前项为文件则进行文件拷贝
        	 CopyFile(root.getPath(), toFile + root.getName());
        }
        
        return 0;
    }
	     
	   
    //文件拷贝
    //要复制的目录下的所有非子目录(文件夹)文件拷贝
    public int CopyFile(String fromFile, String toFile)
    {
        try
        {
            InputStream fosfrom = new FileInputStream(fromFile);
            OutputStream fosto = new FileOutputStream(toFile);
            byte bt[] = new byte[102400];
            int c;
            while ((c = fosfrom.read(bt)) > 0) 
            {
                fosto.write(bt, 0, c);
            }
            fosfrom.close();
            fosto.close();
            return 0;
             
        } catch (Exception ex) 
        {
            return -1;
        }
    }
	
	private File[] getFileList(Map<Integer, ArrayList>categoryMap, int type){
		File[] sf = new File[categoryMap.get(type).size()];
		for(int i=0; i<categoryMap.get(type).size(); i++){
			Object[] obj = categoryMap.get(type).toArray();
			sf[i] = (File)obj[i];
		}
		return sf;
	}
	
	private SmbFile[] getIdataFileList(Map<Integer, ArrayList>categoryMap, int type){
		SmbFile[] sf = new SmbFile[categoryMap.get(type).size()];
		for(int i=0; i<categoryMap.get(type).size(); i++){
			Object[] obj = categoryMap.get(type).toArray();
			sf[i] = (SmbFile)obj[i];
		}
		return sf;
	}
}
