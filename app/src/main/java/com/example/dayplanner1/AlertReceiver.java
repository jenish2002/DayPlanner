package com.example.dayplanner1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlertReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        //When alarm triggers
        Intent screenStarterIntent = new Intent(context,FullscreenActivityForAlarm.class);
        screenStarterIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(screenStarterIntent);

    }
}
