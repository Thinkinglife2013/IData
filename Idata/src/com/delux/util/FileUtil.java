package com.delux.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URLEncoder;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.delux.idata.R;
import com.delux.idata.SelectDirActivity;

public class FileUtil {
	public static final int DEFAULT = 0;
	public static final int PHOTO = 1;
	public static final int VIDEO = 2;
	public static final int MUSIC = 3;
	public static final int DOC = 4;
	public static final int FOLDER = 5;
	public static final int ROOT = 6;
	
	/**
	 *  连接idata获取文件对象           
	 */
	public static SmbFile createNewFileOnIdata(String newName){
		 try {
			jcifs.Config.setProperty( "jcifs.smb.lmCompatibility", "0");
			jcifs.Config.setProperty( "jcifs.smb.client.responseTimeout", "5000");
			
	        NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(null, "admin", "admin");
        
			SmbFile file = new SmbFile("smb://192.168.169.1/Share/", auth);
			SmbFile[] smbFile = file.listFiles();
			
			if(smbFile.length >0){
				String path = smbFile[0].getPath();
				String newPath = path + newName; 
				
				jcifs.Config.setProperty( "jcifs.smb.lmCompatibility", "0");
				jcifs.Config.setProperty( "jcifs.smb.client.responseTimeout", "5000");
				
		        NtlmPasswordAuthentication auth2 = new NtlmPasswordAuthentication(null, "admin", "admin");
	        
				SmbFile newFile = new SmbFile(newPath, auth);
				
				if(!newFile.exists())
					newFile.createNewFile();
				return newFile;
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 获取文件类别
	 */
	public static int getFileCategory(String fileName){
		if(fileName != null){
			int index = fileName.lastIndexOf(".");
			if(index != -1){
				String suffix = fileName.substring(index+1);
				if("GIF".equalsIgnoreCase(suffix) || "png".equalsIgnoreCase(suffix) || "jpg".equalsIgnoreCase(suffix)
						|| "bmp".equalsIgnoreCase(suffix) || "tiff".equalsIgnoreCase(suffix) || "psd".equalsIgnoreCase(suffix)
						|| "swf".equalsIgnoreCase(suffix) || "svg".equalsIgnoreCase(suffix) || "JPEG".equalsIgnoreCase(suffix)){
					return PHOTO;
				}else if("rmvb".equalsIgnoreCase(suffix) || "rm".equalsIgnoreCase(suffix) || "wmv".equalsIgnoreCase(suffix)
						|| "avi".equalsIgnoreCase(suffix) || "MOV".equalsIgnoreCase(suffix) || "MPEG".equalsIgnoreCase(suffix) 
						|| "ASF".equalsIgnoreCase(suffix) || "mp4".equalsIgnoreCase(suffix)){
					return VIDEO;
				}else if("mp3".equalsIgnoreCase(suffix) || "wma".equalsIgnoreCase(suffix) || "wav".equalsIgnoreCase(suffix)
						|| "ogg".equalsIgnoreCase(suffix) || "ac3".equalsIgnoreCase(suffix) || "aiff".equalsIgnoreCase(suffix) 
						|| "dat".equalsIgnoreCase(suffix)){
					return MUSIC;
				}else if("txt".equalsIgnoreCase(suffix) || "wri".equalsIgnoreCase(suffix) || "xls".equalsIgnoreCase(suffix)
						|| "xlsx".equalsIgnoreCase(suffix) || "xml".equalsIgnoreCase(suffix) || "xsl".equalsIgnoreCase(suffix) 
						|| "doc".equalsIgnoreCase(suffix) || "ppt".equalsIgnoreCase(suffix) || "docx".equalsIgnoreCase(suffix)){
					return DOC;
				}else{
					return DEFAULT;
				}
			}
		}
			return DEFAULT;
	}
	
	public static int getFileIconResId(String fileName){
		if(fileName != null){
			int index = fileName.lastIndexOf(".");
			if(index != -1){
				String suffix = fileName.substring(index+1);
				if("GIF".equalsIgnoreCase(suffix) || "png".equalsIgnoreCase(suffix) || "jpg".equalsIgnoreCase(suffix)
						|| "bmp".equalsIgnoreCase(suffix) || "tiff".equalsIgnoreCase(suffix) || "psd".equalsIgnoreCase(suffix)
						|| "swf".equalsIgnoreCase(suffix) || "svg".equalsIgnoreCase(suffix) || "JPEG".equalsIgnoreCase(suffix)){
					return R.drawable.photo_file;
				}else if("rmvb".equalsIgnoreCase(suffix) || "rm".equalsIgnoreCase(suffix) || "wmv".equalsIgnoreCase(suffix)
						|| "avi".equalsIgnoreCase(suffix) || "MOV".equalsIgnoreCase(suffix) || "MPEG".equalsIgnoreCase(suffix) 
						|| "ASF".equalsIgnoreCase(suffix) || "mp4".equalsIgnoreCase(suffix)){
					return R.drawable.video_icon;
				}else if("mp3".equalsIgnoreCase(suffix) || "wma".equalsIgnoreCase(suffix) || "wav".equalsIgnoreCase(suffix)
						|| "ogg".equalsIgnoreCase(suffix) || "ac3".equalsIgnoreCase(suffix) || "aiff".equalsIgnoreCase(suffix) 
						|| "dat".equalsIgnoreCase(suffix)){
					return R.drawable.music_file;
				}else if("wri".equalsIgnoreCase(suffix) || "xml".equalsIgnoreCase(suffix)){
					return R.drawable.default_doc;
				}else if("doc".equalsIgnoreCase(suffix) || "docx".equalsIgnoreCase(suffix)){
					return R.drawable.doc_file;
				}else if("pdf".equalsIgnoreCase(suffix)){
					return R.drawable.pdf_file;
				}else if("epub".equalsIgnoreCase(suffix)){
					return R.drawable.epub_file;
				}else if("ppt".equalsIgnoreCase(suffix)){
					return R.drawable.ppt_file;
				}else if("txt".equalsIgnoreCase(suffix)){
					return R.drawable.txt_file;
				}else if("xls".equalsIgnoreCase(suffix) || "xlsx".equalsIgnoreCase(suffix)){
					return R.drawable.xls_file;
				}else{
					return R.drawable.default_fileicon;
				}
			}
		}
		return R.drawable.default_fileicon;
	}
	
	public static Intent getOpenLocalAppIntent(File file){
		String fileName = file.getName();
		if(fileName != null){
			int index = fileName.lastIndexOf(".");
			if(index != -1){
				String suffix = fileName.substring(index+1);
				 Intent intent = new Intent("android.intent.action.VIEW");
				 
				if("doc".equalsIgnoreCase(suffix) || "docx".equalsIgnoreCase(suffix)){
				     intent.addCategory("android.intent.category.DEFAULT");
				     intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				     Uri uri = Uri.fromFile(new File(file.getPath()));

				     intent.setDataAndType(uri, "application/msword");
				}else if("xls".equalsIgnoreCase(suffix) || "xlsx".equalsIgnoreCase(suffix)){
				     intent.addCategory("android.intent.category.DEFAULT");
				     intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				     Uri uri = Uri.fromFile(new File(file.getPath()));

				     intent.setDataAndType(uri, "application/vnd.ms-excel");
				}else if("ppt".equalsIgnoreCase(suffix) || "pptx".equalsIgnoreCase(suffix)){
					 intent.addCategory("android.intent.category.DEFAULT");
				     intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				     Uri uri = Uri.fromFile(new File(file.getPath()));

				     intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
				}else if("txt".equalsIgnoreCase(suffix) || "xml".equalsIgnoreCase(suffix)){
					intent.addCategory("android.intent.category.DEFAULT");
				     intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				     Uri uri = Uri.fromFile(new File(file.getPath()));

				     intent.setDataAndType(uri, "text/plain");
				}else if("html".equalsIgnoreCase(suffix) || "htm".equalsIgnoreCase(suffix)){
				    Uri uri = Uri.fromFile(new File(file.getPath())).buildUpon().encodedAuthority("com.android.htmlfileprovider").scheme("content").encodedPath(file.getPath()).build();

				     intent.setDataAndType(uri, "text/html");
				}else if("chm".equalsIgnoreCase(suffix)){
				     intent.addCategory("android.intent.category.DEFAULT");
				     intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				     Uri uri = Uri.fromFile(new File(file.getPath()));

				     intent.setDataAndType(uri, "application/x-chm");
				}else if("epub".equalsIgnoreCase(suffix)){
					//TODO
					return null;
					
				}else if("pdf".equalsIgnoreCase(suffix)){
					  intent.addCategory("android.intent.category.DEFAULT");
				     intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				     Uri uri = Uri.fromFile(new File(file.getPath()));

				     intent.setDataAndType(uri, "application/pdf");
				}else if("rmvb".equalsIgnoreCase(suffix) || "rm".equalsIgnoreCase(suffix) || "wmv".equalsIgnoreCase(suffix)
						|| "avi".equalsIgnoreCase(suffix) || "MOV".equalsIgnoreCase(suffix) || "MPEG".equalsIgnoreCase(suffix) 
						|| "ASF".equalsIgnoreCase(suffix)  || "mp4".equalsIgnoreCase(suffix)){
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				     intent.putExtra("oneshot", 0);
				     intent.putExtra("configchange", 0);

				     Uri uri = Uri.fromFile(new File(file.getPath()));

				     intent.setDataAndType(uri, "video/*");
				}else if("mp3".equalsIgnoreCase(suffix) || "wma".equalsIgnoreCase(suffix) || "wav".equalsIgnoreCase(suffix)
						|| "ogg".equalsIgnoreCase(suffix) || "ac3".equalsIgnoreCase(suffix) || "aiff".equalsIgnoreCase(suffix) 
						|| "dat".equalsIgnoreCase(suffix)){
				    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				     intent.putExtra("oneshot", 0);
				     intent.putExtra("configchange", 0);

				     Uri uri = Uri.fromFile(new File(file.getPath()));

				     intent.setDataAndType(uri, "audio/*");
				}else if("GIF".equalsIgnoreCase(suffix) || "png".equalsIgnoreCase(suffix) || "jpg".equalsIgnoreCase(suffix)
						|| "bmp".equalsIgnoreCase(suffix) || "tiff".equalsIgnoreCase(suffix) || "psd".equalsIgnoreCase(suffix)
						|| "swf".equalsIgnoreCase(suffix) || "svg".equalsIgnoreCase(suffix) || "JPEG".equalsIgnoreCase(suffix)){
					 intent.addCategory("android.intent.category.DEFAULT");
				     intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

				     Uri uri = Uri.fromFile(new File(file.getPath()));

				     intent.setDataAndType(uri, "image/*");
				}else if("zip".equalsIgnoreCase(suffix) || "rar".equalsIgnoreCase(suffix)){
					 intent.addCategory("android.intent.category.DEFAULT");
				     intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

				     Uri uri = Uri.fromFile(new File(file.getPath()));

				     intent.setDataAndType(uri, "application/zip");
				}else{
					return null;
				}
				return intent;
			}
		}
		return null;
	}
	
	public static Intent getOpenIdataAppIntent(SmbFile file){
		String fileName = file.getName();
		Log.i("getOpenIdataAppIntent", "idata_fileName ="+fileName);
		if(fileName != null){
			int index = fileName.lastIndexOf(".");
			if(index != -1){
				String path = file.getPath();
				Intent intent= new Intent();        
			    intent.setAction("android.intent.action.VIEW");    
			    
			    String openPath;
			    Uri uri;
			    if(path.lastIndexOf("Share/Storage/") == -1){
			    	openPath = path.substring(path.lastIndexOf("Share/SD_Card/")+14);
			    	uri = Uri.parse("http://192.168.169.1:8080/SD_Card/"+URLEncoder.encode(openPath));   
			    }else{
			    	openPath = path.substring(path.lastIndexOf("Share/Storage/")+14);
			    	uri = Uri.parse("http://192.168.169.1:8080/Storage/"+URLEncoder.encode(openPath));   
			    }
			    
				String suffix = fileName.substring(index+1);
				 
				if("doc".equalsIgnoreCase(suffix) || "docx".equalsIgnoreCase(suffix)){
				     intent.addCategory("android.intent.category.DEFAULT");
				     intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

				     intent.setDataAndType(uri, "application/msword");
				}else if("xls".equalsIgnoreCase(suffix) || "xlsx".equalsIgnoreCase(suffix)){
				     intent.addCategory("android.intent.category.DEFAULT");
				     intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

				     intent.setDataAndType(uri, "application/vnd.ms-excel");
				}else if("ppt".equalsIgnoreCase(suffix) || "pptx".equalsIgnoreCase(suffix)){
					 intent.addCategory("android.intent.category.DEFAULT");
				     intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

				     intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
				}else if("txt".equalsIgnoreCase(suffix) || "xml".equalsIgnoreCase(suffix)){
					intent.addCategory("android.intent.category.DEFAULT");
				     intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

				     intent.setDataAndType(uri, "text/plain");
				}else if("html".equalsIgnoreCase(suffix) || "htm".equalsIgnoreCase(suffix)){
//				    Uri uri = Uri.fromFile(new File(file.getPath())).buildUpon().encodedAuthority("com.android.htmlfileprovider").scheme("content").encodedPath(file.getPath()).build();

				     intent.setDataAndType(uri, "text/html");
				}else if("chm".equalsIgnoreCase(suffix)){
				     intent.addCategory("android.intent.category.DEFAULT");
				     intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

				     intent.setDataAndType(uri, "application/x-chm");
				}else if("epub".equalsIgnoreCase(suffix)){
					//TODO
					return null;
					
				}else if("pdf".equalsIgnoreCase(suffix)){
					  intent.addCategory("android.intent.category.DEFAULT");
				     intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

				     intent.setDataAndType(uri, "application/pdf");
				}else if("rmvb".equalsIgnoreCase(suffix) || "rm".equalsIgnoreCase(suffix) || "wmv".equalsIgnoreCase(suffix)
						|| "avi".equalsIgnoreCase(suffix) || "MOV".equalsIgnoreCase(suffix) || "MPEG".equalsIgnoreCase(suffix) 
						|| "ASF".equalsIgnoreCase(suffix)  || "mp4".equalsIgnoreCase(suffix)){
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				     intent.putExtra("oneshot", 0);
				     intent.putExtra("configchange", 0);

				     intent.setDataAndType(uri, "video/*");
				}else if("mp3".equalsIgnoreCase(suffix) || "wma".equalsIgnoreCase(suffix) || "wav".equalsIgnoreCase(suffix)
						|| "ogg".equalsIgnoreCase(suffix) || "ac3".equalsIgnoreCase(suffix) || "aiff".equalsIgnoreCase(suffix) 
						|| "dat".equalsIgnoreCase(suffix)){
				    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				     intent.putExtra("oneshot", 0);
				     intent.putExtra("configchange", 0);

				     intent.setDataAndType(uri, "audio/*");
				}else if("GIF".equalsIgnoreCase(suffix) || "png".equalsIgnoreCase(suffix) || "jpg".equalsIgnoreCase(suffix)
						|| "bmp".equalsIgnoreCase(suffix) || "tiff".equalsIgnoreCase(suffix) || "psd".equalsIgnoreCase(suffix)
						|| "swf".equalsIgnoreCase(suffix) || "svg".equalsIgnoreCase(suffix) || "JPEG".equalsIgnoreCase(suffix)){
					 intent.addCategory("android.intent.category.DEFAULT");
				     intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

				     intent.setDataAndType(uri, "image/*");
				}else if("zip".equalsIgnoreCase(suffix) || "rar".equalsIgnoreCase(suffix)){
					 intent.addCategory("android.intent.category.DEFAULT");
				     intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

				     intent.setDataAndType(uri, "application/zip");
				}
				return intent;
			}
		}
		return null;
	}
	
	public static String getName(SmbFile file){
		try {
			String name = file.getName();
			if(file.isDirectory()){
				if(name.endsWith("/"))
					name = name.substring(0, name.length()-1);
				else
					name = name.substring(0, name.length());
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
