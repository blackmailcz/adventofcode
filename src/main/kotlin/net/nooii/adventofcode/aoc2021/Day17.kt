package net.nooii.adventofcode.aoc2021

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader

/**
 * Created by Nooii on 17.12.2021
 */
class Day17 {

    private class Area(
        val fromX: Int,
        val toX: Int,
        val fromY: Int,
        val toY: Int
    )

    private class Probe(
        private var vx: Int,
        private var vy: Int,
        private val area: Area
    ) {

        private var x = 0
        private var y = 0
        var highestY = 0
            private set

        private fun step() {
            x += vx
            y += vy
            when {
                vx > 0 -> vx--
                vx < 0 -> vx++
            }
            vy--
            if (y > highestY) {
                highestY = y
            }
        }

        private fun isInArea() = x >= area.fromX && x <= area.toX && y >= area.fromY && y <= area.toY

        fun launch(): Boolean {
            while (y >= area.fromY) {
                step()
                if (isInArea()) {
                    return true
                }
            }
            return false
        }
    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2021).loadStrings("Day17Input")
            val area = processInput(input)
            solution(area)
        }

        private fun solution(area: Area) {
            var highestY = 0
            var validVelocities = 0
            for (vx in 0..area.toX) {
                for (vy in area.fromY..-area.fromY) {
                    val probe = Probe(vx, vy, area)
                    if (probe.launch()) {
                        validVelocities++
                        if (probe.highestY > highestY) {
                            highestY = probe.highestY
                        }
                    }
                }
            }
            println(highestY)
            println(validVelocities)
        }

        private fun processInput(input: List<String>): Area {
            val matches = Regex("x=(-?\\d+)..(-?\\d+), y=(-?\\d+)..(-?\\d+)")
                .findAll(input.first(), 0)
                .first()
                .groupValues
                .drop(1)
                .map { it.toInt() }
            return Area(matches[0], matches[1], matches[2], matches[3])
        }

    }
}