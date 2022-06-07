package org.mixdrinks.plugins

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cachingheaders.*
import java.util.concurrent.TimeUnit

fun Application.configureCache() {
    install(CachingHeaders) {
        options { call, outgoingContent ->
            when (outgoingContent.contentType?.withoutParameters()) {
                ContentType.Application.Json -> CachingOptions(
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