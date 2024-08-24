package hw5

import chisel3._
import chisel3.util._

case class CacheParams(capacity: Int, blockSize: Int, associativity: Int, addrLen: Int = 8, bitsPerWord: Int = 8) {
	require((1 << addrLen) >= capacity)
	require(capacity > blockSize)
	require(isPow2(capacity) && isPow2(blockSize) && isPow2(associativity) && isPow2(bitsPerWord))
	// inputs capacity & blockSize are in units of words

	val numExtMemBlocks = (1 << addrLen) / blockSize
	val memBlockAddrBits = log2Ceil(numExtMemBlocks)

	val numSets = capacity / blockSize / associativity
	val numOffsetBits = log2Ceil(blockSize)
	val numIndexBits = log2Ceil(numSets)
	val numTagBits = addrLen - (numOffsetBits + numIndexBits)
}


class MockDRAM(p: CacheParams) extends Module {
	def CacheBlock(): Vec[UInt] = Vec(p.blockSize, UInt(p.bitsPerWord.W))

	// addresses in terms of blocks
	val io = IO(new Bundle {
		val rAddr = Input(UInt(p.memBlockAddrBits.W))
		val rEn = Input(Bool())
		val rData = Output(CacheBlock())
		val wAddr = Input(UInt(p.memBlockAddrBits.W))
		val wEn = Input(Bool())
		val wData = Input(CacheBlock())
	})
	// Fixed memory latency of 1 cycle
	val dram = SyncReadMem(p.numExtMemBlocks, CacheBlock())
	io.rData := DontCare
	when (io.rEn) {
		io.rData := dram(io.rAddr)
	}
	when (io.wEn) {
		dram(io.wAddr) := io.wData
	}
}


class Cache(val p: CacheParams) extends Module {
	val io = IO(new Bundle {
		val in = Flipped(Decoupled(new Bundle {
			val addr = UInt(p.addrLen.W)
			val write = Bool()
			val wData = UInt(p.bitsPerWord.W)
		}))
		val hit = Output(Bool())                  // helpful for testing
		val out = Valid(UInt(p.bitsPerWord.W))		// sets valid to true to indicate completion (even for writes)
	})

	// extract fields from address
	val tag    = io.in.bits.addr(p.addrLen - 1, p.numOffsetBits + p.numIndexBits)
	val index  = io.in.bits.addr(p.numOffsetBits + p.numIndexBits - 1, p.numOffsetBits)
	val offset = io.in.bits.addr(p.numOffsetBits - 1, 0)

	// essentially making a type alias to make it easy to declare
	def CacheBlock(): Vec[UInt] = Vec(p.blockSize, UInt(p.bitsPerWord.W))

	// backing memory
	val extMem = Module(new MockDRAM(p))
}


class DMCache(p: CacheParams) extends Cache(p) {
	require(p.associativity == 1)
  // BEGIN SOLUTION
  val tags   = SyncReadMem(p.numSets, UInt(p.numTagBits.W))
	val valids = RegInit(VecInit(Seq.fill(p.numSets)(false.B)))
	val data   = SyncReadMem(p.numSets, CacheBlock())

	val state  = RegInit(0.U(2.W)) // 0: ready, 1: lookup, 2: fetch

	io.in.ready  := true.B
	io.hit       := false.B
	io.out.valid := false.B
	io.out.bits  := 0.U

	extMem.io.rAddr := 0.U
	extMem.io.rEn   := false.B
	extMem.io.wAddr := 0.U
	extMem.io.wEn   := false.B
	extMem.io.wData := VecInit(Seq.fill(p.blockSize)(0.U))

	val memReadWire  = Wire(CacheBlock())
	val dataReadWire = Wire(CacheBlock())
	val tagReadWire  = Wire(UInt(p.numTagBits.W))

	memReadWire  := extMem.io.rData
	dataReadWire := data.read(index, io.in.fire)
	tagReadWire  := tags.read(index, io.in.fire)

	when (state === 0.U) {
		io.in.ready   := true.B
		io.hit        := false.B
		io.out.valid  := false.B
		extMem.io.wEn := false.B
		extMem.io.rEn := false.B

		when (io.in.fire) {
			state := 1.U
		}
	} .elsewhen (state === 1.U) {
		io.in.ready   := false.B

		when (valids(index) && tag === tagReadWire) {
			io.hit := true.B
			
			when (io.in.bits.write) {
				dataReadWire(offset) := io.in.bits.wData
				data.write(index, dataReadWire)
			} .otherwise {
				io.out.bits := dataReadWire(offset)
			}

			io.out.valid := true.B
			state := 0.U
		} .otherwise {
			io.hit          := false.B
			extMem.io.rAddr := io.in.bits.addr / p.blockSize.U
			extMem.io.rEn   := true.B
			state           := 2.U
		}
	} .elsewhen (state === 2.U) {
		io.hit        := false.B
		io.in.ready   := false.B
		extMem.io.rEn := false.B

		when (valids(index)) {
			extMem.io.wAddr := (tagReadWire << p.numIndexBits.U) | index
			extMem.io.wEn   := true.B
			extMem.io.wData := dataReadWire
		}

		when (io.in.bits.write) {
			memReadWire(offset) := io.in.bits.wData
		} .otherwise {
			io.out.bits := memReadWire(offset)
		}

		data.write(index, memReadWire)
		tags.write(index, tag)
		valids(index) := true.B
		io.out.valid := true.B
		state := 0.U
	}
}
