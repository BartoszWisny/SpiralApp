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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

lateinit var userChatsAdapter: UserChatsAdapter

class UserChatsFragment : Fragment() {
    private lateinit var userChatsListView: RecyclerView
    private lateinit var userChatsRefresh: SwipeRefreshLayout
    private lateinit var authentication: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_user_chats, container, false)
        userChatsListView = view.findViewById(R.id.user_chats_list)
        userChatsRefresh = view.findViewById(R.id.user_chats_refresh)
        authentication = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
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
        userChatsListView.setHasFixedSize(true)
        userChatsListView.setItemViewCacheSize(20)
        userChatsListView.layoutManager = LinearLayoutManager(requireActivity().applicationContext)
        userChatsAdapter = UserChatsAdapter(requireContext(), chat.usersList)
        userChatsListView.adapter = userChatsAdapter
        userChatsRefresh.setOnRefreshListener {
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

                    userChatsAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {}
            })
            userChatsRefresh.isRefreshing = false
        }
        return view
    }
}