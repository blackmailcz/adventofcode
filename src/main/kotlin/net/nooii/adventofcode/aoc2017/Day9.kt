package net.nooii.adventofcode.aoc2017

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader

class Day9 {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2017).loadStrings("Day9Input").first()
            solution(input)
        }

        private fun solution(input: String) {
            var (score, garbage, level) = listOf(0, 0, 0)
            var insideGarbage = false
            var ignoreNext = false
            for (char in input) {
                when {
                    ignoreNext -> ignoreNext = false
                    !insideGarbage -> {
                        when (char) {
                            '{' -> level++
                            '}' -> score += level-- // First add score, then decrement level
                            '<' -> insideGarbage = true
                        }
                    }
                    else -> {
                        when (char) {
                            '!' -> ignoreNext = true
                            '>' -> insideGarbage = false
                            else -> garbage++
                        }
                    }
                }
            }
            println(score)
            println(garbage)
        }
    }
}