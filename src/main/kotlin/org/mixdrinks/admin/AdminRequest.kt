package org.mixdrinks.admin

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AdminRequest(
    @SerialName("login") val login: String,
    @SerialName("password") val password: String
)

@Serializable
data class AdminResponse(
    @SerialName("login") val login: String,
)
