package net.nooii.adventofcode.helpers

/**
 * A utility class for loading input data for Advent of Code challenges.
 *
 * @property year The AoCYear object representing the year of the Advent of Code challenge.
 */
class InputLoader(private val year: AoCYear) {

    /**
     * Loads a list of integers from a specified resource file.
     *
     * @param resource The name of the resource file to load.
     * @return A list of integers parsed from the lines of the resource file.
     */
    fun loadInts(resource: String): List<Int> {
        return loadStrings(resource).map { it.toInt() }
    }

    /**
     * Loads a list of strings from a specified resource file.
     *
     * @param resource The name of the resource file to load.
     * @return A list of strings, where each string represents a line from the resource file.
     */
    fun loadStrings(resource: String): List<String> {
        return javaClass
            .getResource("/${year.resourcePath}/$resource")!!
            .readText()
            .lines()
    }
}