package com.example.niroigensuntharam.elec390application;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/**
 * Created by niroigensuntharam on 2017-11-07.
 */

public class Application {

    private String Application;
    private ArrayList<String> RoomsToUse = new ArrayList<>();

    Application()
    {

    }

    public ArrayList<String> getRoomsToUse() {
        return RoomsToUse;
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
