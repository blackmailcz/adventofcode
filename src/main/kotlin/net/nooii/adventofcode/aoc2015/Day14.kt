package net.nooii.adventofcode.aoc2015

import net.nooii.adventofcode.helpers.*
import kotlin.math.min

object Day14 {

    private data class Reindeer(
        val name: String,
        val speed: Int,
        val flyTime: Int,
        val restTime: Int
    )

    @JvmStatic
    fun main(args: Array<String>) {
        val input = InputLoader(AoCYear.AOC_2015).loadStrings("Day14Input")
        val reindeers = processInput(input)
        part1(reindeers)
        part2(reindeers)
    }

    private fun part1(reindeers: List<Reindeer>) {
        val solution = computeState(2503, reindeers)
        println(solution.values.max())
    }

    private fun part2(reindeers: List<Reindeer>) {
        val scores = mutableMapOf<Reindeer, Int>()
        for (t in 1 until 2503) {
            val state = computeState(t, reindeers)
            val bestReindeer = state.maxBy { it.value }.key
            scores.add(bestReindeer, 1)
        }
        println(scores.values.max())
    }

    private fun computeState(time: Int, reindeers: List<Reindeer>): NNMap<Reindeer, Int> {
        val map = mutableNNMapOf<Reindeer, Int>()
        for (reindeer in reindeers) {
            val cycleTime = reindeer.flyTime + reindeer.restTime
            val fullDistance = (time / cycleTime) * reindeer.flyTime * reindeer.speed
            val partDistance = min(reindeer.flyTime, time % cycleTime) * reindeer.speed
            map[reindeer] = fullDistance + partDistance
        }
        return map
    }

    private fun processInput(input: List<String>): List<Reindeer> {
        val regex = Regex("(\\w+).* (\\d+) .* (\\d+) .* (\\d+)")
        return input.map {
            val (name, speed, flyTime, restTime) = regex.captureFirstMatch(it)
            Reindeer(name, speed.toInt(), flyTime.toInt(), restTime.toInt())
        }
    }
}