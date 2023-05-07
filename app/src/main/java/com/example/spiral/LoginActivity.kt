package com.example.spiral

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat

class LoginActivity : AppCompatActivity() {
    private lateinit var loginLayout: ConstraintLayout
    private lateinit var loginAppName: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loginLayout = findViewById(R.id.login_layout)
        loginAppName = findViewById(R.id.login_app_name)

        when (applicationContext.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {
                loginLayout.background = ResourcesCompat.getDrawable(resources, R.color.gray_2, applicationContext.theme)
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                loginLayout.background = ResourcesCompat.getDrawable(resources, R.color.blue_1, applicationContext.theme)
            }
            Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                loginLayout.background = ResourcesCompat.getDrawable(resources, R.color.gray_2, applicationContext.theme)
            }
        }

        val typeface: Typeface? = ResourcesCompat.getFont(this, R.font.insomnia)
        loginAppName.typeface = typeface

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(Intent.ACTION_MAIN)
                intent.addCategory(Intent.CATEGORY_HOME)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
        })
    }

    fun loginClick(view: View) {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    fun loginGoogleClick(view: View) {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    fun loginEmailClick(view: View) {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}