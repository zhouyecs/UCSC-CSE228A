package hw1

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class MajorityCircuitTester extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "MajorityCircuit"
  it should "correctly pass tests on all 8 possible inputs" in {
    test(new MajorityCircuit) { dut =>
      // FILL IN HERE
			for(i <- 0 to 1) {
        for(j <- 0 to 1) {
          for(k <- 0 to 1) {
            dut.io.a.poke(i.B)
            dut.io.b.poke(j.B)
            dut.io.c.poke(k.B)
            dut.io.out.expect(((i & j) | (i & k) | (j & k)).B)
          }
        }
      }
    }
  }
}
