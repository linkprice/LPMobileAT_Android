package com.linkprice.lpmat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class InstallReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

        // set tag_value at receiver
        LpMobileAT lpMobileAT = new LpMobileAT(context, intent);
        lpMobileAT.setTagValueReceiver();
    }
}
