package net.nooii.adventofcode.aoc2015

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader
import java.math.BigInteger
import java.security.MessageDigest

class Day4 {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2015).loadStrings("Day4Input").first()
            part1(input)
            part2(input)
        }

        private fun part1(prefix: String) {
            solution(prefix, "00000")
        }

        private fun part2(prefix: String) {
            solution(prefix, "000000")
        }

        private fun solution(inputPrefix: String, requiredHashPrefix: String) {
            var i = 0
            do {
                i++
                val hash = md5hash("$inputPrefix$i")
            } while (!hash.startsWith(requiredHashPrefix))
            println(i)
        }

        private fun md5hash(string: String): String {
            val md = MessageDigest.getInstance("MD5")
            return BigInteger(1, md.digest(string.toByteArray())).toString(16).padStart(32, '0')
        }
    }
}