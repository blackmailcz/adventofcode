package net.nooii.adventofcode.aoc2025

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader

object Day3 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = InputLoader(AoCYear.AOC_2025).loadStrings("Day3Input")
        val banks = processInput(input)
        part1(banks)
        part2(banks)
    }

    private fun part1(banks: List<List<Int>>) {
        solution(banks, 2)
    }

    private fun part2(banks: List<List<Int>>) {
        solution(banks, 12)
    }

    private fun solution(banks: List<List<Int>>, batteryCount: Int) {
        var sum = 0L
        for (initialBank in banks) {
            val digits = mutableListOf<Int>()
            var bank = initialBank
            for (index in (batteryCount - 1) downTo 0) {
                // Make sure there will be enough digits left
                val digit = bank.dropLast(index).max()
                digits.add(digit)
                // Find the first occurrence of the largest digit and cut all batteries before and including it
                bank = bank.drop(bank.indexOf(digit) + 1)
            }
            // Form a number from digits
            sum += digits.joinToString(separator = "").toLong()
        }
        println(sum)
    }

    private fun processInput(input: List<String>): List<List<Int>> {
        return input.map { line ->
            line.map { it.digitToInt() }
        }
    }
}