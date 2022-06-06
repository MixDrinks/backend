package org.mixdrinks

import com.typesafe.config.ConfigFactory
import io.ktor.server.config.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.mixdrinks.plugins.*
import org.mixdrinks.view.cocktails

fun main() {
    embeddedServer(Netty, environment = applicationEngineEnvironment {
        config = HoconApplicationConfig(ConfigFactory.load())

        module {
            configureRouting()
            configureSecurity()
            configureHTTP()
            cocktails()
        }

        connector {
            port = 8080
            host = "0.0.0.0"
        }
    }).start(true)
}
