package com.nogipx.universalonlineplayer.deprecated.players

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.slf4j.LoggerFactory
import java.net.URL


class CustomVideoPlayer(
    override val pageUrl: String,
    private val targetSelectors: List<String>? = null,
    internal val removeSelectors: List<String>? = null) : VideoPlayer {

    private val log = LoggerFactory.getLogger(javaClass)

    private val url = URL(pageUrl)
    private val connection = Jsoup.connect(url.toString())
    val pageHtml: Document = connection.get()

    override fun getEmbedHtml(): String {
        log.debug("Current site URL: $pageUrl")
        val embed = getSkeleton(pageHtml.clone())
        targetSelectors ?: return pageHtml.toString()

        for (selector in targetSelectors) {
            val elements = pageHtml.select(selector)
            log.debug("Found ${elements.size} elements by '$selector' selector.")

            elements.forEach {
                attachToRoot(it, embed)
            }
        }
        replaceSources("http://${url.host}", embed)

        return embed.toString()
    }

    fun replaceSources(domain: String, doc: Document) : Document {
        val elements = doc.select("[src], [href]")

        elements.forEach {
            replaceLocalImport(it, domain)
            log.debug("New link Element :: $it")
        }
        log.debug("Replaced ${elements.size} source links")
        return doc
    }

    fun replaceLocalImport(element: Element, domain: String) : Element{
        if (!element.hasAttr("src") && !element.hasAttr("href"))
            return element
        val attrName = if (element.hasAttr("src")) "src" else "href"
        val link = element.attr(attrName)

        if ("^/[a-zA-Z0-9]".toRegex().containsMatchIn(link)) {
            val newLink = "$domain$link"
            element.attr(attrName, newLink)
            log.debug("Change $link to $newLink.")
        }
        if ("^//[a-zA-Z0-9]".toRegex().containsMatchIn(link)) {
            val newLink = "${url.protocol}:$link"
            element.attr(attrName, newLink)
            log.debug("Change $link to $newLink.")
        }

        return element
    }

    fun hideBody(doc: Document) : Document {
        doc.body().allElements.forEach {
            it.attr("styleTag", "display: none")
        }
        return doc
    }

    fun getSkeleton(doc: Document) : Document {
        return deleteSelectors(removeSelectors, clearBody(doc))
    }

    fun clearBody(doc: Document) : Document {
        doc.body().empty()
        return doc
    }

    fun relativeLinkToAbsolute(doc: Document, domain: String) {

    }

    fun attachToRoot(element: Element, html: Document) {
        html.body().appendChild(element)
    }

    fun deleteSelectors(selectors: List<String>?, doc: Document) : Document {
        selectors ?: return doc

        for (selector in selectors) {
            val tags = doc.select(selector)
            log.debug("Delete ${tags.size} '$selector' tags")
            tags.remove()
        }

        return doc
    }
}

