package hw2

import chisel3._
import chisel3.util.log2Ceil


class SineWave(val period: Int, val amplitude: Int) {
  require(period > 0)
  val B: Double = (2.0 * math.Pi) / period.toDouble

  def apply(index: Int): Int = (amplitude.toDouble * math.sin(B * index)).toInt
}


/**
  *
  * @param s : SineWave (internally contains period & amplitude)
  * ________________________________
  * @field stride:  UInt      (Input)
  * @field en:      Bool      (Input)
  * @field out:     SInt      (Output)
  */
class SineWaveGenIO (sw: SineWave) extends Bundle { 
  val stride = Input(UInt(log2Ceil(sw.period).W))
  val en     = Input(Bool())
  val out    = Output(SInt(log2Ceil(sw.amplitude * 2 + 1).W))
}


/**
  * 
  * @param s : SineWave (internally contains period)
  */
class SineWaveGen(sw: SineWave) extends Module {
  val io = IO(new SineWaveGenIO(sw))

  val ROM = VecInit((0 until sw.period).map(sw(_).S))

  val index = RegInit(0.U(log2Ceil(sw.period).W))
  val output = RegInit(0.S(log2Ceil(sw.amplitude * 2 + 1).W))

  when(io.en) {
    index := (index + 1.U) % sw.period.U
    output := ROM(index)
  }

  io.out := output
}