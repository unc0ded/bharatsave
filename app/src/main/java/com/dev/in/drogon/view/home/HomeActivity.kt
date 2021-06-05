package com.dev.`in`.drogon.view.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dev.`in`.drogon.R
import com.dev.`in`.drogon.view.register.RegistrationActivity
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setupViews()
    }

    private fun setupViews() {
        btn_help.setOnClickListener {
            val helpBottomSheet = HelpBottomSheet()
            helpBottomSheet.show(supportFragmentManager, "help_bottom_sheet")
        }
    }

    fun navigateToIntroductionScreen() {
        val intent = Intent(this@HomeActivity, RegistrationActivity::class.java).apply {
            this.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        startActivity(intent)
        finish()
    }
}