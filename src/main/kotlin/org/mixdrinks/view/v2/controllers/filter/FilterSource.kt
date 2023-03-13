package org.mixdrinks.view.v2.controllers.filter

class FilterSource(
    private val filterCache: FilterCache,
) {

    /**
     * Return the list of FilterGroup, and sort each filter list by count of cocktails.
     */
    fun getMetaInfo(): List<FilterModels.FilterGroup> {
        return filterCache.fullFilters
            .mapNotNull { (filter, filters) ->
                FilterModels.FilterGroup(
                    filters = filter,
                    items = filters
                        .map {
                            FilterModels.FilterItem(it.id, it.name, it.cocktailIds.size)
                        }
                        .sortedBy { it.cocktailCount }
                        .reversed()
                )
            }
    }
}
