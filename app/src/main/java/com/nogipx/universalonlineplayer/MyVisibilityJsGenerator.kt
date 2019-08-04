package com.nogipx.universalonlineplayer

import com.nogipx.universalonlineplayer.jsgenrator.FunctionJS
import com.nogipx.universalonlineplayer.jsgenrator.VisibilityJsGenerator

/**
 * @author https://github.com/nogipx
 */
class MyVisibilityJsGenerator (
    targetsSelectors: List<String> = listOf())
    : VisibilityJsGenerator() {

    private val targetsSelector = targetsSelectors.joinToString()

    companion object {
        private const val prefix = "vis-$GENCORE"
        const val CSS_HIDE = "$prefix-hide"
        const val CSS_TARGET = "$prefix-target"
    }

    override val generationChain = mutableListOf(
        commonCssJS(),
        addClassJS(targetsSelector, CSS_TARGET),
        markupTargetsJS(),
        mobileMetaJS(),
        moveTargetsToBodyJS(),
        if (targetsSelector.isNotEmpty()) hideBodyChildrenJS() else FunctionJS(),
        showTargetsJS(),
        commonClassTargetsJS()
    )


    /* JS Applied Snippets */

    fun mobileMetaJS() =
        injectElementJS(
            "head",
            element = metaTag(mapOf(
                "name" to "viewport",
                "content" to "width=device-width, initial-scale=1"
            ))
        )

    fun moveTargetsToBodyJS() =
        FunctionJS(code = """
            document.querySelectorAll(${targetsSelector.vq()}).forEach(
                ${FunctionJS("","e", code =
                    "document.querySelector(${"body".vq()}).appendChild(e);"
                )}
            );
        """
    )

    /**
     * Makes an opportunity to styling each selected element.
     * Selects nodes by each selector (@see[showSelector]) and sets classes.
     * Sets class like "<prefix>-target-<selector_index>" if found single node.
     * Otherwise sets "<prefix>-target-<selector_index>-<found_index>"
     */
    fun markupTargetsJS() =
        FunctionJS(code = """
            ${targetsSelector.vq()}.split(',').forEach(
                ${FunctionJS(args = "selector, i", code = """
                    let targets = document.querySelectorAll(selector);
                    if (targets.length === 1) {
                        targets[0].classList.add(${CSS_TARGET.vq()});
                        targets[0].classList.add([${CSS_TARGET.vq()}, i].join('-'));
                    } else {
                        targets.forEach(
                            ${FunctionJS(args = "e, j", code = """
                                e.classList.add(${CSS_TARGET.vq()});
                                e.classList.add([${CSS_TARGET.vq()}, i].join('-'));
                                e.classList.add([${CSS_TARGET.vq()}, i, j].join('-'));
                            """)}
                        );
                    }
                """)}
            );
        """
    )

    fun target(index: Int, nested: Int? = null) = ".$CSS_TARGET-$index${if (nested != null) "-$nested" else ""}"

    fun showTargetsJS() = removeClassJS(".$CSS_TARGET", CSS_HIDE)

    fun hideBodyChildrenJS() = addClassJS("body >*", CSS_HIDE)

    fun commonClassTargetsJS() =
        FunctionJS(code = """
            document.querySelectorAll(${".$CSS_TARGET".vq()}).forEach(
                ${FunctionJS(args = "e", code = 
                    "e.classList.add(${CSS_TARGET.vq()});"
                )}
            );
        """
    )

    fun commonCssJS() : FunctionJS =
        injectElementJS("head",
            element = styleTag("""
                .$CSS_HIDE {display: none;}
                .$CSS_TARGET {
                    display: block;
                    width: 100vw;
                }
            """
        )
    )
}