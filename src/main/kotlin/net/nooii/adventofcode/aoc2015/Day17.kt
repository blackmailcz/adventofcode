package net.nooii.adventofcode.aoc2015

import com.github.shiguruikai.combinatoricskt.combinations
import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader

object Day17 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = InputLoader(AoCYear.AOC_2015).loadInts("Day17Input")
        part1(input)
        part2(input)
    }

    private fun part1(input: List<Int>) {
        var sum = 0
        for (i in input.indices) {
            sum += input.combinations(i).count { it.sum() == 150 }
        }
        println(sum)
    }

    private fun part2(input: List<Int>) {
        for (i in input.indices) {
            val count = input.combinations(i).count { it.sum() == 150 }
            if (count > 0) {
                println(count)
                return
            }
        }
        println("No answer")
    }

}