package com.nymp.phselgy.feature_load.additional

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import com.nymp.phselgy.R
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking


@SuppressLint("SetJavaScriptEnabled")
@Composable
fun NympthWebViewScreen(
    viewModel: NympthWebViewModel,
    openGame: () -> Unit
) {
    val context = LocalContext.current
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.filePathCallback!!.onReceiveValue(arrayOf(uri))
            viewModel.filePathCallback = null
        } else {
            viewModel.filePathCallback!!.onReceiveValue(null)
            viewModel.filePathCallback = null
        }
    }
    val fileChooser = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            var results: Array<Uri>? = null

            if (viewModel.capturedImageUri != null) {
                results = arrayOf(viewModel.capturedImageUri!!)
            }

            viewModel.filePathCallback!!.onReceiveValue(results)
            viewModel.filePathCallback = null

        } else if (result.resultCode == Activity.RESULT_CANCELED) {
            viewModel.filePathCallback!!.onReceiveValue(null)
            viewModel.filePathCallback = null
            val contentResolver: ContentResolver = context.contentResolver
            if (viewModel.capturedImageUri != null) {
                contentResolver.delete(viewModel.capturedImageUri!!, null, null)
                viewModel.capturedImageUri = null
            }
        }
    }

    Box(modifier = Modifier) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                WebView(it).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    viewModel.onBackHandler = {
                        if (this.canGoBack()) this.goBack()
                        else (it as Activity).finish()
                    }
                    setSettings()
                    webChromeClient = NympthWebChromeClient(it, viewModel)
                    webViewClient = NympthWebViewClient(it, { openGame() }, viewModel.preferencesManager)
                    val link = runBlocking { viewModel.preferencesManager.getLink().first() }
                    loadUrl(link)
                }
            }
        )

        if (viewModel.isDialogVisible) {
            BottomDialog(
                onDismiss = {
                    viewModel.isDialogVisible = false
                    viewModel.filePathCallback?.onReceiveValue(null)
                    viewModel.filePathCallback = null
                },
                onCamera = {
                    val values = ContentValues()
                    values.put(MediaStore.Images.Media.TITLE, "New Picture")
                    values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera")

                    val mUri = context.contentResolver.insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        values
                    )
                    viewModel.capturedImageUri = mUri

                    val intentCamera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, mUri)
                    viewModel.isDialogVisible = false
                    fileChooser.launch(intentCamera)
                },
                onFiles = {
                    viewModel.isDialogVisible = false
                    imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }
            )
        }
        BackHandler {
            viewModel.onBackHandler()
        }

        LinearProgressIndicator(
            trackColor = Color.White,
            color = Color.Black,
            progress = viewModel.progress,
            modifier = Modifier
                .fillMaxWidth()
                .alpha(
                    if (viewModel.progress == 1f) 0f else 1f
                )
        )
    }

}




@SuppressLint("SetJavaScriptEnabled")
fun WebView.setSettings() {
    this.apply {
        settings.apply {
            userAgentString = this@setSettings.settings.userAgentString
            javaScriptEnabled = true
            cacheMode = WebSettings.LOAD_NO_CACHE
            domStorageEnabled = true
            databaseEnabled = true
            setSupportZoom(false)
            displayZoomControls = false
            builtInZoomControls = false
            allowFileAccess = true
            allowContentAccess = true
            loadWithOverviewMode = true
            useWideViewPort = true
            javaScriptCanOpenWindowsAutomatically = true
        }
        scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        requestFocus(View.FOCUS_DOWN)
        setLayerType(View.LAYER_TYPE_HARDWARE, null)
    }

    val cookieManager: CookieManager = CookieManager.getInstance()
    cookieManager.setAcceptCookie(true)
    cookieManager.acceptCookie()
    cookieManager.setAcceptThirdPartyCookies(this, true)
    cookieManager.flush()
}

@Composable
fun BottomDialog(
    onDismiss: () -> Unit,
    onCamera: () -> Unit,
    onFiles: () -> Unit
) {
    Dialog(
        onDismissRequest = {
            onDismiss()
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        val dialogWindowProvider = LocalView.current.parent as DialogWindowProvider
        dialogWindowProvider.window.setGravity(Gravity.BOTTOM)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .background(Color.Gray)
                .padding(16.dp)
        ) {
            Text(
                text = "Choose an action",
                fontSize = 32.sp,
                fontFamily = FontFamily.Default,
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.CenterHorizontally),
                color = Color.Black,
                fontWeight = FontWeight.Bold,

                )
            Spacer(Modifier.height(10.dp))
            Row {
                Spacer(modifier = Modifier.width(50.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.LightGray)
                            .padding(7.dp)
                            .clickable {
                                onCamera()
                            }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.google_camera),
                            contentDescription = null,
                        )
                    }
                    Spacer(Modifier.height(5.dp))

                    Text(
                        text = "Camera",
                        fontSize = 18.sp,
                        fontFamily = FontFamily.Default,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,

                        )
                }
                Spacer(Modifier.weight(1f))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.LightGray)
                            .padding(7.dp)
                            .clickable { onFiles() }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.gallery),
                            contentDescription = null,
                        )
                    }
                    Spacer(Modifier.height(5.dp))
                    Text(
                        text = "Gallery",
                        fontSize = 18.sp,
                        fontFamily = FontFamily.Default,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(50.dp))


            }
            Spacer(Modifier.height(30.dp))

        }

    }
}