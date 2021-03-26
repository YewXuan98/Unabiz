package com.example.unabiz;

import android.view.View;

import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mATR = new ActivityTestRule<MainActivity>(MainActivity.class);

    private MainActivity mA = null;
    @Before
    public void setUp() throws Exception {

        mA = mATR.getActivity();
    }


    @Test
    public void testLaunch(){
        View view = mA.findViewById(R.id.available_wifi);

        assertNotNull(view);

    }
    @After
    public void tearDown() throws Exception {

        mA=null;
    }
}