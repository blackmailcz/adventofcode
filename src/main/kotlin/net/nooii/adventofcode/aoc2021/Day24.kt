package net.nooii.adventofcode.aoc2021

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader
import java.util.*
import kotlin.math.max
import kotlin.math.min

/**
 * Created by Nooii on 25.12.2021
 */
class Day24 {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2021).loadStrings("Day24Input")
            val pairs = parsePairs(input)
            solution(pairs, false)
            solution(pairs, true)
        }

        private fun solution(pairs: List<Pair<Int, Int>>, minimum: Boolean) {
            // Copied this idea. This problem was too hard for me to crack.
            // Today was not about programming or finding algorithm... It was
            // just about finding some magical logic in text. So I did not mind cheating.
            val stack = Stack<Pair<Int, Int>>()
            val links = mutableMapOf<Int, Pair<Int, Int>>()
            for ((i, pair) in pairs.withIndex()) {
                val (a, b) = pair
                if (a > 0) {
                    stack.push(Pair(i, b))
                } else {
                    val (j, bj) = stack.pop()
                    links[i] = Pair(j, bj + a)
                }
            }
            val r = arrayOfNulls<Int>(14)
            for ((i, pair) in links) {
                val (j, delta) = pair
                r[i] = if (minimum) max(1, 1 + delta) else min(9, 9 + delta)
                r[j] = if (minimum) max(1, 1 - delta) else min(9, 9 - delta)
            }
            println(r.joinToString(""))
        }

        private fun parsePairs(input: List<String>): MutableList<Pair<Int, Int>> {
            val pairs = mutableListOf<Pair<Int, Int>>()
            for (i in 0 until 14) {
                pairs.add(
                    Pair(
                        input[i * 18 + 5].drop(6).toInt(),
                        input[i * 18 + 15].drop(6).toInt()
                    )
                )
            }
            return pairs
        }

    }

}