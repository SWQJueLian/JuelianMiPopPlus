package com.juelian.mipop;

import java.util.List;

import com.juelian.mipop.R;
import com.juelian.mipop.R.id;
import com.juelian.mipop.R.layout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListViewAdapter extends BaseAdapter {

    public Context mContext;

    private List<AppInfo> mlistAppInfo = null;

    LayoutInflater infater = null;

    public ListViewAdapter(Context context,  List<AppInfo> apps) {
        infater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mlistAppInfo = apps ;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mlistAppInfo.size();
    }

    @Override
    public Object getItem(int position) {
        return mlistAppInfo.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

	@Override
    public View getView(int position, View convertview, ViewGroup arg2) {
        System.out.println("getView at " + position);
        View view = infater.inflate(R.layout.applist_listview_item, null);
        ImageView appLogo = (ImageView) view.findViewById(R.id.app_logo);;
        TextView appName = (TextView) view.findViewById(R.id.app_name);;

        AppInfo appInfo = (AppInfo) getItem(position);
        appLogo.setImageDrawable(appInfo.getAppIcon());
        appName.setText(appInfo.getAppLabel());
        return view;
    }
    
}
