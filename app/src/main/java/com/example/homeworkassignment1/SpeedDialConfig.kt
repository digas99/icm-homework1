package com.example.homeworkassignment1

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class SpeedDialConfig : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_speed_dial_config)

        val returns = mutableMapOf<String, String>()
        returns["title"] = intent.getStringExtra("title") ?: ""

        val titleValue = returns["title"]
        val title = findViewById<TextView>(R.id.title)
        title.text = titleValue

        val apply = findViewById<TextView>(R.id.apply)
        val inputName = findViewById<TextView>(R.id.inputName)
        val inputPhone = findViewById<TextView>(R.id.inputPhone)
        apply.setOnClickListener {
            if (inputName.text.isEmpty() || inputPhone.text.isEmpty()) {
                return@setOnClickListener
            }

            returns["name"] = inputName.text.toString()
            returns["number"] = inputPhone.text.toString()
            returnToMain(returns)
        }
    }

    private fun returnToMain(results: Map<String, String>) {
        val returnIntent = Intent()

        for ((key, value) in results) {
            returnIntent.putExtra(key, value)
        }
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }
}