package com.example.spiral

import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

var userProfilesAdapter: UserProfilesAdapter? = null

class UserProfilesFragment : Fragment() {
    private lateinit var userProfilesListView: RecyclerView
    private lateinit var userProfilesRefresh: SwipeRefreshLayout
    private lateinit var authentication: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user_profiles, container, false)
        userProfilesListView = view.findViewById(R.id.user_profiles_list)
        userProfilesRefresh = view.findViewById(R.id.user_profiles_refresh)
        authentication = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
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
        userProfilesListView.setHasFixedSize(true)
        userProfilesListView.setItemViewCacheSize(20)
        userProfilesListView.layoutManager = GridLayoutManager(requireActivity().applicationContext, numberOfColumns)
        userProfilesAdapter = UserProfilesAdapter(requireContext(), chat.usersList)
        userProfilesListView.adapter = userProfilesAdapter
        userProfilesRefresh.setOnRefreshListener {
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

                    userProfilesAdapter!!.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {}
            })
            userProfilesRefresh.isRefreshing = false
        }
        return view
    }
}