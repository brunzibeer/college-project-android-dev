package berna.myappcompany.chronotrainingtracker;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SessionsFragment extends Fragment {

    private View view;
    private ListView listView;
    private DBHelper database;

    public SessionsFragment() {
        //Required empty public constructor
    }

    public void showList() {
        database = new DBHelper(getContext());
        Cursor result = database.getAllSession();
        result.moveToFirst();

        ArrayList<String> sessions = new ArrayList<>();
        listView = (ListView) view.findViewById(R.id.sessionList);

        if (result.getCount() == 0){
            Toast.makeText(getContext(), "NO SESSION TRACKED!", Toast.LENGTH_LONG).show();
        }
        else {
            StringBuffer buffer = new StringBuffer();

            do {
                buffer.append(result.getString(0) + ": ");
                buffer.append(result.getString(11) + " " + result.getString(12) + ", ");
                buffer.append(result.getString(8) + " - ");
                buffer.append(result.getString(6));

                sessions.add(buffer.toString());
                buffer.delete(0, buffer.length());
            } while (result.moveToNext());

            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, sessions);
            listView.setAdapter(adapter);
        }
    }

    public void exportSelectedSession(String id){
        database = new DBHelper(getContext());
        Cursor result = database.getExtendedSessionAndAthletes(id);
        result.moveToFirst();

        try {
            String csv = getActivity().getExternalFilesDir(null) + "/one_session.csv";
            CSVWriter writer = new CSVWriter(new FileWriter(csv));

            String[] array = new String[12];

            array[0] = result.getString(0); //Getting all the info
            array[1] = result.getString(1);
            array[2] = result.getString(2);
            array[3] = result.getString(3);
            array[4] = result.getString(4);
            array[5] = result.getString(5);
            array[6] = result.getString(6);
            array[7] = result.getString(7);
            array[8] = result.getString(8);
            array[9] = result.getString(14);
            array[10] = result.getString(15);

            do {
                array[11] = result.getString(11);
            } while (result.moveToNext());

            writer.writeNext(array);

            writer.close();

            Toast.makeText(getContext(), "File created at: " + getActivity().getExternalFilesDir(null), Toast.LENGTH_LONG).show();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Create a view object to return
        view = inflater.inflate(R.layout.fragment_sessions, container, false);

        //Getting the listView
        listView = (ListView) view.findViewById(R.id.sessionList);

        //Showing session list
        showList();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String pos = String.valueOf(position+1);
                exportSelectedSession(pos);
            }
        });

        return view;
    }
}

