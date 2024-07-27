package hw2

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class XORCipherTester extends AnyFlatSpec with ChiselScalatestTester {
  val key  = 0101
  val data = 1110
  val width = 16

  behavior of "XORCipher"
  it should "go through common case (empty -> ready -> encrypted -> decrypted -> empty" in {
    test(new XORCipher(width)).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      
      dut.io.state.expect(CipherState.empty)
      // empty -> ready
      dut.io.in.poke(key.U)
      dut.io.cmds.clear.poke(false.B)
      dut.io.cmds.loadKey.poke(true.B)
      dut.io.cmds.loadAndEnc.poke(false.B)
      dut.io.cmds.decrypt.poke(false.B)
      dut.clock.step()
      dut.io.state.expect(CipherState.ready)
      dut.io.out.expect(0.U)
      dut.io.full.expect(false.B)
      dut.io.encrypted.expect(false.B)
      
      // ready -> encrypted
      dut.io.in.poke(data.U)
      dut.io.cmds.clear.poke(false.B)
      dut.io.cmds.loadKey.poke(false.B)
      dut.io.cmds.loadAndEnc.poke(true.B)
      dut.io.cmds.decrypt.poke(false.B)
      dut.clock.step()
      dut.io.state.expect(CipherState.encrypted)
      dut.io.out.expect((key ^ data).U)
      dut.io.full.expect(true.B)
      dut.io.encrypted.expect(true.B)
      
      // encrypted -> decrypted
      dut.io.cmds.clear.poke(false.B)
      dut.io.cmds.loadKey.poke(false.B)
      dut.io.cmds.loadAndEnc.poke(false.B)
      dut.io.cmds.decrypt.poke(true.B)
      dut.clock.step()
      dut.io.state.expect(CipherState.decrypted)
      dut.io.out.expect(data.U)
      dut.io.full.expect(true.B)
      dut.io.encrypted.expect(false.B)

      // decrypted -> empty
      dut.io.cmds.clear.poke(true.B)
      dut.io.cmds.loadKey.poke(true.B)
      dut.io.cmds.loadAndEnc.poke(true.B)
      dut.io.cmds.decrypt.poke(true.B)
      dut.clock.step()
      dut.io.state.expect(CipherState.empty)
      dut.io.out.expect(0.U)
      dut.io.full.expect(false.B)
      dut.io.encrypted.expect(false.B)
    }
  }

  // You will want to create additional tests to cover the other FSM arcs
}
