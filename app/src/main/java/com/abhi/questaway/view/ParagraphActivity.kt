package com.abhi.questaway.view

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.abhi.questaway.R
import com.abhi.questaway.network.ApiClient
import com.abhi.questaway.network.ResultModel
import com.abhi.questaway.network.RetrofitApiService
import dagger.android.AndroidInjection
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ParagraphActivity : AppCompatActivity() {

    lateinit var button: Button
    lateinit var question: EditText
    lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paragraph)
        AndroidInjection.inject(this)
        button = findViewById(R.id.txtSubmit)
        question = findViewById(R.id.editText)
        textView = findViewById(R.id.result)
        val paragraph = intent.getStringExtra("paragraph")
        button.setOnClickListener {
            makeRequest(paragraph, question.text.toString())
            hideKeyboard()
        }
    }

    private fun hideKeyboard() {
        val view = this.currentFocus
        view?.let { v ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.let { it.hideSoftInputFromWindow(v.windowToken, 0) }
        }
    }

    private fun makeRequest(paragraph: String, question: String) {
        val apiService = ApiClient.getClient(applicationContext)!!
            .create(RetrofitApiService::class.java)
        apiService.getResult(paragraph, question).enqueue(object : Callback<ResultModel> {
            override fun onFailure(call: Call<ResultModel>, t: Throwable) {
                t.printStackTrace()
                Toast.makeText(this@ParagraphActivity, "Error", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<ResultModel>, response: Response<ResultModel>) {
                if (response.isSuccessful) {
                    val result = response.body()!!.result
                    if (result == "") {
                        Toast.makeText(this@ParagraphActivity, "No result returned", Toast.LENGTH_LONG).show()
                    } else {
                        textView.text = result
                    }
                }
            }
        })
    }
}
