package com.abhi.questaway.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.abhi.questaway.R
import com.abhi.questaway.base.FilePickerActivity
import com.abhi.questaway.events.SelectImageEvent
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.greenrobot.eventbus.EventBus
import android.net.Uri
import android.widget.Toast
import com.abhi.questaway.events.TextViewSetEvent
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage


class HomeScreenActivity : FilePickerActivity() {

    private val CAMERA_REQUEST_CODE = 10000
    private val GALLERY_REQUEST_CODE = 20000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            val homeFragment = HomeFragment()
            val ft = supportFragmentManager.beginTransaction()
            ft.add(R.id.content_frame, homeFragment, HomeFragment::class.java.simpleName).commit()
        }
    }

    override fun setUpComponent() {
    }

    override fun setUpViewHolder(view: View?) {
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (data != null && data.extras != null) {
                if (requestCode == CAMERA_REQUEST_CODE || requestCode == GALLERY_REQUEST_CODE) {
                    onImageResult(this, requestCode, resultCode, data)
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public fun selectImageEvent(event: SelectImageEvent) {
        val fileName = "IMG"
        selectImage(this, { imageFile, _ ->

            startTextRecognition(Uri.fromFile(imageFile))

        }, fileName)
    }

    private fun startTextRecognition(uri: Uri) {

        val image = FirebaseVisionImage.fromFilePath(this, uri)

        val detector = FirebaseVision.getInstance()
            .onDeviceTextRecognizer
        val result = detector.processImage(image)
            .addOnSuccessListener { firebaseVisionText ->
                // Task completed successfully
                // ...
                var linewiseText = ""
                for (block in firebaseVisionText.textBlocks) {
//                    val boundingBox = block.boundingBox
//                    val cornerPoints = block.cornerPoints
//                    val blockConfidence = block.confidence
//                    val blockLanguages = block.recognizedLanguages
//                    val blockCornerPoints = block.cornerPoints
//                    val blockFrame = block.boundingBox

                    val text = block.text
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

                    EventBus.getDefault().post(TextViewSetEvent(linewiseText))

                }
            }
            .addOnFailureListener {
                // Task failed with an exception
                // ...
                Toast.makeText(this, "sdas", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onImageResult(context: Context, requestCode: Int, resultCode: Int, data: Intent?) {
        super.onImageResult(context, requestCode, resultCode, data)
        supportActionBar!!.setTitle("Image to text")
    }

}

