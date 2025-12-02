package net.nooii.adventofcode.aoc2023

import net.nooii.adventofcode.aoc2023.Day7.HandType.*
import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader
import net.nooii.adventofcode.helpers.add

object Day7 {

    private data class Hand(
        val hand: String,
        val type: HandType,
        val bet: Int,
        private val jokerIndices: Set<Int> = emptySet()
    ) : Comparable<Hand> {

        companion object {

            private const val PRIORITIES = "23456789TJQKA"
        }

        override fun compareTo(other: Hand): Int {
            val typeComparison = type.compareTo(other.type)
            if (typeComparison != 0) {
                return typeComparison
            } else {
                for (c in hand.indices) {
                    val thisPriority = if (c in jokerIndices) -1 else PRIORITIES.indexOf(hand[c])
                    val otherPriority = if (c in other.jokerIndices) -1 else PRIORITIES.indexOf(other.hand[c])
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
            // Find jokers and mark their indexes, they will have lower priority in sorting
            val jokerIndices = hand.hand.indices.filter { hand.hand[it] == 'J' }.toSet()
            if (jokerIndices.isNotEmpty()) {
                // Find the symbol with the highest occurrence and add n to it, where n is the number of jokers
                val charMap = createCharMap(hand.hand)
                charMap.remove('J')
                // Default key "A" (the highest possible), in case there are five jokers
                charMap.add(charMap.maxByOrNull { it.value }?.key ?: 'A', jokerIndices.size)
                val type = computeHandType(charMap)
                Hand(hand.hand, type, hand.bet, jokerIndices)
            } else {
                hand
            }
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

    private fun createCharMap(hand: String): MutableMap<Char, Int> {
        val map = mutableMapOf<Char, Int>()
        for (symbol in hand) {
            map.add(symbol, 1)
        }
        return map
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
            Hand(hand, computeHandType(createCharMap(hand)), bet.toInt())
        }
    }
}