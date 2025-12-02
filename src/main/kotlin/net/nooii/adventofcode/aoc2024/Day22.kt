package net.nooii.adventofcode.aoc2024

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader
import net.nooii.adventofcode.helpers.add

object Day22 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = InputLoader(AoCYear.AOC_2024).loadInts("Day22Input")
        part1(input)
        // Runtime ~ 1 second
        part2(input)
    }

    private fun part1(input: List<Int>) {
        val sum = input.sumOf { number ->
            (1..2000).fold(number.toLong()) { acc, _ -> computeSecretNumber(acc) }
        }
        println(sum)
    }

    private fun part2(input: List<Int>) {
        val offset = 4
        val bananas = mutableMapOf<List<Int>, Long>()
        for (number in input) {
            // Compute sequence of last digits of first 2000 secret numbers
            val digits = computeLastDigits(number)
            // Compute price differences
            val diffs = computeDiffs(digits)
            // We need to remember sequences we already visited
            val visited = mutableSetOf<List<Int>>()
            // Iterate sequence from offset-th element to the end
            for ((i, digit) in digits.drop(offset).withIndex()) {
                // Cache key is the last offset count of diffs before corresponding index
                val key = diffs.subList(i, i + offset)
                // If the given key was not visited yet, add it to the cache and increment bananas
                if (key !in visited) {
                    visited.add(key)
                    bananas.add(key, digit.toLong())
                }
            }
        }
        // Find maximum number of bananas we can get
        println(bananas.values.maxOrNull())
    }

    private fun computeLastDigits(input: Int): List<Int> {
        val digits = mutableListOf(input.lastDigit())
        var number = input.toLong()
        repeat(2000) {
            number = computeSecretNumber(number)
            digits += number.lastDigit()
        }
        return digits
    }

    private fun computeDiffs(digits: List<Int>): List<Int> {
        return digits.zipWithNext { a, b -> a - b }
    }

    private fun computeSecretNumber(number: Long): Long = number.secret1().secret2().secret3()
    private fun Long.secret1(): Long = (this * 64).mix(this).prune()
    private fun Long.secret2(): Long = (this / 32).mix(this).prune()
    private fun Long.secret3(): Long = (this * 2048).mix(this).prune()
    private fun Long.mix(number: Long) = this xor number
    private fun Long.prune() = this % 16777216

    private fun Long.lastDigit() = (this % 10).toInt()
    private fun Int.lastDigit() = this % 10
}