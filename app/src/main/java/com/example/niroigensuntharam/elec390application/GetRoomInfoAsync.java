package com.example.niroigensuntharam.elec390application;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

// The AsyncTask is used since the application is doing a web call
class GetRoomInfoAsync extends AsyncTask<String, Void, Void> {
    private ProgressBar progressBar;

    private Context mContext;

    public GetRoomInfoAsync(Context context){
        mContext = context;
    }

    @Override
    protected Void doInBackground(String... params) {

        try {
            // Clearing all the rooms and available rooms
            MainActivity.Rooms.clear();
            MainActivity.RoomsNowAvailable.clear();

            // Looping through all the rooms
            // and getting its information
            for (int i = 0; i < MainActivity.AvailableRooms[0].length; i ++)
            {
                // Creating an object of the async class
                //GetRoomInfoAsync getRoomInfoAsync = new GetRoomInfoAsync();

                // The url will depend on the room number being provided
                // Executing the async task
                // Connecting to the website and retrieving the room information
                Document doc = Jsoup.connect
                        ("https://calendar.encs.concordia.ca/month.php?user=_NUC_LAB_H" +
                        MainActivity.AvailableRooms[0][i]).get();

                // Passing in the room number, its capacity and the date
                Room room = new Room(MainActivity.AvailableRooms[0][i],
                        MainActivity.AvailableRooms[1][i],
                        params[0], doc);

                // Verifying whether the room is currently available or not
                Room.VerifyIfAvalaible(room);

                // Adding the room to the list of rooms
                MainActivity.Rooms.add(room);
            }

            // Connecting to the website and retrieving the room information
            //Document doc = Jsoup.connect(params[0]).get();

        }
        catch (IOException ex)
        {
        }

        return null;
    }

    @Override
    protected void onPreExecute(){

    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if (MainActivity.swipeRefreshLayout.isRefreshing())
            MainActivity.swipeRefreshLayout.setRefreshing(false);

        Toast.makeText(mContext, "Done", Toast.LENGTH_SHORT).show();

        MainActivity.currentRoom = MainActivity.RoomsNowAvailable.get(0);

        MainActivity.myCustomAdapter.notifyDataSetChanged();

        new NotificationHelper(mContext);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}