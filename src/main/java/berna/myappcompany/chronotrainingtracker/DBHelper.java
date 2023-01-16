package berna.myappcompany.chronotrainingtracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "CTT";
    public static final String ATHLETE_TABLE = "athlete";
    public static final String SESSION_TABLE = "session";
    public static final String LAP_TABLE = "lap";

    public DBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Athlete table creation
        db.execSQL("CREATE TABLE IF NOT EXISTS " + ATHLETE_TABLE + " (athleteID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, surname TEXT, favourite_sport TEXT);");

        //Session table creation
        db.execSQL("CREATE TABLE IF NOT EXISTS " + SESSION_TABLE + " (sessionID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "start_time TEXT, stop_time TEXT, distance REAL, unit TEXT, speed REAL, sport TEXT, style TEXT, date TEXT, " +
                "athlete_id INTEGER, " +
                "FOREIGN KEY (athlete_id) REFERENCES athlete(athleteID));");

        //Lap table creation
        db.execSQL("CREATE TABLE IF NOT EXISTS " + LAP_TABLE + " (lapID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "lap_time TEXT, session_id INTEGER, " +
                "FOREIGN KEY (session_id) REFERENCES session(sessionID));");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Dropping Athlete table
        db.execSQL("DROP TABLE IF EXISTS " + ATHLETE_TABLE);

        //Dropping Session table
        db.execSQL("DROP TABLE IF EXISTS " + SESSION_TABLE);

        //Dropping Laps table
        db.execSQL("DROP TABLE IF EXISTS " + LAP_TABLE);

        //Recreate both tables
        onCreate(db);
    }

    //Query to get the athletes list
    public Cursor getAllAthletes() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM " + ATHLETE_TABLE + ";", null);
        return result;
    }

    //Query to get the specified athlete
    public Cursor getSelectedAthlete(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM " + ATHLETE_TABLE + " WHERE athleteID = " + id + ";", null);
        return result;
    }

    //Query to insert an athlete
    public boolean addAthlete(String name, String surname, String sport) {
        //Starting
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        //Getting values
        contentValues.put("name", name);
        contentValues.put("surname", surname);
        contentValues.put("favourite_sport", sport);

        long result = db.insert(ATHLETE_TABLE, null, contentValues);

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    //Query to save session
    public long recordSession(int athleteID, String startTime, String stopTime, float distance, String unit,
                              float speed, String sport, String style, String date) {
        //Starting
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        //Getting values from params
        contentValues.put("athlete_id", athleteID);
        contentValues.put("start_time", startTime);
        contentValues.put("stop_time", stopTime);
        contentValues.put("distance", distance);
        contentValues.put("unit", unit);
        contentValues.put("speed", speed);
        contentValues.put("sport", sport);
        contentValues.put("date", date);
        if (style != null)
            contentValues.put("style", style);

        return db.insert(SESSION_TABLE, null, contentValues);
    }

    //Query to save laps
    public boolean recordLaps(long sessionID, String laps) {
        //Starting
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        //Values from params
        contentValues.put("session_id", sessionID);
        contentValues.put("lap_time", laps);

        long result = db.insert(LAP_TABLE, null, contentValues);

        if (result == -1)
            return false;
        else
            return true;
    }

    //Query to show all session for selected athlete
    public Cursor getAthleteSession(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM " + SESSION_TABLE + " WHERE athlete_id = " + id + ";", null);

        return result;
    }

    //Query to show session on a specific day
    public Cursor getSessionOnDay(int month, int year, int dayOfMonth) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM " + SESSION_TABLE + " INNER JOIN " + ATHLETE_TABLE +
                " ON session.athlete_id = athlete.athleteID WHERE session.date LIKE '" + dayOfMonth + "/" + month + "/" + year + "';", null);

        return result;
    }

    //Query for session + laps
    public Cursor getExtendedSession(String sessionID) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM " + SESSION_TABLE + " LEFT JOIN " + LAP_TABLE +
                " ON session.sessionID = lap.session_id WHERE session.sessionID = " + sessionID + ";", null);

        return result;
    }

    //Query to show all session recorder for all athletes
    public Cursor getAllSession() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM " + SESSION_TABLE + " INNER JOIN " + ATHLETE_TABLE +
                " ON session.athlete_id = athlete.athleteID; ", null);

        return result;
    }

    //Query for session + laps
    public Cursor getExtendedSessionAndAthletes(String sessionID) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM " + SESSION_TABLE + " LEFT JOIN " + LAP_TABLE +
                " ON session.sessionID = lap.session_id INNER JOIN " + ATHLETE_TABLE +
                " ON session.athlete_id = athlete.athleteID WHERE session.sessionID = " + sessionID + ";", null);

        return result;
    }
}

