package com.example.niroigensuntharam.elec390application;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.support.v7.app.AlertDialog;
import android.support.v7.util.SortedList;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RoomsAdapter extends RecyclerView.Adapter<RoomsAdapter.ViewHolder> {

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row

        public TextView roomNumber;

        public TextView volumeLevel;

        public TextView nextClass;

        public CardView cardView;
        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            roomNumber = (TextView) itemView.findViewById(R.id.room_number);

            volumeLevel = (TextView) itemView.findViewById(R.id.volume);

            cardView = (CardView) itemView.findViewById(R.id.card_view);

            nextClass = itemView.findViewById(R.id.next_class);
        }
    }

    private final SortedList<Room> mSortedList = new SortedList<>(Room.class, new SortedList.Callback<Room>() {
        @Override
        public int compare(Room a, Room b) {
            return TIME_COMPARATOR.compare(a, b);
        }

        @Override
        public void onInserted(int position, int count) {
            notifyItemRangeInserted(position, count);
        }

        @Override
        public void onRemoved(int position, int count) {
            notifyItemRangeRemoved(position, count);
        }

        @Override
        public void onMoved(int fromPosition, int toPosition) {
            notifyItemMoved(fromPosition, toPosition);
        }

        @Override
        public void onChanged(int position, int count) {
            notifyItemRangeChanged(position, count);
        }

        @Override
        public boolean areContentsTheSame(Room oldItem, Room newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areItemsTheSame(Room item1, Room item2) {
            return item1.getRoomNumber().equals(item2.getRoomNumber());
        }
    });

    private static final Comparator<Room> TIME_COMPARATOR = new Comparator<Room>() {
        @Override
        public int compare(Room room1, Room room2) {
            int compareTime = room1.getNextTime();
            return compareTime - room2.getNextTime();
        }
    };

    private final Context mContext;
    private final LayoutInflater mInflater;
    private final Comparator<Room> mComparator;

    // Pass in the contact array into the constructor
    public RoomsAdapter(Context context, Comparator<Room> comparator, int position) {
        mInflater = LayoutInflater.from(context);
        mComparator = comparator;
        this.position = position;
        mContext = context;
    }

    // Pass in the contact array into the constructor
    public RoomsAdapter(Context context, Comparator<Room> comparator) {
        mInflater = LayoutInflater.from(context);
        mComparator = comparator;
        mContext = context;
    }

    private ArrayList<Room> mFilteredRooms;
    // Store the context for easy access

    static int position = -1;

//    public RoomsAdapter(List<Room> rooms){
//        mRooms = rooms;
//    }
//
//    public RoomsAdapter(List<Room> rooms, int position){
//        mRooms = rooms;
//        this.position = position;
//    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public RoomsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.card_item, parent, false);

        // Return a new holder instance
        return new ViewHolder(contactView);
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(final RoomsAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        final Room room = mSortedList.get(position);

        // Set item views based on your views and data model
        final TextView textView = viewHolder.roomNumber;
        textView.setText(room.getRoomNumber());

        if (room.getRoomNumber().equals("849"))
        {
            String c ="";
        }

        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, LabDetail.class);
                intent.putExtra("room", textView.getText());
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

                try {
                    alertDialog.show();
                }
                catch (Exception ex){
                    ex.getMessage();
                }

                return true;
            }
        });

        TextView nextClassInfo = viewHolder.nextClass;

        ArrayList<Room> rooms = new ArrayList<>();

        if (RoomsAdapter.position == 0) {
            viewHolder.cardView.setCardBackgroundColor(Color.rgb(159, 208, 137));

            for (Room _room : MainActivity.Rooms) {
                if (_room.getIsAvailable() && _room.getNextClass() == null) {
                    rooms.add(_room);
                }
            }
        }
        else if (RoomsAdapter.position == 1) {
            viewHolder.cardView.setCardBackgroundColor(Color.rgb(233, 175, 0));

            for (Room _room : MainActivity.Rooms) {
                if (_room.getIsAvailable() && _room.getNextClass() != null) {
                    rooms.add(_room);
                }
            }
        }
        else if (RoomsAdapter.position == 2) {
            viewHolder.cardView.setCardBackgroundColor(Color.rgb(233, 64, 0));

            for (Room _room : MainActivity.Rooms) {
                if (!_room.getIsAvailable()) {
                    rooms.add(_room);
                }
            }
        }

        if (rooms.size() != 0 && position < rooms.size()){
            if(rooms.get(position).getIsAvailable()) {
                String out = "";
                if (MainActivity.Rooms.get(position).getNextClass() == null)
                    out = "No other classes";
                else
                    out = "@" + MainActivity.Rooms.get(position).getNextTime() + " \n\t\t\t\t\t\t\t" + MainActivity.Rooms.get(position).getNextClass();

                // do something change color
                nextClassInfo.setText("Next class: " + out);
            }
            else {
                nextClassInfo.setText(rooms.get(position).getCurrentClass());
            }
            if(rooms.get(position).isImageChanged()) {
                if (rooms.get(position).getVolume() < 10) {
                    viewHolder.volumeLevel.setText("Quiet");
                    viewHolder.volumeLevel.setTextColor(Color.rgb(0,90,0));
                } else if (rooms.get(position).getVolume() >= 40 && rooms.get(position).getVolume() < 100) {
                    viewHolder.volumeLevel.setText("A little loud");
                    viewHolder.volumeLevel.setTextColor(Color.rgb(241,215,0));
                } else {
                    viewHolder.volumeLevel.setText("Loud");
                    viewHolder.volumeLevel.setTextColor(Color.rgb(167,0,0));
                }
            }
            else {
                viewHolder.volumeLevel.setText("Quiet");
                viewHolder.volumeLevel.setTextColor(Color.rgb(0,90,0));
            }

             textView.setTextColor(Color.WHITE);
        }
    }

    public void changeImage(int index) {
        MainActivity.Rooms.get(index).setImageChanged(true);
        notifyDataSetChanged();
    }

    public void add(Room room) {
        mSortedList.add(room);
    }

    public void remove(Room room) {
        mSortedList.remove(room);
    }

    public void add(List<Room> rooms) {
        mSortedList.addAll(rooms);
    }

    public void remove(List<Room> rooms) {
        mSortedList.beginBatchedUpdates();
        for (Room room : rooms) {
            mSortedList.remove(room);
        }
        mSortedList.endBatchedUpdates();
    }

    void replaceAll(List<Room> rooms) {
        mSortedList.beginBatchedUpdates();
        for (int i = mSortedList.size() - 1; i >= 0; i--) {
            final Room room = mSortedList.get(i);
            if (!rooms.contains(room)) {
                mSortedList.remove(room);
            }
        }
        mSortedList.addAll(rooms);
        mSortedList.endBatchedUpdates();
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mSortedList.size();
    }
}