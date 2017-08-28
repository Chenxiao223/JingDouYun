package com.zjfd.wanpeng.newcouchbase.UI;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationSet;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//import com.couchbase.lite.CouchbaseLiteException;
//import com.couchbase.lite.Database;
//import com.couchbase.lite.Document;
//import com.couchbase.lite.Manager;
//import com.couchbase.lite.android.AndroidContext;
//import com.couchbase.lite.replicator.Replication;

import com.app.Constants;
import com.dao.Operation;
import com.google.gson.Gson;
import com.hiklife.rfidapi.radioBusyException;
import com.socks.library.KLog;
import com.zjfd.wanpeng.newcouchbase.Entity.CouchbaseBean;
import com.zjfd.wanpeng.newcouchbase.Entity.testBean;
import com.zjfd.wanpeng.newcouchbase.R;
import com.zjfd.wanpeng.newcouchbase.rfid.Result;
import com.zjfd.wanpeng.newcouchbase.rfid.RfidOperation;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class StartActivity extends BaseActivity {
    public static StartActivity mStartActivity;
    public CouchbaseBean mbean;
    //    public testBean mtestBean;
    ImageButton ib_two, ib_rfid;
    Button btn_go, btn_check;
    ImageView iv_connection, image1, image2;
    TextView tv1, tv2, tv3;
    protected static final int MSG_DISCONNECT = 3;
    protected static final int MSG_CONNECT = 2;
    protected static final int MSG_OPERATION_SUCCESS = 1;
    private static final long ANIM_TIME = 1000;
    boolean bln_judge = false;
    public List<String> list_time = new ArrayList<>();//存放采集时间的集合
    public List<String> list_sctime = new ArrayList<>();//存放上传时间的集合
    String Tid;
    Result mresult, res;
    //    private Handler handler = new Handler();
    private static int tagCount = 0;
    private static int uploadCount = 0;
    protected static final int MSG_TOAST =4;

    private class StartHander extends Handler {
        WeakReference<Activity> mActivityRef;

        StartHander(Activity activity) {
            mActivityRef = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            Activity activity = mActivityRef.get();
            if (activity == null) {
                return;
            }

            switch (msg.what) {

                case MSG_DISCONNECT:
                    int returnValue = (Integer) msg.obj;
                    switch (returnValue) {
                        case 0:
//                            tv_rfid_isconneted.setText("rfid已连接");
                            Toast.makeText(StartActivity.this, "rfid已连接", Toast.LENGTH_SHORT).show();
                            break;

                        case 1:
//                            tv_rfid_isconneted.setText("失败");
                            Toast.makeText(StartActivity.this, "失败", Toast.LENGTH_SHORT).show();
                            break;

                        case -1:
//                            tv_rfid_isconneted.setText("失败：忙中");
                            Toast.makeText(StartActivity.this, "失败：忙中", Toast.LENGTH_SHORT).show();
                            break;
                    }

                    break;


                case MSG_CONNECT:
                    int returnValue1 = (Integer) msg.obj;
                    switch (returnValue1) {
                        case 0:
//                            tv_rfid_isconneted.setText("rfid已连接");
//                            Toast.makeText(ActivityHomePage.this, "rfid已连接", Toast.LENGTH_SHORT).show();
                            iv_connection.setImageDrawable(getResources().getDrawable(R.drawable.connection2));
                            break;

                        case -1:
//                            tv_rfid_isconneted.setText("失败");
                            Toast.makeText(StartActivity.this, "失败", Toast.LENGTH_SHORT).show();
                            break;

                        case -2:
//                            tv_rfid_isconneted.setText("失败：忙中");
                            Toast.makeText(StartActivity.this, "失败：忙中", Toast.LENGTH_SHORT).show();

                            break;

                        case 2:
//                            tv_rfid_isconneted.setText("失败：设置天线");
                            Toast.makeText(StartActivity.this, "失败：设置天线", Toast.LENGTH_SHORT).show();
                            break;
                    }

                    break;

                case MSG_OPERATION_SUCCESS:
                    String returnValue2 = (String) msg.obj;
//                    tv_show_epc.setText(returnValue2);
                    break;

                default:

                    break;

            }
        }
    }

    ;
    private Handler hMsg = new StartHander(this);


    @Override
    protected int getLayID() {
        return R.layout.activity_start;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        mStartActivity = this;
        connectRadio();
        initview();

//        tv1.setText(mbean.getData().getObject().get(0).getGsName());
//        tv2.setText(mbean.getData().getObject().get(0).getGsAbbre());
//        tv3.setText(mbean.getData().getObject().get(0).getAttach());


    }

    private void initDate() {
//   String  path="http://192.168.1.13:8080/mc/newjson.txt";
        showLoading();
        if (mresult == null || mresult.getReadInfo().toString().equals("")) {
            Toast.makeText(StartActivity.this, "未扫描标签", Toast.LENGTH_SHORT).show();
        } else {


            String address = "http://192.168.1.66:50789/WebService1.asmx/GetTIDDate";
            String ServerIp = "192.168.1.211";
            String Name = "zjfd123";
            String TID = mresult.getReadInfo().toString();
            System.out.println("***********" + TID);
//        String TID="A08011052000648B0EC9F800";
            String params = "ServerIp=" + ServerIp + "&Name=" + Name + "&data=" + TID;
            URL url = null;
            try {
                url = new URL(address + "?" + params);
                System.out.println("888888888888888888" + url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            //创建okHttpClient对象
            OkHttpClient mOkHttpClient = new OkHttpClient();
            //创建一个Request
            final Request request = new Request.Builder()
                    .url(url)
                    .build();
            //new call
            Call call = mOkHttpClient.newCall(request);
            //请求加入调度
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                    System.out.println("出错了");
//                showLoadinglose()
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            disssLoading();
                            Toast.makeText(StartActivity.this, "验证出错", Toast.LENGTH_SHORT).show();
                        }
                    });


                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            disssLoading();
                            Toast.makeText(StartActivity.this, "验证成功", Toast.LENGTH_SHORT).show();
                        }
                    });

                    String htmlStr = response.body().string();
                    System.out.println("1111111111111111111" + htmlStr);
                    Gson gson = new Gson();
                    mbean = gson.fromJson(htmlStr, CouchbaseBean.class);

                }


            });
        }
    }


    //连接RFID模块，返回结果
    public void connectRadio() {
        new Thread() {

            public void run() {

                Message closemsg = new Message();

                try {
                    RfidOperation.setAntennaPower(15);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                closemsg.obj = RfidOperation.connectRadio();
                closemsg.what = MSG_CONNECT;
                hMsg.sendMessage(closemsg);
            }

            ;

        }.start();

    }


    //断开连接RFID模块，返回结果
    public void disconnectRadio() {
        new Thread() {

            public void run() {

                Message closemsg = new Message();
                closemsg.obj = RfidOperation.DisconnectRadio();
                closemsg.what = MSG_DISCONNECT;
                hMsg.sendMessage(closemsg);
            }

            ;

        }.start();

    }


    private void initview() {

        ib_two = (ImageButton) findViewById(R.id.ib_two);
        ib_rfid = (ImageButton) findViewById(R.id.ib_rfid);
        btn_go = (Button) findViewById(R.id.btn_go);
        btn_check = (Button) findViewById(R.id.btn_check);
        tv1 = (TextView) findViewById(R.id.tv1);
        tv2 = (TextView) findViewById(R.id.tv2);
        tv3 = (TextView) findViewById(R.id.tv3);
        iv_connection = (ImageView) findViewById(R.id.iv_connection);
        image1 = (ImageView) findViewById(R.id.image1);
        image2 = (ImageView) findViewById(R.id.image2);
        //二维码
        ib_two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                open();
                startActivity(new Intent(StartActivity.this, ScanActivity.class));
            }
        });

        //RFID
        ib_rfid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                getRfid();
