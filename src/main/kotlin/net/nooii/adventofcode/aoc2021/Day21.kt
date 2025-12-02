package net.nooii.adventofcode.aoc2021

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader
import net.nooii.adventofcode.helpers.add
import kotlin.math.max

/**
 * Created by Nooii on 21.12.2021
 */
object Day21 {

    private class Part1 {

        companion object {
            private const val DICE_SIDES = 100
            private const val FIELDS = 10
            private const val WIN_SCORE = 1000
        }

        private class Player(val id: Int, var position: Int, var score: Int = 0) {
            fun isWinner() = score >= WIN_SCORE

            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other !is Player) return false

                if (id != other.id) return false

                return true
            }

            override fun hashCode(): Int {
                return id
            }
        }

        private class Dice {
            private var position = 0
            var rolls = 0
                private set

            fun roll(): Int {
                position = (position - 1) % DICE_SIDES + 2
                rolls++
                return position
            }
        }

        private class Game(val p1: Player, val p2: Player) {
            val dice = Dice()
            private var isP1Turn = true

            fun turn() {
                val rollValue = IntRange(1, 3).sumOf { dice.roll() }
                val player = if (isP1Turn) p1 else p2
                player.position = (player.position + rollValue - 1) % FIELDS + 1
                player.score += player.position
                isP1Turn = !isP1Turn
            }

            fun isOver() = p1.isWinner() || p2.isWinner()

            fun getLoser() = setOf(p1, p2).minBy { it.score }
        }

        fun solution(p1position: Int, p2position: Int) {
            val game = Game(Player(1, p1position), Player(2, p2position))
            while (!game.isOver()) {
                game.turn()
            }
            println(game.getLoser().score * game.dice.rolls)
        }

    }

    private class Part2 {

        companion object {
            private const val WIN_SCORE = 21
            private const val FIELDS = 10
        }

        private data class Player(val position: Int, val score: Int) {
            fun copy() = Player(position, score)
            fun isWinner() = score >= WIN_SCORE
        }

        private data class Universe(val p1: Player, val p2: Player)

        private class WonUniverses {
            var p1: Long = 0L
            var p2: Long = 0L
        }

        fun solution(p1position: Int, p2position: Int) {
            var universes = mutableMapOf<Universe, Long>(
                Universe(Player(p1position, 0), Player(p2position, 0)) to 1,
            )
            val wonUniverses = WonUniverses()
            val weightedRolls = generateWeightedRolls(3, 3)
            var isP1Playing = true
            while (universes.isNotEmpty()) {
                val nextUniverses = mutableMapOf<Universe, Long>()
                for ((universe, universeCount) in universes) {
                    for ((rollValue, rollWeight) in weightedRolls) {
                        val clonedUniverse = cloneUniverse(isP1Playing, universe, rollValue)
                        val clonedUniverseCount = universeCount * rollWeight
                        when {
                            clonedUniverse.p1.isWinner() -> wonUniverses.p1 += clonedUniverseCount
                            clonedUniverse.p2.isWinner() -> wonUniverses.p2 += clonedUniverseCount
                            else -> nextUniverses.add(clonedUniverse, clonedUniverseCount)
                        }
                    }
                }
                universes = nextUniverses
                isP1Playing = !isP1Playing
            }
            println(max(wonUniverses.p1, wonUniverses.p2))
        }

        fun generateWeightedRolls(diceSides: Int, dices: Int): Map<Int, Long> {
            var rollMap = mutableMapOf(0 to 1L) // Start with a single roll (= default state) of sum 0
            repeat(dices) {
                val newRollMap = mutableMapOf<Int, Long>()
                for ((rollSum, weight) in rollMap) {
                    for (rollValue in 1..diceSides) {
                        newRollMap.add(rollSum + rollValue, weight)
                    }
                }
                rollMap = newRollMap
            }
            return rollMap
        }

        private fun cloneUniverse(isP1Playing: Boolean, universe: Universe, roll: Int): Universe {
            return if (isP1Playing) {
                Universe(performRoll(universe.p1, roll), universe.p2.copy())
            } else {
                Universe(universe.p1.copy(), performRoll(universe.p2, roll))
            }
        }

        private fun performRoll(player: Player, roll: Int): Player {
            val nextPosition = (player.position + roll - 1) % FIELDS + 1
            val nextScore = player.score + nextPosition
            return Player(nextPosition, nextScore)
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val input = InputLoader(AoCYear.AOC_2021).loadStrings("Day21Input")
        val (p1position, p2position) = processInput(input)
        Part1().solution(p1position, p2position)
        Part2().solution(p1position, p2position)
    }

    private fun processInput(input: List<String>): Pair<Int, Int> {
        return Pair(parsePosition(input[0]), parsePosition(input[1]))
    }

    private fun parsePosition(line: String) = line.split(":")[1].trim().toInt()

}