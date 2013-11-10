package com.delux.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

import com.delux.idata.R;

public class DialogUtil {
	public static void showExitDialog(final Context context){
		Dialog dialog = new AlertDialog.Builder(context)
//      .setIcon(R.drawable.icon)
      .setTitle(R.string.quit)
      .setMessage(R.string.sure_quit)
      .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int whichButton) {
        	  ((Activity)context).finish();
          }
      })
      .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int whichButton) {
          }
      })
      .create();
		dialog.show();
	}
	
	public static void showDeleteDialog(final Context context, DialogInterface.OnClickListener clickListener){
		Dialog dialog = new AlertDialog.Builder(context)
//      .setIcon(R.drawable.icon)
      .setTitle(R.string.delete)
      .setMessage(R.string.delete_confirm)
      .setPositiveButton(R.string.alert_dialog_ok, clickListener)
      .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int whichButton) {
          }
      })
      .create();
		dialog.show();
	}
}
