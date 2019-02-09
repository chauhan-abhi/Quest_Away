package com.abhi.questaway.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.abhi.questaway.R
import android.net.Uri
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.abhi.questaway.base.ImagePickerActivity
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import android.graphics.Bitmap
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import com.karumi.dexter.Dexter
import com.karumi.dexter.listener.PermissionRequest
import java.lang.Exception


class HomeScreenActivity : AppCompatActivity() {

    private val REQUEST_IMAGE = 100
    lateinit var button: Button
    lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button = findViewById(R.id.upload_button)
        textView = findViewById(R.id.textView)
        button.setOnClickListener {
            onSelectImageClicked()
        }
        ImagePickerActivity.clearCache(this)
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

    /*private fun showSettingsDialog() {
        val builder = AlertDialog.Builder(this@HomeScreenActivity)
        builder.setTitle(getString(R.string.dialog_permission_title))
        builder.setMessage(getString(R.string.dialog_permission_message))
        builder.setPositiveButton(getString(R.string.go_to_settings), { dialog, which->
            dialog.cancel()
            openSettings() })
        builder.setNegativeButton(getString(android.R.string.cancel), { dialog, which-> dialog.cancel() })
        builder.show()
    }*/

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

        val result = detector.processImage(image)
            .addOnSuccessListener { firebaseVisionText ->
                var linewiseText = ""
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
                    textView.text = linewiseText
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Oops! Text recognition failed", Toast.LENGTH_SHORT).show()
            }
    }
}

