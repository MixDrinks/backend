package org.mixdrinks

import com.typesafe.config.ConfigFactory
import io.ktor.http.HttpMethod.Companion.DefaultMethods
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.config.HoconApplicationConfig
import io.ktor.server.engine.applicationEngineEnvironment
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import org.jetbrains.exposed.sql.Database
import org.mixdrinks.mongo.Mongo
import org.mixdrinks.plugins.configureCache
import org.mixdrinks.plugins.configureRedirectMiddleWare
import org.mixdrinks.plugins.configureRouting
import org.mixdrinks.plugins.static
import org.mixdrinks.view.controllers.settings.AppSettings
import org.mixdrinks.view.service
import org.mixdrinks.view.v2.api

fun main() {
    embeddedServer(Netty, environment = applicationEngineEnvironment {
        config = HoconApplicationConfig(ConfigFactory.load())

        module {
            configureRouting()
            configureCache()
            static()

            install(CORS) {
                anyHost()
                allowHeaders { true }
                allowCredentials = true
                DefaultMethods.forEach(::allowMethod)

                allowNonSimpleContentTypes = true
            }

            install(ContentNegotiation) {
                json()
            }

            val databaseUrl = environment.config.property("ktor.database.url").getString()
            val user = environment.config.property("ktor.database.user").getString()
            val password = environment.config.propertyOrNull("ktor.database.password")?.getString().orEmpty()

            Database.connect(
                url = "jdbc:postgresql://$databaseUrl",
                user = user,
                password = password,
            )

            configureRedirectMiddleWare()

            val appSettings = AppSettings(
                minVote = config.property("ktor.settings.minVote").getString().toInt(),
                maxVote = config.property("ktor.settings.maxVote").getString().toInt(),
                pageSize = config.property("ktor.settings.pageSize").getString().toInt()
            )

            val mongoString = config.property("ktor.database.mongoString").getString()
            val appVersion = config.property("ktor.app.version").getString()

            service(appVersion)

            api(appSettings, mongoString)
        }

        val port = config.property("ktor.connector.port").getString().toInt()
        val host = config.property("ktor.connector.host").getString()

        connector {
            this.port = port
            this.host = host
        }
    }).start(true)
}
