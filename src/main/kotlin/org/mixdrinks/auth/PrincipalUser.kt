package org.mixdrinks.auth

import io.ktor.server.auth.Principal
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class PrincipalUser(
    @SerialName("userId") val userId: String = "",
    @SerialName("displayName") val displayName: String = ""
) : Principal
