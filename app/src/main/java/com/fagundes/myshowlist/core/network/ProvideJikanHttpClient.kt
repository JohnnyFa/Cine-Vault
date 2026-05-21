package com.fagundes.myshowlist.core.network

import com.fagundes.myshowlist.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.takeFrom

fun provideJikanHttpClient(): HttpClient =
    baseHttpClient().config {
        defaultRequest {
            url {
                takeFrom(BuildConfig.JIKAN_BASE_URL)
            }
        }
    }
