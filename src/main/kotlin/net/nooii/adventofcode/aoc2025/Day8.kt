package net.nooii.adventofcode.aoc2025

import com.github.shiguruikai.combinatoricskt.combinations
import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader
import net.nooii.adventofcode.helpers.Point3D
import net.nooii.adventofcode.helpers.product
import java.util.*

object Day8 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = InputLoader(AoCYear.AOC_2025).loadStrings("Day8Input")
        val points = processInput(input)
        part1(points)
        part2(points)
    }

    private fun part1(points: Set<Point3D>) {
        val distances = computeDistances(points)
        val circuits = mutableListOf<MutableSet<Point3D>>()
        for (pair in distances.values.take(1000)) {
            connectPair(pair, circuits)
        }
        val threeLongest = circuits.sortedByDescending { it.size }.take(3)
        // Because we compute product, a size of 1 does not matter, so we don't need to fill the list with single pair(s)
        val product = threeLongest.map { it.size.toLong() }.product()
        println(product)
    }

    private fun part2(points: Set<Point3D>) {
        val distances = computeDistances(points)
        val circuits = mutableListOf<MutableSet<Point3D>>()
        for (pair in distances.values) {
            connectPair(pair, circuits)
            if (circuits.size == 1 && circuits.first().size == points.size) {
                val product = pair.let { it.first.x.toLong() * it.second.x.toLong() }
                println(product)
                return
            }
        }
        println("No solution found")
    }

    private fun computeDistances(points: Set<Point3D>): TreeMap<Double, Pair<Point3D, Point3D>> {
        val distances = TreeMap<Double, Pair<Point3D, Point3D>>()
        for ((p1, p2) in points.combinations(2)) {
            val distance = p1.euclideanDistance(p2)
            if (distances.contains(distance)) {
                error("Solution ambiguity - multiple points have the same distance: $distance")
            }
            distances[distance] = Pair(p1, p2)
        }
        return distances
    }

    private fun connectPair(pair: Pair<Point3D, Point3D>, circuits: MutableList<MutableSet<Point3D>>) {
        val (p1, p2) = pair
        val matchingCircuits = circuits.filter { it.contains(p1) || it.contains(p2) }
        if (matchingCircuits.isEmpty()) {
            circuits.add(mutableSetOf(p1, p2))
        } else {
            if (matchingCircuits.size > 1) {
                // Merge circuits
                for (circuit in matchingCircuits.drop(1)) {
                    matchingCircuits.first().addAll(circuit)
                    circuits.remove(circuit)
                }
            }
            matchingCircuits.first().add(p1)
            matchingCircuits.first().add(p2)
        }
    }

    private fun processInput(input: List<String>): Set<Point3D> {
        return input.map { line ->
            val (x, y, z) = line.split(",").map { it.toInt() }
            Point3D(x, y, z)
        }.toSet()
    }
}