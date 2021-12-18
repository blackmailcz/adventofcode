package net.nooii.adventofcode2021

import net.nooii.adventofcode2021.Day18.Direction.*
import net.nooii.adventofcode2021.helpers.InputLoader
import kotlin.math.ceil
import kotlin.math.floor

/**
 * Created by Nooii on 18.12.2021
 */
class Day18 {

    private sealed class Fish(var level : Int) {

        companion object {
            var nextId = 0L
        }

        var parent : Container? = null
        protected val id : Long = nextId

        init {
            nextId++
        }

        class Regular(level : Int, var value : Int) : Fish(level) {
            override fun toString() = "$value"
        }

        class Container(level : Int, var left : Fish, var right : Fish) : Fish(level) {
            override fun toString() = "[$left,$right]"
        }

        override fun equals(other : Any?) : Boolean {
            if (this === other) return true
            if (other !is Fish) return false

            if (id != other.id) return false

            return true
        }

        override fun hashCode() : Int {
            return id.hashCode()
        }

    }

    private enum class Direction { LEFT, RIGHT }

    companion object {

        @JvmStatic
        fun main(args : Array<String>) {
            val input = InputLoader().loadStrings("Day18Input")
            part1(processInput(input))
            part2(input)
        }

        private fun part1(fishList : List<Fish>) {
            println(magnitude(sum(fishList)))
        }

        private fun part2(input : List<String>) {
            var largestMagnitude = 0
            for (a in input) {
                for (b in input) {
                    if (a != b) {
                        val magnitude = magnitude(reduce(add(parseFish(a), parseFish(b))))
                        if (magnitude > largestMagnitude) {
                            largestMagnitude = magnitude
                        }
                    }
                }
            }
            println(largestMagnitude)
        }

        private fun explode(fish : Fish) : Boolean {
            val explodingFish = findExplodingFish(fish) ?: return false
            val leftValue = (explodingFish.left as Fish.Regular).value
            val rightValue = (explodingFish.right as Fish.Regular).value
            val parent = explodingFish.parent ?: return false // Level 0 can't explode
            val isExplodingFishLeftChild = parent.left == explodingFish
            // Remove exploded fish
            val newFish = Fish.Regular(parent.level + 1, 0)
            newFish.parent = parent
            if (isExplodingFishLeftChild) {
                parent.left = newFish
            } else {
                parent.right = newFish
            }
            propagateExplosion(LEFT, fish, newFish, leftValue)
            propagateExplosion(RIGHT, fish, newFish, rightValue)
            return true
        }

        private fun findExplodingFish(fish : Fish) : Fish.Container? {
            if (fish is Fish.Container) {
                if (fish.level >= 4 && fish.left is Fish.Regular && fish.right is Fish.Regular) {
                    return fish
                }
                findExplodingFish(fish.left)?.let { return it }
                findExplodingFish(fish.right)?.let { return it }
            }
            return null
        }

        private fun propagateExplosion(direction : Direction, rootFish : Fish, targetFish : Fish, value : Int) {
            val infixList = mutableListOf<Fish.Regular>()
            infix(rootFish, infixList)
            val i = infixList.indexOf(targetFish)
            if (i != -1) {
                when (direction) {
                    LEFT -> infixList.getOrNull(i - 1)?.let { it.value += value }
                    RIGHT -> infixList.getOrNull(i + 1)?.let { it.value += value }
                }
            }
        }

        private fun infix(fish : Fish, output : MutableList<Fish.Regular>) {
            when (fish) {
                is Fish.Regular -> output.add(fish)
                is Fish.Container -> {
                    infix(fish.left, output)
                    infix(fish.right, output)
                }
            }
        }

        private fun split(fish : Fish) : Boolean {
            val splittingFish = findSplittingFish(fish) ?: return false
            val parent = splittingFish.parent ?: return false // Splitting fish must have a parent
            val isSplittingFishLeftChild = parent.left == splittingFish
            val leftFish = Fish.Regular(splittingFish.level + 1, floor(splittingFish.value / 2.0).toInt())
            val rightFish = Fish.Regular(splittingFish.level + 1, ceil(splittingFish.value / 2.0).toInt())
            val newFish = Fish.Container(
                level = splittingFish.level,
                left = leftFish,
                right = rightFish
            )
            leftFish.parent = newFish
            rightFish.parent = newFish
            newFish.parent = splittingFish.parent
            if (isSplittingFishLeftChild) {
                parent.left = newFish
            } else {
                parent.right = newFish
            }
            return true
        }

        private fun findSplittingFish(fish : Fish) : Fish.Regular? {
            when {
                fish is Fish.Regular && fish.value > 9 -> return fish
                fish is Fish.Container -> {
                    findSplittingFish(fish.left)?.let { return it }
                    findSplittingFish(fish.right)?.let { return it }
                }
            }
            return null
        }

        private fun add(fish1 : Fish, fish2 : Fish) : Fish.Container {
            increaseLevel(fish1)
            increaseLevel(fish2)
            val fish = Fish.Container(0, fish1, fish2)
            fish1.parent = fish
            fish2.parent = fish
            return fish
        }

        private fun increaseLevel(fish : Fish) {
            fish.level += 1
            if (fish is Fish.Container) {
                increaseLevel(fish.left)
                increaseLevel(fish.right)
            }
        }

        private fun sum(fishList : List<Fish>) : Fish {
            var previous = fishList[0]
            for (fish in fishList.drop(1)) {
                previous = reduce(add(previous, fish))
            }
            return previous
        }

        private fun reduce(fish : Fish) : Fish {
            while (explode(fish) || split(fish)) {
                continue
            }
            return fish
        }

        private fun magnitude(fish : Fish) : Int {
            return when (fish) {
                is Fish.Regular -> fish.value
                is Fish.Container -> 3 * magnitude(fish.left) + 2 * magnitude(fish.right)
            }
        }

        private fun processInput(input : List<String>) : List<Fish> {
            return input.map { line -> parseFish(line) }
        }

        private fun parseFish(input : String) = parseFish(input, 0, 0).second

        private fun parseFish(input : String, initialI : Int, level : Int) : Pair<Int, Fish> {
            var i = initialI + 1 // Skip '['
            val (li, leftFish) = parsePairValue(input, i, level)
            i = li + 1 // Skip ','
            val (ri, rightFish) = parsePairValue(input, i, level)
            i = ri + 1 // Skip ']'
            val fish = Fish.Container(level, leftFish, rightFish)
            leftFish.parent = fish
            rightFish.parent = fish
            return Pair(i, fish)
        }

        private fun parsePairValue(input : String, i : Int, level : Int) : Pair<Int, Fish> {
            return if (input[i] == '[') {
                parseFish(input, i, level + 1)
            } else { // Number
                Pair(i + 1, Fish.Regular(level + 1, input[i].digitToInt()))
            }
        }

    }
}