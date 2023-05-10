package com.example.spiral

import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

lateinit var userChatsAdapter: UserChatsAdapter

class UserChatsFragment : Fragment() {
    private lateinit var userChatsListView: RecyclerView
    private lateinit var userChatsRefresh: SwipeRefreshLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_user_chats, container, false)
        userChatsListView = view.findViewById(R.id.user_chats_list)
        userChatsRefresh = view.findViewById(R.id.user_chats_refresh)
        when (requireActivity().applicationContext.resources?.configuration?.uiMode?.and(
            Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {
                userChatsRefresh.setColorSchemeColors(ResourcesCompat.getColor(resources, R.color.gray_1,
                    requireActivity().applicationContext.theme))
                userChatsRefresh.setProgressBackgroundColorSchemeColor(ResourcesCompat.getColor(resources, R.color.gray_2,
                    requireActivity().applicationContext.theme))
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                userChatsRefresh.setColorSchemeColors(ResourcesCompat.getColor(resources, R.color.blue_3,
                    requireActivity().applicationContext.theme))
                userChatsRefresh.setProgressBackgroundColorSchemeColor(ResourcesCompat.getColor(resources, R.color.blue_1,
                    requireActivity().applicationContext.theme))
            }
            Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                userChatsRefresh.setColorSchemeColors(ResourcesCompat.getColor(resources, R.color.blue_3,
                    requireActivity().applicationContext.theme))
                userChatsRefresh.setProgressBackgroundColorSchemeColor(ResourcesCompat.getColor(resources, R.color.blue_1,
                    requireActivity().applicationContext.theme))
            }
        }
        userChatsListView.layoutManager = LinearLayoutManager(requireActivity().applicationContext)
        userChatsAdapter = UserChatsAdapter(requireContext(), chat.chatsData)
        userChatsListView.adapter = userChatsAdapter
        userChatsRefresh.setOnRefreshListener {
            // TODO
            // userChatsListView.adapter!!.notifyDataSetChanged()
            userChatsRefresh.isRefreshing = false
        }
        return view
    }
}