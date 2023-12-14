package net.nooii.adventofcode.aoc2015

import com.github.shiguruikai.combinatoricskt.permutations
import net.nooii.adventofcode.helpers.*
import kotlin.math.max

class Day13 {

    private data class Person(
        val name: String,
    ) {
        val happiness: MutableNNMap<String, Int> = mutableNNMapOf()
    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2015).loadStrings("Day13Input")
            val people = processInput(input)
            part1(people)
            // Runtime ~ 1 sec
            part2(people)
        }

        private fun part1(people: Set<Person>) {
            solution(people)
        }

        private fun part2(people: Set<Person>) {
            val you = Person("You")
            for (person in people) {
                you.happiness[person.name] = 0
                person.happiness[you.name] = 0
            }
            solution(people + you)
        }

        private fun solution(people: Set<Person>) {
            var bestHappiness = 0
            for (arrangement in people.permutations()) {
                var happiness = 0
                for ((i, person) in arrangement.withIndex()) {
                    val leftNeighbor = arrangement[Math.floorMod(i - 1, arrangement.size)].name
                    val rightNeighbor = arrangement[Math.floorMod(i + 1, arrangement.size)].name
                    happiness += person.happiness[leftNeighbor]
                    happiness += person.happiness[rightNeighbor]
                }
                bestHappiness = max(bestHappiness, happiness)
            }
            println(bestHappiness)
        }

        private fun processInput(input: List<String>): Set<Person> {
            val regex = Regex("(\\w+).*(gain|lose) (\\d+).* (\\w+)\\.")
            val people = mutableMapOf<String, Person>()
            for (line in input) {
                val (who, rawSgn, diff, nextTo) = regex.captureFirstMatch(line)
                val person = people[who] ?: Person(who).also { people[who] = it }
                val sign = if (rawSgn == "gain") 1 else -1
                person.happiness[nextTo] = sign * diff.toInt()
            }
            return people.values.toSet()
        }
    }
}