package net.nooii.adventofcode.helpers

/**
 * A mutable implementation of [NNMap] that provides non-null value guarantees.
 *
 * This class wraps a mutable map and ensures that all operations that retrieve values
 * will return non-null results. It throws an exception if a key is not found instead
 * of returning null.
 *
 * @param K The type of keys maintained by this map
 * @param V The type of mapped values
 * @property underlying The underlying mutable map that this class wraps
 */
open class MutableNNMap<K : Any, V : Any>(
    override val underlying: MutableMap<K, V> = HashMap() // HashMap by default
) : NNMap<K, V>(underlying), MutableMap<K, V> by underlying {

    /**
     * Returns the value associated with the specified key, or throws an exception if the key is not present.
     *
     * @param key The key whose associated value is to be returned
     * @return The value associated with the specified key
     * @throws NoSuchElementException if the key is not present in the map
     */
    override fun get(key: K): V = underlying[key]!!

    /**
     * Checks whether the map contains the specified key.
     *
     * @param key The key whose presence in this map is to be tested
     * @return `true` if the map contains a mapping for the specified key, `false` otherwise
     */
    override operator fun contains(key: K): Boolean = underlying.containsKey(key)

    /**
     * Checks whether the map contains the specified key.
     *
     * @param key The key whose presence in this map is to be tested
     * @return `true` if the map contains a mapping for the specified key, `false` otherwise
     */
    override fun containsKey(key: K) = contains(key)

    /**
     * Checks whether the map contains the specified value.
     *
     * @param value The value whose presence in this map is to be tested
     * @return `true` if the map maps one or more keys to the specified value, `false` otherwise
     */
    override fun containsValue(value: V): Boolean {
        return underlying.containsValue(value)
    }

    /**
     * Returns a mutable set view of the mappings contained in this map.
     *
     * @return A mutable set view of the mappings contained in this map
     */
    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() = underlying.entries

    /**
     * Checks whether the map is empty.
     *
     * @return `true` if the map contains no key-value mappings, `false` otherwise
     */
    override fun isEmpty(): Boolean {
        return underlying.isEmpty()
    }

    /**
     * Returns a mutable set view of the keys contained in this map.
     *
     * @return A mutable set view of the keys contained in this map
     */
    override val keys: MutableSet<K>
        get() = underlying.keys

    /**
     * Returns the number of key-value mappings in this map.
     *
     * @return The number of key-value mappings in this map
     */
    override val size: Int
        get() = underlying.size

    /**
     * Returns a mutable collection view of the values contained in this map.
     *
     * @return A mutable collection view of the values contained in this map
     */
    override val values: MutableCollection<V>
        get() = underlying.values

    /**
     * Returns a string representation of the map.
     *
     * @return A string representation of the map
     */
    override fun toString(): String {
        return underlying.toString()
    }
}

/**
 * Converts a standard MutableMap to a MutableNNMap (Non-Null Map).
 */
fun <K : Any, V : Any> MutableMap<K, V>.nn(): MutableNNMap<K, V> = MutableNNMap(this)

/**
 * Creates an empty mutable non-null map.
 */
fun <K: Any, V: Any> mutableNNMapOf(): MutableNNMap<K, V> = mutableMapOf<K, V>().nn()

/**
 * Creates a mutable non-null map with the specified initial entries.
 */
fun <K: Any, V: Any> mutableNNMapOf(vararg pairs: Pair<K, V>): MutableNNMap<K, V> = mutableMapOf(*pairs).nn()

/**
 * A non-null map implementation that wraps a standard Map and provides non-null value guarantees.
 *
 * This class ensures that all operations that retrieve values will return non-null results.
 * It throws an exception if a key is not found instead of returning null.
 *
 * @param K The type of keys maintained by this map
 * @param V The type of mapped values
 * @property underlying The underlying map that this class wraps
 */
open class NNMap<K : Any, V : Any>(
    protected open val underlying: Map<K, V> = HashMap() // HashMap by default
) : Map<K, V> by underlying {

    /**
     * Returns the value associated with the specified key, or throws an exception if the key is not present.
     *
     * @param key The key whose associated value is to be returned
     * @return The non-null value associated with the specified key
     * @throws NoSuchElementException if the key is not present in the map
     */
    override fun get(key: K): V = underlying[key]!!

    /**
     * Returns the value for the given key, or the specified default value if the key is not present in the map.
     *
     * @param key The key whose associated value is to be returned
     * @param defaultValue The default value to return if the key is not found in the map
     * @return The value associated with the key if present, otherwise the default value
     */
    override fun getOrDefault(key: K, defaultValue: V): V {
        return if (key in this) get(key) else defaultValue
    }

    /**
     * Checks whether the map contains the specified key.
     *
     * @param key The key whose presence in this map is to be tested
     * @return `true` if the map contains a mapping for the specified key, `false` otherwise
     */
    open operator fun contains(key: K): Boolean = underlying.containsKey(key)

    /**
     * Checks whether the map contains the specified key.
     *
     * @param key The key whose presence in this map is to be tested
     * @return `true` if the map contains a mapping for the specified key, `false` otherwise
     */
    override fun containsKey(key: K) = contains(key)

    /**
     * Returns a string representation of the map.
     *
     * @return A string representation of the map
     */
    override fun toString(): String {
        return underlying.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is NNMap<*, *>) return false

        if (underlying != other.underlying) return false

        return true
    }

    override fun hashCode(): Int {
        return underlying.hashCode()
    }
}

/**
 * Converts a standard Map to a NNMap (Non-Null Map).
 */
fun <K : Any, V : Any> Map<K, V>.nn() = NNMap(this)

/**
 * Creates an empty non-null map.
 */
fun <K: Any, V: Any> nnMapOf(): NNMap<K, V> = mapOf<K, V>().nn()

/**
 * Creates a non-null map with the specified initial entries.
 */
fun <K: Any, V: Any> nnMapOf(vararg pairs: Pair<K, V>): NNMap<K, V> = mapOf(*pairs).nn()