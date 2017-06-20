/*
 * Copyright (c) 2016-2017 Connor Wyatt <connorwyatt1@gmail.com>.
 *
 * This file can not be copied and/or distributed without the express permission of Connor Wyatt.
 */

package io.connorwyatt.flashcards.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import io.connorwyatt.flashcards.R
import io.connorwyatt.flashcards.helpers.auth.AuthHelper
import io.connorwyatt.flashcards.helpers.auth.exceptions.EmailAlreadyInUseException
import io.connorwyatt.flashcards.helpers.auth.exceptions.InvalidCredentialsException
import io.connorwyatt.flashcards.helpers.auth.exceptions.UserNotFoundException
import io.connorwyatt.flashcards.views.textinput.EnhancedTextInputEditText

class AuthActivity : AppCompatActivity() {
  private val auth = AuthHelper.getInstance()
  private val emailRegex = Regex("""^[A-Za-z0-9]+@[A-Za-z0-9]+(\.[A-Za-z0-9]+)+$""")
  private var formState = FormStates.LOGIN
  private var isLoading = false
  lateinit private var errorMessage: TextView
  lateinit private var email: EnhancedTextInputEditText
  lateinit private var password: EnhancedTextInputEditText
  lateinit private var submitButton: Button

  override fun onCreate(savedInstanceState: Bundle?): Unit {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_auth)

    if (auth.isSignedIn) {
      onSignIn()
    }
  }

  override fun onStart() {
    super.onStart()

    initUI()

    updateUI()
  }

  private fun initUI(): Unit {
    initErrorMessage()

    initTextFields()

    initSwitch()

    initButtons()
  }

  private fun initErrorMessage(): Unit {
    errorMessage = findViewById(R.id.activity_auth_error_message) as TextView
  }

  private fun initTextFields(): Unit {
    email = findViewById(R.id.activity_auth_email) as EnhancedTextInputEditText
    password = findViewById(R.id.activity_auth_password) as EnhancedTextInputEditText

    email.addRequiredValidator(getString(R.string.validation_required))
    email.addCustomValidator validator@ {
      val isMatch = emailRegex.matches(it.subSequence(0, it.length))

      return@validator if (!isMatch) getString(R.string.validation_email) else null
    }
    email.addMaxLengthValidator(255, { actualLength, maxLength ->
      getString(R.string.validation_max_length, actualLength, maxLength)
    })
    email.addTextChangedListener { updateUI() }

    password.addRequiredValidator(getString(R.string.validation_required))
    password.addTextChangedListener { updateUI() }
  }

  private fun initSwitch(): Unit {
    val switch = findViewById(R.id.activity_auth_switch) as Switch

    switch.setOnCheckedChangeListener { _, isChecked ->
      formState = if (isChecked) FormStates.REGISTER else FormStates.LOGIN

      setError(null)
      updateUI()
    }
  }

  private fun initButtons(): Unit {
    submitButton = findViewById(R.id.activity_auth_submit_button) as Button

    submitButton.setOnClickListener {
      val (email, password) = getUserInput()

      when (formState) {
        FormStates.LOGIN -> signIn(email, password)
        FormStates.REGISTER -> register(email, password)
      }
    }
  }

  private fun signIn(email: String, password: String): Unit {
    isLoading = true
    setError(null)
    updateUI()

    auth.loginWithEmailAndPassword(email, password) {
      isLoading = false
      updateUI()

      if (it.isSuccessful) {
        onSignIn()
      } else {
        when (it.exception) {
          is UserNotFoundException -> {
            setError(R.string.activity_auth_login_user_not_found_error)
          }
          is InvalidCredentialsException -> {
            setError(R.string.activity_auth_login_invalid_credentials_error)
          }
          else -> {
            setError(R.string.activity_auth_unknown_error)
          }
        }
      }
    }
  }

  private fun register(email: String, password: String): Unit {
    isLoading = true
    setError(null)
    updateUI()

    auth.registerWithEmailAndPassword(email, password) {
      isLoading = false
      updateUI()

      if (it.isSuccessful) {
        onSignIn()
      } else {
        when (it.exception) {
          is InvalidCredentialsException -> {
            setError(R.string.activity_auth_register_invalid_credentials_error)
          }
          is EmailAlreadyInUseException -> {
            setError(R.string.activity_auth_register_email_taken_error)
          }
          else -> {
            setError(R.string.activity_auth_unknown_error)
          }
        }
      }
    }
  }

  private fun getUserInput(): UserInput {
    val email: String = email.text.toString()
    val password: String = password.text.toString()

    return UserInput(email, password)
  }

  private fun updateUI(): Unit {
    updateButtonState()
    updateButtonLabel()
  }

  private fun updateButtonState(): Unit {
    submitButton.isEnabled = isValid() && !isLoading
  }

  private fun updateButtonLabel(): Unit {
    val label = when (formState) {
      FormStates.LOGIN -> getString(R.string.activity_auth_login)
      FormStates.REGISTER -> getString(R.string.activity_auth_register)
    }

    submitButton.text = label
  }

  private fun isValid(): Boolean =
    email.isValid() && password.isValid()

  private fun onSignIn() {
    FlashcardListActivity.startActivity(this)
    finish()
  }

  private fun setError(errorMessageResource: Int?) {
    if (errorMessageResource != null) {
      errorMessage.setText(errorMessageResource)
      errorMessage.visibility = View.VISIBLE
    } else {
      errorMessage.text = ""
      errorMessage.visibility = View.GONE
    }
  }

  private data class UserInput(val email: String, val password: String)

  enum class FormStates { LOGIN, REGISTER }

  companion object {
    fun startActivity(context: Context) {
      val intent = Intent(context, AuthActivity::class.java)

      context.startActivity(intent)
    }
  }
}
