package net.nooii.adventofcode.aoc2021

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader
import java.util.*

/**
 * Created by Nooii on 10.12.2021
 */
object Day10 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = InputLoader(AoCYear.AOC_2021).loadStrings("Day10Input")
        val opening = listOf('(', '[', '{', '<')
        val closing = listOf(')', ']', '}', '>')
        solution(input, opening, closing)
    }

    private fun solution(input: List<String>, opening: List<Char>, closing: List<Char>) {
        var corruptedSum = 0
        val completionScores = mutableListOf<Long>()
        lines@
        for (line in input) {
            val stack = Stack<Char>()
            for (char in line) {
                when {
                    opening.contains(char) -> stack.push(char)
                    closing.contains(char) -> {
                        if (stack.isEmpty()) {
                            corruptedSum += computeCorruptionPoints(char, closing)
                            continue@lines
                        }
                        val openingChar = stack.pop()
                        if (!matchesBracket(openingChar, char, opening, closing)) {
                            corruptedSum += computeCorruptionPoints(char, closing)
                            continue@lines
                        }
                    }
                }
            }
            if (stack.isNotEmpty()) {
                completionScores.add(computeCompletions(stack, opening))
            }
        }
        completionScores.sort()
        println(corruptedSum)
        println(completionScores[completionScores.size / 2])
    }

    private fun matchesBracket(openingChar: Char, closingChar: Char, opening: List<Char>, closing: List<Char>): Boolean {
        return opening.indices.any { opening[it] == openingChar && closing[it] == closingChar }
    }

    private fun computeCorruptionPoints(closingChar: Char, closing: List<Char>): Int {
        return when (closing.indexOf(closingChar)) {
            0 -> 3
            1 -> 57
            2 -> 1197
            3 -> 25137
            else -> 0
        }
    }

    private fun computeCompletions(stack: Stack<Char>, opening: List<Char>): Long {
        var sum = 0L
        while (stack.isNotEmpty()) {
            sum = sum * 5 + computeCompletionPoints(stack.pop(), opening)
        }
        return sum
    }

    private fun computeCompletionPoints(openingChar: Char, opening: List<Char>): Int {
        return opening.indexOf(openingChar).takeIf { it >= 0 }?.let { it + 1 } ?: 0
    }

}