package net.nooii.adventofcode.helpers

data class Point3D(
    val x: Int,
    val y: Int,
    val z: Int
) {

    fun getAdjacentSidePoints() = setOf(
        Point3D(x - 1, y, z),
        Point3D(x + 1, y, z),
        Point3D(x, y - 1, z),
        Point3D(x, y + 1, z),
        Point3D(x, y, z - 1),
        Point3D(x, y, z + 1)
    )

    override fun toString() = "[$x,$y,$z]"
}