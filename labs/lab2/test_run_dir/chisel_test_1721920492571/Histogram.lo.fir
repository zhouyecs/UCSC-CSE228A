FIRRTL version 1.2.0
circuit Histogram :
  module Histogram :
    input clock : Clock
    input reset : UInt<1>
    input io_x : UInt<2> @[cmd40.sc 2:16]
    output io_out : UInt<5> @[cmd40.sc 2:16]

    reg counts_0 : UInt<5>, clock with :
      reset => (UInt<1>("h0"), counts_0) @[cmd40.sc 9:25]
    reg counts_1 : UInt<5>, clock with :
      reset => (UInt<1>("h0"), counts_1) @[cmd40.sc 9:25]
    reg counts_2 : UInt<5>, clock with :
      reset => (UInt<1>("h0"), counts_2) @[cmd40.sc 9:25]
    node _GEN_0 = validif(eq(UInt<1>("h0"), io_x), counts_0) @[cmd40.sc 12:{34,34}]
    node _GEN_1 = mux(eq(UInt<1>("h1"), io_x), counts_1, _GEN_0) @[cmd40.sc 12:{34,34}]
    node _GEN_2 = mux(eq(UInt<2>("h2"), io_x), counts_2, _GEN_1) @[cmd40.sc 12:{34,34}]
    node _counts_io_x = _GEN_2 @[cmd40.sc 12:34]
    node _nextCount_T = add(_counts_io_x, UInt<1>("h1")) @[cmd40.sc 12:34]
    node nextCount = tail(_nextCount_T, 1) @[cmd40.sc 12:34]
    node _counts_io_x_0 = nextCount @[cmd40.sc 13:{18,18}]
    node _GEN_3 = mux(eq(UInt<1>("h0"), io_x), _counts_io_x_0, counts_0) @[cmd40.sc 13:{18,18} 9:25]
    node _GEN_4 = mux(eq(UInt<1>("h1"), io_x), _counts_io_x_0, counts_1) @[cmd40.sc 13:{18,18} 9:25]
    node _GEN_5 = mux(eq(UInt<2>("h2"), io_x), _counts_io_x_0, counts_2) @[cmd40.sc 13:{18,18} 9:25]
    node _counts_WIRE_0 = UInt<5>("h0") @[cmd40.sc 9:{33,33}]
    node _counts_WIRE_1 = UInt<5>("h0") @[cmd40.sc 9:{33,33}]
    node _counts_WIRE_2 = UInt<5>("h0") @[cmd40.sc 9:{33,33}]
    io_out <= nextCount @[cmd40.sc 16:12]
    counts_0 <= mux(reset, _counts_WIRE_0, _GEN_3) @[cmd40.sc 9:{25,25}]
    counts_1 <= mux(reset, _counts_WIRE_1, _GEN_4) @[cmd40.sc 9:{25,25}]
    counts_2 <= mux(reset, _counts_WIRE_2, _GEN_5) @[cmd40.sc 9:{25,25}]
