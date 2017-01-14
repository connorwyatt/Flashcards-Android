/*
 * Copyright (c) 2016-2017 Connor Wyatt <connorwyatt1@gmail.com>.
 *
 * This file can not be copied and/or distributed without the express permission of Connor Wyatt.
 */

package io.connorwyatt.flashcards.listeners

import android.view.View
import android.widget.AdapterView


abstract class SimpleOnItemSelectedListener : AdapterView.OnItemSelectedListener
{
    override fun onNothingSelected(adapterView: AdapterView<*>)
    {
    }

    override fun onItemSelected(adapterView: AdapterView<*>, view: View?, position: Int, id: Long)
    {
    }
}
