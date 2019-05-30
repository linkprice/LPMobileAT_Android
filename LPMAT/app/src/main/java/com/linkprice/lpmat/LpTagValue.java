package com.linkprice.lpmat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;

public class LpTagValue {

    private final Context mContext;
    private final Intent mIntent;
    private final SharedPreferences mSharedPreferences;

    public LpTagValue(Context context, Intent intent) {
        mContext = context;
        mIntent = intent;
        mSharedPreferences = mContext.getSharedPreferences(LpMobileAT.LOG_TAG, Context.MODE_PRIVATE);
    }

    // receiver receive tag_value
    public Boolean setTagValueReceiver() {

        Bundle extras = mIntent.getExtras();

        if (null == extras) {
            Log.d(LpMobileAT.LOG_TAG, "extra null");
            return false;
        }

        SharedPreferences.Editor prefEditor = mSharedPreferences.edit();
        String referrer = extras.getString("referrer");

        if (null == referrer) {
            Log.d(LpMobileAT.LOG_TAG, "referrer null");
            return false;
        }

        try {
            Map<String, String> referrerParse = parseQuery(referrer);

            // LPINFO
            String lpinfo = referrerParse.get("LPINFO");

            if (setLpinfo(prefEditor, lpinfo)) {

                // 광고 인정 기간
                String rd = referrerParse.get("rd");
                setRD(prefEditor, rd);
                // 리퍼러
                setReferrer(prefEditor, referrer);
                // 등록 시간
                setCreateTime(prefEditor);

                prefEditor.apply();

                return true;
            }

        } catch (Exception e) {
//            e.printStackTrace();
        }

        return false;
    }

    // activity receive tag_value
    public Boolean setTagValueActivity() {

        Uri data = mIntent.getData();
        if (null == data) {
            Log.d(LpMobileAT.LOG_TAG, "setTagValueActivity - uri null");
            return false;
        }

        SharedPreferences.Editor prefEditor = mSharedPreferences.edit();

        try {
            // LPINFO
            String lpinfo = data.getQueryParameter("LPINFO");

            if (setLpinfo(prefEditor, lpinfo)) {

                // 광고 인정 기간
                String rd = data.getQueryParameter("rd");
                setRD(prefEditor, rd);
                // 리퍼러
                setReferrer(prefEditor, data.toString());
                // 등록 시간
                setCreateTime(prefEditor);

                prefEditor.apply();

                return true;
            }
        } catch(Exception e) {
//            e.printStackTrace();
        }

        return false;
    }

    // validate tag_value and get the tag_value
    public String getTagValue() {
        if (checkTagValue()) {
            return getLpinfo();
        } else {
            return null;
        }
    }

    // validate tag_value
    public Boolean checkTagValue() {
        if (validateLpinfo() && validateRD()) {
            return true;
        } else {
            return false;
        }
    }

    public String getReferrer() {
        return mSharedPreferences.getString( "referrer", null);
    }

    private Map<String, String> parseQuery(String query)
            throws UnsupportedEncodingException {

        Map<String, String> queryPairs = new LinkedHashMap<>();

        String mQuery = URLDecoder.decode(query, "UTF-8");
        String[] pairs = mQuery.split("&");

        int queryIdx;
        String queryKey = null;
        String queryValue = null;

        for (String pair : pairs) {
            queryIdx = pair.indexOf("=");

            queryKey = pair.substring(0, queryIdx);
            queryValue = pair.substring(queryIdx + 1);

            queryPairs.put(queryKey, queryValue);
        }

        // 광고 인정 기간 초기화
        if (!queryPairs.containsKey("rd")) {
            queryPairs.put("rd", "0");
            Log.d(LpMobileAT.LOG_TAG, "parseQuery : rd - 0");
        }

        return queryPairs;
    }

    private Boolean setLpinfo(SharedPreferences.Editor prefEditor, String lpinfo) {

        Log.d(LpMobileAT.LOG_TAG, "lpinfo - " + lpinfo);
        if (null == lpinfo) {
            return false;
        }

        prefEditor.putString("lpinfo", lpinfo);
        return true;
    }

    private void setRD(SharedPreferences.Editor prefEditor, String rd) {

        int mRd = getRD(rd);

        Log.d(LpMobileAT.LOG_TAG, "rd - " + mRd);
        prefEditor.putInt("rd", mRd);
    }

    private Integer getRD(String rd) {
        int mRd;

        try {
            mRd = Integer.parseInt(rd);
        } catch (Exception e) {
            mRd = 0;
        }

        if (mRd < 0) {
            mRd = 0;
        }

        return mRd;
    }

    private void setReferrer(SharedPreferences.Editor prefEditor, String referrer) {
        Log.d(LpMobileAT.LOG_TAG, "referrer - " + referrer);
        prefEditor.putString("referrer", referrer);
    }

    private void setCreateTime(SharedPreferences.Editor prefEditor) {

        // 등록 시간
        Calendar createCalendar = Calendar.getInstance();

        long create_time = createCalendar.getTimeInMillis();
        prefEditor.putLong("create_time", create_time);
        Log.d(LpMobileAT.LOG_TAG, "create_time - " + String.valueOf(create_time));
    }

    private Long getCreateTime() {
        return mSharedPreferences.getLong( "create_time", -1);
    }
    private String getLpinfo() {
        return mSharedPreferences.getString( "lpinfo", null);
    }
    private Integer getRD() {
        return mSharedPreferences.getInt( "rd", -1);
    }

    private Boolean validateLpinfo() {
        if (null == getReferrer()) return false;
        if (-1 == getCreateTime()) return false;
        if (null == getLpinfo()) return false;
        if (-1 == getRD()) return false;

        return true;
    }

    // 광고 효과 기간 체크
    private Boolean validateRD() {
        if (0 == getRD()) {
            return true;
        }

        try {
            Calendar createCalendar = Calendar.getInstance();
            createCalendar.setTimeInMillis(getCreateTime());

            // 만료 날짜
            Calendar expireCalendar = (Calendar)createCalendar.clone();
            expireCalendar.add(Calendar.DAY_OF_MONTH, getRD());

            Calendar currentCalendar = Calendar.getInstance();

            // 등록일과 만료일 사이에 있는 지 체크
            if (currentCalendar.after(createCalendar) && currentCalendar.before(expireCalendar)) {
                return true;
            }
        } catch (Exception e) {
//            e.printStackTrace();
        }

        return false;
    }
}
