package berna.myappcompany.chronotrainingtracker;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AthletesFragment extends Fragment {

    private View view;
    private ListView listView;

    public AthletesFragment() {
        //Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Letting know it has an option menu
        setHasOptionsMenu(true);
    }

    //Let the add button be visible
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    //Method to show/refresh the listView
    public void refreshList (){
        DBHelper database = new DBHelper(getContext());//Initializing the DB
        Cursor result = database.getAllAthletes(); //Getting the athletes
        result.moveToFirst();

        ArrayList<String> athletes = new ArrayList<String>(); //Array to store athletes
        listView = (ListView) view.findViewById(R.id.athleteList);

        if (result.getCount() == 0) { //If there's no athlete
            Toast.makeText(getContext(), "No athletes found", Toast.LENGTH_SHORT).show(); //Tell the user
        } else {
            StringBuffer buffer = new StringBuffer(); //Buffer to store the string
            do {
                buffer.append(result.getString(0) + ": ");
                buffer.append(result.getString(1) + " ");
                buffer.append(result.getString(2) + ", ");
                buffer.append(result.getString(3));

                athletes.add(buffer.toString());
                buffer.delete(0, buffer.length());
            } while (result.moveToNext());

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, athletes);
            listView.setAdapter(arrayAdapter);
        }
    }

    //Handling the toolbar click
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.refresh:
                refreshList();
                break;
            case R.id.addAthlete:
                Intent intent = new Intent(getActivity(), InsertAthleteActivity.class);
                getActivity().startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Create a view object to return
        view = inflater.inflate(R.layout.fragment_athletes, container, false);

        //Showing the listView
        refreshList();

        //Getting the listView
        listView = (ListView) view.findViewById(R.id.athleteList);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), AthleteDetailsActivity.class);
                intent.putExtra("id", position + 1);
                getActivity().startActivity(intent);
            }
        });


        return view;
    }
}

