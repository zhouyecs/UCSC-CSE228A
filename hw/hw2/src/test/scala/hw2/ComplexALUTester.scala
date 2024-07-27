package hw2

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class ComplexALUTester extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "ComplexALU"
  it should "correctly calculate realOut onlyAdd=true" in {
    test(new ComplexALU(width = 5, onlyAdder = true)) { dut =>
      dut.io.doAdd.get.poke(true.B)
      dut.io.c0.real.poke(1.S)
      dut.io.c0.imag.poke(2.S)
      dut.io.c1.real.poke(3.S)
      dut.io.c1.imag.poke(4.S)
      dut.io.out.get.real.expect(4.S)
    }
  }

  it should "correctly calculate realOut onlyAdd=false" in {
    test(new ComplexALU(width=5, onlyAdder=false)) { dut =>
      dut.io.c0.real.poke(1.S)
      dut.io.c0.imag.poke(2.S)
      dut.io.c1.real.poke(3.S)
      dut.io.c1.imag.poke(4.S)
      dut.io.out.get.real.expect(-2.S)
    }
  }

  it should "correctly calculate imagOut onlyAdd=true" in {
    test(new ComplexALU(width=5, onlyAdder=true)) { dut =>
      dut.io.doAdd.get.poke(true.B)
      dut.io.c0.real.poke(15.S)
      dut.io.c0.imag.poke(-1.S)
      dut.io.c1.real.poke(15.S)
      dut.io.c1.imag.poke(4.S)
      dut.io.out.get.imag.expect(3.S)
    }
  }

  it should "correctly calculate imagOut onlyAdd=false" in {
    test(new ComplexALU(width=5, onlyAdder=false)) { dut =>
      dut.io.c0.real.poke(1.S)
      dut.io.c0.imag.poke(2.S)
      dut.io.c1.real.poke(3.S)
      dut.io.c1.imag.poke(4.S)
      dut.io.out.get.imag.expect(-2.S)
    }
  }
}
