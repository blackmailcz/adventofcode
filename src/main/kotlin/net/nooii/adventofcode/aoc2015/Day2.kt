package net.nooii.adventofcode.aoc2015

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader
import kotlin.time.times

class Day2 {

    private data class Box(
        val length: Int,
        val width: Int,
        val height: Int
    ) {

        fun computeBoxPaper(): Int {
            return 2 * length * width + 2 * width * height + 2 * height * length
        }

        fun computeExtraPaper(): Int {
            return minOf(length * width, width * height, height * length)
        }

        fun computeBoxRibbon(): Int {
            val (a, b) = listOf(length, width, height).sorted().take(2)
            return 2 * a + 2 * b
        }

        fun computeRibbonBow(): Int {
            return length * width * height
        }
    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2015).loadStrings("Day2Input")
            val boxes = processInput(input)
            part1(boxes)
            part2(boxes)
        }

        private fun part1(boxes: List<Box>) {
            val totalPaper = boxes.sumOf {
                it.computeBoxPaper() + it.computeExtraPaper()
            }
            println(totalPaper)
        }

        private fun part2(boxes: List<Box>) {
            val totalRibbon = boxes.sumOf {
                it.computeBoxRibbon() + it.computeRibbonBow()
            }
            println(totalRibbon)
        }

        private fun processInput(input: List<String>): List<Box> {
            return input.map { parseBox(it) }
        }

        private fun parseBox(line: String): Box {
            val (length, width, height) = line.split("x").map { it.toInt() }
            return Box(length, width, height)
        }
    }
}