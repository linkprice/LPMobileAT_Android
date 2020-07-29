package com.linkprice.lpmat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.android.installreferrer.api.ReferrerDetails;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;

public class ManualInstallReferrer extends AppCompatActivity {

/*
private InstallReferrerClient mInstallReferrerClient;
private SharedPreferences mSharedPreferences;

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // Install Referrer
    LpMobileAT lpMobileAT = new LpMobileAT(this, getIntent());
    lpMobileAT.setTagValueInstallReferrer();

    Context mContext = this;
    String log_tag = "your app debug log";

    mInstallReferrerClient = InstallReferrerClient.newBuilder(mContext).build();
    mSharedPreferences = mContext.getSharedPreferences(log_tag,  Context.MODE_PRIVATE);

    if (!getReferrerCheck()) {

        mInstallReferrerClient.startConnection(new InstallReferrerStateListener() {

            @Override
            public void onInstallReferrerSetupFinished(int responseCode) {
                switch (responseCode) {
                    case InstallReferrerClient.InstallReferrerResponse.OK:
                        // Connection established.
                        // 구글 플레이 앱과 연결이 성공했을 때, 리퍼러 데이터를 얻기 위한 작업을 수행합니다.
                        String referrer = null;

                        try {
                            ReferrerDetails response = mInstallReferrerClient.getInstallReferrer();
                            referrer = response.getInstallReferrer();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                            return;
                        }

                        SharedPreferences.Editor prefEditor = mSharedPreferences.edit();

                        // install_referrer check 처리
                        setReferrerCheck(prefEditor);
                        prefEditor.apply();

                        if (null == referrer) {
                            Log.d(log_tag, "referrer null");
                            return;
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
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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
}
*/

}