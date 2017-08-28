package com.zjfd.wanpeng.newcouchbase.UI;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dao.Setting;
import com.zjfd.wanpeng.newcouchbase.R;

//import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity;
import java.util.Timer;
import java.util.TimerTask;

import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.QRCodeDecoder;
import cn.bingoogolapple.qrcode.zxing.ZXingView;

/**
 * Created by Administrator on 2017/4/19.
 */
public class ScanActivity extends AppCompatActivity implements QRCodeView.Delegate, View.OnClickListener {
    Button start_spot, open_flashlight, close_flashlight;
    private static final String TAG = "ScanActivity";
    Timer timer = new Timer();
    public int recLen;
    private boolean star_stop=true;
    private ImageView iv_back;
    Setting setting;
    public String epc;
//    private static final int REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY = 666;

    private QRCodeView mQRCodeView;
    private TextView tv_value, tv_jishi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_scan);
        setting = (Setting) this.getIntent().getSerializableExtra("Scan");

        recLen=Integer.parseInt(setting.getScanInterval());
        initView();
        mQRCodeView = (ZXingView) findViewById(R.id.zxingview);
        mQRCodeView.setDelegate(this);
        mQRCodeView.startSpot();

        HomeActivity.showInfoList.clear();//进来的时候清空主页数据

    }

    private void initView() {
        iv_back= (ImageView) findViewById(R.id.iv_back);
        tv_jishi = (TextView) findViewById(R.id.tv_jishi);
        tv_value = (TextView) findViewById(R.id.tv_value);
        start_spot = (Button) findViewById(R.id.start_spot);
        open_flashlight = (Button) findViewById(R.id.open_flashlight);
        close_flashlight = (Button) findViewById(R.id.close_flashlight);
        open_flashlight.setOnClickListener(this);
        start_spot.setOnClickListener(this);
        close_flashlight.setOnClickListener(this);
        iv_back.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mQRCodeView.startCamera();
//        mQRCodeView.startCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);

        mQRCodeView.showScanRect();
    }

    @Override
    protected void onStop() {
        mQRCodeView.stopCamera();
        super.onStop();
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        epc=result;
        HomeActivity.addShowInfoToList(result);//往HomeActivity添加数据
//        Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
        recLen = Integer.parseInt(setting.getScanInterval());
        tv_jishi.setVisibility(View.VISIBLE);
        tv_value.setVisibility(View.VISIBLE);
        tv_value.setText(result);
        vibrate();
        mQRCodeView.stopSpot();//关闭扫描
        Message message = handler.obtainMessage(1);     // Message
        handler.sendMessageDelayed(message, 1000);
    }

    final Handler handler = new Handler() {

        public void handleMessage(Message msg) {         // handle message
            switch (msg.what) {
                case 1:
                    recLen--;
                    tv_jishi.setText("扫描倒计时：" + recLen);

                    if (recLen > 0) {
                        Message message = handler.obtainMessage(1);
                        handler.sendMessageDelayed(message, 1000);      // send message
                    } else {
                        tv_jishi.setVisibility(View.GONE);
                        tv_value.setVisibility(View.GONE);
                        mQRCodeView.startCamera();
                        mQRCodeView.startSpot();
                    }
            }

            super.handleMessage(msg);
        }
    };

    @Override
    public void onScanQRCodeOpenCameraError() {
        Log.e(TAG, "打开相机出错");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_spot:
                if (star_stop){
                    start_spot.setText("开始扫描");
                    mQRCodeView.changeToScanQRCodeStyle();
                    mQRCodeView.stopSpot();
                    star_stop=false;
                }else{
                    start_spot.setText("停止扫描");
                    mQRCodeView.changeToScanQRCodeStyle();
                    mQRCodeView.startSpot();
                    star_stop=true;
                }

                break;
            case R.id.open_flashlight:
                mQRCodeView.openFlashlight();
                break;
            case R.id.close_flashlight:
                mQRCodeView.closeFlashlight();
                break;
            case R.id.iv_back:
                HomeActivity.showadapter.notifyDataSetChanged();
                HomeActivity.tv_readCount.setText("" + HomeActivity.showInfoList.size());
                HomeActivity.homeActivity.upData(epc);//上传数据
                finish();
                break;
        }
    }
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        mQRCodeView.showScanRect();
//
//        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY) {
//            final String picturePath = BGAPhotoPickerActivity.getSelectedImages(data).get(0);
//
//            /*
//            这里为了偷懒，就没有处理匿名 AsyncTask 内部类导致 Activity 泄漏的问题
//            请开发在使用时自行处理匿名内部类导致Activity内存泄漏的问题，处理方式可参考 https://github.com/GeniusVJR/LearningNotes/blob/master/Part1/Android/Android%E5%86%85%E5%AD%98%E6%B3%84%E6%BC%8F%E6%80%BB%E7%BB%93.md
//             */
//            new AsyncTask<Void, Void, String>() {
//                @Override
//                protected String doInBackground(Void... params) {
//                    return QRCodeDecoder.syncDecodeQRCode(picturePath);
//                }
//
//                @Override
//                protected void onPostExecute(String result) {
//                    if (TextUtils.isEmpty(result)) {
//                        Toast.makeText(ScanActivity.this, "未发现二维码", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(ScanActivity.this, result, Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }.execute();
//        }
//    }
}
