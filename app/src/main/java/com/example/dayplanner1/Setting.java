package com.example.dayplanner1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class Setting extends AppCompatActivity {

    Dialog myDialog;
    TextView tvWhite, tvBlack, tvRingtone, tvAlarmClock, tvBeep, tvChildhood, tvMix, tvMorning, tvSunrise;
    ImageView ar_iv1, ar_iv2, r_iv1, r_iv2, r_iv3, r_iv4, r_iv5, r_iv6;
    Switch sw1,sw2,sw3;
    int ringtone;
    static MediaPlayer ring;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        myDialog = new Dialog(this);
        ringtone = 0;
        tvRingtone = findViewById(R.id.tvRingtoneDes);
        ring = null;
        sw1 = findViewById(R.id.sw1);
        sw2 = findViewById(R.id.sw2);
        sw3 = findViewById(R.id.sw3);

        //Custom ActionBar for close Vector
        Toolbar toolbar=findViewById(R.id.toolbarSetting);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setTitle("Settings");
        setSupportActionBar(toolbar);

        //code to redirect to Home Page on Cancel Button
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Setting.super.finish();
            }
        });

        //checks we selected ringtone so it gets name from shared preferences
        SharedPreferences spRing = getSharedPreferences("Ringtone",MODE_PRIVATE);
        String strRing = spRing.getString("Name","");
        if(strRing.isEmpty()) {
            tvRingtone.setText("Alarm Clock");
            saveRing("Alarm Clock");
        }
        else
            tvRingtone.setText(strRing);

        //checks notification is on or off
        SharedPreferences spNotification = getSharedPreferences("Notification",MODE_PRIVATE);
        String strNotification = spNotification.getString("Value","");
        if(strNotification.equals("true"))
            sw1.setChecked(true);
        else
            sw1.setChecked(false);

        //checks vibration is on or off
        SharedPreferences spVibration = getSharedPreferences("Vibration",MODE_PRIVATE);
        String strVibration = spVibration.getString("Value","");
        if(strVibration.equals("false"))
            sw2.setChecked(false);
        else
            sw2.setChecked(true);

        //checks shutdown is on or off
        SharedPreferences spShutdown = getSharedPreferences("Shutdown",MODE_PRIVATE);
        String strShutdown = spShutdown.getString("Value","");
        if(strShutdown.equals("true"))
            sw3.setChecked(true);
        else
            sw3.setChecked(false);

        //Notification Switch
        sw1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //what should do if switch on
                    SharedPreferences sharedPreferences = getSharedPreferences("Notification",MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("Value","true");
                    editor.commit();

                    //Coding to send notification
                    /*Intent intent = new Intent();
                    PendingIntent pIntent = PendingIntent.getActivity(Setting.this,0,intent,0);
                    Notification noti = new Notification.Builder(Setting.this)
                            .setTicker("TickerTitle")
                            .setContentTitle("Content Title")
                            .setContentText("Content Text")
                            .setSmallIcon(R.drawable.ic_launcher_background)
                            .setContentIntent(pIntent).getNotification();
                    noti.flags = Notification.FLAG_AUTO_CANCEL;
                    NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                    nm.notify(0,noti);*/
                } else {
                    //what should do if switch off
                    SharedPreferences sharedPreferences = getSharedPreferences("Notification",MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("Value","false");
                    editor.commit();
                }
            }
        });

        //Vibration Switch
        sw2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //what should do if switch on
                    SharedPreferences sharedPreferences = getSharedPreferences("Vibration",MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("Value","true");
                    editor.commit();

                    //Coding to vibrate the phone
                    /*Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                    if(Build.VERSION.SDK_INT >= 26)
                        vibrator.vibrate(VibrationEffect.createOneShot(200,VibrationEffect.DEFAULT_AMPLITUDE));
                    else
                        vibrator.vibrate(200);*/
                } else {
                    //what should do if switch off
                    SharedPreferences sharedPreferences = getSharedPreferences("Vibration",MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("Value","false");
                    editor.commit();
                }
            }
        });

        //Shutdown Switch
        sw3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //what should do if switch on
                    SharedPreferences sharedPreferences = getSharedPreferences("Shutdown",MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("Value","true");
                    editor.commit();
                } else {
                    //what should do if switch off
                    SharedPreferences sharedPreferences = getSharedPreferences("Shutdown",MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("Value","false");
                    editor.commit();
                }
            }
        });
    }

    //When click on theme color popup will be shown
    public void changeTheme(View view) {
        myDialog.setContentView(R.layout.popup_theme);
        tvWhite = myDialog.findViewById(R.id.theme_tvWhite);
        tvBlack = myDialog.findViewById(R.id.theme_tvBlack);
        ar_iv1 = myDialog.findViewById(R.id.ar_iv1);
        ar_iv2 = myDialog.findViewById(R.id.ar_iv2);
        myDialog.getWindow().setBackgroundDrawableResource(R.drawable.popup_bk);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        myDialog.getWindow().setLayout(width-35, RelativeLayout.LayoutParams.WRAP_CONTENT);
        myDialog.getWindow().setGravity(Gravity.BOTTOM);
        checkTheme();
        myDialog.setCancelable(true);
        myDialog.setCanceledOnTouchOutside(true);
        myDialog.show();
    }

    //When we click pick ringtone
    public void onRingtone(View view) {
        myDialog.setContentView(R.layout.popup_ringtone);
        tvAlarmClock = myDialog.findViewById(R.id.tvAlarmClock);
        tvBeep = myDialog.findViewById(R.id.tvBeep);
        tvChildhood = myDialog.findViewById(R.id.tvChildhood);
        tvMix = myDialog.findViewById(R.id.tvMix);
        tvMorning = myDialog.findViewById(R.id.tvMorning);
        tvSunrise = myDialog.findViewById(R.id.tvSunrise);

        r_iv1 = myDialog.findViewById(R.id.r_iv1);
        r_iv2 = myDialog.findViewById(R.id.r_iv2);
        r_iv3 = myDialog.findViewById(R.id.r_iv3);
        r_iv4 = myDialog.findViewById(R.id.r_iv4);
        r_iv5 = myDialog.findViewById(R.id.r_iv5);
        r_iv6 = myDialog.findViewById(R.id.r_iv6);

        myDialog.getWindow().setBackgroundDrawableResource(R.drawable.popup_bk);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        myDialog.getWindow().setLayout(width-35, RelativeLayout.LayoutParams.WRAP_CONTENT);
        myDialog.getWindow().setGravity(Gravity.BOTTOM);

        //checks we selected ringtone so it gets name from shared preferences
        checkRingtone();
        myDialog.setCancelable(false);
        myDialog.setCanceledOnTouchOutside(false);
        myDialog.show();
    }

    //checks current ringtone
    public void checkRingtone() {
        SharedPreferences sharedPreferences = getSharedPreferences("Ringtone",MODE_PRIVATE);
        String s = sharedPreferences.getString("Name","");
        if(s.equals("Alarm Clock")) {
            r_iv1.setVisibility(View.VISIBLE);
            r_iv2.setVisibility(View.INVISIBLE);
            r_iv3.setVisibility(View.INVISIBLE);
            r_iv4.setVisibility(View.INVISIBLE);
            r_iv5.setVisibility(View.INVISIBLE);
            r_iv6.setVisibility(View.INVISIBLE);

            tvAlarmClock.setTextColor(getResources().getColor(R.color.LightBlue));
            tvBeep.setTextColor(getResources().getColor(R.color.Black));
            tvChildhood.setTextColor(getResources().getColor(R.color.Black));
            tvMix.setTextColor(getResources().getColor(R.color.Black));
            tvMorning.setTextColor(getResources().getColor(R.color.Black));
            tvSunrise.setTextColor(getResources().getColor(R.color.Black));
            ringtone = 1;
        }
        else if (s.equals("Beep")) {
            r_iv1.setVisibility(View.INVISIBLE);
            r_iv2.setVisibility(View.VISIBLE);
            r_iv3.setVisibility(View.INVISIBLE);
            r_iv4.setVisibility(View.INVISIBLE);
            r_iv5.setVisibility(View.INVISIBLE);
            r_iv6.setVisibility(View.INVISIBLE);

            tvAlarmClock.setTextColor(getResources().getColor(R.color.Black));
            tvBeep.setTextColor(getResources().getColor(R.color.LightBlue));
            tvChildhood.setTextColor(getResources().getColor(R.color.Black));
            tvMix.setTextColor(getResources().getColor(R.color.Black));
            tvMorning.setTextColor(getResources().getColor(R.color.Black));
            tvSunrise.setTextColor(getResources().getColor(R.color.Black));
            ringtone = 2;
        }
        else if (s.equals("Childhood")) {
            r_iv1.setVisibility(View.INVISIBLE);
            r_iv2.setVisibility(View.INVISIBLE);
            r_iv3.setVisibility(View.VISIBLE);
            r_iv4.setVisibility(View.INVISIBLE);
            r_iv5.setVisibility(View.INVISIBLE);
            r_iv6.setVisibility(View.INVISIBLE);

            tvAlarmClock.setTextColor(getResources().getColor(R.color.Black));
            tvBeep.setTextColor(getResources().getColor(R.color.Black));
            tvChildhood.setTextColor(getResources().getColor(R.color.LightBlue));
            tvMix.setTextColor(getResources().getColor(R.color.Black));
            tvMorning.setTextColor(getResources().getColor(R.color.Black));
            tvSunrise.setTextColor(getResources().getColor(R.color.Black));
            ringtone = 3;
        }
        else if (s.equals("Mix")) {
            r_iv1.setVisibility(View.INVISIBLE);
            r_iv2.setVisibility(View.INVISIBLE);
            r_iv3.setVisibility(View.INVISIBLE);
            r_iv4.setVisibility(View.VISIBLE);
            r_iv5.setVisibility(View.INVISIBLE);
            r_iv6.setVisibility(View.INVISIBLE);

            tvAlarmClock.setTextColor(getResources().getColor(R.color.Black));
            tvBeep.setTextColor(getResources().getColor(R.color.Black));
            tvChildhood.setTextColor(getResources().getColor(R.color.Black));
            tvMix.setTextColor(getResources().getColor(R.color.LightBlue));
            tvMorning.setTextColor(getResources().getColor(R.color.Black));
            tvSunrise.setTextColor(getResources().getColor(R.color.Black));
            ringtone = 4;
        }
        else if (s.equals("Morning")) {
            r_iv1.setVisibility(View.INVISIBLE);
            r_iv2.setVisibility(View.INVISIBLE);
            r_iv3.setVisibility(View.INVISIBLE);
            r_iv4.setVisibility(View.INVISIBLE);
            r_iv5.setVisibility(View.VISIBLE);
            r_iv6.setVisibility(View.INVISIBLE);

            tvAlarmClock.setTextColor(getResources().getColor(R.color.Black));
            tvBeep.setTextColor(getResources().getColor(R.color.Black));
            tvChildhood.setTextColor(getResources().getColor(R.color.Black));
            tvMix.setTextColor(getResources().getColor(R.color.Black));
            tvMorning.setTextColor(getResources().getColor(R.color.LightBlue));
            tvSunrise.setTextColor(getResources().getColor(R.color.Black));
            ringtone = 5;
        }
        else if (s.equals("Sunrise")) {
            r_iv1.setVisibility(View.INVISIBLE);
            r_iv2.setVisibility(View.INVISIBLE);
            r_iv3.setVisibility(View.INVISIBLE);
            r_iv4.setVisibility(View.INVISIBLE);
            r_iv5.setVisibility(View.INVISIBLE);
            r_iv6.setVisibility(View.VISIBLE);

            tvAlarmClock.setTextColor(getResources().getColor(R.color.Black));
            tvBeep.setTextColor(getResources().getColor(R.color.Black));
            tvChildhood.setTextColor(getResources().getColor(R.color.Black));
            tvMix.setTextColor(getResources().getColor(R.color.Black));
            tvMorning.setTextColor(getResources().getColor(R.color.Black));
            tvSunrise.setTextColor(getResources().getColor(R.color.LightBlue));
            ringtone = 6;
        }
    }

    //click cancel on ringtone popup
    public void onClickCancel(View view) {
        checkRingtone();
        if(ring!=null && ring.isPlaying()){
            ring.stop();
        }
        myDialog.dismiss();
    }

    //select ringtone by press ok from popup
    public void onSelectRing(View view) {
        if (ringtone == 1)
            saveRing("Alarm Clock");
        else if (ringtone == 2)
            saveRing("Beep");
        else if (ringtone == 3)
            saveRing("Childhood");
        else if (ringtone == 4)
            saveRing("Mix");
        else if (ringtone == 5)
            saveRing("Morning");
        else if (ringtone == 6)
            saveRing("Sunrise");
        else
            saveRing("Alarm Clock");
        if(ring!=null && ring.isPlaying()){
            ring.stop();
        }
        myDialog.dismiss();
    }

    //Saving Ringtone to shared preferences
    public void saveRing(String st) {
        SharedPreferences sharedPreferences = getSharedPreferences("Ringtone",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Name",st);
        editor.commit();
        tvRingtone.setText(st);
    }

    //when we select ringtone alarm clock
    public void onRingtoneAlarmClock(View view) {
        r_iv1.setVisibility(View.VISIBLE);
        r_iv2.setVisibility(View.INVISIBLE);
        r_iv3.setVisibility(View.INVISIBLE);
        r_iv4.setVisibility(View.INVISIBLE);
        r_iv5.setVisibility(View.INVISIBLE);
        r_iv6.setVisibility(View.INVISIBLE);

        tvAlarmClock.setTextColor(getResources().getColor(R.color.LightBlue));
        tvBeep.setTextColor(getResources().getColor(R.color.Black));
        tvChildhood.setTextColor(getResources().getColor(R.color.Black));
        tvMix.setTextColor(getResources().getColor(R.color.Black));
        tvMorning.setTextColor(getResources().getColor(R.color.Black));
        tvSunrise.setTextColor(getResources().getColor(R.color.Black));
        ringtone = 1;
        if(ring!=null && ring.isPlaying()){
            ring.stop();
        }
        ring = MediaPlayer.create(Setting.this, R.raw.alarm_clock);
        ring.start();
    }

    //when we select ringtone beep
    public void onRingtoneBeep(View view) {
        r_iv1.setVisibility(View.INVISIBLE);
        r_iv2.setVisibility(View.VISIBLE);
        r_iv3.setVisibility(View.INVISIBLE);
        r_iv4.setVisibility(View.INVISIBLE);
        r_iv5.setVisibility(View.INVISIBLE);
        r_iv6.setVisibility(View.INVISIBLE);

        tvAlarmClock.setTextColor(getResources().getColor(R.color.Black));
        tvBeep.setTextColor(getResources().getColor(R.color.LightBlue));
        tvChildhood.setTextColor(getResources().getColor(R.color.Black));
        tvMix.setTextColor(getResources().getColor(R.color.Black));
        tvMorning.setTextColor(getResources().getColor(R.color.Black));
        tvSunrise.setTextColor(getResources().getColor(R.color.Black));
        ringtone = 2;
        if(ring!=null && ring.isPlaying()){
            ring.stop();
        }
        ring = MediaPlayer.create(Setting.this, R.raw.beep);
        ring.start();
    }

    //when we select ringtone childhood
    public void onRingtoneChildhood(View view) {
        r_iv1.setVisibility(View.INVISIBLE);
        r_iv2.setVisibility(View.INVISIBLE);
        r_iv3.setVisibility(View.VISIBLE);
        r_iv4.setVisibility(View.INVISIBLE);
        r_iv5.setVisibility(View.INVISIBLE);
        r_iv6.setVisibility(View.INVISIBLE);

        tvAlarmClock.setTextColor(getResources().getColor(R.color.Black));
        tvBeep.setTextColor(getResources().getColor(R.color.Black));
        tvChildhood.setTextColor(getResources().getColor(R.color.LightBlue));
        tvMix.setTextColor(getResources().getColor(R.color.Black));
        tvMorning.setTextColor(getResources().getColor(R.color.Black));
        tvSunrise.setTextColor(getResources().getColor(R.color.Black));
        ringtone = 3;
        if(ring!=null && ring.isPlaying()){
            ring.stop();
        }
        ring = MediaPlayer.create(Setting.this, R.raw.childhood);
        ring.start();
    }

    //when we select ringtone mix
    public void onRingtoneMix(View view) {
        r_iv1.setVisibility(View.INVISIBLE);
        r_iv2.setVisibility(View.INVISIBLE);
        r_iv3.setVisibility(View.INVISIBLE);
        r_iv4.setVisibility(View.VISIBLE);
        r_iv5.setVisibility(View.INVISIBLE);
        r_iv6.setVisibility(View.INVISIBLE);

        tvAlarmClock.setTextColor(getResources().getColor(R.color.Black));
        tvBeep.setTextColor(getResources().getColor(R.color.Black));
        tvChildhood.setTextColor(getResources().getColor(R.color.Black));
        tvMix.setTextColor(getResources().getColor(R.color.LightBlue));
        tvMorning.setTextColor(getResources().getColor(R.color.Black));
        tvSunrise.setTextColor(getResources().getColor(R.color.Black));
        ringtone = 4;
        if(ring!=null && ring.isPlaying()){
            ring.stop();
        }
        ring = MediaPlayer.create(Setting.this, R.raw.mix);
        ring.start();
    }

    //when we select ringtone morning
    public void onRingtoneMorning(View view) {
        r_iv1.setVisibility(View.INVISIBLE);
        r_iv2.setVisibility(View.INVISIBLE);
        r_iv3.setVisibility(View.INVISIBLE);
        r_iv4.setVisibility(View.INVISIBLE);
        r_iv5.setVisibility(View.VISIBLE);
        r_iv6.setVisibility(View.INVISIBLE);

        tvAlarmClock.setTextColor(getResources().getColor(R.color.Black));
        tvBeep.setTextColor(getResources().getColor(R.color.Black));
        tvChildhood.setTextColor(getResources().getColor(R.color.Black));
        tvMix.setTextColor(getResources().getColor(R.color.Black));
        tvMorning.setTextColor(getResources().getColor(R.color.LightBlue));
        tvSunrise.setTextColor(getResources().getColor(R.color.Black));
        ringtone = 5;
        if(ring!=null && ring.isPlaying()){
            ring.stop();
        }
        ring = MediaPlayer.create(Setting.this, R.raw.morning);
        ring.start();
    }

    //when we select ringtone sunrise
    public void onRingtoneSunrise(View view) {
        r_iv1.setVisibility(View.INVISIBLE);
        r_iv2.setVisibility(View.INVISIBLE);
        r_iv3.setVisibility(View.INVISIBLE);
        r_iv4.setVisibility(View.INVISIBLE);
        r_iv5.setVisibility(View.INVISIBLE);
        r_iv6.setVisibility(View.VISIBLE);

        tvAlarmClock.setTextColor(getResources().getColor(R.color.Black));
        tvBeep.setTextColor(getResources().getColor(R.color.Black));
        tvChildhood.setTextColor(getResources().getColor(R.color.Black));
        tvMix.setTextColor(getResources().getColor(R.color.Black));
        tvMorning.setTextColor(getResources().getColor(R.color.Black));
        tvSunrise.setTextColor(getResources().getColor(R.color.LightBlue));
        ringtone = 6;
        if(ring!=null && ring.isPlaying()){
            ring.stop();
        }
        ring = MediaPlayer.create(Setting.this, R.raw.sunrise);
        ring.start();
    }

    //When we click notification switch
    public void onNotification(View view) {
        if(!sw1.isChecked())
            sw1.setChecked(true);
        else
            sw1.setChecked(false);
    }

    //When we click Ascending Volume switch
    public void onVibration(View view) {
        if(!sw2.isChecked())
            sw2.setChecked(true);
        else
            sw2.setChecked(false);
    }

    //When we click notification switch
    public void onShutdown(View view) {
        if(!sw3.isChecked())
            sw3.setChecked(true);
        else
            sw3.setChecked(false);
    }

    //When we click white from theme popup
    public void onThemeWhite(View view) {
        //saving theme white value
        SharedPreferences sharedPreferences = getSharedPreferences("Theme",MODE_PRIVATE);
        String string = sharedPreferences.getString("Value","");
        if(string.equals("black")) {
            saveTheme("white");
        } else if (string.isEmpty()) {
            saveTheme("white");
        }
        ar_iv1.setVisibility(View.VISIBLE);
        ar_iv2.setVisibility(View.INVISIBLE);
        tvWhite.setTextColor(getResources().getColor(R.color.LightBlue));
        tvBlack.setTextColor(getResources().getColor(R.color.Black));
        myDialog.dismiss();
    }

    //When we click black from theme popup
    public void onThemeBlack(View view) {
        //saving theme black value
        SharedPreferences sharedPreferences = getSharedPreferences("Theme",MODE_PRIVATE);
        String string = sharedPreferences.getString("Value","");
        if(string.equals("white")) {
            saveTheme("black");
        }
        ar_iv1.setVisibility(View.INVISIBLE);
        ar_iv2.setVisibility(View.VISIBLE);
        tvWhite.setTextColor(getResources().getColor(R.color.Black));
        tvBlack.setTextColor(getResources().getColor(R.color.LightBlue));
        myDialog.dismiss();
    }

    //Saving Ringtone to shared preferences
    public void saveTheme(String st) {
        SharedPreferences sharedPreferences = getSharedPreferences("Theme",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Value",st);
        editor.commit();
    }

    //checks current ringtone
    public void checkTheme() {
        SharedPreferences sharedPreferences = getSharedPreferences("Theme",MODE_PRIVATE);
        String ctString = sharedPreferences.getString("Value","");
        if(ctString.equals("white")) {
            ar_iv1.setVisibility(View.VISIBLE);
            ar_iv2.setVisibility(View.INVISIBLE);
            tvWhite.setTextColor(getResources().getColor(R.color.LightBlue));
            tvBlack.setTextColor(getResources().getColor(R.color.Black));
        }
        else if(ctString.equals("black")) {
            ar_iv1.setVisibility(View.INVISIBLE);
            ar_iv2.setVisibility(View.VISIBLE);
            tvWhite.setTextColor(getResources().getColor(R.color.Black));
            tvBlack.setTextColor(getResources().getColor(R.color.LightBlue));
        }
        else if(ctString.isEmpty())
            saveTheme("white");
    }

    //When we click cancel from popup
    public void onCancel(View view) {
        myDialog.dismiss();
    }
}