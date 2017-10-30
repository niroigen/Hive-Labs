package com.example.niroigensuntharam.elec390application;

/**
 * Created by abdullahalsaeed on 2017-10-29.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ListViewAdapter extends ArrayAdapter {

    private Context context;
    private ArrayList<Room> room;

    public ListViewAdapter(Context context, int textViewResourceId, ArrayList objects) {
        super(context, textViewResourceId, objects);
        this.context = context;
        room = objects;
    }

    private class ViewHolder {
        TextView labName;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder=null;
        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.lab_cell, null);

            holder = new ViewHolder();
            holder.labName = (TextView)convertView.findViewById(R.id.labName);

            convertView.setTag(holder);

        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        Room individualLab = room.get(position);
        holder.labName.setText(individualLab.getClassList().get(0));
        return convertView;
    }
}


