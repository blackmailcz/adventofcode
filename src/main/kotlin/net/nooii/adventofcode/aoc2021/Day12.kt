package net.nooii.adventofcode.aoc2021

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader

/**
 * Created by Nooii on 12.12.2021
 */
class Day12 {

    private class Cave(val id: String) {

        val isBig = id.any { it.isUpperCase() }
        val paths = mutableSetOf<Cave>()

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Cave) return false

            if (id != other.id) return false

            return true
        }

        override fun hashCode(): Int {
            return id.hashCode()
        }

        override fun toString() = id
    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2021).loadStrings("Day12Input")
            val startCave = processInput(input)
            part1(startCave)
            part2(startCave)
        }

        private fun part1(startCave: Cave) {
            println(next(startCave, canVisitSmallAgain = false))
        }

        private fun part2(startCave: Cave) {
            println(next(startCave, canVisitSmallAgain = true))
        }

        private fun next(cave: Cave, path: MutableList<Cave> = mutableListOf(), canVisitSmallAgain: Boolean): Int {
            path.add(cave)
            return if (cave.id == "end") {
                1 // Successful path
            } else {
                cave.paths.sumOf { nextCave ->
                    val visitingSmallAgain = canVisitSmallAgain && !nextCave.isBig && nextCave.id != "start" && path.contains(nextCave)
                    if (nextCave.isBig || !path.contains(nextCave) || visitingSmallAgain) {
                        next(nextCave, path.toMutableList(), canVisitSmallAgain && !visitingSmallAgain)
                    } else {
                        0 // Dead end
                    }
                }
            }
        }

        private fun processInput(input: List<String>): Cave {
            val caves = mutableMapOf<String, Cave>()
            for (path in input) {
                val pathData = path.split("-")
                val fromCave = getOrCreateCave(pathData[0], caves)
                val toCave = getOrCreateCave(pathData[1], caves)
                fromCave.paths.add(toCave)
                toCave.paths.add(fromCave) // You can also travel backwards
            }
            return caves["start"]!!
        }

        private fun getOrCreateCave(id: String, caves: MutableMap<String, Cave>): Cave {
            return caves[id] ?: Cave(id).also { caves[id] = it }
        }

    }

}