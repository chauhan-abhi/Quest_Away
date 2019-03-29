package com.abhi.questaway.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.abhi.questaway.R
import com.abhi.questaway.di.Injector
import com.abhi.questaway.network.ResultModel
import com.abhi.questaway.network.RetrofitApiService
import dagger.android.AndroidInjection
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ParagraphActivity : AppCompatActivity() {

    lateinit var button: Button
    lateinit var question: EditText
    lateinit var apiService: RetrofitApiService
    lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paragraph)
        button = findViewById(R.id.txtSubmit)
        question = findViewById(R.id.editText)
        textView = findViewById(R.id.result)
        val paragraph = intent.getStringExtra("paragraph")
        button.setOnClickListener {
            makeRequest(paragraph, question.text.toString())
        }
    }

    private fun makeRequest(paragraph: String, question: String) {
        apiService.getResult(paragraph, question).enqueue(object : Callback<ResultModel> {
            override fun onFailure(call: Call<ResultModel>, t: Throwable) {

            }

            override fun onResponse(call: Call<ResultModel>, response: Response<ResultModel>) {
                if (response.isSuccessful) {
                    val result = response.body()!!.result
                    textView.text = result
                }
            }
        })
    }
}
