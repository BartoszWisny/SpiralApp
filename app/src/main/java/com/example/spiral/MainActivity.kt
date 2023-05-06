package com.example.spiral

import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView

var chat = Chat()

class MainActivity : AppCompatActivity() {
    private lateinit var mainLayout: ConstraintLayout
    private lateinit var title: TextView
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainLayout = findViewById(R.id.main_layout)
        title = findViewById(R.id.user_layout_title)
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
        bottomNavigationView.selectedItemId = R.id.user_chats

        bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.user_friends -> chat.viewPager?.currentItem = 0
                R.id.user_chats -> chat.viewPager?.currentItem = 1
                R.id.user_profile -> chat.viewPager?.currentItem = 2
                else -> chat.viewPager?.currentItem = 1
            }

            return@setOnItemSelectedListener true
        }

        chat.viewPager?.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                bottomNavigationView.selectedItemId = when (position) {
                    0 -> R.id.user_friends
                    1 -> R.id.user_chats
                    2 -> R.id.user_profile
                    else -> R.id.user_chats
                }

                title.text = when (position) {
                    0 -> "Friends"
                    1 -> "Chats"
                    2 -> "Profile"
                    else -> "Chats"
                }
            }
        })

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (chat.viewPager?.currentItem == 1) {
                    val intent = Intent(Intent.ACTION_MAIN)
                    intent.addCategory(Intent.CATEGORY_HOME)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                } else {
                    chat.viewPager?.currentItem = 1
                }
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putCharSequence("title", title.text)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        title.text = savedInstanceState.getCharSequence("title")
        super.onRestoreInstanceState(savedInstanceState)
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
}