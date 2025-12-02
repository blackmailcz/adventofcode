package net.nooii.adventofcode.aoc2016

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader
import java.util.*

object Day19 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = InputLoader(AoCYear.AOC_2016).loadInts("Day19Input").first()
        part1(input)
        part2(input)
    }

    private fun part1(input: Int) {
        val elves = TreeSet(IntRange(1, input).toSet())
        var currentElf = 1
        while (elves.size > 1) {
            val victim = elves.higher(currentElf) ?: elves.higher(0) ?: break
            elves.remove(victim)
            currentElf = elves.higher(currentElf) ?: elves.higher(0) ?: break
        }
        println(currentElf)
    }

    private fun part2(input: Int) {
        // Brute force cycle, 30 minutes run time
        val elves = IntRange(1, input).toMutableList()
        var currentElfIndex = 0
        while (elves.size > 1) {
            val oppositeElfIndex = (currentElfIndex + elves.size / 2) % elves.size
            elves.removeAt(oppositeElfIndex)
            currentElfIndex =
                (if (oppositeElfIndex < currentElfIndex) currentElfIndex else (currentElfIndex + 1)) % elves.size
        }
        println(elves.first())
    }
}