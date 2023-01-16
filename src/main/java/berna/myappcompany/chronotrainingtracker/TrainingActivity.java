package berna.myappcompany.chronotrainingtracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class TrainingActivity extends AppCompatActivity {

    private RadioGroup styleSelection;
    private RadioGroup sportSelection;
    private TextView styleSelectionText;
    private Spinner distanceSpinner;

    public void openDialog (){
        TrainingDialog warning = new TrainingDialog();
        warning.show(getSupportFragmentManager(), "warning dialog");
    }

    public void startTracking(View view) {
        EditText editDistance = (EditText) findViewById(R.id.editDistance); //Getting the distance text
        String sEditDistance = editDistance.getText().toString();

        if (sEditDistance.matches("")) //If the distance is not entered
            openDialog();

        else { //else, pass all the info
            float enteredDistance = Float.valueOf(sEditDistance); //If is not empty, get the float value

            Intent newIntent = new Intent(TrainingActivity.this, ChronometerActivity.class); //New intent
            Intent oldIntent = getIntent(); //Getting athlete id
            int id = oldIntent.getIntExtra("id", 0); //Save it

            String distance = distanceSpinner.getSelectedItem().toString(); //Getting km or m

            int checkedSportButton = sportSelection.getCheckedRadioButtonId(); //Get selected sport
            RadioButton checkedSport = findViewById(checkedSportButton); //Get the selected radio button
            String selectedSport = checkedSport.getText().toString(); //Getting the button text


            if (selectedSport.equals("Swimming")) {  //If swim is selected, need to check which style
                int checkedButtonStyle = styleSelection.getCheckedRadioButtonId(); //Checked style id
                RadioButton checkedSportStyle = findViewById(checkedButtonStyle); //Getting the radio button
                String selectedStyle = checkedSportStyle.getText().toString(); //Getting style text

                newIntent.putExtra("style", selectedStyle); //Send style
            }

            newIntent.putExtra("id", id); //Send id
            newIntent.putExtra("unit", distance); //Send unit
            newIntent.putExtra("sport", selectedSport); //Send sport
            newIntent.putExtra("distance", enteredDistance); //Send distance

            startActivity(newIntent);
        }
    }

    //Called when selecting swimming
    public void showStyleSelection(boolean checked) {
        if (checked) { //If swim is checked
            styleSelection.setVisibility(View.VISIBLE); //Style selection is visible
            styleSelectionText.setVisibility(View.VISIBLE);
            styleSelection.setClickable(true); //And also clickable
        } else {
            styleSelection.setVisibility(View.INVISIBLE); //Style selection is not visible
            styleSelectionText.setVisibility((View.INVISIBLE));
            styleSelection.setClickable(false); //And also not clickable
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Hide notification bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_training);

        //Toolbar
        Toolbar toolbar = findViewById(R.id.trainingToolbar);
        setSupportActionBar(toolbar);

        //Back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        distanceSpinner = findViewById(R.id.distanceUnit); //Unit spinner
        ArrayAdapter<CharSequence> distanceAdapter = ArrayAdapter.createFromResource(this, R.array.distance_unit,
                android.R.layout.simple_spinner_item); //Adapter for the spinner
        distanceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); //Setting th dropdown
        distanceSpinner.setAdapter(distanceAdapter); //Linking the adapter with the spinner


        //Getting the sport selection radio group
        sportSelection = (RadioGroup) findViewById(R.id.sportSelection);
        sportSelection.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                View selectedButton = sportSelection.findViewById(checkedId);
                int index = sportSelection.indexOfChild(selectedButton);

                if (index == 0 || index == 1) //If run or bike is selected
                    showStyleSelection(false); //Don't show style selection
                else
                    showStyleSelection(true); //Show style selection
            }
        });

        //Getting the second radio group, the one that will show/hide
        styleSelection = (RadioGroup) findViewById(R.id.styleSelection);
        styleSelectionText = (TextView) findViewById(R.id.selectSwimmingStyleText);

        showStyleSelection(false);
    }

    //Back button
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
