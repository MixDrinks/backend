package org.mixdrinks.auth

import com.google.firebase.auth.FirebaseToken
import io.ktor.http.auth.HttpAuthHeader
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.AuthenticationFunction
import io.ktor.server.auth.AuthenticationProvider
import io.ktor.server.auth.parseAuthorizationHeader

class FirebaseConfig(name: String?) : AuthenticationProvider.Config(name) {
    internal var authHeader: (ApplicationCall) -> HttpAuthHeader? =
        { call -> call.request.parseAuthorizationHeader() }

    var firebaseAuthenticationFunction: AuthenticationFunction<FirebaseToken> = {
        throw NotImplementedError(FIREBASE_IMPLEMENTATION_ERROR)
    }

    fun validate(validate: suspend ApplicationCall.(FirebaseToken) -> PrincipalUser?) {
        firebaseAuthenticationFunction = validate
    }
}

private const val FIREBASE_IMPLEMENTATION_ERROR =
    "Firebase  auth validate function is not specified, use firebase { validate { ... } } to fix this"
