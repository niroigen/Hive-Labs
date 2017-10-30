package com.example.niroigensuntharam.elec390application;

import android.os.AsyncTask;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class Room {

    ArrayList<String> TimeList = new ArrayList<>();
    ArrayList<String> ClassList = new ArrayList<>();
    String roomNumber;
    String capacity;

    public ArrayList<String> getTimeList() {
        return TimeList;
    }

    public ArrayList<String> getClassList() {
        return ClassList;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public String getCapacity() {
        return capacity;
    }

    Room (String room, String cap)
    {
        GetRoomInfoAsync getRoomInfoAsync = new GetRoomInfoAsync();

        getRoomInfoAsync.execute("https://calendar.encs.concordia.ca/month.php?user=_NUC_LAB_H" + room);

        roomNumber = room;
        capacity = cap;

        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();
        System.out.println(dateFormat.format(date));

        final String dateString = dateFormat.format(date);

        try {
            Document document = getRoomInfoAsync.get();

            Elements elements = document.select("a[class=layerentry]");

            for (int i = 0 ; i < elements.select("a[href*=" + dateString + "]").size(); i++)
            {
                Element element = elements.select("a[href*=" + dateString + "]").get(i);

                String eventid = element.attributes().get("id");

                Element event = document.select("dl#eventinfo-" + eventid).first();

                String temp = event.childNode(7).toString().split("\n")[1];

                TimeList.add(temp);

                temp = event.childNode(11).toString().split("\n")[1];

                ClassList.add(temp);
            }
        }
        catch (Exception ex)
        {
        }
    }
}

class GetRoomInfoAsync extends AsyncTask<String, Void, Document> {
    @Override
    protected Document doInBackground(String... params) {

        try {
            Document doc = Jsoup.connect(params[0]).get();

            return doc;
        }
        catch (IOException ex)
        {
        }

        return null;
    }
}

