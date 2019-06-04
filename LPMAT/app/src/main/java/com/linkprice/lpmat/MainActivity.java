package com.linkprice.lpmat;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        // set tag_value at activity
        LpMobileAT lpMobileAT = new LpMobileAT(this, getIntent());
        lpMobileAT.setTagValueActivity();

        // send previous failed sending data
        Log.d(LpMobileAT.LOG_TAG, "failed data - " + lpMobileAT.checking());
        lpMobileAT.reSendSales();
	}

    // view tag value
	public void referrerView(View v) {
        LpMobileAT lpMobileAT = new LpMobileAT(v.getContext(), getIntent());

        String referrer = lpMobileAT.getReferrer();
        String tagValue = lpMobileAT.getTagValue();
        
        Log.d(LpMobileAT.LOG_TAG, "referrer - " + referrer);
        Log.d(LpMobileAT.LOG_TAG, "TAG Value - " + tagValue);
        
        TextView txtView2 = (TextView)findViewById(R.id.textView2);
        txtView2.setText(referrer);
        
        TextView txtView4 = (TextView)findViewById(R.id.textView4);
        txtView4.setText(tagValue);
    }

    // postback call
    public void purchase(View v) {
        final Context mContext = v.getContext();

        LpMobileAT lpMobileAT = new LpMobileAT(mContext, getIntent());

        Map<String, Object> order = new HashMap<>();
        order.put("order_id", "o111232-323234"); // merchant id
        order.put("final_paid_price", 59000);  // order code
        order.put("currency", "KRW");    // user Info
        order.put("user_name", "tester");

        Map<String, Object> lp = new HashMap<>();
        lp.put("user_agent", "blablabla");  // order code
        lp.put("remote_addr", "127.0.0.1");    // user Info
        lp.put("merchant_id", "clickbuy");    // user Info

        lpMobileAT.setOrder(order, lp);

        Map<String, Object> item = new HashMap<>();
        item.put("product_id", "productCode");
        item.put("product_name", "sample"); //
        item.put("category_code", "111");
        item.put("category_name", "web"); //
        item.put("quantity", "ddd");
        item.put("product_final_price", 59000);
        item.put("paid_at", "productName");
        item.put("confirmed_at", "productName");
        item.put("canceled_at", "productName");

        lpMobileAT.addItem(item);

        lpMobileAT.send(null);

//        lpMobileAT.send(new LpResponse() {
//
//            @Override
//            public void fail(String result) {
//                super.fail(result);
//
//                Toast.makeText(mContext, "postback : fail - " + result, Toast.LENGTH_SHORT).show();
//                Log.d(LpMobileAT.LOG_TAG, "postback : fail - " + result);
//            }
//
//            @Override
//            public void success() {
//                super.success();
//
//                Toast.makeText(mContext, "postback : success", Toast.LENGTH_SHORT).show();
//                Log.d(LpMobileAT.LOG_TAG, "postback : success");
//            }
//        });
    }
}
