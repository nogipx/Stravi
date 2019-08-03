package com.nogipx.universalonlineplayer.deprecated.players

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.parser.Parser
import org.slf4j.LoggerFactory
import java.net.URL

/**
 * @author https://github.com/nogipx
 *
 * Create iframe to target site.
 * Pass js methods for manipulate appearing.
 */
open class PlayerExtractorJS (
    private val url: URL,
    private val iframeName: String,
    private val showSelectors: List<String>?) {

    private val log = LoggerFactory.getLogger(javaClass)

    companion object {
        private const val HIDE_CLASS = "player-extractor-hide"
        private const val HTML_SKELETON = """
        <!doctype html>
            <html>
                <head><meta name="viewport" content="width=device-width, initial-scale=1"></head>
                <body></body>
            </html>
        """
    }

    val htmlSkeleton: Document
        get() = Parser.parse(HTML_SKELETON.trimIndent(), "https://")

    private fun style(text: String) : Element = Element("styleTag").text(text.trimIndent())
    private fun script(text: String) : Element = Element("script").text(text.trimIndent())

    fun html() : String {
        return downloadPage().toString()
    }

    fun downloadPageAsync(url: URL) = GlobalScope.async {
        try {
            val connection: Connection = Jsoup.connect(url.toString())
            val resp: Connection.Response = connection.execute()
            resp.parse()
        } catch (e: Exception) {
            log.error("Page not downloaded.")
            null
        }
    }

    fun downloadPage() : Document {
        val pageAsync = downloadPageAsync(url)
        var page: Document? = null
        runBlocking {
            page = pageAsync.await()
        }
        return modifyHtml(page!!)
    }

    fun modifyHtml(doc: Document) : Document {
        doc.body().appendChild(script("""
            ${if (showSelectors != null) showTargetsJSR(showSelectors) else showTargetsJSR(listOf(""))}
            ${hideBodyJSR()}
        """.trimIndent()))

        return doc
    }

    fun getHtml() : String {
        return """
            <div>
                ${genIframe(url, iframeName)}
                ${style("""
                    .$HIDE_CLASS{ display: none; }
                    
                    html, body {
                        margin: 0;
                    }
                    iframe[name="$iframeName"] {
                        height: 100%;
                        width: 100%;
                    }
                """.trimIndent())}
                ${script("""
                    ${if (showSelectors != null) showTargetsJSR(showSelectors) else showTargetsJSR(listOf(""))}
                    ${hideBodyJSR()}
                """.trimIndent())}
            </div>
        """.trimIndent()
    }

    fun genIframe(url: URL, name: String) : Element {
        val mIframe = Element("iframe")
        mIframe
            .attr("src", url.toString())
            .attr("name", name)
            .attr("width", "100%")
            .attr("height", "100%")
            .attr("sandbox", "allow-same-origin allow-scripts")
            .attr("referrerpolicy", "unsafe-url")
            .attr("allowfullscreen", true)
        return mIframe
    }

    /**
     * Naming styleTag for methods containing JS snippets:
     *
     * fun <jsMethodName>JS[F(FunctionJS)]>() {...}
     */

    // Immediately Invoked Function Expression
    fun makeUrlIIFE(code: String) = "javascript:($code)();".trimIndent()

    fun showTargetsJSR(selectors: List<String>) : String {
        val targetsSelector = selectors.joinToString()
        return removeHideClassJSR(targetsSelector, HIDE_CLASS, "showTargets")
    }

    fun hideBodyJSR() : String =
        addHideClassJSR("body *", HIDE_CLASS, "hideBody")

    fun addHideClassJSR(selector: String, hideClass: String,
                        funName: String = "addClassJS",
                        document: String = "document") = """
            FunctionJS $funName() {
                var targets = $document.querySelectorAll("$selector");
                targets.forEach(FunctionJS(e) {e.classList.add("$hideClass")});}
        """.trimIndent()

    fun removeHideClassJSR(selector: String, hideClass: String,
                           funName: String = "removeHideClass",
                           document: String = "document") = """
            FunctionJS $funName() {
                var targets = $document.querySelectorAll("$selector");
                targets.forEach(FunctionJS(e) {e.classList.remove("$hideClass")});}
        """.trimIndent()
}