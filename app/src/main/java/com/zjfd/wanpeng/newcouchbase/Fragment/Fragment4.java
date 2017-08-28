package com.zjfd.wanpeng.newcouchbase.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

//import com.couchbase.client.java.Bucket;
//import com.couchbase.client.java.Cluster;
//import com.couchbase.client.java.CouchbaseCluster;
//import com.couchbase.client.java.document.JsonDocument;
//import com.couchbase.client.java.document.json.JsonArray;
//import com.couchbase.client.java.document.json.JsonObject;
//import com.couchbase.client.java.query.N1qlQuery;
//import com.couchbase.client.java.query.N1qlQueryResult;
//import com.couchbase.client.java.query.N1qlQueryRow;
import com.zjfd.wanpeng.newcouchbase.R;

/**
 * Created by Administrator on 2017/4/17.
 */
public class Fragment4 extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment4_layout, null);
        
//        initDate();
        return view;
    }

//    private void initDate() {
//
// new Thread(new Runnable() {
//     @Override
//     public void run() {
//         Cluster cluster = CouchbaseCluster.create("localhost");
//         Bucket bucket = cluster.openBucket("default");
//
//         // Create a JSON Document
//         JsonObject arthur = JsonObject.create()
//                 .put("name", "Arthur")
//                 .put("email", "kingarthur@couchbase.com")
//                 .put("interests", JsonArray.from("Holy Grail", "African Swallows"));
//
//         // Store the Document
//         bucket.upsert(JsonDocument.create("u:king_arthur", arthur));
//
//         // Load the Document and print it
//         // Prints Content and Metadata of the stored Document
//         System.out.println(bucket.get("u:king_arthur"));
//
//         // Create a N1QL Primary Index (but ignore if it exists)
//         bucket.bucketManager().createN1qlPrimaryIndex(true, false);
//
//         // Perform a N1QL Query
//         N1qlQueryResult result = bucket.query(
//                 N1qlQuery.parameterized("SELECT name FROM default WHERE $1 IN interests",
//                         JsonArray.from("African Swallows"))
//         );
//
//         // Print each found Row
//         for (N1qlQueryRow row : result) {
//             // Prints {"name":"Arthur"}
//             System.out.println(row);
//         }
//     }
// }).start();
//
//    }
}
