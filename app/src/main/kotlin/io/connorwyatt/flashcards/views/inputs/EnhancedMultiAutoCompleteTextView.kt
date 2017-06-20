package io.connorwyatt.flashcards.views.inputs

import android.content.Context
import android.graphics.Rect
import android.support.design.widget.TextInputLayout
import android.util.AttributeSet
import android.view.View
import android.widget.MultiAutoCompleteTextView

class EnhancedMultiAutoCompleteTextView : MultiAutoCompleteTextView {
  private var touched = false
  private var dirty = false
  private var valid = true
  private val textChangedListeners: MutableList<() -> Unit> = mutableListOf()
  private val validators: MutableList<(String) -> String?> = mutableListOf()
  private val textInputLayout by lazy {
    getParentTextInputLayout()
  }

  constructor(context: Context) : super(context) {
    initialise()
  }

  constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    initialise()
  }

  constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) :
    super(context, attrs, defStyleAttr) {
    initialise()
  }

  private fun initialise() {
    setTokenizer(MultiAutoCompleteTextView.CommaTokenizer())
  }

  private fun getParentTextInputLayout(): TextInputLayout? {
    var parent = parent

    while (parent is View) {
      if (parent is TextInputLayout) return parent

      parent = parent.getParent()
    }

    return null
  }

  //region Listeners

  override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
    super.onFocusChanged(focused, direction, previouslyFocusedRect)

    if (!focused) touched = true

    runValidators(editableText.toString())
  }

  override fun onTextChanged(text: CharSequence?, start: Int, lengthBefore: Int, lengthAfter: Int) {
    super.onTextChanged(text, start, lengthBefore, lengthAfter)

    if (lengthBefore != lengthAfter) {
      dirty = true

      runValidators(editableText.toString())

      callTextChangedListeners()
    }
  }

  fun addTextChangedListener(listener: () -> Unit): () -> Unit {
    textChangedListeners.add(listener)

    return { textChangedListeners.removeAll(listOf(listener)) }
  }

  private fun callTextChangedListeners(): Unit {
    textChangedListeners.forEach { it.invoke() }
  }

  //endregion

  //region Status Accessors

  fun isInputTouched() = touched
  fun isInputUntouched() = !touched
  fun isInputDirty() = dirty
  fun isInputPristine() = !dirty

  //endregion

  //region Validation

  fun isValid() = valid
  fun isInvalid() = !valid

  fun addTagMaxLengthValidator(maxLength: Int, getErrorMessage: (Int) -> String): () -> Unit {
    val validator = validator@{ value: String ->
      val tags = value.split(",").map(String::trim)

      tags.forEach { tag ->
        if (tag.length > maxLength) {
          return@validator getErrorMessage.invoke(maxLength)
        }
      }

      return@validator null
    }

    validators.add(validator)

    runValidators(editableText.toString())

    return { validators.removeAll(listOf(validator)) }
  }

  fun addMaxLengthValidator(maxLength: Int, getErrorMessage: (Int, Int) -> String): () -> Unit {
    val validator = { value: String ->
      var errorMessage: String? = null

      if (value.length > maxLength)
        errorMessage = getErrorMessage.invoke(value.length, maxLength)

      errorMessage
    }

    validators.add(validator)

    runValidators(editableText.toString())

    return { validators.removeAll(listOf(validator)) }
  }

  fun addCustomValidator(validator: (String) -> String?): () -> Unit {
    validators.add(validator)

    runValidators(editableText.toString())

    return { validators.removeAll(listOf(validator)) }
  }

  private fun runValidators(value: String): Unit {
    valid = true

    validators.forEach { validator ->
      val errorMessage = validator.invoke(value)

      errorMessage?.let {
        valid = false

        if (touched || dirty) setErrorMessage(it)

        return
      }
    }

    clearErrorMessage()
  }

  private fun setErrorMessage(message: String): Unit {
    textInputLayout?.error = message
  }

  private fun clearErrorMessage(): Unit {
    textInputLayout?.isErrorEnabled = false
  }

  //endregion
}
