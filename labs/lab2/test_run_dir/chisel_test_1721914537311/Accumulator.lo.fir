FIRRTL version 1.2.0
circuit Accumulator :
  module Accumulator :
    input clock : Clock
    input reset : UInt<1>
    input io_in : UInt<4> @[cmd6.sc 2:16]
    input io_en : UInt<1> @[cmd6.sc 2:16]
    output io_out : UInt<4> @[cmd6.sc 2:16]

    node _io_out_T = add(io_out, io_in) @[cmd6.sc 10:32]
    node _io_out_T_1 = tail(_io_out_T, 1) @[cmd6.sc 10:32]
    reg io_out_r : UInt<4>, clock with :
      reset => (UInt<1>("h0"), io_out_r) @[cmd6.sc 10:24]
    node _GEN_0 = mux(io_en, _io_out_T_1, io_out_r) @[cmd6.sc 10:{24,24,24}]
    io_out <= io_out_r @[cmd6.sc 10:12]
    io_out_r <= mux(reset, UInt<1>("h0"), _GEN_0) @[cmd6.sc 10:{24,24}]
