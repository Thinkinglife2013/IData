package com.delux.idata;

import java.io.File;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.delux.util.FileUtil;

public class LocalFileListAdapter extends BaseAdapter {

	private File[] fileArray;
	private LayoutInflater mInflater;

	public LocalFileListAdapter(Context context, File[] fileArray){
		this.mInflater = LayoutInflater.from(context);
		this.fileArray = fileArray;
	}
	
	public File[] getFileArray() {
		return fileArray;
	}

	public void setFileArray(File[] fileArray) {
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
		
		File file = fileArray[position];
		try {
			if(file.isDirectory()){
				holder.icon.setImageResource(R.drawable.folder);
			}else{
				holder.icon.setImageResource(R.drawable.default_fileicon);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String name = getName(file);
		holder.name.setText(name);
		
		return convertView;
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
	
}
