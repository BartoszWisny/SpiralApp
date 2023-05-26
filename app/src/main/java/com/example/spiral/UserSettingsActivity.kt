package com.example.spiral

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.Locale


class UserSettingsActivity : AppCompatActivity() {
    private lateinit var userSettingsLayout: ConstraintLayout
    private lateinit var authentication: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var googleSignInOptions: GoogleSignInOptions
    private lateinit var languageSelectionSpinner: Spinner

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_settings)
        userSettingsLayout = findViewById(R.id.user_settings_layout)
        languageSelectionSpinner = findViewById(R.id.language_selection)
        val adapter = ArrayAdapter(this, R.layout.spinner, arrayOf("English", "Polski"))
        adapter.setDropDownViewResource(R.layout.spinner_item)
        languageSelectionSpinner.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val snackbar = Snackbar.make(
                    findViewById(android.R.id.content),
                    getString(R.string.settings_no_language_changing),
                    Snackbar.LENGTH_SHORT
                )
                val snackbarView = snackbar.view
                snackbarView.setBackgroundResource(R.drawable.item_shape)
                snackbar.setTextColor(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.snackbarText,
                        application.theme
                    )
                )
                val textView: TextView =
                    snackbarView.findViewById(com.google.android.material.R.id.snackbar_text)
                textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
                snackbar.show()
            }
            true
        }
        languageSelectionSpinner.adapter = adapter
        val selection = when (Locale.getDefault().language) {
            "en" -> 0
            "pl" -> 1
            else -> 0
        }
        languageSelectionSpinner.setSelection(selection)
        authentication = FirebaseAuth.getInstance()
        googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        when (applicationContext.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {
                userSettingsLayout.background = ResourcesCompat.getDrawable(resources, R.color.gray_2, applicationContext.theme)
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                userSettingsLayout.background = ResourcesCompat.getDrawable(resources, R.color.blue_1, applicationContext.theme)
            }
            Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                userSettingsLayout.background = ResourcesCompat.getDrawable(resources, R.color.gray_2, applicationContext.theme)
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
    }

    fun signOutClick(view: View) {
        Firebase.auth.signOut()
        googleSignInClient.signOut()
        chat.usersList.clear()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

}