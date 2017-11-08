package com.example.niroigensuntharam.elec390application;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/**
 * Created by niroigensuntharam on 2017-11-07.
 */

public class Application {

    String Application;
    ArrayList<String> RoomsToUse = new ArrayList<>();
    boolean AllRooms = false;

    Application()
    {

    }

    public ArrayList<String> getRoomsToUse() {
        return RoomsToUse;
    }

    public void setAllRooms(boolean allRooms) {
        AllRooms = allRooms;
    }

    public boolean getAllRooms()
    {
        return AllRooms;
    }

    public void setApplication(String application) {
        Application = application;
    }

    public void addRoom(String room) {
        RoomsToUse.add(room);
    }

    public String getApplication() {
        return Application;
    }
}
