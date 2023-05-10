package com.example.spiral

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UserProfilesAdapter(private val data: List<TestProfileData>):
    RecyclerView.Adapter<UserProfilesAdapter.ViewHolder>() {
    private var profilesData = data

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var userImage: ImageView = view.findViewById(R.id.user_profiles_photo)
        var username: TextView = view.findViewById(R.id.user_profiles_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_profiles_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.userImage.setImageResource(R.drawable.default_user_profile_photo)
//        Picasso.get().load(R.drawable.user_profile_image).resize(1000, 1000).centerCrop()
//            .transform(RoundedCornersTransformation(50, 0)).into(holder.userImage)
        holder.username.text = profilesData[position].user
    }

    override fun getItemCount(): Int {
        return profilesData.size
    }

    fun filterFriends(text: String) {
        val filteredList = arrayListOf<TestProfileData>()

        for (item in data) {
            if (item.user.lowercase().contains(text.lowercase())) {
                filteredList.add(item)
            }
        }

        profilesData = filteredList
        notifyDataSetChanged()
    }
}