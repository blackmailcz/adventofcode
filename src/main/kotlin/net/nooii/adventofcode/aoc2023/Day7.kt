package net.nooii.adventofcode.aoc2023

import net.nooii.adventofcode.aoc2023.Day7.HandType.*
import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader
import net.nooii.adventofcode.helpers.add

class Day7 {

    private data class Hand(
        val hand: String,
        val type: HandType,
        val bet: Int,
        private val jokerIndices: Set<Int> = emptySet()
    ) : Comparable<Hand> {

        companion object {

            private val symbolPriority = listOf(
                '2', '3', '4', '5', '6', '7', '8', '9', 'T', 'J', 'Q', 'K', 'A'
            )
        }

        override fun compareTo(other: Hand): Int {
            val typeComparison = type.compareTo(other.type)
            if (typeComparison != 0) {
                return typeComparison
            } else {
                for (c in hand.indices) {
                    val thisPriority = if (c in jokerIndices) -1 else symbolPriority.indexOf(hand[c])
                    val otherPriority = if (c in other.jokerIndices) -1 else symbolPriority.indexOf(other.hand[c])
                    val charComparison = thisPriority.compareTo(otherPriority)
                    if (charComparison != 0) {
                        return charComparison
                    }
                }
                return 0
            }
        }
    }

    private enum class HandType {
        HIGH_CARD,
        ONE_PAIR,
        TWO_PAIRS,
        THREE_OF_A_KIND,
        FULL_HOUSE,
        FOUR_OF_A_KIND,
        FIVE_OF_A_KIND
    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2023).loadStrings("Day7Input")
            val hands = processInput(input)
            part1(hands)
            part2(hands)
        }

        private fun part1(hands: List<Hand>) {
            computeScore(hands.sorted())
        }

        private fun part2(hands: List<Hand>) {
            val newHands = hands.map { hand ->
                val jokerIndices = hand.hand.indices.filter { hand.hand[it] == 'J' }.toSet()
                // Find the symbol with the highest occurrence and add n to it, where n is the number of jokers
                val charMap = createCharMap(hand.hand).toMutableMap()
                charMap.remove('J')
                charMap.add(charMap.maxByOrNull { it.value }?.key ?: 'A', jokerIndices.size)
                val type = computeHandType(charMap)
                Hand(
                    hand.hand,
                    type,
                    hand.bet,
                    jokerIndices
                )
            }
            computeScore(newHands.sorted())
        }

        private fun computeScore(hands: List<Hand>) {
            var sum = 0
            for ((i, hand) in hands.withIndex()) {
                sum += hand.bet * (i + 1)
            }
            println(sum)
        }

        private fun createCharMap(hand: String): Map<Char, Int> {
            val map = mutableMapOf<Char, Int>()
            for (symbol in hand) {
                map.add(symbol, 1)
            }
            return map
        }

        private fun computeHandType(hand: String): HandType {
            return computeHandType(createCharMap(hand))
        }

        private fun computeHandType(charMap: Map<Char, Int>): HandType {
            return with(charMap.values) {
                when {
                    size == 1 -> FIVE_OF_A_KIND
                    any { it == 4 } -> FOUR_OF_A_KIND
                    size == 2 && any { it == 3 } -> FULL_HOUSE
                    any { it == 3 } -> THREE_OF_A_KIND
                    size == 3 && any { it == 2 } -> TWO_PAIRS
                    any { it == 2 } -> ONE_PAIR
                    else -> HIGH_CARD
                }
            }
        }

        private fun processInput(input: List<String>): List<Hand> {
            return input.map {
                val (hand, bet) = it.split(" ")
                Hand(hand, computeHandType(hand), bet.toInt())
            }
        }
    }
}