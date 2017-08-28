package com.zjfd.wanpeng.newcouchbase.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.app.Constants;
import com.baidu.platform.comapi.map.K;
import com.zjfd.wanpeng.newcouchbase.Entity.CouchbaseBean;
import com.zjfd.wanpeng.newcouchbase.Entity.testBean;
import com.zjfd.wanpeng.newcouchbase.Fragment.Fragment1;
import com.zjfd.wanpeng.newcouchbase.Fragment.Fragment2;
import com.zjfd.wanpeng.newcouchbase.Fragment.Fragment3;
import com.zjfd.wanpeng.newcouchbase.Fragment.Fragment4;
import com.zjfd.wanpeng.newcouchbase.R;

import java.io.Serializable;

public class MainActivity extends FragmentActivity implements RadioGroup.OnCheckedChangeListener {
    private RadioGroup navigationBar;
    private RadioButton btn1, btn2, btn3, btn4;
    private Fragment fragment1, fragment2, fragment3, fragment4;

    private Fragment mFragment;//当前显示的Fragment
//    private testBean bean;
private CouchbaseBean mbean;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.main_fragment, fragment1).commit();
        mFragment = fragment1;
//        Intent intent=new Intent();

//        Couch  baseBean bean = (CouchbaseBean) intent.getSerializableExtra(Constants.MEAN);



    }

    private void initViews() {
        Bundle bundle = getIntent().getExtras();
        mbean= (CouchbaseBean) bundle.getSerializable(Constants.MEAN);
//        bean = (testBean) bundle.getSerializable(Constants.MEAN);
        String gsName = mbean.getData().getObject().get(0).getGsName();
//        String gsName = bean.data.object.get(0).GsName;
        Log.d("值===MainActivity",gsName);
        navigationBar = (RadioGroup) findViewById(R.id.navigation_btn);
        btn1 = (RadioButton) findViewById(R.id.btn1);
        btn2 = (RadioButton) findViewById(R.id.btn2);
        btn3 = (RadioButton) findViewById(R.id.btn3);
        btn4 = (RadioButton) findViewById(R.id.btn4);
        navigationBar.setOnCheckedChangeListener(this);

        fragment1 = new Fragment1();
        fragment2 = new Fragment2();
        fragment3 = new Fragment3();
        fragment4 = new Fragment4();
//        Bundle  bundle=new Bundle();
//        bundle.pu
        fragment1.setArguments(bundle);

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.btn1:
                btn1.setChecked(true);
                btn2.setChecked(false);
                btn3.setChecked(false);
                btn4.setChecked(false);
                switchFragment(fragment1);
                break;
            case R.id.btn2:
                btn1.setChecked(false);
                btn2.setChecked(true);
                btn3.setChecked(false);
                btn4.setChecked(false);
                switchFragment(fragment2);

                break;
            case R.id.btn3:
                btn1.setChecked(false);
                btn2.setChecked(false);
                btn3.setChecked(true);
                btn4.setChecked(false);
                switchFragment(fragment3);

                break;
            case R.id.btn4:
                btn1.setChecked(false);
                btn2.setChecked(false);
                btn3.setChecked(false);
                btn4.setChecked(true);
                switchFragment(fragment4);

                break;
        }
    }

    private void switchFragment(Fragment fragment) {
        //判断当前显示的Fragment是不是切换的Fragment
        if (mFragment != fragment) {
            //判断切换的Fragment是否已经添加过
            if (!fragment.isAdded()) {
                //如果没有，则先把当前的Fragment隐藏，把切换的Fragment添加上
                getSupportFragmentManager().beginTransaction().hide(mFragment)
                        .add(R.id.main_fragment, fragment).commit();
            } else {
                //如果已经添加过，则先把当前的Fragment隐藏，把切换的Fragment显示出来
                getSupportFragmentManager().beginTransaction().hide(mFragment).show(fragment).commit();
            }
            mFragment = fragment;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if (fragment1 == null && fragment instanceof Fragment1){
            fragment1 = fragment;
        } else if (fragment2 == null && fragment instanceof Fragment2){
            fragment2 = fragment;
        }else if (fragment3 == null && fragment instanceof Fragment3){
            fragment3 = fragment;
        }else if (fragment4 == null && fragment instanceof Fragment4){
            fragment4 = fragment;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}
