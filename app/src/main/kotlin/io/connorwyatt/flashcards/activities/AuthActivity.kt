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
import io.connorwyatt.flashcards.R
import io.connorwyatt.flashcards.helpers.auth.AuthHelper
import io.connorwyatt.flashcards.helpers.auth.exceptions.EmailAlreadyInUseException
import io.connorwyatt.flashcards.helpers.auth.exceptions.InvalidCredentialsException
import io.connorwyatt.flashcards.helpers.auth.exceptions.UserNotFoundException
import kotlinx.android.synthetic.main.activity_auth.*

class AuthActivity : AppCompatActivity() {
  private val auth = AuthHelper.getInstance()
  private val emailRegex = Regex("""^[A-Za-z0-9]+@[A-Za-z0-9]+(\.[A-Za-z0-9]+)+$""")
  private var formState = FormStates.LOGIN
  private var isLoading = false

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
    initTextFields()

    initSwitch()

    initButtons()
  }


  private fun initTextFields(): Unit {
    activity_auth_email.addRequiredValidator(getString(R.string.validation_required))
    activity_auth_email.addCustomValidator validator@ {
      val isMatch = emailRegex.matches(it.subSequence(0, it.length))

      return@validator if (!isMatch) getString(R.string.validation_email) else null
    }
    activity_auth_email.addMaxLengthValidator(255, { actualLength, maxLength ->
      getString(R.string.validation_max_length, actualLength, maxLength)
    })
    activity_auth_email.addTextChangedListener { updateUI() }

    activity_auth_password.addRequiredValidator(getString(R.string.validation_required))
    activity_auth_password.addTextChangedListener { updateUI() }
  }

  private fun initSwitch(): Unit {
    activity_auth_switch.setOnCheckedChangeListener { _, isChecked ->
      formState = if (isChecked) FormStates.REGISTER else FormStates.LOGIN

      setError(null)
      updateUI()
    }
  }

  private fun initButtons(): Unit {
    activity_auth_submit_button.setOnClickListener {
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
    val email: String = activity_auth_email.text.toString()
    val password: String = activity_auth_password.text.toString()

    return UserInput(email, password)
  }

  private fun updateUI(): Unit {
    updateButtonState()
    updateButtonLabel()
  }

  private fun updateButtonState(): Unit {
    activity_auth_submit_button.isEnabled = isValid() && !isLoading
  }

  private fun updateButtonLabel(): Unit {
    val label = when (formState) {
      FormStates.LOGIN -> getString(R.string.activity_auth_login)
      FormStates.REGISTER -> getString(R.string.activity_auth_register)
    }

    activity_auth_submit_button.text = label
  }

  private fun isValid(): Boolean =
    activity_auth_email.isValid() && activity_auth_password.isValid()

  private fun onSignIn() {
    FlashcardListActivity.startActivity(this)
    finish()
  }

  private fun setError(errorMessageResource: Int?) {
    if (errorMessageResource != null) {
      activity_auth_error_message.setText(errorMessageResource)
      activity_auth_error_message.visibility = View.VISIBLE
    } else {
      activity_auth_error_message.text = ""
      activity_auth_error_message.visibility = View.GONE
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
