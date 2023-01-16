package berna.myappcompany.chronotrainingtracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class InsertAthleteActivity extends AppCompatActivity {

    DBHelper database;

    //Insert athlete method
    public void insertAthlete(View view){
        //Getting name
        EditText nameEdit = (EditText) findViewById(R.id.editName);
        String name = nameEdit.getText().toString();

        //Getting surname
        EditText surnameEditText = (EditText) findViewById(R.id.editSurname);
        String surname = surnameEditText.getText().toString();

        //Getting Sport
        EditText sportEditText = (EditText) findViewById(R.id.editSport);
        String sport = sportEditText.getText().toString();

        try {
            boolean isInserted = database.addAthlete(name, surname, sport);

            if (isInserted){
                Toast.makeText(this, "Athlete Added!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Athlete Not Added!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Hide notification bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_insert_athlete);

        Toolbar toolbar = findViewById(R.id.athleteToolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        database = new DBHelper(this);
    }

    //Back button
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
