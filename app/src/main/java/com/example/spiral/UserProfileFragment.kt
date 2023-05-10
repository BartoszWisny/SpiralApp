package com.example.spiral

import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

lateinit var userProfileAdapter: UserProfileAdapter

class UserProfileFragment : Fragment() {
    private lateinit var userProfileListView: RecyclerView
    private lateinit var userProfileRefresh: SwipeRefreshLayout
    private lateinit var userProfilePhoto: ImageView

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

        userProfilePhoto = view.findViewById(R.id.user_profile_photo)
        userProfilePhoto.setImageResource(R.drawable.default_user_profile_photo)
//        Picasso.get().load(R.drawable.user_profile_image).resize(1000, 1000).centerCrop()
//            .into(holder.userImage)
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

    fun getProfiles() {

    }
}