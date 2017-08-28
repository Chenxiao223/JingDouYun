package com.zjfd.wanpeng.newcouchbase.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.app.Constants;
import com.socks.library.KLog;
import com.zjfd.wanpeng.newcouchbase.Adapter.TimeListAdapter;
import com.zjfd.wanpeng.newcouchbase.Entity.CouchbaseBean;
import com.zjfd.wanpeng.newcouchbase.Entity.Timebean;
import com.zjfd.wanpeng.newcouchbase.Entity.testBean;
import com.zjfd.wanpeng.newcouchbase.R;
import com.zjfd.wanpeng.newcouchbase.UI.BaiduMapActivity;
import com.zjfd.wanpeng.newcouchbase.UI.CompanyCoordinatesActivity;
import com.zjfd.wanpeng.newcouchbase.UI.StartActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/4/17.
 */
public class Fragment1 extends Fragment implements View.OnClickListener {
    private TextView tvLocation,tv_tel,tv_link,tv_gsname,tv_gsAbbre;
    private ImageButton btnMap,btn_map2,btn_tel,btn_link;
    private ListView  timelist;
    private TimeListAdapter mtimeListAdapter;
    private List<Timebean> timebeans;
//    public CouchbaseBean mbean;

    public     String    lat="31.210844";
    public    String    lon="121.601272";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment1_layout, null);

        initView(view);
        initData();

        return view;
    }

//     public     void  setBeanDate(testBean mbean){
//         KLog.d("值===fragment1" + mbean.data.object.get(0).GsName);
//     }
    private void initData() {
//        testBean mbean = (testBean) getArguments().getSerializable(Constants.MEAN);
        CouchbaseBean mbean= (CouchbaseBean) getArguments().getSerializable(Constants.MEAN);
//        Log.d("值===Fragment1",mbean.data.object.get(0).GsName);
        Log.d("值===Fragment1",mbean.getData().getObject().get(0).getGsName());
        String str=mbean.getData().getObject().get(0).getGsName();
        String s=mbean.getData().getObject().get(0).getGsAbbre();
        tv_gsname.setText(str.substring(5));
        tv_gsAbbre.setText(s.substring(3));
        timebeans=new ArrayList<>();
        mtimeListAdapter=new TimeListAdapter(getActivity(),timebeans);
        timelist.setAdapter(mtimeListAdapter);
        timebeans.clear();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mtimeListAdapter.notifyDataSetChanged();
            }
        });

        for (int i=0;i<mbean.getData().getObject().size();i++){
            String s2=mbean.getData().getObject().get(i).getCjDate();
            String s3=mbean.getData().getObject().get(i).getScDate();
//            timebeans.get(i).setTime(StartActivity.mStartActivity.list_time.get(i));
//            System.out.println("$$$$$$$$$$$$$$$" + StartActivity.mStartActivity.list_time.get(i).toString());
//            System.out.println("###############" + StartActivity.mStartActivity.list_time.get(i));
            Timebean  timebean=new Timebean(mbean.getData().getObject().get(i).getCjDate(),mbean.getData().getObject().get(i).getScDate());
//            Timebean  timebean= mbean.getData().getObject().get(i).getCjDate()

//            Log.d("循环",timebean.getTime());
            timebeans.add(timebean);
        }

        mtimeListAdapter.notifyDataSetChanged();

    }

    private void initView(View view) {
        timelist= (ListView) view.findViewById(R.id.list);
        tv_tel= (TextView) view.findViewById(R.id.tv_tel);
        tv_link= (TextView) view.findViewById(R.id.tv_link);
        tvLocation = (TextView) view.findViewById(R.id.tv_location);
        btnMap = (ImageButton) view.findViewById(R.id.btn_map);
        btn_map2 = (ImageButton) view.findViewById(R.id.btn_map2);
        btn_tel = (ImageButton) view.findViewById(R.id.btn_tel);
        btn_link = (ImageButton) view.findViewById(R.id.btn_link);
        tv_gsname= (TextView) view.findViewById(R.id.tv_gsname);
        tv_gsAbbre= (TextView) view.findViewById(R.id.tv_gsAbbre);
        btnMap.setOnClickListener(this);
        btn_map2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mintent = new Intent();
                mintent.putExtra(Constants.KEY_LAT,lat);
                mintent.putExtra(Constants.KEY_LON,lon);
                mintent.setClass(getActivity(), CompanyCoordinatesActivity.class);
                startActivity(mintent);
            }
        });


        btn_tel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
                        + tv_tel.getText().toString()));
                startActivity(intent);
            }
        });

        btn_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(tv_link.getText().toString());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), BaiduMapActivity.class);
        intent.setClass(getActivity(), CompanyCoordinatesActivity.class);
        startActivity(intent);
    }

}
