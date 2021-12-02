package net.nooii.adventofcode2021.helpers

/**
 * Created by Nooii on 01.12.2021
 */
class InputLoader {

    fun loadInts(resource: String) : List<Int> {
        return loadStrings(resource).map { it.toInt() }
    }

    fun loadStrings(resource: String): List<String> {
        return javaClass
            .getResource("/$resource")!!
            .readText()
            .lines()
    }

}