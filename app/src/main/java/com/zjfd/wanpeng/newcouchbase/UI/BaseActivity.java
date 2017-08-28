package com.zjfd.wanpeng.newcouchbase.UI;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.zjfd.wanpeng.newcouchbase.R;

/**
 * Created by Administrator on 2017/4/20.
 */
public abstract class BaseActivity extends AppCompatActivity {

    private LoadingAlertDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int layID = getLayID();
        if (layID != 0) {
            setContentView(layID);
            initViews(savedInstanceState);
        }
    }

    protected abstract int getLayID();

    protected abstract void initViews(Bundle savedInstanceState);


    public void showLoading() {
        dialog = new LoadingAlertDialog(this);
        dialog.show("加载中...");
    }

    public void showLoadinglose() {
        dialog = new LoadingAlertDialog(this);
        dialog.show("加载失败");
    }

    public void disssLoading() {
        if (dialog != null) {
            dialog.dismiss();
        }

    }

}
