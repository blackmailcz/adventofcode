package net.nooii.adventofcode.aoc2021

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader
import java.awt.Point

/**
 * Created by Nooii on 20.12.2021
 */
class Day20 {

    private class Image(
        val algorithm: List<Boolean>,
        val litPoints: Set<Point>,
        val x1: Int,
        val y1: Int,
        val x2: Int,
        val y2: Int,
        val outsidePointValue: Boolean,
    )

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2021).loadStrings("Day20Input")
            val image = processInput(input)
            println(batchTranscode(image, 2).litPoints.size)
            println(batchTranscode(image, 50).litPoints.size)
        }

        private fun batchTranscode(initialImage: Image, n: Int): Image {
            var image = initialImage
            repeat(n) {
                image = transcode(image)
            }
            return image
        }

        private fun transcode(image: Image): Image {
            val litPoints = mutableSetOf<Point>()
            for (y in image.y1 - 1..image.y2 + 1) {
                for (x in image.x1 - 1..image.x2 + 1) {
                    val point = Point(x, y)
                    if (transcodePoint(point, image)) {
                        litPoints.add(point)
                    }
                }
            }
            return Image(
                algorithm = image.algorithm,
                litPoints = litPoints,
                x1 = image.x1 - 1,
                y1 = image.y1 - 1,
                x2 = image.x2 + 1,
                y2 = image.x2 + 1,
                outsidePointValue = getNextOutsidePointValue(image)
            )
        }

        private fun transcodePoint(point: Point, image: Image): Boolean {
            val binaryNumber = StringBuilder()
            for (y in point.y - 1 until point.y + 2) {
                for (x in point.x - 1 until point.x + 2) {
                    val newPoint = Point(x, y)
                    val value = if (isOutsidePoint(newPoint, image)) {
                        image.outsidePointValue
                    } else {
                        newPoint in image.litPoints
                    }
                    binaryNumber.append(booleanToDigit(value))
                }
            }
            val index = binToDec(binaryNumber.toString())
            return image.algorithm[index]
        }

        private fun isOutsidePoint(point: Point, image: Image): Boolean {
            return point.x < image.x1 || point.x > image.x2 || point.y < image.y1 || point.y > image.y2
        }

        private fun getNextOutsidePointValue(image: Image): Boolean {
            val binary = booleanToDigit(image.outsidePointValue).repeat(9)
            val index = binToDec(binary)
            return image.algorithm[index]
        }

        private fun processInput(input: List<String>): Image {
            val algorithm = input.first().map { isLitPoint(it) }
            val litPoints = mutableSetOf<Point>()
            val data = input.drop(2)
            val maxX = data.first().length - 1
            val maxY = data.size - 1
            for ((y, line) in data.withIndex()) {
                for ((x, char) in line.withIndex()) {
                    if (isLitPoint(char)) {
                        litPoints.add(Point(x, y))
                    }
                }
            }
            return Image(algorithm, litPoints, 0, 0, maxX, maxY, false)
        }

        private fun binToDec(bin: String) = bin.toInt(2)

        private fun isLitPoint(symbol: Char) = symbol == '#'

        private fun booleanToDigit(boolean: Boolean) = if (boolean) "1" else "0"

    }

}