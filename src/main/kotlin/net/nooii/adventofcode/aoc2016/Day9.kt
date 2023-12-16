package net.nooii.adventofcode.aoc2016

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader

class Day9 {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2016).loadStrings("Day9Input").first()
            part1(input)
            part2(input)
        }

        private fun part1(input: String) {
            println(decompress(input).length)
        }

        private fun part2(input: String) {
            // Assuming the decompression will always yield other decompression(s) + literal
            // Will not work for arbitrary input!
            println(countDecompress(input))
        }

        private fun countDecompress(input: CharSequence): Long {
            val original = StringBuilder(input)
            val regex = Regex("^(.*?)(\\((\\d+)x(\\d+)\\))")
            var count = 0L
            while (true) {
                val (prefix, compression, howMany, multiplier) = regex.find(original)?.groupValues?.drop(1) ?: break
                if (prefix.isNotEmpty()) {
                    count += prefix.length
                }
                original.delete(0, prefix.length + compression.length)
                count += multiplier.toLong() * countDecompress(original.take(howMany.toInt()))
                original.delete(0, howMany.toInt())
            }
            count += original.length
            return count
        }

        private fun decompress(input: CharSequence): String {
            val original = StringBuilder(input)
            val decoded = StringBuilder()
            val regex = Regex("^(.*?)(\\((\\d+)x(\\d+)\\))")
            while (true) {
                val (prefix, compression, howMany, multiplier) = regex.find(original)?.groupValues?.drop(1) ?: break
                if (prefix.isNotEmpty()) {
                    decoded.append(prefix)
                }
                original.delete(0, prefix.length + compression.length)
                decoded.append(original.take(howMany.toInt()).repeat(multiplier.toInt()))
                original.delete(0, howMany.toInt())
            }
            decoded.append(original)
            return decoded.toString()
        }
    }
}