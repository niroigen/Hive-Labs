package com.example.niroigensuntharam.elec390application;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Query;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dmax.dialog.SpotsDialog;

// The AsyncTask is used since the application is doing a web call
public class GetRoomInfoAsync extends AsyncTask<String, Void, Void> {
    private Context mContext;
    private static boolean isConnected = false;
    private String Date;

    public GetRoomInfoAsync(Context context){

        mContext = context;
    }

    @Override
    protected Void doInBackground(String... params) {

        Date = params[0];

        // Clearing all the rooms and available rooms
        // Clearing all the rooms and available rooms
        MainActivity.Rooms.clear();
        MainActivity.RoomsNowAvailable.clear();
        MainActivity.Applications.clear();

//        if (isConnected && roomsStored == null){
//            RetrieveRoomsInfo();
//            roomsStored = readRooms();
//        }
//
//        if (isConnected && applicationsStored == null){
//            RetrieveApplicationInfo();
//            applicationsStored = readApplications();
//        }
//
//        if (isConnected && dateStored == null)
//        {
//            saveDate();
//
//            return null;
//        }

//        if (isConnected ){&& !dateStored.equals(Date)) {
//            RetrieveRoomsInfo();
//            RetrieveApplicationInfo();
//            saveDate();
//        }
//        else {
//
//            for (int i = 0; i < roomsStored.size(); i++) {
//
//                MainActivity.Rooms.add(roomsStored.get(i));
//
//                // Verifying whether the room is currently available or not
//                Room.VerifyIfAvalaible(roomsStored.get(i));
//            }
//
//            for (int i = 0; i < applicationsStored.size(); i++)
//            {
//                MainActivity.Applications.add(applicationsStored.get(i));
//            }
//        }

        return null;
    }

    private void RetrieveApplicationInfo(){
        try{
            Document doc1 = Jsoup.connect
                    ("https://aits.encs.concordia.ca/services/software-windows-public-labs").get();

            Element table = doc1.select("table").get(0);
            Elements rows = table.select("tr");

            for (int i = 0; i < rows.size(); i++)
            {
                Element row = rows.get(i);
                Elements cols = row.select("td");

                String applicationName = cols.select("td").get(0).text();

                String[] words = applicationName.split("[ _.]+");
                String temp = words[0];

                for (int j = 0; j < words.length; j++){
                    if (!words[j].matches(".*\\d+.*")
                            && !words[j].contains(("v"))){
                        temp += " " + words[j];
                    }
                    else {
                        break;
                    }
                }

                String[] rooms = cols.select("td").get(1).text().split(",");

                Application application = new Application();

                application.setApplication(temp);

                for (int j = 0; j < rooms.length; j++) {

                    if (rooms.length == 0) {

                        break;
                    } else if (rooms[j].contains("H")){
                        application.addRoom(rooms[j]);
                    }

                }

                MainActivity.Applications.add(application);
            }
        }
        catch (Exception ex) {
        }
        finally {
            saveApplications();
        }
    }

    private void RetrieveRoomsInfo(){
        try {

            ArrayList<Thread> threads = new ArrayList<>();

            // Looping through all the rooms
            // and setting all the threads
            for (int i = 0; i < MainActivity.AvailableRooms[0].length; i++) {
                // Creating an object of the async class
                //GetRoomInfoAsync getRoomInfoAsync = new GetRoomInfoAsync();

                String url = "https://calendar.encs.concordia.ca/month.php?user=_NUC_LAB_H" +
                        MainActivity.AvailableRooms[0][i];

                Runnable worker = new MyRunnable(url, MainActivity.AvailableRooms[0][i],
                        MainActivity.AvailableRooms[1][i], Date);

                Thread thread = new Thread(worker);

                threads.add(thread);
            }

            for (int i = 0; i < threads.size(); i++)
            {
                threads.get(i).start();
            }

            for (int i = 0; i < threads.size(); i++)
            {
                threads.get(i).join();
            }
        }
        catch (Exception ex) {
            String  c = "";
        }
        finally {
            saveRooms();
        }
    }

    @Override
    protected void onPreExecute(){
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if (isConnected) {

            if (MainActivity.swipeRefreshLayout.isRefreshing())
                MainActivity.swipeRefreshLayout.setRefreshing(false);

            Toast.makeText(mContext, "Done", Toast.LENGTH_SHORT).show();

            Room.EarliestAvailableTime();

            Room.SortRooms();

            MainActivity.dialog.dismiss();

            //MainActivity.currentRoom = MainActivity.RoomsNowAvailable.get(0);

            MainActivity.myCustomAdapter.notifyDataSetChanged();

            //saveDate();
        }
        else {
            Toast.makeText(mContext, "Not connected to the internet", Toast.LENGTH_SHORT).show();

            MainActivity.swipeRefreshLayout.setRefreshing(false);

            MainActivity.dialog.dismiss();
        }
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    public static void isNetworkAvailable(Context context)
    {
        isConnected = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
    }

    public static void saveRooms() {

        List<Room> rooms = MainActivity.Rooms;

        MainActivity.mRoomRef.setValue(rooms);
    }

    public void saveDate() {
        MainActivity.mDateRef.setValue(Date);
    }

    public void saveApplications() {

        Map<String, Application> apps = new HashMap<>();

        for (int i = 0; i < MainActivity.Applications.size(); i++)
            apps.put(MainActivity.Applications.get(i).getApplication(),
                    MainActivity.Applications.get(i));

        MainActivity.mAppRef.setValue(apps);
    }
}