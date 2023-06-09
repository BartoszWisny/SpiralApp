package com.example.spiral

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

var chat = Chat()

class MainActivity : AppCompatActivity() {
    private lateinit var mainLayout: ConstraintLayout
    private lateinit var title: TextView
    private lateinit var searchText: EditText
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var topMenuBar: LinearLayout
    private lateinit var topMenuBarSearch: LinearLayout
    private lateinit var searchButton: Button
    private lateinit var settingsButton: Button
    private lateinit var searchCloseButton: Button
    private lateinit var authentication: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainLayout = findViewById(R.id.main_layout)
        title = findViewById(R.id.user_layout_title)
        searchText = findViewById(R.id.user_layout_search)
        bottomNavigationView = findViewById(R.id.bottom_menu_bar)
        topMenuBar = findViewById(R.id.top_menu_bar)
        topMenuBarSearch = findViewById(R.id.top_menu_bar_search)
        searchButton = findViewById(R.id.user_search_button)
        settingsButton = findViewById(R.id.user_settings_button)
        searchCloseButton = findViewById(R.id.user_search_close_button)
        authentication = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

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
        chat.viewPager?.currentItem = 0
        bottomNavigationView.selectedItemId = R.id.user_chats

        bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.user_chats -> chat.viewPager?.currentItem = 0
                R.id.user_profiles -> chat.viewPager?.currentItem = 1
                R.id.user_profile_display -> chat.viewPager?.currentItem = 2
                else -> chat.viewPager?.currentItem = 0
            }

            return@setOnItemSelectedListener true
        }

        chat.viewPager?.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                bottomNavigationView.selectedItemId = when (position) {
                    0 -> R.id.user_chats
                    1 -> R.id.user_profiles
                    2 -> R.id.user_profile_display
                    else -> R.id.user_chats
                }

                title.text = when (position) {
                    0 -> getString(R.string.main_chats)
                    1 -> getString(R.string.main_profiles)
                    2 -> {
                        if (chat.tabAdapter?.selectedProfile == "")  getString(R.string.main_your_profile)
                        else getString(R.string.main_profile_display)
                    }
                    else -> getString(R.string.main_chats)
                }

                searchText.hint = when (position) {
                    0 -> getString(R.string.main_search_chat)
                    1 -> getString(R.string.main_search_profile)
                    else -> getString(R.string.main_search_chat)
                }

                //when swiped from profile display - delete selected profile
                if (position != 2) chat.tabAdapter?.selectedProfile = ""
                searchCloseClick(window.decorView)
            }

        })

        searchText.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                when (chat.viewPager?.currentItem) {
                    0 -> userChatsAdapter.filterChats(searchText.text.toString())
                    1 -> userProfilesAdapter!!.filterProfiles(searchText.text.toString())
                    else -> userChatsAdapter.filterChats(searchText.text.toString())
                }
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
        database.child("users").addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chat.usersList.clear()

                for (postSnapshot in snapshot.children) {
                    val user = postSnapshot.getValue(User::class.java)

                    if (authentication.currentUser?.uid != user?.userId) {
                        chat.usersList.add(user!!)
                    } else {
                        chat.currentUser = user!!
                    }
                }

                userChatsAdapter.notifyDataSetChanged()
                if (userProfilesAdapter != null) {
                    userProfilesAdapter!!.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val rootView: View = findViewById(android.R.id.content)
                searchCloseClick(rootView)

                when (chat.viewPager?.currentItem) {
                    0 -> {
                        val intent = Intent(Intent.ACTION_MAIN)
                        intent.addCategory(Intent.CATEGORY_HOME)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    }
                    2 -> {
                        chat.tabAdapter?.selectedProfile = ""
                        chat.viewPager?.currentItem = 1
                    }
                    else -> {
                        chat.viewPager?.currentItem = 0
                    }
                }
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putCharSequence("title", title.text)
        outState.putString("selectedProfile", chat.tabAdapter?.selectedProfile)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        title.text = savedInstanceState.getCharSequence("title")
        chat.tabAdapter?.selectedProfile = savedInstanceState.getString("selectedProfile").toString()
        super.onRestoreInstanceState(savedInstanceState)
    }

    inner class TabAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {

        var selectedProfile: String = ""

        override fun getItemCount(): Int {
            return 3
        }

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> {
                    UserChatsFragment()
                }
                1 -> {
                    UserProfilesFragment()
                }
                2 -> {
                    UserProfileDisplayFragment()
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
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(view?.windowToken, 0)
        topMenuBar.visibility = View.VISIBLE

        if (chat.viewPager?.currentItem != 2) {
            searchButton.visibility = View.VISIBLE
        } else {
            searchButton.visibility = View.INVISIBLE
        }

        settingsButton.visibility = View.VISIBLE
        topMenuBarSearch.visibility = View.INVISIBLE
        searchCloseButton.visibility = View.INVISIBLE
        searchText.setText("")
    }

    fun sendMessageClick(view: View) {

    }

    fun editProfileDataClick(view: View) {

    }

    fun editProfilePhotoClick(view: View) {

    }
}