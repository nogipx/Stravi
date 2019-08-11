package com.nogipx.stravi.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nogipx.stravi.R

class CreateWebPageActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "activities.CreateWebPage"
        const val EXTRA_PAGE = "$TAG.EXTRA_PAGE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_web_page)
    }
}
