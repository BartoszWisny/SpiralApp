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

class UserProfileFragment : Fragment() {
    private lateinit var userProfileListView: RecyclerView
    private lateinit var userProfileAdapter: UserProfileAdapter
    private lateinit var userProfileRefresh: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user_profile, container, false)
        userProfileListView = view.findViewById(R.id.user_profile_list)
        userProfileRefresh = view.findViewById(R.id.user_profile_refresh)
        when (requireActivity().applicationContext.resources?.configuration?.uiMode?.and(
            Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {
                userProfileRefresh.setColorSchemeColors(ResourcesCompat.getColor(resources, R.color.gray_1,
                    requireActivity().applicationContext.theme))
                userProfileRefresh.setProgressBackgroundColorSchemeColor(ResourcesCompat.getColor(resources, R.color.gray_2,
                    requireActivity().applicationContext.theme))
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                userProfileRefresh.setColorSchemeColors(ResourcesCompat.getColor(resources, R.color.blue_3,
                    requireActivity().applicationContext.theme))
                userProfileRefresh.setProgressBackgroundColorSchemeColor(ResourcesCompat.getColor(resources, R.color.blue_1,
                    requireActivity().applicationContext.theme))
            }
            Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                userProfileRefresh.setColorSchemeColors(ResourcesCompat.getColor(resources, R.color.blue_3,
                    requireActivity().applicationContext.theme))
                userProfileRefresh.setProgressBackgroundColorSchemeColor(ResourcesCompat.getColor(resources, R.color.blue_1,
                    requireActivity().applicationContext.theme))
            }
        }
        userProfileListView.layoutManager = LinearLayoutManager(requireActivity().applicationContext)

        val testData = arrayListOf<TestUserData>() // test data for RecyclerView
        testData.add(TestUserData())

        userProfileAdapter = UserProfileAdapter(testData)
        userProfileListView.adapter = userProfileAdapter
        userProfileRefresh.setOnRefreshListener {
            // TODO
            // userChatsListView.adapter!!.notifyDataSetChanged()
            userProfileRefresh.isRefreshing = false
        }
        return view
    }
}