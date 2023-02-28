package org.mixdrinks.plugins

import io.ktor.http.CacheControl
import io.ktor.http.HttpMethod
import io.ktor.http.content.CachingOptions
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.install
import io.ktor.server.plugins.cachingheaders.CachingHeaders
import io.ktor.server.request.httpMethod
import io.ktor.server.request.path
import java.util.concurrent.TimeUnit

fun Application.configureCache() {
    install(CachingHeaders) {
        options { call, outgoingContent ->
            when {
                isCachedCall(call) -> CachingOptions(
                    CacheControl.MaxAge(
                        maxAgeSeconds = TimeUnit.HOURS.toSeconds(1).toInt(),
                        visibility = CacheControl.Visibility.Public
                    )
                )
                else -> null
            }
        }
    }
}

private fun isCachedCall(call: ApplicationCall) =
    call.request.httpMethod == HttpMethod.Get && (CACHE_RESPONSE_PATHS.any { call.request.path().contains(it) })

private val CACHE_RESPONSE_PATHS = listOf(
    "meta/all",
    "tags/all",
    "v2/filters",
    "v2/snapshot",
    "/cocktails/all",
)
