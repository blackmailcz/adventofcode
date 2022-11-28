package net.nooii.adventofcode.aoc2021

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader
import java.awt.Point

/**
 * Created by Nooii on 04.12.2021
 */
class Day4 {

    private class Bingo(
        val numbers: List<Int>,
        val boards: List<BingoBoard>
    )

    private class BingoBoard(
        val id: Int,
        val numbers: Board.Numbers,
        val marks: Board.Marks
    ) {

        fun mark(number: Int) {
            val point = numbers.find(number)
            if (point != null) {
                marks.data[point.y][point.x] = true
            }
        }

        fun isWinning(): Boolean {
            // Check by row
            for (row in marks.data) {
                if (!row.contains(false)) {
                    return true
                }
            }
            // Check by column
            for (r in 0 until 5) {
                var isWinningColumn = true
                for (c in 0 until 5) {
                    if (!marks.data[r][c]) {
                        isWinningColumn = false
                    }
                }
                if (isWinningColumn) {
                    return true
                }
            }
            return false
        }

        fun getNonWinningSum(): Int {
            var sum = 0
            for (r in 0 until 5) {
                for (c in 0 until 5) {
                    if (!marks.data[r][c]) {
                        sum += numbers.data[r][c]
                    }
                }
            }
            return sum
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is BingoBoard) return false

            if (id != other.id) return false

            return true
        }

        override fun hashCode(): Int {
            return id
        }

    }

    private sealed class Board<I : Any> {

        val data: MutableList<MutableList<I>> = mutableListOf()

        abstract val initializer: I

        init {
            for (row in 0 until 5) {
                data.add(mutableListOf())
                for (column in 0 until 5) {
                    @Suppress("LeakingThis")
                    data[row].add(initializer)
                }
            }
        }

        class Numbers : Board<Int>() {
            override val initializer = 0

            fun find(number: Int): Point? {
                for ((rowIndex, row) in data.withIndex()) {
                    val index = row.indexOf(number)
                    if (index != -1) {
                        return Point(index, rowIndex)
                    }
                }
                return null
            }
        }

        class Marks : Board<Boolean>() {
            override val initializer = false
        }

        override fun toString(): String {
            val out = StringBuilder()
            for (r in 0 until 5) {
                for (c in 0 until 5) {
                    out.append(data[r][c].toString().padStart(2, ' '))
                    out.append(" ")
                }
                out.append("\n")
            }
            return out.toString()
        }

    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2021).loadStrings("Day4Input")
            val bingo = processBingoBoards(input)
            part1(bingo)
            part2(bingo)
        }

        private fun part1(bingo: Bingo) {
            var winningBoard: BingoBoard? = null
            var winningNumber: Int? = null
            outer@
            for (number in bingo.numbers) {
                for (board in bingo.boards) {
                    board.mark(number)
                    if (board.isWinning()) {
                        winningBoard = board
                        winningNumber = number
                        break@outer
                    }
                }
            }
            if (winningNumber != null && winningBoard != null) {
                println(winningBoard.getNonWinningSum() * winningNumber)
            }
        }

        private fun part2(bingo: Bingo) {
            val wonBoards = mutableListOf<Int>()
            var lastWinningNumber: Int? = null
            var lastWinningBoard: BingoBoard? = null
            for (number in bingo.numbers) {
                for (board in bingo.boards) {
                    if (board.id in wonBoards) {
                        continue
                    }
                    board.mark(number)
                    if (board.isWinning()) {
                        wonBoards.add(board.id)
                        lastWinningBoard = board
                        lastWinningNumber = number
                    }
                }
            }
            if (lastWinningBoard != null && lastWinningNumber != null) {
                println(lastWinningBoard.getNonWinningSum() * lastWinningNumber)
            } else {
                println("Multiple remaining boards")
            }
        }


        private fun processBingoBoards(input: List<String>): Bingo {
            val numbers = mutableListOf<Int>()
            var boardId = 0
            var currentBoardStart = -1
            var currentBoard = Board.Numbers()
            val boards = mutableListOf<BingoBoard>()
            for ((i, line) in input.withIndex()) {
                when {
                    i == 0 -> numbers.addAll(line.split(",").map { it.toInt() })
                    line.isBlank() -> continue
                    else -> {
                        if (currentBoardStart == -1) {
                            currentBoardStart = i
                        }
                        val r = i - currentBoardStart
                        val row = line.split("\\s".toRegex()).filter { it.isNotBlank() }
                        for (c in 0 until 5) {
                            currentBoard.data[r][c] = row[c].toInt()
                        }
                        if (r == 4) {
                            currentBoardStart = -1
                            boards.add(BingoBoard(boardId, currentBoard, Board.Marks()))
                            currentBoard = Board.Numbers()
                            boardId++
                        }
                    }
                }
            }
            return Bingo(numbers, boards)
        }

    }

}