package hw3

import scala.collection.mutable.ArrayBuffer

class Grid(val nRows: Int, val nCols: Int) {
    val cells = ArrayBuffer.fill(nRows, nCols)(false)

    // YOUR CODE HERE
    def apply(r: Int, c: Int): Boolean = cells(r)(c)
    def update(r: Int, c: Int, v: Boolean): Unit = { cells(r)(c) = v }

    override def toString(): String = {
        val sb = new StringBuilder()
        for (r <- 0 until nRows) {
            for (c <- 0 until nCols) {
                sb += (if (cells(r)(c)) '*' else '-')
            }
            sb += '\n'
        }
        sb.result()
    }
}


object Grid {
    def apply(gridAsString: String) = {
        val split = gridAsString.split('\n')
        val nRows = split.size
        val nCols = split.head.length
        val g = new Grid(nRows, nCols)
        for (r <- 0 until nRows) {
            for (c <- 0 until nCols) {
                g.cells(r)(c) = split(r)(c) == '*'
            }
        }
        g
    }
}


class GameOfLifeSim(initialGrid: Grid, rules: GameRules) {
    var g = initialGrid

    def countNeighbors(r: Int, c: Int): Int = {
        var count = 0
        for (i <- -1 to 1) {
            for (j <- -1 to 1) {
                if (i != 0 || j != 0) {
                    val r2 = r + i
                    val c2 = c + j
                    if (r2 >= 0 && r2 < g.nRows && c2 >= 0 && c2 < g.nCols) {
                        if (g(r2, c2)) {
                            count += 1
                        }
                    }
                }
            }
        }
        count
    }

    def nextGrid(): Grid = {

        // YOUR CODE HERE
        val newGrid = new Grid(g.nRows, g.nCols)
        for (r <- 0 until g.nRows) {
            for (c <- 0 until g.nCols) {
                val n = countNeighbors(r, c)
                newGrid(r, c) = if (g(r, c)) {
                    n >= rules.minToSurvive && n <= rules.maxToSurvive
                } else {
                    n == rules.neighsToSpawn
                }
            }
        }
        newGrid
    }

    def printGrid(): Unit = println("scala Grid:\n" + g)

    def evolve() = { g = nextGrid(); }

    override def toString(): String = g.toString
    val nRows = g.nRows
    val nCols = g.nCols
}
