package com.nogipx.universalonlineplayer

import com.nogipx.universalonlineplayer.jsgenrator.FunctionJS
import java.io.File

fun main(args: Array<String>) {

    val PATH = "C:\\Users\\nogipx\\AndroidStudioProjects\\UniversalOnlinePlayer\\app\\src\\main\\java\\com\\nogipx\\universalonlineplayer"
    val tst = File("$PATH\\test.html")

    println(FunctionJS().isEmpty())
}