package com.sample.restuarant.search.network

import android.content.Context
import android.util.Log
import com.sample.restuarant.search.R
import okhttp3.Interceptor
import okhttp3.Response

/* @author Dinesh Kumar 
   @creation_date 3/8/2020*/

class CredentialsInterceptor(val context: Context) : Interceptor {

    private val tag = "CredentialsInterceptor"

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val newRequestUrl = request.url().newBuilder()
            .addQueryParameter("client_id", context.resources.getString(R.string.api_creds_key))
            .addQueryParameter(
                "client_secret",
                context.resources.getString(R.string.api_creds_secret)
            )
            .build()

        val newRequest = request.newBuilder().url(newRequestUrl)
            .build()

        Log.d(tag, "New Request url: ${newRequest.url()}")
        return chain.proceed(newRequest)
    }

}