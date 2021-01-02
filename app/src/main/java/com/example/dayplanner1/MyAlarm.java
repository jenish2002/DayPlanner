package com.example.dayplanner1;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;

import java.util.Calendar;

public class MyAlarm {

    AlarmManager alarmManager;
    Intent intent;
    PendingIntent pendingIntent;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    public void refreshAlarm(Context context)
    {
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        intent = new Intent(context,AlertReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(context, 1, intent, 0);

        //SharedPreference to store Task and Time. It will be used on run time to show on FullScreenActivity
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();

        Calendar c = Calendar.getInstance();
        DatabaseHelper mydb = GlobalDataHelper.returnDatabase();
        Cursor cursor = mydb.getNextTaskTime(context);

        if(cursor == null)  //if no task available in database
            return;


        cursor.moveToNext();
        String timeString = cursor.getString(2); //third column is time
        //assigning value to SharedPreference
        editor.putInt("Id", Integer.parseInt(cursor.getString(0)));
        editor.putString("Task",cursor.getString(1));
        editor.putString("Time",cursor.getString(2));
        editor.putString("TableName",cursor.getString(3));
        editor.commit();

        String tableName = cursor.getString(3);
        if( tableName.equals("Main") || tableName.equals("RoutineToday")) //if data didn't come from RoutineToday or Main table
        {
            DateTime dateTime = new DateTime(timeString,true);

            c.set(Calendar.YEAR,dateTime.getYear());
            c.set(Calendar.MONTH,dateTime.getMonth()-1); //Month follows 0 indexing system
            c.set(Calendar.DAY_OF_MONTH,dateTime.getDay());
            c.set(Calendar.HOUR_OF_DAY,dateTime.getHour());
            c.set(Calendar.MINUTE,dateTime.getMinute());
            c.set(Calendar.SECOND,0);

            alarmManager.setExact(AlarmManager.RTC_WAKEUP,c.getTimeInMillis(),pendingIntent); //give time in milliseconds
        }
        else if( tableName.equals("Routine"))
        {
            //setting next day task from Routine table
            DateTime dateTime = new DateTime(timeString,false);

            c.set(Calendar.YEAR,dateTime.getYear());
            c.set(Calendar.MONTH,dateTime.getMonth()-1); //Month follows 0 indexing system
            c.set(Calendar.DAY_OF_MONTH,dateTime.getDay());
            c.set(Calendar.HOUR_OF_DAY,dateTime.getHour());
            c.set(Calendar.MINUTE,dateTime.getMinute());
            c.set(Calendar.SECOND,0);

            alarmManager.setExact(AlarmManager.RTC_WAKEUP,c.getTimeInMillis(),pendingIntent); //give time in milliseconds
        }
        //If time given to alarm manager is before current time then it will immediately starts alarm after the it is set
    }

    public void cancelAlarm()
    {
        alarmManager.cancel(pendingIntent);
    }

}
