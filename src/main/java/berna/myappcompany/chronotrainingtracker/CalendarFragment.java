package berna.myappcompany.chronotrainingtracker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class CalendarFragment extends Fragment {

    private View view;
    private ListView listView;
    private ArrayList<String> session;
    private String clickedSessionId;
    private DBHelper database;

    public void refreshList (int month, int year, int dayOfMonth) {
        Cursor result = database.getSessionOnDay(month, year, dayOfMonth); //Getting the session on a specific day
        result.moveToFirst(); //Moving to the first result

        session = new ArrayList<>(); //Array to store sessions
        listView = (ListView) view.findViewById(R.id.dateSessionList);
        ArrayAdapter<String> sessionAdapter;

        if (result.getCount() == 0) {
            Toast.makeText(getContext(), "No session on this day", Toast.LENGTH_SHORT).show(); //Tell the user
            session.clear(); //Clear the list
            sessionAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, session);
            listView.setAdapter(sessionAdapter); //Clearing on screen
        }
        else {
            StringBuffer buffer = new StringBuffer(); //Buffer to store the string
            do {
                buffer.append("Session: " + result.getString(0) + "\n"); //Session id
                buffer.append(result.getString(11) + " "); //Name
                buffer.append(result.getString(12) + "\n"); //Surname
                buffer.append(result.getString(6) + ", "); //Sport
                buffer.append(result.getString(3) + " "); //Distance
                buffer.append(result.getString(4));

                session.add(buffer.toString()); //Adding to the list
                buffer.delete(0, buffer.length()); //Clearing the buffer
            } while (result.moveToNext()); //Until result are found

            sessionAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, session); //Simple list view
            listView.setAdapter(sessionAdapter);

        }
    }

    public void sessionDetails (String sessionID){
        String detailMessage;
        Cursor details = database.getExtendedSession(sessionID); //Getting the tapped session details
        details.moveToFirst();

        int lapNumber = 1; //Lap counter

        if (details.getCount() == 0) {
            Toast.makeText(getContext(), "Error loading details", Toast.LENGTH_SHORT).show();
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

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()); //Creating the builder
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_calendar, container, false); //Getting the view

        CalendarView calendarView = (CalendarView) view.findViewById(R.id.calendar); //Getting the calendar
        listView = (ListView) view.findViewById(R.id.dateSessionList); //Getting the listview

        database = new DBHelper(getContext()); //Initializing the db

        //Date change listener
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                refreshList(month + 1, year, dayOfMonth);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                clickedSessionId = session.get(position).split(":")[1].split("\n")[0].trim(); //Id of clicked session
                sessionDetails(clickedSessionId); //Showing details
            }
        });

        return view;
    }
}
