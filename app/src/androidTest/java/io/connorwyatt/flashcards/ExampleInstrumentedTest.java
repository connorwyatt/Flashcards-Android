/*
 * Copyright (c) 2016-2017 Connor Wyatt <connorwyatt1@gmail.com>.
 *
 * This file can not be copied and/or distributed without the express permission of Connor Wyatt.
 */

package io.connorwyatt.flashcards;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("io.connorwyatt.flashcards", appContext.getPackageName());
    }
}
