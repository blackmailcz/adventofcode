package net.nooii.adventofcode.aoc2016

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader

object Day16 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = InputLoader(AoCYear.AOC_2016).loadStrings("Day16Input").first()
        part1(input)
        part2(input)
    }

    private fun part1(input: String) {
        solution(input, 272)
    }

    private fun part2(input: String) {
        solution(input, 35651584)
    }

    private fun solution(input: String, length: Int) {
        // Curve
        var string = input
        while (string.length < length) {
            string = curve(string)
        }
        // Checksum
        var checksum = string.take(length)
        do {
            checksum = checksum.windowed(2, 2).joinToString("") { if (it[0] == it[1]) "1" else "0" }
        } while (checksum.length % 2 == 0)
        println(checksum)
    }

    private fun curve(string: String): String {
        val tail = StringBuilder()
        for (i in string.length - 1 downTo 0) {
            tail.append(if (string[i] == '0') '1' else '0')
        }
        return "${string}0${tail}"
    }
}