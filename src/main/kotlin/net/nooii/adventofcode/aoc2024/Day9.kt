package net.nooii.adventofcode.aoc2024

import net.nooii.adventofcode.aoc2024.Day9.Data.File
import net.nooii.adventofcode.aoc2024.Day9.Data.Space
import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader
import net.nooii.adventofcode.helpers.repeat
import net.nooii.adventofcode.helpers.size

object Day9 {

    private sealed interface Data {

        val range: IntRange

        data class File(val id: Int, override val range: IntRange) : Data
        data class Space(override val range: IntRange) : Data
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val input = InputLoader(AoCYear.AOC_2024).loadStrings("Day9Input").first()
        part1(input)
        // Runtime ~ 1 second
        part2(input)
    }

    private fun part1(input: String) {
        // Create list of original file segments (expanded)
        val files = input.windowed(1, 2).map { it.toInt() }.mapIndexed { index, digit ->
            listOf(index).repeat(digit)
        }
        // Create "bank" of all file parts to be distributed into spaces
        val bank = files.flatten().reversed()
        // Map the spaces to the data from the bank
        var bankIndex = 0
        val filledSpaces = input.drop(1).windowed(1, 2).map { it.toInt() }.map {
            bank.subList(bankIndex, bankIndex + it).apply {
                bankIndex += it
            }
        }
        // Combine the data together into output, alternate source between files and filled spaces
        val output = buildList {
            for (i in input.indices) {
                if (i % 2 == 0) {
                    add(files[i / 2])
                } else {
                    add(filledSpaces[i / 2])
                }
            }
        }
        // Compute checksum
        var sum = 0L
        for ((index, value) in output.flatten().dropLast(filledSpaces.flatten().size).withIndex()) {
            sum += index * value
        }
        println(sum)
    }

    private fun part2(input: String) {
        // Convert the system into ranges
        var data = createData(input)
        // Try to move each file once
        for (i in (input.length / 2) downTo 0) {
            data = tryMove(data, i)
        }
        // Compute checksum
        val sum = data.filterIsInstance<File>().sumOf {
            it.range.sum().toLong() * it.id
        }
        println(sum)
    }

    private fun createData(input: String): List<Data> {
        var from = 0
        return input.mapIndexedNotNull { index, char ->
            // If the digit is 0, skip it, there will be no range for it
            val digit = char.digitToInt().takeIf { it > 0 }
                ?: return@mapIndexedNotNull null
            val range = from until from + digit
            from += digit
            if (index % 2 == 0) {
                File(index / 2, range)
            } else {
                Space(range)
            }
        }
    }

    private fun tryMove(data: List<Data>, fileId: Int): List<Data> {
        // Find the file to be moved
        val fileIndex = data.indexOfFirst { it is File && it.id == fileId } // We'll need the index later
        val file = data[fileIndex] as File
        // Find the first space before the file where it can fit
        val space = data.find {
            it is Space && it.range.last <= file.range.last && it.range.size() >= file.range.size()
        }
        return if (space != null) {
            data.toMutableList().apply {
                // Move the file
                remove(file)
                add(file.copy(range = space.range.first until space.range.first + file.range.size()))
                // Check the original position surroundings for spaces and merge them into one
                val leftSpace = data.getOrNull(fileIndex - 1)?.takeIf { it is Space }?.also { remove(it) }
                val rightSpace = data.getOrNull(fileIndex + 1)?.takeIf { it is Space }?.also { remove(it) }
                val mergedSpaceStart = listOfNotNull(leftSpace?.range?.first, file.range.first, rightSpace?.range?.first).min()
                val mergedSpaceEnd = listOfNotNull(leftSpace?.range?.last, file.range.last, rightSpace?.range?.last).max()
                add(Space(mergedSpaceStart..mergedSpaceEnd))
                // Slice the space around new file position if needed
                remove(space)
                if (space.range.size() > file.range.size()) {
                    add(Space(space.range.first + file.range.size()..space.range.last))
                }
                // Keep the list sorted
                sortBy { it.range.first }
            }
        } else {
            data
        }
    }
}