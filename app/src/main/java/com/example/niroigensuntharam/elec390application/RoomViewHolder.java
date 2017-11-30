package com.example.niroigensuntharam.elec390application;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by niroigensuntharam on 2017-11-28.
 */

public abstract class RoomViewHolder extends RecyclerView.ViewHolder {

    TextView roomNumber;
    TextView nextClass;
    TextView volume;
    CardView cardView;
    TextView amount;

    RoomViewHolder(View view){
        super(view);

        this.roomNumber = (TextView) view.findViewById(R.id.room_number);
        this.nextClass = (TextView) view.findViewById(R.id.next_class);
        this.volume = (TextView) view.findViewById(R.id.volume);
        this.cardView = (CardView) view.findViewById(R.id.card_view);
        this.amount = view.findViewById(R.id.amount);
    }
}
