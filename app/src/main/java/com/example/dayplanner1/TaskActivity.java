package com.example.dayplanner1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.CheckBox;
import android.widget.TimePicker;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TaskActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    DatabaseHelper mydb;
    DateTime datetime;
    CheckBox routine;
    TextView task;
    Button buttonDate,buttonTime;
    EditText date,time;
    EditText snooze;
    Spinner spnrepeat;
    private int mYear,mMonth,mDay;
    private int mHour,mMinute;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        //Repeater
        spnrepeat = findViewById(R.id.spnRepeat);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.spinner,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnrepeat.setAdapter(adapter);
        spnrepeat.setOnItemSelectedListener(this);

        //findViewById
        mydb = GlobalDataHelper.returnDatabase();
        datetime = new DateTime();
        task = findViewById(R.id.etTask);
        date = findViewById(R.id.etDate);
        time = findViewById(R.id.etTime);
        snooze = findViewById(R.id.etSnooze_Time);
        routine = findViewById(R.id.cbRoutine);
        buttonDate = findViewById(R.id.btnDate);
        buttonDate.setOnClickListener(this);
        buttonTime = findViewById(R.id.btnTime);
        buttonTime.setOnClickListener(this);

        //Custom ActionBar for close Vector
        Toolbar toolbar=findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close_black_24dp);
        toolbar.setTitle("Add New Task");
        setSupportActionBar(toolbar);

        //code to redirect to Home Page on Cancel Button
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(TaskActivity.this, "Task not created", Toast.LENGTH_SHORT).show();
                TaskActivity.super.finish();
            }
        });

        //setting up date TextView with current date and also initializing datetime object to current date and time
        datetime.setCurrentDateTime();
        String curDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        date.setText(curDate);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menufile, menu);
        return true;
    }

    //Redirect when click on Correct vector
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //Redirect to Home page of application on Correct vector--------------------
        //Add data to database and redirect to home page----------------------------

        if(validateActivity())//validating editTexts
        {
            //Creating variable for passing to DataBaseHelper class
            String TASK = task.getText().toString();
            DateTime DATETIME = new DateTime(datetime);//copying data from datetime to DATETIME object where datetime gets value from on click of button
            int ROUTINE=0;
            int REPETITION = spnrepeat.getSelectedItemPosition();
            int SNOOZETIME = Integer.parseInt(snooze.getText().toString());

            if(routine.isChecked())
                ROUTINE = 1;
            else
                ROUTINE = 0;

            //calling insert method with argument of user values
            mydb.insertData(this,TASK,DATETIME.timeString(),DATETIME.dateStringWithZero(),REPETITION,SNOOZETIME,ROUTINE);//true for Enable

            //finishes only current activity
            Intent i = new Intent(this,HomePage.class);
            startActivity(i);
            return true;
        }
        else
        {
            Toast.makeText(this, "Please enter valid date..!!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private Boolean validateActivity() { //set the date and time editText by current date so only need to validate that date is greater or equal to current date or not if date entered is before current date than show error with Toast

        Boolean return_value = true;
        int snoozeTime = Integer.parseInt(snooze.getText().toString());

        //validating editTexts
        if(task.getText().toString().isEmpty())
        {
            task.setError("Enter Your Task");
            return_value = false;
        }
        if(snooze.getText().toString().isEmpty())
        {
            snooze.setError("Empty Text!!!");
            return_value = false;
        }
        if(date.getText().toString().isEmpty())
        {
            date.setError("Select Date");
            return_value = false;
        }
        if(time.getText().toString().isEmpty())
        {
            time.setError("Select Time");
            return_value = false;
        }
        if(return_value == false)
            return return_value;

        //validating time and date of editTexts
        return_value = datetime.validate(TaskActivity.this);
        if(return_value == false)
            return return_value;

        //validating the value of snooze time is between 1 to 20 or not
        if(spnrepeat.getSelectedItemPosition() != 0)
        {
            if (snoozeTime > 0 && snoozeTime <= 20)
                return_value = true;
            else {
                snooze.setError("Enter Input between 1-20");
                return_value = false;
            }
        }
        else
        {
            return_value = true;
        }
        return return_value;
    }

    //onClick for Select Date
    @Override
    public void onClick(View v) {

        if (v == buttonDate) {
            mYear = datetime.YYYY;
            mMonth = datetime.MM - 1;
            mDay = datetime.DD;
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            datetime.setDate(year,monthOfYear + 1,dayOfMonth,TaskActivity.this); //assigning date to DateTime object
                            date.setText(String.format("%02d/%02d/%04d", dayOfMonth, monthOfYear + 1, year));
                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }//onClick for Select Time
        else if(v == buttonTime){
            mHour = datetime.hh;
            mMinute = datetime.mm;
            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            datetime.setTime(hourOfDay,minute,TaskActivity.this); //assigning time to DateTime object
                            int hour = hourOfDay % 12;
                            time.setText(String.format("%02d:%02d %s", hour == 0 ? 12 : hour, minute, hourOfDay < 12 ? "am" : "pm"));
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }
    }

    //Enabling or disabling Snooze EditText depending on Spinner
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(position != 0 )
            snooze.setEnabled(true);
        else {
            snooze.setEnabled(false);
            snooze.setText("0");
        }
    }

    //Called even first Spinner is not called
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
            snooze.setEnabled(false);
    }
}
