package net.nooii.adventofcode.aoc2023

import net.nooii.adventofcode.helpers.*

class Day4 {

    private data class Ticket(
        val id: Int,
        val winning: Set<Int>,
        val bet: Set<Int>
    ) {
        val matchingCount = winning.intersect(bet).size
    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2023).loadStrings("Day4Input")
            val tickets = processInput(input)
            part1(tickets)
            part2(tickets)
        }

        private fun part1(tickets: List<Ticket>) {
            val solution = tickets.sumOf {
                2 pow (it.matchingCount - 1)
            }
            println(solution)
        }

        private fun part2(tickets: List<Ticket>) {
            val ticketMap = tickets.associate { it.id to 1L }.toMutable()
            for (ticket in tickets) {
                for (n in 1..ticket.matchingCount) {
                    if (ticketMap.containsKey(ticket.id + n)) {
                        ticketMap.add(ticket.id + n, ticketMap[ticket.id])
                    }
                }
            }
            println(ticketMap.values.sum())
        }

        private fun processInput(input: List<String>): List<Ticket> {
            return input.map { parseTicket(it) }
        }

        private fun parseTicket(line: String): Ticket {
            val id = Regex("^Card +(\\d+):").captureFirstMatch(line) { it.toInt() }.first()
            val (winning, bet) = line
                .substringAfter(":")
                .split("|")
                .map { numberBlock ->
                    Regex("(\\d+)")
                        .findAll(numberBlock)
                        .map {
                            it.value.toInt()
                        }
                }
            return Ticket(id, winning.toSet(), bet.toSet())
        }
    }
}