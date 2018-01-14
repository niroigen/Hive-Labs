package com.example.niroigensuntharam.elec390application;

import android.*;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.ACCESS_WIFI_STATE;
import static android.Manifest.permission.CHANGE_WIFI_STATE;

/**
 * Created by niroigensuntharam on 2017-11-29.
 */

public class IntroActivity extends AppIntro {

    private static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 1;
    // MY_PREFS_NAME - a static String variable like:
    public static final String MY_PREFS_NAME = "MyPrefsFile";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SliderPage applicationsInClass = new SliderPage();

        applicationsInClass.setTitle("Get Applications In Room");
        applicationsInClass.setDescription("By simply holding on the any of the rooms, you can find out the softwares which are available in that room");
        applicationsInClass.setImageDrawable(R.drawable.search_app);

        SliderPage searchApplication = new SliderPage();

        searchApplication.setTitle("Search For Application");
        searchApplication.setDescription("If you want to use a specific software, by using H.I.V.E Labs you can search for that with ease!");
        searchApplication.setImageDrawable(R.drawable.search_image);

        SliderPage navigate = new SliderPage();

        navigate.setTitle("Navigate to your room");
        navigate.setDescription("If you are having trouble finding a certain room, you can navigate to it using our application");
        navigate.setImageDrawable(R.drawable.navigate_image);

        SliderPage notify = new SliderPage();

        notify.setTitle("Get notified");
        notify.setDescription("As a user you will receive notifications whenever there will be an upcoming class in your room!");
        notify.setImageDrawable(R.drawable.notify_image);

        SliderPage message = new SliderPage();

        message.setTitle("Leave no friend behind");
        message.setDescription("Message all your friends which lab you are in via SMS without leaving the app!");
        message.setImageDrawable(R.drawable.sms_image);

        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest.
        addSlide(AppIntroFragment.newInstance(applicationsInClass));
        addSlide(AppIntroFragment.newInstance(searchApplication));
        addSlide(AppIntroFragment.newInstance(navigate));
        addSlide(AppIntroFragment.newInstance(notify));
        addSlide(AppIntroFragment.newInstance(message));

        setFadeAnimation();

        // OPTIONAL METHODS
        // Override bar/separator color.
        setBarColor(Color.parseColor("#3F51B5"));
        setSeparatorColor(Color.parseColor("#2196F3"));

        // Hide Skip/Done button.
        showSkipButton(true);
        setProgressButtonEnabled(true);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.

        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putBoolean("doneTutorial",true);
        editor.apply();

        finish();

        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        // Do something when users tap on Done button.

        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putBoolean("doneTutorial",true);
        editor.putBoolean("requestAsked", false);
        editor.apply();

        finish();

        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }
}