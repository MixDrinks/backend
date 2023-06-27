package org.mixdrinks.auth

import io.ktor.server.auth.Principal
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class FirebasePrincipalUser(
    @SerialName("userId") val userId: String = "",
) : Principal
