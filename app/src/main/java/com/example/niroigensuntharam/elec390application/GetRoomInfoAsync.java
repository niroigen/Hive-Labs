package com.example.niroigensuntharam.elec390application;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

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

        if (MainActivity.swipeRefreshLayout.isRefreshing())
            MainActivity.swipeRefreshLayout.setRefreshing(false);

        Toast.makeText(mContext, "Done", Toast.LENGTH_SHORT).show();

        Room.EarliestAvailableTime();

        Room.SortRooms();

        saveRooms();

        saveDate();

        MainActivity.SetAllRooms(MainActivity.Rooms);
        //MainActivity.currentRoom = MainActivity.RoomsNowAvailable.get(0);

        MainActivity.myCustomAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    private static void saveRooms() {

        List<Room> rooms = MainActivity.Rooms;

        // Remove after being saving all the locations of each room
        for (Room room: MainActivity.Rooms){
            if (room.getRoomNumber().contains("819")){
                room.setLatitude("45.4969376");
                room.setLongitude("-73.5789478");
            }
            if (room.getRoomNumber().contains("821")){
                room.setLatitude("45.4969595");
                room.setLongitude("-73.5790202");
            }
            if (room.getRoomNumber().contains("807")){
                room.setLatitude("45.4971524");
                room.setLongitude("-73.5786222");
            }
        }

        MainActivity.mRoomRef.setValue(rooms);
    }

    private void saveDate() {
        MainActivity.mDateRef.setValue(Date);
    }
}