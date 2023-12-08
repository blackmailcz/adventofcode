package net.nooii.adventofcode.aoc2015

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader

class Day12 {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2015).loadStrings("Day12Input")
            part1(input)
            part2(input)
        }

        private fun part1(input: List<String>) {
            val regex = Regex("(-?\\d+)")
            val sum = input.sumOf { line ->
                regex.findAll(line).sumOf { it.value.toInt() }
            }
            println(sum)
        }

        private fun part2(input: List<String>) {
            val map = parseJson(input)
            println(collect(map))
        }

        private fun collect(input: Any?): Int {
            return when (input) {
                is Map<*, *> -> {
                    if (input.values.any { it == "red" }) {
                        0
                    } else {
                        input.values.sumOf { collect(it) }
                    }
                }

                is List<*> -> input.sumOf { collect(it) }
                is Double -> input.toInt()
                else -> 0
            }
        }

        private fun parseJson(input: List<String>): Map<String, Any?> {
            val mapAdapter = Gson().getAdapter(object : TypeToken<Map<String, Any?>>() {})
            val jsonString = "{ \"key\": ${input.joinToString("")} }"
            return mapAdapter.fromJson(jsonString)
        }
    }
}