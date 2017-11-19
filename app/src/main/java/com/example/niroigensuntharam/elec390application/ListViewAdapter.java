package com.example.niroigensuntharam.elec390application;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ListViewAdapter extends ArrayAdapter {

    private Context context;

    ListViewAdapter(Context context, int textViewResourceId, ArrayList objects) {
        super(context, textViewResourceId, objects);
        this.context = context;
    }

    private class ViewHolder {
        TextView labName;
    }

    @Override
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent)
    {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.lab_cell, null);

            holder = new ViewHolder();
            holder.labName = convertView.findViewById(R.id.labName);
            holder.labName.setHintTextColor(Color.WHITE);

            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.labName.setText(MainActivity.Rooms.get(position).getRoomNumber());

        if(MainActivity.Rooms.get(position).getIsAvailable())
        {
            String out = "";
            if (MainActivity.Rooms.get(position).getNextClass() == null)
            {
                out = "No other classes";
            }
            else
            {
                //String nextTime = (rooms.get(position).nextClass != null) ? rooms.get(position).nextClass : "No other classes";
                out = "@" + MainActivity.Rooms.get(position).getNextTime() + " \n\t\t\t\t\t\t\t" + MainActivity.Rooms.get(position).getNextClass();
            }

            // do something change color
            holder.labName.append("\t\t\tNext class: " + out);

            if (out.equals("No other classes"))
                convertView.setBackgroundColor (Color.rgb(159,208,137)); // some color
            else
                convertView.setBackgroundColor (Color.rgb(233,175,0));
        }
        else
        {
            holder.labName.append("\t\t\t" + MainActivity.Rooms.get(position).getCurrentClass());
            convertView.setBackgroundColor (Color.rgb(233,64,0)); // default color
        }

        return convertView;
    }
}


