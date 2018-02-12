package com.itslegit.niroigensuntharam.hivelabs;

import android.support.annotation.NonNull;

import com.itslegit.niroigensuntharam.hivelabs.activities.MainActivity;

import java.util.ArrayList;
import java.util.Collections;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class Room implements Comparable<Room> {

    // Storing the list of different time slots for a certain room
    public ArrayList<String> timeList = new ArrayList<>();

    // Storing the list of classes for a certain room
    public ArrayList<String> classList = new ArrayList<>();

    public boolean isAvailable;

    // The room number of the room
    public String roomNumber;

    public String nextClass;

    public int nextTime = 0;

    public String currentClass;

    public double latitude;

    public double longitude;

    public long volume = -1;

    public int amount = 0;

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public boolean isImageChanged;

    public boolean isImageChanged() {
        return isImageChanged;
    }

    public void setImageChanged(boolean imageChanged) {
        isImageChanged = imageChanged;
    }

    public long getVolume() {
        return volume;
    }

    public void setVolume(long volume) {
        this.volume = volume;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(boolean _isAvailable) {
        isAvailable = _isAvailable;
    }

    public ArrayList<String> getTimeList() {
        return timeList;
    }

    public ArrayList<String> getClassList() {
        return classList;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public String getCurrentClass() {
        return currentClass;
    }

    public String getNextClass() {
        return nextClass;
    }

    public int getNextTime() {
        return nextTime;
    }

    public void setNextClass(String next_class) {
        nextClass = next_class;
    }

    public void setCurrentClass(String currentCourse) {
        currentClass = currentCourse;
    }

    public void setNextTime(int next_time) {
        nextTime = next_time;
    }

    public void setRoomNumber(String room_Number) {
        roomNumber = room_Number;
    }

    public void setClassList(ArrayList<String> class_List) {
        classList = class_List;
    }

    public void setTimeList(ArrayList<String> timeList) {
        this.timeList = timeList;
    }

    public Room() {

    }


    Room(String room, String date, Document doc) {
        // Setting the properties
        roomNumber = room;

        try {

            // Getting the all the days which the lab is open
            Elements elements = doc.select("a[class=layerentry]");

            // Finding all the courses that will be held on the dateString
            for (int i = 0; i < elements.select("a[href*=" + date + "]").size(); i++) {
                Element element = elements.select("a[href*=" + date + "]").get(i);

                String eventId = element.attributes().get("id");

                Element event = doc.select("dl#eventinfo-" + eventId).first();

                String temp = event.childNode(7).toString().split("\n")[1];

                timeList.add(temp);

                temp = element.childNode(1).toString().split(";")[1];

                classList.add(temp);
            }
        } catch (Exception ex) {
            // When exception is thrown
        }
    }

    public static void EarliestAvailableTime() {
        for (int i = 0; i < MainActivity.RoomsNowAvailable.size(); i++) {

            if (MainActivity.RoomsNowAvailable.get(i).timeList.size() > 0) {

                String nowTime = MainActivity.timeString;// SimpleDateFormat("HHmm").format(new Date());

                // Getting an integer value for the startTime
                // Ex: 1245
                int StartTime = MainActivity.RoomsNowAvailable.get(i).getNextTime();

                int NowTime = Integer.parseInt(nowTime);

                if (NowTime < StartTime && MainActivity.earliestTime == null) {
                    MainActivity.earliestTime = Integer.toString(StartTime);
                } else if (MainActivity.earliestTime != null
                        && StartTime < Integer.parseInt(MainActivity.earliestTime)
                        && NowTime < StartTime) {
                    MainActivity.earliestTime = Integer.toString(StartTime);
                }
            }
        }
    }

    // Will be used to verify whether a room is currently available
    // and if it is, then it will be added to the RoomsNowAvailable list
    public static void VerifyIfAvalaible(Room room) {
        // The availability of a certain lab
        room.isAvailable = true;

        boolean isNextClass = false;

        for (int i = 0; i < room.timeList.size(); i++) {
            // Getting the time of a certain course
            // Ex: 12:45 - 13:55
            String[] time = room.timeList.get(i).split("-");

            // Retrieving the start time
            // Ex: 12:45
            String[] startTime = time[0].split(":");

            // Retrieving the end time
            // Ex: 13:55
            String[] endTime = time[1].split(":");

            // Getting an integer value for the timeString
            // Ex: 1500
            int TimeNow = Integer.parseInt(MainActivity.timeString);

            // Getting an integer value for the startTime
            // Ex: 1245
            int StartTime = Integer.parseInt(startTime[0].trim() + startTime[1].trim());

            // Getting an integer value for the endTime
            // Ex: 1355
            int EndTime = Integer.parseInt(endTime[0].trim() + endTime[1].trim());

            if (TimeNow < StartTime && !isNextClass) {
                room.setNextClass(room.getClassList().get(i));
                room.setNextTime(StartTime);
                isNextClass = true;
                MainActivity.RoomsNowAvailable.add(room);
            } else if (!isNextClass) {
                room.setNextClass(null);
            }

            // Verifying whether the room is currently unavailable
            if (TimeNow >= StartTime && TimeNow <= EndTime) {
                // Set the availability to false
                room.isAvailable = false;

                room.setCurrentClass(room.getClassList().get(i));

                room.nextTime = -1;

                // Break from the loop
                break;
            } else if (!isNextClass) {
                room.setCurrentClass(null);
            }
        }

        if (room.nextClass == null && room.currentClass == null) {
            room.nextTime = 2400;
        }
    }

    static void SortRooms(ArrayList<Room> rooms) {
        Collections.sort(rooms);
    }

    @Override
    public int compareTo(@NonNull Room room) {
        int compareTime = room.getNextTime();
        return compareTime - this.getNextTime();
    }
}

