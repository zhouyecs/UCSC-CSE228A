package hw2

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class PolyEvalTester extends AnyFlatSpec with ChiselScalatestTester {
  val width = 4
  def testPolyEvalOut(n: Int): Unit = {
    val coefs = Seq(4, 5, 6)
    test(new PolyEval(coefs, width)) { dut =>
      dut.io.x.poke(n.U)
      dut.io.out.expect((4 + 5 * n + 6 * n * n).U)
    }
  }

  behavior of "PolyEval"
  it should "correctly calculate output for deg(2) poly" in {
    testPolyEvalOut(3)
  }

  it should "correctly calculate output for deg(3) poly" in {
    testPolyEvalOut(4)
  }

  it should "correctly calculate output for deg(4) poly" in {
    testPolyEvalOut(5)
  }

  it should "correctly calculate output for deg(5) poly" in {
    testPolyEvalOut(6)
  }
}
