package com.example.dayplanner1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class Routine extends AppCompatActivity {

    TextView noTaskAvailable;
    ListView mListView;
    ArrayList<SingleTask> taskList;
    ListViewAdapter adapter;
    Boolean toDelete=false; //used in Alert dialog for determine Task to delete or not

    @Override
    protected void onRestart() { //Refresh activity when returns
        super.onRestart();
        finish();
        startActivity(getIntent());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine);

        //findViewById
        mListView = findViewById(R.id.routine_listview);
        noTaskAvailable = findViewById(R.id.no_routine_task);
        DatabaseHelper mydb = GlobalDataHelper.createDatabase(this);

        //add that object to this arrayAdapter
        taskList = new ArrayList<>(); //array for SingleTask

        //Refreshing myAlarm
        MyAlarm myAlarm = GlobalDataHelper.returnMyAlarm();
        myAlarm.refreshAlarm(Routine.this);

        //Custom ActionBar for close Vector
        Toolbar toolbar=findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setTitle("Routine Tasks");
        setSupportActionBar(toolbar);

        //code to redirect to Home Page on Cancel Button
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Routine.super.finish();
            }
        });

        Cursor cursor = mydb.getRoutineTasks(this);
        if(cursor.getCount()  != 0)
        {
            while(cursor.moveToNext())
            {
                String task = cursor.getString(0);
                String time = cursor.getString(1);
                int hour = Integer.parseInt(time.substring(0,time.indexOf(":"))) % 12;
                int minute = Integer.parseInt(time.substring(time.indexOf(":")+1));
                time = String.format("%02d:%02d %s", hour == 0 ? 12 : hour, minute, hour < 12 ? "am" : "pm");
                noTaskAvailable.setVisibility(View.INVISIBLE);
                SingleTask newTask = new SingleTask(task, time, false);//making object of SingleTask
                taskList.add(newTask);//adding that object to taskList array
            }
        }
        else
        {
            //showing middle TextView when database empty
            noTaskAvailable.setVisibility(View.VISIBLE);
        }
        adapter = new ListViewAdapter(this,R.layout.singlelistview,taskList);
        mListView.setAdapter(adapter); //connecting array to listView and displaying it
    }

    public void onClickAddButton(View view) //fab button to add routine tasks
    {
        Intent intent = new Intent(Routine.this, RoutineTaskActivity.class);
        startActivity(intent);
    }

    public class ListViewAdapter extends ArrayAdapter<SingleTask> {

        private Context mContext;
        int mResource;

        public ListViewAdapter(@NonNull Context context, int resource, @NonNull ArrayList<SingleTask> objects) {
            super(context, resource, objects);
            this.mContext = context;
            mResource = resource;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            //getTaskInformation
            final String textToDeleteFromTable = getItem(position).getTask();
            String Date = getItem(position).getDate();
            Boolean Enable = getItem(position).getEnable();

            //Create SingleTask
            SingleTask task = new SingleTask(textToDeleteFromTable,Date,Enable);

            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.singlelistview,null);

            TextView tvText = convertView.findViewById(R.id.tvText);
            TextView tvSubText = convertView.findViewById(R.id.tvSubText);
            final CheckBox cbEnable = convertView.findViewById(R.id.cbEnable);

            tvText.setText(textToDeleteFromTable);
            tvSubText.setText(Date);
            cbEnable.setChecked(Enable);

            //process when check changes it's state
            cbEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView,
                                             boolean isChecked) {
                    if (isChecked) {

                        //Alert to user and then delete Task
                        new AlertDialog.Builder(Routine.this)
                                .setTitle("Does your Task Complete?")
                                .setMessage("Are you sure to complete your Task?")
                                .setCancelable(false)

                                // Specifying a listener allows you to take an action before dismissing the dialog.
                                // The dialog is automatically dismissed when a dialog button is clicked.
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Continue with delete operation
                                        //Deleting from database first
                                        DatabaseHelper mydb = GlobalDataHelper.returnDatabase();
                                        mydb.deleteTaskFromRoutine(textToDeleteFromTable);//passing Text which contains Main Text of Task to determine which
                                        adapter.clear(); //clearing adapter and re-getting data from database
                                        Cursor cursor = mydb.getRoutineTasks(Routine.this);
                                        if (cursor.getCount() != 0) {
                                            noTaskAvailable.setVisibility(View.INVISIBLE);
                                            while (cursor.moveToNext()) {
                                                String task = cursor.getString(0);
                                                String time = cursor.getString(1);
                                                int hour = Integer.parseInt(time.substring(0,time.indexOf(":"))) % 12;
                                                int minute = Integer.parseInt(time.substring(time.indexOf(":")+1));
                                                time = String.format("%02d:%02d %s", hour == 0 ? 12 : hour, minute, hour < 12 ? "am" : "pm");
                                                SingleTask newTask = new SingleTask(task, time, false);//making object of SingleTask
                                                taskList.add(newTask);//adding that object to taskList array
                                            }
                                        }
                                        adapter.notifyDataSetChanged();
                                        toDelete = false;
                                        if(adapter.isEmpty())
                                        {
                                            noTaskAvailable.setVisibility(View.VISIBLE);//Displaying this when adapter is empty
                                        }
                                    }
                                })

                                // A null listener allows the button to dismiss the dialog and take no further action.
                                //On No click button
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        cbEnable.setChecked(false);
                                    }
                                })
                                .setIcon(R.drawable.ic_report_problem_black_24dp)
                                .show();
                        //Delete the task from database and recall the HomePage activity


                    } else {
                        // Check changed to false // not possible situation
                    }
                }
            });

            return convertView;
        }
    }

    //perform correct vector coding on RoutineTaskActivity
    //when user add task then it should load the Routine.xml page again to gain data from data base
    //Same for Routine to HomePage it should load the data again
}
