package net.nooii.adventofcode.helpers

/**
 * Created by Nooii on 08.12.2021
 */
data class NonNullMap<K : Any, V : Any>(
    private val underlying: MutableMap<K, V> = HashMap() // HashMap by default
) : MutableMap<K, V> by underlying {

    fun copy() = NonNullMap(underlying.toMutableMap())

    override fun get(key: K): V {
        return underlying[key]!!
    }

    override fun toString(): String {
        return underlying.toString()
    }

    companion object {

        fun <K: Any, V: Any> fromMap(map: Map<K, V>) = NonNullMap(map.toMutableMap())
    }
}