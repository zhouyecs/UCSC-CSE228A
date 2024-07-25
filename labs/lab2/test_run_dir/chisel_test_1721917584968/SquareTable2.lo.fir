FIRRTL version 1.2.0
circuit SquareTable2 :
  module SquareTable2 :
    input clock : Clock
    input reset : UInt<1>
    input io_x : UInt<4> @[cmd25.sc 2:16]
    output io_xSquared : UInt<8> @[cmd25.sc 2:16]

    node ROM_0 = UInt<8>("h0") @[cmd25.sc 10:{33,33}]
    node _GEN_0 = validif(eq(UInt<1>("h0"), io_x), ROM_0) @[cmd25.sc 11:{17,17}]
    node ROM_1 = UInt<8>("h1") @[cmd25.sc 10:{33,33}]
    node _GEN_1 = mux(eq(UInt<1>("h1"), io_x), ROM_1, _GEN_0) @[cmd25.sc 11:{17,17}]
    node ROM_2 = UInt<8>("h4") @[cmd25.sc 10:{33,33}]
    node _GEN_2 = mux(eq(UInt<2>("h2"), io_x), ROM_2, _GEN_1) @[cmd25.sc 11:{17,17}]
    node ROM_3 = UInt<8>("h9") @[cmd25.sc 10:{33,33}]
    node _GEN_3 = mux(eq(UInt<2>("h3"), io_x), ROM_3, _GEN_2) @[cmd25.sc 11:{17,17}]
    node ROM_4 = UInt<8>("h10") @[cmd25.sc 10:{33,33}]
    node _GEN_4 = mux(eq(UInt<3>("h4"), io_x), ROM_4, _GEN_3) @[cmd25.sc 11:{17,17}]
    node ROM_5 = UInt<8>("h19") @[cmd25.sc 10:{33,33}]
    node _GEN_5 = mux(eq(UInt<3>("h5"), io_x), ROM_5, _GEN_4) @[cmd25.sc 11:{17,17}]
    node ROM_6 = UInt<8>("h24") @[cmd25.sc 10:{33,33}]
    node _GEN_6 = mux(eq(UInt<3>("h6"), io_x), ROM_6, _GEN_5) @[cmd25.sc 11:{17,17}]
    node ROM_7 = UInt<8>("h31") @[cmd25.sc 10:{33,33}]
    node _GEN_7 = mux(eq(UInt<3>("h7"), io_x), ROM_7, _GEN_6) @[cmd25.sc 11:{17,17}]
    node ROM_8 = UInt<8>("h40") @[cmd25.sc 10:{33,33}]
    node _GEN_8 = mux(eq(UInt<4>("h8"), io_x), ROM_8, _GEN_7) @[cmd25.sc 11:{17,17}]
    node ROM_9 = UInt<8>("h51") @[cmd25.sc 10:{33,33}]
    node _GEN_9 = mux(eq(UInt<4>("h9"), io_x), ROM_9, _GEN_8) @[cmd25.sc 11:{17,17}]
    node ROM_10 = UInt<8>("h64") @[cmd25.sc 10:{33,33}]
    node _GEN_10 = mux(eq(UInt<4>("ha"), io_x), ROM_10, _GEN_9) @[cmd25.sc 11:{17,17}]
    node _ROM_io_x = _GEN_10 @[cmd25.sc 11:17]
    io_xSquared <= _ROM_io_x @[cmd25.sc 11:17]
