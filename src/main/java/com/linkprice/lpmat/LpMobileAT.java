package com.linkprice.lpmat;

import android.content.Context;
import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LpMobileAT {
    public static String LOG_TAG = "LpMobileAT";

    private final LpTagValue mLpTagValue;
    private final LpPostback mLpPostback;

    public LpMobileAT(Context context, Intent intent) {
        mLpTagValue = new LpTagValue(context, intent);
        mLpPostback = new LpPostback(context);
    }

    /*
     * relate tag_value
     */
    // receiver receive tag_value
    public Boolean setTagValueReceiver() {
        return mLpTagValue.setTagValueReceiver();
    }

    // activity receive tag_value
    public Boolean setTagValueActivity() {
        return mLpTagValue.setTagValueActivity();
    }

    public String getReferrer() {
        return mLpTagValue.getReferrer();
    }

    // validate tag_value and get the tag_value
    public String getTagValue(){
        return mLpTagValue.getTagValue();
    }

    // validate tag_value
    public Boolean checkTagValue() {
        return mLpTagValue.checkTagValue();
    }

    /*
     * relate postback
     */
    // set the postback param
//    public void setParams(Map<String, String> params) {
//        if (!params.containsKey("a_id")) {
//            params.put("a_id", getTagValue());
//        }
//        mLpPostback.setParams(params);
//    }

    /*
     * relate postback
     */
    // set the order info
    public void setOrder(Map<String, Object> orderInfo, Map<String, Object> linkprice) {

        mLpPostback.setOrder(orderInfo);
        if (!linkprice.containsKey("lpinfo")) {
            linkprice.put("lpinfo", getTagValue());
        }

        mLpPostback.setLinkprice(linkprice);
    }

    // postback imem add
    public Boolean addItem(Map<String, Object> item) {
        return mLpPostback.addItem(item);
    }

    // postback V4 call
    public void send() { send(null); }
    public boolean send(LpResponse lpResponse){

        // validate tag_value
        if (checkTagValue()) {
            return mLpPostback.send(lpResponse);
        } else {
            if (null != lpResponse) {
                lpResponse.fail("validate tag_value");
            }
            return false;
        }

    }

    // send previous failed sending data and remove data in SharedPreferences
    public void reSendSales() {
        try {
            if(mLpTagValue.getLpFailed() != null) {
                JSONObject info = new JSONObject(mLpTagValue.getLpFailed());
                mLpPostback.purchase(info);
                mLpTagValue.removeLpfailed();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    // get Target_url(deeplink)
    public String getDl() {
        return mLpTagValue.getDl();
    }

    public String getQuery(String url, String key) {
        Map<String, String> query = null;
        URL tempUrl = null;

        try {
            tempUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


        try {
            query = mLpTagValue.getQuery(tempUrl);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        query.get(key);

        return query.get(key);
    }


    // auto CPI
    public void autoCpi(String m_id, String user_agent, String remote_addr, Boolean auto) {
        if(auto) {
            // send install postBack
            Map<String, Object> order = new HashMap<>();
            order.put("unique_id", UUID.randomUUID().toString());
            order.put("final_paid_price", 0);
            order.put("currency", "KRW");
            order.put("member_id", "installer");
            order.put("action_code", "install");
            order.put("action_name", "install");
            order.put("category_code", "install");

            Map<String, Object> lp = new HashMap<>();
            lp.put("merchant_id", m_id); // merchant id
            lp.put("user_agent", user_agent);  // order code
            lp.put("remote_addr", remote_addr);    // user Info


            setOrder(order, lp);
            send(null);
        }

    }
}
