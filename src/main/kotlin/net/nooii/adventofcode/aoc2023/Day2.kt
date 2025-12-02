package net.nooii.adventofcode.aoc2023

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader
import net.nooii.adventofcode.helpers.captureFirstMatch

object Day2 {

    private class Game(
        val id: Int,
        val sets: List<GameSet>
    )

    private class GameSet {
        var red: Int = 0
        var green: Int = 0
        var blue: Int = 0
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val input = InputLoader(AoCYear.AOC_2023).loadStrings("Day2Input")
        val games = parseGames(input)
        part1(games)
        part2(games)
    }

    private fun part1(games: List<Game>) {
        val solution = games
            .filter { game ->
                game.sets.all { it.red <= 12 && it.green <= 13 && it.blue <= 14 }
            }
            .sumOf { it.id }
        println(solution)
    }

    private fun part2(games: List<Game>) {
        val solution = games.sumOf { game ->
            with(game.sets) {
                maxOf { it.red } * maxOf { it.green } * maxOf { it.blue }
            }
        }
        println(solution)
    }

    private fun parseGames(input: List<String>): List<Game> {
        return input.map { parseGame(it) }
    }

    private fun parseGame(line: String): Game {
        val id = Regex("Game (\\d+):").captureFirstMatch(line) { it.toInt() }.first()
        val sets = line.substringAfter(": ").split("; ").map { gameSetString ->
            GameSet().apply {
                for (match in Regex("(\\d+) (red|green|blue)").findAll(gameSetString)) {
                    val (rawGemCount, type) = match.groupValues.drop(1)
                    val gemCount = rawGemCount.toInt()
                    when (type) {
                        "red" -> red = gemCount
                        "green" -> green = gemCount
                        "blue" -> blue = gemCount
                    }
                }
            }
        }
        return Game(id, sets)
    }
}