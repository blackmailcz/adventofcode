package net.nooii.adventofcode2021

import net.nooii.adventofcode2021.helpers.InputLoader

/**
 * Created by Nooii on 22.12.2021
 */
class Day22 {

    private class Step(
        val on : Boolean,
        val cuboid : Cuboid
    )

    private data class Cuboid(
        val x1 : Int,
        val x2 : Int,
        val y1 : Int,
        val y2 : Int,
        val z1 : Int,
        val z2 : Int
    ) {
        override fun toString() = "([$x1,$y1,$z1]-[$x2,$y2,$z2]"

        private fun intersects(other : Cuboid) : Boolean {
            val x = other.x1 <= x2 && other.x2 >= x1
            val y = other.y1 <= y2 && other.y2 >= y1
            val z = other.z1 <= z2 && other.z2 >= z1
            return x && y && z
        }

        fun cut(cutter : Cuboid) : Set<Cuboid> {
            // Cut off slices on each side of each axis. Add the slice and lessen the original
            var rest = copy()
            if (!intersects(cutter)) {
                return setOf(this)
            }
            val cuts = mutableSetOf<Cuboid>()
            // Cut X left
            if (cutter.x1 in rest.x1 + 1..rest.x2) {
                cuts.add(rest.copy(x2 = cutter.x1 - 1))
                rest = rest.copy(x1 = cutter.x1)
            }
            // Cut X right
            if (cutter.x2 in rest.x1 until rest.x2) {
                cuts.add(rest.copy(x1 = cutter.x2 + 1))
                rest = rest.copy(x2 = cutter.x2)
            }
            // Cut Y left
            if (cutter.y1 in rest.y1 + 1..rest.y2) {
                cuts.add(rest.copy(y2 = cutter.y1 - 1))
                rest = rest.copy(y1 = cutter.y1)
            }
            // Cut Y right
            if (cutter.y2 in rest.y1 until rest.y2) {
                cuts.add(rest.copy(y1 = cutter.y2 + 1))
                rest = rest.copy(y2 = cutter.y2)
            }
            // Cut Z left
            if (cutter.z1 in rest.z1 + 1..rest.z2) {
                cuts.add(rest.copy(z2 = cutter.z1 - 1))
                rest = rest.copy(z1 = cutter.z1)
            }
            // Cut Z right
            if (cutter.z2 in rest.z1 until rest.z2) {
                cuts.add(rest.copy(z1 = cutter.z2 + 1))
            }
            return cuts
        }

        fun volume() = (x2 - x1 + 1).toLong() * (y2 - y1 + 1).toLong() * (z2 - z1 + 1).toLong()

    }

    companion object {

        private val part1filter : (step : Step) -> Boolean = { step ->
            with(step.cuboid) {
                mutableListOf(x1, y1, z1, x2, y2, z2).all { it in -50..50 }
            }
        }

        @JvmStatic
        fun main(args : Array<String>) {
            val input = InputLoader().loadStrings("Day22Input")
            val steps = processInput(input)
            solution(steps.filter(part1filter))
            solution(steps)
        }

        private fun solution(steps : List<Step>) {
            var onCuboids = mutableSetOf<Cuboid>()
            for (step in steps) {
                val nextOnCuboids = mutableSetOf<Cuboid>()
                // Cut a hole in all on cuboids for the new cuboid
                for (onCuboid in onCuboids) {
                    nextOnCuboids.addAll(onCuboid.cut(step.cuboid))
                }
                // And add it only if it should be turned on
                if (step.on) {
                    nextOnCuboids.add(step.cuboid)
                }
                onCuboids = nextOnCuboids
            }
            println(onCuboids.sumOf { it.volume() })
        }

        private fun processInput(input : List<String>) : List<Step> {
            return input.map { line ->
                val matches = Regex("(on|off) x=(-?\\d+)..(-?\\d+),y=(-?\\d+)..(-?\\d+),z=(-?\\d+)..(-?\\d+)")
                    .findAll(line, 0)
                    .first()
                    .groupValues
                    .drop(1)
                val willTurnOn = matches[0] == "on"
                val c = matches.drop(1).map { it.toInt() }
                Step(willTurnOn, Cuboid(c[0], c[1], c[2], c[3], c[4], c[5]))
            }
        }

    }
}