package net.nooii.adventofcode.aoc2015

import net.nooii.adventofcode.helpers.*

object Day16 {

    private enum class Thing {
        CHILDREN, CATS, SAMOYEDS, POMERANIANS, AKITAS, VIZSLAS, GOLDFISH, TREES, CARS, PERFUMES
    }

    private data class Sue(
        val id: Int,
        val things: Map<Thing, Int>
    )

    @JvmStatic
    fun main(args: Array<String>) {
        val input = InputLoader(AoCYear.AOC_2015).loadStrings("Day16Input")
        val sues = processInput(input)
        val message = InputLoader(AoCYear.AOC_2015).loadStrings("Day16Message")
        val messageThings = message.associate { parseThing(it) }.nn()
        part1(sues, messageThings)
        part2(sues, messageThings)
    }

    private fun part1(sues: List<Sue>, messageThings: NNMap<Thing, Int>) {
        val validSues = sues.filter { sue ->
            for ((thing, count) in sue.things) {
                if (messageThings.containsKey(thing) && count != messageThings[thing]) {
                    return@filter false
                }
            }
            true
        }
        check(validSues.size == 1)
        println(validSues.first().id)
    }

    private fun part2(sues: List<Sue>, messageThings: Map<Thing, Int>) {
        val validSues = sues.filter { sue ->
            for ((thing, count) in sue.things) {
                val messageThing = messageThings[thing] ?: continue
                when (thing) {
                    Thing.CATS, Thing.TREES -> {
                        if (count <= messageThing) return@filter false
                    }
                    Thing.POMERANIANS, Thing.GOLDFISH -> {
                        if (count >= messageThing) return@filter false
                    }
                    else -> {
                        if (count != messageThing) return@filter false
                    }
                }
            }
            true
        }
        check(validSues.size == 1)
        println(validSues.first().id)
    }

    private fun parseThing(input: String): Pair<Thing, Int> {
        val (thing, count) = Regex("(\\w+): (\\d+)").captureFirstMatch(input)
        return Thing.valueOf(thing.uppercase()) to count.toInt()
    }

    private fun processInput(input: List<String>): List<Sue> {
        return input.map { line ->
            val id = Regex("Sue (\\d+): ").captureFirstMatch(line).first().toInt()
            val things = mutableMapOf<Thing, Int>()
            for (part in line.substringAfter(": ").split(", ")) {
                val (thing, count) = parseThing(part)
                things[thing] = count
            }
            Sue(id, things)
        }
    }
}