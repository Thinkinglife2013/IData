package com.delux.idata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.delux.util.DialogUtil;
import com.delux.util.FileUtil;

public class FileListAdapter extends BaseAdapter {

	private SmbFile[] fileArray;
	private LayoutInflater mInflater;
	public int categoryType;
	private boolean isMutilMode; //是否多选的状态
	private Context context;
	private int curShowToolPosition = -1;
	private boolean isMoveOrCopy;
	private Map<Integer, ArrayList> categoryMap;
	public Map<Integer,SmbFile> selectFiles = new HashMap<Integer, SmbFile>();

	public boolean isMoveOrCopy() {
		return isMoveOrCopy;
	}

	public void setMoveOrCopy(boolean isMoveOrCopy) {
		this.isMoveOrCopy = isMoveOrCopy;
	}
	
	public FileListAdapter(Context context, SmbFile[] fileArray,  int categoryType, Map<Integer, ArrayList> categoryMap){
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
		this.fileArray = fileArray;
		this.categoryType = categoryType;
		this.categoryMap = categoryMap;
	}
	
	public void setMutilMode(boolean isMutilMode) {
		this.isMutilMode = isMutilMode;
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
		public ImageView cornerIcon;
		public ImageView divider;
		public CheckBox checkBox;
		public RelativeLayout toolLayout;
		public LinearLayout toolLine;
	}
	
	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {
		ViewHolder holder;
		if(convertView == null){
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.file_item, null);
			holder.icon = (ImageView)convertView.findViewById(R.id.icon);
			holder.name = (TextView)convertView.findViewById(R.id.name);
			holder.cornerIcon = (ImageView)convertView.findViewById(R.id.corner_icon);
			holder.divider = (ImageView)convertView.findViewById(R.id.divider);
			holder.checkBox = (CheckBox)convertView.findViewById(R.id.checkbox);
			holder.toolLayout = (RelativeLayout)convertView.findViewById(R.id.tool);
			holder.toolLine = (LinearLayout)convertView.findViewById(R.id.tool_line);
		
			convertView.setTag(holder);
			
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		
		final SmbFile file = fileArray[position];
		String name = FileUtil.getName(file);
		
		final View toolLineView = holder.toolLine; //工具条
		//点击弹出工具条
		holder.toolLayout.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(final View v, MotionEvent event) {
			    int iAction = event.getAction();  
		        if (iAction == MotionEvent.ACTION_DOWN) { 
		        	if(curShowToolPosition > -1){
		        		parent.getChildAt(curShowToolPosition).findViewById(R.id.tool_line).setVisibility(View.INVISIBLE);
		        	}
		        	
		        	//当前显示工具栏的行号
		        	curShowToolPosition = position;
		        	
		        	v.setBackgroundResource(R.color.click_bg);
		        } else if (iAction == MotionEvent.ACTION_UP) { 
		        	v.setBackgroundResource(android.R.color.transparent);
		        }
		        
		        ImageView renameView = (ImageView)toolLineView.findViewById(R.id.rename);
		        ImageView copyView = (ImageView)toolLineView.findViewById(R.id.copy);
		        ImageView moveView = (ImageView)toolLineView.findViewById(R.id.move);
		        ImageView deleteView = (ImageView)toolLineView.findViewById(R.id.delete);
		        
		        //删除
		        deleteView.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						DialogUtil.showDeleteDialog(context, new OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								final ProgressDialog progressDialog = ProgressDialog.show(context, null, context.getString(R.string.delete_files), true, false); 
								new Thread(new Runnable() {
									public void run() {
										Log.i("parentPath", file.getPath());
										if(!deleteFile(file.getPath())){
											deleteDirectory(file.getPath());
										}
										
										SmbFile[] newFileArray = new SmbFile[fileArray.length-1];
										for(int i=0; i<fileArray.length-1; i++){
											newFileArray[i] = fileArray[i];
											if(i >= position){
												newFileArray[i] = fileArray[i+1];
											}
										}
										fileArray = newFileArray;
										
										if(categoryType != FileUtil.ROOT)
											categoryMap.put(categoryType, convertToList(fileArray));
										
										((FragmentActivity)context).runOnUiThread(new Runnable() {
											
											@Override
											public void run() {
												notifyDataSetChanged();
												progressDialog.dismiss();
											}
										});
									}
								}).start();
								
							}
						});
						
//						LocalFragment.onPostFresh(file.getPath());
						toolLineView.setVisibility(View.INVISIBLE);
						curShowToolPosition = -1;
					}
				});
		        
		        //移动
		        moveView.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if(categoryType == FileUtil.ROOT){
							Intent i = new Intent(context, SelectDirActivity.class);
							i.putExtra("moveOrCopy", "move");
							i.putExtra("fromFile", file.getPath());
							((FragmentActivity)context).startActivityForResult(i, 1);
	//						LocalFragment.onPostFresh(file.getPath());
							toolLineView.setVisibility(View.INVISIBLE);
							curShowToolPosition = -1;
						}else{
							Toast.makeText(context, R.string.no_move_tip, 1).show();
						}
					}
				});
		        
		        //重命名
		        renameView.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						showRenameDialog(file, position);
						toolLineView.setVisibility(View.INVISIBLE);
						curShowToolPosition = -1;
					}
				});
		        
		        //复制
		        copyView.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Intent i = new Intent(context, SelectDirActivity.class);
						i.putExtra("moveOrCopy", "copy");
						i.putExtra("fromFile", file.getPath());
						context.startActivity(i);
						toolLineView.setVisibility(View.INVISIBLE);
						curShowToolPosition = -1;
					}
				});
			        
				Animation scale = AnimationUtils.loadAnimation(
						context, R.anim.scale_anim);
				toolLineView.startAnimation(scale);
				toolLineView.setVisibility(View.VISIBLE);
				toolLineView.setTag("visible");
				scale.setAnimationListener(new AnimationListener() {

					@Override
					public void onAnimationStart(Animation animation) {
					}

					@Override
					public void onAnimationRepeat(Animation animation) {
					}

					@Override
					public void onAnimationEnd(Animation animation) {
						v.setBackgroundResource(android.R.color.transparent);
					}
				});
				return true;
			}
		});
		
		try {
			if(isMutilMode){//多选模式
				holder.toolLayout.setVisibility(View.GONE);
				holder.checkBox.setVisibility(View.VISIBLE);
				
				holder.checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						// TODO Auto-generated method stub
						if(isChecked){
							selectFiles.put(position, file);
						}else{
							if(selectFiles.containsKey(position))
								selectFiles.remove(position);
						}
						
					}
				});
				if(selectFiles.containsKey(position)){
					holder.checkBox.setChecked(true);
				}else{
					holder.checkBox.setChecked(false);
				}
			}else{//非多选模式
				//如果是移动或拷贝
				if(isMoveOrCopy){
					holder.toolLayout.setVisibility(View.INVISIBLE);
				}else{
					holder.toolLayout.setVisibility(View.VISIBLE);
				}
				holder.checkBox.setVisibility(View.GONE);
				holder.checkBox.setChecked(false);
			}
			
			if(file.isDirectory()){
				holder.icon.setImageResource(R.drawable.normal_folder);
			}else{
				holder.icon.setImageResource(FileUtil.getFileIconResId(name));
			}
		} catch (SmbException e) {
			e.printStackTrace();
		}
		
		holder.name.setText(name);
		
		return convertView;
	}
	
	public void delMany(final Map<Integer, SmbFile> delFiles, final ArrayList<SmbFile> allFiles){
		
		DialogUtil.showDeleteDialog(context, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				final ProgressDialog progressDialog = ProgressDialog.show(context, null, context.getString(R.string.delete_files), true, false); 
				new Thread(new Runnable() {
					public void run() {
						Log.i("parentPath", "delFiles_count ="+delFiles.size());
					
//						String filePath = "";
						for(Map.Entry<Integer,SmbFile>  entry : delFiles.entrySet()){
//							filePath = entry.getValue().getParent();
							String deletePath = entry.getValue().getPath();
							if(!deleteFile(deletePath)){
								deleteDirectory(deletePath);
							}
						
							allFiles.remove(entry.getValue());
							
							Log.i("parentPath", "fileArray_count ="+fileArray.length);
						}
						delFiles.clear();
						
						SmbFile[] newFileArray = new SmbFile[allFiles.size()];
						for(int i=0; i<allFiles.size(); i++){
							newFileArray[i] = allFiles.get(i);
						}
						fileArray = newFileArray;
						
						if(categoryType != FileUtil.ROOT)
							categoryMap.put(categoryType, allFiles);
						
						((FragmentActivity)context).runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								notifyDataSetChanged();
								progressDialog.dismiss();
							}
						});
					}
				}).start();
				
			}
		});
	}
	
	public void copyMany(Collection<SmbFile> fileCollection){
		ArrayList<String> pathList = new ArrayList<String>();
		for(SmbFile  file : fileCollection){
			pathList.add(file.getPath()+"/");
		}
		
		Intent i = new Intent(context, SelectDirActivity.class);
		i.putExtra("moveOrCopy", "copy");
		i.putExtra("fromManyFile", pathList);
		context.startActivity(i);
	}
	
	public void moveMany(Collection<SmbFile> fileCollection){
		ArrayList<String> pathList = new ArrayList<String>();
		for(SmbFile  file : fileCollection){
			pathList.add(file.getPath()+"/");
		}
		
		Intent i = new Intent(context, SelectDirActivity.class);
		i.putExtra("moveOrCopy", "move");
		i.putExtra("fromManyFile", pathList);
		((FragmentActivity)context).startActivityForResult(i, 1);
	}
	
	/** 
	* 删除文件 
	* @param srcFileName 	源文件完整路径
	* @return 文件删除成功返回true，否则返回false 
	*/  
	public boolean deleteFile(String srcFileName) {
		jcifs.Config.setProperty( "jcifs.smb.lmCompatibility", "0");
		jcifs.Config.setProperty( "jcifs.smb.client.responseTimeout", "5000");
		
        NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(null, "admin", "admin");
       
		try {
	        SmbFile srcFile = new SmbFile(srcFileName,auth);
			if(!srcFile.exists() || !srcFile.isFile()) 
			    return false;
			srcFile.delete();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/** 
	* 移动目录 
	* @param srcDirName 	源目录完整路径
	* @param destDirName 	目的目录完整路径
	* @return 目录移动成功返回true，否则返回false 
	*/  
	public  boolean deleteDirectory(String srcDirName) {
		jcifs.Config.setProperty( "jcifs.smb.lmCompatibility", "0");
		jcifs.Config.setProperty( "jcifs.smb.client.responseTimeout", "5000");
		
        NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(null, "admin", "admin");
       
		try {
	        SmbFile srcDir = new SmbFile(srcDirName,auth);
	    	if(!srcDir.exists() || !srcDir.isDirectory())  
				return false;  
	 	   /**
	 	    * 如果是文件则移动，否则递归移动文件夹。删除最终的空源文件夹
	 	    * 注意移动文件夹时保持文件夹的树状结构
	 	    */
	    	SmbFile[] sourceFiles = srcDir.listFiles();
	 	   for (SmbFile sourceFile : sourceFiles) {
	 		   if (sourceFile.isFile())
	 			   deleteFile(sourceFile.getPath());
	 		   else if (sourceFile.isDirectory())
	 			   deleteDirectory(sourceFile.getPath());
	 		   else
	 			   ;
	 	   }
	 	   srcDir.delete();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	   return true;
	}
	
	private void showRenameDialog(final SmbFile file, final int position){
	      AlertDialog.Builder builder = new AlertDialog.Builder(context);
//          builder.setIcon(android.R.drawable.ic_menu_add);
          builder.setTitle("重命名");
          final EditText editTextAdd = new EditText(context);
          editTextAdd.setSingleLine(true);
          editTextAdd.setFocusable(true);
          editTextAdd.setSelectAllOnFocus(true);
          
          String fileName = file.getName();
          if(fileName.endsWith("/"))
        	  fileName = fileName.substring(0, fileName.length()-1);
          editTextAdd.setText(fileName);

          builder.setView(editTextAdd);
          builder.setPositiveButton("OK", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String parentPath = file.getParent();
				
				String newName = editTextAdd.getText().toString();
				String newFilePath = parentPath+ "/" + newName;
				SmbFile newNameFile;
				try {
					jcifs.Config.setProperty( "jcifs.smb.lmCompatibility", "0");
					jcifs.Config.setProperty( "jcifs.smb.client.responseTimeout", "5000");
					
			        NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(null, "admin", "admin");
			       
			        newNameFile = new SmbFile(newFilePath,auth);
					file.renameTo(newNameFile);
					fileArray[position] = newNameFile;
					notifyDataSetChanged();
					
					categoryMap.put(categoryType, convertToList(fileArray));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
          builder.setNegativeButton("Cancle", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});
          AlertDialog dialogAdd = builder.show();
	}
	
	public ArrayList convertToList(SmbFile[] FileArray){
		ArrayList<SmbFile> list = new ArrayList<SmbFile>();
		for(SmbFile	file : fileArray){
			list.add(file);
		}
		return list;
	}

	public int getCurShowToolPosition() {
		return curShowToolPosition;
	}

	public void setCurShowToolPosition(int curShowToolPosition) {
		this.curShowToolPosition = curShowToolPosition;
	}
	
}
