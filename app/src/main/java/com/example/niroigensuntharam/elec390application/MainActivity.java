package com.example.niroigensuntharam.elec390application;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        parser("847");
    }

    public static void parser(String roomNumber)
    {

        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();
        System.out.println(dateFormat.format(date));

        String da = dateFormat.format(date);

            Thread newThread = new Thread(){
                public void run(){
                    try {
                        Document doc = Jsoup.connect("https://calendar.encs.concordia.ca/month.php?user=_NUC_LAB_H847").get();
                        Elements elements = doc.select("a[class=layerentry]");

                        String date = "20171002";

                        Element element = elements.select("a[href*=" + date + "]").first();

                        String eventid = element.attributes().get("id");

                        Element event = doc.select("dl#eventinfo-" + eventid).first();


                        String hw="";

                    }
                    catch (Exception ex)
                    {
                        String Hello ="";

                    }
                }
            };

            newThread.start();
    }
}



