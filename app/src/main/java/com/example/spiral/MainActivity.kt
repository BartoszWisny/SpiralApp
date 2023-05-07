package com.example.spiral

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
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
    private lateinit var search: EditText
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var topMenuBar: LinearLayout
    private lateinit var topMenuBarSearch: LinearLayout
    private lateinit var searchButton: Button
    private lateinit var settingsButton: Button
    private lateinit var searchCloseButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainLayout = findViewById(R.id.main_layout)
        title = findViewById(R.id.user_layout_title)
        search = findViewById(R.id.user_layout_search)
        bottomNavigationView = findViewById(R.id.bottom_menu_bar)
        topMenuBar = findViewById(R.id.top_menu_bar)
        topMenuBarSearch = findViewById(R.id.top_menu_bar_search)
        searchButton = findViewById(R.id.user_search_button)
        settingsButton = findViewById(R.id.user_settings_button)
        searchCloseButton = findViewById(R.id.user_search_close_button)

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

                search.hint = when (position) {
                    0 -> "Search friend by username"
                    1 -> "Search chat by username"
                    2 -> "Search profile by username"
                    else -> "Search chat by username"
                }

                searchCloseClick(window.decorView)
            }
        })

        search.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                when (chat.viewPager?.currentItem) {
                    0 -> userFriendsAdapter.filterFriends(search.text.toString())
                    1 -> userChatsAdapter.filterChats(search.text.toString())
                    2 -> userProfileAdapter.filterProfiles(search.text.toString())
                    else -> userChatsAdapter.filterChats(search.text.toString())
                }
            }

            override fun afterTextChanged(p0: Editable?) {}
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

    fun searchClick(view: View) {
        topMenuBar.visibility = View.INVISIBLE
        searchButton.visibility = View.INVISIBLE
        settingsButton.visibility = View.INVISIBLE
        topMenuBarSearch.visibility = View.VISIBLE
        searchCloseButton.visibility = View.VISIBLE
    }

    fun settingsClick(view: View) {
        val intent = Intent(this, UserSettingsActivity::class.java)
        startActivity(intent)
    }

    fun searchCloseClick(view: View?) {
        topMenuBar.visibility = View.VISIBLE
        searchButton.visibility = View.VISIBLE
        settingsButton.visibility = View.VISIBLE
        topMenuBarSearch.visibility = View.INVISIBLE
        searchCloseButton.visibility = View.INVISIBLE
        search.setText("")
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(view?.windowToken, 0)
    }
}