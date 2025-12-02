package net.nooii.adventofcode.aoc2022

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader
import net.nooii.adventofcode.helpers.product

object Day11 {

    private class Monkey(
        val items: MutableList<Long>,
        val operation: (Long) -> Long,
        val nextMonkeyId: (Long) -> Int,
        val divisibleBy: Int
    )

    @JvmStatic
    fun main(args: Array<String>) {
        val input = InputLoader(AoCYear.AOC_2022).loadStrings("Day11Input")
        solution(parseInput(input), 3, 20)
        solution(parseInput(input), 1, 10_000)
    }

    private fun solution(monkeys: List<Monkey>, divider: Int, rounds: Int) {
        val inspections = MutableList(monkeys.size) { 0L }
        // The correct way would be to use LCM, but for the given input, all dividers are primes already,
        // so their product is equal to their LCM.
        val lcm = monkeys.map { it.divisibleBy }.product()
        repeat(rounds) {
            for ((monkeyId, monkey) in monkeys.withIndex()) {
                inspections[monkeyId] += monkey.items.size.toLong()
                turn(monkeys, monkey, divider, lcm)
            }
        }
        val output = inspections.sortedDescending().take(2).product()
        println(output)
    }

    private fun turn(monkeys: List<Monkey>, monkey: Monkey, divider: Int, lcm: Int) {
        for (item in monkey.items) {
            val newItem = monkey.operation.invoke(item % lcm) / divider
            val nextMonkeyId = monkey.nextMonkeyId.invoke(newItem)
            monkeys[nextMonkeyId].items.add(newItem)
        }
        monkey.items.clear()
    }

    private fun parseInput(input: List<String>): List<Monkey> {
        return input.plus("").windowed(7, 7).map { lines ->
            val items = lines[1].trimStart().drop("Starting items: ".length).split(", ").map { it.toLong() }.toMutableList()
            val operation = lines[2].trimStart().drop("Operation: new = old ".length).split(" ")
                .let { (op, opValue) ->
                    { it: Long ->
                        val value = if (opValue == "old") it else opValue.toLong()
                        when (op) {
                            "+" -> it + value
                            "*" -> it * value
                            else -> error("Unsupported operation")
                        }
                    }
                }
            val divisibleBy = lines[3].trimStart().drop("Test: divisible by ".length).toInt()
            val positiveMonkeyId = lines[4].trimStart().drop("If true: throw to monkey ".length).toInt()
            val negativeMonkeyId = lines[5].trimStart().drop("If false: throw to monkey ".length).toInt()
            val nextMonkeyId = { it: Long -> if (it % divisibleBy == 0L) positiveMonkeyId else negativeMonkeyId }
            Monkey(items, operation, nextMonkeyId, divisibleBy)
        }
    }

}