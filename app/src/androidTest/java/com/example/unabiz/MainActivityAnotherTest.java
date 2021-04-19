package com.example.unabiz;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@LargeTest

public class MainActivityAnotherTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule
            = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void scanButtonPress() {
        // pressing the button works test

        onView(withId(R.id.button_scanmode)).perform(click());


    }
    @Test
    public void mapButtonPress() {
        onView(withId(R.id.button_mappingmode)).perform(click());

    }
    @Test
    public void testButtonPress() {
        onView(withId(R.id.button_testmode)).perform(click());

    }
    @Test
    public void mainScanButtonPress() {

        onView(withId(R.id.start_scan)).perform(click());

    }

}
