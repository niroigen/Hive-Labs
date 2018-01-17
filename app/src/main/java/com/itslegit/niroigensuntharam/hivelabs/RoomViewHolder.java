package com.itslegit.niroigensuntharam.hivelabs;

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
    CardView cardView;

    RoomViewHolder(View view){
        super(view);

        this.roomNumber = (TextView) view.findViewById(R.id.room_number);
        this.nextClass = (TextView) view.findViewById(R.id.next_class);
        this.cardView = (CardView) view.findViewById(R.id.card_view);
    }
}
