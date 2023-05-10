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

lateinit var userProfilesAdapter: UserProfilesAdapter

class UserProfilesFragment : Fragment() {
    private lateinit var userProfilesListView: RecyclerView
    private lateinit var userProfilesRefresh: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user_profiles, container, false)
        userProfilesListView = view.findViewById(R.id.user_profiles_list)
        userProfilesRefresh = view.findViewById(R.id.user_profiles_refresh)
        when (requireActivity().applicationContext.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {
                userProfilesRefresh.setColorSchemeColors(ResourcesCompat.getColor(resources, R.color.gray_1,
                    requireActivity().applicationContext.theme))
                userProfilesRefresh.setProgressBackgroundColorSchemeColor(ResourcesCompat.getColor(resources, R.color.gray_2,
                    requireActivity().applicationContext.theme))
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                userProfilesRefresh.setColorSchemeColors(ResourcesCompat.getColor(resources, R.color.blue_3,
                    requireActivity().applicationContext.theme))
                userProfilesRefresh.setProgressBackgroundColorSchemeColor(ResourcesCompat.getColor(resources, R.color.blue_1,
                    requireActivity().applicationContext.theme))
            }
            Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                userProfilesRefresh.setColorSchemeColors(ResourcesCompat.getColor(resources, R.color.blue_3,
                    requireActivity().applicationContext.theme))
                userProfilesRefresh.setProgressBackgroundColorSchemeColor(ResourcesCompat.getColor(resources, R.color.blue_1,
                    requireActivity().applicationContext.theme))
            }
        }
        val numberOfColumns = when (requireActivity().applicationContext.resources?.configuration?.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> 2
            Configuration.ORIENTATION_LANDSCAPE -> 4
            Configuration.ORIENTATION_UNDEFINED -> 2
            else -> 2
        }
        userProfilesListView.layoutManager = GridLayoutManager(requireActivity().applicationContext, numberOfColumns)

        val testData = arrayListOf<TestProfileData>() // test data for RecyclerView
        for (i in 1..16) {
            testData.add(TestProfileData(i))
        }

        userProfilesAdapter = UserProfilesAdapter(testData)
        userProfilesListView.adapter = userProfilesAdapter
        userProfilesRefresh.setOnRefreshListener {
            // TODO
            userProfilesRefresh.isRefreshing = false
        }
        return view
    }
}