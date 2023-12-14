package net.nooii.adventofcode.helpers

data class MutableNNMap<K : Any, V : Any>(
    override val underlying: MutableMap<K, V> = HashMap() // HashMap by default
) : NNMap<K, V>(underlying), MutableMap<K, V> by underlying {

    override fun get(key: K): V = underlying[key]!!

    override operator fun contains(key: K): Boolean = underlying.containsKey(key)

    override fun containsKey(key: K) = contains(key)
    override fun containsValue(value: V): Boolean {
        return underlying.containsValue(value)
    }

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() = underlying.entries

    override fun isEmpty(): Boolean {
        return underlying.isEmpty()
    }

    override val keys: MutableSet<K>
        get() = underlying.keys

    override val size: Int
        get() = underlying.size

    override val values: MutableCollection<V>
        get() = underlying.values
}

fun <K : Any, V : Any> MutableMap<K, V>.nn() = MutableNNMap(this)

fun <K : Any, V : Any> MutableMap<K, V>.toImmutable() = toMap().nn()

fun <K: Any, V: Any> mutableNNMapOf(): MutableNNMap<K, V> = mutableMapOf<K, V>().nn()

fun <K: Any, V: Any> mutableNNMapOf(pair: Pair<K, V>): MutableNNMap<K, V> = mutableMapOf(pair).nn()

fun <K: Any, V: Any> mutableNNMapOf(vararg pairs: Pair<K, V>): MutableNNMap<K, V> = mutableMapOf(*pairs).nn()

open class NNMap<K : Any, V : Any>(
    protected open val underlying: Map<K, V> = HashMap() // HashMap by default
) : Map<K, V> by underlying {

    override fun get(key: K): V = underlying[key]!!

    open operator fun contains(key: K): Boolean = underlying.containsKey(key)

    override fun containsKey(key: K) = contains(key)
}

fun <K : Any, V : Any> Map<K, V>.nn() = NNMap(this)

fun <K : Any, V : Any> Map<K, V>.toMutable() = toMutableMap().nn()

fun <K: Any, V: Any> nnMapOf(): NNMap<K, V> = mapOf<K, V>().nn()

fun <K: Any, V: Any> nnMapOf(pair: Pair<K, V>): NNMap<K, V> = mapOf(pair).nn()

fun <K: Any, V: Any> nnMapOf(vararg pairs: Pair<K, V>): NNMap<K, V> = mapOf(*pairs).nn()