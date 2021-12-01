package net.nooii.adventofcode2021.helpers

/**
 * Created by Nooii on 01.12.2021
 */
class InputLoader {
    fun getInput(resource: String) : List<Int> {
        return javaClass
            .getResource("/$resource")!!
            .readText()
            .lines()
            .map { it.toInt() }
    }
}