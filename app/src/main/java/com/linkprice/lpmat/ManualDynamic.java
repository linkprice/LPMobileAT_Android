package com.linkprice.lpmat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class ManualDynamic extends AppCompatActivity {
/*

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    LpMobileAT lpMobileAT = new LpMobileAT(this, getIntent());
    lpMobileAT.setTagValueActivity();

    Context mContext = this;
    Intent mIntent = getIntent();
    String log_tag = "your app debug log";
    SharedPreferences mSharedPreferences = mContext.getSharedPreferences(log_tag,  Context.MODE_PRIVATE);

    Uri data = mIntent.getData();
    if (null == data) {
        Log.d(log_tag, "setTagValueActivity - uri null");
        return;
    }

    SharedPreferences.Editor prefEditor = mSharedPreferences.edit();

    try {
        // LPINFO
        String lpinfo = data.getQueryParameter("LPINFO");

        if (lpinfo != null) {
            // lpinfo
            prefEditor.putString("lpinfo", lpinfo);

            // 광고 인정 기간
            String rd = data.getQueryParameter("rd");
            int mRd;
            try {
                mRd = Integer.parseInt(rd);
            } catch (Exception e) {
                mRd = 0;
            }
            prefEditor.putInt("rd", mRd);

            // 리퍼러
            prefEditor.putString("referrer", data.toString());

            // 등록 시간
            Calendar createCalendar = Calendar.getInstance();
            long create_time = createCalendar.getTimeInMillis();
            prefEditor.putLong("create_time", create_time);

            // install_referrer check 처리
            prefEditor.putBoolean("referrer_check", true);

            prefEditor.apply();
        }
    } catch(Exception e) {
        e.printStackTrace();
    }
}
*/

}