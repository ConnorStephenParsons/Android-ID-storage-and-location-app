package com.example.a300cemproject;

import android.view.View;

import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class MainActivityTest {

    //These unit tests checks if the email and password sections are present before starting the application
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class);
    private MainActivity mActivity = null;

    @Before
    public void setUp() throws Exception {

        mActivity = mActivityTestRule.getActivity();
    }

    @Test
    public void testLaunch() {
        View test1 = mActivity.findViewById(R.id.emailAddress);
        assertNotNull(test1);

        View test2 = mActivity.findViewById(R.id.password);
        assertNotNull(test2);

    }

    @After
    public void tearDown() throws Exception {
        mActivity = null;
    }
}