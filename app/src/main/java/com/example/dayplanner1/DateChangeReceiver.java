package com.example.dayplanner1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DateChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        DatabaseHelper mydb = GlobalDataHelper.returnDatabase();

        //copying from Routine to Routine_Today
        mydb.copyToRoutine();

        //delete tasks from database if it is before the current time
        mydb.clearOverDueTask();

        //Starting/Refreshing alarm
        MyAlarm myAlarm = GlobalDataHelper.createMyAlarm();
        myAlarm.refreshAlarm(context);
    }
}
