package hw3

import chisel3._
import chisel3.util._

case class GameRules(neighsToSpawn: Int, minToSurvive: Int, maxToSurvive: Int)

class GameOfLifeIO(nRows: Int, nCols: Int) extends Bundle {
    val load = Input(Bool())
    val step = Input(Bool())
    val gridIn = Input(Vec(nRows, Vec(nCols, Bool())))
    val gridOut = Output(Vec(nRows, Vec(nCols, Bool())))
}

class GameOfLife(val nRows: Int, val nCols: Int, val rules: GameRules) extends Module {
    val io = IO(new GameOfLifeIO(nRows, nCols))

    // instantiate grid of registers & connect to output
    val cells = Seq.fill(nRows,nCols)(RegInit(false.B))
    for (r <- 0 until nRows) {
        for (c <- 0 until nCols) {
            io.gridOut(r)(c) := cells(r)(c)
        }
    }

    // YOUR CODE HERE
    def countNeighbors(r: Int, c: Int): UInt = {
        val neighbors = Seq(
            (r-1, c-1), (r-1, c), (r-1, c+1),
            (r,   c-1),           (r,   c+1),
            (r+1, c-1), (r+1, c), (r+1, c+1)
        )
        val validNeighbors = neighbors.filter { case (nr, nc) =>
            nr >= 0 && nr < nRows && nc >= 0 && nc < nCols
        }
        val count = PopCount(validNeighbors.map { case (nr, nc) =>
            cells(nr)(nc)
        })
        count
    }

    def printGrid(): Unit = {
        printf("chisel Grid:\n")
        for (r <- 0 until nRows) {
            for (c <- 0 until nCols) {
                printf("%c", Mux(io.gridOut(r)(c), '*'.U, '-'.U))
            }
            printf("\n")
        }
    }

    when(io.load) {
        for (r <- 0 until nRows) {
            for (c <- 0 until nCols) {
                cells(r)(c) := io.gridIn(r)(c)
            }
        }
    }

    when(io.step) {
        for (r <- 0 until nRows) {
            for (c <- 0 until nCols) {
                val count = countNeighbors(r, c)
                val alive = cells(r)(c)
                cells(r)(c) := Mux(alive, count >= rules.minToSurvive.U && count <= rules.maxToSurvive.U, count === rules.neighsToSpawn.U)
            }
        }
        // printGrid()
    }
}
