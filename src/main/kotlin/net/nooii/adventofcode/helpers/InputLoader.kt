package net.nooii.adventofcode.helpers

/**
 * Created by Nooii on 01.12.2021
 */
class InputLoader(private val year: AoCYear) {

    fun loadInts(resource: String): List<Int> {
        return loadStrings(resource).map { it.toInt() }
    }

    fun loadStrings(resource: String): List<String> {
        return javaClass
            .getResource("/${year.resourcePath}/$resource")!!
            .readText()
            .lines()
    }

}