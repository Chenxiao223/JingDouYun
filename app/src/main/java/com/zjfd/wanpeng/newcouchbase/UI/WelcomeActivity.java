package com.zjfd.wanpeng.newcouchbase.UI;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.zjfd.wanpeng.newcouchbase.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2017/5/18.
 */
public class WelcomeActivity extends Activity {
    //    private static final long ANIM_TIME = 3000;
//    private ImageView mIVEntry;
//    @Override
//    protected int getLayID() {
//        return R.layout.activity_welcome;
//    }
//
//    @Override
//    protected void initViews(Bundle savedInstanceState) {
//        initView();
//        startAnim();
//    }
//
//    private void initView() {
//        mIVEntry= (ImageView) findViewById(R.id.mIVEntry);
//    }
//
//    private void startAnim() {
//
//        ObjectAnimator animatorX = ObjectAnimator.ofFloat(mIVEntry, "scaleX", 0f, 1f);
//        ObjectAnimator animatorY = ObjectAnimator.ofFloat(mIVEntry, "scaleY", 0f, 1f);
//
//        AnimatorSet set = new AnimatorSet();
//        set.setDuration(ANIM_TIME).play(animatorX).with(animatorY);
//        set.start();
//
//        set.addListener(new AnimatorListenerAdapter() {
//
//            @Override
//            public void onAnimationEnd(Animator animation) {
//
//                startActivity(new Intent(WelcomeActivity.this, StartActivity.class));
//                WelcomeActivity.this.finish();
//            }
//        });
//    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        //判断网络连接，如果没有网络就报语音
        if (!checkNetworkInfo()) {
            Toast.makeText(WelcomeActivity.this, "网络连接失败，请连接网络", Toast.LENGTH_SHORT).show();
        }
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                startActivity(new Intent(WelcomeActivity.this, HomeActivity.class));
                finish();
            }
        };
        timer.schedule(timerTask, 3000);
    }

    public boolean checkNetworkInfo() {
        ConnectivityManager conMan = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo.State mobile = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
        NetworkInfo.State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        if (mobile == NetworkInfo.State.CONNECTED || mobile == NetworkInfo.State.CONNECTING)
            return true;
        if (wifi == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTING)
            return true;
        return false;
    }
}
