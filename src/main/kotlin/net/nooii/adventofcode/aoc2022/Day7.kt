package net.nooii.adventofcode.aoc2022

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader
import net.nooii.adventofcode.helpers.splitToPair

class Day7 {

    private sealed class FSItem(val name: String, val parent: Directory?) {

        val path: String = "${parent?.path?.trimEnd('/') ?: ""}/$name"

        // FS is read only, first the FS is built and then size is evaluated lazily
        abstract val size: Long

        class File(
            name: String,
            parent: Directory?,
            override val size: Long
        ) : FSItem(name, parent)

        class Directory(
            name: String,
            parent: Directory?,
        ) : FSItem(name, parent) {

            private val files = mutableSetOf<File>()
            val subdirectories = mutableSetOf<Directory>()

            // Lazy implementation to speed up processing time. The size is not changed anymore.
            // In r/w FS, we should not use lazy implementation but rather use get()
            override val size: Long by lazy {
                files.sumOf { it.size } + subdirectories.sumOf { it.size }
            }

            fun addChild(child: FSItem) {
                when (child) {
                    is File -> files.add(child)
                    is Directory -> subdirectories.add(child)
                }
            }

            fun getSubdirectory(name: String): Directory? {
                return subdirectories.find { it.name == name }
            }
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is FSItem) return false

            if (path != other.path) return false

            return true
        }

        override fun hashCode(): Int {
            return path.hashCode()
        }
    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2022).loadStrings("Day7Input")
            val root = parseFileSystem(input)
            part1(root)
            part2(root)
        }

        private fun part1(root: FSItem.Directory) {
            val directorySizeLimit = 100_000
            var accumulatedSize = 0L
            var dirs = listOf(root)
            // DFS
            while (dirs.isNotEmpty()) {
                val nextDirs = mutableListOf<FSItem.Directory>()
                for (dir in dirs) {
                    // Note: Files can be counted more than once - we count already processed subdirectories again towards their parents
                    if (dir.size <= directorySizeLimit) {
                        accumulatedSize += dir.size
                    }
                    nextDirs.addAll(dir.subdirectories)
                }
                dirs = nextDirs
            }
            println(accumulatedSize)
        }

        private fun part2(root: FSItem.Directory) {
            val totalSpace = 70_000_000
            val requiredSpace = 30_000_000
            val minimumSpaceToFree = root.size - totalSpace + requiredSpace
            if (minimumSpaceToFree <= 0) {
                println("There is already enough space")
                return
            }
            var targetDirectorySize = root.size // By default, entire file system is marked for deletion
            var dirs = listOf(root)
            // DFS
            while (dirs.isNotEmpty()) {
                val nextDirs = mutableListOf<FSItem.Directory>()
                for (dir in dirs) {
                    // Find the smallest single directory to be deleted
                    if (dir.size >= minimumSpaceToFree) {
                        if (dir.size < targetDirectorySize) {
                            targetDirectorySize = dir.size
                        }
                        // Only process subdirectories of folders big enough
                        nextDirs.addAll(dir.subdirectories)
                    }
                }
                dirs = nextDirs
            }
            println(targetDirectorySize)
        }

        private fun parseFileSystem(input: List<String>): FSItem.Directory {
            val root = FSItem.Directory("", null)
            var currentDirectory = root
            var i = 0
            while (i < input.size) {
                val command = input[i].drop(2)
                when {
                    command == "ls" -> i = parseLS(input, currentDirectory, i)
                    command.startsWith("cd") -> currentDirectory = parseCD(command, currentDirectory, root)
                    else -> throw IllegalStateException("Unknown command - $command")
                }
                i++
            }
            return root
        }

        private fun parseLS(input: List<String>, directory: FSItem.Directory, lsIndex: Int): Int {
            for (i in (lsIndex + 1) until input.size) {
                val line = input[i]
                if (line.startsWith("$")) {
                    return i - 1
                } else {
                    val (size, name) = line.splitToPair(" ")
                    val newItem = if (size == "dir") {
                        FSItem.Directory(name, directory)
                    } else {
                        FSItem.File(name, directory, size.toLong())
                    }
                    directory.addChild(newItem)
                }
            }
            // Reached end of file
            return input.size
        }

        private fun parseCD(
            command: String,
            currentDirectory: FSItem.Directory,
            root: FSItem.Directory
        ): FSItem.Directory {
            return when (val target = command.drop(3)) { // Cut "cd "
                ".." -> {
                    currentDirectory.parent ?: throw IllegalStateException("Cannot navigate up")
                }
                "/" -> root
                else -> {
                    currentDirectory.getSubdirectory(target)
                        ?: throw IllegalStateException("Subdirectory not found - $target")
                }
            }
        }
    }

}