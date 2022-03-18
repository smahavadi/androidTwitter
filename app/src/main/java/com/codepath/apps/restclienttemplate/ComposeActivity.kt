package com.codepath.apps.restclienttemplate

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.codepath.apps.restclienttemplate.models.Tweet
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers

class ComposeActivity : AppCompatActivity() {

    lateinit var etCompose: EditText
    lateinit var btnTweet: Button

    lateinit var client: TwitterClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compose)

        etCompose = findViewById(R.id.etTweetCompose)
        btnTweet = findViewById(R.id.btnTweet)

        client = TwitterApplication.getRestClient(this)

        val etValue = findViewById<TextView>(R.id.characterCount)
        etCompose.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // Fires right as the text is being changed (even supplies the range of text)
                    val charactersRemaining = 140 - s.length
                    etValue.text = charactersRemaining.toString()
                    if (charactersRemaining <= 0) {
                        etValue.setTextColor(Color.parseColor("#FF0000"))
                    }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Fires right before text is changing
            }

            override fun afterTextChanged(s: Editable) {
                // Fires right after the text has changed
            }
        })

        // Handling the user's click on the tweet button
        btnTweet.setOnClickListener {
            // Grab the content of edit text
            val tweetContent = etCompose.text.toString()
            // make sure tweet isnt empty
            if (tweetContent.isEmpty()) {
                Toast.makeText(this, "Empty tweets not allowed!", Toast.LENGTH_SHORT).show()
                // display SnackBar message
            } else {
                // make sure tweet is under character count
                if (tweetContent.length > 140) {
                    Toast.makeText(this, "Tweet is too long! Limit is 140 characters", Toast.LENGTH_SHORT).show()
                } else {
                    client.publishTweet(tweetContent, object : JsonHttpResponseHandler(){

                        override fun onSuccess(statusCode: Int, headers: Headers, json: JSON) {
                            Log.i(TAG, "Successfully published tweet!")
                            // send tweet back to TimelineActivity
                            val tweet = Tweet.fromJson(json.jsonObject)

                            val intent = Intent()
                            intent.putExtra("tweet", tweet)
                            setResult(RESULT_OK, intent)
                            finish()
                        }

                        override fun onFailure(
                            statusCode: Int,
                            headers: Headers?,
                            response: String?,
                            throwable: Throwable?
                        ) {
                            Log.e(TAG, "Failed to publish tweet", throwable)
                        }
                    })
                }
            }

        }

    }

    companion object {
        val TAG = "ComposeActivity"
    }
}