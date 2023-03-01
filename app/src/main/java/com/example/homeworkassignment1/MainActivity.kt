package com.example.homeworkassignment1

import android.app.Activity
import android.content.Intent
import android.media.Image
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // fill speed dial buttons from shared preferences
        val speedDialButtonsIds = listOf("speedDial1", "speedDial2", "speedDial3")
        for (id in speedDialButtonsIds) {
            val name = getFromSpeedDial(id)
            if (name != null) {
                val button = findViewById<Button>(resources.getIdentifier(id, "id", packageName))
                button.text = "$name"
            }
        }

        val number = findViewById<TextView>(R.id.number)

        // iterate through all buttons
        iterateButtons("button", 12,
            onClick = { button -> number.text = "${number.text}${button.text}" })

        val deleteButton = findViewById<ImageButton>(R.id.delete)
        deleteButton.setOnClickListener {
            val numberText = number.text
            if (numberText.isNotEmpty()) { number.text = numberText.subSequence(0, numberText.length - 1) }
        }
        deleteButton.setOnLongClickListener {
            number.text = ""
            true
        }

        val dialButton = findViewById<ImageButton>(R.id.dial)
        dialButton.setOnClickListener { dialPhoneNumber(number.text.toString()) }

        // iterate through all speed dial buttons
        iterateButtons("speedDial", 3,
            onClick = { button -> dialPhoneNumber(getFromSpeedDial(button.text.toString())) },
            onLongClick = { button -> launchSpeedDialConfig(resources.getResourceEntryName(button.id)) })
    }

    private fun iterateButtons(id: String, buttonCount: Int, onClick: ((Button) -> Unit)? = null, onLongClick: ((Button) -> Unit)? = null) {
        for (i in 1..buttonCount) {
            val buttonName = "$id$i"
            val dynamicId = resources.getIdentifier(buttonName, "id", packageName)
            val button = findViewById<Button>(dynamicId) ?: continue

            if (onClick != null) { button.setOnClickListener { onClick(button) } }
            if (onLongClick != null) { button.setOnLongClickListener { onLongClick(button); true } }
        }
    }

    private fun dialPhoneNumber(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber")
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }

    private val speedDialConfigLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val name = data?.getStringExtra("name")
            val number = data?.getStringExtra("number")
            val title = data?.getStringExtra("title")

            // update button name
            val button = findViewById<Button>(resources.getIdentifier(title, "id", packageName)) ?: return@registerForActivityResult
            button.text = "$name"

            // save number
            val sharedPref = getSharedPreferences("speedDial", MODE_PRIVATE)
            with (sharedPref.edit()) {
                putString(title, name)
                putString(name, number)
                apply()
            }
        }
    }

    private fun launchSpeedDialConfig(title: String) {
        val intent = Intent(this, SpeedDialConfig::class.java)
        intent.putExtra("title", title)
        speedDialConfigLauncher.launch(intent)
    }

    private fun getFromSpeedDial(key: String): String {
        val sharedPref = getSharedPreferences("speedDial", MODE_PRIVATE)
        return sharedPref.getString(key, null) ?: ""
    }

}