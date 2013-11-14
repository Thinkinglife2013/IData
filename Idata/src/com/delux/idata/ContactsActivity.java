package com.delux.idata;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import jcifs.smb.SmbFileOutputStream;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.delux.util.FileUtil;

public class ContactsActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contacts_backup);
		View backupView = findViewById(R.id.backup_contacts);
		View restoreView = findViewById(R.id.restore_contacts);
		final TextView lastTimeView = (TextView)findViewById(R.id.last_time);
		final TextView localNum = (TextView)findViewById(R.id.local);
		
		SharedPreferences lastTimePreference = getSharedPreferences("last_contacts_operation", 0);
		String lastOperationTime = lastTimePreference.getString("time", "");
		if(!"".equals(lastOperationTime)){
			lastTimeView.setText(lastOperationTime);
		}

		backupView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final ProgressDialog progressDialog = ProgressDialog.show(ContactsActivity.this, null, getString(R.string.backuping), true, false); 
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						try{
							backupContactsToIdata(localNum);
	//						Log.i("contacts_backup", str);
							runOnUiThread(new Runnable() {
								
								@Override
								public void run() {
									Toast.makeText(ContactsActivity.this, R.string.backup_success, 1).show();
									String nowDate = getDateToString(new Date(), "yyyy/MM/dd");
									lastTimeView.setText(nowDate);
									//把上次操作时间保存到本地
									saveTopreference(nowDate);
									
									progressDialog.dismiss();
								}
							});
						} catch (Exception e) {
							e.printStackTrace();
							runOnUiThread(new Runnable() {
								
								@Override
								public void run() {
									Toast.makeText(ContactsActivity.this, R.string.not_connect_idata, 1).show();
									progressDialog.dismiss();
								}
							});
						}
					}
				}).start();
				
			}
		});

		restoreView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				restoreContacts();
				final ProgressDialog progressDialog = ProgressDialog.show(ContactsActivity.this, null, getString(R.string.restoring), true, false); 
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						try{
						restoreContactsToLocal();
						
							runOnUiThread(new Runnable() {
								
								@Override
								public void run() {
									Toast.makeText(ContactsActivity.this, R.string.restore_success, 1).show();
									String nowDate = getDateToString(new Date(), "yyyy/MM/dd");
									lastTimeView.setText(nowDate);
									//把上次操作时间保存到本地
									saveTopreference(nowDate);
									
									progressDialog.dismiss();
								}
							});
						} catch (Exception e) {
							e.printStackTrace();
							runOnUiThread(new Runnable() {
								
								@Override
								public void run() {
									Toast.makeText(ContactsActivity.this, R.string.not_connect_idata, 1).show();
									progressDialog.dismiss();
								}
							});
						}
					}
				}).start();
			}
		});
	}
	
	private void saveTopreference(String date){
		SharedPreferences lastOperationTime = getSharedPreferences("last_contacts_operation", 0);
		lastOperationTime.edit().putString("time", date).commit();
	}
	/**
	 * 
	 * getDateToString 根据指定日期格式获取对应字符串
	 * 
	 */
	public static String getDateToString(Date date, String format)
	{
		if (date == null)
		{
			return null;
		}
//		if (format == null)
//		{
//			format = DATE_FORMAT_DEFAULT;
//		}
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);

		return dateFormat.format(date);
	}

	public String str = "";

	public int getContact() {
		int count = 0;
		str = "";
		// 获得所有的联系人
		Cursor cur = getContentResolver().query(
				ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
		// 循环遍历
		if (cur.moveToFirst()) {
			int idColumn = cur.getColumnIndex(ContactsContract.Contacts._ID);

			int displayNameColumn = cur
					.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
			
			do {
				// 获得联系人的ID号
				String contactId = cur.getString(idColumn);
				// 获得联系人姓名
				String disPlayName = cur.getString(displayNameColumn);
				str += disPlayName;
				// 查看该联系人有多少个电话号码。如果没有这返回值为0
				int phoneCount = cur
						.getInt(cur
								.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
				if (phoneCount > 0) {
					// 获得联系人的电话号码
					Cursor phones = getContentResolver().query(
							ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
							null,
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID
									+ " = " + contactId, null, null);
					int i = 0;
					String phoneNumber;
					if (phones.moveToFirst()) {
						do {
							i++;
							phoneNumber = phones
									.getString(phones
											.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
							if (i == 1)
								str = str + "," + phoneNumber;
							System.out.println(phoneNumber);
						} while (phones.moveToNext());
					}
					
				}
				count++;
				str += "\r\n";
			} while (cur.moveToNext());

		}
		return count;
	}

	private void addContacts(String name, String num) {
		ContentValues values = new ContentValues();
		Uri rawContactUri = getContentResolver().insert(
				RawContacts.CONTENT_URI, values);
		long rawContactId = ContentUris.parseId(rawContactUri);

		values.clear();
		values.put(Data.RAW_CONTACT_ID, rawContactId);
		values.put(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);
		values.put(StructuredName.GIVEN_NAME, name);
		// values.put(StructuredName.FAMILY_NAME, "Mike");

		getContentResolver().insert(Data.CONTENT_URI, values);

		values.clear();
		values.put(Data.RAW_CONTACT_ID, rawContactId);
		values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
		values.put(Phone.NUMBER, num);
		values.put(Phone.TYPE, Phone.TYPE_HOME);
		// values.put(Email.DATA, "ligang.02@163.com");
		// values.put(Email.TYPE, Email.TYPE_WORK);
		getContentResolver().insert(Data.CONTENT_URI, values);
	}

	private void backupContactsToIdata(final TextView textView)  throws Exception{
		
		final int contactNum = getContact();
		
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				textView.setText(contactNum+getString(R.string.people));
			}
		});
		
		SmbFile newFile = FileUtil.createNewFileOnIdata("contacts_backup.txt");
		if(newFile != null){
			SmbFileOutputStream fosto = new SmbFileOutputStream(newFile);
//            byte bt[] = new byte[1024];
//            int c;
//            while ((c = fosfrom.read(bt)) > 0) 
//            {
//			String encodeStr = URLEncoder.encode(str);
                fosto.write(str.getBytes("UTF-8"), 0, str.getBytes("UTF-8").length);
                fosto.close();
//            }
		}
		
//		File saveFile = new File("/mnt/sdcard/contacts_backup.txt");
//		FileOutputStream outStream;
//			outStream = new FileOutputStream(saveFile);
//			outStream.write(str.getBytes());
//			outStream.close();
	}
	
	private void restoreContactsToLocal() throws Exception{
			SmbFile backupFile = FileUtil.getBackupFileOnIdata("contacts_backup.txt");
			if(backupFile != null){
				SmbFileInputStream inStream = new SmbFileInputStream(backupFile);
				
				ByteArrayOutputStream outStream = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024 * 5];
				int length = -1;
				while ((length = inStream.read(buffer)) != -1) {
					outStream.write(buffer, 0, length);
				}
				outStream.close();
				inStream.close();
				String txt = outStream.toString();
	
				String[] str = txt.split("\n");
				for (int i = 0; i < str.length; i++) {
					if (str[i].indexOf(",") >= 0) {
						String[] NameAndTel = str[i].split(",");
						addContacts(NameAndTel[0], NameAndTel[1]);
					}
				}
			}
			
