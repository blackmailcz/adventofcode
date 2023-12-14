package net.nooii.adventofcode.aoc2015

import net.nooii.adventofcode.helpers.*
import java.lang.StringBuilder

class Day10 {

    private data class Key(val c: Char, val n: Int)

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2015).loadStrings("Day10Input").first()
            part1(input)
            // Runtime ~ 1 sec
            part2(input)
        }

        private fun part1(input: String) {
            solution(input, 40)
        }

        private fun part2(input: String) {
            solution(input, 50)
        }

        private fun solution(input: String, repeats: Int) {
            // Works fast enough for given input and repeats
            val baseMap: NNMap<Key, String> = createBaseMap()
            var str = input
            repeat(repeats) {
                var n = 0
                var last: Char? = null
                val nextStr = StringBuilder() // Using StringBuilder speeds up the process
                for (i in str.indices) {
                    when (last) {
                        null -> {
                            last = str[i]
                            n++
                        }
                        str[i] -> n++
                        else -> {
                            nextStr.append(baseMap[Key(last, n)])
                            last = str[i]
                            n = 1
                        }
                    }
                    if (i == str.length - 1) {
                        nextStr.append(baseMap[Key(last, n)])
                    }
                }
                str = nextStr.toString()
            }
            println(str.length)
        }

        private fun createBaseMap(): NNMap<Key, String> {
            val baseMap = mutableNNMapOf<Key, String>()
            for (char in setOf('1', '2', '3')) {
                for (n in 1..3) {
                    baseMap[Key(char, n)] = "$n$char"
                }
            }
            return baseMap
        }
    }
}