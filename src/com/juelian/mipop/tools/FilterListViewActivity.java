package com.juelian.mipop.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.juelian.mipop.AppInfo;
import com.juelian.mipop.ListViewAdapter;
import com.juelian.mipop.R;
import com.juelian.mipop.R.id;
import com.juelian.mipop.R.layout;
import com.juelian.mipop.R.string;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class FilterListViewActivity extends Activity {

	private ListView mListView;
	private List<AppInfo> mAppInfos;
	private PackageManager pm;
	private ListViewAdapter browseApplicationInfoAdapter;
	private String appPackNameString = "";
	private String appClassNameString = "";
	private String appNameString = "";
	public SharedPreferences sp;
	private AppInfo appInfo = null;
	private String keyString = "";
	
	private ProgressDialog mProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.applist_listview);
		
		keyString = getIntent().getStringExtra("key");
		
		sp = getSharedPreferences("appIntent", Context.MODE_WORLD_READABLE);
		
		mAppInfos = new ArrayList<AppInfo>();
		mListView = (ListView) findViewById(R.id.all_app_listview);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				appInfo = mAppInfos.get(position);
				appPackNameString = appInfo.getPkgName();
				appClassNameString = appInfo.getClsName();
				appNameString = appInfo.getAppLabel();
				confirmAdd();
			}
		});
		
		mProgressDialog = new ProgressDialog(FilterListViewActivity.this);
		mProgressDialog.setMessage(getResources().getText(R.string.loading));
		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.setCancelable(false);
		
		new AsyncTask<Void, Void, Void>() {
			
			@Override
			protected void onPreExecute() {
				mProgressDialog.show();
			};

			@Override
			protected Void doInBackground(Void... params) {
				queryFilterAppInfo();
				browseApplicationInfoAdapter = new ListViewAdapter(
						getApplicationContext(), mAppInfos);
				return null;
			}
			
			@Override
			protected void onPostExecute(Void result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				mListView.setAdapter(browseApplicationInfoAdapter);
				mProgressDialog.cancel();
			}
		}.execute();
	}

	private void queryFilterAppInfo() {
		pm = this.getPackageManager();
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		
		List<ResolveInfo> resolveInfos = pm
				.queryIntentActivities(mainIntent, PackageManager.MATCH_DEFAULT_ONLY);
		
		Collections.sort(resolveInfos,
				new ResolveInfo.DisplayNameComparator(pm));// 排序
		
		if(mAppInfos != null){
			mAppInfos.clear();
			for (ResolveInfo reInfo : resolveInfos) {
				String clsName = reInfo.activityInfo.name; // 获得该应用程序的启动Activity的name
				String pkgName = reInfo.activityInfo.packageName; // 获得应用程序的包名
				String appLabel = (String) reInfo.loadLabel(pm); // 获得应用程序的Label
				
				Drawable icon = reInfo.loadIcon(pm); // 获得应用程序图标
				
				// 为应用程序的启动Activity 准备Intent
				Intent launchIntent = new Intent();
				launchIntent.setComponent(new ComponentName(pkgName,
						clsName));
				
				// 创建一个AppInfo对象，并赋值
				AppInfo appInfo = new AppInfo();
				appInfo.setAppLabel(appLabel);
				appInfo.setPkgName(pkgName);
				appInfo.setClsName(clsName);
				appInfo.setAppIcon(icon);
				appInfo.setIntent(launchIntent);
				mAppInfos.add(appInfo); // 添加至列表中
			}
		}
	}


	public void confirmAdd() {
		AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
		mBuilder.setTitle("提示");
		mBuilder.setMessage(String.format(
				getResources().getString(R.string.add_freeze_message_format),
				appNameString));
		mBuilder.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Editor editor = sp.edit();
						editor.putString(keyString+"_packname", appPackNameString);
						editor.putString(keyString+"_classname", appClassNameString);
						editor.commit();
						browseApplicationInfoAdapter.notifyDataSetChanged();
						Intent intent = new Intent();
						intent.putExtra("labelName", appNameString);
						FilterListViewActivity.this.setResult(2, intent);
						finish();
					}
				});

		mBuilder.setNegativeButton(android.R.string.cancel,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});

		mBuilder.show();
	}
}
