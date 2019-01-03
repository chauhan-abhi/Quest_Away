package com.abhi.questaway.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.abhi.questaway.R
import com.abhi.questaway.base.FilePickerActivity
import com.abhi.questaway.events.SelectImageEvent
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.greenrobot.eventbus.EventBus
import android.graphics.BitmapFactory





class HomeScreenActivity : FilePickerActivity() {

    private val CAMERA_REQUEST_CODE = 10000
    private val GALLERY_REQUEST_CODE = 20000
    private lateinit var bitmap: Bitmap

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
            if (requestCode == CAMERA_REQUEST_CODE || requestCode == GALLERY_REQUEST_CODE) {
                onImageResult(this, requestCode, resultCode, data)
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public fun selectImageEvent(event: SelectImageEvent) {
        selectImage(this, { imageFile, tag ->
            val bmOptions = BitmapFactory.Options()

            bitmap = BitmapFactory.decodeFile(imageFile.absolutePath, bmOptions)

            startTextRecognition(bitmap)

        }, "PROFILE_PIC")
    }

    private fun startTextRecognition(bitmap: Bitmap?) {


    }

    override fun onImageResult(context: Context, requestCode: Int, resultCode: Int, data: Intent?) {
        super.onImageResult(context, requestCode, resultCode, data)
        supportActionBar!!.setTitle("hjsdbfj")
    }

}

