package com.linkprice.lpmat;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LpNetwork {
    private final Context mContext;
    private AsyncTask<String, Void, String> mAsyncTask = null;

    public LpNetwork(Context context) {
        mContext = context;
    }

    public boolean isOnline() {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo.State wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();

            if (wifi == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTING) {
                return true;
            }

            NetworkInfo.State mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();

            if (mobile == NetworkInfo.State.CONNECTED || mobile == NetworkInfo.State.CONNECTING) {
                return true;
            }
        } catch (NullPointerException e) {
//            e.printStackTrace();
        }

        return false;
    }

    public Boolean call(String purchaseUrl, final LpResponse lpResponse) {
        releaseAsyncTask();

        mAsyncTask = new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                String result = "";
                String uri = params[0];

                Log.d(LpMobileAT.LOG_TAG, "call url - " + uri);

                try {
                    URL url = new URL(uri);
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();

                    InputStreamReader isr = openConnectionCheckRedirects(conn, lpResponse);
                    BufferedReader responseReader = new BufferedReader(isr);

                    String responseLine;
                    StringBuffer responseStringBuffer = new StringBuffer();

                    while (null != (responseLine = responseReader.readLine())) {
                        responseStringBuffer.append(responseLine);
                    }
                    isr.close();

                    result = responseStringBuffer.toString();
                } catch (Exception e) {
                    result = "fail";
//                    e.printStackTrace();
                }

                Log.d(LpMobileAT.LOG_TAG, "call response - " + result);

                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                if (null != lpResponse) {

                    if (result.equals("fail")) {
                        lpResponse.fail("postback fail");
                    } else {
                        lpResponse.success();
                    }
                }
            }
        }.execute(purchaseUrl);

        return true;
    }

    private void releaseAsyncTask() {
        if (null != mAsyncTask) {
            mAsyncTask.cancel(true);
            mAsyncTask = null;
        }
    }

    private InputStreamReader openConnectionCheckRedirects(HttpURLConnection conn, LpResponse lpResponse) throws Exception {

        Boolean redirectFlag;
        Integer redirectCount = 0;
        InputStream in = null;

        do {
            // redirect 해제
            redirectFlag = false;

            conn.setInstanceFollowRedirects(false);
            in = conn.getInputStream();

            int status = conn.getResponseCode();
            // 300 HTTP_MULT_CHOICE
//            if (status >= 300 && status <= 307 && status != 306
//                    && status != HttpURLConnection.HTTP_NOT_MODIFIED) {

            if (checkHttpStatus(status)) {
                URL baseUrl = conn.getURL();
                String location = conn.getHeaderField("Location");
                URL redirectUrl = null;
                if (null != location) {
                    redirectUrl = new URL(baseUrl, location);
                }
                conn.disconnect();

                if (!checkRedirectUrl(redirectUrl) || redirectCount >= 5) {
                    in = null;
                    lpResponse.fail("redirect error(etc, count 5 limit over)");
                    break;
                }

                // redirect 설정
                redirectFlag = true;
                conn = (HttpURLConnection)redirectUrl.openConnection();
                redirectCount++;
            }
        } while (redirectFlag);

        return new InputStreamReader(in);
    }

    // check http status
    private Boolean checkHttpStatus(int status) {

        // HTTP_MOVED_TEMP
        // HTTP_MOVED_PERM
        // HTTP_SEE_OTHER
        // normally, 3xx is redirect
        if (status != HttpURLConnection.HTTP_OK && (
                status == HttpURLConnection.HTTP_MOVED_TEMP
                        || status == HttpURLConnection.HTTP_MOVED_PERM
                        || status == HttpURLConnection.HTTP_SEE_OTHER
        )) {
            return true;
        } else {
            return false;
        }
    }

    // check redirect url
    private Boolean checkRedirectUrl(URL url) {
        if (null == url) return false;

        if (url.getProtocol().equals("http") || url.getProtocol().equals("https")) {
            return true;
        } else {
            return false;
        }
    }
}
