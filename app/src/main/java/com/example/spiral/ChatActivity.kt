package com.example.spiral

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.squareup.picasso.Picasso
import com.visualizer.amplitude.AudioRecordView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.io.ByteArrayOutputStream
import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit

class ChatActivity : AppCompatActivity() {
    private lateinit var chatLayout: ConstraintLayout
    private lateinit var chatPhoto: ImageView
    private lateinit var chatUsername: TextView
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageTextView: EditText
    private lateinit var bottomMessageBar: LinearLayout
    private lateinit var bottomAttachmentBar: LinearLayout
    private lateinit var bottomPhotoBar: LinearLayout
    private lateinit var bottomAudioBar: LinearLayout
    private lateinit var photoMessageImageView: ImageView
    private lateinit var chatListLayout: ConstraintLayout
    private lateinit var audioRecordView: AudioRecordView
    private lateinit var audioMessageTime: TextView
    private lateinit var stopReloadAudioButton: Button
    private lateinit var closeAudioButton: Button
    private lateinit var sendAudioButton: Button
    private lateinit var userPhoto: Bitmap
    private lateinit var photoMessageBitmap: Bitmap
    private var photoPath: String? = null
    private var photoFromCamera = false
    private var audioPath: String? = null
    private var bottomMessageBarVisibility = true
    private var bottomAttachmentBarVisibility = false
    private var bottomPhotoBarVisibility = false
    private var bottomAudioBarVisibility = false
    private var recordAudio = false
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var authentication: FirebaseAuth
    private lateinit var database: DatabaseReference
    private var storage = FirebaseStorage.getInstance()
    private lateinit var messaging: FirebaseMessaging
    private lateinit var ifcmService: IFCMService
    private var compositeDisposable = CompositeDisposable()
    private var senderRoom: String? = null
    private var receiverRoom: String? = null
    private val timer = Timer()
    private lateinit var mediaRecorder: MediaRecorder
    private var currentMaxAmplitude = 0
    private var audioStart = 0L
    private var audioTime = 0L
    private var audioMinutes = 0L
    private var audioSeconds = 0L
    private var audioFile: File? = null
    private var currentPosition = 0
    private var loaded = false
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        authentication = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        messaging = FirebaseMessaging.getInstance()
        ifcmService = RetrofitFCMClient.getInstance().create(IFCMService::class.java)
        chatLayout = findViewById(R.id.chat_layout)

