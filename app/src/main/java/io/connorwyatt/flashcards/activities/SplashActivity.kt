package io.connorwyatt.flashcards.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.connorwyatt.flashcards.R

class SplashActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
    }
}
