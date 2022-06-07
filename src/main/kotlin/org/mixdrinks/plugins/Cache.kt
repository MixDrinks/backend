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
                isGet(call) && isJson(outgoingContent) -> CachingOptions(
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

private fun isGet(call: ApplicationCall) = call.request.httpMethod == HttpMethod.Get

private fun isJson(content: OutgoingContent) = content.contentType?.withoutParameters() == ContentType.Application.Json