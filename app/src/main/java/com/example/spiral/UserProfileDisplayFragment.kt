package com.example.spiral

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

lateinit var userProfileDisplayAdapter: UserProfileDisplayAdapter

class UserProfileDisplayFragment : Fragment() {
    private lateinit var userProfileDisplayListView: RecyclerView
    private lateinit var userProfileDisplayRefresh: SwipeRefreshLayout
    private lateinit var userProfileDisplayPhoto: ImageView

    private var currentSelectedProfileId: String = ""



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

        val data = arrayListOf<User>()

        userProfileDisplayAdapter = UserProfileDisplayAdapter(requireContext(), data)
        userProfileDisplayListView.adapter = userProfileDisplayAdapter
        userProfileDisplayRefresh.setOnRefreshListener {
            // TODO
            userProfileDisplayRefresh.isRefreshing = false
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        val storage = FirebaseStorage.getInstance()
        val storageReference = storage.getReferenceFromUrl(chat.storageUrl)

        if (chat.tabAdapter?.selectedProfile != "") {
            // Changing details only when selected profile id has changed from last time
            if (currentSelectedProfileId != chat.tabAdapter!!.selectedProfile) {
                currentSelectedProfileId = chat.tabAdapter!!.selectedProfile
                val photoReference = storageReference.child("users").child(currentSelectedProfileId)
                currentSelectedProfileId = chat.tabAdapter!!.selectedProfile
                photoReference.downloadUrl.addOnSuccessListener {
                    Picasso.get().load(it.toString()).placeholder(R.drawable.default_user_profile_photo).into(userProfileDisplayPhoto)
                }.addOnFailureListener {
                    userProfileDisplayPhoto.setImageResource(R.drawable.default_user_profile_photo)
                }
                val data = arrayListOf<User>()
                for (user in chat.chatsData) {
                    if (user.userId == currentSelectedProfileId) {
                        data.add(user)
                    }
                }
                userProfileDisplayAdapter = UserProfileDisplayAdapter(requireContext(), data)
                userProfileDisplayListView.adapter = userProfileDisplayAdapter
            }
        } else {
            val userId = FirebaseAuth.getInstance().currentUser!!.uid
            val photoReference = storageReference.child("users").child(userId)
            photoReference.downloadUrl.addOnSuccessListener {
                Picasso.get().load(it.toString()).placeholder(R.drawable.default_user_profile_photo).into(userProfileDisplayPhoto)
            }.addOnFailureListener {
                userProfileDisplayPhoto.setImageResource(R.drawable.default_user_profile_photo)
            }
            val data = arrayListOf<User>()
            data.add(chat.currentUser)
            userProfileDisplayAdapter = UserProfileDisplayAdapter(requireContext(), data)
            userProfileDisplayListView.adapter = userProfileDisplayAdapter
        }
    }

}