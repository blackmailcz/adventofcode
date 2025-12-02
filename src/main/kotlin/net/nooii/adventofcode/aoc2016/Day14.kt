package net.nooii.adventofcode.aoc2016

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.CryptoTool
import net.nooii.adventofcode.helpers.InputLoader

object Day14 {

    @JvmStatic
    fun main(args: Array<String>) {
        val salt = InputLoader(AoCYear.AOC_2016).loadStrings("Day14Input").first()
        part1(salt)
        // Runtime ~ 22 seconds
        part2(salt)
    }

    private fun part1(salt: String) {
        solution(salt, stretch = false)
    }

    private fun part2(salt: String) {
        solution(salt, stretch = true)
    }

    private fun solution(salt: String, stretch: Boolean) {
        val threeConsecutive = Regex("([0-9a-fA-F])\\1\\1")
        val cache = mutableListOf<String>()
        var found = 0
        var hashIndex = 0
        outer@
        while (true) {
            val hash = getOrGenerate(cache, salt, hashIndex, stretch)
            val threes = threeConsecutive.findAll(hash).firstOrNull()?.groupValues?.getOrNull(0)
            if (threes != null) {
                val fives = threes[0].toString().repeat(5)
                for (nextHashIndex in hashIndex + 1..hashIndex + 1000) {
                    val nextHash = getOrGenerate(cache, salt, nextHashIndex, stretch)
                    if (nextHash.contains(fives)) {
                        found++
                        if (found == 64) {
                            break@outer
                        }
                        break
                    }
                }
            }
            hashIndex++
        }
        println(hashIndex)
    }

    private fun getOrGenerate(
        cache: MutableList<String>,
        salt: String,
        hashIndex: Int,
        stretch: Boolean
    ): String {
        val hash = cache.getOrNull(hashIndex)
        return if (hash != null) {
            hash
        } else {
            var newHash = "$salt$hashIndex"
            val repeats = if (stretch) 2017 else 1
            repeat(repeats) {
                newHash = CryptoTool.md5hash(newHash)
            }
            cache.add(newHash)
            newHash
        }
    }
}