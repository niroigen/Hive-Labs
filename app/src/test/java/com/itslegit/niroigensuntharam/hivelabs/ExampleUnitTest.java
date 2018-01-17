package com.itslegit.niroigensuntharam.hivelabs;

//import org.junit.Before;
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import android.support.test.rule.ActivityTestRule;
//import android.support.test.runner.AndroidJUnit4;
//import android.test.suitebuilder.annotation.LargeTest;
//
//@RunWith(Android.class)
//@LargeTest
//public class ChangeTextBehaviorTest {
//
//    private String mStringToBetyped;
//
//    @Rule
//    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
//            MainActivity.class);
//
//    @Before
//    public void initValidString() {
//        // Specify a valid string.
//        mStringToBetyped = "Espresso";
//    }
//
//    @Test
//    public void changeText_sameActivity() {
//        // Type text and then press the button.
//        onView(withId(R.id.editTextUserInput))
//                .perform(typeText(mStringToBetyped), closeSoftKeyboard());
//        onView(withId(R.id.changeTextBt)).perform(click());
//
//        // Check that the text was changed.
//        onView(withId(R.id.textToBeChanged))
//                .check(matches(withText(mStringToBetyped)));
//    }
//}