package com.example.spiral

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class UserChatsFragment : Fragment() {
    private lateinit var userChatsListView: RecyclerView
    private lateinit var userChatsAdapter: UserChatsAdapter
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_user_chats, container, false)
        userChatsListView = view.findViewById(R.id.user_chats_list)
        userChatsListView.layoutManager = LinearLayoutManager(requireActivity().applicationContext)

        val testData = arrayListOf<TestMessage>() // test data for RecyclerView
        for (i in 1..10) {
            testData.add(TestMessage())
        }

        userChatsAdapter = UserChatsAdapter(testData)
        userChatsListView.adapter = userChatsAdapter
        return view
    }
}