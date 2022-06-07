package org.mixdrinks

import com.typesafe.config.ConfigFactory
import io.ktor.http.*
import io.ktor.http.HttpMethod.Companion.DefaultMethods
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import org.jetbrains.exposed.sql.Database
import org.mixdrinks.plugins.configureRouting
import org.mixdrinks.plugins.configureSecurity
import org.mixdrinks.view.cocktail.cocktails
import org.mixdrinks.view.items.items
import org.mixdrinks.view.tag.tags

fun main() {
    embeddedServer(Netty, environment = applicationEngineEnvironment {
        config = HoconApplicationConfig(ConfigFactory.load())

        module {
            configureRouting()
            configureSecurity()
            install(CORS) {
                anyHost()
                allowHeaders { true }
                allowCredentials = true
                DefaultMethods.forEach(::allowMethod)
            }

            install(ContentNegotiation) {
                json()
            }

            val databaseUrl = environment.config.property("ktor.database.url").getString()
            val user = environment.config.property("ktor.database.user").getString()
            val password = environment.config.property("ktor.database.password").getString()

            Database.connect(
                url = "jdbc:postgresql://$databaseUrl?sslmode=require",
                user = user,
                password = password,
            )

            cocktails()
            tags()
            items()
        }

        connector {
            port = 8080
            host = "0.0.0.0"
        }
    }).start(true)
}
