package net.nooii.adventofcode2021.helpers

/**
 * Created by Nooii on 08.12.2021
 */
class NonNullHashMap<K : Any, V : Any>(private val underlying : HashMap<K, V> = HashMap()) : MutableMap<K, V> by underlying {

    override fun get(key : K) : V {
        return underlying[key]!!
    }

}