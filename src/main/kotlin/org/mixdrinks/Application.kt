package org.mixdrinks

import com.typesafe.config.ConfigFactory
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.cors.routing.*
import org.mixdrinks.plugins.configureRouting
import org.mixdrinks.plugins.configureSecurity
import org.mixdrinks.view.cocktails

fun main() {
    embeddedServer(Netty, environment = applicationEngineEnvironment {
        config = HoconApplicationConfig(ConfigFactory.load())

        module {
            configureRouting()
            configureSecurity()
            install(CORS) {
                anyHost()
                allowMethod(HttpMethod.Options)
                allowMethod(HttpMethod.Put)
                allowMethod(HttpMethod.Patch)
                allowMethod(HttpMethod.Delete)
            }
            cocktails()
        }

        connector {
            port = 8080
            host = "0.0.0.0"
        }
    }).start(true)
}
