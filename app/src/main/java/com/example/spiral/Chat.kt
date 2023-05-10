package com.example.spiral

import androidx.viewpager2.widget.ViewPager2

class Chat {
    var tabAdapter: MainActivity.TabAdapter? = null
    var viewPager: ViewPager2? = null
    var chatsData = arrayListOf<User>()
}