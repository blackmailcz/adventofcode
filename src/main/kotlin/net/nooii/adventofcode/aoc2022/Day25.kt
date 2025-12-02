package net.nooii.adventofcode.aoc2022

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader

object Day25 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = InputLoader(AoCYear.AOC_2022).loadStrings("Day25Input")
        part1(input)
    }

    private fun part1(input: List<String>) {
        val sumDecimal = input.sumOf { snafuToDecimal(it) }
        val sumSnafu = decimalToSnafu(sumDecimal)
        println(sumSnafu)
    }

    private fun snafuToDecimal(number: String): Long {
        var dec = 0L
        var order = 1L
        for (i in number.reversed()) {
            dec += order * getSnafuMultiplier(i)
            order *= 5
        }
        return dec
    }

    private fun getSnafuMultiplier(char: Char): Int {
        return when (char) {
            '2' -> 2
            '1' -> 1
            '0' -> 0
            '-' -> -1
            '=' -> -2
            else -> error("Unknown")
        }
    }

    private fun decimalToSnafu(number: Long): String {
        var snafu = ""
        var n = number
        while (n > 0) {
            n += 2
            snafu = toSnafuSymbol(n % 5) + snafu
            n /= 5
        }
        return snafu
    }

    private fun toSnafuSymbol(digit: Long): Char {
        return when (digit) {
            4L -> '2'
            3L -> '1'
            2L -> '0'
            1L -> '-'
            0L -> '='
            else -> error("Unknown")
        }
    }
}