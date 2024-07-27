package hw2

import chisel3._


/**
  * 
  * @param coefs : in ascending exponent order -> Seq(1, 2, 3) == 1 + 2x + 3x^2
  * @param width : the width of x
  */
class PolyEval(coefs: Seq[Int], width: Int) extends Module {
  val io = IO(new Bundle {
    val x      = Input(UInt(width.W))
    val out    = Output(UInt())
  })

  def power(base: UInt, exp: Int): UInt = {
    require(exp >= 0)
    if (exp == 0) {
      1.U
    } else {
      var res = base
      for (_ <- 1 until exp) {
        res = res * base
      }
      res
    }
  }

  // The generated hardware should produce the result combinatorally (within a cycle).
  val out = coefs.zipWithIndex.map { case (coef, i) =>
    coef.U * power(io.x, i)
  }.reduce(_ + _)

  io.out := out
}
