package com.example.spiral

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import rm.com.audiowave.AudioWaveView
import java.util.*
import java.util.concurrent.TimeUnit

class MessageAdapter(private val context: Context, private val data: List<Message>, private val senderRoom: String):
    RecyclerView.Adapter<ViewHolder>() {
    private lateinit var authentication: FirebaseAuth
    private var storage = FirebaseStorage.getInstance()
    private val storageReference = storage.getReferenceFromUrl(chat.storageUrl)
    private val messageSent = 1
    private val messageReceived = 2
    private val photoSent = 3
    private val photoReceived = 4
    private val audioSent = 5
    private val audioReceived = 6
    private lateinit var photoBitmap: Bitmap
    private var play = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            messageSent -> {
                val view = LayoutInflater.from(context).inflate(R.layout.chat_message_sent, parent, false)
                MessageSentViewHolder(view)
            }
            messageReceived -> {
                val view = LayoutInflater.from(context).inflate(R.layout.chat_message_received, parent, false)
                MessageReceivedViewHolder(view)
            }
            photoSent -> {
                val view = LayoutInflater.from(context).inflate(R.layout.chat_photo_sent, parent, false)
                PhotoSentViewHolder(view)
            }
            photoReceived -> {
                val view = LayoutInflater.from(context).inflate(R.layout.chat_photo_received, parent, false)
                PhotoReceivedViewHolder(view)
            }
            audioSent -> {
                val view = LayoutInflater.from(context).inflate(R.layout.chat_audio_sent, parent, false)
                AudioSentViewHolder(view)
            }
            audioReceived -> {
                val view = LayoutInflater.from(context).inflate(R.layout.chat_audio_received, parent, false)
                AudioReceivedViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(context).inflate(R.layout.chat_message_sent, parent, false)
                MessageSentViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = data[position]

        when (holder.javaClass) {
            MessageSentViewHolder::class.java -> {
                holder as MessageSentViewHolder
                holder.messageSent.text = message.message
                holder.messageSent.movementMethod = LinkMovementMethod.getInstance()
                holder.setIsRecyclable(false)
            }
            MessageReceivedViewHolder::class.java -> {
                holder as MessageReceivedViewHolder
                holder.messageReceived.text = message.message
                holder.messageReceived.movementMethod = LinkMovementMethod.getInstance()
                holder.setIsRecyclable(false)
            }
            PhotoSentViewHolder::class.java -> {
                holder as PhotoSentViewHolder
                val photoId = message.message
                val photoReference = storageReference.child("chats").child(senderRoom).child("photos")
                    .child(photoId)
                photoReference.downloadUrl.addOnSuccessListener {
                    Picasso.get().load(it.toString()).into(holder.photoSent)
                }.addOnFailureListener {
                    photoBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.default_photo)
                    holder.photoSent.setImageBitmap(photoBitmap)
                }
                holder.itemView.setOnClickListener {
                    val intent = Intent(context, PhotoShowActivity::class.java)
                    intent.putExtra("photoType", "chat")
                    intent.putExtra("senderRoom", senderRoom)
                    intent.putExtra("photoId", photoId)
                    context.startActivity(intent)
                }
            }
            PhotoReceivedViewHolder::class.java -> {
                holder as PhotoReceivedViewHolder
                val photoId = message.message
                val photoReference = storageReference.child("chats").child(senderRoom).child("photos")
                    .child(photoId)
                photoReference.downloadUrl.addOnSuccessListener {
                    Picasso.get().load(it.toString()).into(holder.photoReceived)
                }.addOnFailureListener {
                    photoBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.default_photo)
                    holder.photoReceived.setImageBitmap(photoBitmap)
                }
                holder.itemView.setOnClickListener {
                    val intent = Intent(context, PhotoShowActivity::class.java)
                    intent.putExtra("photoType", "chat")
                    intent.putExtra("senderRoom", senderRoom)
                    intent.putExtra("photoId", photoId)
                    context.startActivity(intent)
                }
            }
            AudioSentViewHolder::class.java -> {
                holder as AudioSentViewHolder
                val audioId = message.message
                val audioReference = storageReference.child("chats").child(senderRoom).child("audios")
                    .child(audioId)
                var audioPath: String? = null
                holder.mediaPlayer = MediaPlayer()
                audioReference.downloadUrl.addOnSuccessListener {
                    audioPath = it.toString()
                    holder.mediaPlayer.setDataSource(audioPath)
                    holder.mediaPlayer.prepare()
                }.addOnFailureListener {}
                audioReference.getBytes(5 * 1024 * 1024).addOnSuccessListener {
                    holder.audioWaveViewSent.setRawData(it)
                }
                var audioMinutes: Long
                var audioSeconds: Long
                holder.mediaPlayer.setOnPreparedListener {
                    holder.playPauseAudioSentButton.isEnabled = true
                    audioMinutes = TimeUnit.MILLISECONDS.toMinutes(holder.mediaPlayer.duration.toLong())
                    audioSeconds = TimeUnit.MILLISECONDS.toSeconds(holder.mediaPlayer.duration.toLong()) % 60L
                    holder.audioSentTime.text = audioMinutes.toString() + ":" + String.format("%02d", audioSeconds)
                }
                holder.playPauseAudioSentButton.setOnClickListener {
                    play = !play
                    (holder.playPauseAudioSentButton as MaterialButton).icon = ResourcesCompat.getDrawable(context.resources,
                        if (play) R.drawable.pause_icon else R.drawable.play_icon, context.theme)

                    if (play) {
                        val currentOrientation = context.resources.configuration.orientation
                        (context as Activity).requestedOrientation = if (currentOrientation
                            == Configuration.ORIENTATION_PORTRAIT) {
                            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                        } else {
                            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                        }
                        holder.mediaPlayer.start()
                        holder.mediaPlayer.setOnCompletionListener {
                            play = false
                            audioMinutes = TimeUnit.MILLISECONDS.toMinutes(holder.mediaPlayer.duration.toLong())
                            audioSeconds = TimeUnit.MILLISECONDS.toSeconds(holder.mediaPlayer.duration.toLong()) % 60L
                            holder.playPauseAudioSentButton.icon = ResourcesCompat.getDrawable(context.resources,
                                R.drawable.play_icon, context.theme)
                            holder.audioWaveViewSent.progress = 0F
                            holder.audioSentTime.text = audioMinutes.toString() + ":" + String.format("%02d", audioSeconds)
                            holder.mediaPlayer.stop()
                            holder.mediaPlayer.reset()
                            holder.mediaPlayer = MediaPlayer()
                            holder.mediaPlayer.setDataSource(audioPath)
                            holder.mediaPlayer.prepare()
                        }
                    } else {
                        holder.mediaPlayer.pause()
                        (context as Activity).requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                    }
                }
                val timer = Timer()
                timer.scheduleAtFixedRate(object : TimerTask() {
                    override fun run() {
                        if (play && holder.mediaPlayer.isPlaying) {
                            holder.audioWaveViewSent.progress = (100 * holder.mediaPlayer.currentPosition
                                / holder.mediaPlayer.duration).toFloat()
                            audioMinutes = TimeUnit.MILLISECONDS.toMinutes((holder.mediaPlayer.duration
                                - holder.mediaPlayer.currentPosition).toLong())
                            audioSeconds = TimeUnit.MILLISECONDS.toSeconds((holder.mediaPlayer.duration
                                - holder.mediaPlayer.currentPosition).toLong()) % 60L
                            (context as Activity).runOnUiThread {
                                holder.audioSentTime.text = audioMinutes.toString() + ":" + String.format("%02d", audioSeconds)
                            }
                        }
                    }
                }, 0, 50)
            }
            AudioReceivedViewHolder::class.java -> {
                holder as AudioReceivedViewHolder
                val audioId = message.message
                val audioReference = storageReference.child("chats").child(senderRoom).child("audios")
                    .child(audioId)
                var audioPath: String? = null
                holder.mediaPlayer = MediaPlayer()
                audioReference.downloadUrl.addOnSuccessListener {
                    audioPath = it.toString()
                    holder.mediaPlayer.setDataSource(audioPath)
                    holder.mediaPlayer.prepare()
                }.addOnFailureListener {}
                audioReference.getBytes(5 * 1024 * 1024).addOnSuccessListener {
                    holder.audioWaveViewReceived.setRawData(it)
                }
                var audioMinutes: Long
                var audioSeconds: Long
                holder.mediaPlayer.setOnPreparedListener {
                    holder.playPauseAudioReceivedButton.isEnabled = true
                    audioMinutes = TimeUnit.MILLISECONDS.toMinutes(holder.mediaPlayer.duration.toLong())
                    audioSeconds = TimeUnit.MILLISECONDS.toSeconds(holder.mediaPlayer.duration.toLong()) % 60L
                    holder.audioReceivedTime.text = audioMinutes.toString() + ":" + String.format("%02d", audioSeconds)
                }
                holder.playPauseAudioReceivedButton.setOnClickListener {
                    play = !play
                    (holder.playPauseAudioReceivedButton as MaterialButton).icon = ResourcesCompat.getDrawable(context.resources,
                        if (play) R.drawable.pause_icon else R.drawable.play_icon, context.theme)

                    if (play) {
                        holder.mediaPlayer.start()
                        holder.mediaPlayer.setOnCompletionListener {
                            play = false
                            audioMinutes = TimeUnit.MILLISECONDS.toMinutes(holder.mediaPlayer.duration.toLong())
                            audioSeconds = TimeUnit.MILLISECONDS.toSeconds(holder.mediaPlayer.duration.toLong()) % 60L
                            holder.playPauseAudioReceivedButton.icon = ResourcesCompat.getDrawable(context.resources,
                                R.drawable.play_icon, context.theme)
                            holder.audioWaveViewReceived.progress = 0F
                            holder.audioReceivedTime.text = audioMinutes.toString() + ":" + String.format("%02d", audioSeconds)
                            holder.mediaPlayer.stop()
                            holder.mediaPlayer.reset()
                            holder.mediaPlayer = MediaPlayer()
                            holder.mediaPlayer.setDataSource(audioPath)
                            holder.mediaPlayer.prepare()
                        }
                    } else {
                        holder.mediaPlayer.pause()
                    }
                }
                val timer = Timer()
                timer.scheduleAtFixedRate(object : TimerTask() {
                    override fun run() {
                        if (play && holder.mediaPlayer.isPlaying) {
                            holder.audioWaveViewReceived.progress = (100 * holder.mediaPlayer.currentPosition
                                / holder.mediaPlayer.duration).toFloat()
                            audioMinutes = TimeUnit.MILLISECONDS.toMinutes((holder.mediaPlayer.duration
                                - holder.mediaPlayer.currentPosition).toLong())
                            audioSeconds = TimeUnit.MILLISECONDS.toSeconds((holder.mediaPlayer.duration
                                - holder.mediaPlayer.currentPosition).toLong()) % 60L
                            (context as Activity).runOnUiThread {
                                holder.audioReceivedTime.text = audioMinutes.toString() + ":" + String.format("%02d",
                                    audioSeconds)
                            }
                        }
                    }
                }, 0, 50)
            }
            else -> {
                holder as MessageSentViewHolder
                holder.messageSent.text = message.message
                holder.setIsRecyclable(false)
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        authentication = FirebaseAuth.getInstance()
        val message = data[position]
        return if (message.type == "text") {
            if (authentication.currentUser?.uid == message.senderId) messageSent else messageReceived
        } else if (message.type == "photo") {
            if (authentication.currentUser?.uid == message.senderId) photoSent else photoReceived
        } else if (message.type == "audio") {
            if (authentication.currentUser?.uid == message.senderId) audioSent else audioReceived
        } else {
            0
        }
    }

    class MessageSentViewHolder(itemView: View) : ViewHolder(itemView) {
        val messageSent: TextView = itemView.findViewById(R.id.chat_message_sent)
    }

    class MessageReceivedViewHolder(itemView: View) : ViewHolder(itemView) {
        val messageReceived: TextView = itemView.findViewById(R.id.chat_message_received)
    }

    class PhotoSentViewHolder(itemView: View) : ViewHolder(itemView) {
        val photoSent: ImageView = itemView.findViewById(R.id.chat_photo_sent)
    }

    class PhotoReceivedViewHolder(itemView: View) : ViewHolder(itemView) {
        val photoReceived: ImageView = itemView.findViewById(R.id.chat_photo_received)
    }

    class AudioSentViewHolder(itemView: View) : ViewHolder(itemView) {
        val playPauseAudioSentButton: Button = itemView.findViewById(R.id.chat_play_pause_audio_sent_button)
        val audioWaveViewSent: AudioWaveView = itemView.findViewById(R.id.chat_audio_wave_view_sent)
        val audioSentTime: TextView = itemView.findViewById(R.id.chat_audio_sent_time)
        var mediaPlayer = MediaPlayer()
    }

    class AudioReceivedViewHolder(itemView: View) : ViewHolder(itemView) {
        val playPauseAudioReceivedButton: Button = itemView.findViewById(R.id.chat_play_pause_audio_received_button)
        val audioWaveViewReceived: AudioWaveView = itemView.findViewById(R.id.chat_audio_wave_view_received)
        val audioReceivedTime: TextView = itemView.findViewById(R.id.chat_audio_received_time)
        var mediaPlayer = MediaPlayer()
    }
}