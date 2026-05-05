package com.example.findmycircle.util

import android.content.Context
import android.net.ConnectivityManager

object LoginNetworkUtil {

    fun isNetworkAvailable(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(network)
        return capabilities != null
    }

}