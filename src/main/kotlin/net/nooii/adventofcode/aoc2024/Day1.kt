package net.nooii.adventofcode.aoc2024

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader
import net.nooii.adventofcode.helpers.add
import kotlin.math.abs

object Day1 {

    private class Data(
        val list1: List<Int>,
        val list2: List<Int>
    )

    @JvmStatic
    fun main(args: Array<String>) {
        val input = InputLoader(AoCYear.AOC_2024).loadStrings("Day1Input")
        val data = processInput(input)
        part1(data)
        part2(data)
    }

    private fun part1(data: Data) {
        println(data.list1.zip(data.list2).sumOf { (first, second) -> abs(first - second) })
    }

    private fun part2(data: Data) {
        val occurrences = mutableMapOf<Int, Int>()
        data.list2.forEach { occurrences.add(it, 1) }
        println(data.list1.sumOf { it * occurrences.getOrDefault(it, 0) })
    }

    private fun processInput(input: List<String>): Data {
        val firstList = mutableListOf<Int>()
        val secondList = mutableListOf<Int>()
        for (line in input) {
            val (first, second) = line.split(Regex("\\s+")).map { it.toInt() }
            firstList.add(first)
            secondList.add(second)
        }
        firstList.sort()
        secondList.sort()
        return Data(firstList, secondList)
    }
}