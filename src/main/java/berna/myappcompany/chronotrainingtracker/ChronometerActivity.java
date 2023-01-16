package berna.myappcompany.chronotrainingtracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class ChronometerActivity extends AppCompatActivity {

    private Chronometer chronometer;
    private FloatingActionButton startStop;
    private boolean running;
    private long pauseOffset;
    private MaterialButton reset;
    private MaterialButton lapSave;
    private DBHelper database;
    private ArrayList<String> lapList;
    private String startTime;
    private String stopTime;
    private boolean activityStarted = false;
    private int lapNumber = 1;



    public void startTime (View view){
        reset.setVisibility(View.VISIBLE); //reset button will appear
        lapSave.setVisibility(View.VISIBLE); //lap button will appear
        reset.setClickable(true); //making button clickable
        lapSave.setClickable(true); //same

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(); //Date format
        simpleDateFormat.applyPattern("HH:mm:ss"); //Only time
        if (!activityStarted) { //First time clicking the button
            startTime = simpleDateFormat.format(new Date()); //Getting the time
            activityStarted = true;
        }

        if (!running){
            chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset); //getting the right time
            chronometer.start(); //start text
            running = true; //now is running
            startStop.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_pause)); //changing button's icon
            lapSave.setText("LAP"); //change button text
        }
        else {
            chronometer.stop(); //stopping text
            pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase(); //setting the correct offset
            running = false; //now isn't running
            startStop.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_start)); //changing button's icon
            lapSave.setText("SAVE"); //change button text
            stopTime = simpleDateFormat.format(new Date()); //Stop time
        }
    }

    public void resetTime (View view){
        chronometer.setBase(SystemClock.elapsedRealtime()); //resetting time
        pauseOffset = 0; //resetting offset

        if (running) {
            chronometer.stop(); //stopping text
            startStop.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_start)); //change icon
            running = false; //now isn't running
        }

        reset.setVisibility(View.INVISIBLE); //reset invisible
        lapSave.setVisibility(View.INVISIBLE); //lap invisible
        reset.setClickable(false); //button not clickable
        lapSave.setClickable(false); //same
        startTime = null; //resetting starting time
        activityStarted = false;
        lapList.clear(); //Clearing the laps
        lapNumber = 1; //Resetting laps
    }

    public void setLapSave (View view){
        long elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
        String lap;

        if (running){
            //Getting elapsed time, should auto reset when click on reset
            DateFormat formatter = new SimpleDateFormat("HH:mm:ss:SS", Locale.ITALY);
            formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
            lap = formatter.format(new Date(elapsedMillis));

            lapList.add(lap);

            Toast toast = Toast.makeText(this, "LAP#" + lapNumber + ": " + lap, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER_HORIZONTAL, 0 ,0);
            toast.show();
            lapNumber++;
        }
        else {
            Intent intent = getIntent();
            int athleteID = intent.getIntExtra("id", 0); //Athlete id
            String distanceUnit = intent.getStringExtra("unit"); //Km o m
            String sport = intent.getStringExtra("sport"); //Which sport
            String style = null;

            float distance = 0; //Distance
            float speed = 0; //Speed

            //Checking if sport is swimming
            if (sport.equals("Swimming"))
                style = intent.getStringExtra("style"); //swimming style

            if (distanceUnit.equals("m")){
                distance = intent.getFloatExtra("distance", 0); //entered distance
                speed = distance / (elapsedMillis / 1000); //distance by time equals speed!
                speed = Math.round(speed);
            }
            else if (distanceUnit.equals("Km")){
                distance = intent.getFloatExtra("distance", 0); //entered distance
                speed = ((distance * 1000) / (elapsedMillis / 1000)) * 3.6f; //Km/h conversion
                speed = Math.round(speed); //Rounding the value
            }

            try {
                SimpleDateFormat dateFormatter = new SimpleDateFormat();
                dateFormatter.applyPattern("dd/MM/yyyy");
                String date = dateFormatter.format(new Date());

                long sessionID = database.recordSession(athleteID, startTime, stopTime, distance, distanceUnit, speed, sport, style, date); //Insert query

                if (lapList.size() != 0) {
                    int i = 0;
                    do {
                        boolean lapRecorded = database.recordLaps(sessionID, lapList.get(i)); //saving all laps
                        if (!lapRecorded)
                            Toast.makeText(this, "Lap Not Recorded!!!", Toast.LENGTH_SHORT).show();
                        i++; //increase counter
                    } while (i < lapList.size());
                }
            } catch (Exception e){
                e.printStackTrace();
            }

            Toast.makeText(this, "SESSION RECORDED!!", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Hide notification bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_chronometer);

        Toolbar toolbar = findViewById(R.id.toolbarChrono);
        setSupportActionBar(toolbar);

        //Back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Chronometer and buttons
        chronometer = findViewById(R.id.chronometer);
        startStop = findViewById(R.id.startStop);
        reset = findViewById(R.id.reset);
        lapSave = findViewById(R.id.lap);

        //Database
        database = new DBHelper(this);

        //Lap list
        lapList = new ArrayList<>();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
