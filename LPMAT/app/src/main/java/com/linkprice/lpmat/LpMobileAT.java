package com.linkprice.lpmat;

import android.content.Context;
import android.content.Intent;

import java.util.Map;

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
    public void setParams(Map<String, String> params) {
        if (!params.containsKey("a_id")) {
            params.put("a_id", getTagValue());
        }
        mLpPostback.setParams(params);
    }

    // postback imem add
    public Boolean addItem(Map<String, String> item) {
        return mLpPostback.addItem(item);
    }

    // postback call
    public void send() { send(null); }
    public Boolean send(LpResponse lpResponse) {

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
}
