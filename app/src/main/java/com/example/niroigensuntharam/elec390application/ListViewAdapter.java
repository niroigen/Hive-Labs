package com.example.niroigensuntharam.elec390application;

/**
 * Created by abdullahalsaeed on 2017-10-29.
 */

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ListViewAdapter extends ArrayAdapter {

    private Context context;
    private ArrayList<Room> rooms;
    String timeString = new SimpleDateFormat("HHmm").format(new Date());

    public ListViewAdapter(Context context, int textViewResourceId, ArrayList objects) {
        super(context, textViewResourceId, objects);
        this.context = context;
        rooms = objects;
    }

    private class ViewHolder {
        TextView labName;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder = null;
        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.lab_cell, null);

            holder = new ViewHolder();
            holder.labName = (TextView)convertView.findViewById(R.id.labName);
            holder.labName.setHintTextColor(Color.WHITE);

            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }


        // Test of slack1
        holder.labName.setText(rooms.get(position).roomNumber);

        if(MainActivity.RoomsNowAvailable.contains(getItem(position)))
        {
            String out = "";
            if (rooms.get(position).nextClass == null)
            {
                out = "No other classes";
            }
            else
            {
                out = rooms.get(position).nextClass;
            }

            // do something change color
            holder.labName.append("\t\t\tNext class: " + out);
            convertView.setBackgroundColor (Color.rgb(159,208,137)); // some color
        }
        else
        {
            holder.labName.append("\t\t\t" + rooms.get(position).currentClass);
            convertView.setBackgroundColor (Color.WHITE); // default coloe
        }

        return convertView;
    }
}


