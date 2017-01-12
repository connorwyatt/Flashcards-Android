package io.connorwyatt.flashcards.activities

import android.support.v7.app.AppCompatActivity
import io.connorwyatt.flashcards.helpers.auth.AuthHelper

open class BaseActivity : AppCompatActivity()
{
    protected val auth = AuthHelper.getInstance()

    override fun onStart(): Unit
    {
        super.onStart()

        if (!auth.isSignedIn) {
            AuthActivity.startActivity(this)
        }
    }

    fun signOut(): Unit
    {
        auth.signOut()

        recreate()
    }
}
