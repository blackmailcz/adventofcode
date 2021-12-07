package net.nooii.adventofcode2021

import net.nooii.adventofcode2021.helpers.InputLoader
import kotlin.math.abs

/**
 * Created by Nooii on 07.12.2021
 */
class Day7 {

    private class Crabs(
        val min : Int,
        val max : Int,
        val list : List<Int>
    )

    companion object {

        @JvmStatic
        fun main(args : Array<String>) {
            val input = InputLoader().loadStrings("Day7Input")
            val crabs = processInput(input)
            part1(crabs)
            part2(crabs)
        }

        private fun part1(crabs : Crabs) {
            println(computeMin(crabs, fuel = { it }))
        }

        private fun part2(crabs : Crabs) {
            println(computeMin(crabs, fuel = { (it * (it + 1)) / 2 }))
        }

        private fun computeMin(crabs : Crabs, fuel : (Int) -> Int) : Int {
            return IntRange(crabs.min, crabs.max).minOf { i -> crabs.list.sumOf { fuel(abs(it - i)) } }
        }

        private fun processInput(input : List<String>) : Crabs {
            val crabs = input.first().split(",").map { it.toInt() }
            val min = crabs.minOf { it }
            val max = crabs.maxOf { it }
            return Crabs(min, max, crabs)
        }
    }

}