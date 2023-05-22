package com.example.spiral

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class UserProfilesAdapter(private val context: Context, private val data: List<User>):
    RecyclerView.Adapter<UserProfilesAdapter.ViewHolder>() {
    private var profilesData = data
    private var storage = FirebaseStorage.getInstance()
    private lateinit var userPhotoBitmap: Bitmap

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var userPhoto: ImageView = view.findViewById(R.id.user_profiles_photo)
        var username: TextView = view.findViewById(R.id.user_profiles_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_profiles_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val storageReference = storage.getReferenceFromUrl(chat.storageUrl)
        val photoReference = storageReference.child("users").child(profilesData[position].userId)
        photoReference.downloadUrl.addOnSuccessListener {
            Picasso.get().load(it.toString()).placeholder(R.drawable.default_user_profile_photo).into(holder.userPhoto)
        }.addOnFailureListener {
            userPhotoBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.default_user_profile_photo)
            holder.userPhoto.setImageBitmap(userPhotoBitmap)
        }
        val name = "${profilesData[position].firstName} ${profilesData[position].surname}"
        holder.username.text = name
        holder.itemView.setOnClickListener {
            chat.tabAdapter?.selectedProfile = profilesData[position].userId
            chat.viewPager?.currentItem = 2
        }
    }

    override fun getItemCount(): Int {
        return profilesData.size
    }

    fun filterProfiles(text: String) {
        val filteredList = arrayListOf<User>()

        for (item in data) {
            val name = "${item.firstName} ${item.surname}"
            if (name.lowercase().contains(text.lowercase())) {
                filteredList.add(item)
            }
        }

        profilesData = filteredList
        notifyDataSetChanged()
    }
}