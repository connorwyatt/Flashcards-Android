package io.connorwyatt.flashcards.views.textinput

import android.content.Context
import android.graphics.Rect
import android.support.design.widget.TextInputEditText
import android.util.AttributeSet

class EnhancedTextInputEditText : TextInputEditText
{
    private var touched = false
    private var dirty = false

    constructor(context: Context) : super(context)
    {
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    {
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) :
        super(context, attrs, defStyleAttr)
    {
    }

    //region Listeners

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?)
    {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)

        if (!focused) touched = true
    }

    override fun onTextChanged(text: CharSequence?, start: Int, lengthBefore: Int, lengthAfter: Int)
    {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)

        if (lengthBefore != lengthAfter) dirty = true
    }

    //endregion

    //region Status Accessors

    fun isInputTouched() = touched
    fun isInputUntouched() = !touched
    fun isInputDirty() = dirty
    fun isInputPristine() = !dirty

    //endregion
}
