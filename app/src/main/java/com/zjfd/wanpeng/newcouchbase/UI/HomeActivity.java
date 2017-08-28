package com.zjfd.wanpeng.newcouchbase.UI;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.app.Info;
import com.dao.AgentOperation;
import com.dao.BaseDao;
import com.dao.DBOperation;
import com.dao.Operation;
import com.dao.Setting;
import com.dao.ShowAdapter;
import com.dao.ShowInfo;
import com.dao.UploadInfo;
import com.hiklife.rfidapi.InventoryEvent;
import com.hiklife.rfidapi.OnInventoryEventListener;
import com.hiklife.rfidapi.radioBusyException;
import com.sdesrd.tracking.bgentlib.Bgent;
import com.zjfd.wanpeng.newcouchbase.R;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Administrator on 2017/8/9 0009.
 */
public class HomeActivity extends Activity implements View.OnClickListener {
    public static HomeActivity homeActivity;
    protected static final int MSG_SHOW_EPC_INFO = 1;
    protected static final int MSG_TOAST = 4;
    protected static final int MSG_TOAST_ERROR = 5;
    protected static final int MSG_TOAST_SUCCESS = 6;
    private static int tagCount = 0;
    private static int uploadCount = 0;
    private static List<String> tagInfoList = new ArrayList<String>();
    public static List<ShowInfo> showInfoList = new ArrayList<ShowInfo>();
    private static Button btn_scan;
    private ListView list_view;
    public static ShowAdapter showadapter;
    public static Setting setting;
    private ImageView iv_connection;
    protected static final int MSG_DISCONNECT = 3;
    protected static final int MSG_CONNECT = 2;
    protected static final int MSG_OPERATION_SUCCESS = 1;
    private int requestCode;
    public Spinner spinner_node;
    public static TextView tv_readCount, tv_uploadCount;
    private ImageView iv_ewm, iv_rfid;
    private int ewm = 0;
    private int rfid = 0;
    private int scan = 0;
    public static String epc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //
        homeActivity=this;
        connectRadio();//连接RFID模块
        setting = getSettingData();
        Oldsetting = setting;
        initView();

    }

    public void initView() {
        iv_ewm = (ImageView) findViewById(R.id.iv_ewm);
        iv_rfid = (ImageView) findViewById(R.id.iv_rfid);
        tv_readCount = (TextView) findViewById(R.id.tv_readcount);
        tv_uploadCount = (TextView) findViewById(R.id.tv_uploadcount);
        list_view = (ListView) findViewById(R.id.list);
        btn_scan = (Button) findViewById(R.id.btn_scan);
        btn_scan.setOnClickListener(this);
        iv_connection = (ImageView) findViewById(R.id.iv_connection);
        //点击扫描按钮
        iv_connection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (scan == 0 && rfid == 1) {//扫描rfid
                    int i = -1;
                    try {
                        i = Operation.StartInventory();
                    } catch (radioBusyException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    if (i == 1) {
                        tagInfoList.clear();
                        showInfoList.clear();
                        tagCount = 0;
                        uploadCount = 0;

                        Message closemsg = new Message();
                        closemsg.obj = 8;
                        closemsg.what = MSG_TOAST;
                        hMsg.sendMessage(closemsg);

                    } else {
                        return;
                    }
                    scan = 1;
                    iv_connection.setImageResource(R.drawable.a_scan_c);
                } else if (scan == 0 && ewm == 1) {//扫描二维码
                    Intent intent = new Intent(HomeActivity.this, ScanActivity.class);
                    intent.putExtra("Scan", (Serializable) setting);
                    startActivity(intent);
                } else if (scan == 1) {//停止扫描
                    int i = Operation.StopInventory();
                    if (i == 1) {
                        Message closemsg = new Message();
                        closemsg.obj = 7;
                        closemsg.what = MSG_TOAST;
                        hMsg.sendMessage(closemsg);
                    } else {
                        return;
                    }
                    scan = 0;
                    iv_connection.setImageResource(R.drawable.a_scan_b);
                    //上传数据
                    upData(epc);
                } else {
                    Toast.makeText(HomeActivity.this, "请选择扫描模式", Toast.LENGTH_SHORT).show();
                }
            }
        });
        spinner_node = (Spinner) findViewById(R.id.spinner_node);
        setNodeToSpinnerNode(setting);

        iv_ewm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ewm == 0) {
                    iv_ewm.setImageResource(R.drawable.a_ewmscan_b);
                    ewm = 1;
                } else {
                    iv_ewm.setImageResource(R.drawable.a_ewmscan);
                    ewm = 0;
                }
            }
        });

        iv_rfid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rfid == 0) {
                    iv_rfid.setImageResource(R.drawable.a_rfid_b);
                    rfid = 1;
                } else {
                    iv_rfid.setImageResource(R.drawable.a_rfid);
                    rfid = 0;
                }
            }
        });

        showadapter = new ShowAdapter(this, showInfoList);
        list_view.setAdapter(showadapter);
        //监听盘点
        Operation.myRadio.setInventoryEventListener(new OnInventoryEventListener() {
            @Override
            public void RadioInventory(InventoryEvent event) {
                Message msg = new Message();
                msg.obj = event;
                msg.what = MSG_SHOW_EPC_INFO;
                hMsg.sendMessage(msg);
            }
        });
    }

    //上传数据
    public void upData(final String epc){
        new Thread() {
            public void run() {
                if (!isStopInventory()) {//如果停止扫描
                    Message closemsg = new Message();
                    closemsg.obj = 9;
                    closemsg.what = MSG_TOAST;
                    hMsg.sendMessage(closemsg);
                    return;
                } else if (!AgentOperation.NetworkIsConnected(getApplicationContext())) {//如果没有网络
                    Message closemsg = new Message();
                    closemsg.obj = 1;
                    closemsg.what = MSG_TOAST;
                    hMsg.sendMessage(closemsg);
                    return;
                } else {
                    try {
                        Bgent.setConfig("https://180.167.66.99:36753/agent");
                        for (int i = 0; i < showInfoList.size(); i++) {
                            Bgent.Result rlt1 = new Bgent.Result();
                            rlt1.Success = false;
                            Bgent.Result rlt2 = new Bgent.Result();
                            rlt2.Success = false;
                            Info info = new Info();
                            rlt1 = Bgent.queryExists(epc, "ZJfd-12345");//参数：url,dataCode
                            if (rlt1.Success == true && (boolean) rlt1.Data == true) {
                                rlt2 = Bgent.saveTracking("ZJfd-12345", "86.1020.203/AAAA551F0C4446BE810D6E1BC1A1176C", "足迹", Bgent.SaveMode.LIST, AgentOperation.convertUploadInfo(showInfoList.get(i), setting).getData());//参数：dataCode,fullCode,fieldName,saveMode,data
                                Log.i("json",AgentOperation.convertUploadInfo(showInfoList.get(i), setting).getData());
                            } else if (rlt1.Success == true && (boolean) rlt1.Data == false) {
                                if (rlt1.Message != null) {
                                    info.setErrorInfo("查询此码成功," + rlt1.Message);//将错误信息保存到实体类
                                } else {
                                    info.setErrorInfo("查询此码成功，此码不存在");
                                }
                                continue;
                            } else {
                                if (rlt1.Message != null) {
                                    info.setErrorInfo("查询此码失败" + rlt1.Message);//将错误信息保存到实体类
                                } else {
                                    info.setErrorInfo("查询此码失败");
                                }
                                continue;
                            }

                            if (rlt2.Success == true) {//上传成功
                                System.out.println("成功");
                                Toast.makeText(HomeActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
                            } else if (rlt2.Success == false) {//上传失败
                                System.out.println("失败");
                                Toast.makeText(HomeActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (Exception e) {
                        Log.e("异常：",e.getMessage());
//                                    Message closemsg = new Message();
//                                    closemsg.obj = "网络或服务器异常";
//                                    closemsg.what = MSG_TOAST_ERROR;
//                                    hMsg.sendMessage(closemsg);
                    }
                }
            }
        }.start();
    }

    public void setNodeToSpinnerNode(Setting newset) {
        List<String> list = new ArrayList<String>();
        String str = newset.getNode();
        String[] array = str.split("\\|");
        for (int i = 0; i < array.length; i++) {
            list.add(array[i]);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, R.id.tv_item, list);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        spinner_node.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_scan:
                if (btn_scan.getText().toString().equals("开始扫描")) {
                    int i = -1;
                    try {
                        i = Operation.StartInventory();
                    } catch (radioBusyException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    if (i == 1) {
                        tagInfoList.clear();
                        showInfoList.clear();
                        tagCount = 0;
                        uploadCount = 0;

                        Message closemsg = new Message();
                        closemsg.obj = 8;
                        closemsg.what = MSG_TOAST;
                        hMsg.sendMessage(closemsg);

                    } else {
                        return;
                    }
                } else if (btn_scan.getText().toString().equals("停止扫描")) {

                    int i = Operation.StopInventory();
                    if (i == 1) {
                        Message closemsg = new Message();
                        closemsg.obj = 7;
                        closemsg.what = MSG_TOAST;
                        hMsg.sendMessage(closemsg);

                    } else {
                        return;
                    }
                }
                break;
        }
    }

    private Handler hMsg = new StartHander(this);

    //用于集中处理显示等事件信息的静态类
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

                case MSG_SHOW_EPC_INFO:
                    InventoryEvent info = (InventoryEvent) msg.obj;
                    ShowEPC(setting, info.GetFlagID());
                    break;


                case MSG_TOAST:
                    int returnValue1 = (Integer) msg.obj;
                    switch (returnValue1) {

                        case 1:
                            Toast.makeText(activity, "网络无法连接", Toast.LENGTH_SHORT).show();
                            break;

                        case 7:
                            btn_scan.setText("开始扫描");
                            btn_scan.setBackgroundColor(android.graphics.Color.parseColor("#FF7A3F"));
                            break;

                        case 8:
                            tv_uploadCount.setText(String.format("%d", uploadCount));
                            tv_readCount.setText(String.format("%d", tagCount));
                            showadapter.notifyDataSetChanged();
                            btn_scan.setText("停止扫描");
                            btn_scan.setBackgroundColor(android.graphics.Color.parseColor("#FF7A3F"));
                            break;

                        case 9:
                            Toast.makeText(activity, R.string.back_error_info_forNotStopInv, Toast.LENGTH_SHORT).show();
                            break;

                        case 10:
                            AllClear();
                            HomeActivity.this.finish();
                            break;
                    }

                    break;

                case MSG_TOAST_ERROR:

                    String returnValue2 = (String) msg.obj;

                    Toast.makeText(getApplicationContext(), returnValue2, Toast.LENGTH_SHORT).show();

                    break;


                case MSG_TOAST_SUCCESS:

                    int returnValue3 = (Integer) msg.obj;

                    tv_uploadCount.setText(String.format("%d", returnValue3));

                    break;
            }
        }
    }

    public static void ShowEPC(Setting setting, String flagID) {
//        String epc = setting.getDBIP() + "\n" + setting.getExternalCode() + "\n" + BaseDao.exChange(flagID);
//        epc = "http://180.167.66.99:36751/ \n" + "86.1020.203/ \n" + BaseDao.exChange(flagID);
        epc = "http://180.167.66.99:36761/86.1020.203/AAAA551F0C4446BE810D6E1BC1A1176C";
        if (!tagInfoList.contains(epc)) {
            tagCount++;
            tagInfoList.add(epc);
            addShowInfoToList(epc);
            showadapter.notifyDataSetChanged();
            try {
                tv_readCount.setText(String.format("%d", tagCount));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public static void addShowInfoToList(String epc) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        String time = df.format(new Date());

        ShowInfo showinfo = new ShowInfo();
        showinfo.setEpc(epc);
        showinfo.setTime(time);
        showinfo.setUploadFlag(false);
        showInfoList.add(showinfo);
    }

    public void AllClear() {
        tv_uploadCount.setText(String.format("%d", uploadCount));
        tv_readCount.setText(String.format("%d", tagCount));
        showadapter.notifyDataSetChanged();
    }

    //连接RFID模块，返回结果
    public void connectRadio() {
        new Thread() {
            public void run() {
                Message closemsg = new Message();
                try {
                    Operation.setAntennaPower(15);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                closemsg.obj = Operation.connectRadio();
                closemsg.what = MSG_CONNECT;
                hMsg2.sendMessage(closemsg);
            }
        }.start();
    }

    private Handler hMsg2 = new StartHander2(this);

    private class StartHander2 extends Handler {
        WeakReference<Activity> mActivityRef;

        StartHander2(Activity activity) {
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
                            Toast.makeText(HomeActivity.this, "rfid已连接", Toast.LENGTH_SHORT).show();
                            break;

                        case 1:
                            Toast.makeText(HomeActivity.this, "失败", Toast.LENGTH_SHORT).show();
                            break;

                        case -1:
                            Toast.makeText(HomeActivity.this, "失败：忙中", Toast.LENGTH_SHORT).show();
                            break;
                    }

                    break;


                case MSG_CONNECT:
                    int returnValue1 = (Integer) msg.obj;
                    switch (returnValue1) {
                        case 0:
                            iv_connection.setImageDrawable(getResources().getDrawable(R.drawable.a_scan_b));
                            break;

                        case -1:
                            Toast.makeText(HomeActivity.this, "失败", Toast.LENGTH_SHORT).show();
                            break;

                        case -2:
                            Toast.makeText(HomeActivity.this, "失败：忙中", Toast.LENGTH_SHORT).show();

                            break;

                        case 2:
                            Toast.makeText(HomeActivity.this, "失败：设置天线", Toast.LENGTH_SHORT).show();
                            break;
                    }

                    break;

                case MSG_OPERATION_SUCCESS:
                    String returnValue2 = (String) msg.obj;
                    break;

                default:

                    break;

            }
        }
    }

    ;

    //连接RFID模块，返回结果
    public void disconnectRadio() {
        new Thread() {

            public void run() {

                Message closemsg = new Message();
                closemsg.obj = Operation.DisconnectRadio();
                closemsg.what = MSG_DISCONNECT;
                hMsg2.sendMessage(closemsg);
            }

            ;

        }.start();

    }

    //点击back键
    @Override
    public void onBackPressed() {
        //点击弹出对话框
        new AlertDialog.Builder(this).setTitle("温馨提示").setMessage("是否要退出物资通应用？")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //断开RFID模块
                        disconnectRadio();
                        finish();
                    }
                })
                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            Message closemsg = new Message();
            if (isStopInventory()) {
                tagInfoList.clear();
                showInfoList.clear();
                tagCount = 0;
                uploadCount = 0;

                closemsg.obj = 10;
                closemsg.what = MSG_TOAST;
                hMsg.sendMessage(closemsg);
            } else {

                closemsg.obj = 9;
                closemsg.what = MSG_TOAST;
                hMsg.sendMessage(closemsg);
            }
            return false;
        }

        return super.onKeyDown(keyCode, event);
    }

    //判断是否停止盘点
    public boolean isStopInventory() {

        if (scan == 0) {
            return true;
        } else{
            return false;
        }

    }

    //点击设置按钮
    public void setting(View view) {
        Intent intent = new Intent(this, SettingActivity.class);
        intent.putExtra("Setting", (Serializable) setting);

        requestCode = 0;
        startActivityForResult(intent, requestCode);
    }

    public static Setting Oldsetting = new Setting();
    int disconnectRadioFlag;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub

        switch (requestCode) {
            case 0:
                Serializable setdata = data.getSerializableExtra("Setting");
                setting = (Setting) setdata;
                if (Integer.valueOf(Oldsetting.getPower()) != Integer.valueOf(setting.getPower())) {
                    disconnect();

                    Toast.makeText(getApplicationContext(), "重新设置功率中。。。", Toast.LENGTH_SHORT).show();

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    connectRadio();

                    Oldsetting = setting;
                }

                break;

            default:
                break;

        }

        super.onActivityResult(requestCode, resultCode, data);

    }

    public void disconnect() {
        new Thread() {

            public void run() {
                disconnectRadioFlag = Operation.DisconnectRadio();
//    	    	connectRadioFlag =uhfreader.DisconnectRadio();
                Message closemsg = new Message();
                closemsg.obj = disconnectRadioFlag;
                closemsg.what = MSG_DISCONNECT;
                hMsg.sendMessage(closemsg);
            }

        }.start();
    }

    public Setting getSettingData() {

        DBOperation dboperation = new DBOperation(getApplicationContext());
        Setting setting = dboperation.querySet();

        if (setting.getID() == null || setting.getCompany() == null || setting.getNode() == null
                || setting.getWorkName() == null || setting.getUrl() == null || setting.getTelephone() == null || setting.getLat_Lon() == null || setting.getNewData() == null
                || setting.getPower() == null || setting.getLogDeadline() == null || setting.getCCTime() == null || setting.getScanStrategy() == null || setting.getAgainDeadline() == null
                || setting.getVesion() == null || setting.getScanInterval() == null) {
            Setting newset = new Setting();
            newset.setCompany("高斯中国");
            newset.setNode("入库|出库|销售|售后");
            newset.setWorkName("E2043");
            newset.setUrl("www.gaosi.com");
            newset.setTelephone("13887654921");
            newset.setLat_Lon("130.24|31");
            newset.setNewData("备注信息");

            newset.setPower("30");
            newset.setLogDeadline("30");
            newset.setCCTime("不限制");
            newset.setScanStrategy("在线扫描");
            newset.setAgainDeadline("2");
            newset.setVesion("专业终端V.1708cx");
            newset.setScanInterval("3");
            dboperation.insertSet(newset);

            setting = dboperation.querySet();
        }

        return setting;
    }

    public void bgentSetConfig(){

    }

