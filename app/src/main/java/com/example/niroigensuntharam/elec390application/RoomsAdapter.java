package com.example.niroigensuntharam.elec390application;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class RoomsAdapter extends RecyclerView.Adapter<RoomsAdapter.ViewHolder> {

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView roomNumber;

        public ImageView volumeLevel;

        public TextView nextClass;

        public CardView cardView;
        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            roomNumber = (TextView) itemView.findViewById(R.id.room_number);

            volumeLevel = (ImageView) itemView.findViewById(R.id.volume);

            cardView = (CardView) itemView.findViewById(R.id.card_view);

            nextClass = itemView.findViewById(R.id.next_class);
        }
    }

    // Store a member variable for the contacts
    private List<Room> mRooms;
    // Store the context for easy access
    private Context mContext;

    // Pass in the contact array into the constructor
    public RoomsAdapter(Context context, List<Room> rooms) {
        mRooms = rooms;
        mContext = context;
    }

    public RoomsAdapter(List<Room> rooms){
        mRooms = rooms;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public RoomsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.card_item, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(RoomsAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Room room = mRooms.get(position);

        // Set item views based on your views and data model
        final TextView textView = viewHolder.roomNumber;
        textView.setText(room.getRoomNumber());

        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, LabDetail.class);
                intent.putExtra("room", textView.getText().toString().split("\t")[0]);
                mContext.startActivity(intent);
            }
        });

        viewHolder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                String roomNumber = textView.getText().toString().split("\t")[0];

                int  i = -1;

                for (int z = 0; z < MainActivity.Rooms.size(); z++){
                    if (roomNumber.equals(MainActivity.Rooms.get(z).getRoomNumber())){
                        i = z;
                        break;
                    }
                }

                StringBuilder output = new StringBuilder();

                for (int j = 0; j < MainActivity.Applications.size(); j++)
                {
                    ArrayList<String> rooms = MainActivity.Applications.get(j).getRoomsToUse();

                    for (int k = 0; k < rooms.size(); k++)
                    {
                        if (rooms.get(k).substring(3,6).equals(MainActivity.Rooms.get(i).getRoomNumber()))
                        {
                            output.append(MainActivity.Applications.get(j).getApplication());
                            output.append("\n");
                        }
                    }
                }

                AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                alertDialog.setTitle("Software Available");
                alertDialog.setMessage(output);
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();

                return true;
            }
        });

        TextView nextClassInfo = viewHolder.nextClass;

        if(MainActivity.Rooms.get(position).getIsAvailable())
        {
            String out = "";
            if (MainActivity.Rooms.get(position).getNextClass() == null)
                out = "No other classes";
            else
                out = "@" + MainActivity.Rooms.get(position).getNextTime() + " \n\t\t\t\t\t\t\t" + MainActivity.Rooms.get(position).getNextClass();

            // do something change color
            nextClassInfo.setText("Next class: " + out);

            if (out.equals("No other classes"))
                viewHolder.cardView.setBackgroundColor (Color.rgb(159,208,137)); // some color
            else
                viewHolder.cardView.setBackgroundColor (Color.rgb(233,175,0));
        }
        else
        {
            nextClassInfo.setText(MainActivity.Rooms.get(position).getCurrentClass());
            viewHolder.cardView.setBackgroundColor (Color.rgb(233,64,0)); // default color
        }
        if(MainActivity.Rooms.get(position).isImageChanged()) {


            if (MainActivity.Rooms.get(position).getVolume() < 10) {
                viewHolder.volumeLevel.setImageResource(R.drawable.low);
            } else if (MainActivity.Rooms.get(position).getVolume() >= 40 && MainActivity.Rooms.get(position).getVolume() < 100) {
                viewHolder.volumeLevel.setImageResource(R.drawable.medium);
            } else {
                viewHolder.volumeLevel.setImageResource(R.drawable.loud);
            }
        }
        else{
            viewHolder.volumeLevel.setImageResource(R.drawable.low);
        }

        textView.setTextColor(Color.WHITE);
    }

    public void changeImage(int index) {
        MainActivity.Rooms.get(index).setImageChanged(true);
        notifyDataSetChanged();
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mRooms.size();
    }
}