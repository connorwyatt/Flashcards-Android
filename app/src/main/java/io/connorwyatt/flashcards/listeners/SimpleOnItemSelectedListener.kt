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
