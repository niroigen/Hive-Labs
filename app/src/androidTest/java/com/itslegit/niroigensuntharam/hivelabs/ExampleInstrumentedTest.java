package com.itslegit.niroigensuntharam.hivelabs;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.action.ViewActions.swipeDown;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
            MainActivity.class);

    @Test
    public void LongClickOnItem() {
        onView(withId(R.id.text_list_view)).perform(longClick());
    }

    @Test
    public void ClickOnItem(){
        onView(withId(R.id.text_list_view)).perform(click());
        onView(withId(R.id.currentRoomButton)).perform(click());
    }

    @Test
    public void SwipeDown(){
        onView(withId(R.id.text_list_view)).perform(swipeDown());
    }
}
