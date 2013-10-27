package com.delux.util;

import com.delux.idata.R;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

public class FileUtil {
	public static final int DEFAULT = 0;
	public static final int PHOTO = 1;
	public static final int VIDEO = 2;
	public static final int MUSIC = 3;
	public static final int DOC = 4;
	public static final int FOLDER = 5;
	public static final int ROOT = 6;
	
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
						|| "ASF".equalsIgnoreCase(suffix)){
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
					return R.drawable.default_fileicon;
				}else if("rmvb".equalsIgnoreCase(suffix) || "rm".equalsIgnoreCase(suffix) || "wmv".equalsIgnoreCase(suffix)
						|| "avi".equalsIgnoreCase(suffix) || "MOV".equalsIgnoreCase(suffix) || "MPEG".equalsIgnoreCase(suffix) 
						|| "ASF".equalsIgnoreCase(suffix)){
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
	
	public static String getName(SmbFile file){
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
