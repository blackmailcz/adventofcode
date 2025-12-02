package net.nooii.adventofcode.aoc2023

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader

object Day9 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = InputLoader(AoCYear.AOC_2023).loadStrings("Day9Input")
        val sequences = processInput(input)
        part1(sequences)
        part2(sequences)
    }

    private fun part1(sequences: List<List<Long>>) {
        var sum = 0L
        for (sequence in sequences) {
            val chain = buildSequenceChain(sequence)
            chain.last().add(0)
            for (i in (chain.size - 2) downTo 0) {
                chain[i].add(chain[i].last() + chain[i + 1].last())
            }
            sum += chain[0].last()
        }
        println(sum)
    }

    private fun part2(sequences: List<List<Long>>) {
        var sum = 0L
        for (sequence in sequences) {
            val chain = buildSequenceChain(sequence)
            chain.last().add(0, 0)
            for (i in (chain.size - 2) downTo 0) {
                chain[i].add(0, chain[i].first() - chain[i + 1].first())
            }
            sum += chain[0].first()
        }
        println(sum)
    }

    private fun buildSequenceChain(sequence: List<Long>): List<MutableList<Long>> {
        val chain = mutableListOf(sequence.toMutableList())
        var current = sequence
        while (current.size != 1 && current.sum() != 0L) {
            current = current.windowed(2, 1).map { it[1] - it[0] }
            chain.add(current.toMutableList())
        }
        return chain
    }

    private fun processInput(input: List<String>): List<List<Long>> {
        return input.map { line ->
            line.split(" ").map { it.toLong() }
        }
    }
}