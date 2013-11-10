package com.delux.util;

import java.io.File;
import java.io.FileFilter;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileFilter;

public class MyFIleFilter implements FileFilter{

	@Override
	public boolean accept(File file) {
		if(file.isDirectory()){
			return true;
		}else{
			return false;
		}
		
	}
	

}
