package net.nooii.adventofcode.aoc2015

import net.nooii.adventofcode.helpers.*

object Day9 {

    private data class City(
        val name: String
    ) {
        val distances = mutableNNMapOf<City, Int>()
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val input = InputLoader(AoCYear.AOC_2015).loadStrings("Day9Input")
        val cities = parseInput(input)
        val distances = computeDistances(cities)
        part1(distances)
        part2(distances)
    }

    private fun part1(distances: List<Int>) {
        println(distances.min())
    }

    private fun part2(distances: List<Int>) {
        println(distances.max())
    }

    private fun computeDistances(cities: Set<City>): List<Int> {
        val finalResults = mutableListOf<List<City>>()
        for (from in cities) {
            for (to in cities) {
                val results = mutableListOf<List<City>>()
                computePaths(from, to, cities.size, results)
                results
                    .filter { it.size == cities.size }
                    .forEach { finalResults.add(it) }
            }
        }
        return finalResults.map { computeDistance(it) }
    }

    private fun computeDistance(result: List<City>): Int {
        return result
            .windowed(2, 1)
            .sumOf {
                it[0].distances[it[1]]
            }
    }

    private fun computePaths(
        from: City,
        to: City,
        requiredPathSize: Int,
        results: MutableList<List<City>>,
        path: List<City> = emptyList(),
    ) {
        val nextPath = path + from
        when {
            from != to -> {
                for (nextCity in from.distances.keys) {
                    if (nextCity !in nextPath) {
                        computePaths(
                            nextCity,
                            to,
                            requiredPathSize,
                            results,
                            nextPath
                        )
                    }
                }
            }

            nextPath.size == requiredPathSize -> results.add(nextPath)
        }
    }

    private fun parseInput(input: List<String>): Set<City> {
        val cities = mutableSetOf<City>()
        for (line in input) {
            parseDistance(line, cities)
        }
        return cities
    }

    private fun parseDistance(line: String, cities: MutableSet<City>) {
        val matches = Regex("(\\w+) to (\\w+) = (\\d+)").captureFirstMatch(line)
        val distance = matches[2].toInt()
        val fromCity = cities.find { it.name == matches[0] } ?: City(matches[0]).also { cities.add(it) }
        val toCity = cities.find { it.name == matches[1] } ?: City(matches[1]).also { cities.add(it) }
        fromCity.distances[toCity] = distance
        toCity.distances[fromCity] = distance
    }
}