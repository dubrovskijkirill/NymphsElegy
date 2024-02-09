package com.nymp.phselgy.feature_load.additional

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.content.ContextCompat
import com.nymp.phselgy.feature_load.PreferencesManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class NympthWebViewClient(
    private val context: Context,
    private val openGame: () -> Unit,
    private val preferencesManager: PreferencesManager
) : WebViewClient() {
    override fun shouldOverrideUrlLoading(
        view: WebView?,
        request: WebResourceRequest
    ): Boolean {
        return rewriteLink(view!!, request.url.toString())
    }

    private fun rewriteLink(view: WebView, url: String): Boolean {
        val isBot = try {
            val botParam = Uri.parse(url).getQueryParameter("bot")
            botParam?.toBoolean() ?: false
        } catch (e: Exception) {
            false
        }
        return if (isBot) {
            runBlocking {
                preferencesManager.setLink("")
            }
            openGame()
            true
        } else if (url.startsWith("mailto:")) {
            ContextCompat.startActivity(context, Intent(Intent.ACTION_SENDTO, Uri.parse(url)), null)
            true
        } else if (url.startsWith("tg:") || url.startsWith("https://t.me") || url.startsWith("https://telegram.me")) {
            try {
                ContextCompat.startActivity(
                    context,
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(view.hitTestResult.extra)
                    ),
                    null
                )
            } catch (_: Exception) {
            }
            true
        } else if (url.startsWith("https://diia.app") || url.startsWith("privat24")) {
            try {
                ContextCompat.startActivity(
                    context,
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(url)
                    ),
                    null
                )
            } catch (_: Exception) {
            }
            true
        } else {
            false
        }
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        val firstView = runBlocking { preferencesManager.getFirstView().first() }
        if (firstView) {
            if (url != null) {
                runBlocking { preferencesManager.setLink(url) }
            }
            runBlocking { preferencesManager.setNotFirstView() }
            CookieManager.getInstance().flush()
        }
        CookieManager.getInstance().flush()
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
    }
}