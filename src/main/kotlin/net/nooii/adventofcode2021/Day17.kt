package net.nooii.adventofcode2021

import net.nooii.adventofcode2021.helpers.InputLoader
import java.awt.Point

/**
 * Created by Nooii on 17.12.2021
 */
class Day17 {

    private class Area(
        val fromX : Int,
        val toX : Int,
        val fromY : Int,
        val toY : Int
    )

    private class Probe(
        var vx : Int,
        var vy : Int,
        val area : Area
    ) {

        var point = Point(0, 0)
        var highestY = 0

        private fun step() {
            point = Point(point.x + vx, point.y + vy)
            when {
                vx > 0 -> vx--
                vx < 0 -> vx++
            }
            vy--
            if (point.y > highestY) {
                highestY = point.y
            }
        }

        private fun isInArea() = point.x >= area.fromX && point.x <= area.toX && point.y >= area.fromY && point.y <= area.toY

        fun launch() : Boolean {
            while (point.y >= area.fromY) {
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
        fun main(args : Array<String>) {
            val input = InputLoader().loadStrings("Day17Input")
            val area = processInput(input)
            solution(area)
        }

        private fun solution(area : Area) {
            var highestY = 0
            var sum = 0
            for (vx in 0 until area.toX + 1) {
                for (vy in area.fromY until (-area.fromY + 1)) {
                    val probe = Probe(vx, vy, area)
                    if (probe.launch()) {
                        sum++
                        if (probe.highestY > highestY) {
                            highestY = probe.highestY
                        }
                    }
                }
            }
            println(highestY)
            println(sum)
        }

        private fun processInput(input : List<String>) : Area {
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