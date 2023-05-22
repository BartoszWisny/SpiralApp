package com.example.spiral

import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class UserSettingsActivity : AppCompatActivity() {
    private lateinit var userSettingsLayout: ConstraintLayout
    private lateinit var authentication: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var googleSignInOptions: GoogleSignInOptions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_settings)
        userSettingsLayout = findViewById(R.id.user_settings_layout)
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