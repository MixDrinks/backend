package org.mixdrinks.view.v2

import io.ktor.server.application.Application
import org.mixdrinks.view.v2.filter.FilterSource
import org.mixdrinks.view.v2.filter.filterMetaInfo

fun Application.v2(filterSource: FilterSource) {
    this.filterMetaInfo(filterSource)
}
