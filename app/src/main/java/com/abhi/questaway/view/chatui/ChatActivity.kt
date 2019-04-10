package com.abhi.questaway.view.chatui

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.abhi.questaway.R
import com.abhi.questaway.base.ImagePickerActivity
import com.abhi.questaway.network.ApiClient
import com.abhi.questaway.network.ResultModel
import com.abhi.questaway.network.RetrofitApiService
import com.abhi.questaway.view.chatui.data.MessagesFixtures
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.stfalcon.chatkit.messages.MessageHolders
import com.stfalcon.chatkit.messages.MessageInput
import com.stfalcon.chatkit.messages.MessagesList
import com.stfalcon.chatkit.messages.MessagesListAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class ChatActivity : DemoMessagesActivity(), MessageInput.InputListener, MessageInput.AttachmentsListener,
    MessageInput.TypingListener {

    private lateinit var messagesList: MessagesList
    private val REQUEST_IMAGE = 100
    var linewiseText = ""
    lateinit var apiService : RetrofitApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        apiService = ApiClient.getClient(applicationContext)!!
            .create(RetrofitApiService::class.java)
        this.messagesList = findViewById(R.id.messagesList)
        initAdapter()

        val input = findViewById<MessageInput>(R.id.input)
        input.setInputListener(this)
        input.setTypingListener(this)
        input.setAttachmentsListener(this)

    }

    private fun initAdapter() {
        val holdersConfig = MessageHolders()
            .setIncomingTextLayout(R.layout.item_custom_incoming_text_message)
            .setOutcomingTextLayout(R.layout.item_custom_outcoming_text_message)
            .setIncomingImageLayout(R.layout.item_custom_incoming_image_message)
            .setOutcomingImageLayout(R.layout.item_custom_outcoming_image_message)

        super.messagesAdapter = MessagesListAdapter(super.senderId, holdersConfig, super.imageLoader)

        super.messagesAdapter.enableSelectionMode(this)
        super.messagesAdapter.setLoadMoreListener(this)
        super.messagesAdapter.registerViewClickListener(
            R.id.messageUserAvatar
        ) { view, message ->
            /*AppUtils.showToast(
                                this@DefaultMessagesActivity,
                                message.getUser().name + " avatar click",
                                false
                            )*/
        }
        this.messagesList.setAdapter(super.messagesAdapter)
    }


    override fun onSelectionChanged(count: Int) {

    }

    override fun onSubmit(input: CharSequence?): Boolean {
        addMessageToAdapter(input.toString(), false)
        if (linewiseText != "") {
            makeRequest(linewiseText, input.toString())
        } else {
            addMessageToAdapter("Please provide an image to be processed", true)
        }
        return true
    }

    override fun onAddAttachments() {
        linewiseText = ""
        onSelectImageClicked()
    }

    private fun onSelectImageClicked() {
        Dexter.withActivity(this)
            .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        showImagePickerOptions()
                    }

                    if (report.isAnyPermissionPermanentlyDenied) {
                        // showSettingsDialog()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }

            }).check()

    }

    private fun showImagePickerOptions() {
        ImagePickerActivity.showImagePickerOptions(this, object : ImagePickerActivity.PickerOptionListener {
            override fun onTakeCameraSelected() {
                launchCameraIntent()
            }

            override fun onChooseGallerySelected() {
                launchGalleryIntent()
            }

        })
    }

    private fun launchGalleryIntent() {
        val intent = Intent(this, ImagePickerActivity::class.java)
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE)

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true)
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1) // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1)
        startActivityForResult(intent, REQUEST_IMAGE)
    }

    private fun launchCameraIntent() {
        val intent = Intent(this, ImagePickerActivity::class.java)
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_IMAGE_CAPTURE)

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true)
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1) // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1)

        // setting maximum bitmap width and height
        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true)
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 1000)
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 1000)

        startActivityForResult(intent, REQUEST_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    val uri = data.getParcelableExtra<Uri>("path")
                    try {
                        val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                        // ml kit function call
                        super.messagesAdapter.addToStart(
                            MessagesFixtures.getImageMessage(uri.toString()), true
                        )
                        startTextRecognition(bitmap)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    private fun startTextRecognition(bitmap: Bitmap) {
        val image = FirebaseVisionImage.fromBitmap(bitmap)
        val detector = FirebaseVision.getInstance()
            .onDeviceTextRecognizer

        detector.processImage(image)
            .addOnSuccessListener { firebaseVisionText ->
                for (block in firebaseVisionText.textBlocks) {
//                    val boundingBox = block.boundingBox
//                    val cornerPoints = block.cornerPoints
//                    val blockConfidence = block.confidence
//                    val blockLanguages = block.recognizedLanguages
//                    val blockCornerPoints = block.cornerPoints
//                    val blockFrame = block.boundingBox

                    for (line in block.lines) {
                        val lineText = line.text
                        linewiseText = "$linewiseText\n$lineText"
//                        val lineConfidence = line.confidence
//                        val lineLanguages = line.recognizedLanguages
//                        val lineCornerPoints = line.cornerPoints
//                        val lineFrame = line.boundingBox
                        /*for (element in line.elements) {
                            val elementText = element.text
                            val elementConfidence = element.confidence
                            val elementLanguages = element.recognizedLanguages
                            val elementCornerPoints = element.cornerPoints
                            val elementFrame = element.boundingBox
                        }*/
                    }
                }
                if (linewiseText == "") {
                    addMessageToAdapter("Sorry we could not find any text in the image. Please try again", true)

                } else {
                    addMessageToAdapter(linewiseText, true)
                }

            }
            .addOnFailureListener {
                addMessageToAdapter("Sorry we could not find any text in the image. Please try again", true)
            }
    }

    override fun onStartTyping() {
        Log.v("Typing listener", "Start Typing")
    }

    override fun onStopTyping() {
        Log.v("Typing listener", "Stop Typing")
    }

    private fun makeRequest(paragraph: String, question: String) {
       val message = MessagesFixtures.getTextMessage(
            "Fetching the answer to the question",
            true
        )
        val id  = message.id
        super.messagesAdapter.addToStart(message, true)
        apiService.getResult(paragraph, question).enqueue(object : Callback<ResultModel> {
            override fun onFailure(call: Call<ResultModel>, t: Throwable) {
                t.printStackTrace()
                deleteMessage(id)
                addMessageToAdapter("Sorry! could not reach the server. Please check your internet connection", true)
            }

            override fun onResponse(call: Call<ResultModel>, response: Response<ResultModel>) {
                deleteMessage(id)
                if (response.isSuccessful) {
                    val result = response.body()!!.result
                    if (result == "") {
                        addMessageToAdapter("We could not get you. Please specify your question clearly", true)
                    } else {
                        addMessageToAdapter(result.toString(), true)
                    }
                }
            }
        })
    }

    private fun addMessageToAdapter(text: String, isBot: Boolean) {
        super.messagesAdapter.addToStart(
            MessagesFixtures.getTextMessage(
                text,
                isBot
            ),
            true
        )
    }

    private fun deleteMessage(id: String) {
        super.messagesAdapter.deleteById(id)
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setTitle("Quest Away")
            .setMessage("Are you sure you don't want to explore more?")
            .setPositiveButton(android.R.string.yes
            ) { dialog, which -> finish() }
            .setNegativeButton(android.R.string.no, null)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }
}
