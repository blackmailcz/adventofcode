package net.nooii.adventofcode.aoc2025

import net.nooii.adventofcode.helpers.*

object Day5 {

    private data class Ingredients(
        val freshRanges: List<LongRange>,
        val available: Set<Long>
    )

    @JvmStatic
    fun main(args: Array<String>) {
        val input = InputLoader(AoCYear.AOC_2025).loadStrings("Day5Input")
        val ingredients = processInput(input)
        part1(ingredients)
        part2(ingredients)
    }

    private fun part1(ingredients: Ingredients) {
        val sum = ingredients.available.count { ingredient ->
            ingredients.freshRanges.any { ingredient in it }
        }
        println(sum)
    }

    private fun part2(ingredients: Ingredients) {
        var ranges = setOf<LongRange>()
        for (freshRange in ingredients.freshRanges) {
            val overlapping = ranges.filter { it.overlaps(freshRange) }.sortedBy { it.first }
            ranges = when {
                overlapping.isEmpty() -> {
                    ranges + setOf(freshRange)
                }
                else -> {
                    val merged = LongRange(
                        start = minOf(overlapping.first().first, freshRange.first),
                        endInclusive = maxOf(overlapping.last().last, freshRange.last)
                    )
                    ranges - overlapping.toSet() + setOf(merged)
                }
            }
        }
        val sum = ranges.sumOf { it.size() }
        println(sum)
    }

    private fun processInput(input: List<String>): Ingredients {
        val (ranges, available) = input.splitByEmptyLine()
        return Ingredients(
            freshRanges = ranges.map { line ->
                val (first, last) = line.split("-").map { it.toLong() }
                LongRange(first, last)
            },
            available = available.map { it.toLong() }.toSet()
        )
    }
}
