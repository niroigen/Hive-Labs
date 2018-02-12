package com.itslegit.niroigensuntharam.hivelabs.presenter;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import com.itslegit.niroigensuntharam.hivelabs.presenter.contract.ContactsPresenterContract;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by niroigensuntharam on 2018-02-10.
 */

public class ContactsPresenter implements ContactsPresenterContract.Presenter {

    private final ContactsPresenterContract.View view;
    private Context context;
    public static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    public static final int PERMISSIONS_REQUEST_SEND_SMS = 99;

    private ContactsPresenter(Context context, ContactsPresenterContract.View view) {
        this.context = context;

        this.view = view;
    }

    public static ContactsPresenterContract.Presenter buildPresenter(Context context, ContactsPresenterContract.View view) {
        return new ContactsPresenter(context, view);
    }

    @Override
    public void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                (context.checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED ||
                context.checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED)) {
            view.requestContactsPermissions(new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.SEND_SMS},
                    PERMISSIONS_REQUEST_READ_CONTACTS);
        }

//        else {
//            // Android version is lesser than 6.0 or the permission is already granted.
//
//            //final List<String> contacts = new ArrayList<>();
////            view.displayContacts();
//        }
    }

    @Override
    public List<String> sortContacts(ArrayList<HashMap<String, String>> contactData) {

        List<String> contacts = new ArrayList<>();

        for (int i = 0; i < contactData.size(); i++) {

            if (!contacts.contains(contactData.get(i).get("name"))) {
                contacts.add(contactData.get(i).get("name"));
                Collections.sort(contacts);
            }
        }

        return contacts;
    }
}
