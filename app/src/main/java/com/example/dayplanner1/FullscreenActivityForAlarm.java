package com.example.dayplanner1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;


public class FullscreenActivityForAlarm extends AppCompatActivity {

    ImageView imageView;
    TextView textViewTask,textViewTime;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    DatabaseHelper mydb;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);   //to remove title bar from activity
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN); //to get full screen layout
        setContentView(R.layout.activity_fullscreen_for_alarm);

        mydb = GlobalDataHelper.returnDatabase();

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        String taskString = preferences.getString("Task", "Task Not Found!!!");
        String timeString = preferences.getString("Time", "Time Not Found!!!");

        //findViewById
        textViewTask = findViewById(R.id.tvTask);
        textViewTime = findViewById(R.id.tvTime);
        imageView = findViewById(R.id.imageView);
        textViewTask.setText(taskString);
        textViewTime.setText(timeString);
        String gifUrl = "https://media2.giphy.com/media/xT1XGLSb5E1VjIUw4E/giphy.gif?cid=ecf05e470da9c1e810efc717a0a15c29ae3aaa7921bf52f2&rid=giphy.gif";
        Glide.with(this).load(gifUrl).into(imageView);
    }
    @Override //When user cancels alarm it will come here
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {

            //close application completely
            MyAlarm myAlarm = GlobalDataHelper.returnMyAlarm();
            myAlarm.cancelAlarm(); //cancels alarm to Android alarm object
            mydb.updateCurrentAlarmTask(this); //update current alarm time (delete if repetition is 0)
            myAlarm.refreshAlarm(this); //refresh alarm
            this.finishAffinity(); //finish activity
            return;
        }
        this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Tap again to exit", Toast.LENGTH_SHORT).show();
    }
}


