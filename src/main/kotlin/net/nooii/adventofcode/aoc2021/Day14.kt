package net.nooii.adventofcode.aoc2021

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader
import net.nooii.adventofcode.helpers.NonNullMap
import net.nooii.adventofcode.helpers.add

/**
 * Created by Nooii on 14.12.2021
 */
class Day14 {

    private class PolymerInput(
        val template: String,
        val rules: NonNullMap<String, Char>
    )

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2021).loadStrings("Day14Input")
            val polymerInput = processInput(input)
            solution(polymerInput, 10)
            solution(polymerInput, 40)
        }

        private fun getDefaultRuleMap(template: String): MutableMap<String, Long> {
            val ruleMap = mutableMapOf<String, Long>()
            var previousChar: Char? = null
            for (char in template) {
                if (previousChar != null) {
                    ruleMap.add("$previousChar$char", 1)
                }
                previousChar = char
            }
            return ruleMap
        }

        private fun getDefaultCharMap(template: String): MutableMap<Char, Long> {
            val charMap = mutableMapOf<Char, Long>()
            for (char in template) {
                charMap.add(char, 1)
            }
            return charMap
        }

        private fun solution(polymerInput: PolymerInput, n: Int) {
            var ruleCountMap = getDefaultRuleMap(polymerInput.template)
            val charMap = getDefaultCharMap(polymerInput.template)
            repeat(n) {
                val newRuleCountMap = mutableMapOf<String, Long>()
                for ((rule, count) in ruleCountMap) {
                    val insertedChar = polymerInput.rules[rule]
                    charMap.add(insertedChar, count)
                    newRuleCountMap.add("${rule[0]}$insertedChar", count)
                    newRuleCountMap.add("$insertedChar${rule[1]}", count)
                }
                ruleCountMap = newRuleCountMap
            }
            println(charMap.maxOf { it.value } - charMap.minOf { it.value })
        }

        private fun processInput(input: List<String>): PolymerInput {
            // Assuming there exists a replication for each pair of letters to get prettier code
            val template = input.first()
            val rules = input.drop(2).associate { line ->
                val parts = line.split(" -> ")
                parts[0] to parts[1][0]
            }
            return PolymerInput(template, NonNullMap(rules.toMutableMap()))
        }
    }

}