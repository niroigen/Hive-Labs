package com.itslegit.niroigensuntharam.hivelabs.presenter.contract;

import com.google.firebase.database.DatabaseReference;
import com.itslegit.niroigensuntharam.hivelabs.activities.MainActivity;

/**
 * Created by niroigensuntharam on 2018-02-11.
 */

public interface MainPresenterContract {
    interface Presenter {
        boolean isConnected();
        void verifyQueryRoom(String query);
        void verifyQueryApplication(String query);
    }

    interface View {
        void setupViewPager();
    }
}
