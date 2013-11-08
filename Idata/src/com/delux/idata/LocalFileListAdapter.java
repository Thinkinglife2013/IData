package com.delux.idata;

import java.io.File;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.delux.util.FileUtil;

public class LocalFileListAdapter extends BaseAdapter {

	private File[] fileArray;
	private LayoutInflater mInflater;
	private int categoryType;
	private boolean isMutilMode; //是否多选的状态
	private Context context;
	private int curShowToolPosition = -1;

	public LocalFileListAdapter(Context context, File[] fileArray, int categoryType){
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
		this.fileArray = fileArray;
		this.categoryType = categoryType;
	}
	
	public void setMutilMode(boolean isMutilMode) {
		this.isMutilMode = isMutilMode;
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
		
		final File file = fileArray[position];
		String name = getName(file);
		
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
		        
		        //重命名
		        renameView.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						
//						getItem(position);
						showRenameDialog(file);
						toolLineView.setVisibility(View.INVISIBLE);
					}
				});
			        
				Animation scale = AnimationUtils.loadAnimation(
						context, R.anim.scale_anim);
				toolLineView.startAnimation(scale);
				toolLineView.setVisibility(View.VISIBLE);
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
			if(file.isDirectory()){
				holder.icon.setImageResource(R.drawable.folder);
			}else{
				if(isMutilMode){//多选模式
					holder.cornerIcon.setVisibility(View.GONE);
					holder.divider.setVisibility(View.GONE);
					holder.checkBox.setVisibility(View.VISIBLE);
				}else{//非多选模式
					holder.cornerIcon.setVisibility(View.VISIBLE);
					holder.divider.setVisibility(View.VISIBLE);
					holder.checkBox.setVisibility(View.GONE);
				}
				
				holder.icon.setImageResource(FileUtil.getFileIconResId(name));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		holder.name.setText(name);
		
		return convertView;
	}
	
	private String getName(File file){
		try {
			String name = file.getName();
			if(file.isDirectory()){
				name = name.substring(0, name.length());
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
	
	private void showRenameDialog(File file){
	      AlertDialog.Builder builder = new AlertDialog.Builder(context);
//          builder.setIcon(android.R.drawable.ic_menu_add);
          builder.setTitle("重命名");
          EditText editTextAdd = new EditText(context);
          editTextAdd.setSingleLine(true);
          editTextAdd.setFocusable(true);
          editTextAdd.setSelectAllOnFocus(true);
          editTextAdd.setText(file.getName());

          builder.setView(editTextAdd);
          builder.setPositiveButton("OK", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		});
          builder.setNegativeButton("Cancle", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		});
          AlertDialog dialogAdd = builder.show();

	}

	public int getCurShowToolPosition() {
		return curShowToolPosition;
	}

	public void setCurShowToolPosition(int curShowToolPosition) {
		this.curShowToolPosition = curShowToolPosition;
	}
	
}
