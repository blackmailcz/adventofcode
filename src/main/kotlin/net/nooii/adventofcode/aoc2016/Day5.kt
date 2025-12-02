package net.nooii.adventofcode.aoc2016

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.CryptoTool
import net.nooii.adventofcode.helpers.InputLoader

object Day5 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = InputLoader(AoCYear.AOC_2016).loadStrings("Day5Input").first()
        // Runtime ~ 4 seconds
        part1(input)
        // Runtime ~ 17 seconds
        part2(input)
    }

    private fun part1(input: String) {
        val password = StringBuilder()
        var i = 0
        while (password.length < 8) {
            val hash = CryptoTool.md5hash("$input$i")
            if (hash.startsWith("00000")) {
                password.append(hash[5])
            }
            i++
        }
        println(password.toString())
    }

    private fun part2(input: String) {
        val password = "xxxxxxxx".toCharArray()
        var found = 0
        var i = 0
        while (found < 8) {
            val hash = CryptoTool.md5hash("$input$i")
            if (hash.startsWith("00000")) {
                val digit = hash[5].digitToIntOrNull()
                if (digit != null && password.getOrNull(digit) == 'x') {
                    password[digit] = hash[6]
                    found++
                }
            }
            i++
        }
        println(password.joinToString(""))
    }
}