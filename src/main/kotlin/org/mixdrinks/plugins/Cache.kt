package org.mixdrinks.plugins

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cachingheaders.*
import io.ktor.server.request.*
import java.util.concurrent.TimeUnit

fun Application.configureCache() {
    install(CachingHeaders) {
        options { call, outgoingContent ->
            when {
                isCachedCall(call) -> CachingOptions(
                    CacheControl.MaxAge(
                        maxAgeSeconds = TimeUnit.MINUTES.toSeconds(1).toInt(),
                        visibility = CacheControl.Visibility.Public
                    )
                )
                else -> null
            }
        }
    }
}

private fun isCachedCall(call: ApplicationCall) =
    call.request.httpMethod == HttpMethod.Get &&
            (call.request.path().contains("meta/all") || call.request.path().contains("tags/all"))
