package com.dao;

import android.content.Context;
import android.net.ConnectivityManager;
import android.text.TextUtils;


import com.app.Info;
import com.sdesrd.tracking.bgentlib.Bgent;
import com.zjfd.wanpeng.newcouchbase.UI.HomeActivity;

import java.util.ArrayList;
import java.util.List;


public class AgentOperation {


    public static void setConfig(List<ShowInfo> showInfoList, Setting newset) {
//        String ProdutcUrl = newset.getProductUrl();
//        String FieldUrl = newset.getFieldUrl();
//        String SaveUrl = newset.getSaveUrl();暂时注释
//        Bgent.Result rlt1 = new Bgent.Result();
//        rlt1.Success = false;
//        Bgent.Result rlt2 = new Bgent.Result();
//        rlt2.Success = false;
//        Info info = new Info();
//        rlt1 = Bgent.queryExists("", "ZJfd-12345");//参数：url(全码),dataCode
//        if (rlt1.Success == true && (boolean) rlt1.Data == true) {
//            rlt2 = Bgent.saveTracking("", "", "足迹", Bgent.SaveMode.LIST, convertUploadInfo(showInfoList.get(i), newset));//参数：dataCode,fullCode,fieldName,saveMode,data
//        } else if (rlt1.Success == true && (boolean) rlt1.Data == false) {
//            String sql2;
//            if (rlt1.Message != null) {
//                info.setErrorInfo("查询此码成功," + rlt1.Message);//蒋错误信息保存到实体类
//            } else {
//                info.setErrorInfo("查询此码成功，此码不存在");
//            }
//            continue;
//        } else {
//            if (rlt1.Message != null) {
//                info.setErrorInfo("查询此码失败" + rlt1.Message);//蒋错误信息保存到实体类
//            } else {
//                info.setErrorInfo("查询此码失败");
//            }
//            continue;
//        }
//
//        if (rlt2.Success == true) {//上传成功
////            rlt2 = Bgent.saveTracking("", "", "", Bgent.SaveMode.LIST, "");//参数：dataCode,fullCode,fieldName,saveMode,data
//
//        } else if (rlt2.Success == false) {//上传失败
//
//        }
    }


    public static UploadInfo convertUploadInfo(ShowInfo showinfo, Setting set) {
        UploadInfo uploadinfo = new UploadInfo();

        uploadinfo.setProductName("滚轴");
        uploadinfo.setNode(set.getNode());
        uploadinfo.setWorkName(set.getWorkName());
        uploadinfo.setLat("130.24");
        uploadinfo.setLon("31");
        uploadinfo.setFieldName("足迹");
        uploadinfo.setDBIP("http://180.167.66.99");
        uploadinfo.setPort("36751");
        uploadinfo.setExternalCode("86.1020.301");
        uploadinfo.setCompany(set.getCompany());
        uploadinfo.setFullCode("86.1020.301" + "/" + showinfo.getEpc());
        uploadinfo.setTime(showinfo.getTime());
        uploadinfo.setData("" + "{\"节点\":\"" + HomeActivity.homeActivity.spinner_node.getSelectedItem().toString() + "\",\"经办人\":\"" + uploadinfo.getWorkName() + "\",\"时间\":\"" + uploadinfo.getTime() + "\",\"Lon\":\"" + uploadinfo.getLon() + "\",\"Lat\":\"" + uploadinfo.getLat() + "\",\"公司\":\"" + uploadinfo.getCompany() +
                "\",\"备注\":\"" + set.getNewData() + "\"}");

        return uploadinfo;
    }


    public static boolean NetworkIsConnected(Context context) {

        ConnectivityManager con = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean wifi = con.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
        boolean internet = con.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
        if (wifi | internet) {
            return true;
        } else {
            return false;
        }

    }


}
