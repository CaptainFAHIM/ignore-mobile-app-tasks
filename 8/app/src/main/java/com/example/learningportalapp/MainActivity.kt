package com.example.learningportalapp

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.webkit.*
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar
    private lateinit var urlBar: EditText

    private val homeUrl = "https://www.google.com"

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webView)
        progressBar = findViewById(R.id.progressBar)
        urlBar = findViewById(R.id.urlBar)

        val btnBack = findViewById<Button>(R.id.btnBack)
        val btnForward = findViewById<Button>(R.id.btnForward)
        val btnRefresh = findViewById<Button>(R.id.btnRefresh)
        val btnHome = findViewById<Button>(R.id.btnHome)
        val btnGo = findViewById<Button>(R.id.btnGo)

        // WebView settings
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true

        webView.webViewClient = object : WebViewClient() {

            override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                progressBar.visibility = ProgressBar.VISIBLE
                urlBar.setText(url)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                progressBar.visibility = ProgressBar.GONE
                urlBar.setText(url)
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                view?.loadUrl("file:///android_asset/offline.html")
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                progressBar.progress = newProgress
            }
        }

        loadUrl(homeUrl)

        // Buttons
        btnGo.setOnClickListener { loadUrl(urlBar.text.toString()) }
        btnHome.setOnClickListener { loadUrl(homeUrl) }
        btnRefresh.setOnClickListener { webView.reload() }

        btnBack.setOnClickListener {
            if (webView.canGoBack()) webView.goBack()
            else Toast.makeText(this, "No more history", Toast.LENGTH_SHORT).show()
        }

        btnForward.setOnClickListener {
            if (webView.canGoForward()) webView.goForward()
        }

        // Shortcuts
        findViewById<Button>(R.id.btnGoogle).setOnClickListener { loadUrl("https://google.com") }
        findViewById<Button>(R.id.btnYouTube).setOnClickListener { loadUrl("https://youtube.com") }
        findViewById<Button>(R.id.btnWiki).setOnClickListener { loadUrl("https://wikipedia.org") }
        findViewById<Button>(R.id.btnKhan).setOnClickListener { loadUrl("https://khanacademy.org") }
        findViewById<Button>(R.id.btnUni).setOnClickListener { loadUrl(homeUrl) }

        // ✅ MODERN BACK HANDLING (NO onBackPressed)
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (webView.canGoBack()) {
                    webView.goBack()
                } else {
                    finish()
                }
            }
        })
    }

    private fun loadUrl(url: String) {
        val finalUrl = if (url.startsWith("http")) url else "https://$url"

        if (isOnline()) {
            webView.loadUrl(finalUrl)
        } else {
            webView.loadUrl("file:///android_asset/offline.html")
        }
    }

    private fun isOnline(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}