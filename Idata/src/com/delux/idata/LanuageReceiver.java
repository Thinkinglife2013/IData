package com.delux.idata;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class LanuageReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent arg1) {
		   MainActivity.mainActivity.finish();
		   SettingActivity.settingActivity.finish();
		   
		   Intent it = new Intent(context,MainActivity.class);
           it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//这个必须加
           context.startActivity(it);
	}

}
