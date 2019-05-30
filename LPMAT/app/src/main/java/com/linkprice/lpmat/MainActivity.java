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

        Map<String, String> params = new HashMap<>();
        params.put("m_id", "clickbuy"); // merchant id
        params.put("orderCode", "orderCode3");  // order code
        params.put("memberID", "userInfo");    // user Info
        params.put("currency", "KRW");
        params.put("remoteAddress", "127.0.0.1"); // user ip

        lpMobileAT.setParams(params);

        Map<String, String> item = new HashMap<>();
        item.put("productCode", "productCode");    // product code
        item.put("qty", "111"); // order count
        item.put("sales", "111"); // total amount
        item.put("category", "web"); // web or mobile
        item.put("product", "productName"); // product name

        lpMobileAT.addItem(item);

        item.clear();

        item = new HashMap<>();
        item.put("productCode", "productCode1");    // product code
        item.put("qty", "222"); // order count
        item.put("sales", "222"); // total amount
        item.put("category", "web"); // web or mobile
        item.put("product", "productName"); // product name

        lpMobileAT.addItem(item);

        lpMobileAT.send(new LpResponse() {

            @Override
            public void fail(String result) {
                super.fail(result);

                Toast.makeText(mContext, "postback : fail - " + result, Toast.LENGTH_SHORT).show();
                Log.d(LpMobileAT.LOG_TAG, "postback : fail - " + result);
            }

            @Override
            public void success() {
                super.success();

                Toast.makeText(mContext, "postback : success", Toast.LENGTH_SHORT).show();
                Log.d(LpMobileAT.LOG_TAG, "postback : success");
            }
        });
    }
}
