package net.nooii.adventofcode.aoc2015

import net.nooii.adventofcode.helpers.*

object Day20 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = InputLoader(AoCYear.AOC_2015).loadInts("Day20Input").first().toLong()
        // Runtime ~ 3 seconds
        part1(input)
        // Runtime ~ 352 seconds
        part2(input)
    }

    private fun computeDivisorSum(primeCache: List<Long>, input: Long): Long {
        val factors = input.primeFactors(primeCache)
        var result = 1L
        for ((prime, power) in factors) {
            result *= Math.floorDiv(prime.pow(power + 1) - 1, prime - 1)
        }
        return result
    }

    private fun part1(input: Long) {
        val primeCache = SieveOfAtkin.sieve(input.toInt()).map { it.toLong() }
        var i = 0L
        var sum = 0L
        while (sum < input) {
            i++
            sum = computeDivisorSum(primeCache, i) * 10
        }
        println(i)
    }

    private fun part2(input: Long) {
        var i = 0L
        var sum = 0L
        while (sum < input) {
            i++
            sum = i.factors().filter { it * 50 >= i }.sum() * 11
        }
        println(i)
    }
}