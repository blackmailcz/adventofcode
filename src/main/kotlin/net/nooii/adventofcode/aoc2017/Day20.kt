package net.nooii.adventofcode.aoc2017

import com.github.shiguruikai.combinatoricskt.combinations
import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader
import net.nooii.adventofcode.helpers.Point3D
import net.nooii.adventofcode.helpers.solveQuadraticEquation
import java.util.*
import kotlin.math.abs

object Day20 {

    private const val EPSILON = 1e-6

    private data class Particle(
        val id: Int,
        val position: Point3D,
        val velocity: Point3D,
        val acceleration: Point3D
    )

    private data class Collision(
        val particle1: Particle,
        val particle2: Particle
    )

    @JvmStatic
    fun main(args: Array<String>) {
        val input = InputLoader(AoCYear.AOC_2017).loadStrings("Day20Input")
        val particles = processInput(input)
        part1(particles)
        part2(particles)
    }

    private fun part1(particles: List<Particle>) {
        val start = Point3D(0, 0, 0)
        val closest = particles.sortedWith(
            compareBy(
                { it.acceleration.manhattanDistance(start) },
                { it.velocity.manhattanDistance(start) },
                { it.position.manhattanDistance(start) }
            )
        ).first()
        println(closest.id)
    }

    private fun part2(particles: List<Particle>) {
        val timeMap = TreeMap<Int, MutableSet<Collision>>()
        for ((particle1, particle2) in particles.combinations(2)) {
            // Detect first collision time
            val collisionTime = computeFirstCollisionTime(particle1, particle2)
            if (collisionTime != null) {
                timeMap.getOrPut(collisionTime) { mutableSetOf() }.add(Collision(particle1, particle2))
            }
        }
        val collided = mutableSetOf<Particle>()
        // Now go chronologically in time
        for ((_, collisions) in timeMap) {
            // Multiple particles can collide together at the same time, but not across rounds
            val collidedThisRound = mutableSetOf<Particle>()
            for ((particle1, particle2) in collisions) {
                if (particle1 !in collided && particle2 !in collided) {
                    collidedThisRound.add(particle1)
                    collidedThisRound.add(particle2)
                }
            }
            collided.addAll(collidedThisRound)
        }
        val result = particles.size - collided.size
        println(result)
    }

    private fun computeFirstCollisionTime(p1: Particle, p2: Particle): Int? {
        val timesX = solveAxisWithQuadratic(
            pDelta = p1.position.x - p2.position.x,
            vDelta = p1.velocity.x - p2.velocity.x,
            aDelta = p1.acceleration.x - p2.acceleration.x
        )
        val timesY = solveAxisWithQuadratic(
            pDelta = p1.position.y - p2.position.y,
            vDelta = p1.velocity.y - p2.velocity.y,
            aDelta = p1.acceleration.y - p2.acceleration.y
        )
        val timesZ = solveAxisWithQuadratic(
            pDelta = p1.position.z - p2.position.z,
            vDelta = p1.velocity.z - p2.velocity.z,
            aDelta = p1.acceleration.z - p2.acceleration.z
        )
        val candidates = combineAxes(timesX, timesY, timesZ)
        return candidates.minOrNull()
    }

    private fun solveAxisWithQuadratic(pDelta: Int, vDelta: Int, aDelta: Int): Set<Int> {
        if (aDelta == 0) {
            return when {
                vDelta != 0 && pDelta % vDelta == 0 -> setOf(-pDelta / vDelta)
                vDelta == 0 && pDelta == 0 -> setOf(Int.MIN_VALUE) // always aligned
                else -> setOf()
            }
        }

        val quadraticSolution = solveQuadraticEquation(
            a = aDelta.toDouble(),
            b = (2 * vDelta + aDelta).toDouble(),
            c = (2 * pDelta).toDouble()
        )

        return quadraticSolution
            .filter { it >= 0 && abs(it - it.toInt()) < EPSILON }
            .map { it.toInt() }
            .toSet()
    }

    private fun combineAxes(x: Set<Int>, y: Set<Int>, z: Set<Int>): Set<Int> {
        val a1 = if (Int.MIN_VALUE in x) null else x
        val a2 = if (Int.MIN_VALUE in y) null else y
        val a3 = if (Int.MIN_VALUE in z) null else z

        return when {
            a1 != null && a2 != null && a3 != null -> a1 intersect a2 intersect a3
            a1 != null && a2 != null -> a1 intersect a2
            a1 != null && a3 != null -> a1 intersect a3
            a2 != null && a3 != null -> a2 intersect a3
            a1 != null -> a1
            a2 != null -> a2
            a3 != null -> a3
            else -> emptySet() // all axes are always aligned = infinite collision times
        }
    }

    private fun processInput(input: List<String>): List<Particle> {
        val regex = Regex("<(-?\\d+),(-?\\d+),(-?\\d+)>")
        return input.mapIndexed { index, line ->
            val (p, v, a) = regex.findAll(line)
                .map { match -> match.groupValues.drop(1).map { it.toInt() } }
                .toList()
            Particle(
                id = index,
                position = Point3D(p[0], p[1], p[2]),
                velocity = Point3D(v[0], v[1], v[2]),
                acceleration = Point3D(a[0], a[1], a[2])
            )
        }
    }
}