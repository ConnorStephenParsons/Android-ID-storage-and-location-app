package com.example.a300cemproject;

import android.view.View;

import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class RegisterAccountTest {
    //These unit tests checks if the input sections for the user are valid for the user before starting the application
    @Rule
    public ActivityTestRule<RegisterAccount> mActivityTestRule = new ActivityTestRule<RegisterAccount>(RegisterAccount.class);
    private RegisterAccount mActivity = null;

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

        View test3 = mActivity.findViewById(R.id.name);
        assertNotNull(test3);

        View test4 = mActivity.findViewById(R.id.age);
        assertNotNull(test4);

    }

    @After
    public void tearDown() throws Exception {
        mActivity = null;
    }
}