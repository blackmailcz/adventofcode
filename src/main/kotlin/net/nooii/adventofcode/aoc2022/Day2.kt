package net.nooii.adventofcode.aoc2022

import net.nooii.adventofcode.aoc2022.Day2.RPS.*
import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader
import net.nooii.adventofcode.helpers.splitToPair

class Day2 {

    private enum class RPS(val score: Int) {
        ROCK(1),
        PAPER(2),
        SCISSORS(3);

        fun strongAgainst(): RPS {
            return when (this) {
                ROCK -> SCISSORS
                PAPER -> ROCK
                SCISSORS -> PAPER
            }
        }

        fun weakAgainst(): RPS {
            return when (this) {
                ROCK -> PAPER
                PAPER -> SCISSORS
                SCISSORS -> ROCK
            }
        }
    }

    private class Round(
        val opponent: RPS,
        val you: RPS
    ) {

        fun computeScore(): Int {
            val matchScore = when {
                you == opponent -> 3
                you.strongAgainst() == opponent -> 6
                else -> 0
            }
            return matchScore + you.score
        }
    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2022).loadStrings("Day2Input")
            part1(input)
            part2(input)
        }

        private fun part1(input: List<String>) {
            val rounds = input.map { line ->
                val (opponentSymbol, yourSymbol) = line.splitToPair(" ")
                val opponent = parseOpponent(opponentSymbol)
                val you = when (yourSymbol) {
                    "X" -> ROCK
                    "Y" -> PAPER
                    "Z" -> SCISSORS
                    else -> error("Unsupported $yourSymbol")
                }
                Round(opponent, you)
            }
            printTotalScore(rounds)
        }

        private fun part2(input: List<String>) {
            val rounds = input.map { line ->
                val (opponentSymbol, yourSymbol) = line.splitToPair(" ")
                val opponent = parseOpponent(opponentSymbol)
                val you = when (yourSymbol) {
                    "X" -> opponent.strongAgainst()
                    "Y" -> opponent
                    "Z" -> opponent.weakAgainst()
                    else -> error("Unsupported $yourSymbol")
                }
                Round(opponent, you)
            }
            printTotalScore(rounds)
        }

        private fun parseOpponent(part: String): RPS {
            return when (part) {
                "A" -> ROCK
                "B" -> PAPER
                "C" -> SCISSORS
                else -> error("Unsupported $part")
            }
        }

        private fun printTotalScore(rounds: List<Round>) {
            println(rounds.sumOf { it.computeScore() })
        }
    }
}