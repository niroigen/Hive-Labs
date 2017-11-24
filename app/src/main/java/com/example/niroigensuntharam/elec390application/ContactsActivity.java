package com.example.niroigensuntharam.elec390application;

import android.Manifest;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.SparseBooleanArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ContactsActivity extends AppCompatActivity {

    String roomNumber;

    // The ListView with all contacts' names
    private ListView lstNames;

    //Button to send text
    private Button getChoice;

    // Request code for READ_CONTACTS. It can be any number > 0.
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    // Request code for SEND_SMS. It can be any number > 0.
    private static final int PERMISSIONS_REQUEST_SEND_SMS = 99;

//    // Request code for SEND_SMS. It can be any number > 0.
//    private static final int PERMISSIONS_REQUEST_READ_PHONE_STATE = 100;

    //array of hash to hold all contacts' name and phone number
    private ArrayList<HashMap<String,String>> contactData=new ArrayList<HashMap<String,String>>();

    //hold the default message
    private String message = "I am in room H-";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        roomNumber = getIntent().getExtras().getString("roomNumber");

        message = message + roomNumber;

        // Find the list view
        this.lstNames = (ListView) findViewById(R.id.lstNames);
        getChoice = (Button) findViewById(R.id.getchoice);

        // Read and show the contacts
        showContacts();

//        ArrayList<HashMap<String,String>> contactData=new ArrayList<HashMap<String,String>>();
//
//        try {
//            Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
//            while (cursor.moveToNext()) {
//                try {
//                    String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
//                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
//                    String hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
//                    if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
//                        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
//                        while (phones.moveToNext()) {
//                            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                            HashMap<String, String> map = new HashMap<String, String>();
//                            map.put("name", name);
//                            map.put("number", phoneNumber);
//                            contactData.add(map);
//                        }
//                        phones.close();
//                    }
//                } catch (Exception e) {
//                }
//            }
//        }
//        catch (Exception error){
//            String c="";
//        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showContacts() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.SEND_SMS}, PERMISSIONS_REQUEST_SEND_SMS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        }
//        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, PERMISSIONS_REQUEST_SEND_SMS);
//            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
//        }



        else {
            // Android version is lesser than 6.0 or the permission is already granted.
//            List<String> contacts = getContactNames();
//            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, contacts);
//            lstNames.setAdapter(adapter);


            List<String> contacts = new ArrayList<>();
            getContactNames();

            for (int i=0; i < contactData.size(); i++){
               // contacts = new ArrayList<String>(contactData.get(i).get("name"));

                if (!contacts.contains(contactData.get(i).get("name"))){
                    contacts.add(contactData.get(i).get("name"));
                    Collections.sort(contacts); //sort contact names in alphabetic order
                }



            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, contacts);
            lstNames.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            lstNames.setAdapter(adapter);

            getChoice.setOnClickListener(new Button.OnClickListener(){

                @Override
                public void onClick(View v){


                    //String selected = "";

                    String phoneNumber = "";

                    //List to hold contact names that were selected for SMS
                    List<String> selected = new ArrayList<>();

                    int cntChoice = lstNames.getCount();
                    SparseBooleanArray sparseBooleanArray = lstNames.getCheckedItemPositions();

                    for(int i = 0; i < cntChoice; i++){

                        if(sparseBooleanArray.get(i)) {

                            //selected += lstNames.getItemAtPosition(i).toString() + "\n";

                            selected.add(lstNames.getItemAtPosition(i).toString());


                        }

                    }

//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
//                        requestPermissions(new String[]{Manifest.permission.SEND_SMS}, PERMISSIONS_REQUEST_SEND_SMS);
//                        //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
//                    }


                    SmsManager sms = SmsManager.getDefault();

                    for (int i = 0; i < selected.size(); i++) {

                        for (int j = 0; j < contactData.size(); j++) {
                            if (selected.get(i).contentEquals(contactData.get(j).get("name"))) {
                                phoneNumber = contactData.get(j).get("number");
                                phoneNumber = phoneNumber.replace("(", "");
                                phoneNumber = phoneNumber.replace(")", "");
                                phoneNumber = phoneNumber.replace(" ", "");
                                phoneNumber = phoneNumber.replace("-", "");


                                try {
                                    //SmsManager sms = SmsManager.getDefault();
//                                    sms = SmsManager.getDefault();


//                                    PendingIntent piSent = PendingIntent.getBroadcast(ContactsActivity.this, 0,new Intent("android.telephony.SmsManager.STATUS_ON_ICC_SENT"), 0);

                                    sms.sendTextMessage(phoneNumber, null, message, null, null);

//                                    sms.sendTextMessage("5556", null, message, piSent, null);


                                    //String sent = "android.telephony.SmsManager.STATUS_ON_ICC_SENT";
//                                    PendingIntent piSent = PendingIntent.getBroadcast(ContactsActivity.this, 0,new Intent("android.telephony.SmsManager.STATUS_ON_ICC_SENT"), 0);

//                                    sms.sendTextMessage("12345678901", null, "hello!", piSent, null);
                                } catch (Exception error) {
                                    String why = "";
                                }
                            }
                        }
                        Toast.makeText(ContactsActivity.this, "Sent to " + selected.get(i), Toast.LENGTH_SHORT).show();

                    }


                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                showContacts();
            } else {
                Toast.makeText(this, "Until you grant the permission, we cannot access your contacts", Toast.LENGTH_SHORT).show();
            }
        }

        else if (requestCode == PERMISSIONS_REQUEST_SEND_SMS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                showContacts();
            } else {
                Toast.makeText(this, "Until you grant the permission, we cannot send SMS", Toast.LENGTH_SHORT).show();
            }
        }

//        if (requestCode == PERMISSIONS_REQUEST_READ_PHONE_STATE) {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Permission is granted
//                showContacts();
//            } else {
//                Toast.makeText(this, "Until you grant the permission, we cannot display the names", Toast.LENGTH_SHORT).show();
//            }
//        }
    }

    /**
     * Read the name of all the contacts.
     *
     * @return a list of names.
     */
//    private List<String> getContactNames() {
        private ArrayList<HashMap<String,String>> getContactNames(){
//        List<String> contacts = new ArrayList<>();
//        // Get the ContentResolver
//        ContentResolver cr = getContentResolver();
//        // Get the Cursor of all the contacts
//        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
//
//        // Move the cursor to first. Also check whether the cursor is empty or not.
//        if (cursor.moveToFirst()) {
//            // Iterate through the cursor
//            do {
//                // Get the contacts name
//                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
//                contacts.add(name);
//            } while (cursor.moveToNext());
//        }
//        // Close the curosor
//        cursor.close();




        try {
            Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
            while (cursor.moveToNext()) {
                try {
                    String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    String hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                    if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                        while (phones.moveToNext()) {
                            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            HashMap<String, String> map = new HashMap<String, String>();
                            map.put("name", name);
                            map.put("number", phoneNumber);
                            contactData.add(map);
                        }
                        phones.close();
                    }
                } catch (Exception e) {
                }
            }
        }
        catch (Exception error){
            String c="";
        }





        //return contacts;
        return contactData;
    }
}
