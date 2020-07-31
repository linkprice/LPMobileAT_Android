package com.linkprice.lpmat;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LpNetwork {
    private final Context mContext;
    //    private AsyncTask<String, Void, String> mAsyncTask = null;
    private AsyncTask<JSONObject, Void, String> mAsyncTask = null;
    private SharedPreferences mSharedPreferences;

    public LpNetwork(Context context) {

        mContext = context;
        mSharedPreferences = mContext.getSharedPreferences(LpMobileAT.LOG_TAG, Context.MODE_PRIVATE);
    }

    public boolean isOnline() {
        final ConnectivityManager cm = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm != null) {
            if (Build.VERSION.SDK_INT < 23) {
                final NetworkInfo ni = cm.getActiveNetworkInfo();

                if (ni != null) {
                    return (ni.isConnected() && (ni.getType() == ConnectivityManager.TYPE_WIFI || ni.getType() == ConnectivityManager.TYPE_MOBILE));
                }
            } else {
                final Network n = cm.getActiveNetwork();

                if (n != null) {
                    final NetworkCapabilities nc = cm.getNetworkCapabilities(n);

                    return (nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI));
                }
            }
        }

        return false;
    }

    // call V4 lppurchase
    public Boolean call(final JSONObject purchase, final LpResponse lpResponse) {
        releaseAsyncTask();

        mAsyncTask = new AsyncTask<JSONObject, Void, String>() {
            @Override
            protected String doInBackground(JSONObject... data) {
                String result = "";
                String uri = "https://service.linkprice.com/lppurchase_cps_v4.php";

                if(data[0].has("action")) {
                    uri = "https://service.linkprice.com/lppurchase_cpa_v4.php";
                }

                Log.d(LpMobileAT.LOG_TAG, "call url - " + uri);

                try {

                    HttpURLConnection con = (HttpURLConnection)new URL(uri).openConnection();
                    con.setRequestMethod("POST");
                    con.setRequestProperty("Content-Type", "application/json");
                    con.setDoOutput(true);
                    con.setDoInput(true);
                    con.setUseCaches(false);
                    con.setDefaultUseCaches(false);
                    OutputStream os = con.getOutputStream();
                    os.write(data[0].toString().getBytes("UTF-8"));
                    os.flush();
                    os.close();

                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String line;
                    StringBuffer response_data = new StringBuffer();
                    while ((line = in.readLine()) != null) {
                        response_data.append(line);
                    }

                    result = response_data.toString();
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
                        // when fail to send data, it is stored in SharedPreferences
                        SharedPreferences.Editor prefEditor = mSharedPreferences.edit();
                        prefEditor.putString("lpSales", purchase.toString());
                        prefEditor.apply();
                        lpResponse.fail("postback fail");
                    } else {

                        lpResponse.success();
                    }
                }
            }
        }.execute(purchase);

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
