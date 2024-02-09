package com.nymp.phselgy.feature_load.additional

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class NympthWebChromeClient(
    private val context: Context,
    private val viewModel: NympthWebViewModel
) : WebChromeClient() {

    override fun onProgressChanged(view: WebView?, newProgress: Int) {
        viewModel.progress = newProgress / 100f
    }

    override fun onShowFileChooser(
        webView: WebView?,
        filePathCallback: ValueCallback<Array<Uri>>?,
        fileChooserParams: FileChooserParams?
    ): Boolean {
        val permsCheck = ContextCompat.checkSelfPermission(
            context as Activity,
            android.Manifest.permission.CAMERA,
        )
        if (permsCheck == PackageManager.PERMISSION_GRANTED) {
            viewModel.filePathCallback?.onReceiveValue(null)
            viewModel.filePathCallback = filePathCallback

            viewModel.isDialogVisible = true

            return true
        } else checkPermissions(context)
        return false
    }
}

private fun checkPermissions(context: Context) {
    val permissions: Array<String> =
        arrayOf(
            android.Manifest.permission.CAMERA,
        )

    ActivityCompat.requestPermissions(
        context as Activity, permissions, 1
    )
}