package com.sample.restuarant.search.network

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

/* @author Dinesh Kumar 
   @creation_date 3/8/2020*/

class ErrorInterceptor : Interceptor {
    private val tag = "ErrorInterceptor"

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        Log.d(tag, "Request Endpoint: ${request.url()}")
        val response = chain.proceed(request)
        if (!response.isSuccessful) {
            Log.e(tag, "Response code: ${response.code()}")
        }
        return response
    }
}
