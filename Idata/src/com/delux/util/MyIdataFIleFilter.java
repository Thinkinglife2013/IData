package com.delux.util;

import java.io.File;
import java.io.FileFilter;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileFilter;

public class MyIdataFIleFilter implements SmbFileFilter{

	@Override
	public boolean accept(SmbFile arg0) throws SmbException {
		if(arg0.isDirectory()){
			return true;
		}else{
			return false;
		}
	}

}
