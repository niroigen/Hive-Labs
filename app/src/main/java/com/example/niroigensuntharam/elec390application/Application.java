package com.example.niroigensuntharam.elec390application;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

class Application {

    private String Application;
    private ArrayList<String> RoomsToUse = new ArrayList<>();

    Application()
    {

    }

    ArrayList<String> getRoomsToUse() {
        return RoomsToUse;
    }

    void setRoomsToUse(ArrayList<String> roomsToUse) {
        RoomsToUse = roomsToUse;
    }

    void setApplication(String application) {
        Application = application;
    }

    void addRoom(String room) {
        RoomsToUse.add(room);
    }

    String getApplication() {
        return Application;
    }
}