//            byte bt[] = new byte[1024];
//            int c;
//            while ((c = fisfrom.read(bt)) > 0) 
//            {
//        		FileOutputStream outStream = new FileOutputStream(saveFile);
//                fosto.write(str.getBytes("UTF-8"), 0, str.getBytes("UTF-8").length);
//                fosto.close();
//            }
//		}
	}
	
	private void backupContacts() {
		getContact();

		File saveFile = new File("/mnt/sdcard/contacts_backup.txt");
		FileOutputStream outStream;
		try {
			outStream = new FileOutputStream(saveFile);
			outStream.write(str.getBytes());
			outStream.close();
		} catch (Exception e) {

			setTitle(e.toString());
		}
	}

	private void restoreContacts() {
		try {
			File file = new File("/mnt/sdcard/contacts_backup.txt");
			FileInputStream inStream = new FileInputStream(file);
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024 * 5];
			int length = -1;
			while ((length = inStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, length);
			}
			outStream.close();
			inStream.close();
			String txt = outStream.toString();

			String[] str = txt.split("\n");
			for (int i = 0; i < str.length; i++) {
				if (str[i].indexOf(",") >= 0) {
					String[] NameAndTel = str[i].split(",");
					addContacts(NameAndTel[0], NameAndTel[1]);
				}
			}

		} catch (IOException e) {
			setTitle(e.toString());
		}

	}

}
