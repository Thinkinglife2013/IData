package com.delux.idata;

import java.net.MalformedURLException;
import java.net.URLEncoder;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity2 extends Activity {
	private String curParent = "smb://192.168.169.1/Share/";
	private ListView listView;
	private ImageView backView;
	private TextView titleTextView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.file_list);
		
		listView = (ListView)findViewById(R.id.subscription_list);
		titleTextView = (TextView)findViewById(R.id.title_text);
		backView = (ImageView)findViewById(R.id.imgback);
		backView.setVisibility(View.GONE);
		
		final FileListAdapter listAdapter = new FileListAdapter(MainActivity2.this, null);
	    new Thread(new Runnable() {
			
				@Override
				public void run() {
				try {
					jcifs.Config.setProperty( "jcifs.smb.lmCompatibility", "0");
			        NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(null, "admin", "admin");
		//	        SmbFileInputStream in = new SmbFileInputStream("smb://host/c/My Documents/somefile.txt", auth);
			        
//			        SmbFile oldFile = new SmbFile("smb://192.168.169.1/Share/Storage/b.___",auth);
//			        SmbFile newFile = new SmbFile("smb://192.168.169.1/Share/SD_Card/TRASH~RG.___/V.txt",auth);
//			        newFile.createNewFile();
//			        oldFile.renameTo(newFile);
			        
			        
			        SmbFile file = new SmbFile("smb://192.168.169.1/Share/",auth);
			        final SmbFile[] files = file.listFiles();
			        
			        MainActivity2.this.runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							  listAdapter.setFileArray(files);
						      listView.setAdapter(listAdapter);
						}
					});
			        
//			        for(SmbFile	file2 : files){
//			        	
//			        	System.out.println(getName(file2));
//			        }
//			        SmbFileInputStream in = new SmbFileInputStream(file);
//					
//					   byte[] b = new byte[8192];
//				        int n;
//				        while(( n = in.read( b )) > 0 ) {
//				            System.out.write( b, 0, n );
//				        }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	    
	    backView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				new Thread(new Runnable() {
					public void run() {
						try {
							jcifs.Config.setProperty( "jcifs.smb.lmCompatibility", "0");
					        NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(null, "admin", "admin");
							final SmbFile file = new SmbFile(curParent, auth);
							final SmbFile[] files = file.listFiles();
							
						    MainActivity2.this.runOnUiThread(new Runnable() {
								
								@Override
								public void run() {
									if("smb://192.168.169.1/Share/".equals(curParent)){
										backView.setVisibility(View.GONE);
									}
									
									String tempPath = curParent;
									tempPath = tempPath.substring(0, tempPath.length()-1);
									tempPath = tempPath.substring(tempPath.lastIndexOf("/")+1);
									  listAdapter.setFileArray(files);
								      listView.setAdapter(listAdapter);
								      titleTextView.setText(tempPath);
								      
								      curParent = file.getParent();
								}
							});
						    
							
						} catch (MalformedURLException e) {
							e.printStackTrace();
						} catch (SmbException e) {
							e.printStackTrace();
						}
					}
				}).start();
			
				
			}
		});
		
        //杩涘叆涓嬩竴绾х洰褰�
        listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View arg1,
					int position, long arg3) {
				SmbFile file = (SmbFile)parent.getItemAtPosition(position);
				try {
					String fileName = getName(file);
					
					SharedPreferences passwordPreference = getSharedPreferences("encrypt_file", 0);
					String isEncrypt = passwordPreference.getString(fileName, null);
					if(isEncrypt != null){
						//宸茬粡鍔犲瘑
						createPasswordDialog(getString(R.string.input_password), false, position);
					}else{
						//鏈姞瀵�
						openFileOrDir(file, backView, titleTextView, fileName, listAdapter);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
        
        ItemOnLongClick1();
	}
	
	private void openFileOrDir(SmbFile file, ImageView backView, TextView titleTextView, String fileName, FileListAdapter listAdapter){
		try {
			if(file.isDirectory()){
				backView.setVisibility(View.VISIBLE);
				titleTextView.setText(fileName);
				curParent = file.getParent();
				
				SmbFile[] subFiles = file.listFiles();
				listAdapter.setFileArray(subFiles);
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
			    startActivity(intent);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void createPasswordDialog(String title, final boolean isEncrypt, final int position){
		LayoutInflater factory = LayoutInflater.from(MainActivity2.this);//鎻愮ず妗�
        final View view = factory.inflate(R.layout.input_password, null);//杩欓噷蹇呴』鏄痜inal鐨�
        final EditText edit=(EditText)view.findViewById(R.id.editText);//鑾峰緱杈撳叆妗嗗璞�
        
        new AlertDialog.Builder(MainActivity2.this)
                .setTitle(title)//鎻愮ず妗嗘爣棰�
                .setView(view)
                .setPositiveButton(getString(R.string.ok),//鎻愮ず妗嗙殑涓や釜鎸夐挳
                        new android.content.DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which) {
                            	SmbFile file = (SmbFile)listView.getAdapter().getItem(position);
                            	String fileName = getName(file);
                            	SharedPreferences passwordPreference = getSharedPreferences("encrypt_file", 0);
                            	String editText = edit.getText().toString();
                            	if(isEncrypt){
	                            	
	                            	passwordPreference.edit().putString(fileName, editText).commit();
                            	}else{
                            		String password = passwordPreference.getString(fileName, null);
                            		if(editText.equals(password)){
                            			openFileOrDir(file, backView, titleTextView, fileName, (FileListAdapter)listView.getAdapter());
                            		}else{
                            			Toast.makeText(MainActivity2.this, "杈撳叆瀵嗙爜閿欒", 1).show();
                            		}
                            	}
                            }
                        }).setNegativeButton(getString(R.string.cancle), null).create().show();
	}
	
    private void ItemOnLongClick1() { 
    	//娉細setOnCreateContextMenuListener鏄笌涓嬮潰onContextItemSelected閰嶅浣跨敤鐨�
    	listView.setOnCreateContextMenuListener(new OnCreateContextMenuListener() { 

                         public void onCreateContextMenu(ContextMenu menu, View v, 
                                         ContextMenuInfo menuInfo) { 
                                 menu.add(0, 0, 0, getString(R.string.encrypt)); 

                         } 
                 }); 
     } 

     // 闀挎寜鑿滃崟鍝嶅簲鍑芥暟 
     public boolean onContextItemSelected(MenuItem item) { 
             AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item 
                             .getMenuInfo(); 
             int MID = (int) info.id;// 杩欓噷鐨刬nfo.id瀵瑰簲鐨勫氨鏄偣鍑荤殑绗嚑琛�

             switch (item.getItemId()) { 
             case 0: 
                     // 寮瑰嚭鍔犲瘑瀵硅瘽妗�
            	 	createPasswordDialog(getString(R.string.confirm_password), true, MID);
                    break; 

             default: 
                     break; 
             } 

             return super.onContextItemSelected(item); 
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

	private class FileListAdapter extends BaseAdapter{
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
			try {
				if(file.isDirectory()){
					holder.icon.setImageResource(R.drawable.folder);
				}else{
					holder.icon.setImageResource(R.drawable.default_fileicon);
				}
			} catch (SmbException e) {
				e.printStackTrace();
			}
			
			String name = getName(file);
			holder.name.setText(name);
			
			return convertView;
		}
		
	}
	
/*	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}*/

}
