package net.nooii.adventofcode.aoc2017

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader

class Day10 {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2017).loadStrings("Day10Input")
            part1(processInput1(input))
            part2(processInput2(input) + listOf(17, 31, 73, 47, 23))
        }

        private fun part1(lengths: List<Int>) {
            var position = 0
            var data = IntRange(0, 255).toList()
            for ((skipSize, length) in lengths.withIndex()) {
                data = reverseWrapped(data, position, length)
                position = (position + length + skipSize) % data.size
            }
            println(data[0] * data[1])
        }

        @OptIn(ExperimentalStdlibApi::class)
        private fun part2(lengths: List<Int>) {
            var position = 0
            var data = IntRange(0, 255).toList()
            var skipSize = 0
            repeat(64) {
                for (length in lengths) {
                    data = reverseWrapped(data, position, length)
                    position = (position + length + skipSize) % data.size
                    skipSize++
                }
            }
            val hash = data
                .windowed(16, 16)
                .joinToString("") { it.xor().toHexString().takeLast(2) }
            println(hash)
        }

        private fun List<Int>.xor() = reduce { acc, item -> acc xor item }

        private fun reverseWrapped(list: List<Int>, start: Int, count: Int): List<Int> {
            return if (start + count >= list.size) {
                val wrapIndex = (start + count) % list.size
                val reversed = (list + list).subList(start, start + count).reversed()
                buildList {
                    addAll(reversed.subList(reversed.size - wrapIndex, reversed.size))
                    addAll(list.subList(wrapIndex, start))
                    addAll(reversed.subList(0, reversed.size - wrapIndex))
                }
            } else {
                buildList {
                    addAll(list.subList(0, start))
                    addAll(list.subList(start, start + count).reversed())
                    addAll(list.subList(start + count, list.size))
                }
            }
        }

        private fun processInput1(input: List<String>): List<Int> {
            return input.first().split(',').map { it.toInt() }
        }

        private fun processInput2(input: List<String>): List<Int> {
            return input.first().map { it.code }
        }
    }
}