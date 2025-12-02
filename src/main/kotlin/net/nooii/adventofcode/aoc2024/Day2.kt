package net.nooii.adventofcode.aoc2024

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader
import kotlin.math.abs

object Day2 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = InputLoader(AoCYear.AOC_2024).loadStrings("Day2Input")
        val reports = processInput(input)
        part1(reports)
        part2(reports)
    }

    private fun part1(reports: List<List<Int>>) {
        val sum = reports.count { report ->
            analyzeReport(report)
        }
        println(sum)
    }

    private fun part2(reports: List<List<Int>>) {
        val sum = reports.count { report ->
            report.indices.any { i ->
                analyzeReport(report.toMutableList().apply { removeAt(i) })
            }
        }
        println(sum)
    }

    private fun analyzeReport(report: List<Int>): Boolean {
        val allIncreasing = check(report) { a, b -> a > b }
        val allDecreasing = check(report) { a, b -> a < b }
        return (allIncreasing || allDecreasing) && check(report) { a, b -> abs(a - b) in 1..3 }
    }

    private fun check(report: List<Int>, predicate: (a: Int, b: Int) -> Boolean): Boolean {
        return report.windowed(2, 1).all { (a, b) -> predicate(a, b) }
    }

    private fun processInput(input: List<String>): List<List<Int>> {
        return input.map { line ->
            line.split(Regex("\\s+")).map { it.toInt() }
        }
    }
}