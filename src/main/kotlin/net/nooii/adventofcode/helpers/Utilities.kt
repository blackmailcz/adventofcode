package net.nooii.adventofcode.helpers

import java.math.BigDecimal
import java.math.RoundingMode
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
 * Converts hexadecimal number in [String] form to a binary number in [String].
 */
fun hexToBin(hex: String): String {
    return hex
        .map { it.toString().toInt(16).toString(2).padStart(4, '0') }
        .joinToString("")
}

/**
 * Converts binary number in [String] form to a decimal number [BigDecimal].
 */
fun Long.toBinString(length: Int = 64): String {
    return toString(2).padStart(length, '0')
}

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
 * Produces product of all items in the list.
 */
fun Collection<Int>.product() = reduce { acc, i -> acc * i }

/**
 * Produces product of all items in the list.
 */
fun Collection<Long>.product() = reduce { acc, i -> acc * i }

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
 * Computes intersection of two ranges. Ranges must overlap [overlaps]. More effective than [IntRange.intersect].
 */
fun IntRange.fastIntersect(range: IntRange): IntRange {
    if (!this.overlaps(range)) {
        throw IllegalArgumentException("Ranges do not overlap")
    }
    return IntRange(max(first, range.first), min(last, range.last))
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
 * Computes a hole in the range delimited by the other range. More effective than [LongRange.subtract].
 */
fun LongRange.fastCut(range: LongRange): List<LongRange> {
    return when {
        !this.overlaps(range) -> listOf(this)
        this.fastIntersect(range) == this -> emptyList()
        range.first <= first -> listOf(LongRange(range.last + 1, this.last))
        range.last >= last -> listOf(LongRange(this.first, range.first - 1))
        else -> listOf(LongRange(this.first, range.first - 1), LongRange(range.last + 1, this.last))
    }
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
fun gcd(x: Long, y: Long): Long {
    return if (y == 0L) x else gcd(y, x % y)
}

/**
 * Finds the Greatest Common Divisor of collection of [numbers].
 */
fun gcd(numbers: Collection<Long>): Long {
    return numbers.toSet().fold(0) { x, y -> gcd(x, y) }
}

/**
 * Finds the Greatest Common Divisor of collection of [numbers].
 */
fun gcd(vararg numbers: Long): Long = gcd(numbers.toSet())

/**
 * Finds the Least Common Multiple of collection of [numbers].
 */
fun lcm(numbers: Collection<Long>): Long {
    return numbers.toSet().fold(1) { x, y -> x * (y / gcd(x, y)) }
}

/**
 * Finds the Least Common Multiple of collection of [numbers].
 */
fun lcm(vararg numbers: Long): Long = lcm(numbers.toSet())

/**
 * Breakdown [Int] into its factors (all factors, not just primes).
 */
fun Int.factors(): List<Int> {
    return ((1..this / 2).asSequence().filter { this % it == 0 } + this).toList()
}

/**
 * Breakdown [Long] into its factors (all factors, not just primes).
 */
fun Long.factors(): List<Long> {
    return ((1..this / 2).asSequence().filter { this % it == 0L } + this).toList()
}

/**
 * Breakdown [Long] into its prime factors.
 */
fun Long.primeFactors(primeCache: List<Long>): Map<Long, Int> {
    val primeFactors = mutableMapOf<Long, Int>()
    var number = this
    while (number > 1) {
        for (prime in primeCache) {
            if (number % prime == 0L) {
                primeFactors[prime] = (primeFactors[prime] ?: 0) + 1
                number /= prime
                break
            }
        }
    }
    return primeFactors
}

/**
 * Raises this value to the integer power [exponent].
 */
infix fun Int.pow(exponent: Int) = this.toDouble().pow(exponent).toInt()

/**
 * Raises this value to the integer power [exponent].
 */
infix fun Long.pow(exponent: Int) = this.toDouble().pow(exponent).toLong()

/**
 * Round [BigDecimal] to nearest whole number.
 */
fun BigDecimal.round(): BigDecimal = setScale(0, RoundingMode.HALF_UP)

/**
 * Rotates a string by a given number of positions.
 */
fun String.rotate(by: Int): String {
    val charArray = toCharArray()
    val rotated = arrayOfNulls<Char>(length)
    for ((index, char) in charArray.withIndex()) {
        rotated[(index + by).mod(length)] = char
    }
    return rotated.joinToString("")
}

/**
 * Runs a [callback] over each point. Faster than [forEachPoint] for large ranges.
 */
fun forEachPoint(xRange: IntRange, yRange: IntRange, callback: (point: Point) -> Unit) {
    for (y in yRange) {
        for (x in xRange) {
            callback(Point(x, y))
        }
    }
}

/**
 * Runs [forEachPoint] over a [PointMap].
 */
fun <T : Any> forEachPoint(pointMap: PointMap<T>, callback: (point: Point, value: T) -> Unit) {
    forEachPoint(0 until pointMap.width, 0 until pointMap.height) { point ->
        callback.invoke(point, pointMap[point])
    }
}

/**
 * Return a list of points within the given range.
 */
fun pointRange(xRange: IntRange, yRange: IntRange, filter: (Point) -> Boolean = { true }): Set<Point> {
    val points = mutableSetOf<Point>()
    for (y in yRange) {
        for (x in xRange) {
            val point = Point(x, y)
            if (filter.invoke(point)) {
                points.add(point)
            }
        }
    }
    return points
}

/**
 * Repeats this list [times] times and returns the result as a new list.
 */
fun <T> List<T>.repeat(times: Int): List<T> {
    val output = mutableListOf<T>()
    repeat(times) {
        output.addAll(this)
    }
    return output
}

/**
 * Splits the string into two halves.
 * @see [halve]
 */
fun String.halve() = halve { it }

/**
 * Splits the string into two halves, rounded down. [transform] the value in process of splitting.
 * For size <= 1 it produces the original string.
 */
fun <T> String.halve(transform: (String) -> T): List<T> {
    return if (length <= 1) {
        listOf(transform(this))
    } else {
        listOf(transform(take(length / 2)), transform(drop(length / 2)))
    }
}