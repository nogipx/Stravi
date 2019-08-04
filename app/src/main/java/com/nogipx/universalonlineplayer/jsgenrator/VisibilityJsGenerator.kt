package com.nogipx.universalonlineplayer.jsgenrator

import org.jsoup.nodes.Element

/**
 * @author https://github.com/nogipx
 */
open class VisibilityJsGenerator : JsGenerator(){

    /* JS Composition Snippets */

    fun addCSS(text: String, containerSelector: String = "head") {
        val css = injectElementJS(containerSelector, element = styleTag(text))
        generationChain.add(css)
    }

    fun injectElementJS(containerSelector: String, position: String = "beforeend",
                        element: Element, screen: Boolean = false) =
        FunctionJS(code = """
            document.querySelector(${containerSelector.vq()}).insertAdjacentHTML(
                ${position.vq()}, 
                ${ if (screen) element.toString().wq(screen = true)
                    else element.toString().wq()}
            );
        """
    )

    fun removeElementsJS(selector: String) =
        FunctionJS(code = """
            let targets = document.querySelectorAll(${selector.vq()});
            targets.forEach(
            ${FunctionJS("", "e", "e.remove()")}
            );
        """
        )

    fun addClassJS(selector: String, cssClass: String) =
        FunctionJS(code = """
            let targets = document.querySelectorAll(${selector.vq()});
            targets.forEach(
                ${FunctionJS("", "e", "e.classList.add(${cssClass.vq()})")}
            );
        """
    )

    fun removeClassJS(selector: String, cssClass: String) =
        FunctionJS(code = """
            let targets = document.querySelectorAll(${selector.vq()});
            targets.forEach(
            ${FunctionJS("", "e", "e.classList.remove(${cssClass.vq()})")}
            );
        """
    )
}