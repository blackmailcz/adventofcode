package net.nooii.adventofcode.helpers

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
 * Splits string into two strings by given delimiter.
 */
fun String.splitToPair(delimiter: String): Pair<String, String> {
    val parts = split(delimiter)
    if (parts.size != 2) {
        throw IllegalArgumentException("Cannot split to pair - ${parts.size} parts detected")
    }
    return Pair(parts[0], parts[1])
}

/**
 * Splits string into three strings by given delimiter.
 */
fun String.splitToTriple(delimiter: String): Triple<String, String, String> {
    val parts = split(delimiter)
    if (parts.size != 3) {
        throw IllegalArgumentException("Cannot split to triple - ${parts.size} parts detected")
    }
    return Triple(parts[0], parts[1], parts[2])
}