package com.example.niroigensuntharam.elec390application;

import android.content.Context;
import android.hardware.camera2.params.BlackLevelPattern;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// The AsyncTask is used since the application is doing a web call
public class GetRoomInfoAsync extends AsyncTask<String, Void, Void> {
    private Context mContext;
    private String Date;

    GetRoomInfoAsync(Context context){

        mContext = context;
    }

    @Override
    protected Void doInBackground(String... params) {

        Date = params[0];

        MainActivity.Rooms.clear();

        RetrieveRoomsInfo();

        for (Room room: MainActivity.Rooms) {
            // Verifying whether the room is currently available or not
            Room.VerifyIfAvalaible(room);
        }

        return null;
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
            //
        }
        finally {
            saveRooms();
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if (BlankFragment.swipeRefreshLayout.isRefreshing())
            BlankFragment.swipeRefreshLayout.setRefreshing(false);

        Toast.makeText(mContext, "Done", Toast.LENGTH_SHORT).show();

        Room.EarliestAvailableTime();

        saveRooms();

        saveDate();

        MainActivity.SetAllRooms(MainActivity.Rooms);
        //MainActivity.currentRoom = MainActivity.RoomsNowAvailable.get(0);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    private static void saveRooms() {

        Map<String, Room> rooms = new HashMap<>();

        for (Room room: MainActivity.Rooms){
            rooms.put(room.getRoomNumber(), room);
        }

        try {
            MainActivity.mRoomRef.setValue(rooms);
        }
        catch (Exception ex){
            ex.getMessage();
        }
    }

    private void saveDate() {
        MainActivity.mDateRef.setValue(Date);
    }
}