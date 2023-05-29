package org.mixdrinks.view.snapshot.sitemap

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName
import nl.adaptivity.xmlutil.serialization.XmlValue

/** The dto object for create xml sitemap the following format:
 * <urlset>
 * <url>
 * <loc>https://mixdrinks.org/url1</loc>
 * </url>
 * <url>
 * <loc>https://mixdrinks.org/url2</loc>
 * </url>
</urlset>

 **/

@Serializable
@XmlSerialName("urlset", "", "")
data class Urlset(
    @XmlElement(true)
    val url: List<Url>
)

@Serializable
@XmlSerialName("url", "", "")
data class Url(
    @XmlValue(true)
    val loc: Loc
) {
    constructor(loc: String) : this(Loc(loc))
}

@Serializable
@XmlSerialName("loc", "", "")
data class Loc(
    @XmlValue(true)
    val loc: String
)
