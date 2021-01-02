package com.example.dayplanner1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class HomePage extends AppCompatActivity {

    private ListView listview;
    private TextView noTaskAvailable;
    private boolean doubleBackToExitPressedOnce = false;
    ArrayList<SingleTask> taskList;
    ListViewAdapter adapter;
    Boolean toDelete=false; //used in Alert dialog for determine Task to delete or not
    private static SharedPreferences sharedPreference; //
    private static SharedPreferences.Editor editor;

    @Override
    protected void onRestart() { //Refresh activity when returns
        super.onRestart();
        finish();
        startActivity(getIntent());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        //findViewById
        listview = findViewById(R.id.listview);
        noTaskAvailable = findViewById(R.id.no_task_text);
        DatabaseHelper mydb = GlobalDataHelper.createDatabase(this);

        //add that object to this arrayAdapter
        taskList = new ArrayList<>();//array for SingleTask

        //assigning space for SharedPreference
        sharedPreference = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreference.edit();
        String preferenceDate = sharedPreference.getString("lastOpenOn","00/00/0000");

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String todayDate = sdf.format(new Date()); //returns today's date with applied format in String

        //Three cases can be occur
        if(preferenceDate == "00/00/0000") //if this is the first time when sharedPreference is created
        {
            //First time SharedPreference is created //value will 0
            mydb.copyToRoutine();
            editor.putString("lastOpenOn",todayDate); //updating date with today
            editor.commit();
        }
        else if((preferenceDate.compareTo(todayDate) != 0))
        {
            //Value is not matched today //Means first time user opening app
            mydb.copyToRoutine();
            editor.putString("lastOpenOn",todayDate); //updating date with today
            editor.commit();
        }
        else if((preferenceDate.compareTo(todayDate) == 0))
        {
            //value is equal to Toady
            //No need to copy because user already started app before(with same date)
        }

        //delete tasks from database if it is before the current time
        mydb.clearOverDueTask();

        //Starting/Refreshing alarm
        MyAlarm myAlarm = GlobalDataHelper.createMyAlarm();
        myAlarm.refreshAlarm(HomePage.this);

        //ListView Process. Getting data from database
        Cursor cursor = mydb.getTasks(this);
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
        listview.setAdapter(adapter); //connecting array to listView and displaying it

    }

    //creating/inflating menu-bar for the activity_home_page
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.home_menu,menu);
        return true;
    }

    //what to do when user select item of menubar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_settings: {
                Intent intent = new Intent(this,Setting.class);
                startActivity(intent);
                break;
            }
            case R.id.action_Routine: {
                Intent intent = new Intent(this, Routine.class);
                startActivity(intent);
                break;
            }
            case R.id.action_help: {
                Intent intent = new Intent(this, Help.class);
                startActivity(intent);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    //fab button click event
    public void onClickAddButton(View view)
    {
        Intent intent = new Intent(HomePage.this,TaskActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            //close application completely
            this.finishAffinity();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Tap again to exit", Toast.LENGTH_SHORT).show();
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
            final String Text = getItem(position).getTask();
            String Date = getItem(position).getDate();
            Boolean Enable = getItem(position).getEnable();

            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.singlelistview,null);

            TextView tvText = convertView.findViewById(R.id.tvText);
            TextView tvSubText = convertView.findViewById(R.id.tvSubText);
            final CheckBox cbEnable = convertView.findViewById(R.id.cbEnable);

            tvText.setText(Text);
            tvSubText.setText(Date);
            cbEnable.setChecked(Enable);

            //process when check changes it's state
            cbEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView,
                                             boolean isChecked) {
                    if (isChecked) {

                        //Alert to user and then delete Task
                        new AlertDialog.Builder(HomePage.this)
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
                                            mydb.deleteTask(Text);//passing Text which contains Main Text of Task to determine which
                                            adapter.clear(); //clearing adapter and re-getting data from database
                                            Cursor cursor = mydb.getTasks(HomePage.this);
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
}