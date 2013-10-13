package com.xiude.idata;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;



public class LocalFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	/*	TextView mTextView = (TextView) contextView.findViewById(R.id.textview);
		*/
		//��ȡActivity���ݹ�4�Ĳ���
//		Bundle mBundle = getArguments();
//		int index = mBundle.getInt("arg");
		
		View contextView;
//		if(index == 0){
//			contextView = inflater.inflate(R.layout.fragment_item, container, false);
//		}else{
			contextView = inflater.inflate(R.layout.fragment_item2, container, false);
//		}
		return contextView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

}
