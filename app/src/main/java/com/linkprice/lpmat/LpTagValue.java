package com.linkprice.lpmat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;

import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.android.installreferrer.api.ReferrerDetails;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;

public class LpTagValue {

    private final Context mContext;
    private final Intent mIntent;
    private final SharedPreferences mSharedPreferences;
    private final InstallReferrerClient mInstallReferrerClient;

    public LpTagValue(Context context, Intent intent) {
        mContext = context;
        mIntent = intent;
        mSharedPreferences = mContext.getSharedPreferences(LpMobileAT.LOG_TAG, Context.MODE_PRIVATE);
        mInstallReferrerClient = InstallReferrerClient.newBuilder(mContext).build();
    }

    public void setTagValueInstallReferrer() {
        if (getReferrerCheck()) {
            return;
        }

        mInstallReferrerClient.startConnection(new InstallReferrerStateListener() {

            @Override
            public void onInstallReferrerSetupFinished(int responseCode) {
                switch (responseCode) {
                    case InstallReferrerClient.InstallReferrerResponse.OK:
                        // Connection established.
                        // 구글 플레이 앱과 연결이 성공했을 때, 리퍼러 데이터를 얻기 위한 작업을 수행합니다.
                        setTagValueReferrer(mInstallReferrerClient);
                        break;
                    case InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED:
                        // API not available on the current Play Store app.
                        break;
                    case InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE:
                        // Connection couldn't be established.
                        break;
                }
            }

            @Override
            public void onInstallReferrerServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        });
    }

    // receiver receive tag_value
    private Boolean setTagValueReferrer(InstallReferrerClient referrerClient) {
        String referrer = null;

        try {
            ReferrerDetails response = referrerClient.getInstallReferrer();
            referrer = response.getInstallReferrer();
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }

        SharedPreferences.Editor prefEditor = mSharedPreferences.edit();

        // install_referrer check 처리
        setReferrerCheck(prefEditor);
        prefEditor.apply();

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
                setRD(prefEditor, data.getQueryParameter("rd"));
                // 리퍼러
                setReferrer(prefEditor, data.toString());
                // 등록 시간
                setCreateTime(prefEditor);

                // install_referrer check 처리
                setReferrerCheck(prefEditor);

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

    // get failed sending data
    public String getLpFailed() {
        return mSharedPreferences.getString( "lpSales", null);
    }

    // delete failed sending data
    public void removeLpfailed() {
        mSharedPreferences.edit().remove("lpSales").clear().apply();
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

    // get Deeplink URL
    public String getDl() {
        String deeplink = null;
        Uri data = mIntent.getData();

        if(data == null) return null;

        try{
            deeplink = data.getQueryParameter("target_url");
        } catch (Exception e){

        }

        return deeplink;
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

    public Map<String, String> getQuery(URL url) throws UnsupportedEncodingException {
        Map<String, String> query_pairs = new LinkedHashMap<String, String>();
        String query = url.getQuery();
        if(query.indexOf("&") > 0) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
            }
        } else {
            String[] pairs = new String[1];
            pairs[0] = query;
            for (String pair : pairs) {

                int idx = pair.indexOf("=");
                query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
            }
        }

        return query_pairs;
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

    // install_referrer check 처리
    private void setReferrerCheck(SharedPreferences.Editor prefEditor) {
        prefEditor.putBoolean("referrer_check", true);
    }
    // install_referrer check 여부 가져오기
    private Boolean getReferrerCheck() {
        return mSharedPreferences.getBoolean("referrer_check", false);
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