package com.itslegit.niroigensuntharam.hivelabs.activities;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.itslegit.niroigensuntharam.hivelabs.R;
import com.itslegit.niroigensuntharam.hivelabs.databinding.ActivityContactsBinding;
import com.itslegit.niroigensuntharam.hivelabs.presenter.ContactsPresenter;
import com.itslegit.niroigensuntharam.hivelabs.presenter.contract.ContactsPresenterContract;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.itslegit.niroigensuntharam.hivelabs.presenter.ContactsPresenter.PERMISSIONS_REQUEST_READ_CONTACTS;
import static com.itslegit.niroigensuntharam.hivelabs.presenter.ContactsPresenter.PERMISSIONS_REQUEST_SEND_SMS;

public class ContactsActivity extends AppCompatActivity implements MaterialSearchView.OnQueryTextListener,
                                                                    MaterialSearchView.SearchViewListener,
                                                                    Button.OnClickListener,
                                                                    ContactsPresenterContract.View {


    //list of contact names
    private List<String> contacts = new ArrayList<>();

    private ContactsPresenterContract.Presenter presenter;

    //store the desired room number in order to SMS
    String roomNumber;

    private ActivityContactsBinding binding;

    ProgressBar progressBar;

    //array of hash to hold all contacts' name and phone number
    private ArrayList<HashMap<String, String>> contactData = new ArrayList<>();

    //hold the default message
    private String message = "I am in room H-";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        progressBar = new ProgressBar(this);
        progressBar.setVisibility(View.VISIBLE);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_contacts);

        roomNumber = getIntent().getExtras().getString("roomNumber");

        message = message + roomNumber;

        presenter = ContactsPresenter.buildPresenter(this, this);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, contacts);
        binding.lstNames.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        binding.lstNames.setAdapter(adapter);

        binding.searchView.setOnSearchViewListener(this);

        binding.searchView.setOnQueryTextListener(this);

        presenter.checkPermission();
    }

    @Override
    protected void onStart() {

        displayContacts();
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_contacts, menu);
        MenuItem item = menu.findItem(R.id.action_search_application);

        binding.searchView.setMenuItem(item);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                displayContacts();
            } else {
                Toast.makeText(this, getString(R.string.access_contacts), Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PERMISSIONS_REQUEST_SEND_SMS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                displayContacts();
            } else {
                Toast.makeText(this, getString(R.string.access_sms), Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void getContactNames() {
        try {
            @SuppressLint("Recycle") Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
            assert cursor != null;
            while (cursor.moveToNext()) {
                try {
                    String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                        assert phones != null;
                        while (phones.moveToNext()) {
                            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            HashMap<String, String> map = new HashMap<>();
                            map.put("name", name);
                            map.put("number", phoneNumber);
                            contactData.add(map);
                        }
                        phones.close();
                    }
                } catch (Exception ignored) {
                }
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onSearchViewShown() {

    }

    @Override
    public void onSearchViewClosed() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(ContactsActivity.this, android.R.layout.simple_list_item_multiple_choice, contacts);
        binding.lstNames.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        binding.lstNames.setAdapter(adapter);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (query != null && !query.isEmpty()) {

            List<String> lstFound = new ArrayList<>();
            List<String> tempContacts = new ArrayList<>();

            for (int i = 0; i < contacts.size(); i++) {
                tempContacts.add(contacts.get(i).toUpperCase());
            }

            for (int i = 0; i < tempContacts.size(); i++) {
                if (tempContacts.get(i).contains(query.toUpperCase())) {
                    lstFound.add(contacts.get(i));
                }
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(ContactsActivity.this, android.R.layout.simple_list_item_multiple_choice, lstFound);
            binding.lstNames.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            binding.lstNames.setAdapter(adapter);
        } else {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(ContactsActivity.this, android.R.layout.simple_list_item_multiple_choice, contacts);
            binding.lstNames.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            binding.lstNames.setAdapter(adapter);
        }

        return true;
    }

    @Override
    public void onClick(View view) {
        //variable to hold the contact's phone number
        String phoneNumber;

        //List to hold contact names that were selected for SMS
        List<String> selected = new ArrayList<>();

        //temporary array of hash to hold the selected contacts' name and phone number for SMS
        ArrayList<HashMap<String, String>> selectedContacts = new ArrayList<>();

        int cntChoice = binding.lstNames.getCount();
        SparseBooleanArray sparseBooleanArray = binding.lstNames.getCheckedItemPositions();

        for (int i = 0; i < cntChoice; i++) {
            if (sparseBooleanArray.get(i)) {
                selected.add(binding.lstNames.getItemAtPosition(i).toString());
            }
        }

        //get the selected contact info and put in a separate array
        for (int i = 0; i < selected.size(); i++) {
            for (int j = 0; j < contactData.size(); j++) {
                if (selected.get(i).contentEquals(contactData.get(j).get("name"))) {
                    selectedContacts.add(contactData.get(j));
                }
            }
        }

        SmsManager sms = SmsManager.getDefault();

        for (int i = 0; i < selectedContacts.size(); i++) {

            phoneNumber = selectedContacts.get(i).get("number");
            phoneNumber = phoneNumber.replace("(", "");
            phoneNumber = phoneNumber.replace(")", "");
            phoneNumber = phoneNumber.replace(" ", "");
            phoneNumber = phoneNumber.replace("-", "");

            try {
                sms.sendTextMessage(phoneNumber, null, message, null, null);
            } catch (Exception ignored) {
            }

            Toast.makeText(ContactsActivity.this, "Sent to " + selectedContacts.get(i).get("name"), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void displayContacts() {
        getContactNames();

        contacts = presenter.sortContacts(contactData);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, contacts);
        binding.lstNames.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        binding.lstNames.setAdapter(adapter);

        binding.getchoice.setOnClickListener(this);

        progressBar.setVisibility(View.GONE);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void requestContactsPermissions(@NonNull String[] permissions, int permissionRequest) {
        requestPermissions(permissions, permissionRequest);
    }
}
