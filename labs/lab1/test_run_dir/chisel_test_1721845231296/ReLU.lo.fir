FIRRTL version 1.2.0
circuit ReLU :
  module ReLU :
    input clock : Clock
    input reset : UInt<1>
    input io_x : SInt<5> @[cmd36.sc 2:16]
    output io_y : SInt<5> @[cmd36.sc 2:16]

    node _T = leq(io_x, asSInt(UInt<1>("h0"))) @[cmd36.sc 7:15]
    node _T_1 = lt(io_x, asSInt(UInt<3>("h2"))) @[cmd36.sc 9:21]
    node _GEN_0 = mux(_T_1, io_x, asSInt(UInt<3>("h2"))) @[cmd36.sc 10:14 12:14 9:36]
    node _GEN_1 = mux(_T, asSInt(UInt<1>("h0")), _GEN_0) @[cmd36.sc 7:22 8:14]
    io_y <= _GEN_1
