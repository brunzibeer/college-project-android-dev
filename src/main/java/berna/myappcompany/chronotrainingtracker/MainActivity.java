package berna.myappcompany.chronotrainingtracker;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.opencsv.CSVWriter;

import java.io.FileWriter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private static final int STORAGE_PERMISSION_CODE = 101; //Permission code

    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_DENIED) { //If is not granted
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode); //Request the permission
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(MainActivity.this, "PERMISSION GRANTED", Toast.LENGTH_SHORT).show(); //Letting know permission is granted
        }

    }

    public void exportAll(View view) {

        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE); //Checking permission for storage

        try {
            DBHelper database = new DBHelper(this); //Initializing the database
            Cursor result = database.getAllSession(); //Getting all the sessions
            result.moveToFirst();

            String csv = getExternalFilesDir(null) + "/all_session.csv"; //Path to save the file
            CSVWriter writer = new CSVWriter(new FileWriter(csv)); //opencsv library class

            String[] array = new String[11]; //Array passed as parameter

            do {
                array[0] = result.getString(0); //Getting all the info
                array[1] = result.getString(1);
                array[2] = result.getString(2);
                array[3] = result.getString(3);
                array[4] = result.getString(4);
                array[5] = result.getString(5);
                array[6] = result.getString(6);
                array[7] = result.getString(7);
                array[8] = result.getString(8);
                array[9] = result.getString(11);
                array[10] = result.getString(12);

                writer.writeNext(array); //Writing the line of file

            } while (result.moveToNext());

            writer.close(); //Close the file

            Toast.makeText(this, "File created at: " + getExternalFilesDir(null), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showSessionList(View view) {
        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE); //Checking permission for storage
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SessionsFragment()).addToBackStack(null).commit(); //Open session list

    }

    public void homeFragment(View view) {
        getSupportFragmentManager().popBackStack();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Drawer setup
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //this.deleteDatabase("CTT");

        //Starting page
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }

    }

    //OnNavigationItemSelected implementation to do something on menu items click
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit(); //Open home
                break;
            case R.id.nav_athlete:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AthletesFragment()).commit(); //Open athlete
                break;
            case R.id.nav_calendar:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CalendarFragment()).commit(); //Open calendar
                break;
            case R.id.nav_help:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HelpFragment()).commit(); //Open help
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
