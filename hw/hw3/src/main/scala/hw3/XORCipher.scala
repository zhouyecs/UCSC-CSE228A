package hw3

import chisel3._
import chisel3.util._
import javax.crypto.Cipher


class XORCipherCmds extends Bundle {
    val clear = Input(Bool())
    val load  = Input(Bool())
    val read  = Input(Bool())
}


object CipherState extends ChiselEnum {
    val clearing, empty, loading, encrypted, reading = Value
}


class XORCipherIO(width: Int) extends Bundle {
    val in    = Input(UInt(width.W))
    val key   = Flipped(Valid(UInt(width.W)))
    val cmds  = Input(new XORCipherCmds)
    val out   = Valid(UInt(width.W))
    val state = Output(CipherState())
}


class RWSmem(width: Int, numWords: Int) extends Module {
  val io = IO(new Bundle {
    val enable  = Input(Bool())
    val write   = Input(Bool())
    val addr    = Input(UInt(log2Ceil(numWords).W))
    val dataIn  = Input(UInt(width.W))
    val dataOut = Output(UInt(width.W))
  })

  val mem = SyncReadMem(numWords, UInt(width.W))
  io.dataOut := 0.U
  when(io.enable) {
    val rdwrPort = mem(io.addr)
    when (io.write) { rdwrPort := io.dataIn; io.dataOut := 0.U }
      .otherwise    { io.dataOut := rdwrPort }
  }

//   val mem = SyncReadMem(numWords, UInt(width.W))
//   // Create one write port and one read port
//   mem.write(io.addr, io.dataIn)
//   io.dataOut := mem.read(io.addr, io.enable)
}


class XORCipher(width: Int, numWords: Int) extends Module {
    val io = IO(new XORCipherIO(width))

    // YOUR CODE HERE
    val mem   = Module(new RWSmem(width, numWords))
    val state = RegInit(CipherState.clearing)
    val cnt   = RegInit(0.U(log2Ceil(numWords).W))

    // Default values
    mem.io.enable := false.B
    mem.io.write  := false.B
    mem.io.addr   := DontCare
    mem.io.dataIn := DontCare

    io.out.valid := false.B
    io.out.bits  := DontCare

    switch (state) {
        is(CipherState.clearing) {
            when(cnt === (numWords-1).U) {
                cnt := 0.U
                state := CipherState.empty
            } .otherwise {
                cnt := cnt + 1.U
            }
        }
        is(CipherState.empty) {
            when(io.cmds.load && io.key.valid) {
                mem.io.enable := true.B
                mem.io.write  := true.B
                mem.io.addr   := cnt
                mem.io.dataIn := io.in ^ io.key.bits

                when(cnt === (numWords-1).U) {
                    state := CipherState.loading
                } .otherwise {
                    cnt := cnt + 1.U
                }
                state := CipherState.loading
            }
        }
        is(CipherState.loading) {
            when(io.cmds.load && io.key.valid) {
                mem.io.enable := true.B
                mem.io.write  := true.B
                mem.io.addr   := cnt
                mem.io.dataIn := io.in ^ io.key.bits

                when(cnt === (numWords-1).U) {
                    state := CipherState.encrypted
                } .otherwise {
                    cnt := cnt + 1.U
                }
            }
        }
        is(CipherState.encrypted) {
            when(io.cmds.read) {
                mem.io.enable := true.B
                mem.io.write  := false.B
                mem.io.addr   := cnt
                io.out.bits   := mem.io.dataOut
                io.out.valid  := true.B

                cnt := cnt - 1.U
                state := CipherState.reading
            } .elsewhen(io.cmds.clear) {
                for (i <- 0 until numWords) {
                    mem.io.enable := true.B
                    mem.io.write  := true.B
                    mem.io.addr   := i.U
                    mem.io.dataIn := 0.U
                }
                state := CipherState.clearing
            } .elsewhen(io.cmds.load && io.key.valid) {
                mem.io.enable := true.B
                mem.io.write  := true.B
                mem.io.addr   := cnt
                mem.io.dataIn := io.in ^ io.key.bits

                when(cnt === (numWords-1).U) {
                    state := CipherState.loading
                } .otherwise {
                    cnt := cnt + 1.U
                }
            }
        }
        is(CipherState.reading) {
            when(io.cmds.read) {
                mem.io.enable := true.B
                mem.io.write  := false.B
                mem.io.addr   := cnt
                io.out.bits   := mem.io.dataOut
                io.out.valid  := true.B

                when(cnt === 0.U) {
                    state := CipherState.encrypted
                    cnt := (numWords-1).U
                } .otherwise {
                    cnt := cnt - 1.U
                }
            }
        }
    }
    io.state := state
}
