package com.example.spiral

import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

lateinit var userFriendsAdapter: UserFriendsAdapter

class UserFriendsFragment : Fragment() {
    private lateinit var userFriendsListView: RecyclerView
    private lateinit var userFriendsRefresh: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user_friends, container, false)
        userFriendsListView = view.findViewById(R.id.user_friends_list)
        userFriendsRefresh = view.findViewById(R.id.user_friends_refresh)
        when (requireActivity().applicationContext.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {
                userFriendsRefresh.setColorSchemeColors(ResourcesCompat.getColor(resources, R.color.gray_1,
                    requireActivity().applicationContext.theme))
                userFriendsRefresh.setProgressBackgroundColorSchemeColor(ResourcesCompat.getColor(resources, R.color.gray_2,
                    requireActivity().applicationContext.theme))
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                userFriendsRefresh.setColorSchemeColors(ResourcesCompat.getColor(resources, R.color.blue_3,
                    requireActivity().applicationContext.theme))
                userFriendsRefresh.setProgressBackgroundColorSchemeColor(ResourcesCompat.getColor(resources, R.color.blue_1,
                    requireActivity().applicationContext.theme))
            }
            Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                userFriendsRefresh.setColorSchemeColors(ResourcesCompat.getColor(resources, R.color.blue_3,
                    requireActivity().applicationContext.theme))
                userFriendsRefresh.setProgressBackgroundColorSchemeColor(ResourcesCompat.getColor(resources, R.color.blue_1,
                    requireActivity().applicationContext.theme))
            }
        }
        val numberOfColumns = when (requireActivity().applicationContext.resources?.configuration?.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> 3
            Configuration.ORIENTATION_LANDSCAPE -> 6
            Configuration.ORIENTATION_UNDEFINED -> 3
            else -> 3
        }
        userFriendsListView.layoutManager = GridLayoutManager(requireActivity().applicationContext, numberOfColumns)

        val testData = arrayListOf<TestFriendData>() // test data for RecyclerView
        for (i in 1..16) {
            testData.add(TestFriendData(i))
        }

        userFriendsAdapter = UserFriendsAdapter(testData, numberOfColumns)
        userFriendsListView.adapter = userFriendsAdapter
        userFriendsRefresh.setOnRefreshListener {
            // TODO
            // userChatsListView.adapter!!.notifyDataSetChanged()
            userFriendsRefresh.isRefreshing = false
        }
        return view
    }
}