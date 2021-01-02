package com.example.dayplanner1;

import android.content.Context;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateTime {
    int YYYY;
    int MM;
    int DD;
    int hh;
    int mm;

    public DateTime() { }

    public DateTime(String timeString,Boolean isToday)
    {

        YYYY = Integer.parseInt(new SimpleDateFormat("yyyy", Locale.getDefault()).format(new Date()));
        MM = Integer.parseInt(new SimpleDateFormat("M", Locale.getDefault()).format(new Date()));
        DD = Integer.parseInt(new SimpleDateFormat("d", Locale.getDefault()).format(new Date()));

        if(!isToday)
        {
            DD++; //if it is not for current day alarm, then tomorrow day's date is used to set
            //if day is last day of month then more then changes should be done on month and year also
        }
        hh = Integer.parseInt(timeString.substring(0,2));
        mm = Integer.parseInt(timeString.substring(3,5));
    }

    public int getDay()
    {
        return DD;
    }
    public int getMonth()
    {
        return MM;
    }
    public int getYear()
    {
        return YYYY;
    }
    public int getHour()
    {
        return hh;
    }
    public int getMinute()
    {
        return mm;
    }

    public void setDate(int YYYY, int MM, int DD,Context context) {
        this.YYYY = YYYY;
        this.MM = MM;
        this.DD = DD;
    }
    public void setTime(int hh, int mm,Context context) {
        this.hh = hh;
        this.mm = mm;
    }

    public DateTime(DateTime datetime) //copy constructor
    {
         YYYY = datetime.YYYY;
         MM = datetime.MM;
         DD = datetime.DD;
         hh = datetime.hh;
         mm= datetime.mm;
    }
    @Override
    public String toString() {
        return YYYY+"-"+MM+"-"+DD+" "+hh+":"+mm;
    }

    public String timeString()
    {
        if(hh>=10 && mm>=10)
            return hh + ":" + mm;
        else if(hh<=10 && mm>=10)
            return "0"+hh + ":" + mm;
        else if(hh>=10 && mm<=10)
            return hh + ":0" + mm;
        else
            return "0"+hh + ":0" + mm;
    }

    public String dateString() { return DD+"-0"+MM+"-"+YYYY; }

    public String tomorrowDateStringWithZero()
    {
        DD++;
        if(DD<=9 && MM<=9) //date and month both less than 10
            return "0"+DD+"-0"+MM+"-"+YYYY;
        if(DD<=9) //day is 10 or more
            return "0"+DD+"-"+MM+"-"+YYYY;
        else if (MM<=9)
            return DD+"-0"+MM+"-"+YYYY;
        else //date and month both in 2 digit
            return DD+"-"+MM+"-"+YYYY;
    }

    public String dateStringWithZero() {

        if(DD<=9 && MM<=9) //date and month both less than 10
            return "0"+DD+"-0"+MM+"-"+YYYY;
        if(DD<=9) //day is 10 or more
            return "0"+DD+"-"+MM+"-"+YYYY;
        else if (MM<=9)
            return DD+"-0"+MM+"-"+YYYY;
        else //date and month both in 2 digit
            return DD+"-"+MM+"-"+YYYY;
    }

    public Boolean validate(Context context) {

        int curYear = Integer.parseInt(new SimpleDateFormat("yyyy", Locale.getDefault()).format(new Date()));
        int curMonth = Integer.parseInt(new SimpleDateFormat("M", Locale.getDefault()).format(new Date()));
        int curDay = Integer.parseInt(new SimpleDateFormat("d", Locale.getDefault()).format(new Date()));
        int curHour = Integer.parseInt(new SimpleDateFormat("HH", Locale.getDefault()).format(new Date())); //converting hour to 24 manner
        int curMinute = Integer.parseInt(new SimpleDateFormat("mm", Locale.getDefault()).format(new Date()));

        //Toast.makeText(context, "In database Validating values:"+YYYY+" "+MM+" "+DD+" "+hh+" "+mm, Toast.LENGTH_SHORT).show();
        //Toast.makeText(context, "In database Validating curDate:"+curYear+" "+curMonth+" "+curDay+" "+curHour+" "+curMinute, Toast.LENGTH_SHORT).show();

        if(curYear < YYYY)
            return true;
        if(curYear > YYYY)
            return false;

        //comes here only is year is same
        if(curMonth < MM)
            return true;
        if(curMonth > MM)
            return false;

        //comes here only is month is same
        if(curDay < DD)
            return true;
        if(curDay > DD)
            return false;

        //comes here only is day is same
        if(curHour < hh)
            return true;
        if(curHour > hh)
            return false;

        //comes here only is hour is same
        if(curMinute < mm)
            return true;    //minute must be after the current minute
        if(curMinute > mm)
            return false;

        return false;
    }

    public void setCurrentDateTime() {

        YYYY = Integer.parseInt(new SimpleDateFormat("yyyy", Locale.getDefault()).format(new Date()));
        MM = Integer.parseInt(new SimpleDateFormat("M", Locale.getDefault()).format(new Date()));
        DD = Integer.parseInt(new SimpleDateFormat("d", Locale.getDefault()).format(new Date()));
        hh = Integer.parseInt(new SimpleDateFormat("HH", Locale.getDefault()).format(new Date()));
        mm = Integer.parseInt(new SimpleDateFormat("mm", Locale.getDefault()).format(new Date()));

    }

    public static String addSnoozeTimeToCurrentTime(String time, int snoozeTime)
    {
        int intMinute = Integer.parseInt(time.substring(3)); //takes string from index to 3 to end
        int intHour = Integer.parseInt(time.substring(0,2)); //takes hour

        if((intMinute+snoozeTime) < 60)
        {
            intMinute = intMinute + snoozeTime;
        }
        else
        {
            intHour++;
            intMinute = intMinute + snoozeTime - 60;
        }

        String updatedTime="";

        if(intHour < 10 && intMinute < 10)
        {
            updatedTime = "0" + intHour + ":0" + intMinute;
        }
        else if(intHour < 10)
        {
            updatedTime = "0" + intHour + ":" + intMinute;
        }
        else if(intMinute < 10)
        {
            updatedTime = intHour + ":0" + intMinute;
        }
        else
        {
            updatedTime = intHour + ":" + intMinute;
        }

        return updatedTime;
    }
}

