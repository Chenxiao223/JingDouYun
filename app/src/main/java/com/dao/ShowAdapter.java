package com.dao;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.zjfd.wanpeng.newcouchbase.R;

import java.util.ArrayList;
import java.util.List;

public class ShowAdapter extends BaseAdapter {

	private Context mcontext;
	private List<ShowInfo> mList = new ArrayList<ShowInfo>();
	private LayoutInflater minflater;
	
	
	public ShowAdapter(Context context, List<ShowInfo> List) {
		this.mcontext = context;
		minflater = LayoutInflater.from(context);
		this.mList = List;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (null != mList) {
			return mList.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		if (null != mList) {
			return mList.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHold  viewHold;
		if(convertView == null)
		{
			viewHold = new ViewHold();
			convertView = minflater.inflate(R.layout.show, null);
			viewHold.tvfullcode =(TextView) convertView.findViewById(R.id.tv_showfullcode);
			viewHold.tvtime =(TextView) convertView.findViewById(R.id.tv_showscantime);
			
			convertView.setTag(viewHold);
		}
		else{
			viewHold = (ViewHold) convertView.getTag();
			
		}
		viewHold.tvfullcode.setText(mList.get(position).getEpc());
		viewHold.tvtime.setText(mList.get(position).getTime());
		return convertView;
	}
	
	private final static class ViewHold{

		TextView tvfullcode;
		TextView tvtime;
		
	}

}
