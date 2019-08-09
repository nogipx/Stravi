package com.nogipx.stravi.jsgenerator

import java.util.UUID.randomUUID

/**
 * @param name
 * In case 'random' is a random name.
 * In case null is an empty name.
 * In other case is passed argument value.
 *
 * @return String representing js-function.
 */
class FunctionJS(
    val name: String = "",
    private val args: String = "",
    private val code: String = "") {

    private fun randomName() = "r"+ randomUUID().toString().replace("-", "")

    // Immediately Invoked Function Expression
    fun iife() = "($this)();"

    override fun toString(): String =
        "function ${if (name == "random") randomName() else name}" +
            "(${args.trim()})" +
            "{${code.trimIndent()
                .replace("\n", "")
                .replace("[ ]{2,}".toRegex(), " ")}}"

    fun isEmpty() : Boolean = name.isEmpty() && args.isEmpty() && code.isEmpty()

    fun isNotEmpty() : Boolean = !isEmpty()

}