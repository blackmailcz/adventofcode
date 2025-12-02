package net.nooii.adventofcode.aoc2016

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader

object Day7 {

    private val BRACKETS_REGEX = Regex("\\[(.*?)]")

    @JvmStatic
    fun main(args: Array<String>) {
        val input = InputLoader(AoCYear.AOC_2016).loadStrings("Day7Input")
        part1(input)
        part2(input)
    }

    private fun part1(input: List<String>) {
        val sum = input.count { line ->
            val inside = BRACKETS_REGEX.findAll(line)
            val outside = BRACKETS_REGEX.split(line)
            inside.none { hasAbba(it.value) } && outside.any { hasAbba(it) }
        }
        println(sum)
    }

    private fun part2(input: List<String>) {
        val sum = input.count { line ->
            val inside = BRACKETS_REGEX.findAll(line)
            val outside = BRACKETS_REGEX.split(line)
            val abas = outside.flatMap { findAbas(it) }
            abas.any { aba -> inside.any { hasBab(it.value, aba) } }
        }
        println(sum)
    }

    private fun findAbas(part: String): List<String> {
        return part.windowed(3, 1).filter { it[0] == it[2] && it[0] != it[1] }
    }

    private fun hasBab(part: String, aba: String): Boolean {
        return part.windowed(3, 1).any { it[0] == aba[1] && it[1] == aba[0] && it[2] == aba[1] }
    }

    private fun hasAbba(part: String): Boolean {
        return part.windowed(4, 1).any { it[0] == it[3] && it[1] == it[2] && it[0] != it[1] }
    }
}