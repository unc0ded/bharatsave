package com.bharatsave.goldapp.view.webview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bharatsave.goldapp.R
import com.bharatsave.goldapp.databinding.ActivityWebviewBinding


class WebViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWebviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onBackPressed() {
        val navHostFragment: Fragment? =
            supportFragmentManager.findFragmentById(R.id.fragment_container)
        val activeFragment = navHostFragment?.childFragmentManager?.fragments?.get(0)
        if (activeFragment != null && activeFragment is WebViewFragment) {
            activeFragment.handleBackPress()
        } else {
            super.onBackPressed()
        }
    }
}