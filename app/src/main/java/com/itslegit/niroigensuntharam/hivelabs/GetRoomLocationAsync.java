package com.itslegit.niroigensuntharam.hivelabs;

import java.util.List;
import android.content.Context;
import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by niroigensuntharam on 2017-11-19.
 */

public class GetRoomLocationAsync {


    public class GetApplicationInfoAsync extends AsyncTask<Void, Void, Void> {
        private Context mContext;

        GetApplicationInfoAsync(Context context){
            mContext = context;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            try{

                MainActivity.Applications.clear();

                Document doc1 = Jsoup.connect
                        ("https://aits.encs.concordia.ca/services/software-windows-public-labs").get();

                Element table = doc1.select("table").get(0);
                Elements rows = table.select("tr");

                for (int i = 0; i < rows.size(); i++)
                {
                    Element row = rows.get(i);
                    Elements cols = row.select("td");

                    String applicationName = cols.select("td").get(0).text();

                    String[] rooms = cols.select("td").get(1).text().split(",");

                    Application application = new Application();

                    application.setApplication(applicationName);

                    for (String room: rooms) {

                        if (rooms.length == 0) {

                            break;
                        } else if (room.contains("H")){
                            application.addRoom(room);
                        }

                    }

                    MainActivity.Applications.add(application);
                }
            }
            catch (Exception ex) {
                // Print failure
            }
            finally {
                saveApplications();
            }

            return null;
        }

        private void saveApplications() {

            List<Application> apps = MainActivity.Applications;

            MainActivity.mAppRef.setValue(apps);
        }
    }

}

/*

package com.example.niroigensuntharam.elec390application;

import android.content.Context;
import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;

public class GetApplicationInfoAsync extends AsyncTask<Void, Void, Void> {
    private Context mContext;

    GetApplicationInfoAsync(Context context){
        mContext = context;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        try{

            MainActivity.Applications.clear();

            Document doc1 = Jsoup.connect
                    ("https://aits.encs.concordia.ca/services/software-windows-public-labs").get();

            Element table = doc1.select("table").get(0);
            Elements rows = table.select("tr");

            for (int i = 0; i < rows.size(); i++)
            {
                Element row = rows.get(i);
                Elements cols = row.select("td");

                String applicationName = cols.select("td").get(0).text();

                String[] rooms = cols.select("td").get(1).text().split(",");

                application application = new application();

                application.setApplication(applicationName);

                for (String room: rooms) {

                    if (rooms.length == 0) {

                        break;
                    } else if (room.contains("H")){
                        application.addRoom(room);
                    }

                }

                MainActivity.Applications.add(application);
            }
        }
        catch (Exception ex) {
            // Print failure
        }
        finally {
            saveApplications();
        }

        return null;
    }

    private void saveApplications() {

        List<application> apps = MainActivity.Applications;

        MainActivity.mAppRef.setValue(apps);
    }
}

 */
