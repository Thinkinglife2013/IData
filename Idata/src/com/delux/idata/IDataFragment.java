package com.delux.idata;

import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.delux.util.FileUtil;



public class IDataFragment extends Fragment {
	private String curParent = "smb://192.168.169.1/Share/";
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View contextView = inflater.inflate(R.layout.fragment_item, container, false);
		View photoRow = contextView.findViewById(R.id.photo);
		View videoRow = contextView.findViewById(R.id.video);
		View musicRow = contextView.findViewById(R.id.music);
		View docRow = contextView.findViewById(R.id.doc);
		View folderRow = contextView.findViewById(R.id.folder);
		
		
		photoRow.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getActivity(), IDataFilesActivity.class);
				i.putExtra("type", FileUtil.PHOTO);
//				i.putExtra("PHOTO", categoryMap.get(FileUtil.PHOTO));
				i.putExtra("curParent", curParent);
				
				startActivity(i);
			}
		});
		
		videoRow.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getActivity(), IDataFilesActivity.class);
				i.putExtra("type", FileUtil.VIDEO);
//				i.putExtra("VIDEO", categoryMap.get(FileUtil.VIDEO));
				i.putExtra("curParent", curParent);
				
				startActivity(i);
			}
		});
		
		musicRow.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getActivity(), IDataFilesActivity.class);
				i.putExtra("type", FileUtil.MUSIC);
//				i.putExtra("MUSIC", categoryMap.get(FileUtil.MUSIC));
				i.putExtra("curParent", curParent);
				
				startActivity(i);
			}
		});
		
		docRow.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getActivity(), IDataFilesActivity.class);
				i.putExtra("type", FileUtil.DOC);
//				i.putExtra("DOC", categoryMap.get(FileUtil.DOC));
				i.putExtra("curParent", curParent);
				
				startActivity(i);
			}
		});
		
		folderRow.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getActivity(), IDataFilesActivity.class);
				i.putExtra("type", FileUtil.FOLDER);
//				i.putExtra("FOLDER", categoryMap.get(FileUtil.FOLDER));
				i.putExtra("curParent", curParent);
				
				startActivity(i);
			}
		});
		
		return contextView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.i("IDataFragment", "onActivityCreated");
		super.onActivityCreated(savedInstanceState);
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

}
