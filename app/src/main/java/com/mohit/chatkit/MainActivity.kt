package com.mohit.chatkit

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.mohit.chatkit.helper.toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    /*val pref =
        applicationContext.getSharedPreferences("com.mohit.chatkit", Context.MODE_PRIVATE)
    val editor = pref.edit()*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // userId.setText(pref.getString("USER_ID", ""))
    }

    fun submit(view: View) {
        if (userId.text.isNotEmpty()) {
            // editor.putString("USER_ID", userId.text.trim().toString())
            startActivity(
                Intent(this, RoomsActivity::class.java)
                    .putExtra("userId", userId.text.trim().toString())
            )
        } else {
            toast("Please Enter a User Id")
        }
    }
}
