package com.example.spiral

import androidx.viewpager2.widget.ViewPager2

class Chat {
    var tabAdapter: MainActivity.TabAdapter? = null
    var viewPager: ViewPager2? = null
    var chatsData = arrayListOf<User>()
    // var chatsDataKeyList = arrayListOf<String>()
    var messagesList = arrayListOf<Message>()
    // var messagesKeyList = arrayListOf<String>()
    val storageUrl = "gs://spiralapp-828a8.appspot.com"
}