package net.nooii.adventofcode.aoc2023

import net.nooii.adventofcode.helpers.*
import java.util.TreeMap
import kotlin.math.max
import kotlin.math.min

object Day22 {

    private data class Brick(
        val from: Point3D,
        val to: Point3D
    ) {

        val xRange = IntRange(min(from.x, to.x), max(from.x, to.x))
        val yRange = IntRange(min(from.y, to.y), max(from.y, to.y))
        val zRange = IntRange(min(from.z, to.z), max(from.z, to.z))

        fun shiftByZ(diff: Int) = copy(
            from = from.copy(z = from.z + diff),
            to = to.copy(z = to.z + diff)
        )

        fun shiftToZ(z: Int) = copy(
            from = from.copy(z = z),
            to = to.copy(z = to.z - (from.z - z))
        )

        fun isOnGround() = from.z == 1

        fun overlaps(other: Brick): Boolean {
            return overlapsXY(other) && zRange.overlaps(other.zRange)
        }

        fun overlapsXY(other: Brick): Boolean {
            return xRange.overlaps(other.xRange) && yRange.overlaps(other.yRange)
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val input = InputLoader(AoCYear.AOC_2023).loadStrings("Day22Input")
        val bricks = processInput(input).sortedBy { it.from.z } // Sorting is important to determine fall order
        val settlement = settle(bricks)
        val settledBricks = settlement.values.flatten().toSet()
        val supportMap = createSupportMap(settledBricks, settlement)
        val soleSupporters = findSoleSupporters(supportMap)
        part1(settledBricks, soleSupporters)
        part2(supportMap, soleSupporters)
    }

    private fun part1(settledBricks: Set<Brick>, soleSupporters: Set<Brick>) {
        println(settledBricks.size - soleSupporters.size)
    }

    private fun part2(supportMap: NNMap<Brick, MutableSet<Brick>>, soleSupporters: Set<Brick>) {
        val sum = soleSupporters.sumOf { disintegrate(it, supportMap.mutableDeepCopy()) }
        println(sum)
    }

    private fun disintegrate(soleSupporter: Brick, copy: MutableNNMap<Brick, MutableSet<Brick>>): Int {
        // Disintegrate over fresh copy, because it is modified in process
        val originalSize = copy.size
        var bricksToRemove = setOf(soleSupporter)
        while (bricksToRemove.isNotEmpty()) {
            val nextBricksToRemove = mutableSetOf<Brick>()
            // Save bricks affected by the disintegration, so we don't iterate entire map again
            val affectedBricks = mutableSetOf<Brick>()
            for (brickToRemove in bricksToRemove) {
                for ((affectedBrick, supportedBy) in copy) {
                    affectedBricks.add(affectedBrick)
                    supportedBy.remove(brickToRemove)
                }
                copy.remove(brickToRemove)
                affectedBricks.remove(brickToRemove) // No need to check it since it is being removed
            }
            // If any affected bricks lost all its supporters, it will fall (unless it is on the ground)
            for (affectedBrick in affectedBricks) {
                if (!affectedBrick.isOnGround() && copy[affectedBrick].isEmpty()) {
                    nextBricksToRemove.add(affectedBrick)
                }
            }
            bricksToRemove = nextBricksToRemove
        }
        // Copy now contains bricks that survived. We must subtract -1 because we only count other fallen bricks
        // and not the disintegrated brick itself
        return originalSize - copy.size - 1
    }

    private fun NNMap<Brick, MutableSet<Brick>>.mutableDeepCopy(): MutableNNMap<Brick, MutableSet<Brick>> {
        val copy = mutableNNMapOf<Brick, MutableSet<Brick>>()
        for ((k, v) in this) {
            copy[k] = v.toMutableSet()
        }
        return copy
    }

    private fun findSoleSupporters(supportMap: NNMap<Brick, MutableSet<Brick>>): Set<Brick> {
        val soleSupporters = mutableSetOf<Brick>()
        for ((_, supportedBy) in supportMap) {
            // If supportedBy is empty, that means the brick is on the ground
            if (supportedBy.size == 1) {
                val supporter = supportedBy.first()
                soleSupporters.add(supporter)
            }
        }
        return soleSupporters
    }

    private fun createSupportMap(
        settledBricks: Set<Brick>,
        settlement: TreeMap<Int, MutableSet<Brick>>
    ): NNMap<Brick, MutableSet<Brick>> {
        val supportedBy = settledBricks.associateWith { mutableSetOf<Brick>() }.toMutableMap().nn()
        for (brick in settledBricks) {
            val bricksAbove = settlement[brick.to.z + 1] ?: emptySet()
            val supportsAbove = bricksAbove.filter { it.overlapsXY(brick) }.toSet()
            for (support in supportsAbove) {
                supportedBy[support].add(brick)
            }
        }
        return supportedBy
    }

    private fun settle(bricks: List<Brick>): TreeMap<Int, MutableSet<Brick>> {
        val settled = TreeMap<Int, MutableSet<Brick>> { zThis, zOther -> zOther.compareTo(zThis) }
        bricksToSettle@
        for (brick in bricks) {
            for ((z, bricksAtZ) in settled) {
                val settleAttempt = brick.shiftToZ(z)
                for (brickAtZ in bricksAtZ) {
                    if (settleAttempt.overlaps(brickAtZ)) {
                        // We cannot settle the brick on this Z level - there is already another brick
                        // So we settle it on top of that brick
                        settled.add(z + 1, settleAttempt.shiftByZ(1))
                        continue@bricksToSettle
                    }
                }
            }
            // Add to ground
            settled.add(1, brick.shiftToZ(1))
        }
        return settled
    }

    private fun TreeMap<Int, MutableSet<Brick>>.add(z: Int, brick: Brick, zRangeCheck: Boolean = true) {
        if (zRangeCheck && brick.zRange.size() > 1) {
            // "Z" positioned brick spans across multiple Z levels
            for (zCoordinate in brick.zRange) {
                this.add(zCoordinate, brick, zRangeCheck = false)
            }
        } else {
            if (this.containsKey(z)) {
                this[z]!!.add(brick)
            } else {
                this[z] = mutableSetOf(brick)
            }
        }
    }

    private fun processInput(input: List<String>): List<Brick> {
        return input.map { line ->
            val (p1, p2) = line.split("~").map { Point3D.fromXYZString(it) }
            // If z1 > z2, swap the points then (we want to have the bricks sorted how they fall)
            if (p1.z > p2.z) {
                Brick(p2, p1)
            } else {
                Brick(p1, p2)
            }
        }
    }
}