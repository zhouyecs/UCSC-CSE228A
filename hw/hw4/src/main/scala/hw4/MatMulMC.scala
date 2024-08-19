package hw4

import chisel3._
import chisel3.util._

object MatMulMCState extends ChiselEnum {
    val idle, loading, multiplying, outputting = Value
}

class MatMulMC(p: MatMulParams) extends Module {
  val io = IO(new Bundle {
    val in = Flipped(Decoupled(new Bundle {
      val aBlock = Vec(p.aElementsPerTransfer, SInt(p.w))
      val bBlock = Vec(p.bElementsPerTransfer, SInt(p.w))
    }))
    val outBlock = Valid(Vec(p.cElementsPerTransfer, SInt(p.w)))
  })

  // State Declaration
  val a = Reg(Vec(p.aRows, Vec(p.aCols, SInt(p.w))))
  val b = Reg(Vec(p.bRows, Vec(p.bCols, SInt(p.w))))
  val c = Reg(Vec(p.cRows, Vec(p.cCols, SInt(p.w))))

  // BEGIN SOLUTION
  val state             = RegInit(MatMulMCState.idle)
  val inReady           = RegInit(true.B)
  val outValid          = RegInit(false.B)
  val outBits           = RegInit(VecInit(Seq.fill(p.cElementsPerTransfer)(0.S(p.w))))

  val cyclesPerTransfer = p.cyclesPerTransfer
  val parallelism       = p.parallelism
  // printf(cf"cyclesPerTransfer: $cyclesPerTransfer\n")
  // printf(cf"parallelism: $parallelism\n")

  io.in.ready       := inReady
  io.outBlock.valid := outValid
  io.outBlock.bits  := outBits

  def loadBlock[T <: Data](block: Vec[Vec[SInt]], data: Vec[T], index: UInt): Unit = {
    // printf(cf"block height: ${block.size}, block width: ${block(0).size}, data length: ${data.size}\n")

    val baseX = index * data.size.U / block(0).size.U
    val baseY = index * data.size.U % block(0).size.U

    for (i <- 0 until data.length) {
      val rowIndex = baseX
      val colIndex = baseY + i.U
      when(rowIndex < block.length.U && colIndex < block(0).length.U) {
        block(rowIndex)(colIndex) := data(i)
      }.otherwise {
        printf("Index out of bounds: rowIndex = %d, colIndex = %d\n", rowIndex, colIndex)
      }
    }
  }

  val blockIndex      = Counter(cyclesPerTransfer)
  val multiplyCounter = Counter(p.m * p.k * p.n / parallelism)
  val outputCounter   = Counter(p.cRows * p.cCols / p.cElementsPerTransfer)

  switch (state) {
    is (MatMulMCState.idle) {
      when(io.in.valid) {
        inReady  := true.B
        outValid := false.B
        state    := MatMulMCState.loading
      }
    }
    is (MatMulMCState.loading) {
      when (blockIndex.value =/= (cyclesPerTransfer).U) {
        loadBlock(a, io.in.bits.aBlock, blockIndex.value)
        loadBlock(b, io.in.bits.bBlock, blockIndex.value)
        blockIndex.inc()
        
        when (blockIndex.value === (cyclesPerTransfer - 1).U) {
          inReady := false.B
          outValid := false.B
          state := MatMulMCState.multiplying
        }
      }
    }
    is (MatMulMCState.multiplying) {
      for (i <- 0 until p.cRows) {
        val aRow = a(i)
        for (j <- 0 until p.cCols by parallelism) {
          for (k <- j until j + parallelism) {
            val sum = RegInit(0.S(p.w))
            sum := aRow.zip(b.map(_(k))).map { case (a, b) => a * b }.reduce(_ + _)
            c(i)(k) := sum
            multiplyCounter.inc()
          }
        }
      }

      when (multiplyCounter.value === (p.m * p.k * p.n / parallelism - 1).U) {
        outValid := true.B
        state := MatMulMCState.outputting
      }
    }
    is (MatMulMCState.outputting) {
      when (outputCounter.value =/= (cyclesPerTransfer).U) {
        val baseX = outputCounter.value * p.cElementsPerTransfer.U / p.cCols.U
        val baseY = outputCounter.value * p.cElementsPerTransfer.U % p.cCols.U
        for (i <- 0 until p.cElementsPerTransfer) {
          val rowIndex = baseX
          val colIndex = baseY + i.U
          when(rowIndex < p.cRows.U && colIndex < p.cCols.U) {
            outBits(i) := c(rowIndex)(colIndex)
          }.otherwise {
            printf("Index out of bounds: rowIndex = %d, colIndex = %d\n", rowIndex, colIndex)
          }
        }
        outputCounter.inc()

        when (outputCounter.value === (p.cRows * p.cCols / p.cElementsPerTransfer - 1).U) {
          inReady := true.B
          outValid := false.B
          state := MatMulMCState.idle
        }
      }

    }
  }
}