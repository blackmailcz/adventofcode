package net.nooii.adventofcode.aoc2015

import com.github.shiguruikai.combinatoricskt.permutationsWithRepetition
import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader
import net.nooii.adventofcode.helpers.captureFirstMatch
import net.nooii.adventofcode.helpers.product

class Day15 {

    private data class Ingredient(
        val name: String,
        val capacity: Int,
        val durability: Int,
        val flavor: Int,
        val texture: Int,
        val calories: Int
    )

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2015).loadStrings("Day15Input")
            val ingredients = processInput(input)
            // Runtime ~ 5 sec to compute all states
            val states = computeStates(ingredients)
            part1(ingredients, states)
            part2(ingredients, states)
        }

        private fun computeStates(ingredients: List<Ingredient>): List<List<Long>> {
            val teaspoons = 100L
            return LongRange(0, teaspoons)
                .permutationsWithRepetition(ingredients.size)
                .filter { it.sum() == teaspoons }
                .toList()
        }

        private fun part1(ingredients: List<Ingredient>, states: List<List<Long>>) {
            val solution = states.maxOf { computeScore(ingredients, it) }
            println(solution)
        }

        private fun part2(ingredients: List<Ingredient>, states: List<List<Long>>) {
            val solution = states
                .filter { computeCalories(ingredients, it) == 500L }
                .maxOf { computeScore(ingredients, it) }
            println(solution)
        }

        private fun computeScore(ingredients: List<Ingredient>, state: List<Long>): Long {
            val output = listOfNotNull(
                ingredients.indices.sumOf { ingredients[it].capacity * state[it] }.takeIf { it > 0 },
                ingredients.indices.sumOf { ingredients[it].durability * state[it] }.takeIf { it > 0 },
                ingredients.indices.sumOf { ingredients[it].flavor * state[it] }.takeIf { it > 0 },
                ingredients.indices.sumOf { ingredients[it].texture * state[it] }.takeIf { it > 0 }
            )
            return output.product()
        }

        private fun computeCalories(ingredients: List<Ingredient>, state: List<Long>): Long {
            return ingredients.indices.sumOf { ingredients[it].calories * state[it] }
        }

        private fun processInput(input: List<String>): List<Ingredient> {
            val regex = Regex("(\\w+): capacity (-?\\d+), durability (-?\\d+), flavor (-?\\d+), texture (-?\\d+), calories (-?\\d+)")
            return input.map { line ->
                // No "component 6" - cannot destruct
                val data = regex.captureFirstMatch(line)
                Ingredient(data[0], data[1].toInt(), data[2].toInt(), data[3].toInt(), data[4].toInt(), data[5].toInt())
            }
        }
    }
}