package com.example.spiral

import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

lateinit var userProfileDisplayAdapter: UserProfileDisplayAdapter

class UserProfileDisplayFragment : Fragment() {
    private lateinit var userProfileDisplayListView: RecyclerView
    private lateinit var userProfileDisplayRefresh: SwipeRefreshLayout
    private lateinit var userProfileDisplayPhoto: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user_profile_display, container, false)
        userProfileDisplayListView = view.findViewById(R.id.user_profile_display_list)
        userProfileDisplayRefresh = view.findViewById(R.id.user_profile_display_refresh)

        when (requireActivity().applicationContext.resources?.configuration?.uiMode?.and(
            Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {
                userProfileDisplayRefresh.setColorSchemeColors(ResourcesCompat.getColor(resources, R.color.gray_1,
                    requireActivity().applicationContext.theme))
                userProfileDisplayRefresh.setProgressBackgroundColorSchemeColor(ResourcesCompat.getColor(resources,
                    R.color.gray_2, requireActivity().applicationContext.theme))
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                userProfileDisplayRefresh.setColorSchemeColors(ResourcesCompat.getColor(resources, R.color.blue_3,
                    requireActivity().applicationContext.theme))
                userProfileDisplayRefresh.setProgressBackgroundColorSchemeColor(ResourcesCompat.getColor(resources,
                    R.color.blue_1, requireActivity().applicationContext.theme))
            }
            Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                userProfileDisplayRefresh.setColorSchemeColors(ResourcesCompat.getColor(resources, R.color.blue_3,
                    requireActivity().applicationContext.theme))
                userProfileDisplayRefresh.setProgressBackgroundColorSchemeColor(ResourcesCompat.getColor(resources,
                    R.color.blue_1, requireActivity().applicationContext.theme))
            }
        }

        userProfileDisplayListView.layoutManager = LinearLayoutManager(requireActivity().applicationContext)
        userProfileDisplayPhoto = view.findViewById(R.id.user_profile_display_photo)
        userProfileDisplayPhoto.setImageResource(R.drawable.default_user_profile_photo)
//        Picasso.get().load(R.drawable.user_profile_image).resize(1000, 1000).centerCrop()
//            .into(holder.userImage)
        val testData = arrayListOf<TestUserData>() // test data for RecyclerView
        testData.add(TestUserData())

        userProfileDisplayAdapter = UserProfileDisplayAdapter(testData)
        userProfileDisplayListView.adapter = userProfileDisplayAdapter
        userProfileDisplayRefresh.setOnRefreshListener {
            // TODO
            userProfileDisplayRefresh.isRefreshing = false
        }

        return view
    }
}