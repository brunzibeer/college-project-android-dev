package berna.myappcompany.chronotrainingtracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class AthleteDetailsActivity extends AppCompatActivity {

    private DBHelper database;
    private ListView sessionList;
    private Intent oldIntent;
    private int selectedAthleteId;
    private String selectedSessionId;
    private ArrayList<String> sessions;

    public void getDetails (int id){
        Cursor result = database.getSelectedAthlete(id); //Gettin selected athlete info
        result.moveToFirst();

        TextView name = (TextView) findViewById(R.id.textNameDetail); //Getting name
        name.setText(result.getString(1));
        TextView surname = (TextView) findViewById(R.id.textSurnameDetail); //Surname
        surname.setText(result.getString(2));
        TextView sport = (TextView) findViewById(R.id.textSportDetail); //Sport
        sport.setText(result.getString(3));
    }

    public void sessionDetails (String sessionID){
        String detailMessage;
        Cursor details = database.getExtendedSession(sessionID); //Getting the tapped session details
        details.moveToFirst();

        int lapNumber = 1; //Lap counter

        if (details.getCount() == 0) {
            Toast.makeText(this, "Error loading details", Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            StringBuffer buffer = new StringBuffer();

            //Retrieving all data
            buffer.append("Session: " + details.getString(0) + "\n");
            buffer.append("Date: " + details.getString(8) + "\n");
            buffer.append("Start Time: " + details.getString(1) + "\n");
            buffer.append("Stop Time: " + details.getString(2) + "\n");
            buffer.append("Distance: " + details.getString(3) + " ");
            buffer.append(details.getString(4) + "\n");
            buffer.append("Speed: " + details.getString(5) + " ");

            if (details.getString(4).equals("m"))
                buffer.append("m/s\n");
            else
                buffer.append("Km/h\n");

            buffer.append("Sport: " + details.getString(6) + "\n");

            if (details.getString(6).equals("Swimming"))
                buffer.append("Style: " + details.getString(7) + "\n");

            if (details.getString(11) != null) { //Checking if there are laps recorded
                buffer.append("Laps: \n");

                do {
                    buffer.append("#" + lapNumber + ": " + details.getString(11) + "\n");
                    lapNumber++;
                } while (details.moveToNext());
            }

            detailMessage = buffer.toString(); //Setting the message
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this); //Creating the builder
        builder.setMessage(detailMessage)
                .setTitle("Session Details:") //Dialog title
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Close the dialog on click
                    }
                });

        AlertDialog detailDialog = builder.create(); //Create the dialog with builder
        detailDialog.show(); //Showing the dialog
    }

    public void getAthleteSession (int id){
        Cursor cursor = database.getAthleteSession(id); //Getting selected athlete sessions
        cursor.moveToFirst();
        sessions = new ArrayList<>(); //List to store all the sessions

        if (cursor.getCount() == 0) //If there's no session
            Toast.makeText(this, "No session for selected athlete!!", Toast.LENGTH_SHORT).show(); //Output that
        else { //else
            StringBuffer buffer = new StringBuffer(); //Create a buffer to store the final string
            do {
                buffer.append("Session: " + cursor.getString(0) + "\n");
                buffer.append(cursor.getString(8) + "\n"); //Date
                buffer.append(cursor.getString(1) + " - "); //Start time
                buffer.append(cursor.getString(2) + "\n"); //Stop time
                buffer.append(cursor.getString(6) + ": "); //Getting sport
                buffer.append(cursor.getString(3) + " " + cursor.getString(4)); //Getting distance
                buffer.append(", " + cursor.getString(5) + " ");

                if (cursor.getString(4).equals("m"))
                    buffer.append("m/s");
                else if (cursor.getString(4).equals("Km"))
                    buffer.append("Km/h");

                sessions.add(buffer.toString()); //Add info to the buffer
                buffer.delete(0, buffer.length()); //Resetting the buffer
            } while (cursor.moveToNext()); //Until there's a session to show

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, sessions); //Adapter for the listView
            sessionList.setAdapter(adapter); //Linking the adapter to the view
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { //Inflating training icon
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.toolbar_training, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home: //If back arrow is pressed
                onBackPressed(); //Go back
                break;
            case R.id.trainingButton: //If training is pressed
                oldIntent = getIntent(); //Get the selected athlete id
                int id = oldIntent.getIntExtra("id", 0); //Save it to a variable
                Intent trainingIntent = new Intent(AthleteDetailsActivity.this, TrainingActivity.class); //Creating new intent
                trainingIntent.putExtra("id", id); //Storing athlete id for next activity
                startActivity(trainingIntent); //Open new activity
                break;
            case R.id.refreshActivities:
                getAthleteSession(selectedAthleteId);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Hide notification bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_athlete_details);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        sessionList = findViewById(R.id.sessionList); //Session listView

        database = new DBHelper(this);

        oldIntent = getIntent(); //Getting athlete id
        selectedAthleteId = oldIntent.getIntExtra("id", 0); //Save it

        getDetails(selectedAthleteId); //Getting athlete details
        getAthleteSession(selectedAthleteId); //Getting athlete session

        sessionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedSessionId = sessions.get(position).split(":")[1].split("\n")[0].trim(); //Id of clicked session
                sessionDetails(selectedSessionId);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
