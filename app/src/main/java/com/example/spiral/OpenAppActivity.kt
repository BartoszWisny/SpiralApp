package com.example.spiral

import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.motion.widget.MotionLayout.TransitionListener
import androidx.core.content.res.ResourcesCompat

class OpenAppActivity : AppCompatActivity() {
    private lateinit var openAppLayout: MotionLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open_app)
        openAppLayout = findViewById(R.id.open_app_layout)

        when (applicationContext.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {
                openAppLayout.background = ResourcesCompat.getDrawable(resources, R.color.gray_2, applicationContext.theme)
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                openAppLayout.background = ResourcesCompat.getDrawable(resources, R.color.blue_1, applicationContext.theme)
            }
            Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                openAppLayout.background = ResourcesCompat.getDrawable(resources, R.color.gray_2, applicationContext.theme)
            }
        }

        val transitionListener = object : TransitionListener {
            override fun onTransitionStarted(p0: MotionLayout?, startId: Int, endId: Int) {}

            override fun onTransitionChange(p0: MotionLayout?, startId: Int, endId: Int, progress: Float) {}

            override fun onTransitionCompleted(p0: MotionLayout?, currentId: Int) {
                val intent = Intent(this@OpenAppActivity, MainActivity::class.java)
                startActivity(intent)
            }

            override fun onTransitionTrigger(p0: MotionLayout?, triggerId: Int, positive: Boolean, progress: Float) {}
        }

        openAppLayout.setTransitionListener(transitionListener)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(Intent.ACTION_MAIN)
                intent.addCategory(Intent.CATEGORY_HOME)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
        })
    }
}