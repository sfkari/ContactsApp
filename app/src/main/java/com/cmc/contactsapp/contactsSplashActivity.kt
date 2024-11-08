package com.cmc.contactsapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class contactsSplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_contacts_splash)

        val logo = findViewById<ImageView>(R.id.splashLogo)

        logo.alpha = 0f
        logo.animate().alpha(1f).setDuration(3000)

        val splashDuration: Long = 4000
        Handler().postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, splashDuration)

    }
}