package net.nooii.adventofcode.aoc2016

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader
import net.nooii.adventofcode.helpers.add
import net.nooii.adventofcode.helpers.captureFirstMatch
import java.util.SortedSet

class Day4 {

    private class Symbol(
        val char: Char,
        val count: Int
    ) : Comparable<Symbol> {
        override fun compareTo(other: Symbol): Int {
            val countComparison = other.count.compareTo(this.count)
            return if (countComparison == 0) {
                this.char.compareTo(other.char)
            } else {
                countComparison
            }
        }

        override fun toString(): String {
            return char.toString()
        }
    }

    private class Room(
        val symbols: String,
        val sortedSymbols: SortedSet<Symbol>,
        val sectorId: Int,
        val checksum: String
    ) {
        fun isReal(): Boolean {
            return sortedSymbols.take(checksum.length).joinToString("") == checksum
        }
    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2016).loadStrings("Day4Input")
            val rooms = processInput(input)
            part1(rooms)
            part2(rooms)
        }

        private fun part1(rooms: List<Room>) {
            val sum = rooms.filter { it.isReal() }.sumOf { it.sectorId }
            println(sum)
        }

        private fun part2(rooms: List<Room>) {
            for (room in rooms) {
                val message = room.symbols.map { decipher(room, it) }.joinToString("")
                if (message == "northpole object storage") {
                    println(room.sectorId)
                    return
                }
            }
            error("No solution found")
        }

        private fun decipher(room: Room, char: Char): Char {
            return if (char == '-') {
                ' '
            } else {
                ((char.code - 'a'.code + room.sectorId) % 26 + 'a'.code).toChar()
            }
        }

        private fun processInput(input: List<String>): List<Room> {
            val regex = Regex("(.*?)-(\\d+)\\[(\\w+)]")
            return input.map { line ->
                val charMap = mutableMapOf<Char, Int>()
                val (symbols, sectorId, checksum) = regex.captureFirstMatch(line)
                for (symbol in symbols) {
                    if (symbol != '-') {
                        charMap.add(symbol, 1)
                    }
                }
                val symbolSet = charMap.map { (char, count) -> Symbol(char, count) }.toSortedSet()
                Room(symbols, symbolSet, sectorId.toInt(), checksum)
            }
        }
    }
}