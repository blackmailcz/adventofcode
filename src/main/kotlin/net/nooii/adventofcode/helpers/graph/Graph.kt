package net.nooii.adventofcode.helpers.graph

data class Vertex(
    val name: String
) {
    override fun toString(): String {
        return name
    }
}

data class EdgeKey(val vertices: Set<Vertex>) {
    constructor(edge: Edge) : this(edge.vertices)
    constructor(v1: Vertex, v2: Vertex) : this(setOf(v1, v2))
}

class Edge(
    val vertices: Set<Vertex>,
    var weight: Int = 1
) {

    constructor(v1: Vertex, v2: Vertex, weight: Int = 1) : this(setOf(v1, v2), weight)

    val v1 = vertices.first()
    val v2 = vertices.last()

    val name: String = v1.name + " - " + v2.name

    fun getVertex(vertex: Vertex) = getVertex(name)

    fun getVertex(name: String): Vertex {
        return when (name) {
            v1.name -> v1
            v2.name -> v2
            else -> error("Vertex $name not found in this edge")
        }
    }

    fun getOtherVertex(vertex: Vertex): Vertex = getOtherVertex(vertex.name)

    fun getOtherVertex(name: String): Vertex {
        return when (name) {
            v1.name -> v2
            v2.name -> v1
            else -> error("Vertex $name not found in this edge")
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Edge) return false

        if (vertices != other.vertices) return false

        return true
    }

    override fun hashCode(): Int {
        return vertices.hashCode()
    }

    override fun toString(): String {
        return "$name (w: $weight)"
    }
}

class Graph {

    val vertices = mutableMapOf<String, Vertex>()
    val edges = mutableMapOf<EdgeKey, Edge>()

    val vertexToEdgesMap = mutableMapOf<Vertex, MutableSet<Edge>>()

    fun numberOfVertices() = vertices.size

    fun numberOfEdges() = edges.size

    fun getEdges(vertex: Vertex): Set<Edge> {
        return vertexToEdgesMap[vertex] ?: emptySet()
    }

    fun addVertex(vertex: Vertex) {
        vertices.computeIfAbsent(vertex.name) { vertex }
        vertexToEdgesMap.computeIfAbsent(vertex) { mutableSetOf() }
    }

    fun addEdge(edge: Edge) {
        addVertex(edge.v1)
        addVertex(edge.v2)
        vertexToEdgesMap[edge.v1]?.add(edge)
        vertexToEdgesMap[edge.v2]?.add(edge)
        edges[EdgeKey(edge)] = edge
    }

    fun removeVertex(vertex: Vertex) {
        // Remove all connected edges with the vertices
        val edgesToRemove = vertexToEdgesMap[vertex]?.toSet() ?: emptySet()
        for (e in edgesToRemove) {
            removeEdge(e)
        }
        vertexToEdgesMap.remove(vertex)
        vertices.remove(vertex.name)
    }

    fun removeVertex(name: String) = vertices[name]?.let { removeVertex(it) }

    fun removeEdge(edge: Edge) {
        for (v in edge.vertices) {
            vertexToEdgesMap[v]?.remove(edge)
        }
        edges.remove(EdgeKey(edge))
    }

    fun removeEdge(edgeKey: EdgeKey) {
        edges[edgeKey]?.let { removeEdge(it) }
    }

    fun removeEdgesOf(vertex: Vertex) {
        val edgesToRemove = vertexToEdgesMap[vertex]?.toSet() ?: emptySet()
        for (edge in edgesToRemove) {
            removeEdge(edge)
        }
    }
}