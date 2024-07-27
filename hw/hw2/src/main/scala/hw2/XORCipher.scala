package hw2

import chisel3._
import chisel3.util._
import javax.crypto.Cipher


object CipherState extends ChiselEnum {
  val empty, ready, encrypted, decrypted = Value
}

class XORCipherCmds extends Bundle {
  val clear      = Input(Bool())
  val loadKey    = Input(Bool())
  val loadAndEnc = Input(Bool())
  val decrypt    = Input(Bool())
}


/**
  * @param width :    Int
  * @field in:        UInt           (Input) - payload or key
  * @field cmds:      XORCipherCmds  (Input)
  * @field out:       UInt           (Output)
  * @field full:      Bool           (Output)
  * @field encrypted: Bool           (Output)
  * @field state:     CipherState    (Output) - visible for testing
  */
class XORCipherIO(width: Int) extends Bundle {
  val in        = Input(UInt(width.W))
  val cmds      = Input(new XORCipherCmds)
  val out       = Output(UInt(width.W))
  val full      = Output(Bool())
  val encrypted = Output(Bool())
  val state     = Output(CipherState())
}


/**
  * @param width Int
  */
class XORCipher(width: Int) extends Module {
  val io = IO(new XORCipherIO(width))

  // Default values

  val out       = RegInit(0.U(width.W))
  val full      = RegInit(false.B)
  val encrypted = RegInit(false.B)
  val state     = RegInit(CipherState.empty)

  val key       = RegInit(0.U(width.W))
  val data      = RegInit(0.U(width.W))

  when (state === CipherState.empty) {
    when(io.cmds.loadKey) {
      out := 0.U
      key := io.in
      data := 0.U
      full := false.B
      encrypted := false.B

      state := CipherState.ready
    }
  } .elsewhen (state === CipherState.ready) {
    when(io.cmds.clear) {
      out := 0.U
      key := io.in
      data := 0.U
      full := false.B
      encrypted := false.B

      state := CipherState.empty
    } .elsewhen (io.cmds.loadKey) {
      out := 0.U
      key := io.in
      data := 0.U
      full := false.B
      encrypted := false.B

      state := CipherState.ready
    } .elsewhen (io.cmds.loadAndEnc) {
      out := key ^ io.in
      data := io.in
      full := true.B
      encrypted := true.B

      state := CipherState.encrypted
    }
  } .elsewhen (state === CipherState.encrypted) {
    when(io.cmds.clear) {
      out := 0.U
      key := io.in
      data := 0.U
      full := false.B
      encrypted := false.B

      state := CipherState.empty
    } .elsewhen (io.cmds.loadAndEnc) {
      full := true.B
      encrypted := true.B

      state := CipherState.encrypted
    } .elsewhen (io.cmds.decrypt) {
      out := key ^ out
      encrypted := false.B

      state := CipherState.decrypted
    }
  } .elsewhen (state === CipherState.decrypted) {
    out := key ^ out
    full := true.B
    encrypted := false.B

    when(io.cmds.clear) {
      out := 0.U
      key := 0.U
      data := 0.U
      full := false.B
      encrypted := false.B

      state := CipherState.empty
    } .elsewhen (~io.cmds.loadAndEnc) {
      out := 0.U
      key := io.in
      data := 0.U
      full := false.B
      encrypted := false.B

      state := CipherState.ready
    } .elsewhen (io.cmds.loadAndEnc) {
      out := key ^ io.in
      data := io.in
      full := true.B
      encrypted := true.B

      state := CipherState.encrypted
    }
  }

  // switch(state) {
  //   is (CipherState.empty) {
  //     out := 0.U
  //     full := false.B
  //     encrypted := false.B
  //   }
  //   is (CipherState.ready) {
  //     out := 0.U
  //     full := false.B
  //     encrypted := false.B
  //   }
  //   is (CipherState.encrypted) {
  //     out := io.in ^ out
  //     full := true.B
  //     encrypted := true.B
  //   }
  //   is (CipherState.decrypted) {
  //     out := io.in ^ out
  //     full := true.B
  //     encrypted := false.B
  //   }
  // }

  io.out := out
  io.full := full
  io.encrypted := encrypted
  io.state := state
}
