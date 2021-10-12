package com.example.a300cemproject;

import android.view.View;

import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class IdentityActivityTest {
    //These unit tests checks if the camera or image has put a bitmap image into the photo image holder.
    @Rule
    public ActivityTestRule<IdentityActivity> mActivityTestRule = new ActivityTestRule<IdentityActivity>(IdentityActivity.class);
    private IdentityActivity mActivity = null;

    @Before
    public void setUp() throws Exception {

        mActivity = mActivityTestRule.getActivity();
    }

    @Test
    public void testLaunch() {
        View test1 = mActivity.findViewById(R.id.studentId);
        assertNotNull(test1);


    }

    @After
    public void tearDown() throws Exception {
        mActivity = null;
    }
}