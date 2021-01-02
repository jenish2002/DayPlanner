package com.example.dayplanner1;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    //TABLE_MAIN is main table which stores data
    //if routine is selected data will be stored in TABLE_ROUTINE
    //After that TABLE_TODAY get value from both table which is suitable or instead print data on HomePage through Both table which suitable
    //Get data from TABLE_TODAY to display on HomePage canceled
    //When deleting data delete from only TABLE_TODAY and TABLE_MAIN canceled

    public static final String DATABASE_NAME = "Day_Planner.db";
    public static final String TABLE_MAIN = "Main";
    public static final String TABLE_ROUTINE = "Routine";
    public static final String TABLE_ROUTINE_TODAY = "RoutineToday";

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    String COL_1_MAIN = "Task_Id";  //updateCurrentAlarmTask(this method uses Task_Id word directly in query instead of COL_1_MAIN
    String COL_2_MAIN = "Task";
    String COL_3_MAIN = "Time";
    String COL_4_MAIN = "Date";
    String COL_5_MAIN = "Repetition";  //updateCurrentAlarmTask(this method uses Repetition word directly in query instead of COL_5_MAIN
    String COL_6_MAIN = "Snooze_Time"; //updateCurrentAlarmTask(this method uses Snooze_Time word directly in query instead of COL_6_MAIN

    String COL_1_ROUTINE = "Task_Id";
    String COL_2_ROUTINE = "Task";
    String COL_3_ROUTINE = "Time";
    String COL_4_ROUTINE = "Repetition";
    String COL_5_ROUTINE = "Snooze_Time";

    String COL_1_ROUTINE_TODAY = "Task_Id";  //updateCurrentAlarmTask(this method uses Task_Id word directly in query instead of COL_1_ROUTINE_TODAY
    String COL_2_ROUTINE_TODAY = "Task";
    String COL_3_ROUTINE_TODAY = "Time";
    String COL_4_ROUTINE_TODAY = "Repetition";  //updateCurrentAlarmTask(this method uses Repetition word directly in query instead of COL_5_ROUTINE_TODAY
    String COL_5_ROUTINE_TODAY = "Snooze_Time"; //updateCurrentAlarmTask(this method uses Snooze_Time word directly in query instead of COL_6_ROUTINE_TODAY

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
        onCreate(db); //manually calling onCreate to create tables
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL("CREATE TABLE " + TABLE_MAIN + "(" + COL_1_MAIN + " INTEGER PRIMARY KEY AUTOINCREMENT" + "," + COL_2_MAIN + " TEXT" + "," + COL_3_MAIN + " TEXT" + "," +
                    COL_4_MAIN + " TEXT" + "," + COL_5_MAIN + " INTEGER" + "," + COL_6_MAIN + " INTEGER" + ")");

            db.execSQL("CREATE TABLE " + TABLE_ROUTINE + "(" + COL_1_ROUTINE + " INTEGER PRIMARY KEY AUTOINCREMENT" + "," + COL_2_ROUTINE + " TEXT" + "," + COL_3_ROUTINE + " TEXT" + "," +
                    COL_4_ROUTINE + " INTEGER" + "," + COL_5_ROUTINE + " INTEGER)");

            db.execSQL("CREATE TABLE " + TABLE_ROUTINE_TODAY + "(" + COL_1_ROUTINE_TODAY + " INTEGER PRIMARY KEY AUTOINCREMENT" + "," + COL_2_ROUTINE_TODAY + " TEXT" + "," + COL_3_ROUTINE_TODAY + " TEXT" + "," +
                    COL_4_ROUTINE_TODAY + " INTEGER" + "," + COL_5_ROUTINE_TODAY + " INTEGER)");

        }
        catch (Exception e)
        {
            //catch occur when tables already exists
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { } //abstract method.Compulsory to override

    public boolean insertData(Context context,String TASK,String TIME,String DATE,int REPETITION,int SNOOZE_TIME,int ROUTINE)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        Toast.makeText(context, TIME+" BEFORE INSERT", Toast.LENGTH_SHORT).show();
        //if Routine is not selected into MAIN table else insert into ROUTINE table
        if(ROUTINE == 0) {
            db.execSQL("INSERT INTO " + TABLE_MAIN + "(" + COL_2_MAIN + "," + COL_3_MAIN + "," + COL_4_MAIN + "," + COL_5_MAIN + "," + COL_6_MAIN + ") " +
                    "VALUES(" + "'" + TASK + "','" + TIME + "','" + DATE + "'," + REPETITION + "," + SNOOZE_TIME + ")");
        }
        else if(ROUTINE == 1)
        {
            db.execSQL("INSERT INTO " + TABLE_ROUTINE + "(" + COL_2_ROUTINE + "," + COL_3_ROUTINE + "," + COL_4_ROUTINE + "," + COL_5_ROUTINE + ") VALUES("
                    + "'" + TASK + "','" + TIME + "'," + REPETITION + "," + SNOOZE_TIME + ")");

            db.execSQL("INSERT INTO " + TABLE_ROUTINE_TODAY + "(" + COL_2_ROUTINE_TODAY + "," + COL_3_ROUTINE_TODAY + "," + COL_4_ROUTINE_TODAY + "," + COL_5_ROUTINE_TODAY + ") VALUES("
                    + "'" + TASK + "','" + TIME + "'," + REPETITION + "," + SNOOZE_TIME + ")");
        }
        //string need to have quotes ' ' when inserting values
        //put space before any value or field in query e.g. " WHERE "
        return true;
    }

    public Cursor getTasks(Context context)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        //creating todayDate which compares with database during retrieving
        DateTime dateTime = new DateTime();
        dateTime.setCurrentDateTime();
        String todayDate = dateTime.dateStringWithZero();

        Cursor curson = db.rawQuery("SELECT "+ COL_2_MAIN +","+ COL_3_MAIN + " FROM " + TABLE_MAIN +" WHERE "+ TABLE_MAIN +"."+COL_4_MAIN+" = '"+todayDate+ "' UNION ALL SELECT " + COL_2_ROUTINE_TODAY +","+ COL_3_ROUTINE_TODAY +" FROM "+TABLE_ROUTINE_TODAY+" ORDER BY "+COL_3_MAIN,null);

        //SELECT Task, Date FROM TableMain UNION ALL SELECT Task, Date FROM TableRoutine Where Date = todayDate Order By Time

        return curson;
    }


    public void deleteTask(String mainTextOfTask) {

        SQLiteDatabase db = this.getWritableDatabase();

        //Delete Task from both Tables. :( If Both table has same Task then both will be deleted
        db.execSQL("Delete from "+ TABLE_MAIN +" where "+ COL_2_MAIN +" = '"+mainTextOfTask+"'");
        db.execSQL("Delete from "+ TABLE_ROUTINE_TODAY +" where "+ COL_2_ROUTINE_TODAY +" = '"+mainTextOfTask+"'");

    }

    public void deleteTaskFromRoutine(String mainTextOfTask) {

        SQLiteDatabase db = this.getWritableDatabase();

        //Delete Task from both Tables. :( If Both table has same Task then both will be deleted
        db.execSQL("Delete from "+ TABLE_ROUTINE +" where "+ COL_2_MAIN +" = '"+mainTextOfTask+"'");
        db.execSQL("Delete from "+ TABLE_ROUTINE_TODAY +" where "+ COL_2_ROUTINE_TODAY +" = '"+mainTextOfTask+"'");
    }

    public void copyToRoutine() {
        //Called once a day when first time user opens the app
        //Copy data from TABLE_ROUTINE TO TABLE_ROUTINE_TODAY
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("Delete From "+ TABLE_ROUTINE_TODAY); //clearing TABLE_ROUTINE_TODAY task which weren't complete
        //db.execSQL("Delete From "+ TABLE_MAIN); //clearing TABLE_MAIN task which weren't complete
        //Logical Error: delete only those tasks which are before current time on current date.(Second query)


        db.execSQL("INSERT INTO "+ TABLE_ROUTINE_TODAY + " SELECT * FROM "+ TABLE_ROUTINE); //copying data
        //Logical Error: only copy those task which
    }

    public void insertDataToRoutine(Context context, String TASK, String TIME, int REPETITION, int SNOOZE_TIME) {

        SQLiteDatabase db = this.getWritableDatabase();

        //adding data to TABLE_ROUTINE AND TABLE_ROUTINE_TODAY
        db.execSQL("INSERT INTO " + TABLE_ROUTINE + "(" + COL_2_ROUTINE + "," + COL_3_ROUTINE + "," + COL_4_ROUTINE + "," + COL_5_ROUTINE + ") VALUES("
                + "'" + TASK + "','" + TIME + "'," + REPETITION + "," + SNOOZE_TIME + ")");

        db.execSQL("INSERT INTO " + TABLE_ROUTINE_TODAY + "(" + COL_2_ROUTINE + "," + COL_3_ROUTINE + "," + COL_4_ROUTINE + "," + COL_5_ROUTINE + ") VALUES("
                + "'" + TASK + "','" + TIME + "'," + REPETITION + "," + SNOOZE_TIME + ")");
    }

    public Cursor getRoutineTasks(Context context) {

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor curson = db.rawQuery("Select " + COL_2_ROUTINE + "," + COL_3_ROUTINE + " FROM " + TABLE_ROUTINE,null);

        //Select Task,Time from ROUTINE_TODAY;

        return curson;
    }

    public Cursor getNextTaskTime(Context context) {
        SQLiteDatabase db = this.getWritableDatabase();

        DateTime dateTimeObject = new DateTime();
        dateTimeObject.setCurrentDateTime();
        String todayDateString= dateTimeObject.dateStringWithZero();  //giving today's date in this format "00-00-0000"
        Cursor cursor=null; //to put cursor in try block have to do this

        //Normal query which will run on main and routineToday. The nearest time task will be set in the alarm. If query fails means there is no task to set alarm. So set alarm of next day from routine table. And if it is empty then no need to set alarm
        cursor = db.rawQuery("SELECT " + COL_1_MAIN + "," + COL_2_MAIN + "," + COL_3_MAIN + ", 'Main' AS tableName " + " FROM " + TABLE_MAIN + " WHERE " + COL_4_MAIN + " ='" + todayDateString + "' UNION ALL Select " +
                COL_1_ROUTINE_TODAY + "," + COL_2_ROUTINE_TODAY + "," + COL_3_ROUTINE_TODAY + ", 'RoutineToday' AS tableName " + " FROM " + TABLE_ROUTINE_TODAY + " ORDER BY " + COL_3_ROUTINE_TODAY + " LIMIT 1", null);
        //Takes 1 row from Main or Routine_Today where time is least

        if(cursor.getCount() == 0) //There is no task to work today.So getting task from routine table and setting it for tomorrow
        {
            cursor = db.rawQuery("SELECT " + COL_1_ROUTINE + "," + COL_2_ROUTINE + "," + COL_3_ROUTINE + ", 'Routine' AS tableName " + " FROM " + TABLE_ROUTINE + " ORDER BY " + COL_3_ROUTINE + " LIMIT 1",null);
        }

        if(cursor.getCount() == 0)
            return null; //No task available even in Routine table

        return cursor;
        //If task has to be repeat, overwriting the task time with (time+snooze_time) and decreasing repeat by 1
        //Task will be delete in updateCurrentAlarmTask method if has no repetition when task rang
    }

    public void updateCurrentAlarmTask(Context context) {
        SQLiteDatabase db = this.getWritableDatabase();

        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();
        int ID = preferences.getInt("Id", -1);
        String tableName = preferences.getString("TableName", "Unknown Table!!!");
        String time = preferences.getString("Time","00:00");

        Cursor cursorRepetition = db.rawQuery("SELECT Repetition,Snooze_Time FROM " + tableName + " WHERE Task_Id = " + ID ,null);
        cursorRepetition.moveToNext();
        int repetition = Integer.parseInt(cursorRepetition.getString(0));
        int snoozeTime = Integer.parseInt(cursorRepetition.getString(1));
        String updatedTime = DateTime.addSnoozeTimeToCurrentTime(time,snoozeTime);

        if( repetition > 0) //When alarm once triggered and user click close alarm it will redirected here. If repeatition is 0 then else part delete that task other wise
        {                   //if part changes task time and repetition value
            //Decrease the value of repetition and add Snooze_Time to Time column
            if(tableName.equals("Main")) //UPDATE Main SET Time = '19:38' , Repetition = Repetition -1  WHERE  COL_1_MAIN  = 41
            {
                ContentValues data = new ContentValues();
                data.put(COL_3_MAIN, updatedTime);
                data.put(COL_5_MAIN, repetition-1);
                db.update(TABLE_MAIN, data, COL_1_MAIN + " = "+ ID , null);

            }
            else if(tableName.equals("RoutineToday"))
            {
                ContentValues data = new ContentValues();
                data.put(COL_3_ROUTINE_TODAY, updatedTime);
                data.put(COL_4_ROUTINE_TODAY, repetition - 1);
                db.update(TABLE_ROUTINE_TODAY, data, COL_1_ROUTINE_TODAY + " = " + ID, null);

            }
        }
        else
        {
            //Repetition is 0 so delete that task
            if(tableName.equals("Main"))
            {
                db.delete(TABLE_MAIN, COL_1_MAIN + " = " + ID + " AND " + COL_5_MAIN + " <= " + 0 , null);
                //db.rawQuery("DELETE FROM " + TABLE_MAIN + " WHERE " + COL_1_MAIN + " = " + ID, null);
            }
            else if(tableName.equals("RoutineToday"))
            {
                db.delete(TABLE_MAIN, COL_1_ROUTINE_TODAY + " = " + ID + " AND " + COL_4_ROUTINE_TODAY + " <= " + 0 , null);
                //db.rawQuery("DELETE FROM " + TABLE_ROUTINE_TODAY + " WHERE " + COL_1_ROUTINE_TODAY + " = " + ID, null);
            }
        }

    }

    public void clearOverDueTask() {
        SQLiteDatabase db = this.getWritableDatabase();
        DateTime dateTime = new DateTime();
        dateTime.setCurrentDateTime();
        String todayDate = dateTime.dateStringWithZero();
        String currentTime = dateTime.timeString();

        db.execSQL("Delete from "+ TABLE_MAIN +" where "+ COL_4_MAIN + " < '" + todayDate + "'");
        db.execSQL("Delete from "+ TABLE_MAIN +" where "+ COL_4_MAIN + " = '" + todayDate +"' AND " +COL_3_MAIN + " < '" + currentTime + "'");

        db.execSQL("Delete from "+ TABLE_ROUTINE_TODAY +" where "+ COL_3_ROUTINE_TODAY + " < '" + currentTime + "'");
    }
}