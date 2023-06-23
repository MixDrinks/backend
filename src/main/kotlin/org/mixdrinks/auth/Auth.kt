package org.mixdrinks.auth

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.AuthenticationConfig
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.basic
import io.ktor.server.auth.bearer
import org.jetbrains.exposed.sql.transactions.transaction
import org.mixdrinks.admin.Admin
import org.mixdrinks.admin.AdminTable
import org.mixdrinks.admin.getHashFunction

const val FIREBASE_AUTH = "FIREBASE_AUTH"
const val KEY_ADMIN_AUTH = "admin-auth"
const val KEY_SUPPER_ADMIN_AUTH = "supper-admin-auth"

fun Application.configureAuth(supperAdminToken: String, adminPasswordsSlat: String) {
    val digestFunction = getHashFunction(adminPasswordsSlat)

    install(Authentication) {
        basic(KEY_ADMIN_AUTH) {
            realm = "Access to the '/admin/' path"
            validate { credentials ->
                val user = transaction {
                    Admin.find { AdminTable.name eq credentials.name }.firstOrNull()
                }

                return@validate if (user?.password?.contentEquals(digestFunction(credentials.password)) == true) {
                    UserIdPrincipal(user.login)
                } else {
                    null
                }
            }
        }
        bearer(KEY_SUPPER_ADMIN_AUTH) {
            authenticate { tokenCredential ->
                if (tokenCredential.token == supperAdminToken) {
                    UserIdPrincipal("supper_admin")
                } else {
                    null
                }
            }
        }
        firebase {
            validate {
                PrincipalUser(it.uid, it.name.orEmpty())
            }
        }
    }
}

fun AuthenticationConfig.firebase(
    name: String? = FIREBASE_AUTH,
    configure: FirebaseConfig.() -> Unit
) {
    val provider = FirebaseAuthProvider(FirebaseConfig(name).apply(configure))
    register(provider)
}
