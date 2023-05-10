package com.example.spiral

import android.content.res.Configuration
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation

class UserFriendsAdapter(private val data: List<TestFriendData>, private val numberOfColumns: Int):
    RecyclerView.Adapter<UserFriendsAdapter.ViewHolder>() {
    private var friendsData = data

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
//        var userFriendsListItemLayout: ConstraintLayout = view.findViewById(R.id.user_friends_list_item_layout)
        var userImage: ImageView = view.findViewById(R.id.user_friends_image)
        var userName: TextView = view.findViewById(R.id.user_friends_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_friends_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.userImage.setImageResource(R.drawable.default_user_profile_photo)
//        Picasso.get().load(R.drawable.user_profile_image).resize(1000, 1000).centerCrop()
//            .transform(RoundedCornersTransformation(50, 0)).into(holder.userImage)
        holder.userName.text = friendsData[position].user

//        if (position / numberOfColumns == 0) {
//            holder.userFriendsListItemLayout.updatePadding(0, 50, 0, 0)
//        }
//
//        if (position / numberOfColumns == (data.size - 1) / numberOfColumns) {
//            holder.userFriendsListItemLayout.updatePadding(0, 0, 0, 50)
//        }
    }

    override fun getItemCount(): Int {
        return friendsData.size
    }

    fun filterFriends(text: String) {
        val filteredList = arrayListOf<TestFriendData>()

        for (item in data) {
            if (item.user.lowercase().contains(text.lowercase())) {
                filteredList.add(item)
            }
        }

        friendsData = filteredList
        notifyDataSetChanged()
    }
}