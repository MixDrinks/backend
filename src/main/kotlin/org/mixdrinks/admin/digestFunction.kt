package org.mixdrinks.admin

import io.ktor.util.getDigestFunction

fun getHashFunction(slatPrefix: String) = getDigestFunction("SHA-256") { pass -> "${slatPrefix}${pass.length}" }