        when (applicationContext.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {
                chatLayout.background = ResourcesCompat.getDrawable(resources, R.drawable.background_dark_mode,
                    applicationContext.theme)
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                chatLayout.background = ResourcesCompat.getDrawable(resources, R.drawable.background_light_mode,
                    applicationContext.theme)
            }
            Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                chatLayout.background = ResourcesCompat.getDrawable(resources, R.drawable.background_light_mode,
                    applicationContext.theme)
            }
        }

        chatPhoto = findViewById(R.id.chat_photo)
        chatUsername = findViewById(R.id.chat_username)
        chatRecyclerView = findViewById(R.id.chat_list)
        messageTextView = findViewById(R.id.chat_type_message)
        bottomMessageBar = findViewById(R.id.bottom_message_bar)
        bottomAttachmentBar = findViewById(R.id.bottom_attachment_bar)
        bottomPhotoBar = findViewById(R.id.bottom_photo_bar)
        bottomAudioBar = findViewById(R.id.bottom_audio_bar)
        photoMessageImageView = findViewById(R.id.chat_photo_message)
        chatListLayout = findViewById(R.id.chat_list_layout)
        audioRecordView = findViewById(R.id.chat_audio_record_view)
        audioMessageTime = findViewById(R.id.chat_audio_message_time)
        stopReloadAudioButton = findViewById(R.id.chat_stop_reload_audio_button)
        closeAudioButton = findViewById(R.id.chat_close_audio_button)
        sendAudioButton = findViewById(R.id.chat_send_audio_button)
        val storageReference = storage.getReferenceFromUrl(chat.storageUrl)
        userId = intent.getStringExtra("userId")!!
        val photoReference = storageReference.child("users").child(userId)
        photoReference.downloadUrl.addOnSuccessListener {
            Picasso.get().load(it.toString()).placeholder(R.drawable.default_user_profile_photo).into(chatPhoto)
        }.addOnFailureListener {
            userPhoto = BitmapFactory.decodeResource(resources, R.drawable.default_user_profile_photo)
            chatPhoto.setImageBitmap(userPhoto)
        }
        chatUsername.text = intent.getStringExtra("username")!!
        senderRoom = authentication.currentUser?.uid + userId
        receiverRoom = userId + authentication.currentUser?.uid
        messageAdapter = MessageAdapter(this, chat.messagesList, senderRoom!!)
        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        (chatRecyclerView.layoutManager as LinearLayoutManager).stackFromEnd = true
        chatRecyclerView.adapter = messageAdapter
        chatRecyclerView.scrollToPosition(messageAdapter.itemCount - 1)
        chatRecyclerView.itemAnimator = null
        database.child("chats").child(senderRoom!!).child("messages").addValueEventListener(
            object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    currentPosition = (chatRecyclerView.layoutManager as LinearLayoutManager)
                        .findLastCompletelyVisibleItemPosition()
                    chat.messagesList.clear()

                    for (postSnapshot in snapshot.children) {
                        val message = postSnapshot.getValue(Message::class.java)
                        chat.messagesList.add(message!!)
                    }

                    if (currentPosition == messageAdapter.itemCount - 2) {
                        chatRecyclerView.scrollToPosition(messageAdapter.itemCount - 1)
                    }

                    if (loaded) {
                        messageAdapter.notifyItemInserted(messageAdapter.itemCount - 1)
                    } else {
                        messageAdapter.notifyDataSetChanged()
                        loaded = true
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        chatRecyclerView.setItemViewCacheSize(20)
        messaging.subscribeToTopic(receiverRoom!!)

        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            val windowMetrics = windowManager.currentWindowMetrics
            val height = (0.7 * windowMetrics.bounds.height()).toInt()
            bottomPhotoBar.layoutParams.height = height
        }

        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                if (recordAudio) {
                    audioTime = System.currentTimeMillis() - audioStart
                    audioMinutes = TimeUnit.MILLISECONDS.toMinutes(audioTime)
                    audioSeconds = TimeUnit.MILLISECONDS.toSeconds(audioTime) % 60L
                    audioMessageTime.text = "$audioMinutes:" + String.format("%02d", audioSeconds)
                    currentMaxAmplitude = mediaRecorder.maxAmplitude
                    audioRecordView.update(currentMaxAmplitude)
                }
            }
        }, 0, 100)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (recordAudio) {
                    recordAudio = false
                    mediaRecorder.stop()
                    mediaRecorder.release()
                }

                bottomMessageBar.visibility = View.VISIBLE
                bottomAttachmentBar.visibility = View.INVISIBLE
                bottomPhotoBar.visibility = View.INVISIBLE
                bottomAudioBar.visibility = View.INVISIBLE
                messageTextView.setText("")
                finish()
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("bottomMessageBarVisibility", bottomMessageBar.isVisible)
        outState.putBoolean("bottomAttachmentBarVisibility", bottomAttachmentBar.isVisible)
        outState.putBoolean("bottomPhotoBarVisibility", bottomPhotoBar.isVisible)
        outState.putBoolean("bottomAudioBarVisibility", bottomAudioBar.isVisible)
        outState.putBoolean("closeAudioButtonIsEnabled", closeAudioButton.isEnabled)
        outState.putBoolean("sendAudioButtonIsEnabled", sendAudioButton.isEnabled)
        outState.putString("photoPath", photoPath)
        outState.putBoolean("photoFromCamera", photoFromCamera)
        outState.putString("audioPath", audioPath)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        bottomMessageBarVisibility = savedInstanceState.getBoolean("bottomMessageBarVisibility")
        bottomAttachmentBarVisibility = savedInstanceState.getBoolean("bottomAttachmentBarVisibility")
        bottomPhotoBarVisibility = savedInstanceState.getBoolean("bottomPhotoBarVisibility")
        bottomAudioBarVisibility = savedInstanceState.getBoolean("bottomAudioBarVisibility")
        bottomMessageBar.visibility = if (bottomMessageBarVisibility) View.VISIBLE else View.INVISIBLE
        bottomAttachmentBar.visibility = if (bottomAttachmentBarVisibility) View.VISIBLE else View.INVISIBLE
        bottomPhotoBar.visibility = if (bottomPhotoBarVisibility) View.VISIBLE else View.INVISIBLE
        bottomAudioBar.visibility = if (bottomAudioBarVisibility) View.VISIBLE else View.INVISIBLE
        closeAudioButton.isEnabled = savedInstanceState.getBoolean("closeAudioButtonIsEnabled")
        sendAudioButton.isEnabled = savedInstanceState.getBoolean("sendAudioButtonIsEnabled")
        photoPath = savedInstanceState.getString("photoPath")
        photoFromCamera = savedInstanceState.getBoolean("photoFromCamera")
        audioPath = savedInstanceState.getString("audioPath")
        audioMessageTime.text = getString(R.string.chat_audio_sent_default_length)

        if (bottomPhotoBarVisibility) {
            chatListLayout.updateLayoutParams<ConstraintLayout.LayoutParams> {
                bottomToTop = bottomPhotoBar.id
            }
        }

        (stopReloadAudioButton as MaterialButton).icon = ResourcesCompat.getDrawable(resources, R.drawable.reload_icon, theme)

        if (photoPath != null) {
            photoMessageBitmap = BitmapFactory.decodeStream(baseContext.contentResolver.openInputStream(Uri
                .parse(if (photoFromCamera) "file://$photoPath" else photoPath)))
            photoMessageImageView.setImageBitmap(photoMessageBitmap)
        }
    }

    override fun onRestart() {
        super.onRestart()
        chatRecyclerView.adapter = messageAdapter
        chatRecyclerView.scrollToPosition(messageAdapter.itemCount - 1)
        database.child("chats").child(senderRoom!!).child("messages").addValueEventListener(
            object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    currentPosition = (chatRecyclerView.layoutManager as LinearLayoutManager)
                        .findLastCompletelyVisibleItemPosition()
                    chat.messagesList.clear()

                    for (postSnapshot in snapshot.children) {
                        val message = postSnapshot.getValue(Message::class.java)
                        chat.messagesList.add(message!!)
                    }

                    if (currentPosition == messageAdapter.itemCount - 2) {
                        chatRecyclerView.scrollToPosition(messageAdapter.itemCount - 1)
                    }

                    if (loaded) {
                        messageAdapter.notifyItemInserted(messageAdapter.itemCount - 1)
                    } else {
                        messageAdapter.notifyDataSetChanged()
                        loaded = true
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    override fun onPause() {
        super.onPause()

        if (recordAudio) {
            recordAudio = false
            audioStart = System.currentTimeMillis()
            (stopReloadAudioButton as MaterialButton).icon = ResourcesCompat.getDrawable(resources, R.drawable.reload_icon,
                theme)
            mediaRecorder.stop()
            mediaRecorder.release()
            closeAudioButton.isEnabled = true
            sendAudioButton.isEnabled = true
        }

        loaded = false
    }

    override fun onStop() {
        super.onStop()
        chat.messagesList.clear()
        messageAdapter.notifyDataSetChanged()
        chatRecyclerView.adapter = null
        loaded = false
    }

    override fun onDestroy() {
        super.onDestroy()

        if (recordAudio) {
            recordAudio = false
            mediaRecorder.stop()
            mediaRecorder.release()
        }

        compositeDisposable.clear()
        chat.messagesList.clear()
        messageAdapter.notifyDataSetChanged()
        chatRecyclerView.adapter = null
        loaded = false
    }

    fun chatSendMessageClick(view: View) {
        val messageText = messageTextView.text.toString()

        if (messageText != "") {
            val message = Message(authentication.currentUser?.uid, "text", messageText)
            database.child("chats").child(senderRoom!!).child("messages").push().setValue(message)
                .addOnSuccessListener {
                    database.child("chats").child(receiverRoom!!).child("messages").push()
                        .setValue(message)
                    sendNotification(message)
                }.addOnFailureListener {}
            database.child("chats").child(senderRoom!!).child("timestamp").setValue(
                DateTimeFormatter.ISO_INSTANT.format(Instant.now())).addOnSuccessListener {
                database.child("chats").child(receiverRoom!!).child("timestamp").setValue(
                    DateTimeFormatter.ISO_INSTANT.format(Instant.now()))
            }.addOnFailureListener {}
            messageTextView.setText("")
        }
    }

    private fun sendNotification(message: Message) {
        val notificationData: HashMap<String, String> = HashMap<String, String>()
        notificationData["title"] = "${chat.currentUser.firstName} ${chat.currentUser.surname}"
        notificationData["content"] = when (message.type) {
            "text" -> message.message
            "photo" -> getString(R.string.chat_photo_sent)
            "audio" -> getString(R.string.chat_audio_sent)
            else -> getString(R.string.chat_message_sent)
        }
        notificationData["sender"] = authentication.currentUser?.uid!!
        notificationData["receiver"] = userId
        notificationData["room_id"] = receiverRoom!!
        val sendData = FCMSendData("/topics/$receiverRoom", notificationData)
        compositeDisposable.add(ifcmService.sendNotification(sendData).subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread()).subscribe())
    }

    fun chatAttachmentClick(view: View?) {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(view?.windowToken, 0)
        bottomMessageBar.visibility = View.INVISIBLE
        bottomAttachmentBar.visibility = View.VISIBLE
        bottomPhotoBar.visibility = View.INVISIBLE
        bottomAudioBar.visibility = View.INVISIBLE
        messageTextView.setText("")
    }

    fun chatGalleryClick(view: View) {
        val permissionListener: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                photoFromCamera = false
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                gallery.launch(intent)
            }

            override fun onPermissionDenied(deniedPermissions: List<String>) {}
        }
        TedPermission.create().setScreenOrientation(resources.configuration.orientation)
            .setPermissionListener(permissionListener).setDeniedMessage(getString(R.string.no_permission_gallery)).setGotoSettingButtonText("Settings")
            .setPermissions(android.Manifest.permission.READ_EXTERNAL_STORAGE).check()
    }

    private var gallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        result -> if (result.resultCode == Activity.RESULT_OK) {
            photoFromCamera = false
            chatListLayout.updateLayoutParams<ConstraintLayout.LayoutParams> {
                bottomToTop = bottomPhotoBar.id
            }
            bottomMessageBar.visibility = View.INVISIBLE
            bottomAttachmentBar.visibility = View.INVISIBLE
            bottomPhotoBar.visibility = View.VISIBLE
            bottomAudioBar.visibility = View.INVISIBLE
            photoPath = result.data?.data!!.toString()
            photoMessageBitmap = BitmapFactory.decodeStream(baseContext.contentResolver.openInputStream(result.data?.data!!))
            photoMessageImageView.setImageBitmap(photoMessageBitmap)
        }
    }

    fun chatTakePhotoClick(view: View) {
        val permissionListener: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                photoFromCamera = true
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                photoPath = getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString() + "/photo.jpg"
                val cameraFile = photoPath?.let { File(it) }
                val fileUri = cameraFile?.let { FileProvider.getUriForFile(applicationContext,
                    "com.example.spiral.file_provider", it) }
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
                camera.launch(intent)
            }

            override fun onPermissionDenied(deniedPermissions: List<String>) {}
        }
        TedPermission.create().setScreenOrientation(resources.configuration.orientation)
            .setPermissionListener(permissionListener).setDeniedMessage(getString(R.string.no_permission_camera)).setGotoSettingButtonText("Settings")
            .setPermissions(android.Manifest.permission.CAMERA).check()
    }

    private var camera = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        result -> if (result.resultCode == Activity.RESULT_OK) {
            photoFromCamera = true
            chatListLayout.updateLayoutParams<ConstraintLayout.LayoutParams> {
                bottomToTop = bottomPhotoBar.id
            }
            bottomMessageBar.visibility = View.INVISIBLE
            bottomAttachmentBar.visibility = View.INVISIBLE
            bottomPhotoBar.visibility = View.VISIBLE
            bottomAudioBar.visibility = View.INVISIBLE
            photoMessageBitmap = BitmapFactory.decodeStream(baseContext.contentResolver.openInputStream(Uri
                .parse("file://$photoPath")))
            photoMessageImageView.setImageBitmap(photoMessageBitmap)
        }
    }

    fun chatAudioClick(view: View) {
        val permissionListener: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                val currentOrientation = resources.configuration.orientation
                this@ChatActivity.requestedOrientation = if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                } else {
                    ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                }
                (stopReloadAudioButton as MaterialButton).icon = ResourcesCompat.getDrawable(resources,
                    R.drawable.stop_icon, theme)
                bottomMessageBar.visibility = View.INVISIBLE
                bottomAttachmentBar.visibility = View.INVISIBLE
                bottomPhotoBar.visibility = View.INVISIBLE
                bottomAudioBar.visibility = View.VISIBLE
                audioPath = getExternalFilesDir(Environment.DIRECTORY_MUSIC).toString() + "/audio.3gp"
                audioFile = audioPath?.let { File(it) }
                mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) MediaRecorder(this@ChatActivity)
                    else MediaRecorder()
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                mediaRecorder.setOutputFile(audioFile)
                mediaRecorder.prepare()
                mediaRecorder.start()
                audioRecordView.recreate()
                audioStart = System.currentTimeMillis()
                recordAudio = true
            }

            override fun onPermissionDenied(deniedPermissions: List<String>) {}
        }
        TedPermission.create().setScreenOrientation(resources.configuration.orientation)
            .setPermissionListener(permissionListener).setDeniedMessage(getString(R.string.no_permission_gallery)).setGotoSettingButtonText("Settings")
            .setPermissions(android.Manifest.permission.RECORD_AUDIO).check()
    }

    fun closeClick(view: View) {
        bottomMessageBar.visibility = View.VISIBLE
        bottomAttachmentBar.visibility = View.INVISIBLE
        bottomPhotoBar.visibility = View.INVISIBLE
        bottomAudioBar.visibility = View.INVISIBLE
    }

    fun photoCloseClick(view: View) {
        chatListLayout.updateLayoutParams<ConstraintLayout.LayoutParams> {
            bottomToTop = bottomMessageBar.id
        }
        bottomMessageBar.visibility = View.VISIBLE
        bottomAttachmentBar.visibility = View.INVISIBLE
        bottomPhotoBar.visibility = View.INVISIBLE
        bottomAudioBar.visibility = View.INVISIBLE
        photoMessageBitmap = BitmapFactory.decodeResource(resources, R.drawable.default_photo)
        photoMessageImageView.setImageBitmap(photoMessageBitmap)
        photoPath = null
    }

    fun chatSendPhotoClick(view: View) {
        val storageReference = storage.getReferenceFromUrl(chat.storageUrl)
        val byteArrayOutputStream = ByteArrayOutputStream()
        photoMessageBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
        val data: ByteArray = byteArrayOutputStream.toByteArray()
        val hash = MessageDigest.getInstance("SHA-256").digest(data)
        val photoId = BigInteger(1, hash).toString(16)
        val photoReferenceSender = storageReference.child("chats").child(senderRoom!!).child("photos")
            .child(photoId)
        val photoReferenceReceiver = storageReference.child("chats").child(receiverRoom!!).child("photos")
            .child(photoId)
        val message = Message(authentication.currentUser?.uid, "photo", photoId)
        photoReferenceSender.putBytes(data).addOnCompleteListener {
            database.child("chats").child(senderRoom!!).child("messages").push().setValue(message)
        }
        photoReferenceReceiver.putBytes(data).addOnCompleteListener {
            database.child("chats").child(receiverRoom!!).child("messages").push().setValue(message)
        }.addOnSuccessListener { sendNotification(message) }
        database.child("chats").child(senderRoom!!).child("timestamp").setValue(
            DateTimeFormatter.ISO_INSTANT.format(Instant.now())).addOnSuccessListener {
            database.child("chats").child(receiverRoom!!).child("timestamp").setValue(
                DateTimeFormatter.ISO_INSTANT.format(Instant.now()))
        }.addOnFailureListener {}
        chatListLayout.updateLayoutParams<ConstraintLayout.LayoutParams> {
            bottomToTop = bottomMessageBar.id
        }
        bottomMessageBar.visibility = View.VISIBLE
        bottomAttachmentBar.visibility = View.INVISIBLE
        bottomPhotoBar.visibility = View.INVISIBLE
        bottomAudioBar.visibility = View.INVISIBLE
        photoMessageBitmap = BitmapFactory.decodeResource(resources, R.drawable.default_photo)
        photoMessageImageView.setImageBitmap(photoMessageBitmap)
        photoPath = null
    }

    fun stopReloadAudioClick(view: View) {
        recordAudio = !recordAudio
        audioStart = System.currentTimeMillis()
        (stopReloadAudioButton as MaterialButton).icon = ResourcesCompat.getDrawable(resources, if (recordAudio)
            R.drawable.stop_icon else R.drawable.reload_icon, theme)

        if (recordAudio) {
            val currentOrientation = resources.configuration.orientation
            this@ChatActivity.requestedOrientation = if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            } else {
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }
            closeAudioButton.isEnabled = false
            sendAudioButton.isEnabled = false
            audioPath = getExternalFilesDir(Environment.DIRECTORY_MUSIC).toString() + "/audio.3gp"
            audioFile = audioPath?.let { File(it) }
            mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) MediaRecorder(this@ChatActivity)
                else MediaRecorder()
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            mediaRecorder.setOutputFile(audioFile)
            mediaRecorder.prepare()
            mediaRecorder.start()
            audioRecordView.recreate()
        } else {
            mediaRecorder.stop()
            mediaRecorder.release()
            closeAudioButton.isEnabled = true
            sendAudioButton.isEnabled = true
        }
    }

    fun audioCloseClick(view: View) {
        bottomMessageBar.visibility = View.VISIBLE
        bottomAttachmentBar.visibility = View.INVISIBLE
        bottomPhotoBar.visibility = View.INVISIBLE
        bottomAudioBar.visibility = View.INVISIBLE
        audioRecordView.recreate()
        audioMessageTime.text = getString(R.string.chat_audio_sent_default_length)
        recordAudio = false
        (stopReloadAudioButton as MaterialButton).icon = ResourcesCompat.getDrawable(resources, R.drawable.stop_icon, theme)
        closeAudioButton.isEnabled = false
        sendAudioButton.isEnabled = false
        this@ChatActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

    fun chatSendAudioClick(view: View) {
        val audioUri = Uri.fromFile(audioFile)
        val storageReference = storage.getReferenceFromUrl(chat.storageUrl)
        val data: ByteArray = audioFile?.readBytes() ?: ByteArray(0)
        val hash = MessageDigest.getInstance("SHA-256").digest(data)
        val audioId = BigInteger(1, hash).toString(16)
        val mediaMetadataRetriever = MediaMetadataRetriever()
        mediaMetadataRetriever.setDataSource(audioUri.toString())
        val audioDuration = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong()
        val audioReferenceSender = storageReference.child("chats").child(senderRoom!!).child("audios")
            .child(audioId)
        val audioReferenceReceiver = storageReference.child("chats").child(receiverRoom!!).child("audios")
            .child(audioId)
        val message = Message(authentication.currentUser?.uid, "audio", audioId, audioDuration)
        audioReferenceSender.putFile(audioUri).addOnCompleteListener {
            database.child("chats").child(senderRoom!!).child("messages").push().setValue(message)
        }
        audioReferenceReceiver.putFile(audioUri).addOnCompleteListener {
            database.child("chats").child(receiverRoom!!).child("messages").push().setValue(message)
        }.addOnSuccessListener { sendNotification(message) }
        database.child("chats").child(senderRoom!!).child("timestamp").setValue(
            DateTimeFormatter.ISO_INSTANT.format(Instant.now())).addOnSuccessListener {
            database.child("chats").child(receiverRoom!!).child("timestamp").setValue(
                DateTimeFormatter.ISO_INSTANT.format(Instant.now()))
        }.addOnFailureListener {}
        bottomMessageBar.visibility = View.VISIBLE
        bottomAttachmentBar.visibility = View.INVISIBLE
        bottomPhotoBar.visibility = View.INVISIBLE
        bottomAudioBar.visibility = View.INVISIBLE
        audioRecordView.recreate()
        audioMessageTime.text = getString(R.string.chat_audio_sent_default_length)
        recordAudio = false
        (stopReloadAudioButton as MaterialButton).icon = ResourcesCompat.getDrawable(resources, R.drawable.stop_icon, theme)
        closeAudioButton.isEnabled = false
        sendAudioButton.isEnabled = false
        this@ChatActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }
}