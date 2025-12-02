package net.nooii.adventofcode.aoc2015

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader

object Day11 {

    private val invalidLetters = "iol"

    @JvmStatic
    fun main(args: Array<String>) {
        val input = InputLoader(AoCYear.AOC_2015).loadStrings("Day11Input").first()
        val p1password = part1(input)
        part2(p1password)
    }

    private fun part1(input: String): String {
        var password = input
        while (!isValidPassword(password)) {
            password = increment(password)
        }
        println(password)
        return password
    }

    private fun part2(input: String) {
        part1(increment(input))
    }

    private fun isValidPassword(password: String): Boolean {
        return hasIncreasingStraight(password) && hasNoInvalidLetter(password) && hasTwoPairs(password)
    }

    private fun hasIncreasingStraight(password: String): Boolean {
        return password
            .windowed(3, 1)
            .any { it[0] == it[1] - 1 && it[1] == it[2] - 1 }
    }

    private fun hasNoInvalidLetter(password: String): Boolean {
        return password.none { it in invalidLetters }
    }

    private fun hasTwoPairs(password: String): Boolean {
        val firstPair = password.windowed(2, 1).find { it[0] == it[1] } ?: return false
        return password.windowed(2, 1).any { it[0] == it[1] && it[0] !in firstPair }
    }

    private fun increment(input: String): String {
        val output = StringBuilder(input)
        for (i in input.length - 1 downTo 0) {
            output.setCharAt(i, incrementChar(input[i]))
            if (input[i] != 'z') {
                break
            }
        }
        return output.toString()
    }

    private fun incrementChar(char: Char): Char {
        return if (char == 'z') 'a' else char.inc()
    }
}