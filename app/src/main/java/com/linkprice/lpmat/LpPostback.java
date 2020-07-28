package com.linkprice.lpmat;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * ://service.linkprice.com/lppurchase_cps_v4.php
 */
public class LpPostback {

    private final LpNetwork mLpNetwork;
    private LpResponse mLpResponse = new LpResponse();
    private JSONArray mItems = new JSONArray();         // v2 and v4 can use mItems
    private JSONObject orderInfo = new JSONObject();    // only for V4
    private JSONObject linkprice = new JSONObject();    // only for V4

    public LpPostback(Context context) {
        mLpNetwork = new LpNetwork(context);
    }


    // set the postback Order info for v4
    public void setOrder(Map<String, Object> order) {
        for(String key : order.keySet()){
            try {
                orderInfo.put(key, order.get(key));
            } catch (JSONException e) {
//                e.printStackTrace();
            }
        }

    }

    // set the postback linkprice object info for v4
    public void setLinkprice(Map<String, Object> lp) {
        for(String key : lp.keySet()){
            try {
                linkprice.put(key, lp.get(key));
            } catch (JSONException e) {
//                e.printStackTrace();
            }
        }

        try {
            linkprice.put("device_type", "app_android");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    // postback imem add
    public Boolean addItem(Map<String, Object> item) {
        JSONObject jsonObject = new JSONObject();

        for (String key : item.keySet()) {
            try {
                jsonObject.put(key, item.get(key));
            } catch (JSONException e) {
//                e.printStackTrace();
                return false;
            }
        }

        mItems.put(jsonObject);
        return true;
    }


    // postback v4
    public Boolean send(LpResponse lpResponse) {
        if (null != lpResponse) {
            mLpResponse = lpResponse;
        }

        JSONObject info = new JSONObject();

        if(orderInfo.has("order_id") && mItems.length() != 0) {
            try {
                info.put("order", orderInfo);
                info.put("products", mItems);
                info.put("linkprice",linkprice);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            try {
                info.put("action", orderInfo);
                info.put("linkprice",linkprice);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        return purchase(info);
    }

    // postback V4 corresponse
    public Boolean purchase(JSONObject purchase) {
        if (mLpNetwork.isOnline()) {

            return mLpNetwork.call(purchase, mLpResponse);
        }  else {

            mLpResponse.fail("please network connect check");
            return false;
        }
    }

    public String check() {
        JSONObject info = new JSONObject();

        try {
            info.put("order", orderInfo);
            info.put("products", mItems);
            info.put("linkprice",linkprice);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return info.toString();
    }
}