//                if (btn_scan.getText().toString().equals("开始扫描")) {
                    int i = -1;
                    try {
                        i = Operation.StartInventory();
                    } catch (radioBusyException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    if (i == 1) {
//                        tagInfoList.clear();
//                        showInfoList.clear();
                        tagCount = 0;
                        uploadCount = 0;


                        Message closemsg = new Message();
                        closemsg.obj = 8;
                        closemsg.what = MSG_TOAST;
                        hMsg.sendMessage(closemsg);

                    } else {
                        return;
                    }
//                } else if (btn_scan.getText().toString().equals("停止扫描")) {
//
//                    int i = Operation.StopInventory();
//                    if (i == 1) {
//                        Message closemsg = new Message();
//                        closemsg.obj = 7;
//                        closemsg.what = MSG_TOAST;
//                        hMsg.sendMessage(closemsg);
//
//                    } else {
//                        return;
//                    }
//                }
            }
        });
//详情
        btn_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


//                if (mbean==null){
                Intent intent = new Intent(StartActivity.this, MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.MEAN, mbean);
//                intent.putExtra(Constants.MEAN,mbean);
                intent.putExtras(bundle);
                startActivity(intent);
//                }else{
//                    Toast.makeText(StartActivity.this, "无信息，请检查是否验证：" , Toast.LENGTH_SHORT).show();
//                }

            }
        });
