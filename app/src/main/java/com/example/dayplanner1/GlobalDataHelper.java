package com.example.dayplanner1;

import android.content.Context;

public class GlobalDataHelper {

    private static DatabaseHelper mydb;
    private static MyAlarm myAlarm;

    public static DatabaseHelper createDatabase(Context context)
    {
        mydb = new DatabaseHelper(context);
        return mydb;
    }
    public static DatabaseHelper returnDatabase()
    {
        return mydb;
    }

    public static MyAlarm createMyAlarm()
    {
        myAlarm = new MyAlarm(); //When homepage activity is getting refresh, createMyAlarm method is getting called. So it will overwrites the
        return myAlarm;          //previous object. So I think, the alarm which was set last will get destroy. Now new object refreshAlarm method sets new alarm
    }
    public static MyAlarm returnMyAlarm()
    {
        return myAlarm;
    }
}
