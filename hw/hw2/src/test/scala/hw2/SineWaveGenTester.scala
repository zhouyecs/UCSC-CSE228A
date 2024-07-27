package hw2

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class SineWaveGenTester extends AnyFlatSpec with ChiselScalatestTester {
  def testSineWaveGen(sw: SineWave, stride: Int): Unit = {
    val period: Int = sw.period
    val amplitude: Int = sw.amplitude
    test(new SineWaveGen(sw)) { dut =>
      dut.io.stride.poke(stride.U)

      for(i <- 0 until period) {
        for(j <- 0 until stride) {
          dut.io.en.poke(true.B)
        }
        dut.clock.step()
        dut.io.out.expect((sw(i)).S)
      }
    }
  }

  behavior of "SineWaveGen"
  it should "correctly calculate output for period=16 stride=1" in {
    testSineWaveGen(new SineWave(16, 128), 1)
  }

  it should "correctly calculate output for period=16 stride=2" in {
    testSineWaveGen(new SineWave(16, 128), 2)
  }

  it should "correctly calculate output for period=16 stride=3" in {
    testSineWaveGen(new SineWave(16, 128), 3)
  }

  it should "correctly calculate output for period=10 stride=1" in {
    testSineWaveGen(new SineWave(10, 128), 1)
  }

  it should "correctly calculate output for period=10 stride=3" in {
    testSineWaveGen(new SineWave(10, 128), 3)
  }
}
