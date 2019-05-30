package com.linkprice.lpmat;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * https://service.linkprice.com/lppurchase_v2.php?a_id=(tag value)&m_id=(merchant id)&orderCode=(order code)&u_id=(user info)&currency=(USD or KRW)&remoteAddress=(user ip)&items={'list': [{"productCode":(product code), "qty":(order count), "sales":(total amount), "category":(web or mobile), "product":(product name)]}
 */
public class LpPostback {
    private final String mPurchaseUrl = "https://service.linkprice.com/lppurchase_v2.php";
    private final List<String> mRquiredParams = Arrays.asList("m_id", "orderCode", "memberID", "currency", "remoteAddress");

    private final LpNetwork mLpNetwork;
    private LpResponse mLpResponse = new LpResponse();

    private Map<String, String> mParams;
    private JSONArray mItems = new JSONArray();

    public LpPostback(Context context) {
        mLpNetwork = new LpNetwork(context);
    }

    // set the postback param
    public void setParams(Map<String, String> params) {
        mParams = params;
    }

    // postback imem add
    public Boolean addItem(Map<String, String> item) {
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

    // postback
    public Boolean send(LpResponse lpResponse) {
        if (null != lpResponse) {
            mLpResponse = lpResponse;
        }

        if (!sendPrepare()) {
            return false;
        }

        String purchaseUrl = getPurhaseUrl();
        Log.d(LpMobileAT.LOG_TAG, "purchase url - " + purchaseUrl);

        return purchase(purchaseUrl);
    }

    // prepare the postback
    private Boolean sendPrepare() {

        if (!checkPrams()) {
            mLpResponse.fail("please check params");
            return false;
        }

        String itemsQueryString = getItemsQueryString();
        if ("" == itemsQueryString) {
            mLpResponse.fail("please item add");
            return false;
        }

        mParams.put("items", itemsQueryString);

        return true;
    }

    // postback corresponse
    private Boolean purchase(String purchaseUrl) {
        if (mLpNetwork.isOnline()) {

            return mLpNetwork.call(purchaseUrl, mLpResponse);
        }  else {

            mLpResponse.fail("please network connect check");
            return false;
        }
    }

    private String getParams() {
        Uri.Builder qs = new Uri.Builder();

        for (String key : mParams.keySet()) {
            qs.appendQueryParameter(key, mParams.get(key));
        }

        return qs.build().toString();
    }

    private Boolean checkPrams() {

        for (String param: mRquiredParams) {
            if (!mParams.containsKey(param)) {
                mLpResponse.fail("required the param : " + param);
                return false;
            }
        }
        return true;
    }

    // get the postback url
    private String getPurhaseUrl() {
        StringBuffer purchaseUrl = new StringBuffer();

        purchaseUrl.append(mPurchaseUrl);
        purchaseUrl.append(getParams());

        return purchaseUrl.toString();
    }

    private String getItemsQueryString() {
        JSONObject itemsList = new JSONObject();

        try {
            itemsList.put("list", mItems);
            return itemsList.toString();
        } catch (JSONException e) {
//            e.printStackTrace();
            return "";
        }
    }
}