//        Tid=mresult.getReadInfo().toString();
        //验证
        btn_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                initDate();
//                bln_judge = true;
                btn_go.setVisibility(View.VISIBLE);
            }
        });


    }

    /**
     * 打开二维码扫描
     */
    private void open() {
        config();
//        startActivityForResult(new Intent(StartActivity.this,
//                CaptureActivity.class), 0);

    }

    /**
     * 提高屏幕亮度
     */
    private void config() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = 1.0f;
        getWindow().setAttributes(lp);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String aws = bundle.getString("result");
            if (aws != null) {
                image1.setVisibility(View.VISIBLE);
            }
            tv1.setText(aws);
            System.out.println("二维码扫描结果：" + aws);
            Toast.makeText(StartActivity.this, "二维码扫描结果：" + aws, Toast.LENGTH_SHORT).show();
//            Toast.makeText(getApplicationContext(), "默认Toast样式",
//                    Toast.LENGTH_SHORT).show();


        }

    }


    public String getRfid() {
        mresult = RfidOperation.readUnGivenTid((short) 0, (short) 6);
        Result res = RfidOperation.readUnGivenEpc((short) 0, (short) 6);

        String fruit = "TID:" + mresult.getReadInfo().toString() + "和EPC:" + res.getReadInfo().toString();
//        String fruit=mresult.getReadInfo().toString()+res.getReadInfo().toString();
        if (mresult.getReadInfo().toString() != null || mresult.getReadInfo().toString() != "") {
            image2.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(StartActivity.this, "RFID扫描未成功", Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(StartActivity.this, "RFID扫描结果：" + fruit, Toast.LENGTH_SHORT).show();
//       String tid= mresult.getReadInfo().toString();
        return mresult.getReadInfo().toString();

    }


    //点击back键
    @Override
    public void onBackPressed() {
        //点击弹出对话框
        new AlertDialog.Builder(this).setTitle("温馨提示").setMessage("是否要退出应用？")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        disconnectRadio();

                    }
                })
                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }

    private void StartAnimation() {

        ObjectAnimator animatorX = ObjectAnimator.ofFloat(image2, "scaleX", 0f, 1f);
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(image2, "scaleY", 0f, 1f);
        AnimatorSet set = new AnimatorSet();
        set.setDuration(ANIM_TIME).play(animatorX).with(animatorY);
        set.start();

    }

}