package net.nooii.adventofcode2021

import net.nooii.adventofcode2021.helpers.InputLoader

/**
 * Created by Nooii on 22.12.2021
 */
class Day22 {

    private data class Point3D(
        val x : Int,
        val y : Int,
        val z : Int
    ) {
        override fun toString() = "[$x,$y,$z]"
    }

    private class Step(
        val on : Boolean,
        val cuboid : Cuboid
    )

    private data class Cuboid(
        val from : Point3D,
        val to : Point3D
    ) {
        override fun toString() = "($from-$to)"

        private fun intersects(other : Cuboid) : Boolean {
            val x = other.from.x <= this.to.x && other.to.x >= this.from.x
            val y = other.from.y <= this.to.y && other.to.y >= this.from.y
            val z = other.from.z <= this.to.z && other.to.z >= this.from.z
            return x && y && z
        }

        private fun copy(
            x1 : Int = this.from.x,
            x2 : Int = this.to.x,
            y1 : Int = this.from.y,
            y2 : Int = this.to.y,
            z1 : Int = this.from.z,
            z2 : Int = this.to.z
        ) : Cuboid {
            return Cuboid(Point3D(x1, y1, z1), Point3D(x2, y2, z2))
        }

        fun cut(cutter : Cuboid) : Set<Cuboid> {
            // Cut off slices on each side of each axis. Add the slice and lessen the original
            var rest = Cuboid(from, to)
            if (!intersects(cutter)) {
                return setOf(this)
            }
            val cuts = mutableSetOf<Cuboid>()
            // Cut X left
            if (cutter.from.x in rest.from.x + 1..rest.to.x) {
                cuts.add(rest.copy(x2 = cutter.from.x - 1))
                rest = rest.copy(x1 = cutter.from.x)
            }
            // Cut X right
            if (cutter.to.x in rest.from.x until rest.to.x) {
                cuts.add(rest.copy(x1 = cutter.to.x + 1))
                rest = rest.copy(x2 = cutter.to.x)
            }
            // Cut Y left
            if (cutter.from.y in rest.from.y + 1..rest.to.y) {
                cuts.add(rest.copy(y2 = cutter.from.y - 1))
                rest = rest.copy(y1 = cutter.from.y)
            }
            // Cut Y right
            if (cutter.to.y in rest.from.y until rest.to.y) {
                cuts.add(rest.copy(y1 = cutter.to.y + 1))
                rest = rest.copy(y2 = cutter.to.y)
            }
            // Cut Z left
            if (cutter.from.z in rest.from.z + 1..rest.to.z) {
                cuts.add(rest.copy(z2 = cutter.from.z - 1))
                rest = rest.copy(z1 = cutter.from.z)
            }
            // Cut Z right
            if (cutter.to.z in rest.from.z until rest.to.z) {
                cuts.add(rest.copy(z1 = cutter.to.z + 1))
            }
            return cuts
        }

        fun size() : Long {
            return (to.x - from.x + 1).toLong() * (to.y - from.y + 1).toLong() * (to.z - from.z + 1).toLong()
        }

    }

    companion object {

        private val part1filter : (step : Step) -> Boolean = { step ->
            with(step.cuboid) {
                !mutableListOf(from.x, from.y, from.z, to.x, to.y, to.z).any { it !in -50..50 }
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
            println(onCuboids.sumOf { it.size() })
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
                Step(willTurnOn, Cuboid(Point3D(c[0], c[2], c[4]), Point3D(c[1], c[3], c[5])))
            }
        }

    }
}