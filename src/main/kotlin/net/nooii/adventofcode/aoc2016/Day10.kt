package net.nooii.adventofcode.aoc2016

import net.nooii.adventofcode.helpers.*
import kotlin.math.max
import kotlin.math.min

class Day10 {

    private class Area(
        val robots: NNMap<Int, Robot>,
        val outputs: NNMap<Int, Output>
    )

    private sealed interface Destination {
        fun receive(value: Int)
    }

    private class Output : Destination {

        var value: Int = 0
            private set

        override fun receive(value: Int) {
            this.value = value
        }
    }

    private class Robot(val id: Int) : Destination {

        lateinit var lowerTarget: Destination
        lateinit var higherTarget: Destination

        private val values = mutableListOf<Int>()

        fun values(): Set<Int> {
            return values.toSet()
        }

        fun canGive() = values.size == 2

        override fun receive(value: Int) {
            values.add(value)
        }

        fun give() {
            val min = min(values[0], values[1])
            val max = max(values[0], values[1])
            values.remove(min)
            lowerTarget.receive(min)
            values.remove(max)
            higherTarget.receive(max)
        }
    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2016).loadStrings("Day10Input")
            val area = processInput(input)
            solution(area)
        }

        private fun solution(area: Area) {
            val part1values = setOf(17, 61)
            // To optimize, only iterate over with robots that recently received something
            // Start with robots that can give something
            var robots = area.robots.values.filter { it.canGive() }
            while (robots.isNotEmpty()) {
                val nextRobots = mutableListOf<Robot>()
                for (robot in robots) {
                    if (robot.canGive()) {
                        if (robot.values() == part1values) {
                            println(robot.id) // Part 1 solution
                        }
                        robot.give()
                        // Only add robots, not outputs
                        (robot.lowerTarget as? Robot)?.let { nextRobots.add(it) }
                        (robot.higherTarget as? Robot)?.let { nextRobots.add(it) }
                    }
                }
                robots = nextRobots
            }
            // Part 2 solution
            val product = area.outputs.filterKeys { it in 0..2 }.map { it.value.value }.product()
            println(product)
        }

        private fun processInput(input: List<String>): Area {
            // Discovery of robots / outputs
            val robotDiscoveryRegex = Regex("bot (\\d+)")
            val outputDiscoveryRegex = Regex("output (\\d+)")
            val targetDiscoveryRegex = Regex("bot (\\d+) gives low to (bot|output) (\\d+) and high to (bot|output) (\\d+)")
            val valueDiscoveryRegex = Regex("value (\\d+) goes to bot (\\d+)")
            val robots = mutableNNMapOf<Int, Robot>()
            val outputs = mutableNNMapOf<Int, Output>()
            // Discover robots and outputs
            for (line in input) {
                for (match in outputDiscoveryRegex.findAll(line)) {
                    val outputId = match.groupValues[1].toInt()
                    outputs[outputId] = Output()
                }
                for (match in robotDiscoveryRegex.findAll(line)) {
                    val robotId = match.groupValues[1].toInt()
                    robots[robotId] = Robot(robotId)
                }
            }
            // Assign targets and values
            for (line in input) {
                when {
                    line.startsWith("bot") -> {
                        val (id, lowType, lowerTarget, highType, higherTarget) = targetDiscoveryRegex.captureFirstMatch(line)
                        val robot = robots[id.toInt()]
                        robot.lowerTarget = when(lowType) {
                            "bot" -> robots[lowerTarget.toInt()]
                            "output" -> outputs[lowerTarget.toInt()]
                            else -> error("Invalid target type")
                        }
                        robot.higherTarget = when(highType) {
                            "bot" -> robots[higherTarget.toInt()]
                            "output" -> outputs[higherTarget.toInt()]
                            else -> error("Invalid target type")
                        }
                    }
                }
                if (line.startsWith("value")) {
                    val (value, target) = valueDiscoveryRegex.captureFirstMatch(line) { it.toInt() }
                    robots[target].receive(value)
                }
            }
            return Area(robots, outputs)
        }
    }
}