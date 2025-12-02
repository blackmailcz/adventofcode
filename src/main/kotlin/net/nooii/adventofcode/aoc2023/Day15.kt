package net.nooii.adventofcode.aoc2023

import net.nooii.adventofcode.helpers.*

object Day15 {

    private sealed class Lens(
        val label: String,
        val sourceString: String,
    ) {

        val boxId by lazy { hash(label) }

        class Dash(label: String, fullString: String) : Lens(label, fullString)
        class Equals(label: String, fullString: String, val focalLength: Int) : Lens(label, fullString)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val input = InputLoader(AoCYear.AOC_2023).loadStrings("Day15Input")
        val lenses = processInput(input)
        part1(lenses)
        part2(lenses)
    }

    private fun part1(lenses: List<Lens>) {
        val sum = lenses.sumOf { hash(it.sourceString) }
        println(sum)
    }

    private fun part2(lenses: List<Lens>) {
        val boxes = IntRange(0, 255).associateWith { mutableListOf<Lens.Equals>() }.nn()
        for (lens in lenses) {
            when (lens) {
                is Lens.Dash -> boxes[lens.boxId].removeIf { it.label == lens.label }
                is Lens.Equals -> {
                    val existingIndex = boxes[lens.boxId].indexOfFirst { it.label == lens.label }
                    if (existingIndex != -1) {
                        boxes[lens.boxId][existingIndex] = lens
                    } else {
                        boxes[lens.boxId].add(lens)
                    }
                }
            }
        }
        var sum = 0L
        for ((boxId, boxLenses) in boxes) {
            for ((i, lens) in boxLenses.withIndex()) {
                sum += (boxId + 1) * (i + 1) * lens.focalLength
            }
        }
        println(sum)
    }

    private fun hash(string: String): Int {
        return string.fold(0) { acc, char -> (acc + char.code) * 17 % 256 }
    }

    private fun processInput(input: List<String>): List<Lens> {
        val regex = Regex("([a-z]+)([-=])(\\d?)")
        return input.first().split(",").map { line ->
            val (label, operator, focalStrength) = regex.captureFirstMatch(line)
            when (operator) {
                "=" -> Lens.Equals(label, line, focalStrength.toInt())
                "-" -> Lens.Dash(label, line)
                else -> error("Invalid operator: $operator")
            }
        }
    }
}