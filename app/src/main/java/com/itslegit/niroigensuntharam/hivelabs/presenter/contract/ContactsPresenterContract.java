package com.itslegit.niroigensuntharam.hivelabs.presenter.contract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by niroigensuntharam on 2018-02-10.
 */

public interface ContactsPresenterContract {
    interface Presenter {
        void checkPermission();
        List<String> sortContacts(ArrayList<HashMap<String, String>> contactData);
    }

    interface View {
        void requestContactsPermissions(String[] permissions, int permission_request);
        void getContactNames();
        void displayContacts();
    }
}
