package org.mixdrinks.view.controllers.search

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.mixdrinks.dto.FilterGroupId
import org.mixdrinks.dto.FilterId
import org.mixdrinks.view.cocktail.CompactCocktailVM

@Serializable
data class SearchResponse(
    @SerialName("totalCount") val totalCount: Int,
    @SerialName("cocktails") val cocktails: List<CompactCocktailVM>,
    @SerialName("futureCounts") val futureCounts: Map<FilterGroupId, List<FilterCount>>,
    @SerialName("descriptions") val description: String?,
)

@Serializable
data class FilterCount(
    @SerialName("id") val id: FilterId,
    @SerialName("count") val count: Int,
    @SerialName("query") val query: String,
)

