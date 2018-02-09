package com.itslegit.niroigensuntharam.hivelabs;

import java.util.ArrayList;

public class Application {

    public String application;
    public ArrayList<String> roomsToUse = new ArrayList<>();

    Application()
    {

    }

    public ArrayList<String> getRoomsToUse() {
        return roomsToUse;
    }

    public void setRoomsToUse(ArrayList<String> roomsToUse) {
        this.roomsToUse = roomsToUse;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public void addRoom(String room) {
        roomsToUse.add(room);
    }

    public String getApplication() {
        return application;
    }
}
