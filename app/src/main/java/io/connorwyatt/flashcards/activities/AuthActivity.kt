package io.connorwyatt.flashcards.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import io.connorwyatt.flashcards.R
import io.connorwyatt.flashcards.helpers.auth.AuthHelper
import io.connorwyatt.flashcards.listeners.SimpleTextWatcher

class AuthActivity : AppCompatActivity()
{
    private val auth = AuthHelper.getInstance()
    private var formState = FormStates.LOGIN
    private var submitButton: Button? = null

    override fun onStart()
    {
        super.onStart()

        if (auth.isSignedIn)
        {
            onSignIn()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?): Unit
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        initUI()

        updateUI()
    }

    private fun initUI(): Unit
    {
        initTextFields()

        initSwitch()

        initButtons()
    }

    private fun initTextFields(): Unit
    {
        val email = findViewById(R.id.activity_auth_email) as EditText
        val password = findViewById(R.id.activity_auth_password) as EditText

        email.addTextChangedListener(
            object : SimpleTextWatcher()
            {
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int)
                {
                    updateUI()
                }
            }
        )

        password.addTextChangedListener(
            object : SimpleTextWatcher()
            {
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int)
                {
                    updateUI()
                }
            }
        )
    }

    private fun initSwitch(): Unit
    {
        val switch = findViewById(R.id.activity_auth_switch) as Switch

        switch.setOnCheckedChangeListener { compoundButton, isChecked ->
            formState = if (isChecked) FormStates.REGISTER else FormStates.LOGIN

            updateUI()
        }
    }

    private fun initButtons(): Unit
    {
        submitButton = findViewById(R.id.activity_auth_submit_button) as Button

        submitButton!!.setOnClickListener {
            val (email, password) = getUserInput()

            when (formState)
            {
                FormStates.LOGIN -> signIn(email, password)
                FormStates.REGISTER -> register(email, password)
            }
        }
    }

    private fun signIn(email: String, password: String): Unit
    {
        auth.loginWithEmailAndPassword(email, password, {
            if (it.isSuccessful)
            {
                onSignIn()
            }
        })
    }

    private fun register(email: String, password: String): Unit
    {
        auth.registerWithEmailAndPassword(email, password, {
            if (it.isSuccessful)
            {
                onSignIn()
            }
        })
    }

    private fun getUserInput(): UserInput
    {
        val email: String = (findViewById(R.id.activity_auth_email) as EditText).text.toString()
        val password: String = (findViewById(R.id.activity_auth_password) as EditText).text.toString()

        return UserInput(email, password)
    }

    private fun updateUI(): Unit
    {
        updateButtonState()
        updateButtonLabel()
    }

    private fun updateButtonState(): Unit
    {
        submitButton!!.isEnabled = isValid()
    }

    private fun updateButtonLabel(): Unit
    {
        var label: String? = null

        when (formState)
        {
            FormStates.LOGIN -> label = getString(R.string.activity_auth_login)
            FormStates.REGISTER -> label = getString(R.string.activity_auth_register)
        }

        label.let { submitButton!!.text = it }
    }

    private fun isValid(): Boolean =
        getEmailErrorMessage() === null && getPasswordErrorMessage() === null

    private fun getEmailErrorMessage(): String?
    {
        val email: String = (findViewById(R.id.activity_auth_email) as EditText).text.toString()

        when
        {
            email.length === 0 ->
            {
                return getString(R.string.validation_required)
            }
            email.length > 255 ->
            {
                return getString(R.string.validation_max_length, email.length, 255)
            }
            else -> return null
        }
    }

    private fun getPasswordErrorMessage(): String?
    {
        val password: String = (findViewById(R.id.activity_auth_password) as EditText).text.toString()

        when
        {
            password.length === 0 ->
            {
                return getString(R.string.validation_required)
            }
            else -> return null
        }
    }

    private fun onSignIn()
    {
        finish()
    }

    private data class UserInput(val email: String, val password: String)

    enum class FormStates
    { LOGIN, REGISTER }

    companion object
    {
        fun startActivity(context: Context)
        {
            val intent = Intent(context, AuthActivity::class.java)

            context.startActivity(intent)
        }
    }
}
