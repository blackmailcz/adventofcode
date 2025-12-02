package net.nooii.adventofcode.aoc2021

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader
import net.nooii.adventofcode.helpers.add
import net.nooii.adventofcode.helpers.copy

/**
 * Created by Nooii on 06.12.2021
 */
object Day6 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = InputLoader(AoCYear.AOC_2021).loadStrings("Day6Input")
        val fish = processInput(input)
        part1(fish.copy())
        part2(fish.copy())
    }

    private fun part1(fish: MutableMap<Int, Long>) {
        processDays(fish, 80)
        println(countFish(fish))
    }

    private fun part2(fish: MutableMap<Int, Long>) {
        processDays(fish, 256)
        println(countFish(fish))
    }

    private fun processInput(rawInput: List<String>): MutableMap<Int, Long> {
        val input = rawInput.first().split(",").map { it.toInt() }.toMutableList()
        val dayMap = mutableMapOf<Int, Long>()
        for (day in input) {
            dayMap.add(day, 1)
        }
        return dayMap
    }

    private fun processDay(dayMap: MutableMap<Int, Long>) {
        val newDayMap = mutableMapOf<Int, Long>()
        for ((daysLeft, fish) in dayMap) {
            if (daysLeft == 0) {
                newDayMap.add(6, fish)
                newDayMap.add(8, fish)
            } else {
                newDayMap.add(daysLeft - 1, fish)
            }
        }
        dayMap.clear()
        dayMap.putAll(newDayMap)
    }

    private fun processDays(dayMap: MutableMap<Int, Long>, days: Long) {
        for (i in 0 until days) {
            processDay(dayMap)
        }
    }

    private fun countFish(fish: MutableMap<Int, Long>) = fish.values.sum()

}