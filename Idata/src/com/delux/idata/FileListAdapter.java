package com.delux.idata;

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

public class FileListAdapter extends BaseAdapter {

	private SmbFile[] fileArray;
	private LayoutInflater mInflater;

	public FileListAdapter(Context context, SmbFile[] fileArray){
		this.mInflater = LayoutInflater.from(context);
		this.fileArray = fileArray;
	}
	
	public SmbFile[] getFileArray() {
		return fileArray;
	}

	public void setFileArray(SmbFile[] fileArray) {
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
		
		SmbFile file = fileArray[position];
		
		String name = FileUtil.getName(file);
		try {
			if(file.isDirectory()){
				holder.icon.setImageResource(R.drawable.folder);
			}else{
				holder.icon.setImageResource(FileUtil.getFileIconResId(name));
			}
		} catch (SmbException e) {
			e.printStackTrace();
		}
		
		holder.name.setText(name);
		
		return convertView;
	}
	
}
