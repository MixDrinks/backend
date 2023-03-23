package org.mixdrinks.view.controllers.filter

class FilterSource(
    private val filterCache: FilterCache,
) {

    /**
     * Return the list of FilterGroup, and sort each filter list by count of cocktails.
     */
    fun getMetaInfo(): List<FilterModels.FilterGroup> {
        return filterCache.fullFilterGroupBackend
            .mapNotNull { (filter, filters) ->
                FilterModels.FilterGroup(
                    filterGroupBackend = filter,
                    items = filters
                        .map {
                            FilterModels.FilterItem(it.id, it.name, it.cocktailIds.size, it.slug)
                        }
                        .sortedBy { it.cocktailCount }
                        .reversed(),
                    sortOrder = filter.sortOrder,
                )
            }
    }
}
