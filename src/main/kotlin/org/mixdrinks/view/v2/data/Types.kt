package org.mixdrinks.view.v2.data

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class ItemId(val value: Int)

@Serializable
@JvmInline
value class TagId(val value: Int)
