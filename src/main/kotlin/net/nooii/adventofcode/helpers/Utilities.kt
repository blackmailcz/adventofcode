package net.nooii.adventofcode.helpers

import java.awt.Point
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

/**
 * Finds all elements presents in all provided sets.
 */
fun <T> multiIntersection(collections: Collection<MutableSet<T>>): Set<T> {
    return collections.reduce { acc, it ->
        acc.apply { retainAll(it) }
    }
}

/**
 * Converts [Boolean] to [Int].
 */
fun Boolean.toInt() = this.compareTo(false)

/**
 * Converts [Boolean] to [Long].
 */
fun Boolean.toLong() = if (this) 1L else 0L

/**
 * Create a shallow copy of a [Map]. Returns mutable map.
 */
fun <K, V> Map<K, V>.copy() = mutableMapOf<K, V>().also { it.putAll(this) }

/**
 * Creates new entry or increments existing [Int] value of map by given increment.
 */
fun <K> MutableMap<K, Int>.add(k: K, inc: Int) {
    this[k] = getOrDefault(k, 0) + inc
}

/**
 * Creates new entry or increments existing [Long] value of map by given increment.
 */
fun <K> MutableMap<K, Long>.add(k: K, inc: Long) {
    this[k] = getOrDefault(k, 0) + inc
}

/**
 * Converts binary number in [String] form to a decimal number [Int].
 */
fun binToDecInt(bin: String) = bin.toInt(2)

/**
 * Converts binary number in [String] form to a decimal number [Long].
 */
fun binToDecLong(bin: String) = bin.toLong(2)

/**
 * Returns all captures from target [String] based on this [Regex] and [transform]s them to a single type.
 */
fun <T> Regex.captureFirstMatch(input: String, transform: (String) -> T): List<T> {
    return findAll(input)
        .first()
        .groupValues
        .drop(1)
        .map { transform.invoke(it) }
}

/**
 * Returns all captures from target [String] based on this [Regex].
 */
fun Regex.captureFirstMatch(input: String): List<String> {
    return captureFirstMatch(input) { it }
}

/**
 * Computes difference between two points represented by a point.
 */
fun Point.diff(other: Point): Point {
    return Point(other.x - x, other.y - y)
}

/**
 * Produces product of all items in the list.
 */
fun Collection<Int>.product() = reduce { acc, i -> acc * i }

/**
 * Produces product of all items in the list.
 */
fun Collection<Long>.product() = reduce { acc, i -> acc * i }

/**
 * Computes Manhattan distance to other [Point].
 */
fun Point.manhattanDistance(other: Point): Int {
    return abs(x - other.x) + abs(y - other.y)
}

/**
 * Compute size of IntRange. More effective than [IntRange.count].
 */
fun IntRange.size() = last - first + 1

/**
 * Compute if two ranges overlap. More effective than [IntRange.intersect].
 */
fun IntRange.overlaps(range: IntRange): Boolean {
    return range.first in this || range.last in this || first in range || last in range
}

/**
 * Computes size of LongRange. More effective than [LongRange.count].
 */
fun LongRange.size(): Long = last - first + 1

/**
 * Compute if two ranges overlap. More effective than [LongRange.intersect].
 */
fun LongRange.overlaps(range: LongRange): Boolean {
    return range.first in this || range.last in this || first in range || last in range
}

/**
 * Computes intersection of two ranges. Ranges must overlap [overlaps]. More effective than [LongRange.intersect].
 */
fun LongRange.fastIntersect(range: LongRange): LongRange {
    if (!this.overlaps(range)) {
        throw IllegalArgumentException("Ranges do not overlap")
    }
    return LongRange(max(first, range.first), min(last, range.last))
}

/**
 * Splits the list into smaller lists, using the elements matching [predicate] as delimiter, removing it in process.
 */
fun <I> List<I>.splitBy(predicate: (I) -> Boolean): List<List<I>> {
    val lists = mutableListOf<List<I>>()
    var lastBreak = -1
    val addSubList = { i: Int ->
        subList(lastBreak + 1, i).takeIf { it.isNotEmpty() }?.let { lists.add(it) }
        lastBreak = i
    }
    for ((i, item) in withIndex()) {
        if (predicate.invoke(item)) {
            addSubList.invoke(i)
        }
    }
    if (lastBreak < size) {
        addSubList(size)
    }
    return lists
}

/**
 * Splits the list of strings by empty lines.
 */
fun List<String>.splitByEmptyLine() = splitBy { it == "" }

/**
 * Finds the Greatest Common Divisor of two numbers.
 */
fun gcd(a: Int, b: Int): Int {
    var aa = a
    var bb = b
    while (aa != bb) {
        if (aa > bb) aa -= bb else bb -= aa
    }
    return aa
}

/**
 * Raises this value to the integer power [exponent].
 */
infix fun Int.pow(exponent: Int) = this.toDouble().pow(exponent).toInt()

/**
 * Raises this value to the integer power [exponent].
 */
infix fun Long.pow(exponent: Int) = this.toDouble().pow(exponent).toLong()