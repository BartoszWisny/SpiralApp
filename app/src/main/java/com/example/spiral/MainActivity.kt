package com.example.spiral

import android.content.res.ColorStateList
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView

var chat = Chat()

class MainActivity : AppCompatActivity() {
    private lateinit var mainLayout: ConstraintLayout
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainLayout = findViewById(R.id.main_layout)
        bottomNavigationView = findViewById(R.id.bottom_menu_bar)

        when (applicationContext.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {
                mainLayout.background = ResourcesCompat.getDrawable(resources, R.drawable.background_dark_mode,
                    applicationContext.theme)
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                mainLayout.background = ResourcesCompat.getDrawable(resources, R.drawable.background_light_mode,
                    applicationContext.theme)
            }
            Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                mainLayout.background = ResourcesCompat.getDrawable(resources, R.drawable.background_light_mode,
                    applicationContext.theme)
            }
        }

        chat.tabAdapter = TabAdapter(this)
        chat.viewPager = findViewById(R.id.main_pager)
        chat.viewPager?.adapter = chat.tabAdapter
        chat.viewPager?.currentItem = 1
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (chat.viewPager?.currentItem == 1) {
                    finish()
                } else {
                    chat.viewPager?.currentItem = 1
                    chat.viewPager?.adapter = chat.tabAdapter
                }
            }
        })
    }

    inner class TabAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
        override fun getItemCount(): Int {
            return 3
        }

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> {
                    UserFriendsFragment()
                }
                1 -> {
                    UserChatsFragment()
                }
                2 -> {
                    UserProfileFragment()
                }
                else -> {
                    UserChatsFragment()
                }
            }
        }
    }

    fun onUserFriendsClick(item: MenuItem) {
        chat.viewPager?.currentItem = 0
        chat.viewPager?.adapter = chat.tabAdapter
    }

    fun onUserChatsClick(item: MenuItem) {
        chat.viewPager?.currentItem = 1
        chat.viewPager?.adapter = chat.tabAdapter
    }

    fun onUserProfileClick(item: MenuItem) {
        chat.viewPager?.currentItem = 2
        chat.viewPager?.adapter = chat.tabAdapter
    }
}