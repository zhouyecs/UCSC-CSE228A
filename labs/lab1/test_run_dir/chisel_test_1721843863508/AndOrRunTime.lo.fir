FIRRTL version 1.2.0
circuit AndOrRunTime :
  module AndOrRunTime :
    input clock : Clock
    input reset : UInt<1>
    input io_a : UInt<1> @[cmd68.sc 2:16]
    input io_b : UInt<1> @[cmd68.sc 2:16]
    input io_useAnd : UInt<1> @[cmd68.sc 2:16]
    output io_out : UInt<1> @[cmd68.sc 2:16]

    node _io_out_T = and(io_a, io_b) @[cmd68.sc 10:34]
    node _io_out_T_1 = and(io_useAnd, _io_out_T) @[cmd68.sc 10:26]
    node _io_out_T_2 = eq(io_useAnd, UInt<1>("h0")) @[cmd68.sc 10:48]
    node _io_out_T_3 = or(io_a, io_b) @[cmd68.sc 10:67]
    node _io_out_T_4 = and(_io_out_T_2, _io_out_T_3) @[cmd68.sc 10:59]
    node _io_out_T_5 = or(_io_out_T_1, _io_out_T_4) @[cmd68.sc 10:44]
    io_out <= _io_out_T_5 @[cmd68.sc 10:12]
