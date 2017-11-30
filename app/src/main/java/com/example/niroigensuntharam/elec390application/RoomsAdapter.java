package com.example.niroigensuntharam.elec390application;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class RoomsAdapter extends RecyclerView.Adapter<RoomViewHolder> {

    private ArrayList<Room> mRooms;
    private Context mContext;

    public RoomsAdapter (Context context, ArrayList<Room> rooms){
        mContext = context;
        mRooms = rooms;
    }

    @Override
    public int getItemCount() {
        return (null != mRooms ? mRooms.size() : 0);
    }

    @Override
    public void onBindViewHolder(RoomViewHolder holder, final int position) {
        final RoomViewHolder mainHolder = (RoomViewHolder) holder;

        mainHolder.roomNumber.setText(mRooms.get(position).getRoomNumber());

        if (mRooms.get(position).getVolume() < 5){
            mainHolder.volume.setText("Quiet");
            mainHolder.volume.setTextColor(Color.rgb(45,130,61));
        }
        else if (mRooms.get(position).getVolume() > 5 && mRooms.get(position).getVolume() < 15){
            mainHolder.volume.setText("Moderate");
            mainHolder.volume.setTextColor(Color.rgb(181,112,23));

        }
        else {
            mainHolder.volume.setText("Loud");
            mainHolder.volume.setTextColor(Color.rgb(122,4,4));
        }

        if (mRooms.get(position).getNextClass() == null &&
                mRooms.get(position).getCurrentClass() == null) {
            mainHolder.nextClass.setText("No classes for the rest of the day!");
            mainHolder.cardView.setCardBackgroundColor(Color.rgb(147, 204, 57));
        } else if (mRooms.get(position).getNextClass() != null) {
            mainHolder.nextClass.setText("Next class: " + mRooms.get(position).getNextClass() + "\n@\t" + mRooms.get(position).getNextTime());
            mainHolder.cardView.setCardBackgroundColor(Color.rgb(216, 224, 98));
        } else if (mRooms.get(position).getCurrentClass() != null) {
            mainHolder.nextClass.setText("Current Class: " + mRooms.get(position).getCurrentClass());
            mainHolder.cardView.setCardBackgroundColor(Color.rgb(170, 46, 46));
        }

        mainHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext.getApplicationContext(), LabDetail.class);
                intent.putExtra("room", mRooms.get(position).getRoomNumber());
                mContext.startActivity(intent);
            }
        });

        mainHolder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                String roomNumber = mainHolder.roomNumber.getText().toString();

                int i = -1;

                for (int z = 0; z < MainActivity.Rooms.size(); z++) {
                    if (roomNumber.equals(MainActivity.Rooms.get(z).getRoomNumber())) {
                        i = z;
                        break;
                    }
                }

                StringBuilder output = new StringBuilder();

                for (int j = 0; j < MainActivity.Applications.size(); j++) {
                    ArrayList<String> rooms = MainActivity.Applications.get(j).getRoomsToUse();

                    for (int k = 0; k < rooms.size(); k++) {
                        if (rooms.get(k).substring(3, 6).equals(MainActivity.Rooms.get(i).getRoomNumber())) {
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
                } catch (Exception ex) {
                    ex.getMessage();
                }

                return true;
            }
        });

        mainHolder.amount.setText(mRooms.get(position).getAmount() + "/" + mRooms.get(position).getCapacity());
    }

    @Override
    public RoomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater mInflater = LayoutInflater.from(parent.getContext());

            ViewGroup mainGroup = (ViewGroup) mInflater.inflate(
                    R.layout.card_item, parent, false);
            RoomViewHolder mainHolder = new RoomViewHolder(mainGroup) {
                @Override
                public String toString() {
                    return super.toString();
                }
            };

            return mainHolder;
    }
}