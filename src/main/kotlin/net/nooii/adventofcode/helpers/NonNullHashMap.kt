package net.nooii.adventofcode.helpers

/**
 * Created by Nooii on 08.12.2021
 */
data class NonNullHashMap<K : Any, V : Any>(
    private val underlying: MutableMap<K, V> = HashMap()
) : MutableMap<K, V> by underlying {

    fun copy() = NonNullHashMap(underlying.toMutableMap())

    override fun get(key: K): V {
        return underlying[key]!!
    }

    override fun toString(): String {
        return underlying.toString()
    }
}