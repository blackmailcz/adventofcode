package net.nooii.adventofcode.aoc2025

import net.nooii.adventofcode.helpers.*

object Day11 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = InputLoader(AoCYear.AOC_2025).loadStrings("Day11Input")
        val nodes = processInput(input)
        part1(nodes)
        part2(nodes)
    }

    private fun part1(nodeMap: NNMap<String, Set<String>>) {
        val paths = countPaths(nodeMap, "you", "out")
        println(paths)
    }

    private fun part2(nodeMap: NNMap<String, Set<String>>) {
        // SVR -> DAC -> FFT -> OUT
        val way1 = listOf(
            countPaths(nodeMap, "svr", "dac"),
            countPaths(nodeMap, "dac", "fft"),
            countPaths(nodeMap, "fft", "out")
        )
        // SVR -> FFT -> DAC -> OUT
        val way2 = listOf(
            countPaths(nodeMap, "svr", "fft"),
            countPaths(nodeMap, "fft", "dac"),
            countPaths(nodeMap, "dac", "out")
        )
        val paths = way1.product() + way2.product()
        println(paths)
    }

    private fun countPaths(nodeMap: NNMap<String, Set<String>>, from: String, to: String): Long {
        val counts = mutableMapOf<String, Long>()

        fun countPaths(source: String, target: String): Long {
            if (source == target) {
                return 1
            }
            counts[source]?.let { return it }
            val sum = nodeMap[source].sumOf { next -> countPaths(next, target) }
            counts[source] = sum
            return sum
        }

        return countPaths(from, to)
    }

    private fun processInput(input: List<String>): NNMap<String, Set<String>> {
        val map = input.associate { line ->
            val (input, rest) = line.split(":")
            input to rest.trim().split(" ").toSet()
        }.toMutableMap()
        map["out"] = setOf()
        return map.nn()
    }
}