package com.nymp.phselgy.feature_load

object Son {

    init {
        System.loadLibrary("native-lib")
    }

    private external fun adrNativeValues(): Map<String, String>

    fun getValue(key: String): String {
        return adrNativeValues()[key]
            ?: throw IllegalStateException("Key was not found, did you forget to add/remove it in native-lib.cpp?")
    }
}