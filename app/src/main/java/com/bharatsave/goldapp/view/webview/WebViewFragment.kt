package com.bharatsave.goldapp.view.webview

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import androidx.fragment.app.Fragment
import com.bharatsave.goldapp.databinding.FragmentWebviewBinding

// TODO: incomplete! add custom client, handle back presses and redirects
class WebViewFragment : Fragment() {

    private var _binding: FragmentWebviewBinding? = null
    private val binding get() = _binding!!

    private var url: String? = null
    private var title: String = "BharatSave"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWebviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        val webViewActivityArgs = (context as WebViewActivity).intent?.extras?.let {
            WebViewActivityArgs.fromBundle(it)
        }
        webViewActivityArgs?.let {
            url = it.url
            it.titleText?.let { text: String ->
                title = text
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvTitle.text = title
        url?.let { setupWebView(it) }
        binding.btnBack.setOnClickListener {
            // TODO: complete this
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView(url: String) {
        binding.webView.loadUrl(url)
        binding.webView.settings.apply {
            javaScriptEnabled = true
            databaseEnabled = true
            domStorageEnabled = true
            loadsImagesAutomatically = true
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
    }

    fun handleBackPress() {
        binding.webView.evaluateJavascript("handleHardwareBackPress();", null)
    }
}