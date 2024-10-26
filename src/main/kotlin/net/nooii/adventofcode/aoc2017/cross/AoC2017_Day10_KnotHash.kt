package net.nooii.adventofcode.aoc2017.cross

@Suppress("ClassName")
class AoC2017_Day10_KnotHash {

    @OptIn(ExperimentalStdlibApi::class)
    fun hash(input: String): String {
        val lengths = processInput(input) + listOf(17, 31, 73, 47, 23)
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
        return data
            .windowed(16, 16)
            .joinToString("") { it.xor().toHexString().takeLast(2) }
    }

    private fun List<Int>.xor() = reduce { acc, item -> acc xor item }

    private fun processInput(input: String): List<Int> {
        return input.map { it.code }
    }

    companion object {

        fun reverseWrapped(list: List<Int>, start: Int, count: Int): List<Int> {
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
    }
}