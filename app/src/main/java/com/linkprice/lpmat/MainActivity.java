package com.linkprice.lpmat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Install Referrer
        LpMobileAT lpMobileAT = new LpMobileAT(this, getIntent());
        lpMobileAT.setTagValueInstallReferrer();

        // 실행(배너 클릭시)할 때 마다 어필리에이트 변경
//        LpMobileAT lpMobileAT = new LpMobileAT(this, getIntent());
        lpMobileAT.setTagValueActivity();

        String tagValue = lpMobileAT.getTagValue();

        if (tagValue == null) {
            tagValue = "Empty";
        }

        TextView textView1 = (TextView) findViewById(R.id.tag_value) ;
        textView1.setText(tagValue) ;
    }
}