//    //点击提交
//    public void upload(View view) {
//        new Thread() {
//
//            public void run() {
//                if (!isStopInventory()) {
//                    Message closemsg = new Message();
//                    closemsg.obj = 9;
//                    closemsg.what = MSG_TOAST;
//                    hMsg.sendMessage(closemsg);
//                    return;
//                } else if (!AgentOperation.NetworkIsConnected(getApplicationContext())) {
//                    Message closemsg = new Message();
//                    closemsg.obj = 1;
//                    closemsg.what = MSG_TOAST;
//                    hMsg.sendMessage(closemsg);
//                    return;
//                } else {
//                    try {
//                        AgentOperation.setConfig(showInfoList,setting);
//                        for (int i = 0; i < showInfoList.size(); i++) {
//                            if (!showInfoList.get(i).getUploadFlag()) {
//                                UploadInfo info = AgentOperation.convertUploadInfo(showInfoList.get(i), setting);
//                                Bgent.Result result = Bgent.queryExists(epc, "ZJfd-12345");//全码、数据码
//                                if (result.Success == true) {
//                                    uploadCount++;
//                                    showInfoList.get(i).setUploadFlag(true);
//                                    Message closemsg = new Message();
//                                    closemsg.obj = uploadCount;
//                                    closemsg.what = MSG_TOAST_SUCCESS;
//                                    hMsg.sendMessage(closemsg);
//                                } else {
//                                    if (result.Message != null) {
//                                        Message closemsg = new Message();
//                                        closemsg.obj = result.Message.toString();
//                                        closemsg.what = MSG_TOAST_ERROR;
//                                        hMsg.sendMessage(closemsg);
//                                    } else {
//                                        Message closemsg = new Message();
//                                        closemsg.obj = "上传全码失败";
//                                        closemsg.what = MSG_TOAST_ERROR;
//                                        hMsg.sendMessage(closemsg);
//                                    }
//                                }
//                            }
//                        }
//                    } catch (Exception e) {
//                        Message closemsg = new Message();
//                        closemsg.obj = "网络或服务器异常";
//                        closemsg.what = MSG_TOAST_ERROR;
//                        hMsg.sendMessage(closemsg);
//                    }
//                }
//            }
//        }.start();
//    }
}
